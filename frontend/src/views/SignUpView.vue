<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '@/stores/session'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter()
const authStore = useSessionStore()

const name = ref('')
const email = ref('')
const password = ref('')
const repeatPassword = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const handleSignup = async () => {
  errorMessage.value = ''

  if (!name.value || !email.value || !password.value || !repeatPassword.value) {
    errorMessage.value = t('signup.error.required')
    return
  }
  if (password.value.length < 8) {
    errorMessage.value = t('signup.error.passwordLength')
    return
  }
  if (password.value !== repeatPassword.value) {
    errorMessage.value = t('signup.error.passwordMismatch')
    return
  }

  isLoading.value = true
  try {
    const success = await authStore.signup(email.value, name.value, password.value)
    if (success) {
      router.push('/')
    } else {
      errorMessage.value = t('signup.error.failed')
    }
  } catch (error: unknown) {
    console.error('Signup error:', error)

    if (error instanceof Error) {
      errorMessage.value = error.message
    } else {
      errorMessage.value = t('signup.error.unexpected')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="signup-container">
    <h2 class="signup-title">{{ $t('signup.title') }}</h2>

    <form @submit.prevent="handleSignup" class="signup-form">
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div>
        <label for="name" class="input-label">{{ $t('signup.nameLabel') }}</label>
        <input id="name" v-model="name" type="text" class="input-field" required />
      </div>

      <div>
        <label for="email" class="input-label">{{ $t('signup.emailLabel') }}</label>
        <input id="email" v-model="email" type="email" class="input-field" required />
      </div>

      <div>
        <label for="password" class="input-label">{{ $t('signup.passwordLabel') }}</label>
        <input id="password" v-model="password" type="password" class="input-field" required />
      </div>

      <div>
        <label for="repeatPassword" class="input-label">{{
          $t('signup.repeatPasswordLabel')
        }}</label>
        <input
          id="repeatPassword"
          v-model="repeatPassword"
          type="password"
          class="input-field"
          required
        />
      </div>

      <div>
        <button type="submit" :disabled="isLoading" class="submit-button">
          <span v-if="isLoading">{{ $t('signup.loadingButton') }}</span>
          <span v-else>{{ $t('signup.submitButton') }}</span>
        </button>
      </div>

      <div class="login-link-container">
        <router-link to="/login" class="login-link"> {{ $t('signup.loginLink') }} </router-link>
      </div>
    </form>
  </div>
</template>

<style scoped>
.signup-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.signup-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #374151;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.error-message {
  background-color: #fee2e2;
  border: 1px solid #f87171;
  color: #b91c1c;
  padding: 0.75rem 1rem;
  border-radius: 0.375rem;
}

.input-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.25rem;
}

.input-field {
  display: block;
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
}

.submit-button {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0.5rem 1rem;
  border: 1px solid transparent;
  border-radius: 0.375rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  font-size: 0.875rem;
  font-weight: 500;
  color: #fff;
  background-color: #60a5fa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
  background-color: #3b82f6;
}

.submit-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.5);
}

.submit-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-link-container {
  text-align: center;
  margin-top: 1rem;
}

.login-link {
  font-size: 0.875rem;
  color: #60a5fa;
  text-decoration: none;
  transition: color 0.2s;
}

.login-link:hover {
  color: #3b82f6;
}

.signup-form div {
  width: 100%;
}
</style>
