<script setup>
import { RouterView, useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()
</script>

<template>
  <div class="min-h-screen bg-slate-50 font-sans antialiased md:pb-0 md:pt-16"
    :class="{ 'pb-16': !route.meta.hideNav }">
    <RouterView />
    <NavBar v-if="!route.meta.hideNav" />
  </div>
</template>
