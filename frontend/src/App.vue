<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans">
    
    <!-- Navbar -->
    <Navbar
      :sets="sets"
      :current-set-id="currentSetId"
      :active-tab="activeTab"
      @change-tab="activeTab = $event"
      @select-set="selectSet"
      @open-sets-modal="isManageSetsModalOpen = true"
      @open-add-word-modal="openAddWordModal(null)"
      @open-import-modal="isBulkImportModalOpen = true"
    />

    <!-- Main Content Area -->
    <main class="grow">
      
      <!-- Loading indicator -->
      <div v-if="isLoading && !currentSet" class="flex items-center justify-center py-32">
        <div class="flex flex-col items-center gap-3">
          <div class="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
          <p class="text-sm font-semibold text-slate-500">Đang tải kho từ vựng...</p>
        </div>
      </div>

      <!-- Set Banner & Details -->
      <div v-else-if="currentSet" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6">
        
        <!-- Header Info for the active set -->
        <div class="relative overflow-hidden rounded-2xl sm:rounded-3xl bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 border border-indigo-500/20 shadow-xl shadow-indigo-950/20 p-4 sm:p-6 lg:p-8 mb-6 sm:mb-8 text-white">
          
          <!-- Subtle Background Grid Pattern -->
          <div class="absolute inset-0 opacity-10 pointer-events-none">
            <svg class="w-full h-full" xmlns="http://www.w3.org/2000/svg" width="100%" height="100%">
              <defs>
                <pattern id="grid-pattern" width="32" height="32" patternUnits="userSpaceOnUse">
                  <circle cx="2" cy="2" r="1.5" fill="white" />
                </pattern>
              </defs>
              <rect width="100%" height="100%" fill="url(#grid-pattern)" />
            </svg>
          </div>

          <!-- Ambient Glow Orbs -->
          <div class="absolute -right-16 -top-16 w-80 h-80 bg-indigo-500/25 rounded-full blur-3xl pointer-events-none"></div>
          <div class="absolute right-1/3 -bottom-20 w-64 h-64 bg-violet-600/20 rounded-full blur-3xl pointer-events-none"></div>

          <div class="relative z-10 flex flex-col lg:flex-row lg:items-center justify-between gap-5 sm:gap-6">
            
            <!-- Left Column: Set Info & Progress -->
            <div class="w-full lg:max-w-2xl min-w-0">
              <div class="flex items-center gap-2 mb-2.5 sm:mb-3 flex-wrap">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-0.5 sm:px-3 sm:py-1 rounded-full bg-indigo-500/20 border border-indigo-400/30 text-indigo-200 text-[11px] sm:text-xs font-bold backdrop-blur-md">
                  <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                  <span>Bộ từ vựng đang học</span>
                </span>
                <span class="inline-flex items-center gap-1 px-2 py-0.5 sm:px-2.5 sm:py-1 rounded-full bg-white/10 text-slate-200 text-[11px] sm:text-xs font-semibold backdrop-blur-md">
                  <Layers class="w-3 h-3 sm:w-3.5 sm:h-3.5 text-indigo-300" />
                  <span>{{ currentSet.totalWords }} từ</span>
                </span>
              </div>

              <h1 class="text-xl sm:text-2xl md:text-3xl lg:text-4xl font-black text-white tracking-tight leading-snug break-words">
                {{ currentSet.title }}
              </h1>

              <p v-if="currentSet.description" class="text-indigo-200/80 text-xs sm:text-sm mt-1.5 sm:mt-2 line-clamp-3 leading-relaxed">
                {{ currentSet.description }}
              </p>

              <!-- Overall Completion Progress Bar -->
              <div class="mt-4 sm:mt-5 w-full max-w-sm sm:max-w-md">
                <div class="flex items-center justify-between text-[11px] sm:text-xs font-semibold mb-1.5">
                  <span class="text-indigo-200">Tiến độ thành thạo</span>
                  <span class="text-emerald-400 font-bold">
                    {{ Math.round((currentSet.masteredWords / (currentSet.totalWords || 1)) * 100) }}% hoàn thành
                  </span>
                </div>
                <div class="w-full h-2 sm:h-2.5 bg-white/10 rounded-full overflow-hidden flex border border-white/10">
                  <div
                    class="bg-gradient-to-r from-emerald-400 to-teal-400 h-full transition-all duration-500"
                    :style="{ width: `${(currentSet.masteredWords / (currentSet.totalWords || 1)) * 100}%` }"
                    title="Đã thuộc"
                  ></div>
                  <div
                    class="bg-gradient-to-r from-amber-400 to-orange-400 h-full transition-all duration-500"
                    :style="{ width: `${(currentSet.learningWords / (currentSet.totalWords || 1)) * 100}%` }"
                    title="Đang học"
                  ></div>
                </div>
              </div>
            </div>

            <!-- Right Column: Interactive Stats Cards -->
            <div class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-2 gap-2 sm:gap-2.5 w-full lg:w-auto shrink-0 pt-2 lg:pt-0 border-t border-white/10 lg:border-t-0">
              
              <!-- Card: Mastered -->
              <div class="p-2.5 sm:p-3.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl sm:rounded-2xl backdrop-blur-md transition-all">
                <div class="flex items-center gap-1.5">
                  <div class="w-2 h-2 rounded-full bg-emerald-400 shadow-sm shadow-emerald-400/50"></div>
                  <span class="text-[10px] sm:text-[11px] font-bold text-slate-300 uppercase tracking-wider">Đã thuộc</span>
                </div>
                <div class="text-xl sm:text-2xl font-black text-emerald-400 mt-1">
                  {{ currentSet.masteredWords }}
                </div>
              </div>

              <!-- Card: Learning -->
              <div class="p-2.5 sm:p-3.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl sm:rounded-2xl backdrop-blur-md transition-all">
                <div class="flex items-center gap-1.5">
                  <div class="w-2 h-2 rounded-full bg-amber-400 shadow-sm shadow-amber-400/50"></div>
                  <span class="text-[10px] sm:text-[11px] font-bold text-slate-300 uppercase tracking-wider">Đang học</span>
                </div>
                <div class="text-xl sm:text-2xl font-black text-amber-400 mt-1">
                  {{ currentSet.learningWords }}
                </div>
              </div>

              <!-- Card: New / Unlearned -->
              <div class="p-2.5 sm:p-3.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl sm:rounded-2xl backdrop-blur-md transition-all">
                <div class="flex items-center gap-1.5">
                  <div class="w-2 h-2 rounded-full bg-slate-400"></div>
                  <span class="text-[10px] sm:text-[11px] font-bold text-slate-300 uppercase tracking-wider">Chưa học</span>
                </div>
                <div class="text-xl sm:text-2xl font-black text-slate-200 mt-1">
                  {{ currentSet.newWords }}
                </div>
              </div>

              <!-- Card: Starred -->
              <div class="p-2.5 sm:p-3.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl sm:rounded-2xl backdrop-blur-md transition-all">
                <div class="flex items-center gap-1.5">
                  <Star class="w-3 h-3 text-amber-300 fill-amber-300" />
                  <span class="text-[10px] sm:text-[11px] font-bold text-slate-300 uppercase tracking-wider">Gắn sao</span>
                </div>
                <div class="text-xl sm:text-2xl font-black text-amber-300 mt-1">
                  {{ currentSet.starredWords }}
                </div>
              </div>

            </div>

          </div>

        </div>

        <!-- Tab Views -->
        <FlashcardsView
          v-if="activeTab === 'flashcards'"
          :words="currentSet.words || []"
          @toggle-star="handleToggleStar"
          @update-mastery="handleUpdateMastery"
          @open-add-word-modal="openAddWordModal(null)"
          @open-import-modal="isBulkImportModalOpen = true"
        />

        <LearnView
          v-else-if="activeTab === 'learn'"
          :words="currentSet.words || []"
          @record-answer="handleRecordAnswer"
        />

        <MatchGameView
          v-else-if="activeTab === 'match'"
          :words="currentSet.words || []"
        />

        <TestView
          v-else-if="activeTab === 'test'"
          :words="currentSet.words || []"
        />

        <WordListView
          v-else-if="activeTab === 'words'"
          :words="currentSet.words || []"
          @toggle-star="handleToggleStar"
          @update-mastery="handleUpdateMastery"
          @edit-word="openAddWordModal($event)"
          @delete-word="handleDeleteWord"
          @open-add-word-modal="openAddWordModal(null)"
          @open-import-modal="isBulkImportModalOpen = true"
          @deduplicate="handleDeduplicate"
        />

      </div>

    </main>

    <!-- Footer -->
    <footer class="bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500 mt-12">
      <div class="max-w-7xl mx-auto px-4">
        <p class="font-medium">Ứng dụng ôn tập từ vựng phong cách Quizlet • Chạy trên Spring Boot & Vue 3 (Port 8888)</p>
        <p class="text-slate-400 mt-1">Dữ liệu được lưu trữ an toàn trong cơ sở dữ liệu trên máy tính của bạn</p>
      </div>
    </footer>

    <!-- Modals -->
    <AddWordModal
      :is-open="isAddWordModalOpen"
      :is-editing="!!editingWord"
      :edit-data="editingWord"
      :sets="sets"
      :current-set-id="currentSetId"
      @close="isAddWordModalOpen = false; editingWord = null"
      @save="handleSaveWord"
      @switch-to-bulk="isAddWordModalOpen = false; editingWord = null; isBulkImportModalOpen = true"
    />

    <BulkImportModal
      :is-open="isBulkImportModalOpen"
      :sets="sets"
      :current-set-id="currentSetId"
      @close="isBulkImportModalOpen = false"
      @imported="handleBulkImport"
    />

    <ManageSetsModal
      :is-open="isManageSetsModalOpen"
      :sets="sets"
      :current-set-id="currentSetId"
      @close="isManageSetsModalOpen = false"
      @select-set="selectSet"
      @create-set="handleCreateSet"
      @delete-set="handleDeleteSet"
      @reset-progress="handleResetProgress"
    />

    <!-- Toast Notification -->
    <div
      v-if="toast.show"
      class="fixed bottom-6 right-6 z-50 bg-slate-900 text-white text-sm font-semibold px-5 py-3 rounded-2xl shadow-xl flex items-center gap-2.5 transition-all duration-300"
    >
      <span>{{ toast.message }}</span>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import Navbar from '@/components/Navbar.vue';
