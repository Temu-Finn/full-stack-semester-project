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

const editStore = useEditStore()

const categories = ref<Category[]>([])

onMounted(async () => {
  categories.value = await getCategories()
})

async function handleDelete(id: number) {
  try {
    await deleteCategory(id)
    categories.value = categories.value.filter((category) => category.id !== id)
  } catch (error) {
    alert('There are still items associated with this category. Please remove them first.')
  }
}

async function handleAddCategory() {
  const category: CreateCategory = {
    icon: '🌟',
    name: 'New Category',
    description: 'New Category Description',
  }
  try {
    const newCategory = await createCategory(category)
    categories.value.push(newCategory)
  } catch (error) {
    alert('Category name must be unique')
  }
}
</script>

<style scoped>
.category-grid-container {
  width: 100%;
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
