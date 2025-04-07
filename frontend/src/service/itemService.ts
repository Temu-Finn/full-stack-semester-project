import { z } from 'zod'
import api from '@/config/api'

// Zod schemas for type validation
const ItemCardSchema = z.object({
  itemId: z.number(),
  title: z.string(),
  price: z.number(),
  municipality: z.string(),
  imageBase64: z.string(),
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
    return RecommendedItemsResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      throw new Error('Invalid response format from server')
    }
    throw error
  }
}
