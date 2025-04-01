import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/config/api'

interface User {
  id: string
  email: string
  name: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  function setToken(newToken: string | null) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  async function login(email: string, password: string) {
    try {
      const response = await api.post(`/auth/login`, { email, password })
      setToken(response.data.token)
      user.value = {
        id: response.data.userId,
        email: response.data.email,
        name: response.data.name,
      }
      return true
    } catch (error) {
      console.error('Error logging in:', error)
      return false
    }
  }

  async function signup(email: string, name: string, password: string) {
    try {
      const response = await api.post(`/auth/signup`, {
        email,
        name,
        password,
      })
      setToken(response.data.token)
      user.value = {
        id: response.data.userId,
        email: response.data.email,
        name: response.data.name,
      }
      return true
    } catch (error) {
      console.error('Error signing up:', error)
      return false
    }
  }

  function logout() {
    setToken(null)
  }

  return { token, isAuthenticated, login, signup, logout }
})
