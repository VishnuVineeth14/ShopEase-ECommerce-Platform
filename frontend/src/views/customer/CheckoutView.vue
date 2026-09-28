<template>
  <div class="container py-5">
    <h2 class="fw-bold mb-4">Checkout</h2>
    
    <div v-if="cartStore.cartItems.length === 0" class="alert alert-warning">
      Your cart is empty. Please add items to your cart before checking out.
      <br><br>
      <router-link to="/customer/products" class="btn btn-primary">Go to Products</router-link>
    </div>
    
    <div v-else class="row g-5">
      <div class="col-lg-7">
        <div class="card border-0 shadow-sm glass-effect mb-4">
          <div class="card-body p-4 p-md-5">
            <h4 class="fw-bold mb-4">Shipping Information</h4>
            
            <form @submit.prevent="placeOrder" id="checkout-form">
              <div class="mb-3">
                <label class="form-label text-muted fw-medium">Full Name</label>
                <input type="text" class="form-control bg-light" v-model="form.customerName" required>
              </div>
              
              <div class="mb-3">
                <label class="form-label text-muted fw-medium">Phone Number</label>
                <input type="tel" class="form-control bg-light" v-model="form.phone" required>
              </div>
              
              <div class="mb-4">
                <label class="form-label text-muted fw-medium">Delivery Address</label>
                <textarea class="form-control bg-light" rows="3" v-model="form.address" required></textarea>
              </div>
              
              <h4 class="fw-bold mb-4 mt-5">Payment Method</h4>
              
              <div class="form-check mb-3 p-3 border rounded">
                <input class="form-check-input ms-1" type="radio" name="payment" id="cod" value="CASH_ON_DELIVERY" v-model="form.paymentMethod" required>
                <label class="form-check-label ms-3 fw-medium d-flex align-items-center gap-2" for="cod">
                  <i class="bi bi-cash fs-4 text-success"></i> Cash on Delivery
                </label>
              </div>
              
              <div class="form-check mb-3 p-3 border rounded">
                <input class="form-check-input ms-1" type="radio" name="payment" id="card" value="CARD_PAYMENT" v-model="form.paymentMethod" required>
                <label class="form-check-label ms-3 fw-medium d-flex align-items-center gap-2" for="card">
                  <i class="bi bi-credit-card fs-4 text-primary"></i> Mock Card Payment
                </label>
              </div>
              
              <!-- Mock Card Details -->
              <div v-if="form.paymentMethod === 'CARD_PAYMENT'" class="p-4 bg-light rounded mt-3 mb-4 border">
                <p class="small text-muted mb-3"><i class="bi bi-info-circle"></i> This is a mock payment. No real transaction will occur.</p>
                <div class="mb-3">
                  <label class="form-label small">Card Number</label>
                  <input type="text" class="form-control" placeholder="0000 0000 0000 0000" value="4111 1111 1111 1111">
                </div>
                <div class="row">
                  <div class="col-6">
                    <label class="form-label small">Expiry</label>
                    <input type="text" class="form-control" placeholder="MM/YY" value="12/25">
                  </div>
                  <div class="col-6">
                    <label class="form-label small">CVV</label>
                    <input type="text" class="form-control" placeholder="123" value="123">
                  </div>
                </div>
              </div>
              
            </form>
          </div>
        </div>
      </div>
      
      <div class="col-lg-5">
        <div class="card border-0 shadow-sm glass-effect position-sticky" style="top: 20px;">
          <div class="card-body p-4">
            <h5 class="fw-bold mb-4">Order Summary</h5>
            
            <div class="d-flex justify-content-between mb-2 small" v-for="item in cartStore.cartItems" :key="item.productId">
              <span class="text-truncate" style="max-width: 200px;">{{ item.quantity }}x {{ item.productName }}</span>
              <span>${{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
            
            <hr class="my-4">
            
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Subtotal</span>
              <span class="fw-medium">${{ cartStore.cartTotal.toFixed(2) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-4">
              <span class="text-muted">Shipping</span>
              <span class="text-success fw-medium">Free</span>
            </div>
            
            <div class="d-flex justify-content-between mb-4 pb-4 border-bottom">
              <span class="fw-bold fs-4">Total</span>
              <span class="fw-bold fs-4 text-primary">${{ cartStore.cartTotal.toFixed(2) }}</span>
            </div>
            
            <button form="checkout-form" type="submit" class="btn btn-primary btn-lg w-100 fw-bold hover-lift" :disabled="loading">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Place Order
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useCartStore } from '../../stores/cart'
import api from '../../services/api'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const loading = ref(false)

const form = ref({
  customerName: authStore.user?.name || '',
  phone: authStore.user?.phone || '',
  address: authStore.user?.address || '',
  paymentMethod: 'CASH_ON_DELIVERY'
})

const placeOrder = async () => {
  loading.value = true
  try {
    const res = await api.post('/orders/customer/checkout', form.value)
    if (res.success) {
      await cartStore.fetchCart() // should be empty now
      alert('Order placed successfully!')
      router.push('/customer/orders')
    }
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to place order')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>
