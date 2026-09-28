<template>
  <div class="register-wrapper">
    <div class="container py-5 h-100">
      <div class="row h-100 justify-content-center align-items-center">
        <div class="col-12 col-lg-10 col-xl-11">
          <div class="card overflow-hidden shadow-soft border-0 rounded-4">
            <div class="row g-0">
              <!-- Left Side - Branding/Hero Image -->
              <div class="col-md-4 col-lg-5 d-none d-md-flex flex-column justify-content-center align-items-center bg-primary text-white p-5 position-relative overflow-hidden" style="background: linear-gradient(135deg, #635bff, #3b32c4);">
                <!-- Abstract Design Elements -->
                <div class="position-absolute top-0 start-0 w-100 h-100 opacity-25" style="background-image: radial-gradient(circle at 80% 70%, white 1px, transparent 1px); background-size: 20px 20px;"></div>
                <div class="position-absolute bottom-0 end-0 bg-white rounded-circle opacity-10" style="width: 300px; height: 300px; margin-bottom: -150px; margin-right: -150px;"></div>
                <div class="position-absolute top-0 start-0 bg-white rounded-circle opacity-10" style="width: 200px; height: 200px; margin-top: -100px; margin-left: -100px;"></div>
                
                <div class="text-center position-relative z-1">
                  <h1 class="display-6 fw-bold mb-3">Join ShopEase</h1>
                  <p class="lead mb-4 fw-medium text-white-50">Create an account to start shopping and selling premium products.</p>
                  <ul class="list-unstyled text-start mt-4 mb-0 d-inline-block">
                    <li class="mb-3 d-flex align-items-center"><i class="bi bi-check-circle-fill text-info me-3 fs-5"></i> <span class="fw-medium">Exclusive Offers</span></li>
                    <li class="mb-3 d-flex align-items-center"><i class="bi bi-check-circle-fill text-info me-3 fs-5"></i> <span class="fw-medium">Fast Checkout</span></li>
                    <li class="d-flex align-items-center"><i class="bi bi-check-circle-fill text-info me-3 fs-5"></i> <span class="fw-medium">Order Tracking</span></li>
                  </ul>
                </div>
              </div>
              
              <!-- Right Side - Register Form -->
              <div class="col-md-8 col-lg-7 p-4 p-md-5 bg-white">
                <div class="mb-4 text-center text-md-start">
                  <h2 class="fw-bold text-dark mb-1">Create an Account ✨</h2>
                  <p class="text-muted">Join our community today.</p>
                </div>
                
                <div v-if="authStore.error" class="alert alert-danger d-flex align-items-center rounded-3 mb-4" role="alert">
                  <i class="bi bi-exclamation-triangle-fill flex-shrink-0 me-2"></i>
                  <div>{{ authStore.error }}</div>
                </div>
                
                <form @submit.prevent="handleRegister" class="needs-validation">
                  <div class="row g-3">
                    <div class="col-md-12">
                      <div class="form-floating">
                        <input type="text" class="form-control rounded-3" id="floatingName" v-model="form.name" required placeholder="John Doe">
                        <label for="floatingName" class="text-muted">Full Name</label>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="form-floating">
                        <input type="email" class="form-control rounded-3" id="floatingEmail" v-model="form.email" required placeholder="name@example.com">
                        <label for="floatingEmail" class="text-muted">Email address</label>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="form-floating">
                        <input type="tel" class="form-control rounded-3" id="floatingPhone" v-model="form.phone" required placeholder="1234567890">
                        <label for="floatingPhone" class="text-muted">Phone Number</label>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="form-floating">
                        <input :type="showPassword ? 'text' : 'password'" class="form-control rounded-3 pe-5" id="floatingPassword" v-model="form.password" required minlength="6" placeholder="Password">
                        <label for="floatingPassword" class="text-muted">Password</label>
                        <button type="button" class="btn btn-link position-absolute top-50 end-0 translate-middle-y text-decoration-none text-muted" @click="showPassword = !showPassword">
                          <i class="bi" :class="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
                        </button>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="form-floating">
                        <input :type="showPassword ? 'text' : 'password'" class="form-control rounded-3 pe-5" id="floatingConfirmPassword" v-model="form.confirmPassword" required minlength="6" placeholder="Confirm Password">
                        <label for="floatingConfirmPassword" class="text-muted">Confirm Password</label>
                      </div>
                    </div>
                    
                    <div class="col-12">
                      <div class="form-floating">
                        <textarea class="form-control rounded-3" id="floatingAddress" style="height: 80px" v-model="form.address" placeholder="Address"></textarea>
                        <label for="floatingAddress" class="text-muted">Address</label>
                      </div>
                    </div>
                    
                    <div class="col-12 mb-2">
                      <label class="form-label text-muted fw-medium small mb-2 d-block">I want to register as a:</label>
                      <div class="d-flex gap-3">
                        <div class="form-check form-check-inline role-select-card flex-grow-1 position-relative m-0">
                          <input class="form-check-input position-absolute opacity-0 w-100 h-100 start-0 top-0 m-0 z-1" style="cursor:pointer;" type="radio" name="roleOptions" id="roleCustomer" value="CUSTOMER" v-model="form.role" required>
                          <label class="form-check-label w-100 border rounded-3 p-3 text-center d-block transition-all" for="roleCustomer" :class="{'border-primary bg-primary text-white shadow-sm': form.role === 'CUSTOMER'}">
                            <i class="bi bi-person-badge fs-4 d-block mb-1"></i>
                            <span class="fw-bold">Customer</span>
                          </label>
                        </div>
                        <div class="form-check form-check-inline role-select-card flex-grow-1 position-relative m-0">
                          <input class="form-check-input position-absolute opacity-0 w-100 h-100 start-0 top-0 m-0 z-1" style="cursor:pointer;" type="radio" name="roleOptions" id="roleSeller" value="SELLER" v-model="form.role" required>
                          <label class="form-check-label w-100 border rounded-3 p-3 text-center d-block transition-all" for="roleSeller" :class="{'border-primary bg-primary text-white shadow-sm': form.role === 'SELLER'}">
                            <i class="bi bi-shop fs-4 d-block mb-1"></i>
                            <span class="fw-bold">Seller</span>
                          </label>
                        </div>
                      </div>
                    </div>
                    
                    <div class="col-12">
                      <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" value="" id="termsCheck" required>
                        <label class="form-check-label text-muted small" for="termsCheck">
                          I agree to the <a href="#" class="text-primary text-decoration-none">Terms and Conditions</a> and <a href="#" class="text-primary text-decoration-none">Privacy Policy</a>
                        </label>
                      </div>
                    </div>
                    
                    <div class="col-12">
                      <button type="submit" class="btn btn-primary btn-lg w-100 fw-bold rounded-pill hover-lift" :disabled="authStore.loading">
                        <span v-if="authStore.loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        <span v-else>Create Account</span>
                      </button>
                    </div>
                  </div>
                </form>
                
                <div class="text-center mt-4">
                  <span class="text-muted">Already have an account?</span>
                  <router-link to="/login" class="text-primary text-decoration-none fw-bold ms-1 hover-underline">Sign in</router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const showPassword = ref(false)

const form = ref({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  role: 'CUSTOMER',
  address: ''
})

const handleRegister = async () => {
  if (form.value.password !== form.value.confirmPassword) {
    authStore.error = "Passwords do not match!"
    return
  }
  await authStore.register(form.value)
}
</script>

<style scoped>
.register-wrapper {
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

.transition-all {
  transition: all 0.2s ease-in-out;
}

.role-select-card label:hover {
  border-color: var(--primary) !important;
  background-color: rgba(99, 91, 255, 0.05);
}

/* Fix for auto-fill styling in WebKit browsers */
input:-webkit-autofill,
input:-webkit-autofill:hover, 
input:-webkit-autofill:focus, 
input:-webkit-autofill:active{
    -webkit-box-shadow: 0 0 0 30px white inset !important;
}
</style>
