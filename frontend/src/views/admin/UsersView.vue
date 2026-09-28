<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold m-0">User Management</h2>
    </div>
    
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <div class="input-group">
              <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
              <input type="text" class="form-control border-start-0" v-model="searchQuery" placeholder="Search by name or email..." @keyup.enter="fetchUsers">
              <button class="btn btn-outline-secondary" @click="fetchUsers">Search</button>
            </div>
          </div>
          <div class="col-md-4">
            <select class="form-select" v-model="roleFilter" @change="fetchUsers">
              <option value="">All Roles</option>
              <option value="CUSTOMER">Customer</option>
              <option value="SELLER">Seller</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-danger w-100" @click="resetFilters">Reset</button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    
    <div v-else class="card border-0 shadow-sm">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
            <tr>
              <th class="border-0">User Details</th>
              <th class="border-0">Role</th>
              <th class="border-0">Contact</th>
              <th class="border-0">Status</th>
              <th class="border-0 text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>
                <div class="d-flex align-items-center gap-3">
                  <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center" style="width: 40px; height: 40px; font-weight: bold;">
                    {{ user.name.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <h6 class="mb-0 fw-bold">{{ user.name }}</h6>
                    <small class="text-muted d-block text-truncate" style="max-width: 150px;">ID: {{ user.id }}</small>
                  </div>
                </div>
              </td>
              <td>
                <span class="badge" :class="{
                  'bg-primary': user.role === 'ADMIN',
                  'bg-info text-dark': user.role === 'SELLER',
                  'bg-secondary': user.role === 'CUSTOMER'
                }">{{ user.role }}</span>
              </td>
              <td>
                <div class="small"><i class="bi bi-envelope me-1"></i> {{ user.email }}</div>
                <div class="small text-muted"><i class="bi bi-telephone me-1"></i> {{ user.phone }}</div>
              </td>
              <td>
                <span class="badge" :class="user.enabled ? 'bg-success' : 'bg-danger'">
                  {{ user.enabled ? 'Active' : 'Disabled' }}
                </span>
              </td>
              <td class="text-end">
                <button 
                  class="btn btn-sm me-2" 
                  :class="user.enabled ? 'btn-outline-warning' : 'btn-outline-success'"
                  @click="toggleStatus(user.id)"
                  :disabled="user.role === 'ADMIN'"
                  :title="user.enabled ? 'Disable User' : 'Enable User'">
                  <i class="bi" :class="user.enabled ? 'bi-person-dash' : 'bi-person-check'"></i>
                </button>
                <button 
                  class="btn btn-sm btn-outline-danger" 
                  @click="deleteUser(user.id)"
                  :disabled="user.role === 'ADMIN'">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="users.length === 0" class="text-center py-4 text-muted">
        No users found matching your criteria.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const users = ref([])
const loading = ref(true)
const searchQuery = ref('')
const roleFilter = ref('')

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    if (searchQuery.value) params.append('search', searchQuery.value)
    if (roleFilter.value) params.append('role', roleFilter.value)
    
    const res = await api.get(`/admin/users?${params.toString()}`)
    if (res.success) {
      users.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  searchQuery.value = ''
  roleFilter.value = ''
  fetchUsers()
}

const toggleStatus = async (id) => {
  try {
    const res = await api.put(`/admin/users/${id}/toggle-status`)
    if (res.success) {
      fetchUsers()
    }
  } catch (e) {
    alert(e.response?.data?.message || 'Failed to update user status')
  }
}

const deleteUser = async (id) => {
  if (confirm('Are you sure you want to permanently delete this user?')) {
    try {
      await api.delete(`/admin/users/${id}`)
      fetchUsers()
    } catch (e) {
      alert(e.response?.data?.message || 'Failed to delete user')
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
