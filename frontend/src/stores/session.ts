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
  admin: boolean
}

export const useSessionStore = defineStore('session', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') ?? 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.admin ?? false)

  function setToken(response: UserResponse | null) {
    if (response === null) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return
    }

    token.value = response.token
    localStorage.setItem('token', response.token)
    user.value = {
      id: response.userId,
      email: response.email,
      name: response.name,
      joinedAt: response.joinedAt,
      admin: response.admin,
    }
    localStorage.setItem('user', JSON.stringify(user.value))
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

  return { token, user, isAuthenticated, isAdmin, login, signup, logout }
})
