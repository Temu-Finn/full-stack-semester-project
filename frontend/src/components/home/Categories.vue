<template>
  <div class="category-grid-container">
    <div class="category-grid">
      <div v-for="category in categories" :key="category.id" class="category-card-container">
        <router-link :to="`/search?category=${category.id}`" @click.prevent class="category-card">
          <span class="category-icon">{{ category.icon }}</span>
          <span class="category-name">{{ category.name }}</span>
        </router-link>
        <DeleteButton
          v-if="editStore.editMode"
          :onClick="() => handleDelete(category.id)"
          class="delete-button"
        />
      </div>
      <button class="add-category category-card" @click="handleAddCategory">
        <span class="category-icon">+</span>
        <span class="category-name">Add Category</span>
      </button>
    </div>
  </div>

  <Dialog
    :show="showDialog"
    :title="dialogTitle"
    :message="dialogMessage"
    :showCancel="showCancelButton"
    :confirmText="confirmText"
    @confirm="handleConfirm"
    @cancel="closeDialog"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  getCategories,
  deleteCategory,
  type Category,
  createCategory,
  type CreateCategory,
} from '@/service/categoryService'
import DeleteButton from '../DeleteButton.vue'
import Dialog from '../Dialog.vue'
import { useEditStore } from '@/stores/edit'
import { useDialog } from '@/composables/useDialog.ts'

const editStore = useEditStore()
const {
  showDialog,
  dialogTitle,
  dialogMessage,
  showCancelButton,
  confirmText,
  openDialog,
  closeDialog,
  handleConfirm,
} = useDialog()

const categories = ref<Category[]>([])

onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch (error) {
    console.error('Failed to load categories:', error)
    openDialog(
      'Loading Error',
      'Could not load categories. Please try refreshing the page.',
      () => {},
      { showCancel: false },
    )
  }
})

async function handleDelete(id: number) {
  const categoryToDelete = categories.value.find((c) => c.id === id)
  if (!categoryToDelete) {
    console.error('Category to delete not found:', id)
    openDialog('Error', 'Could not find the category to delete.', () => {}, { showCancel: false })
    return
  }

  openDialog(
    'Confirm Deletion',
    `Are you sure you want to delete the category "${categoryToDelete.name}"?`,
    async () => {
      try {
        await deleteCategory(id)
        categories.value = categories.value.filter((category) => category.id !== id)
        openDialog(
          'Deleted',
          `Category "${categoryToDelete.name}" has been deleted successfully.`,
          () => {},
          { showCancel: false },
        )
      } catch (error: any) {
        console.error('Deletion failed:', error)
        let errorMessage = 'Could not delete the category.'
        if (error.response && error.response.status === 409) {
          errorMessage =
            'There might be items still associated with this category. Please ensure it is empty before deleting.'
        } else if (error.message) {
          errorMessage = `Deletion failed: ${error.message}`
        }
        openDialog('Deletion Failed', errorMessage, () => {}, { showCancel: false })
      }
    },
    { showCancel: true, confirmButtonText: 'Delete' },
  )
}

async function handleAddCategory() {
  const tempName = `New Category ${Date.now() % 1000}`
  const categoryData: CreateCategory = {
    icon: '🆕',
    name: tempName,
    description: 'A newly added category',
  }

  openDialog(
    'Add New Category',
    `Do you want to create a new category named "${categoryData.name}"? 
(You can edit details later)`,
    async () => {
      try {
        const newCategory = await createCategory(categoryData)
        categories.value.push(newCategory)
        openDialog('Success', `Category "${newCategory.name}" was added successfully!`, () => {}, {
          showCancel: false,
        })
      } catch (error: any) {
        console.error('Add category failed:', error)
        let errorMessage = 'An unexpected error occurred while adding the category.'
        if (error.response && error.response.status === 409) {
          errorMessage =
            'A category with this name might already exist. Please choose a unique name if you try again.'
        } else if (error.message) {
          errorMessage = `Failed to add category: ${error.message}`
        }
        openDialog('Error Adding Category', errorMessage, () => {}, { showCancel: false })
      }
    },
    { showCancel: true },
  )
}
</script>

<style scoped>
.category-grid-container {
  width: 100%;
  padding-bottom: 5rem;
}

.category-grid-container h2 {
  text-align: center;
  color: #333;
}

.category-grid {
  margin: auto;
  display: grid;
  grid-gap: 24px;
  grid-template-columns: repeat(auto-fill, minmax(8rem, 1fr));
  justify-items: center;
}

.category-card {
  width: 8rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  aspect-ratio: 1 / 1;
  color: #333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition:
    transform 0.1s ease-in-out,
    box-shadow 0.1s ease-in-out;
  cursor: pointer;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.category-icon {
  font-size: 1.5rem;
  margin-bottom: 8px;
  line-height: 1;
}

.category-name {
  font-size: 1rem;
  font-weight: 500;
  text-align: center;
  line-height: 1.2;
}

@media (max-width: 600px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(6rem, 1fr));
  }
  .category-card {
    width: 6rem;
  }
  .category-icon {
    font-size: 1rem;
  }
  .category-name {
    font-size: 0.8rem;
  }
}

.category-card-container {
  position: relative;
}

.add-category {
  color: #777;
  border: 2px dashed #e0e0e0;
  background-color: transparent;
}

.add-category:hover {
  color: #027bff;
  border-color: #71b5ff;
}
</style>
