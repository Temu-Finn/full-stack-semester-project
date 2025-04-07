<script lang="ts">
import mapboxgl from 'mapbox-gl'
mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN

export default {
  mounted() {
    const map = new mapboxgl.Map({
      container: this.$refs.mapContainer,
      style: 'mapbox://styles/mapbox/streets-v12', // Replace with your preferred map style
      center: [-71.224518, 42.213995],
      zoom: 9,
    })

    this.map = map
  },
  unmounted() {
    this.map.remove()
    this.map = null
  },
}
</script>

<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<style scoped>
.map-container {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}
</style>
