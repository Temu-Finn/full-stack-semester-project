<script lang="ts" setup>
import { useLocaleStore } from '@/stores/locale'

const localeStore = useLocaleStore()
const changeLanguage = () => {
  localeStore.setLocale(localeStore.currentLocale)
}
</script>

<template>
  <select v-model="localeStore.currentLocale" @change="changeLanguage">
    <option value="en">English</option>
    <option value="no">Norsk</option>
  </select>
</template>

<style scoped></style>
