<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-end mb-4">
      <div><div class="page-eyebrow">Curated for you</div><h1 class="fw-bold m-0">Find your next favorite</h1><p class="text-muted mb-0 mt-1">Quality picks from independent sellers.</p></div>
      
      <div class="d-flex gap-2 w-50">
        <input type="text" class="form-control" v-model="searchQuery" @keyup.enter="searchProducts" placeholder="Search products...">
        <button class="btn btn-primary" @click="searchProducts"><i class="bi bi-search"></i></button>
      </div>
    </div>
    
    <div class="row">
      <!-- Filters Sidebar -->
      <div class="col-md-3 mb-4">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <h5 class="fw-bold mb-3">Filters</h5>
            
            <div class="mb-4">
              <label class="form-label fw-medium text-muted">Category</label>
              <select class="form-select" v-model="filters.categoryId" @change="fetchProducts">
                <option value="">All Categories</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
              </select>
            </div>
            
            <div class="mb-4">
              <label class="form-label fw-medium text-muted">Sort By</label>
              <select class="form-select" v-model="filters.sort" @change="fetchProducts">
                <option value="newest">Newest First</option>
                <option value="price_asc">Price: Low to High</option>
                <option value="price_desc">Price: High to Low</option>
                <option value="rating">Top Rated</option>
              </select>
            </div>
            
            <button class="btn btn-outline-secondary w-100" @click="resetFilters">Reset Filters</button>
          </div>
        </div>
      </div>
      
      <!-- Product Grid -->
      <div class="col-md-9">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
        </div>
        
        <div v-else-if="products.length === 0" class="text-center py-5">
          <i class="bi bi-emoji-frown display-1 text-muted mb-3"></i>
          <h4>No products found</h4>
          <p class="text-muted">Try adjusting your search or filters.</p>
        </div>
        
        <div v-else class="row g-4">
          <div class="col-md-4 col-sm-6" v-for="product in products" :key="product.id">
            <div class="product-card bg-white h-100">
              <div class="position-relative"><router-link :to="`/customer/products/${product.id}`"><img :src="product.imageUrl" class="w-100 product-image" alt="Product"></router-link><button class="btn btn-light rounded-circle position-absolute top-0 end-0 m-3 shadow-sm" @click="toggleWishlist(product)"><i class="bi" :class="wishlistedIds.has(product.id) ? 'bi-heart-fill text-danger' : 'bi-heart'"></i></button></div>
              
              <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between mb-2">
                  <span class="badge bg-light text-primary">{{ product.categoryName }}</span>
                  <div class="text-warning small">
                    <i class="bi bi-star-fill"></i> {{ product.rating.toFixed(1) }}
                  </div>
                </div>
                
                <router-link :to="`/customer/products/${product.id}`" class="text-decoration-none text-dark"><h5 class="card-title fw-bold mb-1 text-truncate">{{ product.name }}</h5></router-link>
                <p class="card-text text-muted small mb-3 text-truncate-2">{{ product.description }}</p>
                
                <div class="mt-auto d-flex align-items-center justify-content-between">
                  <h5 class="fw-bold text-primary mb-0">${{ product.price.toFixed(2) }}</h5>
                  <button 
                    class="btn btn-primary btn-sm" 
                    @click="addToCart(product)"
                    :disabled="product.stock <= 0 || addingId === product.id">
                    <span v-if="addingId === product.id" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    <span v-else><i class="bi bi-cart-plus"></i> Add</span>
                  </button>
                </div>
                <div class="text-muted small mt-2" v-if="product.stock <= 0">Out of stock</div>
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
import { useCartStore } from '../../stores/cart'

const products = ref([])
const categories = ref([])
const loading = ref(true)
const addingId = ref(null)
const searchQuery = ref('')
const cartStore = useCartStore()
const wishlistedIds = ref(new Set())

const filters = ref({
  categoryId: '',
  sort: 'newest'
})

const fetchCategories = async () => {
  try {
    const res = await api.get('/categories')
    if (res.success) categories.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    if (filters.value.categoryId) params.append('categoryId', filters.value.categoryId)
    if (filters.value.sort) params.append('sort', filters.value.sort)
    
    const res = await api.get(`/products?${params.toString()}`)
    if (res.success) products.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const searchProducts = async () => {
  if (!searchQuery.value.trim()) {
    fetchProducts()
    return
  }
  
  loading.value = true
  try {
    const res = await api.get(`/products/search?query=${encodeURIComponent(searchQuery.value)}`)
    if (res.success) products.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.value = { categoryId: '', sort: 'newest' }
  searchQuery.value = ''
  fetchProducts()
}

const loadWishlist = async () => { try { const res = await api.get('/wishlist'); if (res.success) wishlistedIds.value = new Set((res.data || []).map(item => item.id)) } catch (e) {} }
const toggleWishlist = async (product) => {
  if (wishlistedIds.value.has(product.id)) { await api.delete(`/wishlist/remove/${product.id}`); wishlistedIds.value.delete(product.id) }
  else { await api.post(`/wishlist/add/${product.id}`); wishlistedIds.value.add(product.id) }
  wishlistedIds.value = new Set(wishlistedIds.value)
}

const addToCart = async (product) => {
  addingId.value = product.id
  try {
    await cartStore.addToCart(product.id, 1)
    // Optional: show toast notification here
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to add to cart')
  } finally {
    addingId.value = null
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
  loadWishlist()
})
</script>
