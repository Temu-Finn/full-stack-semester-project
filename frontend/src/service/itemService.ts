import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

const ImageSchema = z.object({
  id: z.number(),
  dataURL: z.string(),
})

const LocationSchema = z.object({
  latitude: z.number(),
  longitude: z.number(),
})

const ItemCardSchema = z.object({
  id: z.number(),
  title: z.string(),
  price: z.number(),
  municipality: z.string(),
  image: ImageSchema.nullish(),
  location: LocationSchema.nullish(),
  status: z.enum(['available', 'reserved', 'sold', 'archived']),
  updatedAt: z.string(),
})

const CreateItemRequestSchema = z.object({
  categoryId: z.number().int().positive(),
  postalCode: z.string().min(1, 'Postal code is required'),
  title: z.string().min(1, 'Title is required'),
  description: z.string().min(1, 'Description is required'),
  price: z.number().positive('Price must be positive'),
  allowVippsBuy: z.boolean(),
})

// Updated schema for the complete item details returned after creation
const CompleteItemSchema = z.object({
  id: z.number(),
  sellerId: z.number(),
  categoryId: z.number(),
  postalCode: z.string(), // API returns postalCode as a string
  title: z.string(),
  description: z.string(),
  price: z.number(),
  purchasePrice: z.number().nullable(),
  buyerId: z.number().nullable(),
  location: LocationSchema.nullable(), // Use reusable LocationSchema
  allowVippsBuy: z.boolean(),
  primaryImageId: z.number().nullable(),
  status: z.string(),
  createdAt: z.string().datetime(), // Validate as ISO date string
  updatedAt: z.string().datetime(), // Validate as ISO date string
  municipality: z.string(),
  images: z.array(ImageSchema).optional(), // Images array might be optional or empty
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

const PageMetadataSchema = z.object({
  size: z.number().int().positive(),
  number: z.number().int().min(0), // current page number (0-indexed)
  totalElements: z.number().int().min(0),
  totalPages: z.number().int().min(0),
})

const SearchItemsResponseSchema = z.object({
  content: z.array(ItemCardSchema),
  page: PageMetadataSchema,
})

// Type exports for use in components
export type ItemCard = z.infer<typeof ItemCardSchema>
export type RecommendedItemsResponse = z.infer<typeof RecommendedItemsResponseSchema>

export type SearchItemParams = z.infer<typeof SearchItemParamsSchema>
export type SearchItemsResponse = z.infer<typeof SearchItemsResponseSchema>

export type CreateItemRequest = z.infer<typeof CreateItemRequestSchema>
export type CompleteItem = z.infer<typeof CompleteItemSchema>

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
  try {
    SearchItemParamsSchema.parse(params)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid search parameters provided', { errors: error.errors })
      throw new Error(`Invalid search parameters: ${error.errors.map((e) => e.message).join(', ')}`)
    }
    // Unexpected error during validation
    throw error
  }

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
    return SearchItemsResponseSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid search response format from server', { errors: error.errors })
      throw new Error('Invalid search response format from server')
    }
    // Log other errors (e.g., network errors, 500 errors from backend)
    logger.error('Failed to fetch search results', error)
    throw error
  }
}

/**
 * Creates a new item with optional images.
 * @param itemData - The data for the item to create (excluding images).
 * @param images - An array of File objects for the item images.
 * @returns Promise containing the details of the created item.
 * @throws Error if validation fails or the API request fails.
 */
export async function createItem(
  itemData: CreateItemRequest,
  images: File[] = [],
): Promise<CompleteItem> {
  try {
    CreateItemRequestSchema.parse(itemData)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid item data provided', { errors: error.errors })
      throw new Error(`Invalid item data: ${error.errors.map((e) => e.message).join(', ')}`)
    }
    throw error
  }

  const formData = new FormData()

  // Append item data as a JSON string blob under the key 'item'
  formData.append(
    'item',
    new Blob([JSON.stringify(itemData)], {
      type: 'application/json',
    }),
  )

  // Append each image file under the key 'image'
  images.forEach((file) => {
    formData.append('image', file)
  })

  logger.debug('Creating new item with data:', { itemData, imageCount: images.length })

  try {
    // Make the POST request with FormData
    // Axios automatically sets 'Content-Type': 'multipart/form-data' for FormData
    const response = await api.post('/item', formData)
    logger.debug('Received create item response:', response.data)

    return CompleteItemSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid response format from server after creating item', {
        errors: error.errors,
      })
      throw new Error('Invalid response format from server')
    }
    logger.error('Failed to create item', error)
    throw error
  }
}
