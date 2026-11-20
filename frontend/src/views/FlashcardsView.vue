<template>
  <div class="max-w-4xl mx-auto py-6 px-4">
    
    <!-- Top toolbar: Filters & Settings -->
    <div class="flex flex-wrap items-center justify-between gap-3 mb-6">
      <div class="flex items-center gap-2">
        <span class="text-sm font-semibold text-slate-700">Tiến độ:</span>
        <span class="text-sm font-bold text-indigo-600">{{ currentIndex + 1 }} / {{ activeList.length }}</span>
        <span v-if="starredOnly" class="ml-2 px-2 py-0.5 text-xs font-medium bg-amber-100 text-amber-700 rounded-full flex items-center gap-1">
          <Star class="w-3 h-3 fill-amber-500 text-amber-500" /> Chỉ từ gắn sao
        </span>
      </div>

      <div class="flex items-center gap-2 flex-wrap">
        <button
          @click="toggleStarredFilter"
          :class="[
            'px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition border',
            starredOnly
              ? 'bg-amber-50 border-amber-300 text-amber-700 shadow-xs'
              : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
          ]"
          title="Chỉ học các từ đã đánh dấu sao"
        >
          <Star :class="['w-3.5 h-3.5', starredOnly ? 'fill-amber-500 text-amber-500' : 'text-slate-400']" />
          <span>{{ starredOnly ? 'Học tất cả từ' : 'Chỉ từ gắn sao (' + starredCount + ')' }}</span>
        </button>

        <button
          @click="toggleShuffle"
          :class="[
            'px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition border',
            isShuffled
              ? 'bg-indigo-50 border-indigo-300 text-indigo-700'
              : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
          ]"
          title="Trộn ngẫu nhiên thứ tự thẻ"
        >
          <Shuffle class="w-3.5 h-3.5" />
          <span>{{ isShuffled ? 'Đã trộn thẻ' : 'Trộn thẻ' }}</span>
        </button>

        <button
          @click="toggleFrontLanguage"
          class="px-3 py-1.5 rounded-lg text-xs font-semibold bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 transition flex items-center gap-1"
          title="Đổi mặt thẻ bắt đầu"
        >
          <RotateCcw class="w-3.5 h-3.5" />
          <span>Mặt trước: {{ frontLanguage === 'en' ? 'Tiếng Anh' : 'Tiếng Việt' }}</span>
        </button>

        <button
          @click="toggleAutoPlay"
          :class="[
            'px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition border',
            isAutoPlaying
              ? 'bg-rose-50 border-rose-300 text-rose-700 animate-pulse'
              : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
          ]"
          title="Tự động lật và chuyển thẻ kèm phát âm"
        >
          <component :is="isAutoPlaying ? Pause : Play" class="w-3.5 h-3.5" />
          <span>{{ isAutoPlaying ? 'Dừng phát' : 'Tự động chạy' }}</span>
        </button>
      </div>
    </div>

    <!-- Empty state -->
    <div v-if="activeList.length === 0" class="text-center py-20 bg-white rounded-2xl border border-slate-200 shadow-xs">
      <Star class="w-12 h-12 text-slate-300 mx-auto mb-3" />
      <h3 class="text-lg font-bold text-slate-700">Chưa có từ vựng nào để hiển thị</h3>
      <p class="text-sm text-slate-500 mt-1">
        {{ starredOnly ? 'Bạn chưa gắn sao từ nào trong bộ này.' : 'Bộ từ này hiện đang trống. Hãy thêm từ mới hoặc nhập hàng loạt.' }}
      </p>
      <div v-if="!starredOnly" class="mt-4 flex items-center justify-center gap-3">
        <button
          @click="$emit('open-add-word-modal')"
          class="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
        >
          <Plus class="w-3.5 h-3.5" />
          <span>Thêm từ</span>
        </button>
        <button
          @click="$emit('open-import-modal')"
          class="px-4 py-2 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white rounded-xl text-xs font-bold transition flex items-center gap-1.5 shadow-xs shadow-indigo-100"
        >
          <Layers class="w-3.5 h-3.5" />
          <span>Thêm nhiều từ</span>
        </button>
      </div>
    </div>

    <!-- Active Card Display -->
    <div v-else>
      <!-- Progress Bar -->
      <div class="w-full bg-slate-200 rounded-full h-2 mb-4 overflow-hidden">
        <div
          class="bg-gradient-to-r from-indigo-500 to-violet-600 h-2 rounded-full transition-all duration-300"
          :style="{ width: `${((currentIndex + 1) / activeList.length) * 100}%` }"
        ></div>
      </div>

      <!-- 3D Flashcard Container -->
      <div class="perspective-1000 w-full select-none cursor-pointer mb-6" @click="flipCard">
        <div
          :class="[
            'relative w-full h-80 sm:h-96 rounded-3xl transition-transform duration-500 transform-style-preserve-3d shadow-lg border border-slate-100',
            isFlipped ? 'rotate-y-180' : ''
          ]"
        >
          
          <!-- Front Face -->
          <div
            class="absolute inset-0 backface-hidden rounded-3xl bg-white p-6 sm:p-10 flex flex-col justify-between border-2 border-transparent hover:border-indigo-100 transition-all shadow-sm"
          >
            <!-- Card Top: Star & Audio & Tag -->
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span v-if="currentWord.partOfSpeech" class="px-2.5 py-1 text-xs font-bold rounded-lg bg-indigo-50 text-indigo-700">
                  {{ currentWord.partOfSpeech }}
                </span>
                <span v-if="currentWord.masteryLevel === 2" class="px-2 py-0.5 text-xs font-semibold rounded bg-emerald-50 text-emerald-600">
                  Đã thuộc
                </span>
              </div>

              <div class="flex items-center gap-2" @click.stop>
                <button
                  @click="playAudio(frontLanguage === 'en' ? currentWord.word : '')"
                  class="p-2.5 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-full transition"
                  title="Phát âm tiếng Anh"
                >
                  <Volume2 class="w-5 h-5" />
                </button>
                <button
                  @click="onToggleStar"
                  class="p-2.5 rounded-full hover:bg-amber-50 transition"
                  :title="currentWord.isStarred ? 'Bỏ gắn sao' : 'Gắn sao từ này'"
                >
                  <Star :class="['w-5 h-5', currentWord.isStarred ? 'fill-amber-400 text-amber-400' : 'text-slate-400 hover:text-amber-400']" />
                </button>
              </div>
            </div>

            <!-- Card Center Content (Front) -->
            <div class="text-center my-auto px-4">
              <template v-if="frontLanguage === 'en'">
                <h2 class="text-3xl sm:text-4xl md:text-5xl font-extrabold text-slate-800 tracking-tight leading-tight">
                  {{ currentWord.word }}
                </h2>
                <p v-if="currentWord.ipa" class="text-lg sm:text-xl text-indigo-600 font-mono mt-3 font-medium">
                  {{ currentWord.ipa }}
                </p>
              </template>
              <template v-else>
                <h2 class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-800 leading-snug">
                  {{ formatMeaning(currentWord.meaning, currentWord.partOfSpeech) }}
                </h2>
              </template>
            </div>

            <!-- Card Bottom: Hint -->
            <div class="flex items-center justify-between text-xs text-slate-400">
              <span class="flex items-center gap-1">
                <kbd class="px-1.5 py-0.5 bg-slate-100 border border-slate-200 rounded font-mono text-[10px]">Space</kbd> hoặc nhấp chuột để lật
              </span>
              <span class="text-slate-400 font-medium">Mặt trước</span>
            </div>
          </div>

          <!-- Back Face -->
          <div
            class="absolute inset-0 backface-hidden rotate-y-180 rounded-3xl bg-gradient-to-br from-slate-900 to-indigo-950 text-white p-6 sm:p-10 flex flex-col justify-between shadow-xl"
          >
            <!-- Card Top: Star & Audio -->
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span v-if="currentWord.partOfSpeech" class="px-2.5 py-1 text-xs font-bold rounded-lg bg-indigo-500/30 text-indigo-300">
                  {{ currentWord.partOfSpeech }}
                </span>
                <span v-if="frontLanguage === 'vi' && currentWord.ipa" class="text-sm font-mono text-indigo-300">
                  {{ currentWord.ipa }}
                </span>
              </div>

              <div class="flex items-center gap-2" @click.stop>
                <button
                  @click="playAudio(currentWord.word)"
                  class="p-2.5 text-indigo-300 hover:text-white hover:bg-white/10 rounded-full transition"
                  title="Phát âm tiếng Anh"
                >
                  <Volume2 class="w-5 h-5" />
                </button>
                <button
                  @click="onToggleStar"
                  class="p-2.5 rounded-full hover:bg-white/10 transition"
                >
                  <Star :class="['w-5 h-5', currentWord.isStarred ? 'fill-amber-400 text-amber-400' : 'text-slate-400 hover:text-amber-400']" />
                </button>
              </div>
            </div>

            <!-- Card Center Content (Back) -->
            <div class="text-center my-auto px-4">
              <template v-if="frontLanguage === 'en'">
                <h3 class="text-2xl sm:text-3xl md:text-4xl font-bold text-white leading-snug">
                  {{ formatMeaning(currentWord.meaning, currentWord.partOfSpeech) }}
                </h3>
                <div v-if="currentWord.exampleSentence" class="mt-4 p-3 bg-white/5 rounded-xl border border-white/10 max-w-lg mx-auto">
                  <p class="text-sm text-indigo-200 italic">"{{ currentWord.exampleSentence }}"</p>
                  <p v-if="currentWord.exampleTranslation" class="text-xs text-slate-400 mt-1">{{ currentWord.exampleTranslation }}</p>
                </div>
              </template>
              <template v-else>
                <h2 class="text-3xl sm:text-4xl md:text-5xl font-extrabold text-white tracking-tight">
                  {{ currentWord.word }}
                </h2>
                <p v-if="currentWord.ipa" class="text-lg sm:text-xl text-indigo-300 font-mono mt-3">
                  {{ currentWord.ipa }}
                </p>
              </template>
            </div>

            <!-- Card Bottom: Hint -->
            <div class="flex items-center justify-between text-xs text-slate-400">
              <span class="flex items-center gap-1">
                <kbd class="px-1.5 py-0.5 bg-white/10 border border-white/20 rounded font-mono text-[10px]">Space</kbd> để lật lại
              </span>
              <span class="text-indigo-400 font-medium">Mặt sau</span>
            </div>
          </div>

        </div>
      </div>

      <!-- Quick Learning Feedback Buttons -->
      <div class="grid grid-cols-2 gap-4 max-w-md mx-auto mb-6">
        <button
          @click="markWordStatus(1)"
          class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 font-semibold text-sm transition border border-rose-200 shadow-xs"
        >
          <X class="w-4 h-4" />
          <span>Cần học lại (1)</span>
        </button>

        <button
          @click="markWordStatus(2)"
          class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl bg-emerald-50 hover:bg-emerald-100 text-emerald-700 font-semibold text-sm transition border border-emerald-200 shadow-xs"
        >
          <Check class="w-4 h-4" />
          <span>Đã thuộc từ này (2)</span>
        </button>
      </div>

      <!-- Card Navigation Arrows -->
      <div class="flex items-center justify-center gap-6">
        <button
          @click="prevCard"
          class="p-3 rounded-full bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 hover:border-indigo-300 transition shadow-xs"
          title="Thẻ trước (Mũi tên trái)"
        >
          <ChevronLeft class="w-6 h-6" />
        </button>

        <span class="text-sm font-semibold text-slate-600 font-mono">
          {{ currentIndex + 1 }} / {{ activeList.length }}
        </span>

        <button
          @click="nextCard"
          class="p-3 rounded-full bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 hover:border-indigo-300 transition shadow-xs"
          title="Thẻ tiếp theo (Mũi tên phải)"
        >
          <ChevronRight class="w-6 h-6" />
        </button>
      </div>

      <!-- Keyboard shortcuts helper banner -->
      <div class="mt-8 p-3 bg-slate-100/70 rounded-xl text-center text-xs text-slate-500 max-w-xl mx-auto flex items-center justify-center gap-4 flex-wrap">
        <span><kbd class="px-1.5 py-0.5 bg-white rounded border border-slate-200">Space</kbd> Lật thẻ</span>
        <span><kbd class="px-1.5 py-0.5 bg-white rounded border border-slate-200">←</kbd> <kbd class="px-1.5 py-0.5 bg-white rounded border border-slate-200">→</kbd> Di chuyển</span>
        <span><kbd class="px-1.5 py-0.5 bg-white rounded border border-slate-200">A</kbd> Phát âm</span>
        <span><kbd class="px-1.5 py-0.5 bg-white rounded border border-slate-200">S</kbd> Gắn sao</span>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { 
  Volume2, 
  Star, 
  Shuffle, 
  RotateCcw, 
  Play, 
  Pause, 
  ChevronLeft, 
  ChevronRight, 
  Check, 
  X,
  Plus,
  Layers 
} from 'lucide-vue-next';
import { speakText } from '@/utils/speech';