import FlashcardsView from '@/views/FlashcardsView.vue';
import LearnView from '@/views/LearnView.vue';
import MatchGameView from '@/views/MatchGameView.vue';
import TestView from '@/views/TestView.vue';
import WordListView from '@/views/WordListView.vue';
import AddWordModal from '@/components/AddWordModal.vue';
import BulkImportModal from '@/components/BulkImportModal.vue';
import ManageSetsModal from '@/components/ManageSetsModal.vue';
import { Star, Layers, Sparkles } from 'lucide-vue-next';
import { api } from '@/utils/api';

const sets = ref([]);
const currentSetId = ref(null);
const currentSet = ref(null);
const activeTab = ref('flashcards');
const isLoading = ref(true);

const isAddWordModalOpen = ref(false);
const isBulkImportModalOpen = ref(false);
const isManageSetsModalOpen = ref(false);
const editingWord = ref(null);

const toast = reactive({
  show: false,
  message: '',
  timer: null
});

function showToast(message) {
  toast.message = message;
  toast.show = true;
  if (toast.timer) clearTimeout(toast.timer);
  toast.timer = setTimeout(() => {
    toast.show = false;
  }, 3000);
}

async function loadSets(targetId = null) {
  try {
    isLoading.value = true;
    const fetchedSets = await api.getSets();
    sets.value = fetchedSets;

    if (fetchedSets.length > 0) {
      let chosenId = targetId || currentSetId.value;
      if (!chosenId || !fetchedSets.some(s => s.id === chosenId)) {
        chosenId = fetchedSets[0].id;
      }
      await selectSet(chosenId);
    } else {
      currentSet.value = null;
    }
  } catch (err) {
    console.error('Lỗi khi tải bộ từ vựng:', err);
    showToast('Lỗi kết nối cơ sở dữ liệu: ' + err.message);
  } finally {
    isLoading.value = false;
  }
}

