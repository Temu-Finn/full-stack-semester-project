<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form @submit.prevent="handleSubmit" novalidate>
      <!-- Category Select -->
      <BaseSelect
        id="categoryId"
        v-model.number="product.categoryId"
        :label="$t('newProduct.category')"
        :placeholder="$t('newProduct.selectCategory')"
        :error="errors.categoryId || categoryError"
        required
      >
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.icon + ' ' + category.name }}
        </option>
      </BaseSelect>

      <!-- Title -->
      <BaseInput
        id="name"
        v-model.trim="product.title"
        :label="$t('newProduct.productTitle')"
        :error="errors.title"
        required
        type="text"
      />

      <!-- Description -->
      <BaseTextarea
        id="description"
        v-model.trim="product.description"
        :label="$t('newProduct.description')"
        :error="errors.description"
        auto-resize
        required
      />

      <!-- Price -->
      <BaseInput
        id="price"
        v-model.number="product.price"
        :label="$t('newProduct.price')"
        :error="errors.price"
        required
        step="1"
        type="number"
        min="0"
      />

      <!-- Postal Code -->
      <BaseInput
        id="postalCode"
        v-model="product.postalCode"
        :label="$t('newProduct.postalCode')"
        :error="errors.postalCode"
        required
        type="text"
        pattern="[0-9]{4}"
        minlength="4"
        maxlength="4"
        placeholder="7032"
      />

      <!-- Images -->
      <ImageUploader
        v-model:image-files="product.imageFiles"
        v-model:image-urls="product.imageUrls"
      />

      <!-- Allow Vipps -->
      <div class="form-group toggle-group">
        <ToggleButton v-model="product.allowVippsBuy" active-color="#ff5b24" :scale="1.4" />
        <div class="vipps-logo-text">
          <img src="/Vipps.svg" alt="Vipps Logo" class="vipps-logo-inline" />
          <span class="vipps-label-text">{{ $t('newProduct.vipps') }}</span>
        </div>
      </div>

      <!-- Submit Button -->
      <BaseButton :loading="isLoading" :disabled="isLoading" type="submit">
        {{ isLoading ? $t('newProduct.creating') : $t('newProduct.createButton') }}
      </BaseButton>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { createItem } from '@/service/itemService'
import { getCategories } from '@/service/categoryService'
import { logger } from '@/utils/logger'
import { useI18n } from 'vue-i18n'
import ToggleButton from '@/components/ToggleButton.vue'
import BaseInput from '@/components/BaseInput.vue'
import BaseTextarea from '@/components/BaseTextarea.vue'
import BaseSelect from '@/components/BaseSelect.vue'
import BaseButton from '@/components/BaseButton.vue'
import ImageUploader from '@/components/newProduct/ImageUploader.vue'

interface Product {
  categoryId: number | null
  postalCode: string
  title: string
  description: string
  price: number | null
  allowVippsBuy: boolean
  imageFiles: File[]
  imageUrls: string[]
}

interface FormErrors {
  categoryId: string
  title: string
  description: string
  price: string
  postalCode: string
}

interface Category {
  id: number
  name: string
  icon: string
}

const product = ref<Product>({
  categoryId: null,
  postalCode: '',
  title: '',
  description: '',
  price: null,
  allowVippsBuy: true,
  imageFiles: [],
  imageUrls: [],
})

const isLoading = ref(false)
const errorMessage = ref('')
const categories = ref<Category[]>([])
const categoryError = ref('')
const router = useRouter()
const { t } = useI18n()

const errors = ref<FormErrors>({
  categoryId: '',
  title: '',
  description: '',
  price: '',
  postalCode: '',
})

onMounted(async () => {
  try {
    categories.value = await getCategories()
    logger.info('Categories loaded successfully')
  } catch (error: any) {
    logger.error('Failed to load categories in component:', error)
    categoryError.value = error.message || 'Could not load categories.'
  }
})

onUnmounted(() => {
  logger.debug('NewProductView unmounted')
})

