<template>
  <div class="max-w-4xl mx-auto py-6 px-4">
    
    <!-- Empty State -->
    <div v-if="words.length < 6" class="text-center py-20 bg-white rounded-2xl border border-slate-200">
      <h3 class="text-lg font-bold text-slate-700">Cần ít nhất 6 từ để chơi trò chơi ghép thẻ</h3>
      <p class="text-sm text-slate-500 mt-1">Vui lòng thêm thêm từ vựng hoặc chọn bộ từ khác.</p>
    </div>

    <!-- Game Screen -->
    <div v-else>
      
      <!-- Top Bar: Timer, Best Score, Restart -->
      <div class="flex items-center justify-between gap-4 mb-6 bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <div class="flex items-center gap-3">
          <div class="p-2.5 bg-indigo-50 text-indigo-600 rounded-xl">
            <Clock class="w-5 h-5" />
          </div>
          <div>
            <span class="text-xs font-semibold text-slate-400 block uppercase tracking-wider">Thời gian</span>
            <span class="text-2xl font-black font-mono text-slate-800">{{ formattedTime }}s</span>
          </div>
        </div>

        <div v-if="bestTime" class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-amber-50 border border-amber-200 rounded-xl text-amber-700 text-xs font-bold">
          <Trophy class="w-4 h-4 text-amber-500" />
          <span>Kỷ lục: {{ bestTime }}s</span>
        </div>

        <button
          @click="startNewGame"
          class="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
        >
          <RotateCcw class="w-4 h-4" />
          <span>Ván mới</span>
        </button>
      </div>

      <!-- Victory Overlay Modal -->
      <div v-if="gameCompleted" class="bg-white rounded-3xl p-8 sm:p-12 text-center border border-slate-200 shadow-lg max-w-lg mx-auto my-12 animate-fade-in">
        <div class="w-20 h-20 bg-amber-100 text-amber-500 rounded-full flex items-center justify-center mx-auto mb-4">
          <Trophy class="w-10 h-10" />
        </div>
        <h2 class="text-3xl font-black text-slate-800 mb-2">Thần tốc! Tuyệt vời! ⚡</h2>
        <p class="text-slate-500 mb-6">Bạn đã ghép chính xác toàn bộ các thẻ từ vựng.</p>

        <div class="p-4 bg-indigo-50 rounded-2xl border border-indigo-100 mb-6">
          <span class="text-xs font-bold text-indigo-600 uppercase tracking-wider">Thời gian hoàn thành</span>
          <p class="text-4xl font-black text-indigo-700 mt-1 font-mono">{{ finalTime }}s</p>
          <p v-if="isNewRecord" class="text-xs font-bold text-emerald-600 mt-2 flex items-center justify-center gap-1">
            <Sparkles class="w-3.5 h-3.5" /> Kỷ lục mới của bạn!
          </p>
        </div>

        <button
          @click="startNewGame"
          class="w-full py-3.5 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white rounded-xl font-bold shadow-md shadow-indigo-200 transition"
        >
          Chơi ván tiếp theo
        </button>
      </div>

      <!-- Tiles Grid -->
      <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3.5">
        <div
          v-for="tile in tiles"
          :key="tile.id"
          @click="selectTile(tile)"
          :class="[
            'min-h-[110px] p-4 rounded-2xl flex items-center justify-center text-center font-bold text-sm sm:text-base select-none cursor-pointer transition-all duration-200 shadow-xs border-2',
            tile.matched
              ? 'opacity-0 pointer-events-none scale-90'
              : tile.status === 'wrong'
                ? 'bg-rose-50 border-rose-500 text-rose-800 animate-shake shadow-md'
                : tile.status === 'selected'
                  ? 'bg-indigo-50 border-indigo-500 text-indigo-900 shadow-md scale-102 ring-2 ring-indigo-300'
                  : 'bg-white hover:bg-slate-50 border-slate-200 hover:border-indigo-300 text-slate-800 hover:shadow-sm'
          ]"
        >
          <div>
            <span class="line-clamp-3 leading-snug">{{ tile.text }}</span>
            <span v-if="tile.type === 'word' && tile.ipa" class="block text-xs font-mono font-normal text-indigo-500 mt-1">
              {{ tile.ipa }}
            </span>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, onUnmounted, watch } from 'vue';
import { Clock, Trophy, RotateCcw, Sparkles } from 'lucide-vue-next';
import confetti from 'canvas-confetti';
import { speakText } from '@/utils/speech';

