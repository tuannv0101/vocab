<template>
  <div class="max-w-3xl mx-auto py-6 px-4">
    
    <!-- Empty State -->
    <div v-if="words.length < 4" class="text-center py-20 bg-white rounded-3xl border border-slate-200 shadow-xs">
      <div class="w-16 h-16 bg-slate-100 text-slate-400 rounded-2xl flex items-center justify-center mx-auto mb-4">
        <FileText class="w-8 h-8" />
      </div>
      <h3 class="text-lg font-bold text-slate-700">Cần ít nhất 4 từ vựng để tạo bài kiểm tra</h3>
      <p class="text-sm text-slate-500 mt-1">Vui lòng thêm thêm từ vựng hoặc chọn bộ từ khác.</p>
    </div>

    <!-- 1. QUIZLET-STYLE TEST CONFIGURATION SCREEN (Dark Theme Card exactly like screenshot) -->
    <div v-else-if="!testStarted && !testFinished" class="max-w-lg mx-auto">
      
      <div class="bg-[#1c1d36] rounded-3xl p-6 sm:p-8 shadow-2xl border border-slate-800 text-white">
        
        <!-- Header title & subtitle -->
        <div class="mb-6">
          <div class="flex items-center justify-between">
            <h2 class="text-xl sm:text-2xl font-black text-white tracking-tight flex items-center gap-2.5">
              <span>Thiết lập bài kiểm tra</span>
            </h2>
            <span class="px-2.5 py-1 bg-indigo-500/20 text-indigo-300 border border-indigo-500/30 text-xs font-bold rounded-full">
              {{ words.length }} từ có sẵn
            </span>
          </div>
          <p class="text-xs text-slate-400 mt-1">Tùy chỉnh số lượng câu hỏi và hình thức kiểm tra tương tự Quizlet</p>
        </div>

        <!-- Options Container -->
        <div class="space-y-4">
          
          <!-- Option: Number of questions -->
          <div class="flex items-center justify-between py-2">
            <div>
              <span class="text-sm sm:text-base font-semibold text-slate-200 block">
                Câu hỏi <span class="text-xs text-slate-400 font-normal">(tối đa {{ maxQuestions }})</span>
              </span>
            </div>

            <div class="flex items-center gap-2">
              <input
                type="number"
                v-model.number="testSettings.questionCount"
                :min="1"
                :max="maxQuestions"
                class="w-20 px-3 py-2 bg-[#2a2c4e] border border-slate-700/80 rounded-xl text-center font-bold text-white text-base focus:outline-none focus:ring-2 focus:ring-indigo-500"
                @blur="sanitizeQuestionCount"
              />
            </div>
          </div>

          <!-- Option: Answer with (Trả lời bằng) -->
          <div class="flex items-center justify-between py-2">
            <div>
              <span class="text-sm sm:text-base font-semibold text-slate-200 block">
                Trả lời bằng
              </span>
            </div>

            <div class="relative">
              <select
                v-model="testSettings.answerLanguage"
                class="appearance-none px-4 py-2 pr-9 bg-[#2a2c4e] border border-slate-700/80 rounded-xl text-sm font-bold text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 cursor-pointer"
              >
                <option value="both">Cả hai</option>
                <option value="vi">Tiếng Việt (Định nghĩa)</option>
                <option value="en">Tiếng Anh (Thuật ngữ)</option>
              </select>
              <ChevronDown class="w-4 h-4 text-slate-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" />
            </div>
          </div>

          <!-- Horizontal Divider Line -->
          <div class="border-t border-slate-700/60 my-2 pt-2"></div>

          <!-- Question Types Toggles -->
          <div class="space-y-3.5">
            
            <!-- Type 1: Đúng / Sai -->
            <div class="flex items-center justify-between py-1">
              <span class="text-sm sm:text-base font-semibold text-slate-200">
                Đúng/Sai
              </span>
              <button
                type="button"
                @click="toggleType('trueFalse')"
                :class="[
                  'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none',
                  testSettings.types.trueFalse ? 'bg-[#5b5af5]' : 'bg-slate-600/70'
                ]"
              >
                <span
                  :class="[
                    'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                    testSettings.types.trueFalse ? 'translate-x-5' : 'translate-x-0'
                  ]"
                />
              </button>
            </div>

            <!-- Type 2: Trắc nghiệm -->
            <div class="flex items-center justify-between py-1">
              <span class="text-sm sm:text-base font-semibold text-slate-200">
                Trắc nghiệm
              </span>
              <button
                type="button"
                @click="toggleType('multipleChoice')"
                :class="[
                  'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none',
                  testSettings.types.multipleChoice ? 'bg-[#5b5af5]' : 'bg-slate-600/70'
                ]"
              >
                <span
                  :class="[
                    'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                    testSettings.types.multipleChoice ? 'translate-x-5' : 'translate-x-0'
                  ]"
                />
              </button>
            </div>

            <!-- Type 3: Ghép thẻ -->
            <div class="flex items-center justify-between py-1">
              <span class="text-sm sm:text-base font-semibold text-slate-200">
                Ghép thẻ
              </span>
              <button
                type="button"
                @click="toggleType('matching')"
                :class="[
                  'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none',
                  testSettings.types.matching ? 'bg-[#5b5af5]' : 'bg-slate-600/70'
                ]"
              >
                <span
                  :class="[
                    'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                    testSettings.types.matching ? 'translate-x-5' : 'translate-x-0'
                  ]"
                />
              </button>
            </div>

            <!-- Type 4: Tự luận -->
            <div class="flex items-center justify-between py-1">
              <span class="text-sm sm:text-base font-semibold text-slate-200">
                Tự luận
              </span>
              <button
                type="button"
                @click="toggleType('written')"
                :class="[
                  'relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none',
                  testSettings.types.written ? 'bg-[#5b5af5]' : 'bg-slate-600/70'
                ]"
              >
                <span
                  :class="[
                    'pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out',
                    testSettings.types.written ? 'translate-x-5' : 'translate-x-0'
                  ]"
                />
              </button>
            </div>

          </div>

          <!-- Bottom CTA Button (Aligned Bottom Right as in Screenshot) -->
          <div class="pt-6 flex justify-end">
            <button
              type="button"
              @click="generateTest"
              :disabled="activeTypesCount === 0"
              class="w-full sm:w-auto px-7 py-3.5 bg-[#5b5af5] hover:bg-[#4d4ce3] active:scale-95 disabled:opacity-40 disabled:cursor-not-allowed text-white font-extrabold text-sm rounded-2xl shadow-lg shadow-indigo-600/30 transition flex items-center justify-center gap-2 cursor-pointer"
            >
              <span>Bắt đầu làm kiểm tra</span>
            </button>
          </div>

        </div>

      </div>

    </div>

    <!-- 2. RESULT SCREEN -->
    <div v-else-if="testFinished" class="bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-sm">
      <div class="text-center mb-8 pb-6 border-b border-slate-100">
        <div class="inline-flex p-4 rounded-full mb-3" :class="scoreColorClass">
          <Trophy class="w-10 h-10" />
        </div>
        <h2 class="text-3xl font-black text-slate-800 mb-1">{{ scoreGradeText }}</h2>
        <p class="text-sm text-slate-500">Kết quả bài thi kiểm tra từ vựng của bạn</p>

        <div class="inline-flex items-center gap-6 mt-6 p-4 bg-slate-50 rounded-2xl border border-slate-100">
          <div>
            <span class="text-xs font-semibold text-slate-400 block uppercase">Điểm số</span>
            <span class="text-3xl font-black text-indigo-600">{{ finalScore }} / {{ totalScoredItems }}</span>
          </div>
          <div class="w-px h-8 bg-slate-200"></div>
          <div>
            <span class="text-xs font-semibold text-slate-400 block uppercase">Tỷ lệ đúng</span>
            <span class="text-3xl font-black text-emerald-600">
              {{ Math.round((finalScore / (totalScoredItems || 1)) * 100) }}%
            </span>
          </div>
        </div>

        <div class="mt-6 flex justify-center gap-3">
          <button
            @click="testStarted = false; testFinished = false"
            class="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold text-sm shadow-md shadow-indigo-100 transition flex items-center gap-2"
          >
            <RotateCcw class="w-4 h-4" />
            <span>Thiết lập bài kiểm tra mới</span>
          </button>
        </div>
      </div>

      <!-- Question Review List -->
      <h3 class="text-base font-bold text-slate-700 mb-4">Chi tiết bài làm:</h3>
      <div class="space-y-4">
        <div
          v-for="(q, idx) in questions"
          :key="idx"
          :class="[
            'p-5 rounded-2xl border transition',
            q.userCorrect ? 'bg-emerald-50/50 border-emerald-200' : 'bg-rose-50/50 border-rose-200'
          ]"
        >
          <!-- Multiple Choice / True-False / Written review -->
          <div v-if="q.type !== 'matching'">
            <div class="flex items-start justify-between gap-2 mb-2">
              <div class="flex items-center gap-2">
                <span
                  class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold font-mono"
                  :class="q.userCorrect ? 'bg-emerald-200 text-emerald-800' : 'bg-rose-200 text-rose-800'"
                >
                  {{ idx + 1 }}
                </span>
                <span class="font-extrabold text-slate-800 text-lg">{{ q.prompt }}</span>
                <button
                  v-if="q.targetWord"
                  @click="speakText(q.targetWord.word)"
                  class="p-1 text-slate-400 hover:text-indigo-600 transition"
                  title="Nghe phát âm"
                >
                  <Volume2 class="w-4 h-4" />
                </button>
              </div>
              <span
                class="text-xs font-bold px-2 py-0.5 rounded-full"
                :class="q.userCorrect ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'"
              >
                {{ q.userCorrect ? 'Đúng' : 'Sai' }}
              </span>
            </div>

            <div class="text-sm space-y-1 ml-8">
              <p v-if="q.userAnswer">
                <span class="text-slate-500">Câu trả lời của bạn:</span>
                <strong :class="q.userCorrect ? 'text-emerald-700 font-semibold' : 'text-rose-700 font-semibold'">
                  {{ q.userAnswer }}
                </strong>
              </p>
              <p v-else class="text-rose-600 italic">Bạn chưa trả lời câu này</p>
              
              <p v-if="!q.userCorrect" class="text-slate-700">
                <span class="text-slate-500">Đáp án chính xác:</span>
                <strong class="text-indigo-900 font-bold ml-1">{{ q.correctAnswerText }}</strong>
                <span v-if="q.targetWord && q.targetWord.ipa" class="text-xs font-mono text-indigo-500 ml-2">
                  {{ q.targetWord.ipa }}
                </span>
              </p>
            </div>
          </div>

          <!-- Matching Review -->
          <div v-else>
            <div class="flex items-start justify-between gap-2 mb-3">
              <div class="flex items-center gap-2">
                <span
                  class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold font-mono"
                  :class="q.userCorrect ? 'bg-emerald-200 text-emerald-800' : 'bg-rose-200 text-rose-800'"
                >
                  {{ idx + 1 }}
                </span>
                <span class="font-extrabold text-slate-800 text-lg">Ghép thẻ từ vựng</span>
              </div>
              <span
                class="text-xs font-bold px-2 py-0.5 rounded-full"
                :class="q.userCorrect ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'"
              >
                {{ q.matchingScore }} / {{ q.pairs.length }} cặp đúng
              </span>
            </div>

            <div class="space-y-2 ml-8 text-sm">
              <div
                v-for="pair in q.pairs"
                :key="pair.id"
                class="p-2.5 rounded-xl border bg-white flex flex-col sm:flex-row sm:items-center justify-between gap-2"
                :class="q.userMatches[pair.id] === pair.correctLetter ? 'border-emerald-200' : 'border-rose-200'"
              >
                <div class="flex items-center gap-2">
                  <strong class="text-slate-800">{{ pair.word }}</strong>
                  <span class="text-slate-400">→</span>
                  <span :class="q.userMatches[pair.id] === pair.correctLetter ? 'text-emerald-700 font-semibold' : 'text-rose-600 font-semibold'">
                    {{ q.userMatches[pair.id] ? `Đã chọn [${q.userMatches[pair.id]}]` : 'Chưa ghép' }}
                  </span>
                </div>
                <div v-if="q.userMatches[pair.id] !== pair.correctLetter" class="text-xs text-indigo-800 font-medium">
                  Đáp án: <strong>[{{ pair.correctLetter }}] {{ pair.meaning }}</strong>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- 3. ACTIVE TEST PAPER SCREEN -->
    <div v-else>
      
      <!-- Top Sticky Action Bar -->
      <div class="flex items-center justify-between gap-4 mb-6 sticky top-20 z-20 bg-white/95 backdrop-blur p-4 rounded-2xl border border-slate-200 shadow-sm">
        <div class="flex items-center gap-3">
          <button
            @click="testStarted = false"
            class="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition"
            title="Quay lại cài đặt bài thi"
          >
            <RotateCcw class="w-4 h-4" />
          </button>
          <div>
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wider">Bài thi kiểm tra</span>
            <p class="text-sm font-bold text-slate-800">
              Đã làm: <span class="text-indigo-600">{{ answeredCount }} / {{ totalScoredItems }}</span> câu
            </p>
          </div>
        </div>

        <button
          @click="submitTest"
          class="px-5 py-2.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white rounded-xl font-bold text-sm shadow-md shadow-emerald-200 transition"
        >
          Nộp bài chấm điểm
        </button>
      </div>

      <!-- Questions List -->
      <div class="space-y-6">
        <div
          v-for="(q, idx) in questions"
          :key="idx"
          class="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-xs"
        >
          <div class="flex items-center justify-between text-xs text-slate-400 font-semibold mb-3">
            <span class="bg-indigo-50 text-indigo-700 px-2.5 py-0.5 rounded-md font-mono">
              Câu {{ idx + 1 }} / {{ questions.length }}
            </span>
            <span class="text-slate-400 font-medium">{{ q.typeLabel }}</span>
          </div>

          <h3 v-if="q.type !== 'matching'" class="text-xl sm:text-2xl font-extrabold text-slate-800 mb-6">
            {{ q.prompt }}
          </h3>

          <!-- Form 1: Multiple Choice -->
          <div v-if="q.type === 'multiple_choice'" class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <button
              v-for="(opt, optIdx) in q.options"
              :key="optIdx"
              @click="q.userAnswer = opt"
              :class="[
                'p-3.5 rounded-xl border text-left font-medium text-sm transition-all flex items-center gap-3',
                q.userAnswer === opt
                  ? 'bg-indigo-50 border-indigo-500 text-indigo-900 ring-2 ring-indigo-200 font-bold'
                  : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
              ]"
            >
              <span
                class="w-6 h-6 rounded-md flex items-center justify-center text-xs font-bold border shrink-0"
                :class="q.userAnswer === opt ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-slate-100 text-slate-600 border-slate-200'"
              >
                {{ ['A', 'B', 'C', 'D'][optIdx] }}
              </span>
              <span class="leading-snug">{{ opt }}</span>
            </button>
          </div>

          <!-- Form 2: True / False -->
          <div v-else-if="q.type === 'true_false'" class="space-y-4">
            <div class="p-4 bg-slate-50 rounded-2xl border border-slate-200 text-sm">
              <span class="text-slate-500">Định nghĩa: </span>
              <strong class="text-slate-800 font-bold ml-1 text-base">{{ q.statementMeaning }}</strong>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <button
                type="button"
                @click="q.userAnswer = 'Đúng'"
                :class="[
                  'py-3 rounded-xl border font-bold text-sm transition',
                  q.userAnswer === 'Đúng'
                    ? 'bg-emerald-50 border-emerald-500 text-emerald-800 ring-2 ring-emerald-200'
                    : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
                ]"
              >
                ✓ Đúng
              </button>
              <button
                type="button"
                @click="q.userAnswer = 'Sai'"
                :class="[
                  'py-3 rounded-xl border font-bold text-sm transition',
                  q.userAnswer === 'Sai'
                    ? 'bg-rose-50 border-rose-500 text-rose-800 ring-2 ring-rose-200'
                    : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
                ]"
              >
                ✗ Sai
              </button>
            </div>
          </div>

          <!-- Form 3: Written input -->
          <div v-else-if="q.type === 'written'" class="space-y-2">
            <input
              type="text"
              v-model="q.userAnswer"
              :placeholder="q.isPromptVi ? 'Nhập từ vựng tiếng Anh tương ứng...' : 'Nhập nghĩa tiếng Việt tương ứng...'"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-base font-semibold focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white transition"
            />
          </div>

          <!-- Form 4: Matching (Ghép thẻ) -->
          <div v-else-if="q.type === 'matching'" class="space-y-4">
            <p class="text-sm text-slate-600 font-medium">
              Ghép từng từ vựng bên trái với định nghĩa phù hợp bằng cách chọn chữ cái tương ứng:
            </p>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Left: Terms with selectors -->
              <div class="space-y-2.5">
                <div
                  v-for="(pair, pIdx) in q.pairs"
                  :key="pair.id"
                  class="p-3 bg-slate-50 rounded-2xl border border-slate-200 flex items-center justify-between gap-3"
                >
                  <div class="flex items-center gap-2">
                    <span class="w-6 h-6 rounded-md bg-indigo-100 text-indigo-700 font-bold text-xs flex items-center justify-center">
                      {{ pIdx + 1 }}
                    </span>
                    <strong class="text-slate-800 font-bold text-sm">{{ pair.word }}</strong>
                  </div>

                  <select
                    v-model="q.userMatches[pair.id]"
                    class="px-3 py-1.5 bg-white border border-slate-300 rounded-lg text-xs font-bold text-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  >
                    <option value="">Chọn...</option>
                    <option
                      v-for="def in q.letteredDefinitions"
                      :key="def.letter"
                      :value="def.letter"
                    >
                      Đáp án {{ def.letter }}
                    </option>
                  </select>
                </div>
              </div>

              <!-- Right: Lettered Definitions Reference -->
              <div class="space-y-2 bg-indigo-50/50 p-3.5 rounded-2xl border border-indigo-100">
                <span class="text-xs font-bold text-indigo-900 uppercase tracking-wider block mb-1">Danh sách định nghĩa</span>
                <div
                  v-for="def in q.letteredDefinitions"
                  :key="def.letter"
                  class="p-2.5 bg-white rounded-xl border border-indigo-100/80 text-xs flex items-start gap-2.5"
                >
                  <span class="w-5 h-5 rounded-md bg-indigo-600 text-white font-bold text-xs flex items-center justify-center shrink-0">
                    {{ def.letter }}
                  </span>
                  <span class="text-slate-700 font-medium leading-relaxed">{{ def.text }}</span>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>

      <div class="mt-8 text-center pb-6">
        <button
          @click="submitTest"
          class="px-8 py-3.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white rounded-xl font-bold text-base shadow-lg shadow-emerald-200 transition"
        >
          Nộp bài kiểm tra ({{ answeredCount }} / {{ totalScoredItems }})
        </button>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { FileText, Trophy, Volume2, RotateCcw, ChevronDown } from 'lucide-vue-next';
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