const validateForm = (): boolean => {
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
  if (!product.value.title.trim()) {
    errors.value.title = t('validation.required', { field: t('newProduct.productTitle') })
    isValid = false
  }
  if (!product.value.description.trim()) {
    errors.value.description = t('validation.required', { field: t('newProduct.description') })
    isValid = false
  }
  const priceValue = product.value.price
  if (priceValue === null || priceValue === undefined || priceValue <= 0) {
    errors.value.price = t('validation.positiveNumber', { field: t('newProduct.price') })
    isValid = false
  }

  const postalCode = product.value.postalCode.trim()
  if (postalCode === '') {
    errors.value.postalCode = t('validation.required', { field: t('newProduct.postalCode') })
    isValid = false
  } else if (!/^\d{4}$/.test(postalCode)) {
    errors.value.postalCode = t('validation.postalCode', { field: t('newProduct.postalCode') })
    isValid = false
  }

  return isValid
}

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
      categoryId: product.value.categoryId as number,
      postalCode: product.value.postalCode,
      title: product.value.title,
      description: product.value.description,
      price: product.value.price as number,
      allowVippsBuy: product.value.allowVippsBuy,
    }

    const images = product.value.imageFiles

    logger.debug('Submitting new product:', { itemData, imageCount: images.length })

    const createdItem = await createItem(itemData, images)

    logger.info('Product created successfully:', createdItem)

    router.push({ name: 'product', params: { id: createdItem.id } })
  } catch (error: any) {
    logger.error('Failed to create product:', error)
    errorMessage.value =
      error.response?.data?.message ||
      error.message ||
      'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.new-product-view {
  max-width: 500px;
  min-width: 500px;
  margin: 40px auto;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

h1 {
  text-align: center;
  margin-bottom: 35px;
  color: #333;
  font-weight: bold;
}

.form-group {
  margin-bottom: 25px;
  position: relative;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

input[type='text'],
input[type='number'],
textarea,
select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: #fff;
  font-size: 1rem;
  color: #333;
}

select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23888' class='bi bi-chevron-down' viewBox='0 0 16 16'%3E%3Cpath fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px 16px;
  padding-right: 40px;
}

input[type='text']:focus,
input[type='number']:focus,
textarea:focus,
select:focus {
  outline: none;
  border-color: #007bff;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(0, 123, 255, 0.2);
}

.file-input-label {
  display: block;
  padding: 12px 15px;
  border: 1px dashed #dcdcdc;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  background-color: #f9f9f9;
  color: #555;
  transition:
    border-color 0.2s ease,
    background-color 0.2s ease;
}

.file-input-label:hover {
  border-color: #007bff;
  background-color: #f0f8ff;
}

.file-input-label.window-dragging {
  border-color: #007bff;
  border-style: dashed;
  background-color: #e6f2ff;
  box-shadow: 0 0 10px rgba(0, 123, 255, 0.3);
}

#images {
  display: none;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.image-preview-item {
  position: relative;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  background-color: #f0f0f0;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.image-preview {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 14px;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  padding: 0;
  opacity: 0.8;
  transition:
    background-color 0.2s ease,
    opacity 0.2s ease;
}

.remove-image-btn:hover {
  background-color: rgba(220, 53, 69, 0.9);
  opacity: 1;
}

.toggle-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
}

.vipps-logo-inline {
  height: 32px;
  width: auto;
  vertical-align: middle;
  opacity: 1;
}

.vipps-label-text {
  color: #333;
  font-weight: 500;
  font-size: 1rem;
  line-height: 1.2;
}

.vipps-logo-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

.error-message {
  color: #dc3545;
  margin-top: 20px;
  text-align: center;
  font-size: 0.9rem;
}

.field-error-message {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 6px;
  min-height: 1.2em;
}

input.is-invalid,
textarea.is-invalid,
select.is-invalid {
  border-color: #dc3545 !important;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(220, 53, 69, 0.2) !important;
}

.image-preview-item.primary-image {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.4);
}

.image-preview-item.primary-image .primary-indicator {
  font-weight: bold;
}

.image-preview-item.dragging {
  opacity: 0.5;
  transform: scale(0.95);
}

.image-preview-item.drop-target {
  border-style: dashed;
  border-color: #28a745;
  background-color: #e9f5ec;
}

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
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
  user-select: none;
}
</style>
