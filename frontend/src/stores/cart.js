import { defineStore } from 'pinia'
import api from '../services/api'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cart: null,
    loading: false,
    error: null
  }),
  
  getters: {
    cartItems: (state) => state.cart?.items || [],
    cartTotal: (state) => {
      if (!state.cart?.items) return 0
      return state.cart.items.reduce((total, item) => total + (item.price * item.quantity), 0)
    },
    itemCount: (state) => {
      if (!state.cart?.items) return 0
      return state.cart.items.reduce((count, item) => count + item.quantity, 0)
    }
  },
  
  actions: {
    async fetchCart() {
      this.loading = true
      try {
        const response = await api.get('/cart')
        if (response.success) {
          this.cart = response.data
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to fetch cart'
      } finally {
        this.loading = false
      }
    },
    
    async addToCart(productId, quantity = 1) {
      this.loading = true
      try {
        const response = await api.post('/cart/add', { productId, quantity })
        if (response.success) {
          this.cart = response.data
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to add to cart'
        throw err
      } finally {
        this.loading = false
      }
    },
    
    async updateQuantity(productId, quantity) {
      this.loading = true
      try {
        const response = await api.put('/cart/update', { productId, quantity })
        if (response.success) {
          this.cart = response.data
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to update quantity'
        throw err
      } finally {
        this.loading = false
      }
    },
    
    async removeFromCart(productId) {
      this.loading = true
      try {
        const response = await api.delete(`/cart/remove/${productId}`)
        if (response.success) {
          this.cart = response.data
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to remove item'
        throw err
      } finally {
        this.loading = false
      }
    },
    
    async clearCart() {
      this.loading = true
      try {
        const response = await api.delete('/cart/clear')
        if (response.success) {
          this.cart = { items: [] }
          return true
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to clear cart'
        throw err
      } finally {
        this.loading = false
      }
    }
  }
})
