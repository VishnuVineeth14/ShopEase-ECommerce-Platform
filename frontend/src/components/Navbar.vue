<template>
  <nav class="navbar navbar-expand-lg navbar-dark app-navbar shadow-sm">
    <div class="container">
      <router-link class="navbar-brand fw-bold d-flex align-items-center gap-2" :to="dashboardRoute">
        <i class="bi bi-cart3"></i> ShopEase
      </router-link>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-if="authStore.isAuthenticated">
          <!-- Customer Links -->
          <template v-if="authStore.isCustomer">
            <li class="nav-item">
              <router-link class="nav-link" to="/customer/dashboard"><i class="bi bi-grid-1x2 me-1"></i> Overview</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/customer/products"><i class="bi bi-shop me-1"></i> Shop</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/customer/orders"><i class="bi bi-box-seam me-1"></i> Orders</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/customer/wishlist"><i class="bi bi-heart me-1"></i> Wishlist</router-link>
            </li>
          </template>
          
          <!-- Seller Links -->
          <template v-if="authStore.isSeller">
            <li class="nav-item">
              <router-link class="nav-link" to="/seller/dashboard">Dashboard</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/seller/products">My Products</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/seller/orders">Orders</router-link>
            </li>
          </template>
          
          <!-- Admin Links -->
          <template v-if="authStore.isAdmin">
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/dashboard">Dashboard</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/users">Users</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/categories">Categories</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/orders">Orders</router-link>
            </li>
          </template>
        </ul>
        
        <div class="d-flex align-items-center gap-3" v-if="authStore.isAuthenticated">
          <router-link v-if="authStore.isCustomer" to="/customer/cart" class="nav-cart position-relative">
            <i class="bi bi-bag"></i>
            <span v-if="cartStore.itemCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
              {{ cartStore.itemCount }}
            </span>
          </router-link>
          
          <div class="dropdown">
            <button class="btn btn-light dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
              <div class="avatar" style="width: 30px; height: 30px; font-size: 12px;">
                {{ authStore.user?.name?.charAt(0).toUpperCase() }}
              </div>
              {{ authStore.user?.name }}
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2">
              <li><h6 class="dropdown-header">{{ authStore.user?.role }}</h6></li>
              <li><hr class="dropdown-divider"></li>
              <li><button class="dropdown-item text-danger" @click="handleLogout">
                <i class="bi bi-box-arrow-right me-2"></i>Logout
              </button></li>
            </ul>
          </div>
        </div>
        
        <div class="d-flex gap-2" v-else>
          <router-link to="/login" class="btn btn-outline-light">Login</router-link>
          <router-link to="/register" class="btn btn-light text-primary fw-medium">Register</router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const dashboardRoute = computed(() => {
  if (!authStore.isAuthenticated) return '/'
  if (authStore.isAdmin) return '/admin/dashboard'
  if (authStore.isSeller) return '/seller/dashboard'
  return '/customer/dashboard'
})

const handleLogout = () => {
  authStore.logout()
}
</script>
