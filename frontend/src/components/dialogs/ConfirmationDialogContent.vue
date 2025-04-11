<template>
  <div class="confirmation-dialog">
    <h3 v-if="title" class="dialog-title">{{ title }}</h3>
    <p class="dialog-message">{{ message }}</p>
    <div class="dialog-actions">
      <button v-if="showCancel !== false" @click="dialogStore.cancel()" class="btn btn-secondary">
        {{ cancelText || $t('dialog.cancel') }}
      </button>
      <button @click="dialogStore.confirm()" class="btn btn-primary">
        {{ confirmText || $t('dialog.confirm') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDialogStore } from '@/stores/dialog'

const dialogStore = useDialogStore()

defineProps<{
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  showCancel?: boolean
}>()
</script>

<style scoped>
.confirmation-dialog {
  text-align: center;
}

.dialog-title {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 1.25rem;
  font-weight: 600;
}

.dialog-message {
  margin-bottom: 25px;
  font-size: 1rem;
  line-height: 1.6;
  white-space: pre-wrap;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: background-color 0.2s ease;
  min-width: 80px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.dialog-actions button.btn-primary {
  order: 1;
}
.dialog-actions button.btn-secondary {
  order: 0;
}
</style>
