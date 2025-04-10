<template>
  <div class="product-container">
    <a :href="`/product/${product.id}`" class="product-card">
      <img
        :alt="product.title"
        :src="product.image?.dataURL || '/placeholder.svg'"
        class="product-image"
      />
      <div class="product-info">
        <h3 class="product-name">{{ product.title }}</h3>
        <div class="product-meta">
          <span class="product-location">{{ product.municipality }}</span>
          <span class="product-price">{{ product.price }} kr</span>
        </div>
      </div>
    </a>
    <DeleteButton v-if="editStore.editMode" :onClick="confirmAndDelete" />
  </div>
</template>

<script lang="ts" setup>
import { type ItemCard, deleteItem } from '@/service/itemService'
import { useEditStore } from '@/stores/edit'
import DeleteButton from './DeleteButton.vue'
import { useDialogStore } from '@/stores/dialog'
import ConfirmationDialogContent from '@/components/dialogs/ConfirmationDialogContent.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps<{
  product: ItemCard
}>()

const emit = defineEmits<{
  (e: 'delete', id: number): void
}>()

const editStore = useEditStore()
const dialogStore = useDialogStore()

async function confirmAndDelete() {
  try {
    await dialogStore.show(ConfirmationDialogContent, {
      title: t('dialog.deleteProductTitle'),
      message: t('dialog.confirmDeleteMessage', { item: props.product.title }),
      confirmText: t('dialog.delete'),
      cancelText: t('dialog.cancel'),
      showCancel: true,
    })

    try {
      await deleteItem(props.product.id)
      emit('delete', props.product.id)
    } catch (error) {
      console.error('Failed to delete item:', error)
      dialogStore.show(ConfirmationDialogContent, {
        title: t('dialog.deletionFailedTitle'),
        message: t('dialog.deletionFailedDefaultMessage'),
        showCancel: false,
        confirmText: t('dialog.ok'),
      })
    }
  } catch {
    console.log('Deletion cancelled')
  }
}
</script>

<style scoped>
.product-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  height: 100%;
  text-decoration: none;
  color: inherit;
  width: 100%;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 2.6em;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #777;
  margin-top: auto;
  padding-top: 0.5rem;
}

.product-price {
  font-weight: bold;
  color: #007bff;
}

.product-container {
  position: relative;
  height: 100%;
  width: 100%;
}
</style>
