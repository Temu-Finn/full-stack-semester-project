<script lang="ts" setup>
import { useI18n } from 'vue-i18n'
import { watch } from 'vue'

const { locale } = useI18n()

const changeLanguage = () => {
  watch(locale, (newLocale) => {
    localStorage.setItem('locale', newLocale)
  })
}
</script>

<template>
  <select v-model="locale" @change="changeLanguage">
    <option value="en">English</option>
    <option value="no">Norsk</option>
  </select>
</template>

<style scoped></style>
