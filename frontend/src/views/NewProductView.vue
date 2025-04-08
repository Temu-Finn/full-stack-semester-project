<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form @submit.prevent="handleSubmit" novalidate>
      <!-- Category Select -->
      <div class="form-group">
        <label for="categoryId">{{ $t('newProduct.category') }}</label>
        <select
          id="categoryId"
          v-model.number="product.categoryId"
          :class="{ 'is-invalid': errors.categoryId }"
          required
        >
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
        <input
          id="name"
          v-model.trim="product.title"
          :class="{ 'is-invalid': errors.title }"
          required
          type="text"
        />
        <p v-if="errors.title" class="field-error-message">{{ errors.title }}</p>
      </div>

      <!-- Description -->
      <div class="form-group">
        <label for="description">{{ $t('newProduct.description') }}</label>
        <textarea
          id="description"
          v-model.trim="product.description"
          :class="{ 'is-invalid': errors.description }"
          auto-resize
          required
        ></textarea>
        <p v-if="errors.description" class="field-error-message">{{ errors.description }}</p>
      </div>

      <!-- Price -->
      <div class="form-group">
        <label for="price">{{ $t('newProduct.price') }}</label>
        <input
          id="price"
          v-model.number="product.price"
          :class="{ 'is-invalid': errors.price }"
          required
          step="1"
          type="number"
          min="0"
        />
        <p v-if="errors.price" class="field-error-message">{{ errors.price }}</p>
      </div>

      <!-- Postal Code -->
      <div class="form-group">
        <label for="postalCode">{{ $t('newProduct.postalCode') }}</label>
        <input
          id="postalCode"
          v-model="product.postalCode"
          :class="{ 'is-invalid': errors.postalCode }"
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
        <!-- Styled label acts as the file input trigger -->
        <label
          for="images"
          class="file-input-label"
          :class="{ 'window-dragging': isWindowDragging }"
        >
          {{
            product.imageUrls.length > 0
              ? $t('newProduct.addImages')
              : $t('newProduct.selectImages')
          }}
        </label>
        <div v-if="product.imageUrls.length > 0" class="image-preview-grid">
          <div
            v-for="(url, index) in product.imageUrls"
            :key="url"
            class="image-preview-item"
            :class="{
              'primary-image': index === 0,
              dragging: index === draggedIndex,
              'drop-target': index === dropIndex && index !== draggedIndex,
            }"
            draggable="true"
            @dragstart="handleImageDragStart(index)"
            @dragover.prevent="handleImageDragOver($event, index)"
            @drop.prevent="handleImageDrop(index)"
            @dragend="handleImageDragEnd"
          >
            <img :src="url" alt="Product Preview" class="image-preview" />
            <button class="remove-image-btn" type="button" @click="removeImage(index)">×</button>
            <span v-if="index === 0" class="primary-indicator">{{
              $t('newProduct.primaryIndicator')
            }}</span>
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
import { ref, onMounted, onUnmounted, watch } from 'vue'
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

const isWindowDragging = ref(false) // Track drag state for the WINDOW
const draggedIndex = ref(null)
const dropIndex = ref(null)

// Cleanup object URLs on component unmount or when urls change
watch(
  () => [...product.value.imageUrls], // Watch a copy to detect changes
  (newUrls, oldUrls) => {
    if (oldUrls) {
      oldUrls.forEach((url) => {
        if (!newUrls.includes(url)) {
          URL.revokeObjectURL(url)
          logger.debug(`Revoked object URL: ${url}`)
        }
      })
    }
  },
  { deep: true },
)

// --- Window Drag and Drop Handlers ---
const windowDragOver = (event) => {
  event.preventDefault() // Prevent default to allow drop
  isWindowDragging.value = true
}

const windowDragLeave = (event) => {
  // Check if the mouse truly left the window or entered a child that prevents drop
  if (
    event.relatedTarget === null ||
    event.target === document.documentElement ||
    event.clientY <= 0 ||
    event.clientX <= 0 ||
    event.clientX >= window.innerWidth ||
    event.clientY >= window.innerHeight
  ) {
    isWindowDragging.value = false
  }
}

const windowDrop = (event) => {
  event.preventDefault() // Prevent default file opening
  isWindowDragging.value = false
  const files = event.dataTransfer?.files
  if (files && files.length > 0) {
    logger.debug('Files dropped on window', files)
    processFiles(files) // Process the dropped files
  } else {
    logger.debug('Window drop event without files.')
  }
}

