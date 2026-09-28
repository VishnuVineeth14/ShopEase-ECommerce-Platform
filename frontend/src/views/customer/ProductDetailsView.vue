<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between mb-4">
      <h2 class="fw-bold">Product Details</h2>
      <button class="btn btn-outline-secondary" @click="$router.go(-1)">Back</button>
    </div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="product" class="row g-5">
      <div class="col-md-6">
        <img :src="product.imageUrl" class="img-fluid rounded shadow-sm w-100" style="object-fit: cover; max-height: 500px;" alt="Product Image">
      </div>
      
      <div class="col-md-6">
        <span class="badge bg-primary mb-2">{{ product.categoryName }}</span>
        <h1 class="fw-bold mb-3">{{ product.name }}</h1>
        
        <div class="d-flex align-items-center mb-4">
          <div class="text-warning fs-5 me-2">
            <i class="bi bi-star-fill"></i> {{ product.rating.toFixed(1) }}
          </div>
          <span class="text-muted">({{ product.reviewCount }} reviews)</span>
        </div>
        
        <h2 class="fw-bold text-primary mb-4">${{ product.price.toFixed(2) }}</h2>
        
        <p class="text-muted mb-4">{{ product.description }}</p>
        
        <div class="d-flex align-items-center gap-3 mb-4">
          <span class="fw-medium">Availability:</span>
          <span v-if="product.stock > 0" class="badge bg-success">In Stock ({{ product.stock }})</span>
          <span v-else class="badge bg-danger">Out of Stock</span>
        </div>
        
        <div class="p-4 bg-light rounded mb-4 border">
          <span class="text-muted small d-block mb-1">Sold by</span>
          <span class="fw-bold">{{ product.sellerName }}</span>
        </div>
        
        <div class="d-flex gap-3">
          <button 
            class="btn btn-primary btn-lg flex-grow-1 hover-lift" 
            @click="addToCart" 
            :disabled="product.stock <= 0 || adding">
            <span v-if="adding" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            <i class="bi bi-cart-plus me-2" v-else></i> Add to Cart
          </button>
          
          <button class="btn btn-outline-danger btn-lg hover-lift" @click="toggleWishlist" :title="inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'">
            <i class="bi" :class="inWishlist ? 'bi-heart-fill' : 'bi-heart'"></i>
          </button>
        </div>
      </div>
    </div>
    <section v-if="product" class="card border-0 mt-5 p-4 p-md-5">
      <div class="d-flex justify-content-between align-items-center mb-4"><div><div class="page-eyebrow">Community feedback</div><h3 class="fw-bold mb-0">Customer reviews</h3></div><span class="text-muted">{{ reviews.length }} reviews</span></div>
      <div class="row g-4">
        <div class="col-lg-7">
          <div v-if="!reviews.length" class="text-muted py-3">Be the first to share your experience.</div>
          <div v-for="review in reviews" :key="review.id" class="border-bottom pb-3 mb-3"><div class="d-flex justify-content-between"><strong>{{ review.userName || 'Verified customer' }}</strong><span class="text-warning">{{ '★'.repeat(review.rating) }}<span class="text-muted">{{ '★'.repeat(5 - review.rating) }}</span></span></div><p class="mb-0 mt-2 text-muted">{{ review.comment }}</p></div>
        </div>
        <div class="col-lg-5"><div class="bg-light rounded-3 p-4"><h5 class="fw-bold">Share your review</h5><div class="mb-3"><label class="form-label small">Rating</label><select v-model="reviewForm.rating" class="form-select"><option v-for="rating in 5" :key="rating" :value="rating">{{ rating }} / 5</option></select></div><textarea v-model="reviewForm.comment" class="form-control mb-3" rows="3" placeholder="What did you think?"></textarea><button class="btn btn-primary w-100" @click="submitReview" :disabled="reviewing">{{ reviewing ? 'Posting…' : 'Post review' }}</button></div></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../../services/api'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const cartStore = useCartStore()

const product = ref(null)
const loading = ref(true)
const adding = ref(false)
const inWishlist = ref(false)
const reviews = ref([])
const reviewing = ref(false)
const reviewForm = ref({ rating: 5, comment: '' })

const fetchProduct = async () => {
  try {
    const res = await api.get(`/products/${route.params.id}`)
    if (res.success) {
      product.value = res.data
      checkWishlist()
      fetchReviews()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => { try { const res = await api.get(`/reviews/product/${route.params.id}`); if (res.success) reviews.value = res.data || [] } catch (e) {} }
const submitReview = async () => { if (!reviewForm.value.comment.trim()) return; reviewing.value = true; try { const res = await api.post('/reviews', { productId: product.value.id, ...reviewForm.value }); if (res.success) { reviews.value.unshift(res.data); reviewForm.value = { rating: 5, comment: '' } } } catch (e) { alert(e.response?.data?.message || 'Unable to post review') } finally { reviewing.value = false } }

const checkWishlist = async () => {
  try {
    const res = await api.get('/wishlist')
    if (res.success && res.data) {
      inWishlist.value = res.data.some(p => p.id === product.value.id)
    }
  } catch (e) {
    // Ignore error if not logged in or wishlist fails
  }
}

const toggleWishlist = async () => {
  try {
    if (inWishlist.value) {
      await api.delete(`/wishlist/remove/${product.value.id}`)
      inWishlist.value = false
    } else {
      await api.post(`/wishlist/add/${product.value.id}`)
      inWishlist.value = true
    }
  } catch (e) {
    alert(e.response?.data?.message || 'Action failed')
  }
}

const addToCart = async () => {
  adding.value = true
  try {
    await cartStore.addToCart(product.value.id, 1)
    alert('Added to cart')
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to add')
  } finally {
    adding.value = false
  }
}

onMounted(() => {
  fetchProduct()
})
</script>
