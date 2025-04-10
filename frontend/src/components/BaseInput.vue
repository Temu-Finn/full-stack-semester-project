<template>
  <div class="form-group">
    <label v-if="label" :for="inputId">{{ label }}</label>
    <input
      :id="inputId"
      :type="type"
      :value="modelValue"
      :class="{ 'is-invalid': error }"
      v-bind="$attrs"
      @input="updateValue"
    />
    <p v-if="error" class="field-error-message">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: string | number | null
  label?: string
  type?: string
  error?: string
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  label: '',
  type: 'text',
  error: '',
  id: undefined,
})

const emit = defineEmits(['update:modelValue'])

const inputId = computed(() => props.id || `input-${Math.random().toString(36).substring(2, 9)}`)

const updateValue = (event: Event) => {
  const target = event.target as HTMLInputElement
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

input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: #fff;
  font-size: 1rem;
  color: #333;
}

input:focus {
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

input.is-invalid {
  border-color: #dc3545 !important;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(220, 53, 69, 0.2) !important;
}
</style>
