import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/config/api'
import router from '@/router'

interface User {
  id: string
  email: string
  name: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') ?? 'null'))

  const isAuthenticated = computed(() => !!token.value)

  function setToken(newToken: string | null, newUser: User | null = null) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
    user.value = newUser
    if (newUser) {
      localStorage.setItem('user', JSON.stringify(newUser))
    } else {
      localStorage.removeItem('user')
    }
  }

  async function login(email: string, password: string) {
    try {
      const response = await api.post(`/auth/login`, { email, password })
      const userData = {
        id: response.data.userId,
        email: response.data.email,
        name: response.data.name,
      }
      setToken(response.data.token, userData)
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
      const userData = {
        id: response.data.userId,
        email: response.data.email,
        name: response.data.name,
      }
      setToken(response.data.token, userData)
      return true
    } catch (error) {
      console.error('Error signing up:', error)
      return false
    }
  }

  function logout() {
    setToken(null, null)
    router.push('/')
  }

  return { token, isAuthenticated, login, signup, logout, user }
})
