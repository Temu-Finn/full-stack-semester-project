<template>
  <div class="processing-container">
    <h2>{{ t('Processing Vipps sale') }}</h2>

    <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
    <div v-if="status === 'pending'" class="spinner" />

    <div v-else-if="status === 'approved'" class="status-message approved">
      {{ t('vipps.success') }}
    </div>

    <div v-else-if="status === 'failed'" class="status-message failed">
      {{ t('vipps.failed') }}
      <p></p>
      <button class="home-button" @click="goBackToItem">
        {{ t('Leave') }}
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
        // Retry up to 5 times with delay
        if (attempt < 5) {
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

  checkStatus(reference)
})
</script>

<style scoped>
.processing-container {
  text-align: center;
  margin-top: 100px;
  font-size: 18px;
  padding: 20px;
}

.spinner {
  margin: 30px auto;
  border: 5px solid #eee;
  border-top: 5px solid #4c8bf5;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
