<template>
  <Spinner v-if="isLoading"></Spinner>
  <div v-else-if="error" class="error-state">
    {{ $t('productView.errorLoadingPrefix') }} {{ error }}
  </div>
  <div v-else-if="product" class="product">
    <div class="left-column">
      <div class="main-image">
        <img :alt="$t('productView.altMainImage')" :src="selectedImage" />
      </div>
      <div v-if="productImages.length > 1" class="thumbnails">
        <img
          v-for="(img, index) in productImages"
          :key="index"
          :alt="$t('productView.altThumbnail')"
          :class="{ active: selectedImage === img }"
          :src="img"
          @click="selectedImage = img"
        />
      </div>
      <div v-else-if="productImages.length === 0" class="no-images">
        {{ $t('productView.noImages') }}
      </div>
    </div>

    <div class="right-column">
      <div class="right-column-details">
        <h1 class="product-title">{{ product.title }}</h1>
        <div class="product-price">{{ product.price }}{{ $t('productView.currencySuffix') }}</div>

        <div v-if="userId != product.sellerId" class="buttons">
          <BaseButton
            v-if="product.allowVippsBuy"
            background-color="#ff5b24"
            class="vipps-button"
            @click="startVipps"
          >
            <template #icon>
              <img alt="Vipps" src="/VippsWhite.svg" />
            </template>
            {{ $t('productView.buyNowVipps') }}
          </BaseButton>
          <BaseButton
            v-else
            :disabled="product.status === 'reserved' || product.status === 'reserved_by_user'"
            background-color="#ffffff"
            class="outline-button"
            text-color="#007bff"
            @click="reserveItemHandle"
          >
            {{
              product.status === 'reserved_by_user'
                ? $t('productView.reservedByUser')
                : product.status === 'reserved'
                  ? $t('productView.reservedByOtherUser')
                  : $t('productView.reserveItem')
            }}
          </BaseButton>
          <BaseButton :disabled="!product.sellerId" @click="showMessageDialog = true">
            {{ $t('productView.sendMessage') }}
          </BaseButton>
        </div>

        <div class="product-details">
          <p>
            <strong>{{ $t('productView.statusLabel') }}</strong> {{ product.status }}
          </p>
          <p>
            <strong>{{ $t('productView.locationLabel') }}</strong> {{ product.municipality }} ({{
              product.postalCode
            }})
          </p>
          <p>
            <strong>{{ $t('productView.categoryLabel') }}</strong> {{ product.category.icon }}
            {{ product.category.name }}
          </p>
          <p>
            <strong>{{ $t('productView.descriptionLabel') }}</strong>
            {{ product.description }}
          </p>
        </div>
      </div>

      <div class="map-section">
        <Map :items="[product]" :location="product.location ?? undefined" />
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    {{ $t('productView.notFound') }}
  </div>
  <div v-if="searchResponse && product" class="similar-items-section">
    <h3 v-if="filteredSimilarItems.length > 0">Similar items</h3>
    <div class="similar-items">
      <Product v-for="item in filteredSimilarItems" :key="item.id" :product="item" />
    </div>
  </div>

  <!-- Toast Notification -->
  <div v-if="toast.visible" :class="['toast', toast.success ? 'success' : 'error']">
    {{ toast.message }}
  </div>

  <!-- Modern Message Dialog Popover -->
  <div v-if="showMessageDialog" class="dialog-overlay" @click="showMessageDialog = false">
    <div class="dialog" @click.stop>
      <h2>Send a message to the owner</h2>
      <input
        v-model="messageContent"
        placeholder="Type your message"
        type="text"
        @keydown.enter="handleSendMessage"
      />
      <div class="dialog-buttons">
        <button class="send-btn" @click="handleSendMessage">Send</button>
        <button class="cancel-btn" @click="showMessageDialog = false">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Map from '@/components/Map.vue'
import 'mapbox-gl/dist/mapbox-gl.css'
import {
  type CompleteItem,
  getItem,
  searchItems,
  type SearchItemsResponse,
  type ItemCard,
  reserveItem,
} from '@/service/itemService'
import { logger } from '@/utils/logger'
import { useI18n } from 'vue-i18n'
import Product from '@/components/Product.vue'
import BaseButton from '@/components/BaseButton.vue'
import { startVippsPayment } from '@/service/vippsService'
import { sendMessage } from '@/service/conversationService.ts'
import { useSessionStore } from '@/stores/session.ts'

const userId = useSessionStore().user.id
const route = useRoute()
const router = useRouter()
const product = ref<CompleteItem | null>(null)
const selectedImage = ref<string>('')
const isLoading = ref<boolean>(true)
const error = ref<string | null>(null)
const { t } = useI18n()

const searchResponse = ref<SearchItemsResponse>({
  counties: [],
  result: { content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } },
})

const productId = computed(() => {
  const idParam = route.params.id
  return Number(Array.isArray(idParam) ? idParam[0] : idParam)
})

const productImages = computed(() => {
  if (!product.value?.images?.length) {
    return ['/placeholder.svg']
  }
  return product.value.images.map((img) => img.dataURL)
})

const filteredSimilarItems = computed((): ItemCard[] => {
  if (!searchResponse.value || !product.value) {
    return []
  }
  return searchResponse.value.result.content.filter((value) => value.id !== product.value!.id)
})

