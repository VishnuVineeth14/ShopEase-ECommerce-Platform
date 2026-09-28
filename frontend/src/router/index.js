import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/RegisterView.vue'),
    meta: { guest: true }
  },
  
  // Customer Routes
  {
    path: '/customer/dashboard',
    name: 'CustomerDashboard',
    component: () => import('../views/customer/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/products',
    name: 'CustomerProducts',
    component: () => import('../views/customer/ProductsView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/products/:id',
    name: 'CustomerProductDetails',
    component: () => import('../views/customer/ProductDetailsView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/cart',
    name: 'CustomerCart',
    component: () => import('../views/customer/CartView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/checkout',
    name: 'CustomerCheckout',
    component: () => import('../views/customer/CheckoutView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/orders',
    name: 'CustomerOrders',
    component: () => import('../views/customer/OrdersView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  {
    path: '/customer/wishlist',
    name: 'CustomerWishlist',
    component: () => import('../views/customer/WishlistView.vue'),
    meta: { requiresAuth: true, role: 'CUSTOMER' }
  },
  
  // Seller Routes
  {
    path: '/seller/dashboard',
    name: 'SellerDashboard',
    component: () => import('../views/seller/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'SELLER' }
  },
  {
    path: '/seller/products',
    name: 'SellerProducts',
    component: () => import('../views/seller/ProductsView.vue'),
    meta: { requiresAuth: true, role: 'SELLER' }
  },
  {
    path: '/seller/orders',
    name: 'SellerOrders',
    component: () => import('../views/seller/OrdersView.vue'),
    meta: { requiresAuth: true, role: 'SELLER' }
  },
  
  // Admin Routes
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/admin/UsersView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/categories',
    name: 'AdminCategories',
    component: () => import('../views/admin/CategoriesView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('../views/admin/OrdersView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Wait for init auth
  if (!authStore.user && localStorage.getItem('token')) {
    authStore.initAuth()
  }

  const isAuthenticated = authStore.isAuthenticated
  const userRole = authStore.userRole

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.guest && isAuthenticated) {
    if (userRole === 'ADMIN') next('/admin/dashboard')
    else if (userRole === 'SELLER') next('/seller/dashboard')
    else next('/customer/dashboard')
  } else if (to.meta.requiresAuth && to.meta.role && to.meta.role !== userRole) {
    if (userRole === 'ADMIN') next('/admin/dashboard')
    else if (userRole === 'SELLER') next('/seller/dashboard')
    else next('/customer/dashboard')
  } else {
    next()
  }
})

export default router
