<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import mapboxgl from 'mapbox-gl'
import type { Location } from '@/service/itemService'

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN

const props = defineProps<{
  initialLocation?: Location
}>()

const emit = defineEmits<{
  (e: 'update:location', location: Location): void
}>()

const mapContainer = ref<HTMLElement | null>(null)
let map: mapboxgl.Map | null = null
let marker: mapboxgl.Marker | null = null

const defaultLocation = { latitude: 63.44, longitude: 10.399 }
const center = props.initialLocation ?? defaultLocation

onMounted(() => {
  if (mapContainer.value) {
    map = new mapboxgl.Map({
      container: mapContainer.value,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [center.longitude, center.latitude],
      zoom: 9,
    })

    marker = new mapboxgl.Marker({ color: '#007bff' })
      .setLngLat([center.longitude, center.latitude])
      .addTo(map)

    map.on('click', (e) => {
      const coords = e.lngLat
      if (marker) {
        marker.setLngLat(coords)
      } else {
        marker = new mapboxgl.Marker({ color: '#007bff' }).setLngLat(coords).addTo(map)
      }
      emit('update:location', { latitude: coords.lat, longitude: coords.lng })
    })

    map.on('click', () => {})
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
  <div ref="mapContainer" class="new-product-map-container"></div>
</template>

<style scoped>
.new-product-map-container {
  width: 100%;
  height: 300px;
  border-radius: 8px;
}
</style>
