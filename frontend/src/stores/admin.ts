import { ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { useSessionStore } from './session'

export const useAdminStore = defineStore('admin', () => {
  const editMode = ref(false)
  const sessionStore = useSessionStore()

  function toggleEditMode(value?: boolean) {
    if (!sessionStore.isAdmin) {
      console.warn('User is not authorized to change edit mode.')
      editMode.value = false
      return
    }
    editMode.value = value !== undefined ? value : !editMode.value
  }

  watch(
    () => sessionStore.isAdmin,
    (isAdmin) => {
      if (!isAdmin) {
        editMode.value = false
      }
    },
  )

  return { editMode, toggleEditMode }
})
