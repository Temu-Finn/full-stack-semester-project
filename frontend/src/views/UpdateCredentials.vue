<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useSessionStore, type User } from '@/stores/session'
import BaseButton from '@/components/BaseButton.vue'
import { useRouter } from 'vue-router'

const sessionStore = useSessionStore()
const { t } = useI18n()
const router = useRouter()

const user = ref<User | null>(null)
const credentials = ref<{ id: string; type: string; value: string }[]>([])

onMounted(() => {
  if (sessionStore.user) {
    user.value = sessionStore.user
    // Mock credential data - replace with actual API call
    credentials.value = [
      { id: '1', type: 'email', value: user.value.email },
      { id: '2', type: 'password', value: '••••••••' }
    ]
  }
})

const updateCredentials = () => {
  router.push({ name: 'UpdateCredentials' }) // Ensure this route exists
}

const logout = () => {
  sessionStore.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <div class="credentials-view">
    <header class="header">
      <div class="header-info">
        <h1 class="title">
          {{ t('credentials.title') }}
        </h1>
        <p class="subtitle">
          {{ t('credentials.subtitle') }}
        </p>
      </div>
      <div class="header-actions">
        <BaseButton class="update-button" @click="updateCredentials">
          {{ t('credentials.update') }}
        </BaseButton>
        <BaseButton class="logout-button" @click="logout">
          {{ t('credentials.logout') }}
        </BaseButton>
      </div>
    </header>

    <section class="credentials-list">
      <div v-for="credential in credentials" :key="credential.id" class="credential-item">
        <span class="credential-type">{{ t(`credentials.${credential.type}`) }}</span>
        <span class="credential-value">{{ credential.value }}</span>
      </div>
    </section>
  </div>
</template>

<style scoped>
.credentials-view {
  width: 100%;
  min-height: calc(100vh - 60px);
  padding: 2rem 1rem;
  font-family: sans-serif;
  color: #333;
  background-color: #fff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
}

.header-info {
  flex: 1;
}

.title {
  margin: 0;
  font-size: 2rem;
  color: #333;
}

.subtitle {
  margin: 0.5rem 0 0;
  color: #666;
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.update-button {
  background-color: #007bff;
  padding: 0.5rem 1rem;
  color: #fff;
}

.update-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.logout-button {
  background-color: #dd4422;
  padding: 0.5rem 1rem;
  color: #fff;
}

.logout-button:hover:not(:disabled) {
  background-color: #cc3311;
}

.credentials-list {
  max-width: 600px;
  margin: 0 auto;
}

.credential-item {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.credential-type {
  font-weight: 500;
  color: #333;
}

.credential-value {
  color: #666;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>    