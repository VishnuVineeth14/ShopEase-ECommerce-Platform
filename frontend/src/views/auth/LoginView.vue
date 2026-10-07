<template>
  <div class="login-wrapper">
    <div class="container py-5 h-100">
      <div class="row h-100 justify-content-center align-items-center">
        <div class="col-12 col-lg-10 col-xl-9">
          <div class="card overflow-hidden shadow-soft border-0 rounded-4">
            <div class="row g-0">
              <!-- Left Side - Branding/Hero Image -->
              <div class="col-md-5 d-none d-md-flex flex-column justify-content-center align-items-center bg-primary text-white p-5 position-relative overflow-hidden" style="background: linear-gradient(135deg, #635bff, #3b32c4);">
                <!-- Abstract Design Elements -->
                <div class="position-absolute top-0 start-0 w-100 h-100 opacity-25" style="background-image: radial-gradient(circle at 20% 30%, white 1px, transparent 1px); background-size: 20px 20px;"></div>
                <div class="position-absolute top-0 end-0 bg-white rounded-circle opacity-10" style="width: 300px; height: 300px; margin-top: -150px; margin-right: -150px;"></div>
                <div class="position-absolute bottom-0 start-0 bg-white rounded-circle opacity-10" style="width: 200px; height: 200px; margin-bottom: -100px; margin-left: -100px;"></div>
                
                <div class="text-center position-relative z-1">
                  <h1 class="display-5 fw-bold mb-3">ShopEase</h1>
                  <p class="lead mb-4 fw-medium text-white-50">Your premium shopping destination. Discover, shop, and elevate your lifestyle.</p>
                  <div class="d-flex justify-content-center gap-2 mt-4">
                    <span class="badge bg-white text-primary rounded-pill py-2 px-3 fw-bold shadow-sm">Fast Delivery</span>
                    <span class="badge bg-white text-primary rounded-pill py-2 px-3 fw-bold shadow-sm">Secure</span>
                  </div>
                </div>
              </div>
              
              <!-- Right Side - Login Form -->
              <div class="col-md-7 p-4 p-md-5 bg-white">
                <div class="mb-5 text-center text-md-start">
                  <h2 class="fw-bold text-dark mb-1">Welcome Back 👋</h2>
                  <p class="text-muted">Please enter your details to sign in.</p>
                </div>
                
                <div v-if="authStore.error" class="alert alert-danger d-flex align-items-center rounded-3 mb-4" role="alert">
                  <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2"></i>
                  <div>{{ authStore.error }}</div>
                </div>
                
                <form @submit.prevent="handleLogin" class="needs-validation">
                  <div class="form-floating mb-4">
                    <input type="email" class="form-control rounded-3" id="floatingInput" v-model="email" required placeholder="name@example.com">
                    <label for="floatingInput" class="text-muted">Email address</label>
                  </div>
                  
                  <div class="form-floating mb-4">
                    <input :type="showPassword ? 'text' : 'password'" class="form-control rounded-3 pe-5" id="floatingPassword" v-model="password" required placeholder="Password">
                    <label for="floatingPassword" class="text-muted">Password</label>
                    <button type="button" class="btn btn-link position-absolute top-50 end-0 translate-middle-y text-decoration-none text-muted" @click="showPassword = !showPassword">
                      <i class="bi" :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                      <span class="small" v-if="!showPassword">Show</span>
                      <span class="small" v-else>Hide</span>
                    </button>
                  </div>

                  <div class="d-flex justify-content-between align-items-center mb-4">
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" value="" id="rememberMe">
                      <label class="form-check-label text-muted small" for="rememberMe">
                        Remember me
                      </label>
                    </div>
                    <a href="#" class="text-primary text-decoration-none small fw-medium" @click.prevent="showToast('Forgot password feature coming soon')">Forgot Password?</a>
                  </div>
                  
                  <button type="submit" class="btn btn-primary btn-lg w-100 fw-bold rounded-pill hover-lift mb-4" :disabled="authStore.loading">
                    <span v-if="authStore.loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    <span v-else>Sign In</span>
                  </button>

                  <!-- <div class="position-relative mb-4">
                    <hr class="text-muted">
                    <span class="position-absolute top-50 start-50 translate-middle bg-white px-2 text-muted small">Or continue with</span>
                  </div> -->

                  <!-- <div class="d-flex gap-3 justify-content-center mb-4">
                    <button type="button" class="btn btn-outline-secondary rounded-circle p-2 d-flex align-items-center justify-content-center" style="width: 45px; height: 45px;" @click="showToast('Social login coming soon')">
                      <i class="bi bi-google fs-5"></i>
                    </button>
                    <button type="button" class="btn btn-outline-secondary rounded-circle p-2 d-flex align-items-center justify-content-center" style="width: 45px; height: 45px;" @click="showToast('Social login coming soon')">
                      <i class="bi bi-github fs-5"></i>
                    </button>
                  </div> -->
                </form>
                
                <div class="text-center mt-4 pt-3">
                  <span class="text-muted">Don't have an account?</span>
                  <router-link to="/register" class="text-primary text-decoration-none fw-bold ms-1 hover-underline">Create an account</router-link>
                </div>
                
                <!-- Demo Accounts Accordion -->
                <div class="mt-4 border rounded-3">
                  <div class="bg-transparent">
                    <button class="btn btn-light w-100 text-start rounded-3 py-2 px-3 small text-muted shadow-none d-flex justify-content-between align-items-center" type="button" @click="showDemoCreds = !showDemoCreds">
                      <span><i class="bi bi-info-circle me-2"></i> View Demo Credentials</span>
                      <i class="bi" :class="showDemoCreds ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                    </button>
                    <div v-show="showDemoCreds" class="p-3 bg-white border-top small text-muted rounded-bottom-3">
                      <div class="d-flex justify-content-between mb-1"><span><strong>Admin:</strong> admin@example.com</span> <span>Admin@123</span></div>
                      <div class="d-flex justify-content-between mb-1"><span><strong>Seller:</strong> seller@example.com</span> <span>Seller@123</span></div>
                      <div class="d-flex justify-content-between"><span><strong>Customer:</strong> customer@example.com</span> <span>Customer@123</span></div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Toast Notification for pending features -->
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
      <div id="featureToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true" ref="toastRef">
        <div class="toast-header bg-primary text-white">
          <strong class="me-auto">Notification</strong>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
        <div class="toast-body bg-white">
          {{ toastMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import * as bootstrap from 'bootstrap'

const authStore = useAuthStore()
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const showDemoCreds = ref(false)

const toastRef = ref(null)
const toastMessage = ref('')
let bsToast = null

onMounted(() => {
  if (toastRef.value) {
    bsToast = new bootstrap.Toast(toastRef.value)
  }
})

const showToast = (message) => {
  toastMessage.value = message
  if (bsToast) {
    bsToast.show()
  }
}

const handleLogin = async () => {
  await authStore.login(email.value, password.value)
}
</script>

<style scoped>
.login-wrapper {
  min-height: calc(100vh - 76px); /* Adjust based on navbar height */
  background: var(--light);
}

.form-floating > .form-control:focus ~ label,
.form-floating > .form-control:not(:placeholder-shown) ~ label,
.form-floating > .form-select ~ label {
  color: var(--primary);
  font-weight: 600;
}

.form-control:focus {
  border-color: rgba(99, 91, 255, 0.5);
  box-shadow: 0 0 0 0.25rem rgba(99, 91, 255, 0.15);
}

.hover-underline:hover {
  text-decoration: underline !important;
}

/* Fix for auto-fill styling in WebKit browsers */
input:-webkit-autofill,
input:-webkit-autofill:hover, 
input:-webkit-autofill:focus, 
input:-webkit-autofill:active{
    -webkit-box-shadow: 0 0 0 30px white inset !important;
}
</style>
