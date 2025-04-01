<script setup lang="ts">

</script>

<template>
<nav class="nav-bar">
    <a  class="logo-section" href="/">
      <img width="36" src="../assets/logo.svg" alt="logo">
      <p>Temu Finn</p>
    </a>
  <div class="items-section">
    <a href="/new">Nytt produkt</a>
    <a href="/messages">Meldinger</a>
  </div>
    <a class="profile-section" href="/profile">
      <img width="36" src="../assets/logo.svg" alt="profile-icon">
      <p>Henrik</p>
    </a>
</nav>
</template>

<style scoped>
.nav-bar {
  position: fixed;
  width: 100%;
  max-width: 100vh;
  padding: 1vh;
  height: 10vh;
  top: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo-section {
  display: flex;
  gap:1rem;
  align-items: center;
}
.items-section {
  display: flex;
  gap: 2rem;
  vertical-align: middle;
}
.profile-section {
  display: flex;
  gap: 1rem;
  align-items: center;
}
</style>
