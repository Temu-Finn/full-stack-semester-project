import { createI18n } from 'vue-i18n'
import en from '/locales/en.json'
import no from '/locales/no.json'

const messages = {
  en,
  no,
}

const savedLocale = localStorage.getItem('locale') || 'en'

const i18n = createI18n({
  locale: savedLocale,
  fallbackLocale: 'en',
  messages,
})

export default i18n
