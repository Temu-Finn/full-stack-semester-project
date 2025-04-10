<template>
  <button @click="handleClick" class="delete-button">
    <img src="/trash.svg" alt="delete" />
  </button>
</template>

<script setup lang="ts">
const props = defineProps<{ onClick: () => void }>()

function handleClick() {
  props.onClick()
}
</script>

<style scoped>
.delete-button {
  position: absolute;
  top: -0.75rem;
  right: -0.75rem;
  background-color: #ff5252;
  border: none;
  border-radius: 2.5rem;
  height: 2.5rem;
  width: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background-color 0.2s ease,
    transform 0.1s ease;
}

.delete-button:hover {
  background-color: red;
  cursor: pointer;
  transform: translateY(-1px) scale(1.03);
}

.delete-button img {
  width: 1.5rem;
  height: 1.5rem;
}
</style>