// Fetch categories and set up window listeners on component mount
onMounted(async () => {
  try {
    categories.value = await getCategories()
    logger.info('Categories loaded successfully')
  } catch (error) {
    logger.error('Failed to load categories in component:', error)
    categoryError.value = error.message || 'Could not load categories.'
  }

  // Add window event listeners
  window.addEventListener('dragover', windowDragOver)
  window.addEventListener('dragleave', windowDragLeave)
  window.addEventListener('drop', windowDrop)
  logger.debug('Window drag/drop listeners added')
})

// Clean up window listeners on component unmount
onUnmounted(() => {
  window.removeEventListener('dragover', windowDragOver)
  window.removeEventListener('dragleave', windowDragLeave)
  window.removeEventListener('drop', windowDrop)
  logger.debug('Window drag/drop listeners removed')
})

// Shared function to process files (from input or drag/drop)
const processFiles = (files) => {
  if (!files) return

  let addedCount = 0
  for (const file of files) {
    if (file.type.startsWith('image/')) {
      product.value.imageFiles.push(file)
      const url = URL.createObjectURL(file)
      product.value.imageUrls.push(url)
      logger.debug(`Created object URL: ${url} for file: ${file.name}`)
      addedCount++
    } else {
      logger.warn(`Skipped non-image file: ${file.name}`)
      // Optionally show a user message about skipped files
    }
  }
  logger.debug(`${addedCount} image(s) added.`)
}

// Handle image selection via file input click
const handleImageUpload = (event) => {
  processFiles(event.target.files)
  // Reset the file input value so the change event fires even if the same files are selected again
  event.target.value = ''
}

// Remove an image by index
const removeImage = (index) => {
  if (index >= 0 && index < product.value.imageUrls.length) {
    const urlToRemove = product.value.imageUrls[index]
    URL.revokeObjectURL(urlToRemove) // Revoke URL when removing
    product.value.imageFiles.splice(index, 1)
    product.value.imageUrls.splice(index, 1)
  }
}

// --- Start Drag and Drop for Reordering Images ---
const handleImageDragStart = (index) => {
  draggedIndex.value = index
  // Optional: Add a class to the body or element for styling
}

const handleImageDragOver = (event, index) => {
  event.preventDefault() // Necessary to allow drop
  dropIndex.value = index
  // Optional: Add visual feedback for the drop target
}

const handleImageDrop = (index) => {
  if (draggedIndex.value === null || draggedIndex.value === index) {
    return // No drop if not dragging or dropping on itself
  }

  const itemToMove = product.value.imageFiles[draggedIndex.value]
  const urlToMove = product.value.imageUrls[draggedIndex.value]

  // Remove from original position
  product.value.imageFiles.splice(draggedIndex.value, 1)
  product.value.imageUrls.splice(draggedIndex.value, 1)

  // Insert at new position
  product.value.imageFiles.splice(index, 0, itemToMove)
  product.value.imageUrls.splice(index, 0, urlToMove)

  // Reset drag state
  draggedIndex.value = null
  dropIndex.value = null
}

const handleImageDragEnd = () => {
  // Reset drag state and remove any global styling classes
  draggedIndex.value = null
  dropIndex.value = null
}
// --- End Drag and Drop for Reordering Images ---

// Validate the form before submission
const validateForm = () => {
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
  errorMessage.value = ''

  if (!validateForm()) {
    logger.warn('Form validation failed', errors.value)
    errorMessage.value = t('validation.invalid')
    return
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

    const images = product.value.imageFiles

    logger.debug('Submitting new product:', { itemData, imageCount: images.length })

    const createdItem = await createItem(itemData, images)

    logger.info('Product created successfully:', createdItem)

    router.push({ name: 'product', params: { id: createdItem.id } })
  } catch (error) {
    logger.error('Failed to create product:', error)
    errorMessage.value = error.message || 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
h1 {
  font-weight: bold;
}

.new-product-view {
  max-width: 600px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff; /* Explicit white background */
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* Softer shadow */
}

h1 {
  text-align: center;
  margin-bottom: 35px; /* Slightly more space */
  color: #333; /* Darker text */
}

.form-group {
  margin-bottom: 25px; /* Increased spacing */
  position: relative;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555; /* Slightly lighter label color */
}

/* Consistent input styling */
input[type='text'],
input[type='number'],
textarea,
select {
  width: 100%;
  padding: 12px 15px; /* Slightly more padding */
  border: 1px solid #dcdcdc; /* Lighter border */
  border-radius: 8px; /* More rounded corners */
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05); /* Subtle inset shadow */
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: #fff; /* Ensure white background */
  font-size: 1rem; /* Explicit font size */
  color: #333;
}

/* Style the select dropdown */
select {
  appearance: none; /* Remove default arrow */
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23888' class='bi bi-chevron-down' viewBox='0 0 16 16'%3E%3Cpath fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/%3E%3C/svg%3E"); /* Custom arrow */
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px 16px;
  padding-right: 40px; /* Space for the arrow */
}

/* Improve focus state */
input[type='text']:focus,
input[type='number']:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: #007bff; /* Use primary color for focus */
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(0, 123, 255, 0.2); /* Focus ring */
}

