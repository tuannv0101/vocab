<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
    <div class="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl border border-slate-100 max-h-[90vh] overflow-y-auto">
      
      <!-- Modal Header -->
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-4">
        <div>
          <h3 class="text-xl font-extrabold text-slate-800">
            {{ isEditing ? 'Chỉnh sửa từ vựng' : 'Thêm từ vựng mới' }}
          </h3>
          <p class="text-xs text-slate-500 mt-0.5">Lưu trữ từ vựng vào kho học tập hàng ngày</p>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Quick switch to Bulk Mode if adding new word -->
      <div v-if="!isEditing" class="mb-5 p-3 bg-indigo-50/70 border border-indigo-100 rounded-2xl flex items-center justify-between gap-3 text-xs">
        <div class="flex items-center gap-2 text-indigo-900 font-medium">
          <Layers class="w-4 h-4 text-indigo-600 shrink-0" />
          <span>Muốn thêm nhiều từ vựng cùng một lúc?</span>
        </div>
        <button
          type="button"
          @click="$emit('switch-to-bulk')"
          class="px-2.5 py-1 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-lg transition whitespace-nowrap"
        >
          Thêm đồng loạt →
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-4">
        
        <!-- Target Set -->
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Bộ từ vựng</label>
          <select
            v-model="form.setId"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:ring-2 focus:ring-indigo-500 focus:bg-white"
          >
            <option v-for="s in sets" :key="s.id" :value="s.id">{{ s.title }}</option>
          </select>
        </div>

        <!-- English Word -->
        <div>
          <div class="flex items-center justify-between mb-1.5">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">Từ vựng tiếng Anh *</label>
            <button
              v-if="form.word"
              type="button"
              @click="speakText(form.word)"
              class="text-xs text-indigo-600 hover:text-indigo-800 flex items-center gap-1 font-semibold"
            >
              <Volume2 class="w-3.5 h-3.5" /> Nghe thử
            </button>
          </div>
          <input
            type="text"
            v-model="form.word"
            required
            placeholder="Ví dụ: leave - left - left hoặc accomplish"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-base font-bold text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white"
          />
        </div>

        <!-- IPA & Part of Speech (grid) -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Phiên âm (IPA)</label>
            <input
              type="text"
              v-model="form.ipa"
              placeholder="Ví dụ: /liːv/ - /left/ - /left/"
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm font-mono text-indigo-700 focus:ring-2 focus:ring-indigo-500 focus:bg-white"
            />
          </div>

          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Từ loại</label>
            <input
              type="text"
              v-model="form.partOfSpeech"
              placeholder="Ví dụ: (v), (n), (adj), (phr)"
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:ring-2 focus:ring-indigo-500 focus:bg-white"
            />
          </div>
        </div>

        <!-- Vietnamese Meaning -->
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Nghĩa tiếng Việt *</label>
          <input
            type="text"
            v-model="form.meaning"
            required
            placeholder="Ví dụ: rời đi; để lại, bỏ lại"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm font-semibold text-slate-800 focus:ring-2 focus:ring-indigo-500 focus:bg-white"
          />
        </div>

        <!-- Example Sentence (Optional) -->
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Câu ví dụ (Tùy chọn)</label>
          <input
            type="text"
            v-model="form.exampleSentence"
            placeholder="Ví dụ: She left her briefcase in the meeting room."
            class="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-indigo-500 focus:bg-white"
          />
        </div>

        <!-- Example Translation (Optional) -->
        <div>
          <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Dịch câu ví dụ (Tùy chọn)</label>
          <input
            type="text"
            v-model="form.exampleTranslation"
            placeholder="Ví dụ: Cô ấy để quên cặp tài liệu trong phòng họp."
            class="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-indigo-500 focus:bg-white"
          />
        </div>

        <!-- Actions -->
        <div class="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
          <button
            type="button"
            @click="$emit('close')"
            class="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition"
          >
            Hủy
          </button>
          <button
            type="submit"
            class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold rounded-xl shadow-md shadow-indigo-100 transition"
          >
            {{ isEditing ? 'Lưu thay đổi' : 'Thêm vào kho' }}
          </button>
        </div>

      </form>

    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue';
import { X, Volume2, Layers } from 'lucide-vue-next';
import { speakText } from '@/utils/speech';

const props = defineProps({
  isOpen: Boolean,
  isEditing: Boolean,
  editData: Object,
  sets: Array,
  currentSetId: Number
});

const emit = defineEmits(['close', 'save', 'switch-to-bulk']);

const form = reactive({
  id: null,
  setId: null,
  word: '',
  ipa: '',
  partOfSpeech: '',
  meaning: '',
  exampleSentence: '',
  exampleTranslation: ''
});

watch(() => props.isOpen, (open) => {
  if (open) {
    if (props.isEditing && props.editData) {
      form.id = props.editData.id;
      form.setId = props.editData.setId || props.currentSetId;
      form.word = props.editData.word || '';
      form.ipa = props.editData.ipa || '';
      form.partOfSpeech = props.editData.partOfSpeech || '';
      form.meaning = props.editData.meaning || '';
      form.exampleSentence = props.editData.exampleSentence || '';
      form.exampleTranslation = props.editData.exampleTranslation || '';
    } else {
      form.id = null;
      form.setId = props.currentSetId;
      form.word = '';
      form.ipa = '';
      form.partOfSpeech = '';
      form.meaning = '';
      form.exampleSentence = '';
      form.exampleTranslation = '';
    }
  }
});

function handleSubmit() {
  let meaning = (form.meaning || '').trim();
  let pos = (form.partOfSpeech || '').trim();

  const match = meaning.match(/^\s*\(([a-zA-Z /,-]+)\)\s*(.*)$/);
  if (match) {
    if (!pos) {
      pos = `(${match[1].toLowerCase().trim()})`;
    }
    meaning = match[2].trim();
  }

  emit('save', {
    ...form,
    partOfSpeech: pos,
    meaning
  });
}
</script>
