<template>
    <div class="receipt-container" v-if="item">
      <h2>{{ 'Sale approved!!' }}</h2>
      <img v-if="item.images?.length" :src="item.images[0].dataURL" alt="Product image" class="product-image" />
      <p><strong>{{ 'Product' }}:</strong> {{ item.title }}</p>
      <p><strong>{{ 'Price' }}:</strong> {{ item.purchasePrice ?? item.price }} kr</p>
    </div>
  
    <div v-else class="loading">
      {{ t('receipt.loading') }}
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { getItem, type CompleteItem } from '@/service/itemService'
  import { useI18n } from 'vue-i18n'
  
  const { t } = useI18n()
  const route = useRoute()
  
  const item = ref<CompleteItem | null>(null)
  
  onMounted(async () => {
    const id = Number(route.params.id)
    if (!id) return
  
    try {
      item.value = await getItem(id)
    } catch (err) {
      console.error('Failed to load item', err)
    }
  })
  </script>
  
  <style scoped>
  .receipt-container {
    max-width: 600px;
    margin: 80px auto;
    padding: 24px;
    background: #f9f9f9;
    border-radius: 8px;
    text-align: center;
    font-size: 18px;
  }
  .product-image {
    max-width: 300px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
  .loading {
    text-align: center;
    padding: 80px;
    font-size: 18px;
    color: #666;
  }
  </style>
  