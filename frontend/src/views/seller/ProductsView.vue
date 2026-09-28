<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold m-0">My Products</h2>
      <button class="btn btn-primary" @click="showAddModal = true">
        <i class="bi bi-plus-circle me-1"></i> Add Product
      </button>
    </div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else-if="products.length === 0" class="text-center py-5 card border-0 shadow-sm glass-effect">
      <div class="card-body py-5">
        <i class="bi bi-box display-1 text-muted mb-3 d-block"></i>
        <h3 class="fw-bold">No products found</h3>
        <p class="text-muted mb-4">You haven't listed any products yet.</p>
        <button class="btn btn-primary px-4" @click="showAddModal = true">Add Your First Product</button>
      </div>
    </div>
    
    <div v-else class="card border-0 shadow-sm">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
            <tr>
              <th class="border-0">Product</th>
              <th class="border-0">Category</th>
              <th class="border-0">Price</th>
              <th class="border-0">Stock</th>
              <th class="border-0 text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>
                <div class="d-flex align-items-center gap-3">
                  <img :src="product.imageUrl" class="rounded" style="width: 50px; height: 50px; object-fit: cover;">
                  <div>
                    <h6 class="mb-0 fw-bold">{{ product.name }}</h6>
                    <small class="text-muted text-truncate d-block" style="max-width: 200px;">{{ product.description }}</small>
                  </div>
                </div>
              </td>
              <td><span class="badge bg-secondary">{{ product.categoryName }}</span></td>
              <td class="fw-medium">${{ product.price.toFixed(2) }}</td>
              <td>
                <span class="badge" :class="product.stock > 10 ? 'bg-success' : (product.stock > 0 ? 'bg-warning text-dark' : 'bg-danger')">
                  {{ product.stock }}
                </span>
              </td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-primary me-2" @click="editProduct(product)">
                  <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" @click="deleteProduct(product.id)">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Modal (Simplified for demo) -->
    <div v-if="showAddModal || editingProduct" class="modal-backdrop show"></div>
    <div v-if="showAddModal || editingProduct" class="modal show d-block" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow">
          <div class="modal-header bg-light border-0">
            <h5 class="modal-title fw-bold">{{ editingProduct ? 'Edit Product' : 'Add Product' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body p-4">
            <form @submit.prevent="saveProduct" id="productForm">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label text-muted fw-medium">Name</label>
                  <input type="text" class="form-control" v-model="form.name" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label text-muted fw-medium">Category</label>
                  <select class="form-select" v-model="form.categoryId" required>
                    <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label text-muted fw-medium">Price</label>
                  <input type="number" step="0.01" class="form-control" v-model="form.price" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label text-muted fw-medium">Stock</label>
                  <input type="number" class="form-control" v-model="form.stock" required>
                </div>
                <div class="col-12">
                  <label class="form-label text-muted fw-medium">Image URL</label>
                  <input type="url" class="form-control" v-model="form.imageUrl">
                </div>
                <div class="col-12">
                  <label class="form-label text-muted fw-medium">Description</label>
                  <textarea class="form-control" rows="3" v-model="form.description"></textarea>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer border-0 bg-light">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
            <button type="submit" form="productForm" class="btn btn-primary" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ editingProduct ? 'Update' : 'Save' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const products = ref([])
const categories = ref([])
const loading = ref(true)
const saving = ref(false)
const showAddModal = ref(false)
const editingProduct = ref(null)

const form = ref({
  name: '',
  categoryId: '',
  price: 0,
  stock: 0,
  imageUrl: '',
  description: ''
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await api.get('/seller/products')
    if (res.success) products.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await api.get('/categories')
    if (res.success) categories.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingProduct.value = null
  form.value = { name: '', categoryId: '', price: 0, stock: 0, imageUrl: '', description: '' }
}

const editProduct = (product) => {
  editingProduct.value = product
  form.value = { ...product }
}

const saveProduct = async () => {
  saving.value = true
  try {
    if (editingProduct.value) {
      await api.put(`/seller/products/${editingProduct.value.id}`, form.value)
    } else {
      await api.post('/seller/products', form.value)
    }
    closeModal()
    fetchProducts()
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to save product')
  } finally {
    saving.value = false
  }
}

const deleteProduct = async (id) => {
  if (confirm('Are you sure you want to delete this product?')) {
    try {
      await api.delete(`/seller/products/${id}`)
      fetchProducts()
    } catch (e) {
      alert(e.response?.data?.message || 'Failed to delete')
    }
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>
