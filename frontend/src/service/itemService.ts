import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

// Zod schemas for type validation
const ItemCardSchema = z.object({
  id: z.number(),
  title: z.string(),
  price: z.number(),
  municipality: z.string(),
  image: z
    .object({
      id: z.number(),
      dataURL: z.string(),
    })
    .nullish(),
  location: z
    .object({
      latitude: z.number(),
      longitude: z.number(),
    })
    .nullish(),
  status: z.string(),
  updatedAt: z.string(),
})

const RecommendedItemsResponseSchema = z.object({
  items: z.array(ItemCardSchema),
})

// Type exports for use in components
export type ItemCard = z.infer<typeof ItemCardSchema>
export type RecommendedItemsResponse = z.infer<typeof RecommendedItemsResponseSchema>

/**
 * Fetches recommended items for the current user
 * @returns Promise containing the recommended items
 * @throws Error if the API request fails or response validation fails
 */
export async function getRecommendedItems(): Promise<RecommendedItemsResponse> {
  try {
    const response = await api.get('/item/recommended')
    logger.debug('Received recommended items response', response.data)
    return RecommendedItemsResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid response format from server', { errors: error.errors })
      throw new Error('Invalid response format from server')
    }
    logger.error('Failed to fetch recommended items', error)
    throw error
  }
}