function formatMeaning(meaning, partOfSpeech) {
  if (!meaning) return '';
  let text = meaning.trim();
  if (partOfSpeech) {
    const cleanPos = partOfSpeech.trim();
    if (text.toLowerCase().startsWith(cleanPos.toLowerCase())) {
      text = text.substring(cleanPos.length).trim();
    }
  }
  return text.replace(/^\s*\([a-zA-Z /,-]+\)\s*/, '').trim();
}

const props = defineProps({
  words: {
    type: Array,
    default: () => []
  }
});

const tiles = ref([]);
const firstSelected = ref(null);
const secondSelected = ref(null);
const isChecking = ref(false);

const timerInterval = ref(null);
const startTime = ref(null);
const elapsedTimeMs = ref(0);
const gameCompleted = ref(false);
const finalTime = ref('0.0');
const isNewRecord = ref(false);

const bestTime = ref(localStorage.getItem('vocab_best_match_time') || null);

const formattedTime = computed(() => {
  return (elapsedTimeMs.value / 1000).toFixed(1);
});

function startNewGame() {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
    timerInterval.value = null;
  }

  gameCompleted.value = false;
  firstSelected.value = null;
  secondSelected.value = null;
  isChecking.value = false;
  isNewRecord.value = false;

  const sample = [...props.words]
    .sort(() => Math.random() - 0.5)
    .slice(0, 6);

  const tileList = [];
  sample.forEach((item, index) => {
    tileList.push({
      id: `w-${item.id}-${index}`,
      wordId: item.id,
      type: 'word',
      text: item.word,
      ipa: item.ipa,
      matched: false,
      status: 'idle'
    });

    tileList.push({
      id: `m-${item.id}-${index}`,
      wordId: item.id,
      type: 'meaning',
      text: formatMeaning(item.meaning, item.partOfSpeech),
      matched: false,
      status: 'idle'
    });
  });

  tileList.sort(() => Math.random() - 0.5);
  tiles.value = tileList;

  startTime.value = Date.now();
  elapsedTimeMs.value = 0;
  timerInterval.value = setInterval(() => {
    elapsedTimeMs.value = Date.now() - startTime.value;
  }, 100);
}

function selectTile(tile) {
  if (tile.matched || isChecking.value) return;

  if (firstSelected.value === tile) {
    tile.status = 'idle';
    firstSelected.value = null;
    return;
  }

  if (!firstSelected.value) {
    firstSelected.value = tile;
    tile.status = 'selected';
    if (tile.type === 'word') {
      speakText(tile.text);
    }
  } else {
    secondSelected.value = tile;
    tile.status = 'selected';
    if (tile.type === 'word') {
      speakText(tile.text);
    }
    checkMatch();
  }
}

function checkMatch() {
  isChecking.value = true;
  const t1 = firstSelected.value;
  const t2 = secondSelected.value;

  const isMatch = t1.wordId === t2.wordId && t1.type !== t2.type;

  if (isMatch) {
    setTimeout(() => {
      t1.matched = true;
      t2.matched = true;
      firstSelected.value = null;
      secondSelected.value = null;
      isChecking.value = false;

      if (tiles.value.every(t => t.matched)) {
        finishGame();
      }
    }, 250);
  } else {
    t1.status = 'wrong';
    t2.status = 'wrong';

    setTimeout(() => {
      t1.status = 'idle';
      t2.status = 'idle';
      firstSelected.value = null;
      secondSelected.value = null;
      isChecking.value = false;
    }, 700);
  }
}

function finishGame() {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
    timerInterval.value = null;
  }

  finalTime.value = (elapsedTimeMs.value / 1000).toFixed(1);
  gameCompleted.value = true;

  const currentBest = bestTime.value ? parseFloat(bestTime.value) : Infinity;
  const finalSec = parseFloat(finalTime.value);
  if (finalSec < currentBest) {
    bestTime.value = finalTime.value;
    localStorage.setItem('vocab_best_match_time', finalTime.value);
    isNewRecord.value = true;
  }

  confetti({
    particleCount: 120,
    spread: 80,
    origin: { y: 0.6 }
  });
}

watch(() => props.words, () => {
  if (props.words.length >= 6) {
    startNewGame();
  }
}, { immediate: true });

onUnmounted(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
  }
});
</script>

<style scoped>
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-5px); }
  40%, 80% { transform: translateX(5px); }
}

.animate-shake {
  animation: shake 0.4s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

.scale-102 {
  transform: scale(1.02);
}
</style>
