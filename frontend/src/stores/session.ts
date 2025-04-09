import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/config/api'
import router from '@/router'
import { loginUser, signupUser, type UserResponse } from '@/service/authService'

export interface User {
  id: number
  email: string
  name: string
  joinedAt: string
}

export const useSessionStore = defineStore('session', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') ?? 'null'))

  const isAuthenticated = computed(() => !!token.value)

  function setToken(response: UserResponse | null) {
    if (response) {
      token.value = response.token
      localStorage.setItem('token', response.token)
    } else {
      localStorage.removeItem('token')
    }
    if (response) {
      user.value = {
        id: response.userId,
        email: response.email,
        name: response.name,
        joinedAt: response.joinedAt,
      }
      localStorage.setItem('user', JSON.stringify(user.value))
    } else {
      localStorage.removeItem('user')
    }
  }

  async function login(email: string, password: string) {
    try {
      const response = await loginUser(email, password)
      setToken(response)
      return true
    } catch (error) {
      console.error('Error logging in:', error)
      return false
    }
  }

  async function signup(email: string, name: string, password: string) {
    try {
      const response = await signupUser(email, name, password)
      setToken(response)
      return true
    } catch (error) {
      console.error('Error signing up:', error)
      return false
    }
  }

  function logout() {
    setToken(null)
    router.push('/')
  }

  return { token, isAuthenticated, login, signup, logout, user }
})
