<template>
  <div class="container py-4">
    <h2 class="fw-bold mb-4">My Orders</h2>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="!orders.length" class="text-center py-5 card border-0 shadow-sm glass-effect">
      <div class="card-body py-5">
        <i class="bi bi-box-seam display-1 text-muted mb-3 d-block"></i>
        <h3 class="fw-bold">No orders yet</h3>
        <p class="text-muted mb-4">You haven't placed any orders.</p>
        <router-link to="/customer/products" class="btn btn-primary px-4">Start Shopping</router-link>
      </div>
    </div>
    
    <div v-else class="row g-4">
      <div class="col-12" v-for="order in orders" :key="order.id">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white border-bottom-0 pt-4 pb-0 d-flex justify-content-between align-items-center">
            <div>
              <span class="text-muted small">Order #{{ order.id.substring(order.id.length - 8).toUpperCase() }}</span>
              <h5 class="fw-bold mb-0">{{ new Date(order.createdAt).toLocaleDateString() }}</h5>
            </div>
            <div class="text-end">
              <span class="badge px-3 py-2" :class="getStatusClass(order.orderStatus)">{{ order.orderStatus }}</span>
            </div>
          </div>
          
          <div class="card-body pt-3">
            <div class="row">
              <div class="col-md-8">
                <div class="d-flex mb-3 border-bottom pb-3" v-for="item in order.items" :key="item.productId">
                  <img :src="item.imageUrl" class="rounded me-3" style="width: 60px; height: 60px; object-fit: cover;">
                  <div>
                    <h6 class="fw-bold mb-1">{{ item.productName }}</h6>
                    <div class="text-muted small">Qty: {{ item.quantity }} | ${{ item.price.toFixed(2) }} each</div>
                  </div>
                </div>
              </div>
              <div class="col-md-4 border-start">
                <div class="mb-2">
                  <span class="text-muted small d-block">Total Amount</span>
                  <span class="fw-bold fs-5 text-primary">${{ order.totalAmount.toFixed(2) }}</span>
                </div>
                <div class="mb-3">
                  <span class="text-muted small d-block">Payment Status</span>
                  <span class="badge" :class="order.paymentStatus === 'COMPLETED' ? 'bg-success' : 'bg-warning text-dark'">
                    {{ order.paymentStatus }}
                  </span>
                </div>
                
                <button 
                  v-if="order.orderStatus === 'PENDING' || order.orderStatus === 'CONFIRMED'" 
                  class="btn btn-outline-danger btn-sm w-100 mt-2" 
                  @click="cancelOrder(order.id)">
                  Cancel Order
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const orders = ref([])
const loading = ref(true)

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await api.get('/orders/customer')
    if (res.success) {
      orders.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const cancelOrder = async (orderId) => {
  if (confirm('Are you sure you want to cancel this order?')) {
    try {
      const res = await api.put(`/orders/customer/${orderId}/cancel`)
      if (res.success) {
        alert('Order cancelled')
        fetchOrders()
      }
    } catch (e) {
      alert(e.response?.data?.message || 'Failed to cancel order')
    }
  }
}

const getStatusClass = (status) => {
  const map = {
    'PENDING': 'bg-warning text-dark',
    'CONFIRMED': 'bg-info text-dark',
    'PACKED': 'bg-primary',
    'SHIPPED': 'bg-primary',
    'DELIVERED': 'bg-success',
    'CANCELLED': 'bg-danger'
  }
  return map[status] || 'bg-secondary'
}

onMounted(() => {
  fetchOrders()
})
</script>
