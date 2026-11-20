<template>
  <div class="max-w-3xl mx-auto py-6 px-4">
    
    <!-- Empty State -->
    <div v-if="words.length < 4" class="text-center py-20 bg-white rounded-2xl border border-slate-200">
      <h3 class="text-lg font-bold text-slate-700">Cần ít nhất 4 từ để bắt đầu chế độ học</h3>
      <p class="text-sm text-slate-500 mt-1">Bộ từ vựng hiện tại của bạn chưa đủ từ. Vui lòng thêm từ hoặc chọn bộ khác.</p>
    </div>

    <!-- Finished Screen -->
    <div v-else-if="isCompleted" class="bg-white rounded-3xl p-8 sm:p-12 text-center border border-slate-200 shadow-sm max-w-xl mx-auto">
      <div class="w-20 h-20 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-inner">
        <Trophy class="w-10 h-10" />
      </div>

      <h2 class="text-3xl font-extrabold text-slate-800 mb-2">Chúc mừng bạn đã hoàn thành! 🎉</h2>
      <p class="text-slate-500 mb-8">Bạn đã xuất sắc ôn tập toàn bộ từ vựng trong lượt học này.</p>

      <div class="grid grid-cols-3 gap-4 mb-8 text-center">
        <div class="p-4 bg-slate-50 rounded-2xl border border-slate-100">
          <p class="text-2xl font-black text-indigo-600">{{ totalLearned }}</p>
          <p class="text-xs font-semibold text-slate-500 mt-1">Từ đã học</p>
        </div>
        <div class="p-4 bg-emerald-50 rounded-2xl border border-emerald-100">
          <p class="text-2xl font-black text-emerald-600">{{ correctCount }}</p>
          <p class="text-xs font-semibold text-emerald-700 mt-1">Đúng lần 1</p>
        </div>
        <div class="p-4 bg-amber-50 rounded-2xl border border-amber-100">
          <p class="text-2xl font-black text-amber-600">{{ Math.round((correctCount / Math.max(1, totalLearned)) * 100) }}%</p>
          <p class="text-xs font-semibold text-amber-700 mt-1">Độ chính xác</p>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row gap-3 justify-center">
        <button
          @click="restartSession(false)"
          class="px-6 py-3 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white rounded-xl font-bold shadow-md shadow-indigo-200 transition"
        >
          Học lại toàn bộ
        </button>
        <button
          v-if="missedWords.length > 0"
          @click="restartSession(true)"
          class="px-6 py-3 bg-rose-50 hover:bg-rose-100 text-rose-700 rounded-xl font-bold border border-rose-200 transition"
        >
          Ôn lại {{ missedWords.length }} từ làm sai
        </button>
      </div>
    </div>

    <!-- Active Question -->
    <div v-else>
      
      <!-- Session Header & Progress -->
      <div class="flex items-center justify-between gap-4 mb-4">
        <div>
          <span class="text-xs font-bold text-slate-400 uppercase tracking-wider">Tiến trình học</span>
          <p class="text-sm font-bold text-slate-700">
            Còn lại {{ queue.length }} câu • Đã đúng: <span class="text-emerald-600">{{ correctCount }}</span>
          </p>
        </div>

        <button
          @click="restartSession(false)"
          class="text-xs font-semibold text-slate-500 hover:text-indigo-600 p-2 rounded-lg hover:bg-slate-100 transition flex items-center gap-1"
        >
          <RotateCcw class="w-3.5 h-3.5" />
          <span>Bắt đầu lại</span>
        </button>
      </div>

      <div class="w-full bg-slate-200 rounded-full h-2.5 mb-6 overflow-hidden">
        <div
          class="bg-gradient-to-r from-indigo-500 to-emerald-500 h-2.5 rounded-full transition-all duration-300"
          :style="{ width: `${progressPercent}%` }"
        ></div>
      </div>

      <!-- Question Card -->
      <div class="bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-sm mb-6">
        
        <div class="flex items-center justify-between text-xs text-slate-400 font-semibold mb-4">
          <span class="bg-indigo-50 text-indigo-700 px-3 py-1 rounded-full">
            {{ currentQuestion.questionType === 'en_to_vi' ? 'Tiếng Anh → Nghĩa tiếng Việt' : 'Nghĩa tiếng Việt → Chọn từ tiếng Anh' }}
          </span>
          <button
            @click="speakCurrent"
            class="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-full transition"
            title="Nghe phát âm"
          >
            <Volume2 class="w-5 h-5" />
          </button>
        </div>

        <!-- Prompt -->
        <div class="py-4 text-center">
          <h2 class="text-2xl sm:text-4xl font-extrabold text-slate-800 leading-snug">
            {{ currentQuestion.prompt }}
          </h2>
          <p v-if="currentQuestion.subPrompt" class="text-indigo-600 font-mono text-lg mt-2 font-medium">
            {{ currentQuestion.subPrompt }}
          </p>
        </div>

        <!-- Multiple Choice Options -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5 mt-6">
          <button
            v-for="(option, idx) in currentQuestion.options"
            :key="idx"
            @click="selectOption(option)"
            :disabled="answered"
            :class="[
              'p-4 rounded-2xl text-left font-medium transition-all border-2 text-sm sm:text-base flex items-center justify-between group',
              getOptionClass(option)
            ]"
          >
            <div class="flex items-center gap-3">
              <span class="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold font-mono transition-colors border"
                :class="getOptionBadgeClass(option)"
              >
                {{ ['A', 'B', 'C', 'D'][idx] }}
              </span>
              <span class="text-slate-800 font-semibold">{{ option.label }}</span>
            </div>

            <span v-if="answered && option.isCorrect" class="text-emerald-600 font-bold">
              <CheckCircle2 class="w-5 h-5" />
            </span>
            <span v-else-if="answered && selectedAnswer === option && !option.isCorrect" class="text-rose-600 font-bold">
              <X class="w-5 h-5" />
            </span>
          </button>
        </div>

        <!-- Answer Feedback Sheet -->
        <div v-if="answered" class="mt-8 pt-6 border-t border-slate-100">
          <div
            :class="[
              'p-4 rounded-2xl flex items-start gap-4 mb-4',
              isCorrect ? 'bg-emerald-50 text-emerald-800' : 'bg-rose-50 text-rose-800'
            ]"
          >
            <component :is="isCorrect ? CheckCircle2 : X" class="w-6 h-6 shrink-0 mt-0.5" />
            <div class="grow">
              <p class="font-bold text-base mb-1">
                {{ isCorrect ? 'Chính xác! Làm rất tốt! 👏' : 'Chưa đúng rồi! Hãy ghi nhớ nhé:' }}
              </p>
              <div v-if="!isCorrect" class="text-sm space-y-1">
                <p><span class="font-semibold">Từ đúng:</span> <strong class="text-indigo-900 font-bold">{{ currentQuestion.targetWord.word }}</strong></p>
                <p v-if="currentQuestion.targetWord.ipa"><span class="font-semibold">Phát âm:</span> <span class="font-mono">{{ currentQuestion.targetWord.ipa }}</span></p>
                <p><span class="font-semibold">Ý nghĩa:</span> {{ currentQuestion.targetWord.meaning }}</p>
              </div>
            </div>
          </div>

          <div class="text-right">
            <button
              @click="nextQuestion"
              class="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-bold shadow-md shadow-indigo-100 transition inline-flex items-center gap-2"
            >
              <span>Tiếp tục</span>
              <ArrowRight class="w-4 h-4" />
            </button>
          </div>
        </div>

      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { 
  Trophy, 
  RotateCcw, 
  Volume2, 
  CheckCircle2, 
  X, 
  ArrowRight 
} from 'lucide-vue-next';
import confetti from 'canvas-confetti';
import { speakText } from '@/utils/speech';