// Setup Settings modeled after Quizlet's setup screen
const testSettings = ref({
  questionCount: 20,
  answerLanguage: 'both', // 'both', 'vi', 'en'
  types: {
    trueFalse: false,
    multipleChoice: true,
    matching: false,
    written: false
  }
});

const maxQuestions = computed(() => {
  return Math.min(100, props.words.length || 100);
});

const activeTypesCount = computed(() => {
  return Object.values(testSettings.value.types).filter(Boolean).length;
});

function toggleType(typeKey) {
  // Prevent disabling all toggles
  if (testSettings.value.types[typeKey] && activeTypesCount.value <= 1) {
    return;
  }
  testSettings.value.types[typeKey] = !testSettings.value.types[typeKey];
}

function sanitizeQuestionCount() {
  if (!testSettings.value.questionCount || testSettings.value.questionCount < 1) {
    testSettings.value.questionCount = Math.min(10, maxQuestions.value);
  } else if (testSettings.value.questionCount > maxQuestions.value) {
    testSettings.value.questionCount = maxQuestions.value;
  }
}

const testStarted = ref(false);
const testFinished = ref(false);
const questions = ref([]);
const finalScore = ref(0);

const totalScoredItems = computed(() => {
  let count = 0;
  for (const q of questions.value) {
    if (q.type === 'matching') {
      count += q.pairs.length;
    } else {
      count += 1;
    }
  }
  return count;
});

