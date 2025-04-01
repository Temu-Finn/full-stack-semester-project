import { defineStore } from 'pinia'
import { ref, type Ref } from 'vue'
import i18n from '@/config/i18n'

export type SupportedLocale = 'en' | 'no'
export const availableLocales: SupportedLocale[] = ['en', 'no']

const defaultLocale: SupportedLocale = 'en'

const getInitialLocale = (): SupportedLocale => {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale && availableLocales.includes(savedLocale as SupportedLocale)) {
    return savedLocale as SupportedLocale
  }
  return defaultLocale
}

export const useLocaleStore = defineStore('locale', () => {
  const currentLocale = ref<SupportedLocale>(getInitialLocale())

  function setLocale(newLocale: SupportedLocale) {
    if (!availableLocales.includes(newLocale)) {
      console.warn(`Attempted to set unsupported locale: ${newLocale}`)
      return
    }

    currentLocale.value = newLocale
    localStorage.setItem('locale', newLocale)
    ;(i18n.global.locale as unknown as Ref<SupportedLocale>).value = newLocale
  }

  return { currentLocale, setLocale, availableLocales }
})
