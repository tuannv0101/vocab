<template>
  <header class="bg-white/95 backdrop-blur-md border-b border-slate-200/80 sticky top-0 z-30 shadow-xs">
    <div class="max-w-7xl mx-auto px-3 sm:px-6 lg:px-8">
      
      <!-- Primary Top Row: Brand, Set Selector, (Desktop Tabs), Actions -->
      <div class="flex items-center justify-between h-14 sm:h-16 gap-2 sm:gap-4">
        
        <!-- Logo & Brand -->
        <div
          class="flex items-center gap-2 sm:gap-3 cursor-pointer shrink-0 group"
          @click="$emit('change-tab', 'flashcards')"
          title="Trang chủ VocabMaster"
        >
          <div class="w-8 h-8 sm:w-10 sm:h-10 rounded-xl sm:rounded-2xl bg-gradient-to-tr from-indigo-600 via-indigo-700 to-violet-600 flex items-center justify-center text-white shadow-md shadow-indigo-500/20 group-hover:scale-105 transition-transform shrink-0">
            <Sparkles class="w-4 h-4 sm:w-5 sm:h-5" />
          </div>
          <div class="min-w-0">
            <div class="flex items-center gap-1.5 sm:gap-2">
              <span class="text-base sm:text-xl font-black bg-gradient-to-r from-indigo-600 via-indigo-700 to-violet-600 bg-clip-text text-transparent tracking-tight truncate">
                VocabMaster
              </span>
              <span class="px-1.5 py-0.2 sm:py-0.5 text-[9px] sm:text-[10px] font-black uppercase tracking-wider bg-gradient-to-r from-indigo-500/10 to-violet-500/10 text-indigo-700 border border-indigo-200/70 rounded shrink-0">
                PRO
              </span>
            </div>
            <p class="text-[10px] sm:text-[11px] text-slate-400 font-medium hidden md:block">Ôn tập từ vựng chuẩn Quizlet</p>
          </div>
        </div>

        <!-- Navigation Tabs (Only in main bar on extra-large screens >= 1280px) -->
        <nav class="hidden xl:flex items-center gap-1 bg-slate-100/90 p-1 rounded-2xl border border-slate-200/60 shadow-xs shrink-0">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="$emit('change-tab', tab.id)"
            :class="[
              'flex items-center gap-2 px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer',
              activeTab === tab.id
                ? 'bg-white text-indigo-700 shadow-xs font-extrabold'
                : 'text-slate-600 hover:text-slate-900 hover:bg-white/40'
            ]"
          >
            <component :is="tab.icon" class="w-4 h-4" />
            <span>{{ tab.name }}</span>
          </button>
        </nav>

        <!-- Right Controls: Set Selector & Action Buttons -->
        <div class="flex items-center gap-1.5 sm:gap-2 min-w-0">
          
          <!-- Set Selector Pill Dropdown -->
          <div class="flex items-center gap-1 min-w-0">
            <div class="relative flex items-center bg-slate-50 hover:bg-slate-100 border border-slate-200/80 rounded-xl px-2 sm:px-2.5 py-1 sm:py-1.5 transition max-w-[130px] sm:max-w-[200px] md:max-w-xs">
              <Folder class="w-3.5 h-3.5 text-indigo-600 mr-1.5 shrink-0 pointer-events-none hidden sm:block" />
              <select
                :value="currentSetId"
                @change="$emit('select-set', Number($event.target.value))"
                class="w-full bg-transparent text-xs sm:text-sm font-bold text-slate-700 focus:outline-none cursor-pointer pr-4 truncate"
              >
                <option v-for="set in sets" :key="set.id" :value="set.id">
                  {{ set.title }} ({{ set.totalWords }} từ)
                </option>
              </select>
              <ChevronDown class="w-3 h-3 text-slate-400 absolute right-1.5 sm:right-2 pointer-events-none" />
            </div>

            <button
              @click="$emit('open-sets-modal')"
              title="Quản lý các bộ từ vựng"
              class="p-1.5 sm:p-2 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50/80 border border-slate-200/80 rounded-xl transition shrink-0 cursor-pointer"
            >
              <FolderPlus class="w-3.5 h-3.5 sm:w-4 sm:h-4" />
            </button>
          </div>

          <!-- Action Buttons -->
          <div class="flex items-center gap-1 sm:gap-1.5 shrink-0">
            <button
              @click="$emit('open-add-word-modal')"
              class="inline-flex items-center justify-center gap-1.5 p-1.5 sm:px-3 sm:py-2 bg-indigo-50/80 hover:bg-indigo-100 text-indigo-700 border border-indigo-200/60 rounded-xl text-xs font-bold transition cursor-pointer"
              title="Thêm 1 từ vựng mới"
            >
              <Plus class="w-4 h-4" />
              <span class="hidden sm:inline">Thêm từ</span>
            </button>

            <button
              @click="$emit('open-import-modal')"
              class="inline-flex items-center justify-center gap-1.5 p-1.5 sm:px-3.5 sm:py-2 bg-gradient-to-r from-indigo-600 via-indigo-700 to-violet-600 hover:opacity-95 text-white rounded-xl text-xs font-extrabold shadow-sm shadow-indigo-300/40 transition active:scale-95 cursor-pointer"
              title="Thêm nhiều từ cùng lúc (nhập hàng loạt hoặc bảng nhập liệu)"
            >
              <Layers class="w-4 h-4" />
              <span class="hidden sm:inline">Thêm nhiều từ</span>
            </button>
          </div>

        </div>

      </div>

      <!-- Secondary Sub-bar: Navigation Tabs for screens < 1280px (Tablet & Mobile) -->
      <div class="xl:hidden py-1.5 border-t border-slate-100/80 overflow-x-auto scrollbar-none">
        <nav class="flex items-center justify-start sm:justify-center gap-1 sm:gap-1.5 min-w-max px-1">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="$emit('change-tab', tab.id)"
            :class="[
              'flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap cursor-pointer',
              activeTab === tab.id
                ? 'bg-indigo-600 text-white shadow-xs font-extrabold'
                : 'bg-slate-100/80 text-slate-600 hover:text-slate-900 hover:bg-slate-200/80'
            ]"
          >
            <component :is="tab.icon" class="w-3.5 h-3.5" />
            <span>{{ tab.name }}</span>
          </button>
        </nav>
      </div>

    </div>
  </header>
</template>

<script setup>
import { 
  Sparkles, 
  Layers, 
  BookOpen, 
  Gamepad2, 
  FileText, 
  FolderPlus, 
  Folder, 
  Plus, 
  ChevronDown,
  List
} from 'lucide-vue-next';

defineProps({
  sets: {
    type: Array,
    default: () => []
  },
  currentSetId: {
    type: Number,
    default: null
  },
  activeTab: {
    type: String,
    default: 'flashcards'
  }
});

defineEmits([
  'change-tab',
  'select-set',
  'open-sets-modal',
  'open-add-word-modal',
  'open-import-modal'
]);

const tabs = [
  { id: 'flashcards', name: 'Thẻ ghi nhớ', icon: Layers },
  { id: 'learn', name: 'Học thông minh', icon: BookOpen },
  { id: 'match', name: 'Ghép thẻ', icon: Gamepad2 },
  { id: 'test', name: 'Kiểm tra', icon: FileText },
  { id: 'words', name: 'Danh sách từ', icon: List }
];
</script>
