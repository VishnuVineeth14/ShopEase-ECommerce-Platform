<template>
  <div class="container py-5">
    <div class="hero-panel p-4 p-md-5 mb-5 position-relative">
      <div class="hero-orb"></div>
      <div class="position-relative row align-items-center"><div class="col-lg-8"><div class="text-uppercase small fw-bold opacity-75 mb-2">Welcome back, {{ authStore.user?.name?.split(' ')[0] }}</div><h1 class="display-5 fw-bold mb-3">Make space for things you love.</h1><p class="lead opacity-75 mb-4">Discover thoughtful products, trusted sellers and a checkout that stays simple.</p><router-link to="/customer/products" class="btn btn-light text-primary fw-bold px-4"><i class="bi bi-arrow-right me-2"></i>Explore the store</router-link></div><div class="col-lg-4 d-none d-lg-block text-center"><i class="bi bi-bag-heart" style="font-size: 9rem; opacity: .2"></i></div></div>
    </div>
    <div class="d-flex justify-content-between align-items-end mb-3"><div><div class="page-eyebrow">Your space</div><h2 class="fw-bold mb-0">Account overview</h2></div><router-link to="/customer/orders" class="text-primary text-decoration-none fw-bold">View orders <i class="bi bi-arrow-up-right"></i></router-link></div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="stats" class="row g-4">
      <div class="col-md-3">
        <div class="stat-card h-100"><div class="card-body p-4"><div class="stat-icon mb-3"><i class="bi bi-box-seam"></i></div>
            <h6 class="text-muted text-uppercase fw-bold mb-2">Total Orders</h6><h2 class="display-5 fw-bold mb-0">{{ stats.totalOrders }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="stat-card h-100"><div class="card-body p-4"><div class="stat-icon mb-3" style="background:#fff4d8;color:#c48a00"><i class="bi bi-clock-history"></i></div>
            <h6 class="text-muted text-uppercase fw-bold mb-2">Pending Orders</h6><h2 class="display-5 fw-bold mb-0">{{ stats.pendingOrders }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="stat-card h-100"><div class="card-body p-4"><div class="stat-icon" style="background:#dff8ec;color:#15945b"><i class="bi bi-check2-circle"></i></div>
            <h6 class="text-muted text-uppercase fw-bold mb-2 mt-3">Completed Orders</h6><h2 class="display-5 fw-bold mb-0">{{ stats.completedOrders }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="stat-card h-100"><div class="card-body p-4"><div class="stat-icon" style="background:#e3f5ff;color:#1585b7"><i class="bi bi-bag"></i></div>
            <h6 class="text-muted text-uppercase fw-bold mb-2 mt-3">Items in Cart</h6><h2 class="display-5 fw-bold mb-0">{{ stats.cartCount }}</h2>
          </div>
        </div>
      </div>
    </div>
    
    <div class="row g-3 mt-5"><div class="col-md-4"><router-link to="/customer/wishlist" class="card border-0 h-100 p-4 text-decoration-none text-dark"><i class="bi bi-heart text-danger fs-3 mb-3"></i><h5 class="fw-bold">Your wishlist</h5><p class="text-muted mb-0">Save products you want to come back to.</p></router-link></div><div class="col-md-4"><router-link to="/customer/cart" class="card border-0 h-100 p-4 text-decoration-none text-dark"><i class="bi bi-cart3 text-primary fs-3 mb-3"></i><h5 class="fw-bold">Ready to checkout?</h5><p class="text-muted mb-0">Review your bag and place an order.</p></router-link></div><div class="col-md-4"><div class="card border-0 h-100 p-4"><i class="bi bi-shield-check text-success fs-3 mb-3"></i><h5 class="fw-bold">Shop with confidence</h5><p class="text-muted mb-0">Secure accounts and transparent order tracking.</p></div></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'
import { useCartStore } from '../../stores/cart'
import { useAuthStore } from '../../stores/auth'

const stats = ref(null)
const loading = ref(true)
const cartStore = useCartStore()
const authStore = useAuthStore()

onMounted(async () => {
  try {
    const res = await api.get('/customer/dashboard')
    if (res.success) {
      stats.value = res.data
    }
    await cartStore.fetchCart()
  } catch (err) {
    console.error('Failed to fetch dashboard stats', err)
  } finally {
    loading.value = false
  }
})
</script>
