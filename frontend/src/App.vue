<template>
  <div class="app-container">
    <Navbar />
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'
import Navbar from './components/Navbar.vue'

const authStore = useAuthStore()

onMounted(() => {
  authStore.initAuth()
})
</script>

<style>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  background-color: #f8f9fa;
  padding-bottom: 2rem;
}
</style>
