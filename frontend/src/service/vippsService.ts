import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

const StartVippsPaymentResponseSchema = z.object({
  redirectUrl: z.string().url(),
  reference: z.string().uuid(),
})

export type StartVippsPaymentResponse = z.infer<typeof StartVippsPaymentResponseSchema>

const VippsPaymentStatusSchema = z.object({
  amount: z.object({
    currency: z.string(),
    value: z.number(),
  }),
  aggregate: z.object({
    authorizedAmount: z.object({
      currency: z.string(),
      value: z.number(),
    }),
    cancelledAmount: z.object({
      currency: z.string(),
      value: z.number(),
    }),
    capturedAmount: z.object({
      currency: z.string(),
      value: z.number(),
    }),
    refundedAmount: z.object({
      currency: z.string(),
      value: z.number(),
    }),
  }),
  state: z.string(),
  reference: z.string().uuid(),
})

export type VippsPaymentStatus = z.infer<typeof VippsPaymentStatusSchema>

export async function startVippsPayment(price: number): Promise<StartVippsPaymentResponse> {
  try {
    const response = await api.post('/vipps/payment', { price })
    logger.debug('Received Vipps payment response:', response.data)
    return StartVippsPaymentResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid response format from Vipps /vipps/payment endpoint', {
        errors: error.errors,
      })
      throw new Error('Invalid response format from server')
    }
    logger.error('Failed to start Vipps payment', error)
    throw error
  }
}

export async function checkVippsStatus(
  reference: string,
): Promise<{ status: 'APPROVED' | 'PENDING' | 'FAILED' }> {
  try {
    const response = await api.get(`/vipps/payment/${reference}`)
    const parsed = VippsPaymentStatusSchema.parse(response.data)

    logger.debug('Vipps payment status response:', parsed)

    if (parsed.state === 'AUTHORIZED') {
      return { status: 'APPROVED' }
    }

    if (parsed.state === 'CREATED' || parsed.state === 'INITIATED') {
      return { status: 'PENDING' }
    }

    return { status: 'FAILED' }
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid Vipps status response format', { errors: error.errors })
      throw new Error('Invalid response format from server')
    }
    logger.error('Failed to check Vipps payment status', error)
    throw error
  }
}