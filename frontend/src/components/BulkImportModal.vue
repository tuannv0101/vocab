<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-900/60 backdrop-blur-xs">
    <div class="bg-white rounded-3xl max-w-4xl w-full p-5 sm:p-7 shadow-2xl border border-slate-100 max-h-[94vh] flex flex-col">
      
      <!-- Modal Header -->
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 shrink-0">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-2xl bg-gradient-to-tr from-indigo-600 to-violet-600 text-white flex items-center justify-center shadow-md shadow-indigo-100">
            <Layers class="w-5 h-5" />
          </div>
          <div>
            <h3 class="text-xl font-black text-slate-800">Thêm từ vựng đồng loạt</h3>
            <p class="text-xs text-slate-500">Tạo nhiều thẻ cùng lúc hoặc dán danh sách từ vựng chuẩn Quizlet</p>
          </div>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Scrollable Modal Body -->
      <div class="overflow-y-auto grow py-4 space-y-4 pr-1">
        
        <!-- Target Destination Selector -->
        <div class="bg-slate-50 p-3.5 rounded-2xl border border-slate-200/80 space-y-3">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
            <label
              :class="[
                'p-3 rounded-xl border text-xs font-semibold cursor-pointer transition flex items-center gap-3',
                destinationType === 'existing'
                  ? 'bg-indigo-50/80 border-indigo-400 text-indigo-900 ring-2 ring-indigo-100'
                  : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-100/60'
              ]"
            >
              <input type="radio" v-model="destinationType" value="existing" class="sr-only" />
              <div class="w-4 h-4 rounded-full border-2 flex items-center justify-center" :class="destinationType === 'existing' ? 'border-indigo-600 bg-indigo-600' : 'border-slate-300'">
                <div v-if="destinationType === 'existing'" class="w-1.5 h-1.5 bg-white rounded-full"></div>
              </div>
              <div>
                <span class="block font-bold">Thêm vào bộ hiện có</span>
                <span class="text-[11px] text-slate-500 font-normal">Lưu từ vào bộ từ bạn đang học</span>
              </div>
            </label>

            <label
              :class="[
                'p-3 rounded-xl border text-xs font-semibold cursor-pointer transition flex items-center gap-3',
                destinationType === 'new'
                  ? 'bg-indigo-50/80 border-indigo-400 text-indigo-900 ring-2 ring-indigo-100'
                  : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-100/60'
              ]"
            >
              <input type="radio" v-model="destinationType" value="new" class="sr-only" />
              <div class="w-4 h-4 rounded-full border-2 flex items-center justify-center" :class="destinationType === 'new' ? 'border-indigo-600 bg-indigo-600' : 'border-slate-300'">
                <div v-if="destinationType === 'new'" class="w-1.5 h-1.5 bg-white rounded-full"></div>
              </div>
              <div>
                <span class="block font-bold">Tạo bộ từ vựng mới</span>
                <span class="text-[11px] text-slate-500 font-normal">Tạo bộ mới như Day 2, Chuyên ngành...</span>
              </div>
            </label>
          </div>

          <!-- Existing set picker -->
          <div v-if="destinationType === 'existing'" class="pt-1">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Chọn bộ từ vựng đích</label>
            <select
              v-model="targetSetId"
              class="w-full px-3.5 py-2 bg-white border border-slate-200 rounded-xl text-sm font-semibold text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option v-for="s in sets" :key="s.id" :value="s.id">📁 {{ s.title }} ({{ s.totalWords }} từ)</option>
            </select>
          </div>

          <!-- New set inputs -->
          <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Tên bộ từ mới *</label>
              <input
                type="text"
                v-model="newSetName"
                placeholder="Ví dụ: Từ vựng Day 2 - Kinh tế & Đàm phán"
                class="w-full px-3.5 py-2 bg-white border border-slate-200 rounded-xl text-sm font-semibold text-slate-800 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">Mô tả (tùy chọn)</label>
              <input
                type="text"
                v-model="newSetDesc"
                placeholder="Ví dụ: 30 từ vựng luyện thi..."
                class="w-full px-3.5 py-2 bg-white border border-slate-200 rounded-xl text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>
        </div>

        <!-- Mode Tabs: Grid vs Paste -->
        <div class="flex items-center justify-between border-b border-slate-200 pt-2 pb-1">
          <div class="flex items-center gap-2">
            <button
              type="button"
              @click="activeMode = 'grid'"
              :class="[
                'flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition',
                activeMode === 'grid'
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              ]"
            >
              <Table class="w-4 h-4" />
              <span>Bảng nhập liệu nhiều dòng</span>
              <span class="px-1.5 py-0.2 bg-white/20 rounded-full text-[10px]">{{ validGridCount }}</span>
            </button>

            <button
              type="button"
              @click="activeMode = 'paste'"
              :class="[
                'flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition',
                activeMode === 'paste'
                  ? 'bg-indigo-600 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              ]"
            >
              <FileText class="w-4 h-4" />
              <span>Dán văn bản / Excel</span>
              <span v-if="parsedPreview.length > 0" class="px-1.5 py-0.2 bg-indigo-100 text-indigo-700 rounded-full text-[10px]">
                {{ parsedPreview.length }}
              </span>
            </button>
          </div>

          <span class="text-xs text-slate-400 font-medium hidden sm:inline">
            {{ activeMode === 'grid' ? 'Điền trực tiếp từng ô và nhấn Tab để sang ô tiếp theo' : 'Dán nhanh hàng loạt từ bảng Excel hoặc văn bản' }}
          </span>
        </div>

        <!-- MODE 1: INTERACTIVE GRID TABLE -->
        <div v-show="activeMode === 'grid'" class="space-y-3">
          
          <!-- Table container -->
          <div class="border border-slate-200 rounded-2xl overflow-hidden bg-white shadow-xs">
            <div class="overflow-x-auto max-h-72">
              <table class="w-full text-left text-xs border-collapse">
                <thead class="bg-slate-50 text-slate-500 uppercase tracking-wider font-bold border-b border-slate-200 sticky top-0 z-10">
                  <tr>
                    <th class="py-2.5 px-3 w-10 text-center">#</th>
                    <th class="py-2.5 px-3 min-w-[150px]">Từ vựng (tiếng Anh) *</th>
                    <th class="py-2.5 px-3 min-w-[130px]">Phiên âm (IPA)</th>
                    <th class="py-2.5 px-3 w-28">Từ loại</th>
                    <th class="py-2.5 px-3 min-w-[200px]">Nghĩa tiếng Việt *</th>
                    <th class="py-2.5 px-2 w-10 text-center">Xóa</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                  <tr
                    v-for="(row, idx) in gridRows"
                    :key="idx"
                    class="hover:bg-indigo-50/30 transition group"
                  >
                    <!-- STT -->
                    <td class="py-2 px-3 text-center font-mono text-slate-400 text-xs">
                      {{ idx + 1 }}
                    </td>

                    <!-- Word -->
                    <td class="py-1 px-2">
                      <input
                        type="text"
                        v-model="row.word"
                        placeholder="Ví dụ: accomplish"
                        class="w-full px-2.5 py-1.5 bg-slate-50/60 focus:bg-white border border-transparent focus:border-indigo-400 rounded-lg font-bold text-slate-800 text-xs focus:outline-none transition"
                        @paste="handleGridPaste($event, idx, 'word')"
                      />
                    </td>

                    <!-- IPA -->
                    <td class="py-1 px-2">
                      <input
                        type="text"
                        v-model="row.ipa"
                        placeholder="/əˈkɑːmplɪʃ/"
                        class="w-full px-2.5 py-1.5 bg-slate-50/60 focus:bg-white border border-transparent focus:border-indigo-400 rounded-lg font-mono text-indigo-700 text-xs focus:outline-none transition"
                        @paste="handleGridPaste($event, idx, 'ipa')"
                      />
                    </td>

                    <!-- Part of Speech -->
                    <td class="py-1 px-2">
                      <div class="relative">
                        <input
                          type="text"
                          v-model="row.partOfSpeech"
                          placeholder="(v), (n)..."
                          class="w-full px-2.5 py-1.5 bg-slate-50/60 focus:bg-white border border-transparent focus:border-indigo-400 rounded-lg text-slate-600 text-xs focus:outline-none transition"
                        />
                      </div>
                    </td>

                    <!-- Meaning -->
                    <td class="py-1 px-2">
                      <input
                        type="text"
                        v-model="row.meaning"
                        placeholder="hoàn thành, đạt được"
                        class="w-full px-2.5 py-1.5 bg-slate-50/60 focus:bg-white border border-transparent focus:border-indigo-400 rounded-lg font-medium text-slate-800 text-xs focus:outline-none transition"
                        @keydown.tab="handleLastCellTab(idx, $event)"
                        @paste="handleGridPaste($event, idx, 'meaning')"
                      />
                    </td>

                    <!-- Delete button -->
                    <td class="py-1 px-2 text-center">
                      <button
                        type="button"
                        @click="removeGridRow(idx)"
                        class="p-1.5 text-slate-300 hover:text-rose-500 hover:bg-rose-50 rounded-lg transition"
                        title="Xóa dòng này"
                      >
                        <Trash2 class="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Grid Action Toolbar -->
          <div class="flex items-center justify-between flex-wrap gap-2 pt-1">
            <div class="flex items-center gap-2">
              <button
                type="button"
                @click="addGridRows(1)"
                class="px-3 py-1.5 bg-slate-100 hover:bg-indigo-50 hover:text-indigo-600 text-slate-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
              >
                <Plus class="w-3.5 h-3.5" />
                <span>+ 1 dòng</span>
              </button>

              <button
                type="button"
                @click="addGridRows(5)"
                class="px-3 py-1.5 bg-slate-100 hover:bg-indigo-50 hover:text-indigo-600 text-slate-700 rounded-xl text-xs font-bold transition"
              >
                + 5 dòng
              </button>

              <button
                type="button"
                @click="addGridRows(10)"
                class="px-3 py-1.5 bg-slate-100 hover:bg-indigo-50 hover:text-indigo-600 text-slate-700 rounded-xl text-xs font-bold transition"
              >
                + 10 dòng
              </button>

              <button
                type="button"
                @click="clearEmptyGridRows"
                class="px-3 py-1.5 text-slate-500 hover:text-slate-700 text-xs font-medium transition"
                title="Xóa các dòng chưa nhập từ"
              >
                Dọn dòng trống
              </button>
            </div>

            <div class="flex items-center gap-1.5 text-xs text-slate-400">
              <span>Mẹo từ loại:</span>
              <button type="button" @click="insertPosToLast('(v)')" class="px-1.5 py-0.5 bg-slate-100 hover:bg-slate-200 rounded text-[11px] font-semibold text-slate-600">(v)</button>
              <button type="button" @click="insertPosToLast('(n)')" class="px-1.5 py-0.5 bg-slate-100 hover:bg-slate-200 rounded text-[11px] font-semibold text-slate-600">(n)</button>
              <button type="button" @click="insertPosToLast('(adj)')" class="px-1.5 py-0.5 bg-slate-100 hover:bg-slate-200 rounded text-[11px] font-semibold text-slate-600">(adj)</button>
              <button type="button" @click="insertPosToLast('(adv)')" class="px-1.5 py-0.5 bg-slate-100 hover:bg-slate-200 rounded text-[11px] font-semibold text-slate-600">(adv)</button>
              <button type="button" @click="insertPosToLast('(phr)')" class="px-1.5 py-0.5 bg-slate-100 hover:bg-slate-200 rounded text-[11px] font-semibold text-slate-600">(phr)</button>
            </div>
          </div>

        </div>

        <!-- MODE 2: PASTE RAW TEXT / EXCEL -->
        <div v-show="activeMode === 'paste'" class="space-y-3">
          
          <!-- Instructions Guide -->
          <div class="p-3 bg-indigo-50/60 border border-indigo-100 rounded-2xl text-xs text-indigo-900 space-y-1">
            <p class="font-bold flex items-center gap-1.5">
              <Sparkles class="w-3.5 h-3.5 text-indigo-600" />
              Định dạng được hỗ trợ tự động:
            </p>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-2 text-[11px] text-slate-600 font-mono mt-1">
              <div class="p-1.5 bg-white rounded-lg border border-indigo-100/50">
                <span class="text-indigo-600 font-bold block">1. Tab (Excel / Quizlet):</span>
                <span>từ &lt;tab&gt; /phiên âm/ &lt;tab&gt; nghĩa</span>
              </div>
              <div class="p-1.5 bg-white rounded-lg border border-indigo-100/50">
                <span class="text-indigo-600 font-bold block">2. Gạch nối:</span>
                <span>từ - nghĩa</span>
              </div>
              <div class="p-1.5 bg-white rounded-lg border border-indigo-100/50">
                <span class="text-indigo-600 font-bold block">3. Hai chấm:</span>
                <span>từ : nghĩa</span>
              </div>
            </div>
          </div>

          <!-- Textarea toolbar -->
          <div class="flex items-center justify-between">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
              Dán danh sách từ vựng (mỗi từ một dòng)
            </label>
            <div class="flex items-center gap-2">
              <button
                type="button"
                @click="pasteSample"
                class="text-xs text-indigo-600 hover:text-indigo-800 font-semibold flex items-center gap-1"
              >
                <Sparkles class="w-3 h-3" />
                <span>Chèn văn bản mẫu</span>
              </button>
              <button
                v-if="rawInput"
                type="button"
                @click="rawInput = ''"
                class="text-xs text-slate-400 hover:text-rose-600 font-semibold"
              >
                Xóa hết
              </button>
            </div>
          </div>

          <!-- Textarea -->
          <textarea
            v-model="rawInput"
            rows="6"
            placeholder="paint&#9;/peɪnt/&#9;(v) sơn, vẽ&#10;park&#9;/pɑːrk/&#9;(v) đỗ xe; (n) công viên&#10;leave - left - left&#9;/liːv/ - /left/ - /left/&#9;(v) rời đi; để lại, bỏ lại&#10;briefcase&#9;/ˈbriːfkeɪs/&#9;(n) cặp tài liệu"
            class="w-full p-3 font-mono text-xs bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none leading-relaxed transition"
          ></textarea>

          <!-- Transfer to Grid button -->
          <div v-if="parsedPreview.length > 0" class="flex items-center justify-between pt-1">
            <span class="text-xs font-bold text-slate-700">
              Đã nhận diện: <span class="text-indigo-600 font-black">{{ parsedPreview.length }}</span> từ hợp lệ
            </span>
            <button
              type="button"
              @click="transferParsedToGrid"
              class="px-3.5 py-1.5 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 rounded-xl text-xs font-bold transition flex items-center gap-1.5"
            >
              <Table class="w-3.5 h-3.5" />
              <span>Chuyển sang Bảng để chỉnh sửa chi tiết →</span>
            </button>
          </div>

          <!-- Preview recognized list -->
          <div v-if="parsedPreview.length > 0" class="border border-slate-200 rounded-2xl bg-white overflow-hidden max-h-48 overflow-y-auto">
            <table class="w-full text-left text-xs">
              <thead class="bg-slate-50 text-slate-500 font-bold border-b border-slate-200 sticky top-0">
                <tr>
                  <th class="py-2 px-3 w-8">#</th>
                  <th class="py-2 px-3">Từ vựng</th>
                  <th class="py-2 px-3">Phiên âm</th>
                  <th class="py-2 px-3">Từ loại</th>
                  <th class="py-2 px-3">Nghĩa tiếng Việt</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr v-for="(w, idx) in parsedPreview" :key="idx" class="hover:bg-slate-50">
                  <td class="py-1.5 px-3 font-mono text-slate-400 text-center">{{ idx + 1 }}</td>
                  <td class="py-1.5 px-3 font-bold text-slate-800">{{ w.word }}</td>
                  <td class="py-1.5 px-3 font-mono text-indigo-600">{{ w.ipa }}</td>
                  <td class="py-1.5 px-3 text-slate-500">{{ w.partOfSpeech }}</td>
                  <td class="py-1.5 px-3 text-slate-700">{{ w.meaning }}</td>
                </tr>
              </tbody>
            </table>
          </div>

        </div>

      </div>

      <!-- Modal Footer -->
      <div class="flex items-center justify-between pt-4 border-t border-slate-100 shrink-0">
        <div class="flex items-center gap-2">
          <span class="text-xs font-medium text-slate-500">
            Sẵn sàng thêm:
            <strong class="text-indigo-600 font-bold text-sm ml-1">
              {{ totalValidCount }}
            </strong> từ vựng
          </span>
        </div>

        <div class="flex items-center gap-3">
          <button
            type="button"
            @click="$emit('close')"
            class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition"
          >
            Hủy
          </button>
          <button
            type="button"
            @click="handleSubmit"
            :disabled="totalValidCount === 0 || isSaving"
            class="px-5 py-2.5 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 disabled:opacity-50 disabled:cursor-not-allowed text-white text-sm font-bold rounded-xl shadow-md shadow-indigo-100 transition inline-flex items-center gap-2"
          >
            <span v-if="isSaving" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            <span>{{ isSaving ? 'Đang lưu...' : `Thêm tất cả (${totalValidCount} từ)` }}</span>
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';
import { Layers, X, Plus, Trash2, Table, FileText, Sparkles } from 'lucide-vue-next';