const answeredCount = computed(() => {
  let count = 0;
  for (const q of questions.value) {
    if (q.type === 'matching') {
      for (const pair of q.pairs) {
        if (q.userMatches[pair.id]) count++;
      }
    } else if (q.userAnswer && q.userAnswer.toString().trim() !== '') {
      count++;
    }
  }
  return count;
});

const scoreGradeText = computed(() => {
  const total = totalScoredItems.value || 1;
  const pct = (finalScore.value / total) * 100;
  if (pct >= 90) return 'Xuất sắc! 🎉';
  if (pct >= 75) return 'Làm tốt lắm! 👍';
  if (pct >= 50) return 'Khá tốt! 📚';
  return 'Cần cố gắng thêm! 💪';
});

const scoreColorClass = computed(() => {
  const total = totalScoredItems.value || 1;
  const pct = (finalScore.value / total) * 100;
  if (pct >= 80) return 'bg-emerald-100 text-emerald-600';
  if (pct >= 50) return 'bg-amber-100 text-amber-600';
  return 'bg-rose-100 text-rose-600';
});

function generateTest() {
  sanitizeQuestionCount();
  const activeTypes = [];
  if (testSettings.value.types.multipleChoice) activeTypes.push('multiple_choice');
  if (testSettings.value.types.trueFalse) activeTypes.push('true_false');
  if (testSettings.value.types.written) activeTypes.push('written');
  if (testSettings.value.types.matching) activeTypes.push('matching');

  if (activeTypes.length === 0) return;

  const targetCount = Math.min(testSettings.value.questionCount, props.words.length);
  const shuffledWords = [...props.words].sort(() => Math.random() - 0.5);

  const generated = [];
  let wordIndex = 0;

  // If matching is active, generate 1 or 2 matching sections (4 pairs each)
  if (testSettings.value.types.matching && shuffledWords.length >= 4) {
    const matchingBlockSize = Math.min(4, targetCount - generated.length);
    if (matchingBlockSize >= 3) {
      const matchWords = shuffledWords.slice(wordIndex, wordIndex + matchingBlockSize);
      wordIndex += matchingBlockSize;

      const letteredDefinitions = matchWords
        .map(w => ({ id: w.id, text: formatMeaning(w.meaning, w.partOfSpeech) }))
        .sort(() => Math.random() - 0.5)
        .map((item, idx) => ({
          letter: String.fromCharCode(65 + idx),
          id: item.id,
          text: item.text
        }));

      const pairs = matchWords.map(w => {
        const correctDef = letteredDefinitions.find(d => d.id === w.id);
        const cleanMeaning = formatMeaning(w.meaning, w.partOfSpeech);
        return {
          id: w.id,
          word: w.word,
          meaning: cleanMeaning,
          correctLetter: correctDef ? correctDef.letter : 'A'
        };
      });

      generated.push({
        type: 'matching',
        typeLabel: 'Ghép thẻ từ vựng',
        pairs,
        letteredDefinitions,
        userMatches: {},
        matchingScore: 0,
        userCorrect: false
      });
    }
  }

  // Generate other question types for remaining targetCount
  const otherTypes = activeTypes.filter(t => t !== 'matching');
  const fallbackTypes = otherTypes.length > 0 ? otherTypes : ['multiple_choice'];

  while (generated.length < targetCount && wordIndex < shuffledWords.length) {
    const word = shuffledWords[wordIndex++];
    const qType = fallbackTypes[generated.length % fallbackTypes.length];
    const cleanTargetMeaning = formatMeaning(word.meaning, word.partOfSpeech);

    // Determine direction based on answerLanguage
    let isPromptVi = false;
    if (testSettings.value.answerLanguage === 'en') {
      isPromptVi = true; // prompt in Vietnamese, answer in English
    } else if (testSettings.value.answerLanguage === 'vi') {
      isPromptVi = false; // prompt in English, answer in Vietnamese
    } else {
      isPromptVi = Math.random() > 0.5; // Both
    }

    if (qType === 'multiple_choice') {
      const distractors = props.words
        .filter(w => w.id !== word.id)
        .sort(() => Math.random() - 0.5)
        .slice(0, 3);

      const options = [
        isPromptVi ? word.word : cleanTargetMeaning,
        ...distractors.map(d => isPromptVi ? d.word : formatMeaning(d.meaning, d.partOfSpeech))
      ].sort(() => Math.random() - 0.5);

      generated.push({
        type: 'multiple_choice',
        typeLabel: 'Trắc nghiệm 4 đáp án',
        prompt: isPromptVi
          ? `Nghĩa "${cleanTargetMeaning}" tương ứng với từ tiếng Anh nào?`
          : `Từ "${word.word}" có nghĩa là gì?`,
        targetWord: word,
        options,
        correctAnswerText: isPromptVi ? word.word : cleanTargetMeaning,
        userAnswer: null,
        userCorrect: false
      });
    } else if (qType === 'true_false') {
      const isActuallyTrue = Math.random() > 0.5;
      let statementMeaning = cleanTargetMeaning;
      if (!isActuallyTrue) {
        const falseWord = props.words.find(w => w.id !== word.id) || word;
        statementMeaning = formatMeaning(falseWord.meaning, falseWord.partOfSpeech);
      }

      generated.push({
        type: 'true_false',
        typeLabel: 'Đúng / Sai',
        prompt: `Từ "${word.word}" có đúng với định nghĩa sau không?`,
        statementMeaning,
        targetWord: word,
        correctAnswerText: isActuallyTrue ? 'Đúng' : `Sai (nghĩa đúng: ${cleanTargetMeaning})`,
        userAnswer: null,
        isActuallyTrue,
        userCorrect: false
      });
    } else if (qType === 'written') {
      generated.push({
        type: 'written',
        typeLabel: 'Tự luận (Điền từ)',
        prompt: isPromptVi
          ? `Điền từ tiếng Anh có nghĩa: "${cleanTargetMeaning}"`
          : `Điền nghĩa tiếng Việt của từ: "${word.word}"`,
        targetWord: word,
        isPromptVi,
        correctAnswerText: isPromptVi ? word.word : cleanTargetMeaning,
        userAnswer: '',
        userCorrect: false
      });
    }
  }

  questions.value = generated;
  testStarted.value = true;
  testFinished.value = false;
  finalScore.value = 0;
}