onMounted(async () => {
  if (isNaN(productId.value)) {
    error.value = t('productView.errorInvalidId')
    isLoading.value = false
    return
  }

  try {
    isLoading.value = true
    error.value = null
    product.value = await getItem(productId.value)
    fetchItems(product.value.category.id)
    console.log(product.value)

    if (productImages.value.length > 0) {
      const primary = product.value.images?.find((img) => img.id === product.value?.primaryImageId)
      selectedImage.value = primary ? primary.dataURL : productImages.value[0]
    }
  } catch (err) {
    logger.error('Failed to fetch product:', err)
    error.value = t('productView.errorFetchFailed')
    product.value = null
  } finally {
    isLoading.value = false
  }
})

async function fetchItems(categoryId: number) {
  isLoading.value = true
  try {
    searchResponse.value = await searchItems({
      categoryId: categoryId,
      page: 0,
      size: 5,
    })
  } catch (error) {
    console.error('Error fetching search results:', error)
    searchResponse.value = {
      counties: [],
      result: { content: [], page: { size: 0, number: 0, totalElements: 0, totalPages: 0 } },
    }
  } finally {
    isLoading.value = false
  }
}

const reserveItemHandle = async () => {
  if (!product.value) return
  product.value = await reserveItem(product.value.id)
  console.log('Reserved: ', product.value)
}

const startVipps = async () => {
  if (!product.value) return

  try {
    localStorage.setItem('vippsPurchasedItemId', String(product.value.id))
    const { redirectUrl } = await startVippsPayment(product.value.price)
    window.location.href = redirectUrl
  } catch (err) {
    console.error('Could not start Vipps payment', err)
    alert(t('productView.paymentInitFailed'))
  }
}

const showMessageDialog = ref(false)
const messageContent = ref('')

// Toast reactive object
const toast = ref({
  message: '',
  success: true,
  visible: false,
})

// Helper function to show toast notifications
function showToast(message: string, success = true) {
  toast.value = { message, success, visible: true }
  // Hide the toast after 3 seconds
  setTimeout(() => {
    toast.value.visible = false
  }, 3000)
}

// Updated handleSendMessage to be async and show toast notifications
async function handleSendMessage() {
  if (!product.value) return
  try {
    // Await the sendMessage function (assuming it returns a promise)
    await sendMessage(null, product.value.id, messageContent.value)
    showToast('Message sent successfully!', true)
  } catch (err) {
    console.error('Error sending message:', err)
    showToast('Message failed to send.', false)
  }
  messageContent.value = ''
  showMessageDialog.value = false
}
</script>

<style scoped>
.product {
  display: flex;
  gap: 20px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: Arial, sans-serif;
}

.left-column {
  width: 100%;
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-image img {
  width: 100%;
  border-radius: 8px;
  object-fit: cover;
  aspect-ratio: 1;
}

.thumbnails {
  display: flex;
  gap: 10px;
  width: 100%;
}
.thumbnails img {
  width: 100%;
  max-width: 80px;
  min-width: 0;
  aspect-ratio: 1;
  object-fit: cover;
  cursor: pointer;
  border-radius: 4px;
  border: 2px solid transparent;
}
.thumbnails img.active {
  border-color: #4c8bf5;
}

.right-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.product-title {
  font-size: 24px;
  margin: 0;
}

.product-price {
  font-size: 20px;
  color: #333;
  font-weight: bold;
}

.product-details p {
  margin: 5px 0;
}

.map-section {
  margin-top: 20px;
  height: 100%;
}

.map-container {
  width: 100%;
  height: 100%;
  border-radius: 6px;
}
.similar-items-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  width: 100%;
}
.similar-items {
  width: 100%;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}
.similar-items div {
  width: 220px;
}

.loading-state,
.error-state,
.not-found {
  padding: 40px 20px;
  text-align: center;
  font-size: 18px;
  color: #666;
}

.error-state {
  color: red;
}

.no-images {
  text-align: center;
  color: #999;
  padding: 20px;
  border: 1px dashed #ccc;
  border-radius: 4px;
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.outline-button {
  border: 2px solid #007bff;
}

.outline-button:disabled {
  border: 2px solid #ccc;
}

/* Toast Styles */
.toast {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  min-width: 200px;
  padding: 10px 20px;
  border-radius: 6px;
  text-align: center;
  color: #fff;
  z-index: 1100;
  animation:
    fadein 0.5s,
    fadeout 0.5s 2.5s;
}
.toast.success {
  background-color: #4caf50;
}
.toast.error {
  background-color: #f44336;
}
@keyframes fadein {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
@keyframes fadeout {
  from {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  to {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
}

/* Modern Popover (Dialog) Styles */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: #fff;
  padding: 30px 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-width: 400px;
  width: 90%;
}

.dialog h2 {
  margin-top: 0;
  font-size: 1.5rem;
}

.dialog input {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 20px;
  outline: none;
}

.dialog-buttons {
  display: flex;
  gap: 10px;
}

.dialog-buttons button {
  flex: 1;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.send-btn {
  background-color: #007bff;
  color: #fff;
}

.cancel-btn {
  background-color: #bbb;
  color: #fff;
}

@media (max-width: 960px) {
  .product {
    flex-direction: column;
  }
  .main-image img {
    aspect-ratio: 2/1;
  }
  .right-column {
    flex-direction: row;
    align-content: stretch;
  }
  .right-column-details {
    flex: 1;
  }
  .map-section {
    flex: 1;
    width: 100%;
    height: 18rem;
    margin-top: 0;
  }
  .similar-items div {
    width: calc(50% - 1rem);
  }
}
@media (max-width: 496px) {
  .similar-items div {
    width: 100%;
  }
}
</style>
