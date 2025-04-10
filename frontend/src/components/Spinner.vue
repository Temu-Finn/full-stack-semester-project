<!-- src/components/Spinner.vue -->
<template>
    <div class="spinner" />
  </template>
  
  <script setup lang="ts">
  </script>
  
  <style scoped>
  .spinner {
    margin: 30px auto;
    border: 5px solid #eee;
    border-top: 5px solid #4c8bf5;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  </style>
  