<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const handleLogin = async () => {
  if (!email.value || !password.value) {
    errorMessage.value = t('login.error.required')
    return
  }

  isLoading.value = true
  try {
    const success = await authStore.login(email.value, password.value)
    if (success) {
      router.push('/')
    } else {
      errorMessage.value = t('login.error.invalidCredentials')
    }
  } catch (error: unknown) {
    console.error('Login error:', error)

    if (error instanceof Error) {
      errorMessage.value = error.message
    } else {
      errorMessage.value = t('login.error.unexpected')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <h2 class="login-title">{{ $t('login.title') }}</h2>

    <form @submit.prevent="handleLogin" class="login-form">
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div>
        <label for="email" class="input-label">{{ $t('login.emailLabel') }}</label>
        <input id="email" v-model="email" type="email" class="input-field" required />
      </div>

      <div>
        <label for="password" class="input-label">{{ $t('login.passwordLabel') }}</label>
        <input id="password" v-model="password" type="password" class="input-field" required />
      </div>

      <div>
        <button type="submit" :disabled="isLoading" class="submit-button">
          <span v-if="isLoading">{{ $t('login.loadingButton') }}</span>
          <span v-else>{{ $t('login.submitButton') }}</span>
        </button>
      </div>

      <div class="signup-link-container">
        <router-link to="/signup" class="signup-link"> {{ $t('login.signupLink') }} </router-link>
      </div>
    </form>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #374151;
}

.login-form {
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

.signup-link-container {
  text-align: center;
  margin-top: 1rem;
}

.signup-link {
  font-size: 0.875rem;
  color: #60a5fa;
  text-decoration: none;
  transition: color 0.2s;
}

.signup-link:hover {
  color: #3b82f6;
}

.login-form div {
  width: 100%;
}
</style>