function submitTest() {
  let score = 0;

  questions.value.forEach(q => {
    if (q.type === 'multiple_choice') {
      q.userCorrect = q.userAnswer === q.correctAnswerText;
      if (q.userCorrect) score++;
    } else if (q.type === 'true_false') {
      const expected = q.isActuallyTrue ? 'Đúng' : 'Sai';
      q.userCorrect = q.userAnswer === expected;
      if (q.userCorrect) score++;
    } else if (q.type === 'written') {
      const cleanUser = (q.userAnswer || '').trim().toLowerCase();
      const cleanTarget = q.correctAnswerText.trim().toLowerCase();
      q.userCorrect = cleanUser === cleanTarget || 
                      cleanTarget.startsWith(cleanUser) || 
                      cleanTarget.includes(cleanUser);
      if (q.userCorrect) score++;
    } else if (q.type === 'matching') {
      let matchingScore = 0;
      for (const pair of q.pairs) {
        if (q.userMatches[pair.id] === pair.correctLetter) {
          matchingScore++;
        }
      }
      q.matchingScore = matchingScore;
      q.userCorrect = matchingScore === q.pairs.length;
      score += matchingScore;
    }
  });

  finalScore.value = score;
  testFinished.value = true;

  if (score / (totalScoredItems.value || 1) >= 0.7) {
    confetti({
      particleCount: 120,
      spread: 70,
      origin: { y: 0.6 }
    });
  }
}
</script>
