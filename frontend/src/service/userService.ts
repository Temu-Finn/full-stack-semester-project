import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'
import axios from 'axios'

// === Zod Schemas ===
const UserSchema = z.object({
  id: z.number(),
  name: z.string(),
  email: z.string().email(),
})

const UpdateNameSchema = z.object({
  name: z.string().min(1, 'Name is required'),
})

const UpdateEmailSchema = z.object({
  email: z.string().email('Invalid email format'),
})

const ChangePasswordSchema = z.object({
  currentPassword: z.string().min(1),
  newPassword: z.string().min(8, 'New password must be at least 8 characters'),
})

// === Types ===
export type User = z.infer<typeof UserSchema>
export type UpdateNameRequest = z.infer<typeof UpdateNameSchema>
export type UpdateEmailRequest = z.infer<typeof UpdateEmailSchema>
export type ChangePasswordRequest = z.infer<typeof ChangePasswordSchema>

// === Service Methods ===

export async function updateUserName(data: UpdateNameRequest): Promise<void> {
  try {
    const validated = UpdateNameSchema.parse(data)
    await api.put('/user/update-name', validated)
  } catch (error) {
    handleError(error, 'updating name')
  }
}

export async function updateUserEmail(data: UpdateEmailRequest): Promise<void> {
  try {
    const validated = UpdateEmailSchema.parse(data)
    await api.put('/user/update-email', validated)
  } catch (error) {
    handleError(error, 'updating email')
  }
}

export async function changeUserPassword(data: ChangePasswordRequest): Promise<void> {
  try {
    const validated = ChangePasswordSchema.parse(data)
    await api.put('/user/update-password', validated)
  } catch (error) {
    handleError(error, 'changing password')
  }
}

// === Central Error Handling ===
function handleError(error: unknown, action: string): never {
  if (error instanceof z.ZodError) {
    logger.error(`Validation failed while ${action}`, { errors: error.errors })
    throw new Error(`Invalid input provided while ${action}.`)
  }

  if (axios.isAxiosError(error)) {
    logger.error(`API error while ${action}`, {
      status: error.response?.status,
      data: error.response?.data,
    })
    if (error.response?.status === 400) {
      throw new Error(`Bad request while ${action}.`)
    } else if (error.response?.status === 409) {
      throw new Error(`Conflict while ${action}.`)
    }
  }

  logger.error(`Unexpected error while ${action}`, error)
  throw new Error(`Something went wrong while ${action}. Please try again later.`)
}
