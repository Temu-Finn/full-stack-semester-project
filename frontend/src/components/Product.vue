<template>
  <div class="product-container">
    <a
      class="product-card"
      :class="{
        'status-reserved': product.status === 'reserved' || product.status === 'reserved_by_user',
        'status-sold': product.status === 'sold',
        'status-bought': product.status === 'bought',
        'status-archived': product.status === 'archived',
      }"
      :href="`/product/${product.id}`"
    >
      <div class="image-container">
        <img
          :alt="product.title"
          :src="product.image?.dataURL || '/placeholder.svg'"
          class="product-image"
        />
        <span v-if="product.status === 'sold'" class="status-badge sold">{{
          t('product.status.sold')
        }}</span>
        <span v-else-if="product.status === 'bought'" class="status-badge bought">{{
          t('product.status.bought')
        }}</span>
        <span
          v-else-if="product.status === 'reserved' || product.status === 'reserved_by_user'"
          class="status-badge reserved"
          >{{ t('product.status.reserved') }}</span
        >
        <span v-else-if="product.status === 'archived'" class="status-badge archived">{{
          t('product.status.archived')
        }}</span>
      </div>
      <div class="product-info">
        <h3 class="product-name">{{ product.title }}</h3>
        <div class="product-meta">
          <span class="product-location">{{ product.municipality }}</span>
          <div class="price-container">
            <img
              v-if="product.allowVippsBuy"
              src="/Vipps.svg"
              alt="Vipps Logo"
              class="vipps-logo"
              :title="t('product.vippsBuyAvailable')"
            />
            <span class="product-price">{{ product.price }} kr</span>
          </div>
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
  position: relative;
  width: 100%;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.image-container {
  position: relative;
  width: 100%;
  height: 180px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.status-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 0.8rem;
  font-weight: 600;
  z-index: 1;
}

.status-badge.sold {
  background-color: #d34242;
}

.status-badge.bought {
  background-color: #1fb726;
}

.status-badge.reserved {
  background-color: #027bff;
}

.status-badge.archived {
  background-color: #bdbdbd;
}

.status-sold .product-image,
.status-bought .product-image,
.status-archived .product-image {
  filter: grayscale(60%) opacity(75%);
}

.status-sold .product-info,
.status-bought .product-info,
.status-archived .product-info {
  opacity: 0.85;
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

.price-container {
  display: flex;
  align-items: center;
}

.vipps-logo {
  height: 24px;
  margin-right: 2px;
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
