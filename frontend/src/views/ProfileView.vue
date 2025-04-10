<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getItemsOfUser, type ItemCard } from '@/service/itemService'
import LanguageSelector from '@/components/LanguageSelector.vue'
import { useSessionStore, type User } from '@/stores/session'
import Product from '@/components/Product.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useRouter } from 'vue-router'

const sessionStore = useSessionStore()
const { t } = useI18n()
const router = useRouter()

const user = ref<User | null>(null)
const items = ref<ItemCard[]>([])

onMounted(async () => {
  if (sessionStore.user) {
    user.value = sessionStore.user
  }
  const response = sessionStore.user ? await getItemsOfUser(sessionStore.user.id) : []
  items.value = response
})

type ItemStatus = 'active' | 'reserved' | 'sold' | 'archived' | 'bought'
const selectedStatus = ref<ItemStatus>('active')

const statusMatches = (item: ItemCard, status: ItemStatus) => {
  switch (status) {
    case 'active':
      return item.status === 'available' || item.status === 'reserved'
    case 'reserved':
      return item.status === 'reserved_by_user'
    case 'sold':
      return item.status === 'sold'
    case 'archived':
      return item.status === 'archived'
    case 'bought':
      return item.status === 'bought'
  }
}

const filteredItems = computed(() => {
  return items.value.filter((item) => statusMatches(item, selectedStatus.value))
})

const selectStatus = (status: ItemStatus) => {
  selectedStatus.value = status
}

const editUser = () => {
  router.push({ name: 'UpdateCredentials' }) // Ensure the route exists
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
          <p class="email">
            {{ user?.email || 'Email' }}
          </p>
          <p class="join-date">
            {{ new Date(user?.joinedAt || '').toLocaleDateString() || 'N/A' }}
          </p>
        </div>
      </div>
      <div class="header-actions">
        <BaseButton class="edit-button" @click="editUser">
          {{ t('profile.editUser') }}
        </BaseButton>
        <BaseButton class="logout-button" @click="sessionStore.logout">
          {{ t('profile.logout') }}
        </BaseButton>
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
        <Product v-for="item in filteredItems" :key="item.id" :product="item" />
      </div>
    </section>
  </div>
</template>

<style scoped>
.profile-view {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 2rem 1rem 0 1rem;
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
  padding-bottom: 0.5rem;
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
  height: 140px;
  object-fit: cover;
  display: block;
  border-radius: 50%;
}

.info {
  flex: 1;
}

.name {
  margin: 0 0 0.25rem;
  font-size: 2.5rem;
}

.email {
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
  min-width: 140px;
}

.logout-button {
  background-color: #dd4422;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  width: auto;
  max-height: 20px;
  margin-bottom: 1rem;
  color: #fff;
}

.logout-button:hover:not(:disabled) {
  background-color: #cc3311;
}

.edit-button {
  background-color: #007bff;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  width: auto;
  margin-bottom: 1rem;
  color: #fff;
}

.edit-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.listings {
  margin-bottom: 2rem;
}

.listings-menu {
  display: flex;
  margin-bottom: 1rem;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
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

@media (max-width: 496px) {
  .header {
    flex-direction: column;
    flex-flow: column-reverse;
  }
  .header-actions {
    flex-direction: row;
    flex-flow: row-reverse;
    align-items: end;
  }
  .logout-button,
  .edit-button {
    margin-bottom: 1.5rem;
  }
}
</style>
