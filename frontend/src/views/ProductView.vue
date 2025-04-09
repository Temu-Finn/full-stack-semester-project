<template>
  <div v-if="isLoading" class="loading-state">Loading product details...</div>
  <div v-else-if="error" class="error-state">Error loading product: {{ error }}</div>
  <div v-else-if="product" class="product-container">
    <div class="left-column">
      <div class="main-image">
        <img :src="selectedImage" alt="Main product" />
      </div>
      <div v-if="productImages.length > 1" class="thumbnails">
        <img
          v-for="(img, index) in productImages"
          :key="index"
          :class="{ active: selectedImage === img }"
          :src="img"
          alt="Thumbnail"
          @click="selectedImage = img"
        />
      </div>
      <div v-else-if="productImages.length === 0" class="no-images">No images available.</div>
    </div>

    <div class="right-column">
      <h1 class="product-title">{{ product.title }}</h1>
      <div class="product-price">{{ product.price }} kr</div>

      <button class="buy-button" :disabled="!product.allowVippsBuy">
        {{ product.allowVippsBuy ? 'Kjøp nå (Vipps)' : 'Kjøp ikke tilgjengelig' }}
      </button>

      <div class="product-details">
        <p><strong>Status:</strong> {{ product.status }}</p>
        <p><strong>Sted:</strong> {{ product.municipality }} ({{ product.postalCode }})</p>
        <!-- Add other relevant details from CompleteItem if needed -->
        <p><strong>Beskrivelse:</strong> {{ product.description }}</p>
      </div>

      <div class="map-section">
        <!-- Pass location data to MapB component if it accepts props -->
        <MapB :location="product.location" />
      </div>
    </div>
  </div>
  <div v-else class="not-found">Product not found.</div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MapB from '@/components/Map.vue'
import 'mapbox-gl/dist/mapbox-gl.css' // Keep mapbox CSS import if MapB requires it globally styled
import { getItem, type CompleteItem } from '@/service/itemService'
import { logger } from '@/utils/logger' // Import logger for error handling

const route = useRoute()
const product = ref<CompleteItem | null>(null)
const selectedImage = ref<string>('')
const isLoading = ref<boolean>(true)
const error = ref<string | null>(null)

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

onMounted(async () => {
  if (isNaN(productId.value)) {
    error.value = 'Invalid Product ID.'
    isLoading.value = false
    return
  }

  try {
    isLoading.value = true
    error.value = null
    const fetchedProduct = await getItem(productId.value)
    product.value = fetchedProduct

    if (productImages.value.length > 0) {
      const primary = product.value.images?.find((img) => img.id === product.value?.primaryImageId)
      selectedImage.value = primary ? primary.dataURL : productImages.value[0]
    }
  } catch (err) {
    logger.error('Failed to fetch product:', err)
    error.value = 'Could not load product details. Please try again later.'
    product.value = null
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.product-container {
  display: flex;
  gap: 20px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: Arial, sans-serif;
}

.left-column {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-image img {
  width: 100%;
  max-width: 600px;
  border-radius: 8px;
  object-fit: cover;
}

.thumbnails {
  display: flex;
  gap: 10px;
}
.thumbnails img {
  width: 80px;
  height: 80px;
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

.buy-button {
  padding: 10px 20px;
  background-color: #4c8bf5;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.buy-button:hover {
  background-color: #3a71d8;
}

.product-details p {
  margin: 5px 0;
}

.map-section {
  margin-top: 20px;
  height: 300px;
}

.map-container {
  width: 100%;
  height: 300px;
  border-radius: 6px;
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
</style>
