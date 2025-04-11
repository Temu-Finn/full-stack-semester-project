describe('Login Flow', () => {
  beforeEach(() => {
    // Reset any state before each test
    cy.visit('/login')
  })

  it('should display login form', () => {
    cy.get('[data-test="login-form"]').should('be.visible')
    cy.get('[data-test="email-input"]').should('be.visible')
    cy.get('[data-test="password-input"]').should('be.visible')
    cy.get('[data-test="login-button"]').should('be.visible')
  })

  it('should show validation error for invalid email', () => {
    cy.get('[data-test="email-input"]').type('invalid-email')
    cy.get('[data-test="login-button"]').click()
    cy.get('[data-test="email-error"]').should('be.visible')
  })

  it('should show error message for incorrect credentials', () => {
    // Intercept API request and mock failed response
    cy.intercept('POST', '/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid email or password' },
    }).as('loginRequest')

    cy.get('[data-test="email-input"]').type('test@example.com')
    cy.get('[data-test="password-input"]').type('wrongpassword')
    cy.get('[data-test="login-button"]').click()

    cy.wait('@loginRequest')
    cy.get('[data-test="login-error"]').should('be.visible')
  })

  it('should successfully log in with correct credentials', () => {
    // Mock successful login response
    cy.intercept('POST', '/auth/login', {
      statusCode: 200,
      body: {
        userId: 1,
        name: 'Test User',
        email: 'test@example.com',
        joinedAt: '2025-04-10T12:00:00.000Z',
        admin: false,
        token: 'mock-jwt-token',
        expiresIn: 3600,
      },
    }).as('loginRequest')

    cy.get('[data-test="email-input"]').type('test@example.com')
    cy.get('[data-test="password-input"]').type('password123')
    cy.get('[data-test="login-button"]').click()

    cy.wait('@loginRequest')

    // Verify redirect to home page after successful login
    cy.url().should('include', '/')

    // Verify user is logged in (UI elements that should appear when logged in)
    cy.get('[data-test="user-menu"]').should('be.visible')
  })
})
