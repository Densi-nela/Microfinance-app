<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <span class="auth-logo">🛡️</span>
        <h2>ShieldFlow Portal</h2>
        <p>{{ isLoginMode ? 'Sign in to access your claims account' : 'Create an account to start filing claims' }}</p>
      </div>

      <!-- Main Form -->
      <form @submit.prevent="handleSubmit" class="auth-form">
        <!-- Error Alert -->
        <div v-if="globalError" class="alert error-alert">
          <span>⚠️ {{ globalError }}</span>
        </div>

        <!-- Full Name (Only for Registration) -->
        <div v-if="!isLoginMode" class="form-group">
          <label class="form-label">Full Name</label>
          <input 
            type="text" 
            v-model="fields.fullName" 
            placeholder="e.g. John Doe"
            class="form-input"
            :class="{ 'has-error': errors.fullName }"
            required
          />
          <span v-if="errors.fullName" class="error-msg">{{ errors.fullName }}</span>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label class="form-label">Email Address</label>
          <input 
            type="email" 
            v-model="fields.email" 
            placeholder="e.g. john@example.com"
            class="form-input"
            :class="{ 'has-error': errors.email }"
            required
          />
          <span v-if="errors.email" class="error-msg">{{ errors.email }}</span>
        </div>

        <!-- Password -->
        <div class="form-group">
          <label class="form-label">Password</label>
          <input 
            type="password" 
            v-model="fields.password" 
            placeholder="••••••••"
            class="form-input"
            :class="{ 'has-error': errors.password }"
            required
          />
          <span v-if="errors.password" class="error-msg">{{ errors.password }}</span>
        </div>

        <!-- Role Select (Only for Registration - helper for testing!) -->
        <div v-if="!isLoginMode" class="form-group">
          <label class="form-label">Account Role</label>
          <select v-model="fields.role" class="form-select">
            <option value="CUSTOMER">Customer / Claimant</option>
            <option value="ADMIN">Claims Adjuster / Admin</option>
          </select>
          <p class="role-helper">Choose "Admin" to access the Claims Adjuster global queue!</p>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="btn-auth" :disabled="loading">
          <span v-if="loading" class="spinner-sm"></span>
          <span v-else>{{ isLoginMode ? 'Sign In' : 'Create Account' }}</span>
        </button>
      </form>

      <!-- Mode Toggle Footer -->
      <div class="auth-footer">
        <span>{{ isLoginMode ? "Don't have an account?" : "Already have an account?" }}</span>
        <button class="toggle-mode-btn" @click="toggleMode" type="button">
          {{ isLoginMode ? 'Sign Up' : 'Sign In' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { authStore } from './authStore';

const isLoginMode = ref(true);
const loading = ref(false);
const globalError = ref('');

const fields = reactive({
  email: '',
  password: '',
  fullName: '',
  role: 'CUSTOMER'
});

const errors = reactive({
  email: '',
  password: '',
  fullName: ''
});

const API_BASE = import.meta.env.PROD 
  ? 'https://shieldflow-portal.onrender.com' 
  : 'http://localhost:8080';

const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value;
  globalError.value = '';
  // Clear errors
  errors.email = '';
  errors.password = '';
  errors.fullName = '';
};

const validateFields = () => {
  let isValid = true;
  errors.email = '';
  errors.password = '';
  errors.fullName = '';

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!fields.email) {
    errors.email = 'Email is required';
    isValid = false;
  } else if (!emailRegex.test(fields.email)) {
    errors.email = 'Enter a valid email address';
    isValid = false;
  }

  if (!fields.password) {
    errors.password = 'Password is required';
    isValid = false;
  } else if (fields.password.length < 6) {
    errors.password = 'Password must be at least 6 characters';
    isValid = false;
  }

  if (!isLoginMode.value && !fields.fullName) {
    errors.fullName = 'Full name is required';
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (!validateFields()) return;

  loading.value = true;
  globalError.value = '';

  const url = isLoginMode.value 
    ? `${API_BASE}/api/auth/login` 
    : `${API_BASE}/api/auth/register`;

  const payload = isLoginMode.value
    ? { email: fields.email, password: fields.password }
    : { email: fields.email, password: fields.password, fullName: fields.fullName, role: fields.role };

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();

    if (!res.ok) {
      if (res.status === 400 && typeof data === 'object') {
        // Validation/field error (e.g. Email in use)
        if (data.email) errors.email = data.email;
        if (data.password) errors.password = data.password;
        if (data.fullName) errors.fullName = data.fullName;
      } else {
        // Global unauthorized or server error
        globalError.value = data.error || data.message || 'An error occurred. Please try again.';
      }
      loading.value = false;
      return;
    }

    // Success: Save token & session to reactive auth store
    authStore.login(data.token, {
      id: data.id,
      email: data.email,
      fullName: data.fullName,
      role: data.role
    });

  } catch (error) {
    console.error('Auth request failed', error);
    globalError.value = 'Failed to connect to the authentication server. Please verify your backend is running.';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
  width: 100%;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 2.25rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  animation: fadeIn 0.4s ease-out;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-logo {
  font-size: 2.5rem;
  line-height: 1;
  display: block;
  margin-bottom: 0.5rem;
}

.auth-header h2 {
  font-size: 1.4rem;
  font-weight: 800;
  color: var(--color-heading);
  margin-bottom: 0.25rem;
}

.auth-header p {
  font-size: 0.85rem;
  color: var(--color-text);
  opacity: 0.75;
}

/* Forms */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.15rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 0.4rem;
  color: var(--color-text);
}

.form-input, .form-select {
  padding: 0.65rem 0.85rem;
  font-size: 0.9rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  color: var(--color-text);
  outline: none;
  font-family: inherit;
  transition: all 0.2s;
}

.form-input:focus, .form-select:focus {
  border-color: #3182ce;
  background-color: var(--color-background);
}

.form-input.has-error {
  border-color: #e53e3e;
}

.error-msg {
  font-size: 0.75rem;
  color: #e53e3e;
  margin-top: 0.3rem;
  font-weight: 500;
}

.role-helper {
  font-size: 0.75rem;
  opacity: 0.65;
  margin-top: 0.35rem;
  font-style: italic;
}

/* Buttons */
.btn-auth {
  background-color: #3182ce;
  color: #ffffff;
  border: none;
  padding: 0.7rem 1.25rem;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2.5rem;
  margin-top: 0.5rem;
}

.btn-auth:hover:not(:disabled) {
  background-color: #2b6cb0;
}

.btn-auth:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  font-size: 0.85rem;
  margin-top: 1.5rem;
  border-top: 1px solid var(--color-border);
  padding-top: 1rem;
  display: flex;
  justify-content: center;
  gap: 0.35rem;
}

.toggle-mode-btn {
  background: none;
  border: none;
  color: #3182ce;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
}

/* Alerts */
.alert {
  padding: 0.65rem 0.85rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
}

.error-alert {
  background-color: #fff5f5;
  border: 1px solid #fed7d7;
  color: #c53030;
}

/* Spinner */
.spinner-sm {
  width: 1.2rem;
  height: 1.2rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
