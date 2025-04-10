<template>
    <div class="processing-container">
      <h2>{{ t('vipps.processingTitle') }}</h2>
  
      <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
      <div v-if="status === 'pending'" class="spinner" />
  
      <div v-else-if="status === 'approved'" class="status-message approved">
        {{ t('vipps.success') }}
      </div>
  
      <div v-else-if="status === 'failed'" class="status-message failed">
        {{ t('vipps.failed') }}
      </div>
  
      <div v-else-if="status === 'notfound'" class="status-message failed">
        {{ t('vipps.referenceNotFound') }}
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
  
  const checkStatus = async (reference: string) => {
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
  
        case 'FAILED':
          status.value = 'failed'
          break
  
        case 'PENDING':
        default:
          setTimeout(() => location.reload(), 3000)
          break
      }
    } catch (error) {
      console.error('Vipps status check failed:', error)
      status.value = 'failed'
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
  </style>
  