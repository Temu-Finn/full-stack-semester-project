import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NewProductView from '@/views/NewProductView.vue'
import MessagesView from '@/views/ChatView.vue'
import ProfileView from '@/views/ProfileView.vue'
import LogInView from '@/views/LogInView.vue'
import SignUpView from '@/views/SignUpView.vue'
import { useSessionStore } from '@/stores/session'
import ChatView from '@/views/ChatView.vue'
import ProductView from '@/views/ProductView.vue'
import SearchResultsView from '@/views/SearchResultsView.vue'
import UpdateCredentials from '@/views/UpdateCredentials.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LogInView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignUpView,
    },
    {
      path: '/new',
      name: 'new',
      component: NewProductView,
      meta: { requiresAuth: false },
    },
    {
      path: '/chat/:id',
      name: 'Chat',
      component: ChatView,
      meta: { requiresAuth: false },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/chat/1',
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true },
    },
    {
      path: '/product/:id',
      name: 'product',
      component: ProductView,
      meta: { requiresAuth: false },
    },
    {
      path: '/search',
      name: 'search',
      component: SearchResultsView,
      meta: { requiresAuth: false },
    },
    {
      path: '/vipps/processing',
      name: 'VippsProcessing',
      component: () => import('@/views/VippsProcessing.vue'),
    },
    {
      path: '/edit-user',
      name: 'UpdateCredentials',
      component: UpdateCredentials
    }     
  ],
})

router.beforeEach((to, from, next) => {
  const authStore = useSessionStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
