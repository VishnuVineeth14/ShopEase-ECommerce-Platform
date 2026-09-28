<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold m-0">Category Management</h2>
      <button class="btn btn-primary" @click="showAddModal = true">
        <i class="bi bi-plus-circle me-1"></i> Add Category
      </button>
    </div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else class="row g-4">
      <div class="col-md-4 col-sm-6" v-for="category in categories" :key="category.id">
        <div class="card h-100 border-0 shadow-sm hover-lift">
          <img v-if="category.imageUrl" :src="category.imageUrl" class="card-img-top" style="height: 160px; object-fit: cover;">
          <div v-else class="card-img-top bg-light d-flex align-items-center justify-content-center text-muted" style="height: 160px;">
            <i class="bi bi-image fs-1"></i>
          </div>
          
          <div class="card-body">
            <h5 class="fw-bold text-primary mb-2">{{ category.name }}</h5>
            <p class="text-muted small mb-0">{{ category.description }}</p>
          </div>
          
          <div class="card-footer bg-white border-top-0 d-flex justify-content-end gap-2 pb-3">
            <button class="btn btn-sm btn-outline-primary" @click="editCategory(category)">
              <i class="bi bi-pencil"></i> Edit
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="deleteCategory(category.id)">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="categories.length === 0" class="col-12 text-center py-5">
        <i class="bi bi-tags display-1 text-muted mb-3 d-block"></i>
        <h4 class="text-muted">No categories available</h4>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div v-if="showAddModal || editingCategory" class="modal-backdrop show"></div>
    <div v-if="showAddModal || editingCategory" class="modal show d-block" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content border-0 shadow">
          <div class="modal-header bg-light border-0">
            <h5 class="modal-title fw-bold">{{ editingCategory ? 'Edit Category' : 'Add Category' }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body p-4">
            <form @submit.prevent="saveCategory" id="categoryForm">
              <div class="mb-3">
                <label class="form-label text-muted fw-medium">Name</label>
                <input type="text" class="form-control" v-model="form.name" required>
              </div>
              <div class="mb-3">
                <label class="form-label text-muted fw-medium">Image URL</label>
                <input type="url" class="form-control" v-model="form.imageUrl">
              </div>
              <div class="mb-3">
                <label class="form-label text-muted fw-medium">Description</label>
                <textarea class="form-control" rows="3" v-model="form.description"></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer border-0 bg-light">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
            <button type="submit" form="categoryForm" class="btn btn-primary" :disabled="saving">
              <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
              {{ editingCategory ? 'Update' : 'Save' }}
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

const categories = ref([])
const loading = ref(true)
const saving = ref(false)
const showAddModal = ref(false)
const editingCategory = ref(null)

const form = ref({
  name: '',
  description: '',
  imageUrl: ''
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await api.get('/categories')
    if (res.success) {
      categories.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const closeModal = () => {
  showAddModal.value = false
  editingCategory.value = null
  form.value = { name: '', description: '', imageUrl: '' }
}

const editCategory = (category) => {
  editingCategory.value = category
  form.value = { ...category }
}

const saveCategory = async () => {
  saving.value = true
  try {
    if (editingCategory.value) {
      await api.put(`/admin/categories/${editingCategory.value.id}`, form.value)
    } else {
      await api.post('/admin/categories', form.value)
    }
    closeModal()
    fetchCategories()
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to save category')
  } finally {
    saving.value = false
  }
}

const deleteCategory = async (id) => {
  if (confirm('Are you sure you want to delete this category? Products linked to this category may lose their reference.')) {
    try {
      await api.delete(`/admin/categories/${id}`)
      fetchCategories()
    } catch (e) {
      alert(e.response?.data?.message || 'Failed to delete category')
    }
  }
}

onMounted(() => {
  fetchCategories()
})
</script>
