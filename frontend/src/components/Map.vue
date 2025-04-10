<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import mapboxgl from 'mapbox-gl'
import type { Location } from '@/service/itemService'
import type { ItemCard } from '@/service/itemService'

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN

const mapContainer = ref<HTMLElement | null>(null)
let map: mapboxgl.Map | null = null
let currentPopup: mapboxgl.Popup | null = null

const props = defineProps<{
  location?: Location
  items?: ItemCard[]
}>()

const defaultLocation = { latitude: 63.44, longitude: 10.399 }
const centerLocation = props.location ?? defaultLocation

const markers: mapboxgl.Marker[] = []

function addMarkers() {
  markers.forEach((marker) => marker.remove())
  markers.length = 0

  if (!props.items) return

  props.items.forEach((item) => {
    if (item.location) {
      const marker = new mapboxgl.Marker()
        .setLngLat([item.location.longitude, item.location.latitude])
        .addTo(map!)

      marker.getElement().addEventListener('click', (e) => {
        e.stopPropagation()
        if (currentPopup) {
          currentPopup.remove()
          currentPopup = null
        }
        const popupContent = `
          <div class="popup-content">
            <a href="/product/${item.id}" target="_blank">
              ${item.image ? `<img src="${item.image.dataURL}" alt="${item.title}" class="popup-image"/>` : ''}
              <h3>${item.title}</h3>
              <p>Price: ${item.price}</p>
            </a>
          </div>
        `
        currentPopup = new mapboxgl.Popup({ closeOnClick: false })
          .setLngLat([item.location.longitude, item.location.latitude])
          .setHTML(popupContent)
          .addTo(map!)
      })

      markers.push(marker)
    }
  })
}

onMounted(() => {
  if (mapContainer.value) {
    map = new mapboxgl.Map({
      container: mapContainer.value,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [centerLocation.longitude, centerLocation.latitude],
      zoom: 9,
    })

    map.on('load', () => {
      addMarkers()
    })

    map.on('click', () => {
      if (currentPopup) {
        currentPopup.remove()
        currentPopup = null
      }
    })
  }
})

// When the items prop changes, add markers again.
watch(
  () => props.items,
  () => {
    if (map) {
      addMarkers()
    }
  },
  { deep: true },
)

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
  height: 300px;
  border-radius: 12px;
}
.popup-content {
  font-family: sans-serif;
  text-align: center;
}
.popup-content a {
  text-decoration: none;
  color: inherit;
  display: block;
}
.popup-image {
  width: 100%;
  max-width: 200px;
  border-radius: 4px;
  margin-bottom: 0.5rem;
}
</style>
