<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { getRecommendedItems, type ItemCard } from '@/service/itemService'
import LanguageSelector from '@/components/LanguageSelector.vue'
import { useSessionStore } from '@/stores/session'

const authStore = useSessionStore()

const { t } = useI18n()

const user = ref({
  name: 'User Name',
  location: 'Location',
  joinDate: '2023-01-15',
})

const listings = ref<ListingItem[]>([])
getRecommendedItems().then((response) => {
  listings.value = response.listings
})

const selectedStatus = ref<'active' | 'reserved' | 'sold' | 'archived' | 'bought'>('active')

const filteredListings = computed(() => {
  return listings.value.filter((listing) => listing.status === selectedStatus.value)
})

const selectStatus = (status: 'active' | 'reserved' | 'sold' | 'archived' | 'bought') => {
  selectedStatus.value = status
}
</script>

<template>
  <div class="profile-view">
    <header class="header">
      <div class="header-profile">
        <div class="avatar">
          <img alt="User Avatar" class="avatar-img" src="/profile-avatar.png" />
        </div>
        <div class="info">
          <h1 class="name">
            {{ user?.name || 'User Name' }}
          </h1>
          <p class="location">
            {{ user?.location || 'Location' }}
          </p>
          <p class="join-date">{{ t('profile.joinedOn') }}: {{ user?.joinDate || 'N/A' }}</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="logout-button" @click="authStore.logout">
          {{ t('profile.logout') }}
        </button>
        <LanguageSelector />
      </div>
    </header>

    <section class="listings">
      <div class="listings-menu">
        <button :class="{ active: selectedStatus === 'active' }" @click="selectStatus('active')">
          {{ t('profile.active') }}
        </button>
        <button
          :class="{ active: selectedStatus === 'reserved' }"
          @click="selectStatus('reserved')"
        >
          {{ t('profile.reserved') }}
        </button>
        <button :class="{ active: selectedStatus === 'sold' }" @click="selectStatus('sold')">
          {{ t('profile.sold') }}
        </button>
        <button
          :class="{ active: selectedStatus === 'archived' }"
          @click="selectStatus('archived')"
        >
          {{ t('profile.archived') }}
        </button>
        <button :class="{ active: selectedStatus === 'bought' }" @click="selectStatus('bought')">
          {{ t('profile.bought') }}
        </button>
      </div>

      <div class="listings-grid">
        <ItemCard v-for="listing in filteredListings" :key="listing.id" :listing="listing" />
      </div>
    </section>
  </div>
</template>

<style scoped>
.profile-view {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 2rem 1rem;
  font-family: sans-serif;
  color: #333;
  background-color: #fff;
}

.header {
  display: flex;
  align-items: stretch;
  gap: 1rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1.5rem;
  justify-content: space-between;
}

.header-profile {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.avatar {
  min-width: 80px;
  min-height: 80px;
  overflow: hidden;
  border-radius: 50%;
}

.avatar-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
  border-radius: 50%;
}

.info {
  flex: 1;
}

.name {
  margin: 0 0 0.25rem;
  font-size: 1.5rem;
}

.location {
  margin: 0.25rem 0;
  color: #666;
}

.join-date {
  margin: 0.5rem 0 0 0;
  font-size: 0.9rem;
  color: #999;
}

.header-actions {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.logout-button {
  background-color: #dd4422;
  border: none;
  outline: none;
  color: #fff;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.1s ease;
}

.logout-button:hover {
  background-color: #cc3311;
}

.listings {
  margin-bottom: 2rem;
}

.listings-menu {
  display: flex;
  margin-bottom: 1rem;
}

.listings-menu button {
  min-width: fit-content;
  width: 12%;
  background: none;
  border: none;
  padding: 0.5rem 1rem;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition:
    color 0.1s,
    border-color 0.1s;
  font-size: 1rem;
  color: #333;
}

.listings-menu button.active {
  border-color: #007bff;
  color: #007bff;
}

.listings-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}
</style>
