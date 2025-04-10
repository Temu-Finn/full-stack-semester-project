<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useSessionStore, type User } from '@/stores/session'
import { useRouter } from 'vue-router'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import {
  updateUserEmail,
  updateUserName,
  changeUserPassword,
} from '@/service/userService'

const sessionStore = useSessionStore()
const { t } = useI18n()
const router = useRouter()

const user = ref<User | null>(null)
const credentials = ref<{ id: string; type: string; value: string }[]>([])
const editing = ref<{ [key: string]: boolean }>({
  name: false,
  email: false,
  password: false,
})

const form = ref({
  name: '',
  email: '',
  password: {
    old: '',
    new: '',
  },
})

const errors = ref({
  name: '',
  email: '',
  password: {
    old: '',
    new: '',
  },
})

onMounted(() => {
  if (sessionStore.user) {
    user.value = sessionStore.user
    credentials.value = [
      { id: '1', type: 'name', value: user.value.name },
      { id: '2', type: 'email', value: user.value.email },
      { id: '3', type: 'password', value: '*********' },
    ]
  }
})

const edit = (type: string) => {
  editing.value[type] = true
  if (type !== 'password') {
    form.value[type] = (user.value as any)[type]
  }
}

const cancelEdit = (type: string) => {
  editing.value[type] = false
  if (type === 'password') {
    form.value.password.old = ''
    form.value.password.new = ''
  }
}

const logout = () => {
  sessionStore.logout()
  router.push({ name: 'login' }) // Ensure this route exists
}

const submitUpdate = async (type: string) => {
  try {
    if (!user.value) throw new Error('No user loaded.')

    // Reset validation errors
    if (type === 'password') {
      errors.value.password.old = ''
      errors.value.password.new = ''
    } else {
      errors.value[type] = ''
    }

    // Validation
    if (type === 'name' && !form.value.name.trim()) {
      errors.value.name = t('validation.required', { field: t('credentials.name') })
      return
    }

    if (type === 'email' && !form.value.email.includes('@')) {
      errors.value.email = t('validation.email')
      return
    }

    if (type === 'password') {
      if (!form.value.password.old) {
        errors.value.password.old = t('validation.required', { field: t('credentials.oldPassword') })
        return
      }
      if (form.value.password.new.length < 8) {
        errors.value.password.new = t('validation.passwordMinLength')
        return
      }
    }

    // Update
    if (type === 'email') {
      await updateUserEmail({ email: form.value.email })
    } else if (type === 'name') {
      await updateUserName({ name: form.value.name })
    } else if (type === 'password') {
      await changeUserPassword({
        currentPassword: form.value.password.old,
        newPassword: form.value.password.new,
      })
    }

    alert(t('credentials.updatedLogout'))
    logout()
  } catch (err: any) {
    alert(`Failed to update ${type}: ${err?.message || 'Unknown error'}`)
  }
}
</script>

<template>
  <div class="credentials-view">
    <header class="header">
      <div class="header-info">
        <h1 class="title">{{ t('credentials.title') }}</h1>
        <p class="subtitle">{{ t('credentials.subtitle') }}</p>
      </div>
      <div class="header-actions">
        <BaseButton class="logout-button" @click="logout">
          {{ t('credentials.logout') }}
        </BaseButton>
      </div>
    </header>

    <section class="credentials-list">
      <div v-for="credential in credentials" :key="credential.id" class="credential-item">
        <span class="credential-type">{{ t(`credentials.${credential.type}`) }}</span>

        <template v-if="editing[credential.type]">
          <template v-if="credential.type === 'password'">
            <BaseInput
              v-model="form.password.old"
              :label="t('credentials.oldPassword')"
              type="password"
              :error="errors.password.old"
            />
            <BaseInput
              v-model="form.password.new"
              :label="t('credentials.newPassword')"
              type="password"
              :error="errors.password.new"
            />
          </template>
          <template v-else>
            <BaseInput
              v-model="form[credential.type]"
              :label="t(`credentials.${credential.type}`)"
              :type="credential.type === 'email' ? 'email' : 'text'"
              :error="errors[credential.type]"
            />
          </template>

          <BaseButton @click="submitUpdate(credential.type)">
            {{ t('credentials.saveAndLogout') }}
          </BaseButton>
          <BaseButton @click="cancelEdit(credential.type)">
            {{ t('credentials.cancel') }}
          </BaseButton>
        </template>

        <template v-else>
          <span class="credential-value">{{ credential.value }}</span>
          <BaseButton @click="edit(credential.type)">
            {{ t('credentials.edit') }}
          </BaseButton>
        </template>
      </div>
    </section>
  </div>
</template>

<style scoped>
.credentials-view {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: sans-serif;
  color: #333;
  background-color: #fff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
}

.header-info {
  flex: 1;
}

.title {
  margin: 0;
  font-size: 2rem;
  color: #333;
}

.subtitle {
  margin: 0.5rem 0 0;
  color: #666;
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.logout-button {
  background-color: #dd4422;
  padding: 0.5rem 1rem;
  color: #fff;
}

.logout-button:hover:not(:disabled) {
  background-color: #cc3311;
}

.credentials-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.credential-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.credential-type {
  font-weight: 500;
  color: #333;
}

.credential-value {
  color: #666;
}
</style>
