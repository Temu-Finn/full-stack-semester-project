<script lang="ts" setup>
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <nav class="nav-bar">
    <a class="logo-section" href="/">
      <img alt="logo" src="../assets/logo.svg" width="36" />
      <p>Temu Finn</p>
    </a>
    <div class="items-section">
      <a href="/new">{{ $t('navbar.newProduct') }}</a>
      <a href="/messages">{{ $t('navbar.messages') }}</a>
    </div>
    <a class="profile-section" href="/profile">
      <img alt="profile-icon" src="../assets/logo.svg" width="36" />
      <p>{{ authStore.user?.name ?? 'Log in' }}</p>
    </a>
  </nav>
</template>

<style scoped>
.nav-bar {
  position: fixed;
  width: 100%;
  max-width: 100vh;
  padding: 1vh;
  height: 10vh;
  top: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}
.items-section {
  display: flex;
  gap: 2rem;
  vertical-align: middle;
}
.profile-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}
</style>