const props = defineProps({
  words: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['record-answer']);

const queue = ref([]);
const currentQuestion = ref(null);
const answered = ref(false);
const selectedAnswer = ref(null);
const isCorrect = ref(false);

const correctCount = ref(0);
const totalLearned = ref(0);
const missedWords = ref([]);
const isCompleted = ref(false);

const initialQueueSize = ref(0);

const progressPercent = computed(() => {
  if (initialQueueSize.value === 0) return 0;
  const completed = initialQueueSize.value - queue.value.length;
  return Math.min(100, Math.round((completed / initialQueueSize.value) * 100));
});

function initSession(onlyMissed = false) {
  let source = onlyMissed ? [...missedWords.value] : [...props.words];
  if (source.length < 4) {
    source = [...props.words];
  }
  
  source.sort(() => Math.random() - 0.5);
  queue.value = [...source];
  initialQueueSize.value = source.length;
  correctCount.value = 0;
  totalLearned.value = 0;
  missedWords.value = [];
  isCompleted.value = false;
  loadNextQuestion();
}

watch(() => props.words, () => {
  if (props.words.length >= 4) {
    initSession(false);
  }
}, { immediate: true });

function cleanMeaningText(meaning, partOfSpeech) {
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

function loadNextQuestion() {
  answered.value = false;
  selectedAnswer.value = null;
  isCorrect.value = false;

  if (queue.value.length === 0) {
    isCompleted.value = true;
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    });
    return;
  }

  const targetWord = queue.value[0];
  const questionType = Math.random() > 0.4 ? 'vi_to_en' : 'en_to_vi';
  const targetMeaning = cleanMeaningText(targetWord.meaning, targetWord.partOfSpeech);

  const distractors = props.words
    .filter(w => w.id !== targetWord.id)
    .sort(() => Math.random() - 0.5)
    .slice(0, 3);

  const options = [
    {
      label: questionType === 'vi_to_en' ? targetWord.word : targetMeaning,
      isCorrect: true,
      wordObj: targetWord
    },
    ...distractors.map(d => ({
      label: questionType === 'vi_to_en' ? d.word : cleanMeaningText(d.meaning, d.partOfSpeech),
      isCorrect: false,
      wordObj: d
    }))
  ];

  options.sort(() => Math.random() - 0.5);

  let subPrompt = '';
  if (questionType === 'en_to_vi') {
    subPrompt = targetWord.ipa || '';
  } else {
    const pos = (targetWord.partOfSpeech || '').trim();
    if (pos && !targetMeaning.toLowerCase().includes(pos.toLowerCase())) {
      subPrompt = pos;
    }
  }

  currentQuestion.value = {
    targetWord,
    questionType,
    prompt: questionType === 'vi_to_en' ? targetMeaning : targetWord.word,
    subPrompt,
    options
  };

  if (questionType === 'en_to_vi') {
    speakText(targetWord.word);
  }
}

