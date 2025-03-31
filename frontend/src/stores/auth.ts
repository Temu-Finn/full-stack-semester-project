import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import api from '@/config/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))

  const isAuthenticated = computed(() => !!token.value)

  function setToken(newToken: string | null) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  async function login(username: string, password: string) {
    try {
      const response = await api.post(`/auth/login`, { username, password })
      setToken(response.data.token)
      return true
    } catch (error) {
      console.error('Error logging in:', error)
      return false
    }
  }

  async function signup(username: string, email: string, password: string) {
    try {
      const response = await axios.post(`/auth/signup`, {
        username,
        email,
        password,
      })
      setToken(response.data.token)
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
