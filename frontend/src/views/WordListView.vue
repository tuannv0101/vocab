<template>
  <div class="max-w-5xl mx-auto py-6 px-4">
    
    <!-- Top Stats Banner -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
      <div class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <span class="text-xs font-semibold text-slate-400 block uppercase tracking-wider">Tổng số từ</span>
        <p class="text-2xl font-black text-slate-800 mt-1">{{ words.length }}</p>
      </div>

      <div class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <span class="text-xs font-semibold text-emerald-600 block uppercase tracking-wider">Đã thành thạo</span>
        <p class="text-2xl font-black text-emerald-600 mt-1">{{ stats.mastered }}</p>
      </div>

      <div class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <span class="text-xs font-semibold text-amber-600 block uppercase tracking-wider">Đang rèn luyện</span>
        <p class="text-2xl font-black text-amber-600 mt-1">{{ stats.learning }}</p>
      </div>

      <div class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <span class="text-xs font-semibold text-indigo-600 block uppercase tracking-wider">Gắn sao lưu ý</span>
        <p class="text-2xl font-black text-indigo-600 mt-1">{{ stats.starred }}</p>
      </div>
    </div>

    <!-- Duplicate Warning Banner -->
    <div
      v-if="totalExcessDuplicates > 0"
      class="mb-6 p-4 sm:p-5 bg-gradient-to-r from-amber-500/10 via-orange-500/10 to-amber-500/10 border border-amber-300 rounded-3xl flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-xs"
    >
      <div class="flex items-start sm:items-center gap-3.5">
        <div class="w-10 h-10 rounded-2xl bg-amber-500 text-white flex items-center justify-center shrink-0 shadow-md shadow-amber-200">
          <Sparkles class="w-5 h-5" />
        </div>
        <div>
          <h4 class="text-sm font-extrabold text-amber-950 flex items-center gap-2">
            <span>Phát hiện {{ totalExcessDuplicates }} từ vựng bị trùng lặp trong bộ này</span>
            <span class="px-2 py-0.5 bg-amber-200/80 text-amber-900 text-[10px] font-black rounded-full uppercase tracking-wider">Cần dọn dẹp</span>
          </h4>
          <p class="text-xs text-amber-800/90 mt-0.5">
            Hệ thống sẽ tự động gộp các từ cùng từ vựng & nghĩa thành 1 thẻ duy nhất, giữ lại nghĩa đầy đủ nhất và toàn bộ tiến độ học tập (đã thuộc, gắn sao).
          </p>
        </div>
      </div>
      <button
        @click="$emit('deduplicate')"
        class="px-4 py-2.5 bg-gradient-to-r from-amber-600 to-orange-600 hover:from-amber-700 hover:to-orange-700 text-white text-xs font-bold rounded-xl transition shadow-md shadow-amber-200 whitespace-nowrap self-start sm:self-center flex items-center gap-2 cursor-pointer"
        title="Gộp và xóa tất cả các bản ghi từ vựng bị lặp lại"
      >
        <Sparkles class="w-4 h-4" />
        <span>Gộp & Xóa từ trùng ngay</span>
      </button>
    </div>

    <!-- Toolbar: Search, Filters, Actions -->
    <div class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs mb-6 space-y-3">
      <div class="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
        
        <div class="relative grow max-w-md">
          <Search class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Tìm kiếm theo từ vựng, phiên âm, nghĩa tiếng Việt..."
            class="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white transition"
          />
        </div>

        <div class="flex items-center gap-2">
          <select
            v-model="sortBy"
            class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs font-semibold text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="default">Thứ tự mặc định</option>
            <option value="alpha_asc">Từ A - Z</option>
            <option value="alpha_desc">Từ Z - A</option>
            <option value="mastery">Mức độ thuộc</option>
          </select>

          <button
            @click="exportToText"
            class="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
            title="Xuất danh sách từ vựng thành file văn bản"
          >
            <Download class="w-3.5 h-3.5" />
            <span class="hidden sm:inline">Xuất file</span>
          </button>

          <button
            @click="$emit('open-add-word-modal')"
            class="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
            title="Thêm 1 từ vựng mới"
          >
            <Plus class="w-3.5 h-3.5" />
            <span>Thêm từ</span>
          </button>

          <button
            @click="$emit('open-import-modal')"
            class="px-3.5 py-2 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white rounded-xl text-xs font-bold transition flex items-center gap-1.5 shadow-xs shadow-indigo-100"
            title="Thêm nhiều từ cùng lúc (nhập hàng loạt hoặc điền theo bảng)"
          >
            <Layers class="w-3.5 h-3.5" />
            <span>Thêm nhiều từ</span>
          </button>
        </div>

      </div>

      <!-- Filter chips -->
      <div class="flex items-center gap-1.5 flex-wrap pt-2 border-t border-slate-100">
        <button
          v-for="f in filterOptions"
          :key="f.id"
          @click="activeFilter = f.id"
          :class="[
            'px-3 py-1 rounded-lg text-xs font-semibold transition',
            activeFilter === f.id
              ? 'bg-indigo-600 text-white shadow-xs'
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
          ]"
        >
          {{ f.name }} ({{ f.count }})
        </button>
      </div>
    </div>

    <!-- Words List Table / Cards -->
    <div v-if="filteredWords.length === 0" class="text-center py-16 bg-white rounded-2xl border border-slate-200">
      <p class="text-slate-500 text-sm">Không tìm thấy từ vựng nào phù hợp với bộ lọc.</p>
    </div>

    <div v-else class="space-y-2.5">
      <div
        v-for="word in filteredWords"
        :key="word.id"
        class="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs hover:border-indigo-300 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3 group"
      >
        <div class="flex items-start sm:items-center gap-3.5 grow min-w-0">
          <button
            @click="speakText(word.word)"
            class="p-2.5 text-slate-400 group-hover:text-indigo-600 hover:bg-indigo-50 rounded-xl transition shrink-0"
            title="Nghe phát âm"
          >
            <Volume2 class="w-5 h-5" />
          </button>

          <div class="grow min-w-0">
            <div class="flex items-center gap-2 flex-wrap">
              <span class="text-base sm:text-lg font-bold text-slate-800 tracking-tight">{{ word.word }}</span>
              <span v-if="word.ipa" class="text-xs sm:text-sm font-mono text-indigo-600 font-medium bg-indigo-50 px-2 py-0.5 rounded">
                {{ word.ipa }}
              </span>
              <span v-if="word.partOfSpeech" class="text-xs font-semibold text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                {{ word.partOfSpeech }}
              </span>
              <span v-if="duplicateWordKeys.has((word.word || '').trim().toLowerCase())" class="text-[10px] font-bold text-amber-800 bg-amber-100 border border-amber-200 px-2 py-0.5 rounded-full">
                Trùng lặp
              </span>
            </div>

            <p class="text-sm font-semibold text-slate-700 mt-1 line-clamp-2 leading-relaxed">
              {{ formatMeaning(word.meaning, word.partOfSpeech) }}
            </p>

            <div v-if="word.exampleSentence" class="text-xs text-slate-400 mt-1 italic line-clamp-1">
              "{{ word.exampleSentence }}"
            </div>
          </div>
        </div>

        <div class="flex items-center gap-2 self-end sm:self-center shrink-0">
          <button
            @click="cycleMastery(word)"
            :class="[
              'px-2.5 py-1 text-xs font-bold rounded-lg transition border',
              word.masteryLevel === 2
                ? 'bg-emerald-50 text-emerald-700 border-emerald-200 hover:bg-emerald-100'
                : word.masteryLevel === 1
                  ? 'bg-amber-50 text-amber-700 border-amber-200 hover:bg-amber-100'
                  : 'bg-slate-50 text-slate-500 border-slate-200 hover:bg-slate-100'
            ]"
            title="Nhấp để đổi trạng thái thuộc: Chưa học -> Đang học -> Đã thuộc"
          >
            {{ word.masteryLevel === 2 ? 'Đã thuộc' : (word.masteryLevel === 1 ? 'Đang học' : 'Chưa học') }}
          </button>

          <button
            @click="$emit('toggle-star', word.id)"
            class="p-2 rounded-lg hover:bg-amber-50 transition"
            :title="word.isStarred ? 'Bỏ gắn sao' : 'Gắn sao từ này'"
          >
            <Star :class="['w-4 h-4', word.isStarred ? 'fill-amber-400 text-amber-400' : 'text-slate-300 hover:text-amber-400']" />
          </button>

          <button
            @click="$emit('edit-word', word)"
            class="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition"
            title="Sửa từ vựng"
          >
            <Edit3 class="w-4 h-4" />
          </button>

          <button
            @click="$emit('delete-word', word.id)"
            class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa từ vựng"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { 
  Search, 
  Plus, 
  Download, 
  Volume2, 
  Star, 
  Edit3, 
  Trash2,
  Layers,
  Sparkles
} from 'lucide-vue-next';
import { speakText } from '@/utils/speech';

