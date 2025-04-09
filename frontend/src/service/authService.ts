import { z } from 'zod'
import api from '@/config/api'

const LoginRequestSchema = z.object({
  email: z.string().email(),
  password: z.string(),
})

const UserResponseSchema = z.object({
  userId: z.number(),
  name: z.string(),
  email: z.string(),
  joinedAt: z.string(),
  isAdmin: z.boolean(),
  token: z.string(),
  expiresIn: z.number(),
})

export type UserResponse = z.infer<typeof UserResponseSchema>

export async function login(email: string, password: string): Promise<UserResponse> {
  const response = await api.post('/auth/login', { email, password })
  return UserResponseSchema.parse(response.data)
}

export async function signup(email: string, name: string, password: string): Promise<UserResponse> {
  const response = await api.post('/auth/signup', { email, name, password })
  return UserResponseSchema.parse(response.data)
}
