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
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid email or password' },
    }).as('loginRequest')

    cy.get('[data-test="email-input"]').type('test@example.com')
    cy.get('[data-test="password-input"]').type('wrongpassword')
    cy.get('[data-test="login-button"]').click()

    cy.wait('@loginRequest')
    cy.get('[data-test="login-error"]').should('be.visible')
  })
})
