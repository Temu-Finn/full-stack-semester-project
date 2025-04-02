<script lang="ts" setup>
import { useSessionStore } from '@/stores/session'

const authStore = useSessionStore()
</script>

<template>
  <nav class="nav-bar">
    <div class="nav-content">
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
    </div>
  </nav>
</template>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  box-shadow: rgba(0, 0, 0, 0.1) 0 0 8px;
  z-index: 10;
  font-size: 15px;
}
.nav-content {
  padding: 0.8rem;
  width: 100%;
  max-width: 960px;
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
.nav-bar p {
  font-size: 15px;
}
.nav-bar a {
  font-size: 15px;
}
.profile-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}
</style>
