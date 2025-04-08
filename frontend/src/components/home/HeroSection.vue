<script lang="ts" setup>
import Search from '@/components/home/Search.vue'
import Categories from '@/components/home/Categories.vue'
</script>

<template>
  <div class="hero">
    <h1 class="title">{{ $t('home.heroTitle') }}</h1>
    <Search />
    <span class="space"></span>
    <Categories />
  </div>
</template>

<style scoped>
.hero {
  width: 100%;
  max-width: 60rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: calc(100vh - 60px);
  justify-content: space-evenly;
  padding: 12vh 0;
}
.title {
  text-align: center;
  font-weight: bolder;
  font-size: 4em;
}
.space {
  margin-top: 1rem;
}
</style>