async function selectSet(id) {
  try {
    currentSetId.value = id;
    const setDetails = await api.getSet(id);
    currentSet.value = setDetails;
  } catch (err) {
    console.error('Lỗi tải chi tiết bộ từ:', err);
    showToast('Không thể tải bộ từ: ' + err.message);
  }
}

async function handleToggleStar(wordId) {
  try {
    const updated = await api.toggleStar(wordId);
    if (currentSet.value && currentSet.value.words) {
      const idx = currentSet.value.words.findIndex(w => w.id === wordId);
      if (idx !== -1) {
        currentSet.value.words[idx].isStarred = updated.isStarred;
        currentSet.value.starredWords = currentSet.value.words.filter(w => w.isStarred).length;
      }
    }
  } catch (err) {
    showToast('Lỗi cập nhật sao: ' + err.message);
  }
}

async function handleUpdateMastery({ wordId, masteryLevel }) {
  try {
    const updated = await api.updateMastery(wordId, masteryLevel);
    if (currentSet.value && currentSet.value.words) {
      const idx = currentSet.value.words.findIndex(w => w.id === wordId);
      if (idx !== -1) {
        currentSet.value.words[idx].masteryLevel = updated.masteryLevel;
        recalculateSetStats();
      }
    }
  } catch (err) {
    showToast('Lỗi cập nhật tiến độ: ' + err.message);
  }
}

async function handleRecordAnswer({ wordId, correct }) {
  try {
    const updated = await api.recordAnswer(wordId, correct);
    if (currentSet.value && currentSet.value.words) {
      const idx = currentSet.value.words.findIndex(w => w.id === wordId);
      if (idx !== -1) {
        currentSet.value.words[idx].masteryLevel = updated.masteryLevel;
        currentSet.value.words[idx].correctCount = updated.correctCount;
        currentSet.value.words[idx].incorrectCount = updated.incorrectCount;
        recalculateSetStats();
      }
    }
  } catch (err) {
    console.error(err);
  }
}

