<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form @submit.prevent="handleSubmit" novalidate>
      <!-- Category Select -->
      <div class="form-group">
        <label for="categoryId">{{ $t('newProduct.category') }}</label>
        <select id="categoryId" v-model.number="product.categoryId" required>
          <option :value="null" disabled>{{ $t('newProduct.selectCategory') }}</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.icon + ' ' + category.name }}
          </option>
        </select>
        <p v-if="categoryError" class="error-message">{{ categoryError }}</p>
        <p v-if="errors.categoryId" class="field-error-message">{{ errors.categoryId }}</p>
      </div>

      <!-- Title -->
      <div class="form-group">
        <label for="name">{{ $t('newProduct.productTitle') }}</label>
        <input id="name" v-model.trim="product.title" required type="text" />
        <p v-if="errors.title" class="field-error-message">{{ errors.title }}</p>
      </div>

      <!-- Description -->
      <div class="form-group">
        <label for="description">{{ $t('newProduct.description') }}</label>
        <textarea
          id="description"
          v-model.trim="product.description"
          auto-resize
          required
        ></textarea>
        <p v-if="errors.description" class="field-error-message">{{ errors.description }}</p>
      </div>

      <!-- Price -->
      <div class="form-group">
        <label for="price">{{ $t('newProduct.price') }}</label>
        <input id="price" v-model.number="product.price" required step="1" type="number" />
        <p v-if="errors.price" class="field-error-message">{{ errors.price }}</p>
      </div>

      <!-- Postal Code -->
      <div class="form-group">
        <label for="postalCode">{{ $t('newProduct.postalCode') }}</label>
        <input
          id="postalCode"
          v-model="product.postalCode"
          required
          type="text"
          pattern="[0-9]{4}"
          minlength="4"
          maxlength="4"
          placeholder="7032"
          @input="formatPostalCode"
        />
        <p v-if="errors.postalCode" class="field-error-message">{{ errors.postalCode }}</p>
      </div>

      <!-- Images -->
      <div class="form-group">
        <label for="images">{{ $t('newProduct.images') }}</label>
        <input id="images" accept="image/*" multiple type="file" @change="handleImageUpload" />
        <!-- Preview area for multiple images -->
        <div v-if="product.imageUrls.length > 0" class="image-preview-grid">
          <div v-for="(url, index) in product.imageUrls" :key="index" class="image-preview-item">
            <img :src="url" alt="Product Preview" class="image-preview" />
            <button class="remove-image-btn" type="button" @click="removeImage(index)">×</button>
          </div>
        </div>
      </div>

      <!-- Allow Vipps -->
      <div class="form-group toggle-group">
        <input id="vipps" v-model="product.allowVippsBuy" type="checkbox" />
        <label class="toggle-label" for="vipps">{{ $t('newProduct.vipps') }}</label>
      </div>

      <!-- Submit Button -->
      <button :disabled="isLoading" type="submit">
        {{ isLoading ? $t('newProduct.creating') : $t('newProduct.createButton') }}
      </button>

      <!-- General Error Message (for API errors) -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { createItem } from '@/service/itemService'
import { getCategories } from '@/service/categoryService'
import { logger } from '@/utils/logger'
import { useI18n } from 'vue-i18n' // Import useI18n for translations

// Define reactive state
const product = ref({
  categoryId: null,
  postalCode: '',
  title: '',
  description: '',
  price: null,
  allowVippsBuy: true,
  imageFiles: [], // Store File objects here
  imageUrls: [], // Store preview URLs here
})

const isLoading = ref(false)
const errorMessage = ref('') // For general/API errors
const categories = ref([])
const categoryError = ref('')
const router = useRouter()
const { t } = useI18n() // Get the translation function

// Ref for field-specific validation errors
const errors = ref({
  categoryId: '',
  title: '',
  description: '',
  price: '',
  postalCode: '',
})

// Cleanup object URLs on component unmount or when urls change
watch(
  () => [...product.value.imageUrls], // Watch a copy to detect changes
  (newUrls, oldUrls) => {
    if (oldUrls) {
      oldUrls.forEach((url) => {
        if (!newUrls.includes(url)) {
          // Only revoke if it's truly removed, not just reordered/replaced
          URL.revokeObjectURL(url)
          logger.debug(`Revoked object URL: ${url}`)
        }
      })
    }
  },
  { deep: true }, // Necessary if watching nested properties, though array copy works here
)

// Fetch categories on component mount
onMounted(async () => {
  try {
    categories.value = await getCategories()
    logger.info('Categories loaded successfully')
  } catch (error) {
    logger.error('Failed to load categories in component:', error)
    categoryError.value = error.message || 'Could not load categories.'
  }
})

// Handle image selection for multiple files
const handleImageUpload = (event) => {
  const files = event.target.files
  if (!files) return

  // Clear previous selections and revoke old URLs
  product.value.imageUrls.forEach(URL.revokeObjectURL)
  product.value.imageFiles = []
  product.value.imageUrls = []

  for (const file of files) {
    if (file.type.startsWith('image/')) {
      product.value.imageFiles.push(file)
      const url = URL.createObjectURL(file)
      product.value.imageUrls.push(url)
      logger.debug(`Created object URL: ${url} for file: ${file.name}`)
    }
  }
  // Reset the file input value so the change event fires even if the same files are selected again
  event.target.value = ''
}

