import { ref, readonly } from 'vue'

// Define the type for the onConfirm callback more explicitly
type ConfirmCallback = (() => Promise<void>) | (() => void)

export function useDialog() {
  const showDialog = ref(false)
  const dialogTitle = ref('')
  const dialogMessage = ref('')
  const showCancelButton = ref(true)
  const confirmText = ref('Confirm') // Add confirm text state
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
    showCancelButton.value = options.showCancel ?? true // Default to true if not provided
    confirmText.value = options.confirmButtonText ?? (showCancelButton.value ? 'Confirm' : 'OK') // Set default based on cancel button
    showDialog.value = true
  }

  function closeDialog() {
    showDialog.value = false
    // Optional: Reset state after a short delay to allow for transitions
    // setTimeout(() => {
    //   dialogTitle.value = '';
    //   dialogMessage.value = '';
    //   confirmCallback.value = null;
    //   confirmText.value = 'Confirm'; // Reset confirm text
    // }, 300); // Adjust delay as needed
  }

  async function handleConfirm() {
    if (confirmCallback.value) {
      try {
        await Promise.resolve(confirmCallback.value()) // Handle both sync/async
      } catch (error) {
        console.error('Error during dialog confirmation callback:', error)
        // Optionally open *another* dialog here to show the error from the callback
        // openDialog('Error', 'An error occurred during the operation.', () => {}, { showCancel: false });
      } finally {
        closeDialog() // Ensure dialog closes even if callback throws
      }
    } else {
      closeDialog() // Close if there's no callback
    }
  }

  // Expose state and methods
  // Use readonly() for state properties that shouldn't be directly mutated from the component
  return {
    showDialog: readonly(showDialog),
    dialogTitle: readonly(dialogTitle),
    dialogMessage: readonly(dialogMessage),
    showCancelButton: readonly(showCancelButton),
    confirmText: readonly(confirmText), // Expose confirmText
    openDialog,
    closeDialog,
    handleConfirm,
  }
}
