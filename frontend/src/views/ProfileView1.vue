<script lang="ts" setup>
import LanguageSelector from '@/components/LanguageSelector.vue'
import { useSessionStore } from '@/stores/session'

const authStore = useSessionStore()
</script>

<template>
  <main>
    <button @click="authStore.logout">{{ $t('profile.logout') }}</button>
    <h1>{{ $t('profile.title') }}</h1>
    <LanguageSelector />
  </main>
</template>

<style></style>
