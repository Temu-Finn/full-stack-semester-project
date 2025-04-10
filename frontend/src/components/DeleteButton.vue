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
  background-color: #ff3c3c;
  border: none;
  border-radius: 2.5rem;
  height: 2.5rem;
  width: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-button img {
  width: 1.5rem;
  height: 1.5rem;
}

.delete-button:hover {
  background-color: red;
  cursor: pointer;
}
</style>
