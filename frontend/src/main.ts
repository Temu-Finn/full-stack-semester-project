import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import i18n from '@/config/i18n.ts'
import PrimeVue from 'primevue/config'
import Lara from '@primeuix/themes/lara'
import { Button } from 'primevue'
import { definePreset } from '@primeuix/themes'

const app = createApp(App)
const MyPreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: '{indigo.50}',
      100: '{indigo.100}',
      200: '{indigo.200}',
      300: '{indigo.300}',
      400: '{indigo.400}',
      500: '{indigo.500}',
      600: '{indigo.600}',
      700: '{indigo.700}',
      800: '{indigo.800}',
      900: '{indigo.900}',
      950: '{indigo.950}',
    },
  },
})

app.use(createPinia())
app.use(i18n)
app.use(router)
app.use(PrimeVue, {
  theme: {
    preset: MyPreset,
    options: { darkModeSelector: 'light' },
  },
  ripple: true,
})
app.component('Button', Button)

app.mount('#app')