// Remove an image by index
const removeImage = (index) => {
  if (index >= 0 && index < product.value.imageUrls.length) {
    const urlToRemove = product.value.imageUrls[index]
    product.value.imageFiles.splice(index, 1)
    product.value.imageUrls.splice(index, 1)
    // No need to revoke here, the watch effect handles it
  }
}

// Validate the form before submission
const validateForm = () => {
  // Clear previous errors
  errors.value = {
    categoryId: '',
    title: '',
    description: '',
    price: '',
    postalCode: '',
  }

  let isValid = true

  if (!product.value.categoryId) {
    errors.value.categoryId = t('validation.required', { field: t('newProduct.category') })
    isValid = false
  }
  if (!product.value.title) {
    errors.value.title = t('validation.required', { field: t('newProduct.productTitle') })
    isValid = false
  }
  if (!product.value.description) {
    errors.value.description = t('validation.required', { field: t('newProduct.description') })
    isValid = false
  }
  if (product.value.price === null || product.value.price <= 0) {
    errors.value.price = t('validation.positiveNumber', { field: t('newProduct.price') })
    isValid = false
  }
  if (product.value.postalCode === '') {
    errors.value.postalCode = t('validation.required', { field: t('newProduct.postalCode') })
    isValid = false
  }
  if (product.value.postalCode.length !== 4) {
    errors.value.postalCode = t('validation.postalCode', { field: t('newProduct.postalCode') })
    isValid = false
  }

  return isValid
}

// Handle form submission
const handleSubmit = async () => {
  errorMessage.value = '' // Clear general errors

  if (!validateForm()) {
    logger.warn('Form validation failed', errors.value)
    return // Stop submission if validation fails
  }

  isLoading.value = true

  try {
    const itemData = {
      categoryId: product.value.categoryId,
      postalCode: product.value.postalCode,
      title: product.value.title,
      description: product.value.description,
      price: product.value.price,
      allowVippsBuy: product.value.allowVippsBuy,
    }

    // Use the imageFiles array
    const images = product.value.imageFiles

    logger.debug('Submitting new product:', { itemData, imageCount: images.length })

    // Call the service function
    const createdItem = await createItem(itemData, images)

    logger.info('Product created successfully:', createdItem)
    // TODO: Add a success message/toast notification

    // Navigate to the new item's detail page (assuming route name 'ItemDetail')
    // You might need to adjust the route name and parameter based on your router setup
    router.push({ name: 'product', params: { id: createdItem.id } })
  } catch (error) {
    logger.error('Failed to create product:', error)
    errorMessage.value = error.message || 'An unexpected error occurred. Please try again.'
    // TODO: Provide more specific error messages based on error type/response
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.new-product-view {
  max-width: 600px;
  margin: 40px auto;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

input[type='text'],
input[type='number'],
textarea,
input[type='file'] {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid lightgray;
  border-radius: 6px;
  box-shadow:
    inset 0 1px 4px rgba(0, 0, 0, 0.075),
    0 0 0 rgba(0, 0, 0, 0);
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;
}

input[type='text']:focus,
input[type='number']:focus,
textarea:focus,
input[type='file']:focus {
  outline: none;
  border-color: gray;
  box-shadow:
    inset 0 0 0 rgba(0, 0, 0, 0.075),
    0 2px 8px rgba(0, 0, 0, 0.2);
}

.image-preview-container {
  margin-top: 10px;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
  margin-top: 10px;
}

.image-preview-item {
  position: relative;
  border: 1px solid lightgray;
  border-radius: 6px;
  overflow: hidden; /* Ensure button stays within bounds */
}

.image-preview {
  display: block; /* Remove extra space below image */
  width: 100%;
  height: 100px; /* Fixed height for grid items */
  object-fit: cover; /* Cover the area, cropping if needed */
}

.remove-image-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 12px;
  line-height: 18px; /* Center the 'x' */
  text-align: center;
  cursor: pointer;
  padding: 0;
}

.remove-image-btn:hover {
  background-color: rgba(255, 0, 0, 0.8);
}

.toggle-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-group input[type='checkbox'] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}
.toggle-group label {
  margin: 4px 0; /* Align label vertically */
}

button {
  width: 100%;
  padding: 12px;
  color: #fff;
  background-color: #007bff; /* Example primary color */
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
  margin-top: 10px;
}

button:hover:not(:disabled) {
  background-color: #0056b3; /* Darker shade on hover */
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

textarea {
  resize: vertical;
  min-height: 100px; /* Provide a minimum height */
}

.error-message {
  color: #dc3545; /* Standard error color */
  margin-top: 15px;
  text-align: center;
}

/* Optional: Add styles for focusing on invalid fields if needed */
input:invalid,
textarea:invalid,
select:invalid {
  border-color: #dc3545;
}

.field-error-message {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 4px;
}

/* Add red border to invalid fields *after* submission attempt */
input:invalid,
textarea:invalid,
select:invalid {
  /* Remove default browser invalid styling if needed */
}

input.invalid, /* Custom class for fields with errors */
textarea.invalid,
select.invalid {
  border-color: #dc3545;
}

/* Style adjustments for spacing */
.form-group p {
  min-height: 1.2em; /* Reserve space for error messages to prevent layout shifts */
}
</style>
