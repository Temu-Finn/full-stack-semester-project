import { z } from 'zod'
import api from '@/config/api'
import { logger } from '@/utils/logger'

const CategorySchema = z.object({
  id: z.number(),
  name: z.string(),
  icon: z.string(),
  description: z.string(),
})

const ImageSchema = z.object({
  id: z.number(),
  dataURL: z.string(),
})

const LocationSchema = z.object({
  latitude: z.number(),
  longitude: z.number(),
})

const ItemStatusSchema = z.enum(['available', 'reserved', 'sold', 'archived', 'bought'])

const ItemCardSchema = z.object({
  id: z.number(),
  title: z.string(),
  price: z.number(),
  municipality: z.string(),
  image: ImageSchema.nullish(),
  location: LocationSchema.nullish(),
  status: ItemStatusSchema,
  updatedAt: z.string().datetime(),
})

const CreateItemRequestSchema = z.object({
  categoryId: z.number().int().positive(),
  postalCode: z.string().min(1, 'Postal code is required'),
  title: z.string().min(1, 'Title is required'),
  description: z.string().min(1, 'Description is required'),
  price: z.number().positive('Price must be positive'),
  allowVippsBuy: z.boolean(),
})

const CompleteItemSchema = z.object({
  id: z.number(),
  sellerId: z.number(),
  category: CategorySchema,
  postalCode: z.string(),
  title: z.string(),
  description: z.string(),
  price: z.number(),
  purchasePrice: z.number().nullable(),
  buyerId: z.number().nullable(),
  location: LocationSchema.nullable(),
  allowVippsBuy: z.boolean(),
  primaryImageId: z.number().nullable(),
  status: ItemStatusSchema,
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime(),
  municipality: z.string(),
  images: z.array(ImageSchema).optional(),
})

const RecommendedItemsResponseSchema = z.object({
  items: z.array(ItemCardSchema),
})

const SearchItemParamsSchema = z
  .object({
    searchText: z.string().optional(),
    categoryId: z.number().int().nonnegative().optional(),
    county: z.string().optional(),
    municipality: z.string().optional(),
    city: z.string().optional(),
    latitude: z.number().min(-90).max(90).optional(),
    longitude: z.number().min(-180).max(180).optional(),
    maxDistanceKm: z.number().min(0).optional(),
    page: z.number().int().min(0).optional(),
    size: z.number().int().positive().optional(),
    sort: z.string().array().optional(),
  })
  .refine(
    (data) => {
      const hasLat = data.latitude !== undefined
      const hasLon = data.longitude !== undefined
      const hasDist = data.maxDistanceKm !== undefined
      return !(hasLat || hasLon || hasDist) || (hasLat && hasLon && hasDist)
    },
    {
      message: 'If using distance search, latitude, longitude, and maxDistanceKm are all required.',
    },
  )

const PageMetadataSchema = z.object({
  size: z.number().int().positive(),
  number: z.number().int().min(0), // current page number (0-indexed)
  totalElements: z.number().int().min(0),
  totalPages: z.number().int().min(0),
})

const MunicipalitySchema = z.object({
  name: z.string(),
  count: z.number().int().nonnegative(),
})

const CountySchema = z.object({
  name: z.string(),
  count: z.number().int().nonnegative(),
  municipalities: z.array(MunicipalitySchema),
})

const PageSchema = z.object({
  content: z.array(ItemCardSchema),
  page: PageMetadataSchema,
})

const SearchItemsResponseSchema = z.object({
  counties: z.array(CountySchema),
  result: PageSchema,
})

export type ItemCard = z.infer<typeof ItemCardSchema>
export type RecommendedItemsResponse = z.infer<typeof RecommendedItemsResponseSchema>

export type SearchItemParams = z.infer<typeof SearchItemParamsSchema>
export type SearchItemsResponse = z.infer<typeof SearchItemsResponseSchema>

export type CreateItemRequest = z.infer<typeof CreateItemRequestSchema>
export type CompleteItem = z.infer<typeof CompleteItemSchema>
export type Location = z.infer<typeof LocationSchema>
export type ItemStatus = z.infer<typeof ItemStatusSchema>

export async function getItem(id: number): Promise<CompleteItem> {
  try {
    const response = await api.get(`/item/${id}`)
    return CompleteItemSchema.parse(response.data)
  } catch (error) {
    if (error instanceof z.ZodError) {
      logger.error('Invalid response format from server', { errors: error.errors })
      throw new Error('Invalid response format from server')
    }
    throw error
  }
}

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
    throw error
  }

  const queryParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined) {
      if (key === 'sort' && Array.isArray(value)) {
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

  formData.append(
    'item',
    new Blob([JSON.stringify(itemData)], {
      type: 'application/json',
    }),
  )

  images.forEach((file) => {
    formData.append('image', file)
  })

  logger.debug('Creating new item with data:', { itemData, imageCount: images.length })

  try {
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

/**
 * Fetches items associated with a specific user.
 * @param userId - The ID of the user to fetch items for.
 * @returns Promise containing an array of ItemCard objects.
 * @throws Error if the API request fails or response validation fails.
 */
export async function getItemsOfUser(userId: number): Promise<ItemCard[]> {
  const response = await api.get(`/item/user/${userId}`)
  return ItemCardSchema.array().parse(response.data)
}

/**
 * Deletes an item by its ID.
 * @param id - The ID of the item to delete.
 * @returns Promise that resolves when the item is deleted.
 * @throws Error if the API request fails.
 */
export async function deleteItem(id: number): Promise<void> {
  try {
    await api.delete(`/item/${id}`)
  } catch (error) {
    logger.error('Failed to delete item', error)
    throw error
  }
}
