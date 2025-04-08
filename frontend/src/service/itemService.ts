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

// Schema for Search Query Parameters
const SearchItemParamsSchema = z
  .object({
    searchText: z.string().optional(),
    categoryId: z.number().int().positive().optional(),
    county: z.string().optional(),
    municipality: z.string().optional(),
    city: z.string().optional(),
    latitude: z.number().min(-90).max(90).optional(),
    longitude: z.number().min(-180).max(180).optional(),
    maxDistanceKm: z.number().min(0).optional(),
    page: z.number().int().min(0).optional(),
    size: z.number().int().positive().optional(),
    // Allow an array of sort strings like ["price,asc", "updatedAt,desc"]
    sort: z
      .string()
      .regex(/^[a-zA-Z]+,(asc|desc)$/)
      .array()
      .optional(),
  })
  .refine(
    (data) => {
      const hasLat = data.latitude !== undefined
      const hasLon = data.longitude !== undefined
      const hasDist = data.maxDistanceKm !== undefined
      // If any distance param is present, all must be present
      return !(hasLat || hasLon || hasDist) || (hasLat && hasLon && hasDist)
    },
    {
      message: 'If using distance search, latitude, longitude, and maxDistanceKm are all required.',
      // Optional: Specify path for better error association, though maybe less critical here
      // path: ["latitude", "longitude", "maxDistanceKm"],
    },
  )

// Schema for the paginated response structure (metadata part)
// Adjusted based on example: { content: [...], page: { size, number, totalElements, totalPages } }
const PageMetadataSchema = z.object({
  size: z.number().int().positive(),
  number: z.number().int().min(0), // current page number (0-indexed)
  totalElements: z.number().int().min(0),
  totalPages: z.number().int().min(0),
})

// Schema for the full search response, combining page metadata and item content
// Updated structure: nests pagination under 'page'
const SearchItemsResponseSchema = z.object({
  content: z.array(ItemCardSchema),
  page: PageMetadataSchema,
})

// Type exports for use in components
export type ItemCard = z.infer<typeof ItemCardSchema>
export type RecommendedItemsResponse = z.infer<typeof RecommendedItemsResponseSchema>
export type SearchItemParams = z.infer<typeof SearchItemParamsSchema>
// Updated type to reflect nested page structure
export type SearchItemsResponse = z.infer<typeof SearchItemsResponseSchema>

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

/**
 * Searches for items based on specified criteria.
 * @param params - The search parameters (filters, pagination, sorting).
 * @returns Promise containing the paginated search results.
 * @throws Error if the API request fails or response validation fails.
 */
export async function searchItems(params: SearchItemParams): Promise<SearchItemsResponse> {
  // Validate input params using Zod schema before making the API call
  try {
    SearchItemParamsSchema.parse(params)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid search parameters provided', { errors: error.errors })
      // Re-throw a more specific error or handle as needed
      throw new Error(`Invalid search parameters: ${error.errors.map((e) => e.message).join(', ')}`)
    }
    // Unexpected error during validation
    throw error
  }

  // Construct query parameters, filtering out undefined values
  const queryParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined) {
      if (key === 'sort' && Array.isArray(value)) {
        // Append each sort criterion individually
        value.forEach((sortValue) => queryParams.append(key, sortValue))
      } else {
        queryParams.append(key, String(value))
      }
    }
  })

  const queryString = queryParams.toString()
  const url = `/item/search${queryString ? '?' + queryString : ''}`
  logger.debug(`Searching items with URL: ${url}`)

  try {
    const response = await api.get(url)
    logger.debug('Received search items response', response.data)
    // Validate the response from the server
    return SearchItemsResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid search response format from server', { errors: error.errors })
      throw new Error('Invalid search response format from server')
    }
    // Log other errors (e.g., network errors, 500 errors from backend)
    logger.error('Failed to fetch search results', error)
    // Rethrow the original error or a custom error
    throw error
  }
}
