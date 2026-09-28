import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  const token = authStore.token
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  return config
}, (error) => {
  return Promise.reject(error)
})

// Response interceptor to handle errors globally
api.interceptors.response.use((response) => {
  return response.data
}, (error) => {
  const authStore = useAuthStore()
  
  // Handle 401 Unauthorized globally
  if (error.response && error.response.status === 401) {
    authStore.logout()
    window.location.href = '/login'
  }
  
  return Promise.reject(error)
})

export default api