const props = defineProps({
  isOpen: Boolean,
  sets: Array,
  currentSetId: Number
});

const emit = defineEmits(['close', 'imported']);

const destinationType = ref('existing');
const targetSetId = ref(props.currentSetId);
const newSetName = ref('');
const newSetDesc = ref('');
const activeMode = ref('grid'); // 'grid' or 'paste'
const rawInput = ref('');
const isSaving = ref(false);

// Interactive Table Rows
const gridRows = ref([
  { word: '', ipa: '', partOfSpeech: '', meaning: '' },
  { word: '', ipa: '', partOfSpeech: '', meaning: '' },
  { word: '', ipa: '', partOfSpeech: '', meaning: '' },
  { word: '', ipa: '', partOfSpeech: '', meaning: '' },
  { word: '', ipa: '', partOfSpeech: '', meaning: '' }
]);

watch(() => props.currentSetId, (val) => {
  if (val) targetSetId.value = val;
});

watch(() => props.isOpen, (open) => {
  if (open) {
    if (props.currentSetId) targetSetId.value = props.currentSetId;
    // ensure at least 5 rows in grid if empty
    if (gridRows.value.length === 0) {
      addGridRows(5);
    }
  }
});

function addGridRows(count = 1) {
  for (let i = 0; i < count; i++) {
    gridRows.value.push({ word: '', ipa: '', partOfSpeech: '', meaning: '' });
  }
}

