<template>
  <div class="container py-5">
    <div class="page-eyebrow">Operations</div><h1 class="fw-bold mb-1">All orders</h1><p class="text-muted mb-4">Monitor marketplace activity from one place.</p>
    <div v-if="loading" class="text-center py-5"><div class="spinner-border text-primary"></div></div>
    <div v-else class="card border-0"><div class="table-responsive"><table class="table align-middle mb-0"><thead><tr><th>Order</th><th>Customer</th><th>Date</th><th>Payment</th><th>Status</th><th>Total</th></tr></thead><tbody><tr v-for="order in orders" :key="order.id"><td class="font-monospace">#{{ order.id.slice(-8).toUpperCase() }}</td><td><strong>{{ order.customerName }}</strong><small class="d-block text-muted">{{ order.customerEmail }}</small></td><td>{{ new Date(order.createdAt).toLocaleDateString() }}</td><td><span class="badge bg-light text-dark">{{ order.paymentStatus }}</span></td><td><span class="badge" :class="statusClass(order.orderStatus)">{{ order.orderStatus }}</span></td><td class="fw-bold">${{ order.totalAmount.toFixed(2) }}</td></tr></tbody></table></div><div v-if="!orders.length" class="text-center text-muted py-5">No orders found.</div></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'
const orders = ref([]); const loading = ref(true)
const statusClass = status => ({ PENDING: 'bg-warning text-dark', CONFIRMED: 'bg-info text-dark', PACKED: 'bg-primary', SHIPPED: 'bg-primary', DELIVERED: 'bg-success', CANCELLED: 'bg-danger' }[status] || 'bg-secondary')
onMounted(async () => { try { const res = await api.get('/admin/orders'); if (res.success) orders.value = res.data } finally { loading.value = false } })
</script>
