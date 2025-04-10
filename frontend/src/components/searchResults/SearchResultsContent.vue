<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'
import Product from '@/components/Product.vue'
import type { ItemCard } from '@/service/itemService.ts'

interface PageMetadata {
  totalElements: number
  totalPages: number
}

interface SearchResults {
  result: {
    page: PageMetadata
    content: ItemCard[]
  }
}

const props = defineProps<{
  searchResponse: SearchResults | null
  isLoading: boolean
  searchQuery: string
  currentPage: number
}>()

const emit = defineEmits<{
  (e: 'pageChange', newPage: number): void
}>()

const totalPagesRange = computed(() => {
  if (props.searchResponse && props.searchResponse.result.page.totalPages) {
    const total = props.searchResponse.result.page.totalPages
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  return []
})
</script>

<template>
  <section class="search-results">
    <div v-if="isLoading" class="loading-spinner">Loading...</div>
    <div v-else>
      <div class="search-count">
        {{ searchResponse.result.page.totalElements }} results
        <span v-if="searchQuery.length !== 0"> for "{{ searchQuery }}"</span>
      </div>
      <div class="results-grid">
        <Product v-for="item in searchResponse.result.content" :key="item.id" :product="item" />
      </div>
      <div v-if="totalPagesRange.length > 1" class="pagination">
        <button
          :disabled="currentPage === 1"
          class="pagination-button"
          @click="$emit('pageChange', currentPage - 1)"
        >
          Prev
        </button>

        <button
          v-for="page in totalPagesRange"
          :key="page"
          :class="{ active: page === currentPage }"
          class="pagination-button"
          @click="$emit('pageChange', page)"
        >
          {{ page }}
        </button>

        <button
          :disabled="currentPage === searchResponse.result.page.totalPages"
          class="pagination-button"
          @click="$emit('pageChange', currentPage + 1)"
        >
          Next
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.search-results {
  flex: 1;
  padding: 0.5rem 1rem;
  background-color: #fff;
}
.search-count {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 1rem;
}
.loading-spinner {
  text-align: center;
  padding: 2rem;
}
.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}
.pagination-button {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ccc;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.pagination-button:hover:not(:disabled) {
  background-color: #f0f8ff;
}
.pagination-button.active {
  background-color: #007bff;
  color: #fff;
  border-color: #007bff;
}
.pagination-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