function removeGridRow(idx) {
  if (gridRows.value.length <= 1) {
    gridRows.value = [{ word: '', ipa: '', partOfSpeech: '', meaning: '' }];
  } else {
    gridRows.value.splice(idx, 1);
  }
}

function clearEmptyGridRows() {
  const filtered = gridRows.value.filter(r => r.word.trim() || r.meaning.trim());
  if (filtered.length === 0) {
    gridRows.value = [{ word: '', ipa: '', partOfSpeech: '', meaning: '' }];
  } else {
    gridRows.value = filtered;
  }
}

function handleLastCellTab(idx, event) {
  if (idx === gridRows.value.length - 1) {
    // on last row meaning input, pressing Tab creates another row
    addGridRows(1);
  }
}

function insertPosToLast(pos) {
  for (let i = gridRows.value.length - 1; i >= 0; i--) {
    if (gridRows.value[i].word.trim() || i === gridRows.value.length - 1) {
      gridRows.value[i].partOfSpeech = pos;
      break;
    }
  }
}

// Support pasting TSV into grid
function handleGridPaste(event, startIdx, field) {
  const clipboardData = event.clipboardData || window.clipboardData;
  if (!clipboardData) return;
  const pastedText = clipboardData.getData('text');
  if (!pastedText.includes('\n') && !pastedText.includes('\t')) {
    // Single cell normal paste
    return;
  }

  event.preventDefault();
  const parsed = parseTextToWords(pastedText);
  if (parsed.length === 0) return;

  // Insert parsed rows into grid starting at startIdx
  for (let i = 0; i < parsed.length; i++) {
    const targetIdx = startIdx + i;
    if (targetIdx < gridRows.value.length) {
      gridRows.value[targetIdx] = { ...parsed[i] };
    } else {
      gridRows.value.push({ ...parsed[i] });
    }
  }
}

