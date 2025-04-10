<template>
  <Transition name="dialog-fade">
    <div v-if="store.isVisible" class="dialog-overlay" @click.self="handleOverlayClick">
      <div class="dialog-box">
        <component
          :is="store.component"
          v-if="store.component"
          v-bind="store.props"
          @close="handleComponentClose"
        />
        <button @click="store.cancel()" class="close-button" aria-label="Close dialog">
          &times;
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { useDialogStore } from '@/stores/dialog'

const store = useDialogStore()

const handleOverlayClick = () => {
  store.cancel()
}

const handleComponentClose = () => {
  store.cancel()
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
  backdrop-filter: blur(3px);
}

.dialog-box {
  position: relative; /* Needed for absolute positioning of close button */
  background-color: white;
  padding: 30px 35px; /* Adjusted padding */
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  min-width: 300px; /* Minimum width */
  max-width: 600px; /* Increased max width for potentially complex content */
  width: 90%;
  border: 1px solid #ddd;
  /* Removed text-align: center; content component will control its alignment */
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.8rem; /* Larger close icon */
  color: #888;
  cursor: pointer;
  line-height: 1;
  padding: 5px;
  transition: color 0.2s ease;
}

.close-button:hover {
  color: #333;
}

/* Basic transition for fade-in/out */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.3s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}

/* Optional: Add a subtle scale transition */
.dialog-fade-enter-active .dialog-box,
.dialog-fade-leave-active .dialog-box {
  transition: transform 0.3s ease;
}

.dialog-fade-enter-from .dialog-box,
.dialog-fade-leave-to .dialog-box {
  transform: scale(0.95);
}

/* Removed styles specific to title, message, actions as they are now part of the dynamic component */
</style>
