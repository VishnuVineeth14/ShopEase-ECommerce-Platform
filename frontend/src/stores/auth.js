import { defineStore } from 'pinia'
import api from '../services/api'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    loading: false,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    userRole: (state) => state.user?.role || null,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isSeller: (state) => state.user?.role === 'SELLER',
    isCustomer: (state) => state.user?.role === 'CUSTOMER'
  },
  
  actions: {
    initAuth() {
      const storedToken = localStorage.getItem('token')
      const storedUser = localStorage.getItem('user')
      
      if (storedToken && storedUser) {
        this.token = storedToken
        this.user = JSON.parse(storedUser)
      }
    },
    
    async login(email, password) {
      this.loading = true
      this.error = null
      
      try {
        const response = await api.post('/auth/login', { email, password })
        if (response.success) {
          const data = response.data
          this.token = data.token
          this.user = {
            id: data.id,
            name: data.name,
            email: data.email,
            role: data.role,
            phone: data.phone,
            address: data.address
          }
          
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
          
          // Redirect based on role
          if (this.isAdmin) router.push('/admin/dashboard')
          else if (this.isSeller) router.push('/seller/dashboard')
          else router.push('/customer/dashboard')
          
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Login failed'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async register(userData) {
      this.loading = true
      this.error = null
      
      try {
        const response = await api.post('/auth/register', userData)
        if (response.success) {
          const data = response.data
          this.token = data.token
          this.user = {
            id: data.id,
            name: data.name,
            email: data.email,
            role: data.role,
            phone: data.phone,
            address: data.address
          }
          
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
          
          if (this.isAdmin) router.push('/admin/dashboard')
          else if (this.isSeller) router.push('/seller/dashboard')
          else router.push('/customer/dashboard')
          
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Registration failed'
        return false
      } finally {
        this.loading = false
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
  }
})
