<template>
  <div class="image-uploader">
    <input
      id="images"
      ref="fileInput"
      type="file"
      accept="image/*"
      multiple
      style="display: none"
      @change="handleInputChange"
    />
    <label
      for="images"
      class="file-input-label"
      :class="{ 'window-dragging': isWindowDragging }"
      @click.prevent="triggerFileInput"
    >
      {{ imageUrls.length > 0 ? $t('newProduct.addImages') : $t('newProduct.selectImages') }}
    </label>
    <div v-if="imageUrls.length > 0" class="image-preview-grid">
      <div
        v-for="(url, index) in imageUrls"
        :key="url"
        class="image-preview-item"
        :class="{
          'primary-image': index === 0,
          dragging: index === draggedIndex,
          'drop-target': index === dropIndex && index !== draggedIndex,
        }"
        draggable="true"
        @dragstart="handleDragStart(index)"
        @dragover.prevent="handleDragOver($event, index)"
        @drop.prevent="handleDrop(index)"
        @dragend="handleDragEnd"
      >
        <img :src="url" alt="Product Preview" class="image-preview" />
        <button class="remove-image-btn" type="button" @click="removeImage(index)">×</button>
        <span v-if="index === 0" class="primary-indicator">{{
          $t('newProduct.primaryIndicator')
        }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { logger } from '@/utils/logger'

interface Props {
  imageFiles: File[]
  imageUrls: string[]
}

const props = defineProps<Props>()
const emit = defineEmits(['update:imageFiles', 'update:imageUrls'])

const fileInput = ref<HTMLInputElement | null>(null)
const isWindowDragging = ref(false)
const draggedIndex = ref<number | null>(null)
const dropIndex = ref<number | null>(null)

watch(
  () => [...props.imageUrls],
  (newUrls, oldUrls) => {
    if (oldUrls) {
      oldUrls.forEach((url) => {
        if (!newUrls.includes(url)) {
          URL.revokeObjectURL(url)
          logger.debug(`Revoked object URL: ${url}`)
        }
      })
    }
  },
  { deep: true, immediate: false },
)

const windowDragOver = (event: DragEvent) => {
  event.preventDefault()
  isWindowDragging.value = true
}

const windowDragLeave = (event: DragEvent) => {
  if (
    !event.relatedTarget ||
    event.target === document.documentElement ||
    event.clientY <= 0 ||
    event.clientX <= 0 ||
    event.clientX >= window.innerWidth ||
    event.clientY >= window.innerHeight
  ) {
    isWindowDragging.value = false
  }
}

const windowDrop = (event: DragEvent) => {
  event.preventDefault()
  isWindowDragging.value = false
  const files = event.dataTransfer?.files
  if (files && files.length > 0) {
    logger.debug('Files dropped on window', files)
    processFiles(files)
  } else {
    logger.debug('Window drop event without files.')
  }
}

onMounted(() => {
  window.addEventListener('dragover', windowDragOver)
  window.addEventListener('dragleave', windowDragLeave)
  window.addEventListener('drop', windowDrop)
  logger.debug('ImageUploader: Window drag/drop listeners added')
})

onUnmounted(() => {
  window.removeEventListener('dragover', windowDragOver)
  window.removeEventListener('dragleave', windowDragLeave)
  window.removeEventListener('drop', windowDrop)
  props.imageUrls.forEach((url) => URL.revokeObjectURL(url))
  logger.debug('ImageUploader: Window drag/drop listeners removed and URLs revoked')
})

const processFiles = (files: FileList | null) => {
  if (!files) return

  let addedCount = 0
  const newImageFiles = [...props.imageFiles]
  const newImageUrls = [...props.imageUrls]
  const availableSlots = 10 - newImageFiles.length

  if (availableSlots <= 0) {
    logger.warn('Maximum number of images (10) already reached.')
    return
  }

  let filesProcessed = 0
  for (const file of files) {
    if (filesProcessed >= availableSlots) {
      logger.warn(`Skipped file ${file.name} as the maximum limit of 10 images would be exceeded.`)
      continue
    }

    if (file.type.startsWith('image/')) {
      newImageFiles.push(file)
      const url = URL.createObjectURL(file)
      newImageUrls.push(url)
      logger.debug(`Created object URL: ${url} for file: ${file.name}`)
      addedCount++
      filesProcessed++
    } else {
      logger.warn(`Skipped non-image file: ${file.name}`)
    }
  }

  if (addedCount > 0) {
    emit('update:imageFiles', newImageFiles)
    emit('update:imageUrls', newImageUrls)
    logger.debug(`${addedCount} image(s) added.`)
  }
}

const handleInputChange = (event: Event) => {
  if (props.imageFiles.length >= 10) {
    return
  }

  const target = event.target as HTMLInputElement
  processFiles(target.files)
  target.value = ''
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const removeImage = (index: number) => {
  if (index >= 0 && index < props.imageUrls.length) {
    const urlToRemove = props.imageUrls[index]
    URL.revokeObjectURL(urlToRemove)
    logger.debug(`Revoked object URL on removal: ${urlToRemove}`)

    const newImageFiles = [...props.imageFiles]
    const newImageUrls = [...props.imageUrls]
    newImageFiles.splice(index, 1)
    newImageUrls.splice(index, 1)

    emit('update:imageFiles', newImageFiles)
    emit('update:imageUrls', newImageUrls)
  }
}

const handleDragStart = (index: number) => {
  draggedIndex.value = index
}

const handleDragOver = (event: DragEvent, index: number) => {
  event.preventDefault()
  dropIndex.value = index
}

const handleDrop = (index: number) => {
  if (draggedIndex.value === null || draggedIndex.value === index) {
    dropIndex.value = null
    return
  }

  const newImageFiles = [...props.imageFiles]
  const newImageUrls = [...props.imageUrls]

  const itemToMove = newImageFiles[draggedIndex.value]
  const urlToMove = newImageUrls[draggedIndex.value]

  newImageFiles.splice(draggedIndex.value, 1)
  newImageUrls.splice(draggedIndex.value, 1)

  newImageFiles.splice(index, 0, itemToMove)
  newImageUrls.splice(index, 0, urlToMove)

  emit('update:imageFiles', newImageFiles)
  emit('update:imageUrls', newImageUrls)

  draggedIndex.value = null
  dropIndex.value = null
}

const handleDragEnd = () => {
  draggedIndex.value = null
  dropIndex.value = null
}
</script>

<style scoped>
.image-uploader {
  margin-bottom: 25px;
}

.file-input-label {
  display: block;
  padding: 12px 15px;
  border: 1px dashed #dcdcdc;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  background-color: #f9f9f9;
  color: #555;
  transition:
    border-color 0.2s ease,
    background-color 0.2s ease;
  font-weight: 500;
}

.file-input-label:hover {
  border-color: #007bff;
  background-color: #f0f8ff;
}

.file-input-label.window-dragging {
  border-color: #007bff;
  border-style: solid;
  background-color: #e6f2ff;
  box-shadow: 0 0 10px rgba(0, 123, 255, 0.3);
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.image-preview-item {
  position: relative;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  background-color: #f0f0f0;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.image-preview {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 14px;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  padding: 0;
  opacity: 0.8;
  transition:
    background-color 0.2s ease,
    opacity 0.2s ease;
}

.remove-image-btn:hover {
  background-color: rgba(220, 53, 69, 0.9);
  opacity: 1;
}

.image-preview-item.primary-image {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.4);
}

.image-preview-item.dragging {
  opacity: 0.5;
  transform: scale(0.95);
}

.image-preview-item.drop-target {
  border-style: dashed;
  border-color: #28a745;
  background-color: #e9f5ec;
}

.primary-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 123, 255, 0.7);
  color: white;
  font-size: 0.75rem;
  text-align: center;
  padding: 2px 0;
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 6px;
  user-select: none;
}
</style>
