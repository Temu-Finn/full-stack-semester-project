<template>
  <div class="processing-container">
    <h2>{{ t('vipps.processingTitle') }}</h2>

    <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
    <Spinner v-if="status === 'pending'" />

    <div v-else-if="status === 'approved'" class="status-message approved">
      {{ t('vipps.success') }}
    </div>

    <div v-else-if="status === 'failed'" class="status-message failed">
      {{ t('vipps.failed') }}
      <p></p>
      <button class="home-button" @click="goBackToItem">
        {{ t('vipps.leave') }}
      </button>
    </div>

    <div v-else-if="status === 'notfound'" class="status-message failed">
      {{ t('vipps.referenceNotFound') }}
      <button class="home-button" @click="goBackToItem">
        {{ t('vipps.goBackToItem') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { checkVippsStatus } from '@/service/vippsService'
import Spinner from '@/components/Spinner.vue'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()

type Status = 'pending' | 'approved' | 'failed' | 'notfound'
const status = ref<Status>('pending')

const reference = route.query.ref as string | undefined

const checkStatus = async (reference: string, attempt = 0) => {
  try {
    const { status: vippsStatus } = await checkVippsStatus(reference)

    switch (vippsStatus) {
      case 'APPROVED':
        status.value = 'approved'

        const itemId = localStorage.getItem('vippsPurchasedItemId')
        localStorage.removeItem('vippsPurchasedItemId')

        if (!itemId) {
          status.value = 'failed'
          return
        }

        setTimeout(() => {
          router.push(`/receipt/${itemId}`)
        }, 1500)
        break

      case 'PENDING':
        if (attempt < 10) {
          setTimeout(() => checkStatus(reference, attempt + 1), 3000)
        } else {
          status.value = 'failed'
        }
        break

      case 'FAILED':
      default:
        status.value = 'failed'
        break
    }
  } catch (error) {
    console.error('Vipps status check failed:', error)
    status.value = 'failed'
  }
}

const goBackToItem = () => {
  const itemId = localStorage.getItem('vippsPurchasedItemId')
  if (itemId) {
    router.push(`/product/${itemId}`)
  } else {
    router.push('/')
  }
}

onMounted(() => {
  if (!reference) {
    status.value = 'notfound'
    return
  }

  setTimeout(() => {
    checkStatus(reference)
  }, 5000)
})
</script>

<style scoped>
.processing-container {
  text-align: center;
  margin-top: 100px;
  font-size: 18px;
  padding: 20px;
}

.status-message {
  margin-top: 20px;
  font-weight: bold;
}

.approved {
  color: green;
}

.failed {
  color: red;
}

.home-button {
  margin-top: 16px;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4c8bf5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.home-button:hover {
  background-color: #3a71d8;
}
</style>
