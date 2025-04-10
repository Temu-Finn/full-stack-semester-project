<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import mapboxgl from 'mapbox-gl'
import * as turf from '@turf/turf'
import type { Location } from '@/service/itemService'

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_TOKEN

// Props: initial map center and optional initial maxDistanceKm
const props = defineProps<{
  location: Location
  maxDistanceKm?: number
}>()

// Emits: when the location filter is updated, emit an object with latitude, longitude and maxDistanceKm.
const emit = defineEmits<{
  (
    e: 'update:locationFilter',
    payload: { latitude: number; longitude: number; maxDistanceKm: number },
  ): void
}>()

const mapContainer = ref<HTMLElement | null>(null)
let map: mapboxgl.Map | null = null
let marker: mapboxgl.Marker | null = null

// Local state for the selected location and radius
const selectedLat = ref(props.location.latitude)
const selectedLng = ref(props.location.longitude)
const radiusKm = ref(props.maxDistanceKm ?? 10) // default to 10 km

// IDs for the circle source and layer
const circleSourceId = 'circleSource'
const circleLayerId = 'circleLayer'

function updateCircle() {
  if (!map) return
  // Use Turf.js to create a circle polygon using the current point and radius
  const center = [selectedLng.value, selectedLat.value]
  const options = { steps: 64, units: 'kilometers' }
  const circleGeoJSON = turf.circle(center, radiusKm.value, options)
  if (map.getSource(circleSourceId)) {
    ;(map.getSource(circleSourceId) as mapboxgl.GeoJSONSource).setData(circleGeoJSON)
  } else {
    map.addSource(circleSourceId, {
      type: 'geojson',
      data: circleGeoJSON,
    })
    map.addLayer({
      id: circleLayerId,
      type: 'fill',
      source: circleSourceId,
      layout: {},
      paint: {
        'fill-color': '#007bff',
        'fill-opacity': 0.2,
      },
    })
  }
}

// Called when the slider is released (change event)
function onSliderChange() {
  // The circle updates continuously via v-model (if you wish, you can also call updateCircle() here),
  // then emit the new filter.
  updateCircle()
  emit('update:locationFilter', {
    latitude: selectedLat.value,
    longitude: selectedLng.value,
    maxDistanceKm: radiusKm.value,
  })
}

onMounted(() => {
  if (mapContainer.value) {
    map = new mapboxgl.Map({
      container: mapContainer.value,
      style: 'mapbox://styles/mapbox/streets-v12',
      center: [props.location.longitude, props.location.latitude],
      zoom: 9,
    })
    map.on('load', () => {
      // Place the initial marker.
      marker = new mapboxgl.Marker({ color: '#007bff' })
        .setLngLat([selectedLng.value, selectedLat.value])
        .addTo(map)
      updateCircle()
    })
    // Listen for click events on the map.
    map.on('click', (e) => {
      const lngLat = e.lngLat
      selectedLat.value = lngLat.lat
      selectedLng.value = lngLat.lng
      if (marker) {
        marker.setLngLat([selectedLng.value, selectedLat.value])
      } else {
        marker = new mapboxgl.Marker({ color: '#007bff' })
          .setLngLat([selectedLng.value, selectedLat.value])
          .addTo(map)
      }
      updateCircle()
      // Immediately emit the new location when marker changes.
      emit('update:locationFilter', {
        latitude: selectedLat.value,
        longitude: selectedLng.value,
        maxDistanceKm: radiusKm.value,
      })
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
  <div class="map-container">
    <div ref="mapContainer" class="map-container-inner"></div>
    <div class="radius-slider">
      <label for="radiusRange">Radius: {{ radiusKm }} km</label>
      <!-- Using @change to trigger update only when slider is released -->
      <input
        id="radiusRange"
        v-model.number="radiusKm"
        max="100"
        min="1"
        type="range"
        @change="onSliderChange"
      />
    </div>
  </div>
</template>

<style scoped>
.map-container {
  position: relative;
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}
.map-container-inner {
  width: 100%;
  height: 300px;
  border-radius: 12px;
}
.radius-slider {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.8);
  padding: 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
  width: 90%;
}
.radius-slider input[type='range'] {
  width: 100%;
}
</style>
