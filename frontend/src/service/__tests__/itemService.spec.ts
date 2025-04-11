import { describe, it, expect, vi, beforeEach } from 'vitest'
import { getItem, getRecommendedItems, searchItems, reserveItem } from '../itemService'
import api from '@/config/api'
import { logger } from '@/utils/logger'

vi.mock('@/config/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}))

vi.mock('@/utils/logger', () => ({
  logger: {
    error: vi.fn(),
    debug: vi.fn(),
  },
}))

describe('itemService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  describe('getItem', () => {
    it('should fetch an item by ID successfully', async () => {
      const mockItem = {
        id: 123,
        sellerId: 456,
        category: {
          id: 1,
          name: 'Electronics',
          icon: 'laptop',
          description: 'Electronic items',
        },
        postalCode: '1234',
        title: 'Test Item',
        description: 'A test item description',
        price: 100,
        purchasePrice: null,
        buyerId: null,
        location: {
          latitude: 63.4,
          longitude: 10.4,
        },
        allowVippsBuy: true,
        primaryImageId: null,
        status: 'available',
        createdAt: '2025-04-10T12:00:00.000Z',
        updatedAt: '2025-04-10T12:00:00.000Z',
        municipality: 'Trondheim',
        images: [],
      }

      vi.mocked(api.get).mockResolvedValueOnce({ data: mockItem })

      const result = await getItem(123)

      expect(api.get).toHaveBeenCalledWith('/item/123')
      expect(result).toEqual(mockItem)
    })

    it('should handle validation errors properly', async () => {
      const invalidItem = {
        id: 123,
      }

      vi.mocked(api.get).mockResolvedValueOnce({ data: invalidItem })

      const errorSpy = vi.spyOn(logger, 'error')

      await expect(getItem(123)).rejects.toThrow('Invalid response format from server')
      expect(errorSpy).toHaveBeenCalledWith(
        'Invalid response format from server',
        expect.objectContaining({ errors: expect.any(Array) }),
      )
    })

    it('should propagate API errors', async () => {
      const mockError = new Error('Network Error')
      vi.mocked(api.get).mockRejectedValueOnce(mockError)

      await expect(getItem(123)).rejects.toThrow('Network Error')
    })
  })

  describe('getRecommendedItems', () => {
    it('should fetch recommended items successfully', async () => {
      const mockRecommendedItems = {
        items: [
          {
            id: 1,
            title: 'Item 1',
            price: 100,
            municipality: 'Oslo',
            image: { id: 1, dataURL: 'data:image/png;base64,abc' },
            location: { latitude: 59.9, longitude: 10.8 },
            status: 'available',
            updatedAt: '2025-04-10T12:00:00.000Z',
            allowVippsBuy: true,
          },
          {
            id: 2,
            title: 'Item 2',
            price: 200,
            municipality: 'Bergen',
            image: null,
            location: { latitude: 60.4, longitude: 5.3 },
            status: 'available',
            updatedAt: '2025-04-10T12:00:00.000Z',
            allowVippsBuy: false,
          },
        ],
      }

      vi.mocked(api.get).mockResolvedValueOnce({ data: mockRecommendedItems })

      const result = await getRecommendedItems()

      expect(api.get).toHaveBeenCalledWith('/item/recommended')
      expect(result).toEqual(mockRecommendedItems)
      expect(logger.debug).toHaveBeenCalledWith(
        'Received recommended items response',
        mockRecommendedItems,
      )
    })
  })

  describe('searchItems', () => {
    it('should search items with valid parameters', async () => {
      const searchParams = {
        searchText: 'test',
        categoryId: 1,
        municipality: 'Oslo',
        page: 0,
        size: 10,
      }

      const mockSearchResults = {
        counties: [
          {
            name: 'Oslo',
            count: 5,
            municipalities: [
              {
                name: 'Oslo',
                count: 5,
              },
            ],
          },
        ],
        result: {
          content: [
            {
              id: 1,
              title: 'Test Item 1',
              price: 100,
              municipality: 'Oslo',
              image: { id: 1, dataURL: 'data:image/png;base64,abc' },
              location: { latitude: 59.9, longitude: 10.8 },
              status: 'available',
              updatedAt: '2025-04-10T12:00:00.000Z',
              allowVippsBuy: true,
            },
          ],
          page: {
            size: 10,
            number: 0,
            totalElements: 1,
            totalPages: 1,
          },
        },
      }

      vi.mocked(api.get).mockResolvedValueOnce({ data: mockSearchResults })

      const result = await searchItems(searchParams)

      expect(api.get).toHaveBeenCalledWith(
        '/item/search?searchText=test&categoryId=1&municipality=Oslo&page=0&size=10',
      )
      expect(result).toEqual(mockSearchResults)
    })

    it('should validate search parameters', async () => {
      const invalidParams = {
        latitude: 60,
      }

      await expect(searchItems(invalidParams as any)).rejects.toThrow(/Invalid search parameters/)
    })
  })

  describe('reserveItem', () => {
    it('should reserve an item successfully', async () => {
      const mockReservedItem = {
        id: 123,
        sellerId: 456,
        category: {
          id: 1,
          name: 'Electronics',
          icon: 'laptop',
          description: 'Electronic items',
        },
        postalCode: '1234',
        title: 'Test Item',
        description: 'A test item description',
        price: 100,
        purchasePrice: null,
        buyerId: 789,
        location: {
          latitude: 63.4,
          longitude: 10.4,
        },
        allowVippsBuy: true,
        primaryImageId: null,
        status: 'reserved',
        createdAt: '2025-04-10T12:00:00.000Z',
        updatedAt: '2025-04-11T12:00:00.000Z',
        municipality: 'Trondheim',
        images: [],
      }

      vi.mocked(api.post).mockResolvedValueOnce({ data: mockReservedItem })

      const result = await reserveItem(123)

      expect(api.post).toHaveBeenCalledWith('/item/reserve/123')
      expect(result).toEqual(mockReservedItem)
    })

    it('should handle reservation errors', async () => {
      const mockError = new Error('Item already reserved')
      vi.mocked(api.post).mockRejectedValueOnce(mockError)

      await expect(reserveItem(123)).rejects.toThrow('Item already reserved')
      expect(logger.error).toHaveBeenCalledWith('Failed to reserve item', mockError)
    })
  })
})