const props = defineProps({
  words: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['toggle-star', 'update-mastery', 'open-add-word-modal', 'open-import-modal']);

const currentIndex = ref(0);
const isFlipped = ref(false);
const isShuffled = ref(false);
const starredOnly = ref(false);
const frontLanguage = ref('en');
const isAutoPlaying = ref(false);
let autoPlayTimer = null;

const displayList = ref([]);

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

function syncDisplayList() {
  let list = [...props.words];
  if (isShuffled.value) {
    list.sort(() => Math.random() - 0.5);
  }
  displayList.value = list;
  if (currentIndex.value >= list.length) {
    currentIndex.value = 0;
  }
}

watch(() => props.words, () => {
  syncDisplayList();
}, { deep: true, immediate: true });

const starredCount = computed(() => {
  return props.words.filter(w => w.isStarred).length;
});

const activeList = computed(() => {
  if (starredOnly.value) {
    return displayList.value.filter(w => w.isStarred);
  }
  return displayList.value;
});

const currentWord = computed(() => {
  if (activeList.value.length === 0) return {};
  return activeList.value[currentIndex.value] || activeList.value[0] || {};
});

function flipCard() {
  isFlipped.value = !isFlipped.value;
}

function nextCard() {
  isFlipped.value = false;
  if (currentIndex.value < activeList.value.length - 1) {
    currentIndex.value++;
  } else {
    currentIndex.value = 0;
  }
}

function prevCard() {
  isFlipped.value = false;
  if (currentIndex.value > 0) {
    currentIndex.value--;
  } else {
    currentIndex.value = Math.max(0, activeList.value.length - 1);
  }
}

function playAudio(text) {
  if (text) {
    speakText(text);
  } else if (currentWord.value?.word) {
    speakText(currentWord.value.word);
  }
}

function toggleShuffle() {
  isShuffled.value = !isShuffled.value;
  currentIndex.value = 0;
  isFlipped.value = false;
  syncDisplayList();
}

function toggleStarredFilter() {
  starredOnly.value = !starredOnly.value;
  currentIndex.value = 0;
  isFlipped.value = false;
}

function toggleFrontLanguage() {
  frontLanguage.value = frontLanguage.value === 'en' ? 'vi' : 'en';
  isFlipped.value = false;
}

function onToggleStar() {
  if (currentWord.value && currentWord.value.id) {
    emit('toggle-star', currentWord.value.id);
  }
}

function markWordStatus(level) {
  if (currentWord.value && currentWord.value.id) {
    emit('update-mastery', { wordId: currentWord.value.id, masteryLevel: level });
    nextCard();
  }
}

function toggleAutoPlay() {
  if (isAutoPlaying.value) {
    stopAutoPlay();
  } else {
    startAutoPlay();
  }
}

function startAutoPlay() {
  isAutoPlaying.value = true;
  playAudio(currentWord.value.word);
  
  autoPlayTimer = setInterval(() => {
    if (!isFlipped.value) {
      isFlipped.value = true;
    } else {
      nextCard();
      setTimeout(() => {
        if (currentWord.value?.word) {
          playAudio(currentWord.value.word);
        }
      }, 300);
    }
  }, 2500);
}

function stopAutoPlay() {
  isAutoPlaying.value = false;
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer);
    autoPlayTimer = null;
  }
}

function handleKeyDown(e) {
  if (['INPUT', 'TEXTAREA'].includes(e.target.tagName)) return;

  if (e.code === 'Space' || e.code === 'Enter') {
    e.preventDefault();
    flipCard();
  } else if (e.code === 'ArrowRight') {
    e.preventDefault();
    nextCard();
  } else if (e.code === 'ArrowLeft') {
    e.preventDefault();
    prevCard();
  } else if (e.code === 'KeyA' || e.code === 'ArrowUp') {
    e.preventDefault();
    playAudio(currentWord.value?.word);
  } else if (e.code === 'KeyS') {
    e.preventDefault();
    onToggleStar();
  } else if (e.code === 'Digit1') {
    e.preventDefault();
    markWordStatus(1);
  } else if (e.code === 'Digit2') {
    e.preventDefault();
    markWordStatus(2);
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown);
  stopAutoPlay();
});
</script>
