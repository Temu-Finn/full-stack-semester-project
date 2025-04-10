<script lang="ts" setup>
import { ref } from 'vue'
import { useSessionStore } from '@/stores/session'
import { useI18n } from 'vue-i18n'

const authStore = useSessionStore()
const { t } = useI18n()

const isSidebarOpen = ref(false)
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}
const closeSidebar = () => {
  isSidebarOpen.value = false
}
</script>

<template>
  <div>
    <nav class="nav-bar">
      <div class="nav-content">
        <button class="menu-icon" @click="toggleSidebar">
          <svg height="24" viewBox="0 0 24 24" width="24">
            <path d="M3 6h18v2H3zM3 11h18v2H3zM3 16h18v2H3z" fill="#333" />
          </svg>
        </button>
        <a class="logo-section" href="/">
          <img alt="logo" src="/TemuFinn.png" width="96" />
        </a>
        <div class="items-section">
          <a href="/search">{{ t('navbar.browse') }}</a>
          <a href="/new">{{ t('navbar.newProduct') }}</a>
          <a href="/chat">{{ t('navbar.messages') }}</a>
        </div>
        <a class="profile-section" href="/profile">
          <img
            :class="authStore.user ? '' : 'hidden'"
            alt="profile-icon"
            src="/profile-avatar.png"
            width="36"
          />
          <p>{{ authStore.user?.name ?? 'Log in' }}</p>
        </a>
      </div>
    </nav>

    <aside :class="{ 'mobile-open': isSidebarOpen }" class="sidebar">
      <button class="close-sidebar" @click="closeSidebar">&times;</button>
      <nav class="sidebar-nav">
        <a href="/search" @click="closeSidebar">{{ t('navbar.browse') }}</a>
        <a href="/new" @click="closeSidebar">{{ t('navbar.newProduct') }}</a>
        <a href="/chat" @click="closeSidebar">{{ t('navbar.messages') }}</a>
        <a class="sidebar-profile" href="/profile" @click="closeSidebar">
          <img
            :class="authStore.user ? '' : 'hidden'"
            alt="profile-icon"
            src="/profile-avatar.png"
            width="36"
          />
          <span>{{ authStore.user?.name ?? 'Log in' }}</span>
        </a>
      </nav>
    </aside>

    <div :class="isSidebarOpen ? 'overlay' : 'overlay hidden'" @click="closeSidebar"></div>
  </div>
</template>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  box-shadow: rgba(0, 0, 0, 0.1) 0 0 8px;
  z-index: 10;
  font-size: 15px;
  height: 60px;
}

.nav-content {
  padding: 0.8rem;
  width: 100%;
  max-width: 960px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu-icon {
  background: none;
  border: none;
  cursor: pointer;
  display: none;
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

.nav-bar p,
.nav-bar a {
  font-size: 15px;
}

.sidebar {
  position: fixed;
  top: 0;
  left: -100%;
  width: 75%;
  height: 100%;
  background-color: #fff;
  padding: 1rem;
  z-index: 1001;
  transition: left 0.3s ease;
}

.sidebar.mobile-open {
  left: 0;
}

.close-sidebar {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  text-align: end;
  width: 100%;
}

.sidebar-nav {
  margin-top: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.sidebar-nav a {
  text-decoration: none;
  color: #333;
  font-size: 1.2rem;
}

.sidebar-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  transition: background-color 0.3s ease;
}

.overlay.hidden {
  background-color: rgba(0, 0, 0, 0);
  pointer-events: none;
}

@media (max-width: 768px) {
  .menu-icon {
    display: block;
  }
  .items-section {
    display: none;
  }
  .profile-section {
    display: none;
  }
}
</style>
