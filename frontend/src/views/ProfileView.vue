<script lang="ts" setup>
import LanguageSelector from '@/components/LanguageSelector.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <main>
    <button @click="authStore.logout">Logout</button>
    <h1>{{ $t('profile.title') }}</h1>
    <LanguageSelector />
  </main>
</template>

<style></style>
