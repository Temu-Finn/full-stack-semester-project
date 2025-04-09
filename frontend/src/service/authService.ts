import { z } from 'zod'
import api from '@/config/api'

const LoginRequestSchema = z.object({
  email: z.string().email(),
  password: z.string(),
})

const SignupRequestSchema = z.object({
  email: z.string().email(),
  name: z.string(),
  password: z.string(),
})

const UserResponseSchema = z.object({
  userId: z.number(),
  name: z.string(),
  email: z.string(),
  joinedAt: z.string(),
  admin: z.boolean(),
  token: z.string(),
  expiresIn: z.number(),
})

export type UserResponse = z.infer<typeof UserResponseSchema>

export async function loginUser(email: string, password: string): Promise<UserResponse> {
  const request = LoginRequestSchema.parse({ email, password })
  const response = await api.post('/auth/login', request)
  return UserResponseSchema.parse(response.data)
}

export async function signupUser(
  email: string,
  name: string,
  password: string,
): Promise<UserResponse> {
  const request = SignupRequestSchema.parse({ email, name, password })
  const response = await api.post('/auth/signup', request)
  return UserResponseSchema.parse(response.data)
}
