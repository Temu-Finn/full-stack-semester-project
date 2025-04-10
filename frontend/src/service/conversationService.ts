import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

export type ConversationCardsResponse = z.infer<typeof ConversationCardsResponseSchema>
export type MessageResponse = z.infer<typeof MessageResponseSchema>

const ItemSchema = z.object({
  id: z.number(),
  title: z.string(),
  price: z.number(),
  municipality: z.string(),
  image: z.string().nullable(),
  location: z.object({
    latitude: z.number(),
    longitude: z.number(),
  }),
  status: z.string(),
  updatedAt: z.string(),
})

const ConversationCardsResponseSchema = z.object({
  conversations: z.array(
    z.object({
      id: z.number(),
      lastMessage: z.string().nullable(),
      lastMessageTime: z.string().nullable(),
      item: ItemSchema,
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
  item: ItemSchema,
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

    const validatedData = getConversationResponseSchema.parse(response.data)
    return validatedData
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid conversation response format from server', { errors: error.errors })
      throw new Error('Invalid conversation response format from server')
    }
     logger.error('Failed to fetch conversation', error)
    throw new Error('Failed to load conversation. Please try again later.')
  }

  export async function sendMessage(
    conversationId?: number, itemId: number,  message: string
  ): Promise<MessageResponse> {
    try {
      logger.debug('Sending message to conversation:', conversationId, message)
      const payload: Record<string, any> = { content: message, itemId };
      if (conversationId) {
        payload.conversationId = conversationId;
      }
      await api.post(`conversations/sendMessage`, {
        conversationId: conversationId, itemId: itemId, content: message })
      logger.debug('Message sent successfully')
    } catch (error) {
      logger.error('Failed to send message', error)
      throw new Error('Failed to send message. Please try again later.')
    }
  }
}
