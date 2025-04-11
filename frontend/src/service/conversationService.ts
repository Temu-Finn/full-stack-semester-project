import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

export type ConversationCardsResponse = z.infer<typeof ConversationCardsResponseSchema>
export type ConversationResponse = z.infer<typeof getConversationResponseSchema>
export type MessageResponse = z.infer<typeof MessageResponseSchema>

import { ItemCardSchema } from '@/service/itemService.ts'

const ConversationCardsResponseSchema = z.object({
  conversations: z.array(
    z.object({
      id: z.number(),
      lastMessage: z.string().nullable(),
      lastMessageTime: z.string().nullable(),
      item: ItemCardSchema,
    }),
  ),
})

const MessageResponseSchema = z.object({
  conversationId: z.number().optional(),
  senderId: z.number(),
  content: z.string(),
  sentAt: z.string().datetime(),
})

const getConversationResponseSchema = z.object({
  otherParticipantName: z.string(),
  createdAt: z.string(), // ISO 8601 string for LocalDateTime
  updatedAt: z.string(), // ISO 8601 string for LocalDateTime
  messages: z.array(MessageResponseSchema),
  item: ItemCardSchema,
})

export async function getConversations(): Promise<ConversationCardsResponse> {
  try {
    logger.debug('Calling API to fetch conversations...')
    const response = await api.get('conversations/getAll')
    logger.debug('Received conversations response', response.data)

    return ConversationCardsResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid conversations response format from server', { errors: error.errors })
      throw new Error('Invalid conversations response format from server')
    }
    logger.error('Failed to fetch conversations', error)
    throw new Error('Failed to load conversations. Please try again later.')
  }
}

export async function getConversation(
  conversationId: number,
): Promise<z.infer<typeof getConversationResponseSchema>> {
  try {
    logger.debug('Calling API to fetch conversation with ID:', conversationId)
    const response = await api.get(`conversations/${conversationId}`)
    logger.debug('Received conversation response', response.data)

    return getConversationResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid conversation response format from server', { errors: error.errors })
      throw new Error('Invalid conversation response format from server')
    }
    logger.error('Failed to fetch conversation', error)
    throw new Error('Failed to load conversation. Please try again later.')
  }
}

export async function sendMessage(
  conversationId?: number,
  itemId: number,
  message: string,
): Promise<MessageResponse> {
  try {
    logger.debug('Sending message to conversation:', conversationId, message)
    const payload: Record<string, any> = { content: message, itemId }
    if (conversationId) {
      payload.conversationId = conversationId
    }
    await api.post(`conversations/sendMessage`, {
      conversationId: conversationId,
      itemId: itemId,
      content: message,
    })
    logger.debug('Message sent successfully')
  } catch (error) {
    logger.error('Failed to send message', error)
    throw new Error('Failed to send message. Please try again later.')
  }
}
