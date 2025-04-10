<template>
  <div class="form-group">
    <label v-if="label" :for="textareaId">{{ label }}</label>
    <textarea
      :id="textareaId"
      :value="modelValue"
      :class="{ 'is-invalid': error }"
      v-bind="$attrs"
      @input="updateValue"
    ></textarea>
    <p v-if="error" class="field-error-message">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  modelValue: string | null
  label?: string
  error?: string
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  label: '',
  error: '',
  id: undefined,
})

const emit = defineEmits(['update:modelValue'])

const textareaId = computed(
  () => props.id || `textarea-${Math.random().toString(36).substring(2, 9)}`,
)

const updateValue = (event: Event) => {
  const target = event.target as HTMLTextAreaElement
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

textarea {
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
  resize: vertical;
  min-height: 120px;
  line-height: 1.5;
}

textarea:focus {
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

textarea.is-invalid {
  border-color: #dc3545 !important;
  box-shadow:
    inset 0 1px 2px rgba(0, 0, 0, 0.05),
    0 0 0 3px rgba(220, 53, 69, 0.2) !important;
}
</style>
