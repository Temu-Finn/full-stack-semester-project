import { defineStore } from 'pinia'
import { shallowRef, type Component } from 'vue'

// Type for the props that the dynamic component might accept
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type ComponentProps = Record<string, any> | null

// Type for the promise resolve/reject functions
type PromiseResolve<T = unknown> = (value: T | PromiseLike<T>) => void
type PromiseReject = (reason?: unknown) => void

interface DialogState {
  isVisible: boolean
  component: Component | null // Holds the actual component to render
  props: ComponentProps
  _resolve: PromiseResolve | null // Internal resolve function
  _reject: PromiseReject | null // Internal reject function
}

export const useDialogStore = defineStore('dialog', {
  state: (): DialogState => ({
    isVisible: false,
    // Use shallowRef for components to prevent deep reactivity analysis
    component: shallowRef(null),
    props: null,
    _resolve: null,
    _reject: null,
  }),
  actions: {
    /**
     * Shows a dialog with the specified component and props.
     * Returns a promise that resolves when the dialog is confirmed,
     * and rejects when it is cancelled or closed.
     * @param componentToShow The Vue component to display in the dialog.
     * @param componentProps Props to pass to the componentToShow.
     */
    show<T = unknown>(
      componentToShow: Component,
      componentProps: ComponentProps = null,
    ): Promise<T> {
      return new Promise<T>((resolve, reject) => {
        this.component = shallowRef(componentToShow) // Use shallowRef here too
        this.props = componentProps
        this._resolve = resolve as PromiseResolve // Cast is necessary here
        this._reject = reject
        this.isVisible = true
      })
    },

    /**
     * Hides the dialog and resets its state.
     * Typically called internally by confirm/cancel or by the dialog component itself.
     */
    hide() {
      this.isVisible = false
      // It's good practice to reset fully after closing
      // Adding a small delay can help if the dialog has closing transitions
      // setTimeout(() => {
      this.component = null
      this.props = null
      this._resolve = null
      this._reject = null
      // }, 300); // Adjust delay to match transition if needed
    },

    /**
     * Confirms the dialog action.
     * Resolves the promise returned by show() with the provided value.
     * @param value Optional value to resolve the promise with.
     */
    confirm(value: unknown = true) {
      // Default resolve value to true
      if (this._resolve) {
        this._resolve(value)
      }
      this.hide()
    },

    /**
     * Cancels the dialog action.
     * Rejects the promise returned by show().
     * @param reason Optional reason for rejection.
     */
    cancel(reason: unknown = 'cancelled') {
      // Default rejection reason
      if (this._reject) {
        this._reject(reason)
      }
      this.hide()
    },
  },
})
