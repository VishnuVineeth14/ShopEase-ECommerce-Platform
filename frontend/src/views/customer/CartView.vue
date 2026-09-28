<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold m-0">Shopping Cart</h2>
      <button class="btn btn-outline-danger" @click="clearCart" :disabled="!cartStore.cartItems.length">
        <i class="bi bi-trash"></i> Clear Cart
      </button>
    </div>
    
    <div v-if="cartStore.loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="!cartStore.cartItems.length" class="text-center py-5 card border-0 shadow-sm glass-effect">
      <div class="card-body py-5">
        <i class="bi bi-cart-x display-1 text-muted mb-3 d-block"></i>
        <h3 class="fw-bold">Your cart is empty</h3>
        <p class="text-muted mb-4">Looks like you haven't added anything to your cart yet.</p>
        <router-link to="/customer/products" class="btn btn-primary btn-lg px-4 hover-lift">Start Shopping</router-link>
      </div>
    </div>
    
    <div v-else class="row g-4">
      <div class="col-lg-8">
        <div class="card border-0 shadow-sm mb-3" v-for="item in cartStore.cartItems" :key="item.productId">
          <div class="card-body p-3">
            <div class="row align-items-center">
              <div class="col-md-2 col-3">
                <img :src="item.imageUrl" alt="Product" class="img-fluid rounded" style="height: 80px; object-fit: cover; width: 100%;">
              </div>
              <div class="col-md-4 col-9">
                <h6 class="fw-bold mb-1">{{ item.productName }}</h6>
                <div class="text-primary fw-bold">${{ item.price.toFixed(2) }}</div>
              </div>
              <div class="col-md-4 col-8 mt-3 mt-md-0">
                <div class="input-group input-group-sm w-75 mx-auto mx-md-0">
                  <button class="btn btn-outline-secondary" @click="updateQty(item, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
                  <input type="text" class="form-control text-center" readonly :value="item.quantity">
                  <button class="btn btn-outline-secondary" @click="updateQty(item, item.quantity + 1)">+</button>
                </div>
              </div>
              <div class="col-md-2 col-4 mt-3 mt-md-0 text-end">
                <div class="fw-bold mb-2">${{ (item.price * item.quantity).toFixed(2) }}</div>
                <button class="btn btn-sm btn-link text-danger p-0 text-decoration-none" @click="removeItem(item)">
                  <i class="bi bi-x-circle"></i> Remove
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-lg-4">
        <div class="card border-0 shadow-sm glass-effect position-sticky" style="top: 20px;">
          <div class="card-body p-4">
            <h5 class="fw-bold mb-4">Order Summary</h5>
            
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Subtotal ({{ cartStore.itemCount }} items)</span>
              <span class="fw-medium">${{ cartStore.cartTotal.toFixed(2) }}</span>
            </div>
            
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Shipping</span>
              <span class="text-success fw-medium">Free</span>
            </div>
            
            <hr>
            
            <div class="d-flex justify-content-between mb-4">
              <span class="fw-bold fs-5">Total</span>
              <span class="fw-bold fs-5 text-primary">${{ cartStore.cartTotal.toFixed(2) }}</span>
            </div>
            
            <router-link to="/customer/checkout" class="btn btn-primary btn-lg w-100 fw-bold hover-lift">
              Proceed to Checkout
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useCartStore } from '../../stores/cart'

const cartStore = useCartStore()

const updateQty = async (item, qty) => {
  try {
    await cartStore.updateQuantity(item.productId, qty)
  } catch (e) {
    alert(e.response?.data?.message || 'Error updating quantity')
  }
}

const removeItem = async (item) => {
  if (confirm('Remove this item from cart?')) {
    await cartStore.removeFromCart(item.productId)
  }
}

const clearCart = async () => {
  if (confirm('Are you sure you want to clear your cart?')) {
    await cartStore.clearCart()
  }
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>
