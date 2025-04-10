<template>
  <div class="add-category-form">
    <h3 class="dialog-title">Add New Category</h3>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="category-name">Name:</label>
        <input
          id="category-name"
          v-model="categoryData.name"
          type="text"
          required
          placeholder="Enter category name"
        />
      </div>

      <div class="form-group">
        <label for="category-icon">Icon (Emoji):</label>
        <input
          id="category-icon"
          v-model="categoryData.icon"
          type="text"
          maxlength="2"
          placeholder="🌟"
        />
      </div>

      <div class="form-group">
        <label for="category-description">Description:</label>
        <textarea
          id="category-description"
          v-model="categoryData.description"
          rows="3"
          placeholder="Optional description"
        ></textarea>
      </div>

      <div class="dialog-actions">
        <button type="button" @click="dialogStore.cancel()" class="btn btn-secondary">
          Cancel
        </button>
        <button type="submit" class="btn btn-primary">Add Category</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useDialogStore } from '@/stores/dialog'
import type { CreateCategory } from '@/service/categoryService'

const dialogStore = useDialogStore()

const categoryData = reactive<CreateCategory>({
  name: '',
  icon: '',
  description: '',
})

const handleSubmit = () => {
  if (categoryData.name.trim()) {
    dialogStore.confirm(categoryData)
  }
}
</script>

<style scoped>
.add-category-form {
  padding: 10px;
}

.dialog-title {
  text-align: center;
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 1.3rem;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input[type='text'],
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input[type='text']:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #007bff;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
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
