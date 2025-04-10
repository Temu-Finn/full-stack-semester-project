<script lang="ts" setup>
/**
 * SearchResults.vue
 *
 * This component fetches items for a given query, selected category, sort order,
 * and location (county/municipality). It displays them in a grid and includes a
 * side filter menu plus a top bar with controls.
 */
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import Product from '@/components/home/Product.vue'
import { searchItems, type SearchItemsResponse } from '@/service/itemService'
import Map from '@/components/Map.vue'
import { getCategories } from '@/service/categoryService.ts'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const searchResponse = ref<SearchItemsResponse>()
const isLoading = ref(true)

const searchQuery = computed(() => route.query.textSearch ?? '')
const selectedCategory = computed(() =>
  route.query.category ? String(route.query.category) : null,
)

// For the search form's local binding
const localSearch = ref(searchQuery.value)

// Sort options
const sortOptions = [
  { value: 'price,asc', label: t('search.priceAsc') },
  { value: 'price,desc', label: t('search.priceDesc') },
  { value: 'updated_at,asc', label: t('search.updatedAtAsc') },
  { value: 'updated_at,desc', label: t('search.updatedAtDesc') },
  { value: 'closest', label: t('search.closest') },
]

const sortQuery = computed(() => route.query.sort ?? sortOptions[0].value)
const selectedSort = ref(sortQuery.value)

const selectedCountyQuery = computed(() => (route.query.county ? String(route.query.county) : null))
const selectedMunicipalityQuery = computed(() =>
  route.query.municipality ? String(route.query.municipality) : null,
)

watch(sortQuery, () => {
  selectedSort.value = sortQuery.value
  fetchItems()
})
watch(searchQuery, () => {
  localSearch.value = searchQuery.value
  fetchItems()
})
watch(selectedCategory, () => {
  fetchItems()
})
watch(selectedCountyQuery, () => {
  fetchItems()
})
watch(selectedMunicipalityQuery, () => {
  fetchItems()
})

const currentPage = computed(() => (route.query.page ? Number(route.query.page) : 1))
watch(currentPage, () => {
  fetchItems()
})

async function fetchItems() {
  isLoading.value = true
  try {
    console.log('Selected sort: ' + selectedSort.value)
    searchResponse.value = await searchItems({
      searchText: searchQuery.value,
      categoryId: selectedCategory.value ? Number(selectedCategory.value) : undefined,
      sort: [selectedSort.value],
      page: currentPage.value - 1,
      size: 9,
      county: selectedCountyQuery.value ? selectedCountyQuery.value : undefined,
      municipality: selectedMunicipalityQuery.value ? selectedMunicipalityQuery.value : undefined,
    })
    console.log('Total elements: ' + searchResponse.value.result.page.totalElements)
  } catch (error) {
    console.error('Error fetching search results:', error)
    searchResponse.value = null
  } finally {
    isLoading.value = false
  }
}

onMounted(fetchItems)

function handleSortChange(newSortValue: string) {
  router.push({
    query: {
      ...route.query,
      sort: newSortValue,
    },
  })
}

function showMap() {
  console.log('Showing map view...')
}

function handleSearchSubmit() {
  router.push({
    query: {
      ...route.query,
      textSearch: localSearch.value,
    },
  })
}

function handleCategoryClick(categoryId: number | string) {
  router.push({
    query: {
      ...route.query,
      category: String(categoryId),
    },
  })
}

function clearCategory() {
  router.push({
    query: {
      ...route.query,
      category: null,
    },
  })
}

function toggleCounty(countyName: string) {
  if (selectedCountyQuery.value === countyName) {
    router.push({
      query: {
        ...route.query,
        county: undefined,
        municipality: undefined,
      },
    })
  } else {
    router.push({
      query: {
        ...route.query,
        county: countyName,
        municipality: undefined,
      },
    })
  }
}

function toggleMunicipality(municipalityName: string) {
  if (selectedMunicipalityQuery.value === municipalityName) {
    router.push({
      query: {
        ...route.query,
        municipality: undefined,
      },
    })
  } else {
    router.push({
      query: {
        ...route.query,
        municipality: municipalityName,
      },
    })
  }
}

