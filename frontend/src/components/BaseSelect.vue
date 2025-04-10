<template>
  <div class="form-group">
    <label v-if="label" :for="selectId">{{ label }}</label>
    <select
      :id="selectId"
      :value="modelValue"
      :class="{ 'is-invalid': error }"
      v-bind="$attrs"
      @change="updateValue"
    >
      <option v-if="placeholder" :value="null" disabled>{{ placeholder }}</option>
      <slot></slot>
    </select>
    <p v-if="error" class="field-error-message">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: string | number | null
  label?: string
  error?: string
  id?: string
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  label: '',
  error: '',
  id: undefined,
  placeholder: '',
})

const emit = defineEmits(['update:modelValue'])

const selectId = computed(() => props.id || `select-${Math.random().toString(36).substring(2, 9)}`)

const updateValue = (event: Event) => {
  const target = event.target as HTMLSelectElement
  // Handle potential number conversion if needed, though v-model handles it
  emit('update:modelValue', target.value)
}
</script>

<style scoped>
.form-group {
  margin-bottom: 25px;
  position: relative;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

select {
  width: 100%;
  padding: 12px 40px 12px 15px; /* Adjusted padding for arrow */
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: #fff;
  font-size: 1rem;
  color: #333;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='%23888' class='bi bi-chevron-down' viewBox='0 0 16 16'%3E%3Cpath fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px 16px;
}

select:focus {
  outline: none;
  border-color: #007bff;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(0, 123, 255, 0.2);
}

.field-error-message {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 6px;
  min-height: 1.2em;
}

select.is-invalid {
  border-color: #dc3545 !important;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(220, 53, 69, 0.2) !important;
}
</style>
