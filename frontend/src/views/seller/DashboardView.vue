<template>
  <div class="container py-4">
    <h2 class="mb-4 fw-bold">Seller Dashboard</h2>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="stats" class="row g-4">
      <div class="col-md-3">
        <div class="card border-0 shadow-sm bg-primary text-white h-100">
          <div class="card-body p-4">
            <h6 class="text-uppercase text-white-50 fw-bold mb-3">Total Sales</h6>
            <h2 class="display-5 fw-bold mb-0">${{ stats.totalSales.toFixed(2) }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card border-0 shadow-sm bg-info text-white h-100">
          <div class="card-body p-4">
            <h6 class="text-uppercase text-white-50 fw-bold mb-3">Total Products</h6>
            <h2 class="display-4 fw-bold mb-0">{{ stats.totalProducts }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card border-0 shadow-sm bg-warning text-dark h-100">
          <div class="card-body p-4">
            <h6 class="text-uppercase text-black-50 fw-bold mb-3">Pending Orders</h6>
            <h2 class="display-4 fw-bold mb-0">{{ stats.pendingOrders }}</h2>
          </div>
        </div>
      </div>
      
      <div class="col-md-3">
        <div class="card border-0 shadow-sm bg-danger text-white h-100">
          <div class="card-body p-4">
            <h6 class="text-uppercase text-white-50 fw-bold mb-3">Low Stock</h6>
            <h2 class="display-4 fw-bold mb-0">{{ stats.lowStockProducts }}</h2>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const stats = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await api.get('/seller/dashboard')
    if (res.success) {
      stats.value = res.data
    }
  } catch (err) {
    console.error('Failed to fetch dashboard stats', err)
  } finally {
    loading.value = false
  }
})
</script>
