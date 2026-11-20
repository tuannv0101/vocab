<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
    <div class="bg-white rounded-3xl max-w-xl w-full p-6 sm:p-8 shadow-2xl border border-slate-100 max-h-[90vh] flex flex-col">
      
      <!-- Modal Header -->
      <div class="flex items-center justify-between pb-4 border-b border-slate-100 shrink-0">
        <div>
          <h3 class="text-xl font-extrabold text-slate-800">Quản lý các bộ từ vựng</h3>
          <p class="text-xs text-slate-500">Tổ chức từ vựng theo ngày học (Day 1, Day 2,...) hoặc chủ đề</p>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Scrollable Body -->
      <div class="overflow-y-auto grow py-4 space-y-5">
        
        <!-- Create new set section -->
        <div class="p-4 bg-slate-50 rounded-2xl border border-slate-200">
          <span class="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-3">Tạo bộ từ vựng mới</span>
          <div class="space-y-3">
            <input
              type="text"
              v-model="newTitle"
              placeholder="Tên bộ từ (Ví dụ: Từ vựng Day 2 - TOEIC)"
              class="w-full px-3.5 py-2 bg-white border border-slate-200 rounded-xl text-sm font-semibold text-slate-800 focus:ring-2 focus:ring-indigo-500"
            />
            <input
              type="text"
              v-model="newDesc"
              placeholder="Mô tả ngắn về chủ đề (Tùy chọn)"
              class="w-full px-3.5 py-2 bg-white border border-slate-200 rounded-xl text-xs text-slate-600 focus:ring-2 focus:ring-indigo-500"
            />
            <div class="text-right">
              <button
                @click="handleCreateSet"
                :disabled="!newTitle.trim()"
                class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-slate-300 text-white rounded-xl text-xs font-bold transition shadow-xs"
              >
                Tạo bộ từ
              </button>
            </div>
          </div>
        </div>

        <!-- Existing sets list -->
        <div>
          <span class="text-xs font-bold text-slate-700 uppercase tracking-wider block mb-2">Các bộ từ hiện có ({{ sets.length }})</span>
          <div class="space-y-2.5">
            <div
              v-for="s in sets"
              :key="s.id"
              :class="[
                'p-4 rounded-2xl border transition-all flex items-center justify-between gap-3',
                s.id === currentSetId ? 'bg-indigo-50/70 border-indigo-300 ring-2 ring-indigo-100' : 'bg-white border-slate-200 hover:border-slate-300'
              ]"
            >
              <div class="grow min-w-0 cursor-pointer" @click="$emit('select-set', s.id); $emit('close')">
                <div class="flex items-center gap-2">
                  <h4 class="font-bold text-slate-800 text-sm truncate">{{ s.title }}</h4>
                  <span v-if="s.id === currentSetId" class="px-2 py-0.5 bg-indigo-600 text-white text-[10px] font-bold rounded-full">
                    Đang chọn
                  </span>
                </div>
                <p v-if="s.description" class="text-xs text-slate-500 mt-0.5 truncate">{{ s.description }}</p>
                <div class="flex items-center gap-3 mt-1.5 text-xs text-slate-400">
                  <span>{{ s.totalWords }} từ</span>
                  <span>•</span>
                  <span class="text-emerald-600 font-semibold">{{ s.masteredWords }} đã thuộc</span>
                  <span>•</span>
                  <span class="text-indigo-600">{{ s.starredWords }} gắn sao</span>
                </div>
              </div>

              <!-- Set actions -->
              <div class="flex items-center gap-1 shrink-0">
                <button
                  @click="$emit('reset-progress', s.id)"
                  class="p-2 text-slate-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition"
                  title="Đặt lại tiến độ học về 0"
                >
                  <RotateCcw class="w-4 h-4" />
                </button>
                <button
                  v-if="sets.length > 1"
                  @click="$emit('delete-set', s.id)"
                  class="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                  title="Xóa bộ từ này"
                >
                  <Trash2 class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>

      </div>

      <!-- Modal Footer -->
      <div class="pt-4 border-t border-slate-100 text-right shrink-0">
        <button
          @click="$emit('close')"
          class="px-5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-xl text-sm transition"
        >
          Đóng
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { X, Trash2, RotateCcw } from 'lucide-vue-next';

defineProps({
  isOpen: Boolean,
  sets: Array,
  currentSetId: Number
});

const emit = defineEmits(['close', 'select-set', 'create-set', 'delete-set', 'reset-progress']);

const newTitle = ref('');
const newDesc = ref('');

function handleCreateSet() {
  if (!newTitle.value.trim()) return;
  emit('create-set', {
    title: newTitle.value.trim(),
    description: newDesc.value.trim()
  });
  newTitle.value = '';
  newDesc.value = '';
}
</script>
