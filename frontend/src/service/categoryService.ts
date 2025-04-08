import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

const CategorySchema = z.object({
  id: z.number(),
  name: z.string(),
  description: z.string(),
})

const CategoryResponseSchema = z.object({
  categories: z.array(CategorySchema),
})

export type Category = z.infer<typeof CategorySchema>
export type CategoryResponse = z.infer<typeof CategoryResponseSchema>

export async function getCategories(): Promise<CategoryResponse> {
  try {
    const response = await api.get('/categories/getAll')
    return CategoryResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid response format from server', { errors: error.errors })
      throw new Error('Invalid response format from server')
    }
    logger.error('Failed to fetch categories', error)
    throw error
  }
}
