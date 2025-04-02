<template>
  <div class="product-grid">
    <Product v-for="product in products" :key="product.id" :product="product" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Product from './Product.vue'

const products = ref([
  {
    id: '1000',
    name: 'Bamboo Watch',
    description:
      'A stylish and eco-friendly wristwatch made from natural bamboo wood, perfect for everyday use.',
    image: 'bamboo-watch.jpg',
    price: 65,
    category: 'Accessories',
  },
  {
    id: '1001',
    name: 'Black Watch',
    description: 'A sleek black timepiece with a minimalist design and premium leather strap.',
    image: 'black-watch.jpg',
    price: 72,
    category: 'Accessories',
  },
  {
    id: '1002',
    name: 'Blue Band',
    description:
      'Comfortable and durable fitness band that tracks your steps, sleep, and heart rate.',
    image: 'blue-band.jpg',
    price: 79,
    category: 'Fitness',
  },
  {
    id: '1003',
    name: 'Blue T-Shirt',
    description: 'Soft cotton t-shirt in a calming blue shade, ideal for casual wear or workouts.',
    image: 'blue-t-shirt.jpg',
    price: 29,
    category: 'Clothing',
  },
  {
    id: '1004',
    code: 'h456wer53',
    name: 'Bracelet',
    description: 'A handmade bracelet with a timeless design, suitable for all occasions.',
    image: 'bracelet.jpg',
    price: 15,
    category: 'Accessories',
  },
  {
    id: '1005',
    name: 'Brown Purse',
    description: 'Elegant brown leather purse with spacious compartments and a magnetic closure.',
    image: 'brown-purse.jpg',
    price: 120,
    category: 'Accessories',
  },
  {
    id: '1006',
    name: 'Chakra Bracelet',
    description:
      'Colorful beaded bracelet that represents the seven chakras, promoting balance and energy.',
    image: 'chakra-bracelet.jpg',
    price: 32,
    category: 'Accessories',
  },
  {
    id: '1007',
    name: 'Galaxy Earrings',
    description:
      'Unique earrings with a cosmic design, perfect for space enthusiasts and stargazers.',
    image: 'galaxy-earrings.jpg',
    price: 34,
    category: 'Accessories',
  },
  {
    id: '1008',
    code: 'vbb124btr',
    name: 'Game Controller',
    description:
      'Ergonomic wireless game controller with responsive buttons and vibration feedback.',
    image: 'game-controller.jpg',
    price: 99,
    category: 'Electronics',
  },
])
</script>

<style scoped>
.product-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
  padding-top: 2rem;
}
</style>
