import { ref, readonly } from 'vue'

type ConfirmCallback = (() => Promise<void>) | (() => void)

export function useDialog() {
  const showDialog = ref(false)
  const dialogTitle = ref('')
  const dialogMessage = ref('')
  const showCancelButton = ref(true)
  const confirmText = ref('Confirm')
  const confirmCallback = ref<ConfirmCallback | null>(null)

  function openDialog(
    title: string,
    message: string,
    onConfirm: ConfirmCallback,
    options: { showCancel?: boolean; confirmButtonText?: string } = {},
  ) {
    dialogTitle.value = title
    dialogMessage.value = message
    confirmCallback.value = onConfirm
    showCancelButton.value = options.showCancel ?? true
    confirmText.value = options.confirmButtonText ?? (showCancelButton.value ? 'Confirm' : 'OK')
    showDialog.value = true
  }

  function closeDialog() {
    showDialog.value = false
  }

  async function handleConfirm() {
    if (confirmCallback.value) {
      try {
        await Promise.resolve(confirmCallback.value())
      } catch (error) {
        console.error('Error during dialog confirmation callback:', error)
      } finally {
        closeDialog()
      }
    } else {
      closeDialog()
    }
  }

  return {
    showDialog: readonly(showDialog),
    dialogTitle: readonly(dialogTitle),
    dialogMessage: readonly(dialogMessage),
    showCancelButton: readonly(showCancelButton),
    confirmText: readonly(confirmText),
    openDialog,
    closeDialog,
    handleConfirm,
  }
}
