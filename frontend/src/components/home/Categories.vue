<template>
  <div class="category-grid-container">
    <div v-if="isLoading" class="loading-indicator">Loading...</div>
    <div v-else class="category-grid">
      <div v-for="category in categories" :key="category.id" class="category-card-container">
        <router-link :to="`/search?category=${category.id}`" class="category-card">
          <span class="category-icon">{{ category.icon }}</span>
          <span class="category-name">{{ category.name }}</span>
        </router-link>
        <DeleteButton
          v-if="editStore.editMode"
          :onClick="() => handleDelete(category.id)"
          class="delete-button"
        />
      </div>
      <button
        v-if="editStore.editMode"
        class="add-category category-card"
        @click="handleAddCategory"
      >
        <span class="category-icon">+</span>
        <span class="category-name">Add Category</span>
      </button>
    </div>
  </div>
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
import { useEditStore } from '@/stores/edit'
import { useDialogStore } from '@/stores/dialog'
import ConfirmationDialogContent from '@/components/dialogs/ConfirmationDialogContent.vue'
import AddCategoryForm from '@/components/dialogs/AddCategoryForm.vue'

const editStore = useEditStore()
const dialogStore = useDialogStore()

const categories = ref<Category[]>([])
const isLoading = ref(false)

onMounted(async () => {
  isLoading.value = true
  try {
    categories.value = await getCategories()
  } catch (error) {
    console.error('Failed to load categories:', error)
    dialogStore.show(ConfirmationDialogContent, {
      title: 'Loading Error',
      message: 'Could not load categories.',
      showCancel: false,
      confirmText: 'OK',
    })
  } finally {
    isLoading.value = false
  }
})

async function handleDelete(id: number) {
  const categoryName = categories.value.find((c) => c.id === id)?.name || 'this category'

  try {
    await dialogStore.show(ConfirmationDialogContent, {
      title: 'Confirm Deletion',
      message: `Delete "${categoryName}"?`,
      confirmText: 'Delete',
      showCancel: true,
    })

    try {
      await deleteCategory(id)
      categories.value = categories.value.filter((category) => category.id !== id)
    } catch (error: any) {
      console.error('Deletion failed:', error)
      let errorDetail = 'Could not delete category.'
      if (error.response?.status === 409) {
        errorDetail = 'Category has associated items.'
      }
      dialogStore.show(ConfirmationDialogContent, {
        title: 'Deletion Failed',
        message: errorDetail,
        showCancel: false,
        confirmText: 'OK',
      })
    }
  } catch {
    console.log('Deletion cancelled')
  }
}

async function handleAddCategory() {
  try {
    const newCategoryData = await dialogStore.show<CreateCategory>(AddCategoryForm, {})

    try {
      isLoading.value = true
      const createdCategory = await createCategory(newCategoryData)
      categories.value.push(createdCategory)
    } catch (error: any) {
      console.error('Add category failed:', error)
      let errorDetail = 'Could not add category.'
      if (error.response?.status === 409) {
        errorDetail = 'Category name already exists.'
      }
      dialogStore.show(ConfirmationDialogContent, {
        title: 'Error Adding Category',
        message: errorDetail,
        showCancel: false,
        confirmText: 'OK',
      })
    } finally {
      isLoading.value = false
    }
  } catch {
    console.log('Add category cancelled')
  }
}
</script>

<style scoped>
.loading-indicator {
  text-align: center;
  padding: 2rem;
  font-size: 1.1rem;
  color: #555;
}

.category-grid-container {
  width: 100%;
  padding-bottom: 5rem;
}

.category-grid {
  margin: auto;
  display: grid;
  grid-gap: 24px;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  justify-items: center;
  max-width: 1200px;
  padding: 1rem;
}

.category-card-container {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.category-card {
  width: 100%;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 10px;
  aspect-ratio: 1 / 1;
  background-color: #fff;
  color: #333;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.08);
  transition:
    transform 0.2s ease-in-out,
    box-shadow 0.2s ease-in-out;
  cursor: pointer;
  text-decoration: none;
  overflow: hidden;
  box-sizing: border-box;
}

a.category-card {
  color: inherit;
  text-decoration: none;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.12);
}

.category-icon {
  font-size: 2rem;
  margin-bottom: 10px;
  line-height: 1;
}

.category-name {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  line-height: 1.3;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-height: 2.6em;
}

button.add-category {
  color: #555;
  border: 2px dashed #ccc;
  background-color: #f9f9f9;
  font-weight: 500;
}

button.add-category:hover {
  color: #007bff;
  border-color: #a0d0ff;
  background-color: #eef7ff;
}

/* Delete button is styled within Product.vue or globally now */

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
    grid-gap: 16px;
    padding: 0.5rem;
  }
  .category-card {
    min-height: 100px;
  }
  .category-icon {
    font-size: 1.75rem;
  }
  .category-name {
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-gap: 12px;
  }
  .category-icon {
    font-size: 1.5rem;
  }
  .category-name {
    font-size: 0.8rem;
  }
}
</style>
