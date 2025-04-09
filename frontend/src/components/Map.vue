<script setup lang="ts">
import { ref, onMounted, onUnmounted, defineProps } from 'vue'
import mapboxgl from 'mapbox-gl'
import type { Location } from '@/service/itemService'

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN

const mapContainer = ref<HTMLElement | null>(null)
let map: mapboxgl.Map | null = null

const props = defineProps<{
  location: Location
}>()

onMounted(() => {
  if (mapContainer.value) {
    const centerLng = props.location ? props.location.longitude : 10.4036
    const centerLat = props.location ? props.location.latitude : 63.4168
    console.log(props.location)

    map = new mapboxgl.Map({
      container: mapContainer.value,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [centerLng, centerLat],
      zoom: 9,
    })
  }
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
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