const totalPagesRange = computed(() => {
  if (
    searchResponse.value &&
    searchResponse.value.result &&
    searchResponse.value.result.page.totalPages
  ) {
    const total = searchResponse.value.result.page.totalPages
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  return []
})

function handlePageClick(newPage: number) {
  router.push({
    query: {
      ...route.query,
      page: newPage,
    },
  })
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
        <form class="search-form" @submit.prevent="handleSearchSubmit">
          <input
            v-model="localSearch"
            :placeholder="$t('home.search')"
            class="search-bar"
            type="text"
          />
          <button class="search-submit-button" type="submit">
            {{ t('search.searchButton') }}
          </button>
        </form>

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
          <div
            :class="{ selected: selectedCategory == null }"
            class="category-card"
            @click.prevent="clearCategory()"
          >
            <span class="category-icon">🌐</span>
            <span class="category-name">All</span>
          </div>
          <div
            v-for="cat in categories"
            :key="cat.id"
            :class="{ selected: selectedCategory === String(cat.id) }"
            class="category-card"
            @click.prevent="handleCategoryClick(cat.id)"
          >
            <span class="category-icon">{{ cat.icon }}</span>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>

        <hr />

        <div class="vipps-section">
          <input type="checkbox" />
          <p>{{ $t('search.acceptsVipps') }}</p>
        </div>

        <hr />

        <div class="map-section">
          <Map :location="{ latitude: 63.44, longitude: 10.399 }" />
        </div>

        <div v-if="!isLoading" class="location-filters">
          <h3 class="location-title">{{ t('search.location') }}</h3>
          <div class="county-list">
            <div v-for="county in searchResponse.counties" :key="county.name">
              <div
                :class="{ selected: selectedCountyQuery === county.name }"
                class="county-item"
                @click.prevent="toggleCounty(county.name)"
              >
                {{ county.name }} ({{ county.count }})
              </div>
              <div v-if="selectedCountyQuery === county.name" class="municipality-list">
                <div
                  v-for="municipality in county.municipalities"
                  :key="municipality.name"
                  :class="{ selected: selectedMunicipalityQuery === municipality.name }"
                  class="municipality-item"
                  @click.prevent="toggleMunicipality(municipality.name)"
                >
                  {{ municipality.name }} ({{ municipality.count }})
                </div>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <section class="search-results">
        <div v-if="isLoading" class="loading-spinner">{{ t('search.loading') }}...</div>
        <div v-else>
          <div class="search-count">
            {{ searchResponse.result.page.totalElements }} {{ t('search.results') }}
            {{ searchQuery.length !== 0 ? t('search.for') : '' }} {{ searchQuery }}
          </div>
          <div class="results-grid">
            <Product v-for="item in searchResponse.result.content" :key="item.id" :product="item" />
          </div>
          <div v-if="totalPagesRange.length > 1" class="pagination">
            <button
              :disabled="currentPage === 1"
              class="pagination-button"
              @click="handlePageClick(currentPage - 1)"
            >
              Prev
            </button>

            <button
              v-for="page in totalPagesRange"
              :key="page"
              :class="{ active: page === currentPage }"
              class="pagination-button"
              @click="handlePageClick(page)"
            >
              {{ page }}
            </button>

            <button
              :disabled="currentPage === searchResponse.result.page.totalPages"
              class="pagination-button"
              @click="handlePageClick(currentPage + 1)"
            >
              Next
            </button>
          </div>
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
.search-filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
  gap: 1rem;
}
.search-form {
  display: flex;
  flex: 1;
  min-width: 200px;
}
.search-bar {
  flex-grow: 1;
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.search-submit-button {
  margin-left: 0.5rem;
  background-color: #007bff;
  border: none;
  color: #fff;
  padding: 0.55rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s ease;
}
.search-submit-button:hover {
  background-color: #0056b3;
}

.sort-dropdown {
  display: flex;
  align-items: center;
}
.sort-dropdown label {
  margin-right: 0.5rem;
  font-weight: bold;
}
.sort-dropdown select {
  padding: 0.6rem 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #ccc;
}
.sort-dropdown select:hover {
  background-color: #e4e4e4;
}

.show-on-map {
  background-color: transparent;
  border: 1px solid #007bff;
  color: #007bff;
  border-radius: 4px;
  padding: 0.6rem 1rem;
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
  gap: 0.2rem;
}
.category-section .category-card {
  display: flex;
  gap: 0.5rem;
  color: #888;
  padding: 0.2rem 0.3rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.category-section .category-card:hover {
  background-color: #f0f8ff;
}
.category-section .category-card.selected {
  color: #333;
  background-color: #d0eaff;
}
.vipps-section {
  padding: 0 0.5rem;
  display: flex;
  gap: 10px;
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
.search-count {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 1rem;
}
.loading-spinner {
  text-align: center;
  padding: 2rem;
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

.location-filters {
  margin-top: 1rem;
}
.location-title {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  font-weight: bold;
}
.county-list {
  display: flex;
  flex-direction: column;
}
.county-item,
.municipality-item {
  padding: 0.3rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.municipality-list {
  padding-left: 1rem;
}
.county-item:hover,
.municipality-item:hover {
  background-color: #f0f8ff;
}
.county-item.selected,
.municipality-item.selected {
  background-color: #d0eaff;
  color: #333;
}

.map-section {
  height: 300px;
  width: 100%;
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
