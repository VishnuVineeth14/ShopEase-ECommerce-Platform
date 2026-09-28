<template>
  <div class="container py-5">
    <div class="d-flex justify-content-between align-items-end mb-4">
      <div><div class="page-eyebrow">Saved for later</div><h1 class="fw-bold mb-1">Your wishlist</h1><p class="text-muted mb-0">Keep the pieces you love close by.</p></div>
      <router-link to="/customer/products" class="btn btn-outline-primary"><i class="bi bi-shop me-2"></i>Continue shopping</router-link>
    </div>
    <div v-if="loading" class="text-center py-5"><div class="spinner-border text-primary"></div></div>
    <div v-else-if="!items.length" class="card border-0 text-center py-5"><div class="card-body py-5"><i class="bi bi-heart display-3 text-primary mb-3 d-block"></i><h3 class="fw-bold">Nothing saved yet</h3><p class="text-muted">Tap the heart on a product to build your personal collection.</p></div></div>
    <div v-else class="row g-4">
      <div v-for="product in items" :key="product.id" class="col-sm-6 col-lg-3">
        <div class="product-card bg-white h-100">
          <router-link :to="`/customer/products/${product.id}`"><img :src="product.imageUrl" class="w-100 product-image" :alt="product.name"></router-link>
          <div class="p-3 d-flex flex-column h-100"><span class="badge bg-light text-primary align-self-start mb-2">{{ product.categoryName }}</span><h5 class="fw-bold mb-1">{{ product.name }}</h5><p class="small text-muted text-truncate-2">{{ product.description }}</p><div class="mt-auto d-flex align-items-center justify-content-between"><span class="fw-bold fs-5 text-primary">${{ product.price.toFixed(2) }}</span><div class="d-flex gap-2"><button class="btn btn-sm btn-primary" @click="moveToCart(product)" :disabled="product.stock <= 0"><i class="bi bi-bag-plus"></i></button><button class="btn btn-sm btn-outline-danger" @click="remove(product)"><i class="bi bi-heart-fill"></i></button></div></div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'
import { useCartStore } from '../../stores/cart'
const items = ref([]); const loading = ref(true); const cart = useCartStore()
const fetchWishlist = async () => { try { const res = await api.get('/wishlist'); if (res.success) items.value = res.data || [] } finally { loading.value = false } }
const remove = async (product) => { await api.delete(`/wishlist/remove/${product.id}`); items.value = items.value.filter(item => item.id !== product.id) }
const moveToCart = async (product) => { await api.post(`/wishlist/move-to-cart/${product.id}`); items.value = items.value.filter(item => item.id !== product.id); await cart.fetchCart() }
onMounted(fetchWishlist)
</script>