function selectOption(option) {
  if (answered.value) return;
  answered.value = true;
  selectedAnswer.value = option;
  isCorrect.value = option.isCorrect;

  totalLearned.value++;
  const target = currentQuestion.value.targetWord;

  if (option.isCorrect) {
    correctCount.value++;
    speakText(target.word);
    emit('record-answer', { wordId: target.id, correct: true });
    queue.value.shift();
  } else {
    emit('record-answer', { wordId: target.id, correct: false });
    if (!missedWords.value.some(w => w.id === target.id)) {
      missedWords.value.push(target);
    }
    const missed = queue.value.shift();
    queue.value.push(missed);
  }
}

function nextQuestion() {
  loadNextQuestion();
}

function speakCurrent() {
  if (currentQuestion.value?.targetWord?.word) {
    speakText(currentQuestion.value.targetWord.word);
  }
}

function restartSession(onlyMissed = false) {
  initSession(onlyMissed);
}

function getOptionClass(option) {
  if (!answered.value) {
    return 'bg-white hover:bg-slate-50 border-slate-200 hover:border-indigo-400 text-slate-800 shadow-xs cursor-pointer';
  }
  if (option.isCorrect) {
    return 'bg-emerald-50 border-emerald-500 text-emerald-900 shadow-sm';
  }
  if (selectedAnswer.value === option && !option.isCorrect) {
    return 'bg-rose-50 border-rose-500 text-rose-900 shadow-sm';
  }
  return 'bg-slate-50 border-slate-200 text-slate-400 opacity-60';
}

function getOptionBadgeClass(option) {
  if (!answered.value) {
    return 'bg-slate-100 border-slate-200 text-slate-600 group-hover:bg-indigo-50 group-hover:border-indigo-300 group-hover:text-indigo-600';
  }
  if (option.isCorrect) {
    return 'bg-emerald-500 border-emerald-600 text-white';
  }
  if (selectedAnswer.value === option && !option.isCorrect) {
    return 'bg-rose-500 border-rose-600 text-white';
  }
  return 'bg-slate-200 text-slate-400 border-transparent';
}
</script>