// Text parsing logic
function parseTextToWords(text) {
  if (!text || !text.trim()) return [];
  const lines = text.split(/\r?\n/);
  const result = [];

  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#') || trimmed.startsWith('//')) continue;

    let word = '';
    let ipa = '';
    let partOfSpeech = '';
    let meaning = '';

    if (trimmed.includes('\t')) {
      const parts = trimmed.split(/\t+/);
      if (parts.length >= 3) {
        word = parts[0].trim();
        ipa = parts[1].trim();
        meaning = parts.slice(2).join(' ').trim();
      } else if (parts.length === 2) {
        word = parts[0].trim();
        meaning = parts[1].trim();
      }
    } else {
      const firstSlash = trimmed.indexOf('/');
      const lastSlash = trimmed.lastIndexOf('/');
      if (firstSlash !== -1 && lastSlash > firstSlash) {
        word = trimmed.substring(0, firstSlash).trim();
        ipa = trimmed.substring(firstSlash, lastSlash + 1).trim();
        meaning = trimmed.substring(lastSlash + 1).trim();
      } else if (trimmed.includes(' - ')) {
        const parts = trimmed.split(' - ');
        word = parts[0].trim();
        meaning = parts.slice(1).join(' - ').trim();
      } else if (trimmed.includes(':')) {
        const parts = trimmed.split(':');
        word = parts[0].trim();
        meaning = parts.slice(1).join(':').trim();
      } else {
        word = trimmed;
      }
    }

    // Extract part of speech from meaning if formatted like (v), (n), (adj) or (n); (v)
    if (meaning.startsWith('(')) {
      const semiMatch = meaning.match(/^\(([a-zA-Z /,-]+)\)\s*;\s*\(([a-zA-Z /,-]+)\)\s*(.*)$/);
      if (semiMatch) {
        partOfSpeech = `(${semiMatch[1].trim()}, ${semiMatch[2].trim()})`;
        meaning = semiMatch[3].trim();
      } else {
        const match = meaning.match(/^\(([a-zA-Z /,-]+)\)\s*(.*)$/);
        if (match) {
          partOfSpeech = `(${match[1].trim()})`;
          meaning = match[2].trim();
        }
      }
    }

    if (word) {
      result.push({ word, ipa, partOfSpeech, meaning });
    }
  }

  // Deduplicate within the parsed text list
  const seen = new Map();
  for (const item of result) {
    const key = item.word.trim().toLowerCase();
    if (seen.has(key)) {
      const prev = seen.get(key);
      if (!prev.ipa && item.ipa) prev.ipa = item.ipa;
      if (!prev.partOfSpeech && item.partOfSpeech) prev.partOfSpeech = item.partOfSpeech;
      if (item.meaning && (!prev.meaning || item.meaning.length > prev.meaning.length)) {
        prev.meaning = item.meaning;
      }
    } else {
      seen.set(key, item);
    }
  }
  return Array.from(seen.values());
}

