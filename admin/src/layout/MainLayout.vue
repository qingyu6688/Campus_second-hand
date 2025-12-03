<script setup>
import SideBar from './components/SideBar.vue'
import Header from './components/Header.vue'
import TagsView from './components/TagsView.vue'
</script>

<template>
  <div class="flex h-screen w-full bg-gray-100 overflow-hidden">
    <!-- Sidebar -->
    <SideBar class="flex-shrink-0" />

    <!-- Main Container -->
    <div class="flex flex-col flex-1 min-w-0">
      <!-- Header -->
      <Header />

      <!-- Tags View -->
      <TagsView />

      <!-- App Main -->
      <div class="flex-1 overflow-y-auto bg-[#f0f2f5] p-4 relative">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
