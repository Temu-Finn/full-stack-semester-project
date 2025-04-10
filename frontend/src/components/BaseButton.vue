<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    :style="buttonStyle"
    class="base-button"
    v-bind="$attrs"
  >
    <span v-if="loading" class="spinner"></span>
    <span v-else class="content">
      <span v-if="$slots.icon" class="icon-wrapper">
        <slot name="icon"></slot>
      </span>
      <p><slot></slot></p>
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  type?: 'button' | 'submit' | 'reset'
  disabled?: boolean
  loading?: boolean
  backgroundColor?: string
  textColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'button',
  disabled: false,
  loading: false,
  backgroundColor: '#007bff',
  textColor: '#ffffff',
})

// Use CSS variables for dynamic styling
const buttonStyle = computed(() => ({
  '--button-background-color': props.backgroundColor,
  '--button-text-color': props.textColor,
  // Add more variables as needed, e.g., for hover/active states if calculated
}))
</script>

<style scoped>
.base-button {
  width: 100%;
  padding: 10px 16px;
  color: var(--button-text-color);
  background-color: var(--button-background-color);
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: bold;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    transform 0.1s ease,
    opacity 0.2s ease,
    box-shadow 0.15s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  text-align: center;
  line-height: 1.5;
  position: relative;
  overflow: hidden;
}

.base-button:hover:not(:disabled) {
  filter: brightness(90%);
  transform: translateY(-1px);
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
}

.base-button:active:not(:disabled) {
  filter: brightness(80%);
  transform: translateY(0px);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.base-button:disabled {
  background-color: #cccccc;
  color: #666666;
  cursor: not-allowed;
  opacity: 0.6;
}

.content {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 1.8em;
}

.icon-wrapper :deep(svg),
.icon-wrapper :deep(img) {
  height: 100%;
  width: 100%;
  object-fit: contain;
}

.spinner {
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: var(--button-text-color);
  width: 18px;
  height: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.base-button p {
  font-weight: 600;
}
</style>
