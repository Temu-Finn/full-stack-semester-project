// userService.ts
import axios from 'axios'

export interface UpdateEmailRequest {
  email: string
}

export interface UpdateNameRequest {
  name: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

export async function updateEmail(request: UpdateEmailRequest): Promise<void> {
  await axios.put('/api/user/update-email', request)
}

export async function updateName(request: UpdateNameRequest): Promise<void> {
  await axios.put('/api/user/update-name', request)
}

export async function changePassword(request: ChangePasswordRequest): Promise<void> {
  await axios.put('/api/user/update-password', request)
}
