<script lang="ts" setup>
/**
 * SearchResults.vue
 *
 * This component fetches items for a given query, displays them in a
 * grid, and includes a side filter menu plus a top bar with controls.
 */

import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import Product from '@/components/home/Product.vue'
import { getRecommendedItems, type ItemCard as SearchItem } from '@/service/itemService'
import MapB from '@/components/Map.vue'
import { getCategories } from '@/service/categoryService.ts'

const { t } = useI18n()
const route = useRoute()

const items = ref<SearchItem[]>([])
const isLoading = ref(true)

const searchQuery = computed(() => route.query.textSearch ?? '')

const sortOptions = [
  { value: 'lowestPrice', label: t('search.lowestPrice') },
  { value: 'highestPrice', label: t('search.highestPrice') },
  { value: 'newest', label: t('search.newest') },
  { value: 'oldest', label: t('search.oldest') },
  { value: 'closest', label: t('search.closest') },
]
const selectedSort = ref(sortOptions[0].value)

async function fetchItems() {
  isLoading.value = true
  try {
    const response = await getRecommendedItems()
    items.value = response.items
  } catch (error) {
    console.error('Error fetching search results:', error)
    items.value = []
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchItems)

function handleSortChange(newSortValue: string) {
  selectedSort.value = newSortValue
}

function showMap() {
  console.log('Showing map view...')
}

const categories = ref([])

onMounted(async () => {
  categories.value = await getCategories()
})
</script>

<template>
  <div class="search-page">
    <header class="search-header">
      <div class="search-filter-bar">
        <input
          :placeholder="$t('home.search')"
          :value="searchQuery"
          class="search-bar"
          type="text"
        />
        <div class="sort-dropdown">
          <label for="sort-select">{{ t('search.sortBy') }}:</label>
          <select
            id="sort-select"
            v-model="selectedSort"
            @change="handleSortChange($event.target.value)"
          >
            <option v-for="opt in sortOptions" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </option>
          </select>
        </div>

        <button class="show-on-map" @click="showMap">
          {{ t('search.showOnMap') }}
        </button>
      </div>
    </header>

    <div class="search-main">
      <aside class="search-filters">
        <div class="category-section">
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

        <hr />

        <div class="vipps-section">
          <input type="checkbox" />
          <p>{{ $t('search.acceptsVipps') }}</p>
        </div>

        <hr />

        <div class="map-section">
          <MapB />
        </div>
      </aside>

      <section class="search-results">
        <div class="search-count">
          {{ items.length }} {{ t('search.hitsFor') }} {{ searchQuery }}
        </div>
        <div v-if="isLoading" class="loading-spinner">{{ t('search.loading') }}...</div>

        <div v-else class="results-grid">
          <Product v-for="item in items" :key="item.id" :product="item" />
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.search-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 60px);
  font-family: sans-serif;
  color: #333;
}

.search-header {
  border-bottom: 1px solid #ddd;
  padding: 1rem;
}
.search-bar {
  width: 100%;
}
.search-filter-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.search-count {
  font-size: 1.1rem;
  font-weight: bold;
}
.sort-dropdown {
  display: flex;
  align-items: center;
  margin-left: auto;
}
.sort-dropdown label {
  margin-right: 0.5rem;
  font-weight: 500;
}
.sort-dropdown select {
  padding: 0.3rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #ccc;
}
.show-on-map {
  background-color: transparent;
  border: 1px solid #007bff;
  color: #007bff;
  border-radius: 4px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  white-space: nowrap;
  transition: background-color 0.2s ease;
}
.show-on-map:hover {
  background-color: #f0f8ff;
}

.search-main {
  display: flex;
  flex: 1;
  margin: 1rem auto;
  width: 100%;
}
.search-filters {
  min-width: 240px;
  max-width: 280px;
  border-right: 1px solid #ddd;
  padding: 1rem;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.category-section {
  display: flex;
  flex-direction: column;
}
.category-section a {
  display: flex;
  gap: 0.5rem;
}
.vipps-section {
  display: flex;
  gap: 10px;
}
.search-filters h2 {
  font-size: 1rem;
  margin-bottom: 0.5rem;
}
.search-filters ul {
  list-style: none;
  padding: 0;
  margin: 0 0 1rem 0;
}
.search-filters li {
  margin: 0.25rem 0;
}
.search-filters a {
  color: #007bff;
  text-decoration: none;
}
.search-filters a:hover {
  text-decoration: underline;
}

.search-results {
  flex: 1;
  padding: 0.5rem 1rem;
  background-color: #fff;
}
.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
}

.loading-spinner {
  text-align: center;
  padding: 2rem;
}
.map-container {
  width: 100%;
  height: 300px;
  border-radius: 6px;
}

@media (max-width: 768px) {
  .search-main {
    flex-direction: column;
  }
  .search-filters {
    width: 100%;
    max-width: 100%;
    border-right: none;
    border-bottom: 1px solid #ddd;
  }
  .results-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}
</style>
