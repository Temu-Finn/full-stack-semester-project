<template>
    <div class="processing-container">
      <h2>{{ t('vipps.processingTitle') }}</h2>
<<<<<<< HEAD
  
      <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
      <div v-if="status === 'pending'" class="spinner" />
  
      <div v-else-if="status === 'approved'" class="status-message approved">
        {{ t('vipps.success') }}
      </div>
  
      <div v-else-if="status === 'failed'" class="status-message failed">
        {{ t('vipps.failed') }}
      </div>
  
      <div v-else-if="status === 'notfound'" class="status-message failed">
=======
      <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
      <div v-if="status === 'pending'" class="spinner" />
      <div v-else-if="status === 'approved'" class="approved">
        {{ t('vipps.success') }}
      </div>
      <div v-else-if="status === 'failed'" class="failed">
        {{ t('vipps.failed') }}
      </div>
      <div v-else-if="status === 'notfound'" class="failed">
>>>>>>> main
        {{ t('vipps.referenceNotFound') }}
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'
  import { checkVippsStatus } from '@/service/vippsService'
  
<<<<<<< HEAD
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
=======
  const status = ref<'pending' | 'approved' | 'failed' | 'notfound'>('pending')
  const route = useRoute()
  const router = useRouter()
  const { t } = useI18n()
  
  const reference = route.query.ref as string | undefined
  
  onMounted(async () => {
>>>>>>> main
    if (!reference) {
      status.value = 'notfound'
      return
    }
<<<<<<< HEAD
    checkStatus(reference)
=======
  
    try {
      const { status: vippsStatus } = await checkVippsStatus(reference)
  
      if (vippsStatus === 'APPROVED') {
        status.value = 'approved'
        setTimeout(() => {
          router.push('/order/success') // redirect to confirmation page
        }, 1500)
      } else if (vippsStatus === 'FAILED') {
        status.value = 'failed'
      } else {
        setTimeout(() => location.reload(), 3000)
      }
    } catch (err) {
      console.error('Error checking Vipps status:', err)
      status.value = 'failed'
    }
>>>>>>> main
  })
  </script>
  
  <style scoped>
  .processing-container {
    text-align: center;
    margin-top: 100px;
    font-size: 18px;
    padding: 20px;
  }
<<<<<<< HEAD
  
=======
>>>>>>> main
  .spinner {
    margin: 30px auto;
    border: 5px solid #eee;
    border-top: 5px solid #4c8bf5;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
  }
<<<<<<< HEAD
  
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
=======
  @keyframes spin {
    0% { transform: rotate(0deg) }
    100% { transform: rotate(360deg) }
  }
  .approved {
    color: green;
    margin-top: 20px;
    font-weight: bold;
  }
  .failed {
    color: red;
    margin-top: 20px;
    font-weight: bold;
>>>>>>> main
  }
  </style>
  