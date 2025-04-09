<template>
  <div class="category-grid-container">
    <div class="category-grid">
      <a
        v-for="category in categories"
        :key="category.id"
        :href="`/item/search?category=${category.id}`"
        class="category-card"
        @click.prevent
      >
        <span class="category-icon">{{ category.icon }}</span>
        <span class="category-name">{{ category.name }}</span>
      </a>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from '@/service/categoryService'

const categories = ref([])

onMounted(async () => {
  categories.value = await getCategories()
})
</script>

<style scoped>
.category-grid-container {
  width: 100%;
}

.category-grid-container h2 {
  text-align: center;
  color: #333;
}

.category-grid {
  margin: auto;
  display: grid;
  grid-gap: 24px;
  grid-template-columns: repeat(auto-fill, minmax(8rem, 1fr));
  justify-items: center;
}

.category-card {
  width: 8rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  aspect-ratio: 1 / 1;
  color: #333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition:
    transform 0.1s ease-in-out,
    box-shadow 0.1s ease-in-out;
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.category-icon {
  font-size: 1.5rem;
  margin-bottom: 8px;
  line-height: 1;
}

.category-name {
  font-size: 1rem;
  font-weight: 500;
  text-align: center;
  line-height: 1.2;
}

@media (max-width: 600px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(6rem, 1fr));
  }
  .category-card {
    width: 6rem;
  }
  .category-icon {
    font-size: 1rem;
  }
  .category-name {
    font-size: 0.8rem;
  }
}
</style>
