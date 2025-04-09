<script lang="ts" setup>
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()
const changeLanguage = () => {
  localeStore.setLocale(localeStore.currentLocale)
}
</script>

<template>
  <div class="language-selector">
    <label for="language">{{ $t('profile.language') }}</label>
    <select v-model="localeStore.currentLocale" @change="changeLanguage">
      <option value="en">English</option>
      <option value="no">Norsk</option>
    </select>
  </div>
</template>

<style scoped>
.language-selector {
  display: flex;
  flex-direction: column;
}
</style>
