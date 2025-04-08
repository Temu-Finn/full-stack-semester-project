<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form @submit.prevent="handleSubmit">
      <!-- Category ID -->
      <div class="form-group">
        <label for="categoryId">{{ $t('newProduct.category') }}</label>
        <!-- TODO: Replace with a dropdown/select component fetching actual categories -->
        <input id="categoryId" v-model.number="product.categoryId" required type="number" />
      </div>

      <!-- Title -->
      <div class="form-group">
        <label for="name">{{ $t('newProduct.productTitle') }}</label>
        <input id="name" v-model="product.title" required type="text" />
      </div>

      <!-- Description -->
      <div class="form-group">
        <label for="description">{{ $t('newProduct.description') }}</label>
        <textarea id="description" v-model="product.description" auto-resize required></textarea>
      </div>

      <!-- Price -->
      <div class="form-group">
        <label for="price">{{ $t('newProduct.price') }}</label>
        <input id="price" v-model.number="product.price" required step="1" type="number" />
      </div>

      <!-- Postal Code -->
      <div class="form-group">
        <label for="postalCode">{{ $t('newProduct.postalCode') }}</label>
        <input id="postalCode" v-model.number="product.postalCode" required type="number" />
      </div>

      <!-- Image -->
      <div class="form-group">
        <label for="image">{{ $t('newProduct.image') }}</label>
        <input id="image" accept="image/*" type="file" @change="handleImageUpload" />
        <div v-if="product.imageUrl" class="image-preview-container">
          <img :src="product.imageUrl" alt="Product Preview" class="image-preview" />
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

      <!-- Error Message -->
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createItem } from '@/service/itemService'
import { logger } from '@/utils/logger'

// Define reactive state
const product = ref({
  categoryId: null,
  postalCode: null,
  title: '',
  description: '',
  price: null,
  allowVippsBuy: true, // Defaulting to true as checkbox is initially checked
  imageFile: null, // Store the File object here
  imageUrl: '', // For preview
})

const isLoading = ref(false)
const errorMessage = ref('')
const router = useRouter()

// Handle image selection
const handleImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    product.value.imageFile = file
    product.value.imageUrl = URL.createObjectURL(file)
  } else {
    product.value.imageFile = null
    product.value.imageUrl = ''
  }
}

// Handle form submission
const handleSubmit = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    // Prepare data according to CreateItemRequest schema
    const itemData = {
      categoryId: product.value.categoryId,
      postalCode: product.value.postalCode,
      title: product.value.title,
      description: product.value.description,
      price: product.value.price,
      allowVippsBuy: product.value.allowVippsBuy,
    }

    // Prepare image array
    const images = product.value.imageFile ? [product.value.imageFile] : []

    logger.debug('Submitting new product:', { itemData, hasImage: images.length > 0 })

    // Call the service function
    const createdItem = await createItem(itemData, images)

    logger.info('Product created successfully:', createdItem)
    // TODO: Add a success message/toast notification

    // Navigate to the new item's detail page (assuming route name 'ItemDetail')
    // You might need to adjust the route name and parameter based on your router setup
    router.push({ name: 'ItemDetail', params: { id: createdItem.id } })
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

.image-preview {
  width: 100%; /* Limit preview width */
  max-height: 300px; /* Limit preview height */
  object-fit: contain; /* Maintain aspect ratio */
  border: 1px solid lightgray;
  border-radius: 6px;
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
textarea:invalid {
  border-color: #dc3545;
}
</style>
