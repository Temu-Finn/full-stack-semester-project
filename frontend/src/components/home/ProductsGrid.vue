<script setup lang="ts">
import { ref } from 'vue'
import Product from './Product.vue'
import { getRecommendedItems, type ItemCard } from '@/service/itemService'

const products = ref<ItemCard[]>([])

getRecommendedItems().then((items) => {
  products.value = items.items
})
</script>

<template>
  <div class="container">
    <h3 class="title">Recommended for you</h3>
    <div class="product-grid">
      <Product v-for="product in products" :key="product.id" :product="product" />
    </div>
  </div>
</template>

<style scoped>
.container {
  width: 100%;
}
.title {
  font-weight: bold;
}
.product-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
  padding: 1rem 0 1.5rem 0;
}
</style>
