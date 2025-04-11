import { describe, it, expect, vi, beforeEach } from 'vitest'
import { loginUser, signupUser } from '../authService'
import api from '@/config/api'

// Mock the api module
vi.mock('@/config/api', () => ({
  default: {
    post: vi.fn(),
  },
}))

describe('authService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  describe('loginUser', () => {
    it('should successfully login and return user data', async () => {
      // Arrange
      const mockResponse = {
        data: {
          userId: 1,
          name: 'Test User',
          email: 'test@example.com',
          joinedAt: '2025-04-10T12:00:00.000Z',
          admin: false,
          token: 'mock-jwt-token',
          expiresIn: 3600,
        },
      }

      vi.mocked(api.post).mockResolvedValueOnce(mockResponse)

      // Act
      const result = await loginUser('test@example.com', 'password123')

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/login', {
        email: 'test@example.com',
        password: 'password123',
      })
      expect(result).toEqual({
        userId: 1,
        name: 'Test User',
        email: 'test@example.com',
        joinedAt: '2025-04-10T12:00:00.000Z',
        admin: false,
        token: 'mock-jwt-token',
        expiresIn: 3600,
      })
    })

    it('should throw an error for invalid email format', async () => {
      // Act & Assert
      await expect(loginUser('invalid-email', 'password123')).rejects.toThrow()
    })

    it('should propagate API errors', async () => {
      // Arrange
      const mockError = new Error('Network Error')
      vi.mocked(api.post).mockRejectedValueOnce(mockError)

      // Act & Assert
      await expect(loginUser('test@example.com', 'password123')).rejects.toThrow('Network Error')
    })
  })

  describe('signupUser', () => {
    it('should successfully create a new user account and return user data', async () => {
      // Arrange
      const mockResponse = {
        data: {
          userId: 2,
          name: 'New User',
          email: 'new@example.com',
          joinedAt: '2025-04-11T10:30:00.000Z',
          admin: false,
          token: 'mock-jwt-token-new',
          expiresIn: 3600,
        },
      }

      vi.mocked(api.post).mockResolvedValueOnce(mockResponse)

      // Act
      const result = await signupUser('new@example.com', 'New User', 'password123')

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/signup', {
        email: 'new@example.com',
        name: 'New User',
        password: 'password123',
      })
      expect(result).toEqual({
        userId: 2,
        name: 'New User',
        email: 'new@example.com',
        joinedAt: '2025-04-11T10:30:00.000Z',
        admin: false,
        token: 'mock-jwt-token-new',
        expiresIn: 3600,
      })
    })

    it('should throw an error for invalid parameters', async () => {
      // Act & Assert
      await expect(signupUser('invalid-email', 'Test User', 'password123')).rejects.toThrow()
    })
  })
})