const parsedPreview = computed(() => {
  return parseTextToWords(rawInput.value);
});

const validGridCount = computed(() => {
  return gridRows.value.filter(r => r.word && r.word.trim()).length;
});

const totalValidCount = computed(() => {
  if (activeMode.value === 'grid') {
    return validGridCount.value;
  } else {
    return parsedPreview.value.length;
  }
});

function pasteSample() {
  rawInput.value = `accomplish\t/əˈkɑːmplɪʃ/\t(v) hoàn thành, đạt được\nnegotiation\t/nɪˌɡoʊʃiˈeɪʃn/\t(n) cuộc đàm phán\nefficient\t/ɪˈfɪʃnt/\t(adj) có hiệu quả, năng suất\ncollaborate with\t/kəˈlæbəreɪt/\t(v) cộng tác với\ninnovative\t/ˈɪnəveɪtɪv/\t(adj) mang tính đổi mới, sáng tạo`;
}

function transferParsedToGrid() {
  if (parsedPreview.value.length === 0) return;
  gridRows.value = parsedPreview.value.map(w => ({ ...w }));
  activeMode.value = 'grid';
}

async function handleSubmit() {
  let wordsToSave = [];
  if (activeMode.value === 'grid') {
    const map = new Map();
    for (const r of gridRows.value) {
      if (!r.word || !r.word.trim()) continue;
      const key = r.word.trim().toLowerCase();
      if (map.has(key)) {
        const prev = map.get(key);
        if (!prev.ipa && r.ipa) prev.ipa = r.ipa.trim();
        if (!prev.partOfSpeech && r.partOfSpeech) prev.partOfSpeech = r.partOfSpeech.trim();
        if (r.meaning && (!prev.meaning || r.meaning.trim().length > prev.meaning.length)) {
          prev.meaning = r.meaning.trim();
        }
      } else {
        map.set(key, {
          word: r.word.trim(),
          ipa: r.ipa ? r.ipa.trim() : '',
          partOfSpeech: r.partOfSpeech ? r.partOfSpeech.trim() : '',
          meaning: r.meaning ? r.meaning.trim() : ''
        });
      }
    }
    wordsToSave = Array.from(map.values());
  } else {
    wordsToSave = parsedPreview.value;
  }

  if (wordsToSave.length === 0) return;

  isSaving.value = true;
  try {
    const payload = {
      words: wordsToSave
    };

    if (destinationType.value === 'existing') {
      payload.setId = targetSetId.value;
    } else {
      payload.setName = newSetName.value.trim() || `Bộ từ mới ${new Date().toISOString().slice(0, 10)}`;
      payload.setDescription = newSetDesc.value.trim();
    }

    emit('imported', payload);
  } finally {
    isSaving.value = false;
  }
}
</script>