const props = defineProps({
  words: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits([
  'toggle-star',
  'update-mastery',
  'edit-word',
  'delete-word',
  'open-add-word-modal',
  'open-import-modal',
  'deduplicate'
]);

const searchQuery = ref('');
const activeFilter = ref('all');
const sortBy = ref('default');

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

const duplicateWordKeys = computed(() => {
  const map = new Map();
  for (const w of props.words) {
    const key = (w.word || '').trim().toLowerCase();
    if (!key) continue;
    map.set(key, (map.get(key) || 0) + 1);
  }
  const set = new Set();
  for (const [key, count] of map.entries()) {
    if (count > 1) set.add(key);
  }
  return set;
});

const totalExcessDuplicates = computed(() => {
  const map = new Map();
  for (const w of props.words) {
    const key = (w.word || '').trim().toLowerCase();
    if (!key) continue;
    map.set(key, (map.get(key) || 0) + 1);
  }
  let excess = 0;
  for (const count of map.values()) {
    if (count > 1) excess += (count - 1);
  }
  return excess;
});

const stats = computed(() => {
  let mastered = 0;
  let learning = 0;
  let starred = 0;
  props.words.forEach(w => {
    if (w.isStarred) starred++;
    if (w.masteryLevel === 2) mastered++;
    else if (w.masteryLevel === 1) learning++;
  });
  return {
    mastered,
    learning,
    starred,
    unlearned: props.words.length - mastered - learning
  };
});

const filterOptions = computed(() => {
  const base = [
    { id: 'all', name: 'Tất cả', count: props.words.length },
    { id: 'starred', name: 'Gắn sao ⭐', count: stats.value.starred },
    { id: 'mastered', name: 'Đã thuộc 🟢', count: stats.value.mastered },
    { id: 'learning', name: 'Đang học 🟡', count: stats.value.learning },
    { id: 'new', name: 'Chưa học ⚪', count: stats.value.unlearned }
  ];
  if (totalExcessDuplicates.value > 0) {
    base.push({
      id: 'duplicates',
      name: `Từ bị trùng (${totalExcessDuplicates.value}) ⚠️`,
      count: totalExcessDuplicates.value
    });
  }
  return base;
});

const filteredWords = computed(() => {
  let list = [...props.words];

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase();
    list = list.filter(w =>
      (w.word && w.word.toLowerCase().includes(q)) ||
      (w.meaning && w.meaning.toLowerCase().includes(q)) ||
      (w.ipa && w.ipa.toLowerCase().includes(q))
    );
  }

  if (activeFilter.value === 'starred') {
    list = list.filter(w => w.isStarred);
  } else if (activeFilter.value === 'mastered') {
    list = list.filter(w => w.masteryLevel === 2);
  } else if (activeFilter.value === 'learning') {
    list = list.filter(w => w.masteryLevel === 1);
  } else if (activeFilter.value === 'new') {
    list = list.filter(w => !w.masteryLevel || w.masteryLevel === 0);
  } else if (activeFilter.value === 'duplicates') {
    list = list.filter(w => duplicateWordKeys.value.has((w.word || '').trim().toLowerCase()));
  }

  if (sortBy.value === 'alpha_asc') {
    list.sort((a, b) => a.word.localeCompare(b.word));
  } else if (sortBy.value === 'alpha_desc') {
    list.sort((a, b) => b.word.localeCompare(a.word));
  } else if (sortBy.value === 'mastery') {
    list.sort((a, b) => (b.masteryLevel || 0) - (a.masteryLevel || 0));
  }

  return list;
});

function cycleMastery(word) {
  const current = word.masteryLevel || 0;
  const next = (current + 1) % 3;
  emit('update-mastery', { wordId: word.id, masteryLevel: next });
}

function exportToText() {
  const lines = props.words.map(w => `${w.word}\t${w.ipa || ''}\t${w.meaning || ''}`).join('\n');
  const blob = new Blob([lines], { type: 'text/plain;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `vocab_export_${new Date().toISOString().slice(0, 10)}.txt`;
  a.click();
  URL.revokeObjectURL(url);
}
</script>
