<template>
  <div v-if="show" class="dialog-overlay" @click.self="cancelAction">
    <div class="dialog-box">
      <h3 v-if="title" class="dialog-title">{{ title }}</h3>
      <p class="dialog-message">{{ message }}</p>
      <div class="dialog-actions">
        <button v-if="showCancel" @click="cancelAction" class="btn btn-secondary">Cancel</button>
        <button @click="confirmAction" class="btn btn-primary">{{ confirmText }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, withDefaults } from 'vue'

interface Props {
  show: boolean
  title?: string
  message: string
  showCancel?: boolean // Added optional prop to hide cancel button
  confirmText?: string // Added optional prop for confirm button text
}

// Use withDefaults for default prop values
const props = withDefaults(defineProps<Props>(), {
  title: '',
  showCancel: true,
  confirmText: 'Confirm',
})

const emit = defineEmits(['confirm', 'cancel'])

const confirmAction = () => {
  emit('confirm')
}

const cancelAction = () => {
  // Only emit cancel if the button is shown or overlay is clicked
  if (props.showCancel) {
    emit('cancel')
  }
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6); /* Slightly darker overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050; /* High z-index */
  backdrop-filter: blur(3px); /* Optional: Add a blur effect */
}

.dialog-box {
  background-color: white;
  padding: 25px 30px; /* Increase padding */
  border-radius: 12px; /* Smoother border radius */
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2); /* More pronounced shadow */
  min-width: 320px; /* Slightly wider minimum */
  max-width: 500px; /* Max width */
  width: 90%; /* Responsive width */
  text-align: center;
  border: 1px solid #ddd; /* Subtle border */
}

.dialog-title {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  font-size: 1.25rem; /* Larger title */
  font-weight: 600;
}

.dialog-message {
  margin-bottom: 25px; /* More space before actions */
  color: #555;
  font-size: 1rem;
  line-height: 1.6; /* Better readability */
  white-space: pre-wrap; /* Allows line breaks in message */
}

.dialog-actions {
  display: flex;
  justify-content: flex-end; /* Align buttons to the right */
  gap: 12px; /* Space between buttons */
}

/* Shared button styles */
.btn {
  padding: 10px 20px; /* Larger padding */
  border: none;
  border-radius: 6px; /* Slightly more rounded */
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: all 0.2s ease; /* Smooth transition for all properties */
  min-width: 80px; /* Minimum button width */
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Primary (Confirm) Button */
.btn-primary {
  background-color: #007bff;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 123, 255, 0.3);
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
  box-shadow: 0 4px 8px rgba(0, 86, 179, 0.4);
  transform: translateY(-1px); /* Slight lift effect */
}

/* Secondary (Cancel) Button */
.btn-secondary {
  background-color: #6c757d;
  color: white;
  box-shadow: 0 2px 4px rgba(108, 117, 125, 0.3);
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
  box-shadow: 0 4px 8px rgba(84, 91, 98, 0.4);
  transform: translateY(-1px);
}

/* Adjust button order visually if needed, e.g., Confirm on the right */
.dialog-actions button.btn-primary {
  order: 1; /* Ensure primary button is last (right-most in flex-end) */
}
.dialog-actions button.btn-secondary {
  order: 0;
}
</style>
