<template>
  <div class="product-container">
    <div class="left-column">
      <div class="main-image">
        <img :src="selectedImage" alt="Main product image" />
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
    </div>

    <div class="right-column">
      <h1 class="product-title">{{ product.title }}</h1>
      <div class="product-price">{{ product.price }} kr</div>

      <button class="buy-button">Kjøp nå</button>

      <div class="product-details">
        <p><strong>Tilstand:</strong> {{ product.condition }}</p>
        <p><strong>Størrelse:</strong> {{ product.size }}</p>
        <p><strong>Merke:</strong> {{ product.brand }}</p>
        <p><strong>Kjønn:</strong> {{ product.gender }}</p>
        <p><strong>Frakt:</strong> {{ product.shipping }}</p>
        <p><strong>Beskrivelse:</strong> {{ product.description }}</p>
      </div>

      <div class="map-section">
        <MapB />
      </div>
    </div>
  </div>
</template>

<script>
import MapB from '@/components/Map.vue'
import 'mapbox-gl/dist/mapbox-gl.css'

export default {
  name: 'ProductView',
  components: {
    MapB,
  },
  data() {
    return {
      product: {
        title: 'Bamboo Watch',
        price: 350,
        condition: 'Pent brukt',
        size: '104',
        brand: 'Polarn O. Pyret',
        gender: 'Gutt',
        shipping: 'Hentes eller sendes mot frakt',
        description: 'Flott klespakke fra John Lewis & GAP ...',
      },
      productImages: [
        'https://primefaces.org/cdn/primevue/images/product/bamboo-watch.jpg',
        'https://primefaces.org/cdn/primevue/images/product/black-watch.jpg',
        'https://primefaces.org/cdn/primevue/images/product/blue-band.jpg',
      ],
      selectedImage: '',
    }
  },
  created() {
    if (this.productImages.length) {
      this.selectedImage = this.productImages[0]
    }
  },
}
</script>

<style scoped>
.product-container {
  display: flex;
  gap: 20px;
  max-width: 1200px;
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
  max-width: 400px;
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
}

.map-container {
  width: 100%;
  height: 300px;
  border-radius: 6px;
}
</style>