/* Styling for the file input trigger */
.file-input-label {
  display: block;
  padding: 12px 15px;
  border: 1px dashed #dcdcdc; /* Dashed border for dropzone feel */
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  background-color: #f9f9f9; /* Light background */
  color: #555;
  transition:
    border-color 0.2s ease,
    background-color 0.2s ease;
}

.file-input-label:hover {
  /* Keep hover style */
  border-color: #007bff;
  background-color: #f0f8ff;
}

/* Style the label when dragging files over the window */
.file-input-label.window-dragging {
  border-color: #007bff; /* Dashed blue border on the label */
  border-style: dashed;
  background-color: #e6f2ff; /* Slightly stronger blue background */
  box-shadow: 0 0 10px rgba(0, 123, 255, 0.3); /* Add a glow */
}

/* Hide the actual file input */
#images {
  display: none;
}

/* Refined image preview grid */
.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px; /* Increased gap */
  margin-top: 15px;
}

.image-preview-item {
  position: relative;
  border: 1px solid #eee; /* Lighter border */
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1 / 1; /* Make previews square */
  background-color: #f0f0f0; /* Placeholder background */
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease; /* Add transitions */
}

.image-preview {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px; /* Adjust position */
  right: 5px; /* Adjust position */
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px; /* Slightly larger */
  height: 24px;
  font-size: 14px; /* Adjust font size */
  line-height: 24px; /* Center 'x' */
  text-align: center;
  cursor: pointer;
  padding: 0;
  opacity: 0.8; /* Slightly transparent */
  transition:
    background-color 0.2s ease,
    opacity 0.2s ease;
}

.remove-image-btn:hover {
  background-color: rgba(220, 53, 69, 0.9); /* Use error color on hover */
  opacity: 1;
}

/* Toggle switch styling */
.toggle-group {
  display: flex;
  align-items: center;
  gap: 12px; /* Slightly more gap */
}

.toggle-group input[type='checkbox'] {
  /* Consider using a custom toggle switch component/style here for better UX */
  /* Basic styling for now */
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #007bff; /* Use primary color */
}
.toggle-group label {
  margin: 0; /* Remove default margin */
  user-select: none; /* Prevent text selection */
  cursor: pointer;
}

/* Button styling */
button[type='submit'] {
  /* Be more specific */
  width: 100%;
  padding: 14px; /* More padding */
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 8px;
  font-size: 1rem; /* Consistent font size */
  font-weight: 500;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    transform 0.1s ease;
  margin-top: 15px; /* Adjust margin */
}

button[type='submit']:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-1px); /* Subtle lift */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

button[type='submit']:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

/* Textarea specific */
textarea {
  resize: vertical;
  min-height: 120px; /* Slightly taller */
  line-height: 1.5; /* Improve readability */
}

/* Error message styling */
.error-message {
  color: #dc3545;
  margin-top: 20px; /* More space */
  text-align: center;
  font-size: 0.9rem;
}

.field-error-message {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 6px; /* Adjust spacing */
  min-height: 1.2em; /* Keep preventing layout shifts */
}

/* Invalid field indication */
input:invalid,
textarea:invalid,
select:invalid {
  /* Remove browser default invalid styles if desired */
}

/* Apply border directly to elements with errors via script if possible,
   or use a class like .is-invalid */
input.is-invalid,
textarea.is-invalid,
select.is-invalid {
  border-color: #dc3545 !important; /* Ensure override */
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(220, 53, 69, 0.2) !important; /* Red focus ring */
}

/* Primary image styling */
.image-preview-item.primary-image {
  border-color: #007bff; /* Highlight primary image border */
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.4); /* Add a subtle glow */
}

.image-preview-item.primary-image .primary-indicator {
  font-weight: bold;
}

/* Style for the item being dragged */
.image-preview-item.dragging {
  opacity: 0.5;
  transform: scale(0.95); /* Slightly shrink dragged item */
}

/* Style for potential drop target */
.image-preview-item.drop-target {
  border-style: dashed;
  border-color: #28a745; /* Green border for drop target */
  background-color: #e9f5ec;
}

/* Style for the primary indicator text */
.primary-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 123, 255, 0.7);
  color: white;
  font-size: 0.75rem;
  text-align: center;
  padding: 2px 0;
  border-bottom-left-radius: 6px; /* Match item border radius */
  border-bottom-right-radius: 6px;
  user-select: none;
}

/* We might need to adjust pointer-events if the overlay blocks interaction */
/* Example: Add an overlay div instead of just styling the main div */
</style>
