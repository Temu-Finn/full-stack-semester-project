<script lang="ts" setup>
import { computed } from 'vue'

const props = defineProps<{
  modelValue: boolean
  label?: string
  activeColor?: string
  scale?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const uniqueId = `toggle-${Math.random().toString(36).substring(2, 9)}`

const scale = computed(() => props.scale ?? 1)

const baseHeight = 22
const baseWidth = 40
const baseHandleSize = 18
const baseOffset = 2

const cssVars = computed(() => {
  const currentScale = scale.value
  const handleSize = baseHandleSize * currentScale
  const height = baseHeight * currentScale
  const width = baseWidth * currentScale
  const offset = baseOffset * currentScale
  return {
    '--toggle-height': `${height}px`,
    '--toggle-width': `${width}px`,
    '--handle-size': `${handleSize}px`,
    '--handle-offset': `${offset}px`,
    '--handle-checked-left': `${width - handleSize - offset}px`,
    '--label-line-height': `${height}px`,
  }
})
</script>

<template>
  <div :style="cssVars" class="toggle-switch-container">
    <input
      :id="uniqueId"
      type="checkbox"
      class="toggle-checkbox-hidden"
      :checked="props.modelValue"
      @change="emit('update:modelValue', ($event.target as HTMLInputElement).checked)"
    />
    <label :for="uniqueId" class="toggle-label">
      <span class="toggle-handle"></span>
    </label>
    <span v-if="props.label" class="toggle-text-label">{{ label }}</span>
  </div>
</template>

<style scoped>
.toggle-switch-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toggle-checkbox-hidden {
  opacity: 0;
  position: absolute;
  width: 0;
  height: 0;
}

.toggle-label {
  display: inline-block;
  cursor: pointer;
  background-color: #ccc;
  border-radius: calc(var(--toggle-height) / 2);
  transition: background-color 0.2s ease;
  position: relative;
  user-select: none;
  height: var(--toggle-height);
  width: var(--toggle-width);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.toggle-handle {
  display: block;
  width: var(--handle-size);
  height: var(--handle-size);
  background-color: white;
  border-radius: 50%;
  transition: left 0.2s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  position: absolute;
  left: var(--handle-offset);
  top: var(--handle-offset);
  z-index: 1;
}

.toggle-checkbox-hidden:checked + .toggle-label {
  background-color: v-bind('props.activeColor ?? "#027BFF"');
}

.toggle-checkbox-hidden:checked + .toggle-label .toggle-handle {
  left: var(--handle-checked-left);
}

.toggle-text-label {
  color: #333;
  font-size: 15px;
  vertical-align: middle;
  line-height: var(--label-line-height);
}

.toggle-checkbox-hidden:focus-visible + .toggle-label {
  outline: 2px solid #027bff;
  outline-offset: 2px;
}
</style>
