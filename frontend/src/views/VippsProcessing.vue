<template>
    <div class="processing-container">
      <h2>{{ t('vipps.processingTitle') }}</h2>
      <p v-if="status === 'pending'">{{ t('vipps.pleaseWait') }}</p>
      <div v-if="status === 'pending'" class="spinner" />
      <div v-else-if="status === 'approved'" class="approved">
        {{ t('vipps.success') }}
      </div>
      <div v-else-if="status === 'failed'" class="failed">
        {{ t('vipps.failed') }}
      </div>
      <div v-else-if="status === 'notfound'" class="failed">
        {{ t('vipps.referenceNotFound') }}
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'
  import { checkVippsStatus } from '@/service/vippsService'
  
  const status = ref<'pending' | 'approved' | 'failed' | 'notfound'>('pending')
  const route = useRoute()
  const router = useRouter()
  const { t } = useI18n()
  
  const reference = route.query.ref as string | undefined
  
  onMounted(async () => {
    if (!reference) {
      status.value = 'notfound'
      return
    }
  
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
  }
  </style>
  