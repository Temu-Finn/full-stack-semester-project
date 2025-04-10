<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form novalidate @submit.prevent="handleSubmit">
      <BaseSelect
        id="categoryId"
        v-model.number="product.categoryId"
        :error="errors.categoryId || categoryError"
        :label="$t('newProduct.category')"
        :placeholder="$t('newProduct.selectCategory')"
        required
      >
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.icon + ' ' + category.name }}
        </option>
      </BaseSelect>

      <BaseInput
        id="name"
        v-model.trim="product.title"
        :error="errors.title"
        :label="$t('newProduct.productTitle')"
        required
        type="text"
      />

      <BaseTextarea
        id="description"
        v-model.trim="product.description"
        :error="errors.description"
        :label="$t('newProduct.description')"
        auto-resize
        required
      />

      <BaseInput
        id="price"
        v-model.number="product.price"
        :error="errors.price"
        :label="$t('newProduct.price')"
        min="0"
        required
        step="1"
        type="number"
      />

      <BaseInput
        id="postalCode"
        v-model="product.postalCode"
        :error="errors.postalCode"
        :label="$t('newProduct.postalCode')"
        maxlength="4"
        minlength="4"
        pattern="[0-9]{4}"
        placeholder="7032"
        required
        type="text"
      />

      <ImageUploader
        v-model:image-files="product.imageFiles"
        v-model:image-urls="product.imageUrls"
      />

      <div class="form-group toggle-group">
        <ToggleButton v-model="product.allowVippsBuy" :scale="1.4" active-color="#ff5b24" />
        <div class="vipps-logo-text">
          <img alt="Vipps Logo" class="vipps-logo-inline" src="/Vipps.svg" />
          <span class="vipps-label-text">{{ $t('newProduct.vipps') }}</span>
        </div>
      </div>

      <BaseButton :disabled="isLoading" :loading="isLoading" type="submit">
        {{ isLoading ? $t('newProduct.creating') : $t('newProduct.createButton') }}
      </BaseButton>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script lang="ts" setup>
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
</style>
