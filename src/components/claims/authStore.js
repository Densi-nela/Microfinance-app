import { reactive, computed } from 'vue';

// Read initial state from localStorage
const storedToken = localStorage.getItem('sf_auth_token') || '';
let storedUser = null;
try {
  const userJson = localStorage.getItem('sf_auth_user');
  if (userJson) storedUser = JSON.parse(userJson);
} catch (e) {
  console.error('Failed to parse stored user session', e);
}

// Global reactive state
const state = reactive({
  token: storedToken,
  user: storedUser
});

// Getter properties
const isAuthenticated = computed(() => !!state.token);
const currentUser = computed(() => state.user);
const userRole = computed(() => state.user?.role || '');

// Authentication actions
const login = (token, user) => {
  state.token = token;
  state.user = user;
  
  localStorage.setItem('sf_auth_token', token);
  localStorage.setItem('sf_auth_user', JSON.stringify(user));
  
  // Wipe any localized frontend claims draft on fresh session to prevent cross-contamination
  localStorage.removeItem('claim_form_draft');
};

const logout = () => {
  state.token = '';
  state.user = null;
  
  localStorage.removeItem('sf_auth_token');
  localStorage.removeItem('sf_auth_user');
  localStorage.removeItem('claim_form_draft');
};

const getAuthHeader = () => {
  if (state.token) {
    return { 'Authorization': `Bearer ${state.token}` };
  }
  return {};
};

// Expose store interface
export const authStore = {
  state,
  isAuthenticated,
  currentUser,
  userRole,
  login,
  logout,
  getAuthHeader
};
