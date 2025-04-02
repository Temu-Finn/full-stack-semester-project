<template>
  <a class="product-card" href="">
    <img
      :alt="product.name"
      :src="'https://primefaces.org/cdn/primevue/images/product/' + product.image"
      class="product-image"
    />
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-meta">
        <span class="product-category">{{ product.category }}</span>
        <span class="product-price">${{ product.price }}</span>
      </div>
    </div>
  </a>
</template>

<script setup>
defineProps({
  product: Object,
})
</script>

<style scoped>
.product-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.product-name {
  font-size: 1.1rem;
  margin: 0 0 0.5rem 0;
  color: #333;
}

.product-description {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: auto;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: #999;
  margin-top: 1rem;
}

.product-price {
  font-weight: bold;
  color: #2c3e50;
}
</style>
