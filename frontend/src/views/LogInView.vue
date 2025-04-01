<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = 'Username and password are required'
    return
  }

  isLoading.value = true
  try {
    const success = await authStore.login(username.value, password.value)
    if (success) {
      router.push('/') // Redirect to home or dashboard on success
    } else {
      errorMessage.value = 'Invalid credentials'
    }
  } catch (error) {
    errorMessage.value = 'An error occurred during login'
    console.error('Login error:', error) // Log the actual error
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <h2 class="login-title">Log In</h2>

    <form @submit.prevent="handleLogin" class="login-form">
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div>
        <label for="username" class="input-label">Username</label>
        <input id="username" v-model="username" type="text" class="input-field" required />
      </div>

      <div>
        <label for="password" class="input-label">Password</label>
        <input id="password" v-model="password" type="password" class="input-field" required />
      </div>

      <div>
        <button type="submit" :disabled="isLoading" class="submit-button">
          <span v-if="isLoading">Logging in...</span>
          <span v-else>Log In</span>
        </button>
      </div>

      <div class="signup-link-container">
        <router-link to="/signup" class="signup-link"> Don't have an account? Sign up </router-link>
      </div>
    </form>
  </div>
</template>

<style>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 1.5rem; /* 2xl */
  font-weight: bold;
  margin-bottom: 1.5rem; /* mb-6 */
  text-align: center;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem; /* space-y-4 */
}

.error-message {
  background-color: #fee2e2; /* bg-red-100 */
  border: 1px solid #f87171; /* border-red-400 */
  color: #b91c1c; /* text-red-700 */
  padding: 0.75rem 1rem; /* px-4 py-3 */
  border-radius: 0.375rem; /* rounded */
}

.input-label {
  display: block;
  font-size: 0.875rem; /* text-sm */
  font-weight: 500; /* font-medium */
  color: #374151; /* text-gray-700 */
  margin-bottom: 0.25rem; /* Implicit margin from mt-1 on input */
}

.input-field {
  display: block;
  width: 100%;
  padding: 0.5rem 0.75rem; /* px-3 py-2 */
  border: 1px solid #d1d5db; /* border-gray-300 */
  border-radius: 0.375rem; /* rounded-md */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); /* shadow-sm */
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: #3b82f6; /* focus:border-blue-500 */
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3); /* focus:ring-blue-500 */
}

.submit-button {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0.5rem 1rem; /* py-2 px-4 */
  border: 1px solid transparent;
  border-radius: 0.375rem; /* rounded-md */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); /* shadow-sm */
  font-size: 0.875rem; /* text-sm */
  font-weight: 500; /* font-medium */
  color: #fff; /* text-white */
  background-color: #60a5fa; /* bg-blue-400 */
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
  background-color: #3b82f6; /* hover:bg-blue-500 */
}

.submit-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.5); /* focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 */
}

.submit-button:disabled {
  opacity: 0.5; /* disabled:opacity-50 */
  cursor: not-allowed;
}

.signup-link-container {
  text-align: center;
  margin-top: 1rem; /* mt-4 */
}

.signup-link {
  font-size: 0.875rem; /* text-sm */
  color: #60a5fa; /* text-blue-400 */
  text-decoration: none;
  transition: color 0.2s;
}

.signup-link:hover {
  color: #3b82f6; /* hover:text-blue-500 */
}

/* Ensure inputs take full width inside flex container */
.login-form div {
  width: 100%;
}
</style>
