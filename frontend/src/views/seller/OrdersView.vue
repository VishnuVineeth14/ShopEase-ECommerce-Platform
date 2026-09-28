<template>
  <div class="container py-4">
    <h2 class="fw-bold mb-4">Seller Orders</h2>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="!orders.length" class="text-center py-5 card border-0 shadow-sm glass-effect">
      <div class="card-body py-5">
        <i class="bi bi-inbox display-1 text-muted mb-3 d-block"></i>
        <h3 class="fw-bold">No orders yet</h3>
        <p class="text-muted mb-0">Customers haven't placed any orders for your products yet.</p>
      </div>
    </div>
    
    <div v-else class="row g-4">
      <div class="col-12" v-for="order in orders" :key="order.id">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-light border-bottom-0 pt-3 pb-2 d-flex justify-content-between align-items-center">
            <div>
              <span class="text-muted small d-block">Order ID</span>
              <span class="fw-bold font-monospace">{{ order.id }}</span>
            </div>
            <div>
              <span class="text-muted small d-block text-end">Date</span>
              <span class="fw-bold">{{ new Date(order.createdAt).toLocaleString() }}</span>
            </div>
          </div>
          
          <div class="card-body">
            <div class="row">
              <div class="col-md-7">
                <h6 class="text-muted small text-uppercase fw-bold mb-3">Items from your store</h6>
                <div class="d-flex mb-3 pb-3 border-bottom" v-for="item in order.items" :key="item.productId">
                  <img :src="item.imageUrl" class="rounded me-3" style="width: 60px; height: 60px; object-fit: cover;">
                  <div>
                    <h6 class="fw-bold mb-1">{{ item.productName }}</h6>
                    <div class="text-muted small">Qty: {{ item.quantity }} | ${{ item.price.toFixed(2) }} each</div>
                  </div>
                </div>
                
                <div class="d-flex justify-content-between align-items-center">
                  <span class="fw-bold">Your Earnings from this Order:</span>
                  <span class="fs-5 fw-bold text-success">${{ order.totalAmount.toFixed(2) }}</span>
                </div>
              </div>
              
              <div class="col-md-5 border-start-md mt-4 mt-md-0 pl-md-4">
                <h6 class="text-muted small text-uppercase fw-bold mb-3">Customer Details</h6>
                <p class="mb-1"><i class="bi bi-person me-2"></i> {{ order.customerName }}</p>
                <p class="mb-1"><i class="bi bi-envelope me-2"></i> {{ order.customerEmail }}</p>
                <p class="mb-1"><i class="bi bi-telephone me-2"></i> {{ order.phone }}</p>
                <p class="mb-4"><i class="bi bi-geo-alt me-2"></i> {{ order.address }}</p>
                
                <h6 class="text-muted small text-uppercase fw-bold mb-2">Update Status</h6>
                <div class="input-group">
                  <select class="form-select" v-model="order.orderStatus">
                    <option value="PENDING">Pending</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="PACKED">Packed</option>
                    <option value="SHIPPED">Shipped</option>
                    <option value="DELIVERED">Delivered</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                  <button class="btn btn-primary" @click="updateStatus(order.id, order.orderStatus)" :disabled="updatingId === order.id">
                    <span v-if="updatingId === order.id" class="spinner-border spinner-border-sm"></span>
                    <span v-else>Update</span>
                  </button>
                </div>
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
const updatingId = ref(null)

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await api.get('/orders/seller')
    if (res.success) {
      orders.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const updateStatus = async (orderId, status) => {
  updatingId.value = orderId
  try {
    const res = await api.put(`/orders/seller/${orderId}/status`, { orderStatus: status })
    if (res.success) {
      alert('Status updated successfully')
    }
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to update status')
    fetchOrders() // revert select change
  } finally {
    updatingId.value = null
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
@media (min-width: 768px) {
  .border-start-md {
    border-left: 1px solid #dee2e6 !important;
  }
}
</style>