function recalculateSetStats() {
  if (!currentSet.value || !currentSet.value.words) return;
  let mastered = 0;
  let learning = 0;
  let starred = 0;
  currentSet.value.words.forEach(w => {
    if (w.isStarred) starred++;
    if (w.masteryLevel === 2) mastered++;
    else if (w.masteryLevel === 1) learning++;
  });
  currentSet.value.masteredWords = mastered;
  currentSet.value.learningWords = learning;
  currentSet.value.newWords = currentSet.value.words.length - mastered - learning;
  currentSet.value.starredWords = starred;
}

function openAddWordModal(word = null) {
  editingWord.value = word;
  isAddWordModalOpen.value = true;
}

async function handleSaveWord(wordData) {
  try {
    if (wordData.id) {
      await api.updateWord(wordData.id, wordData);
      showToast('Đã cập nhật từ vựng thành công! ✨');
    } else {
      await api.addWord(wordData);
      showToast('Đã thêm từ vựng mới vào kho! 🎉');
    }
    isAddWordModalOpen.value = false;
    editingWord.value = null;
    await selectSet(wordData.setId || currentSetId.value);
    await refreshSetsList();
  } catch (err) {
    showToast('Lỗi lưu từ vựng: ' + err.message);
  }
}

async function handleDeleteWord(wordId) {
  if (!confirm('Bạn có chắc chắn muốn xóa từ vựng này khỏi kho?')) return;
  try {
    await api.deleteWord(wordId);
    showToast('Đã xóa từ vựng thành công');
    await selectSet(currentSetId.value);
    await refreshSetsList();
  } catch (err) {
    showToast('Lỗi xóa từ: ' + err.message);
  }
}

async function handleBulkImport(payload) {
  try {
    let importedSet;
    if (payload.words && payload.words.length > 0) {
      importedSet = await api.batchAddWords(payload);
    } else {
      importedSet = await api.bulkImport(payload);
    }
    const count = importedSet.words ? importedSet.words.length : '';
    showToast(`Đã thêm thành công các từ vựng vào "${importedSet.title}"! 🎉`);
    isBulkImportModalOpen.value = false;
    await loadSets(importedSet.id);
  } catch (err) {
    showToast('Lỗi nhập dữ liệu: ' + err.message);
  }
}

async function handleCreateSet(setData) {
  try {
    const created = await api.createSet(setData);
    showToast(`Đã tạo bộ từ mới "${created.title}"! 📁`);
    isManageSetsModalOpen.value = false;
    await loadSets(created.id);
  } catch (err) {
    showToast('Lỗi tạo bộ từ: ' + err.message);
  }
}

async function handleDeleteSet(setId) {
  if (!confirm('Bạn có chắc chắn muốn xóa toàn bộ bộ từ vựng này và các từ bên trong?')) return;
  try {
    await api.deleteSet(setId);
    showToast('Đã xóa bộ từ vựng');
    await loadSets();
  } catch (err) {
    showToast('Lỗi xóa bộ từ: ' + err.message);
  }
}

async function handleResetProgress(setId) {
  if (!confirm('Bạn muốn đặt lại tiến độ học của bộ này về 0?')) return;
  try {
    await api.resetSetProgress(setId);
    showToast('Đã đặt lại tiến độ học');
    await selectSet(setId);
    await refreshSetsList();
  } catch (err) {
    showToast('Lỗi đặt lại tiến độ: ' + err.message);
  }
}

async function handleDeduplicate() {
  if (!currentSetId.value) return;
  if (!confirm(`Bạn có muốn tự động gộp các từ bị trùng lặp trong bộ này không?\n\n• Hệ thống sẽ giữ lại 1 từ duy nhất với thông tin đầy đủ nhất.\n• Giữ nguyên trạng thái học tập (đã thuộc, đang học, gắn sao).\n• Loại bỏ tất cả các bản ghi thừa bị lặp lại.`)) {
    return;
  }
  try {
    isLoading.value = true;
    const res = await api.deduplicateSet(currentSetId.value);
    showToast(res.message || 'Đã dọn dẹp các từ trùng lặp thành công! ✨');
    await selectSet(currentSetId.value);
    await refreshSetsList();
  } catch (err) {
    showToast('Lỗi dọn từ trùng: ' + err.message);
  } finally {
    isLoading.value = false;
  }
}

async function refreshSetsList() {
  const fetched = await api.getSets();
  sets.value = fetched;
}

onMounted(() => {
  loadSets();
});
</script>
