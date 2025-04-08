import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

const CategorySchema = z.object({
  id: z.number(),
  icon: z.string(),
  name: z.string(),
})

const CategoriesResponseSchema = z.object({
  categories: z.array(CategorySchema),
})

export type Category = z.infer<typeof CategorySchema>

/**
 * Fetches all available categories.
 * @returns Promise containing an array of categories.
 * @throws Error if the API request fails or response validation fails.
 */
export async function getCategories(): Promise<Category[]> {
  try {
    const response = await api.get('/categories/getAll')
    logger.debug('Received categories response', response.data)

    const validatedData = CategoriesResponseSchema.parse(response.data)
    return validatedData.categories
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid categories response format from server', { errors: error.errors })
      throw new Error('Invalid categories response format from server')
    }
    logger.error('Failed to fetch categories', error)
    throw new Error('Failed to load categories. Please try again later.')
  }
}
