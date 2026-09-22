<template>
  <div class="admin-container">
    <div class="admin-header-card">
      <div class="admin-header-title">
        <h2>🛠️ Claims Adjuster Panel</h2>
        <p>Review, audit, and update status lifecycles for all filed insurance claims globally.</p>
      </div>
      
      <!-- Stats Row -->
      <div class="stats-row">
        <div class="stat-box">
          <span class="stat-count">{{ claims.length }}</span>
          <span class="stat-label">Total Claims</span>
        </div>
        <div class="stat-box pending">
          <span class="stat-count">{{ pendingCount }}</span>
          <span class="stat-label">Submitted</span>
        </div>
        <div class="stat-box review">
          <span class="stat-count">{{ reviewCount }}</span>
          <span class="stat-label">In Review</span>
        </div>
        <div class="stat-box approved">
          <span class="stat-count">{{ approvedCount }}</span>
          <span class="stat-label">Approved</span>
        </div>
      </div>
    </div>

    <!-- Filters Row -->
    <div class="filters-card">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search by claim number, claimant name, policy..."
          class="search-input"
        />
      </div>
      
      <div class="filter-options">
        <button 
          v-for="filter in filters" 
          :key="filter.value"
          class="filter-tab"
          :class="{ 'active': activeFilter === filter.value }"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredClaims.length === 0" class="empty-state">
      <span class="empty-icon">📂</span>
      <h3>No claims found</h3>
      <p>No filed claims match your selected filter criteria.</p>
    </div>

    <!-- Claims Table -->
    <div v-else class="table-container">
      <table class="claims-table">
        <thead>
          <tr>
            <th>Reference</th>
            <th>Claimant</th>
            <th>Policy & Type</th>
            <th>Date Filed</th>
            <th>Est. Payout</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="claim in filteredClaims" :key="claim.id">
            <!-- Row Summary -->
            <tr :class="{ 'row-active': expandedClaimId === claim.id }">
              <td class="bold">{{ claim.reference }}</td>
              <td>
                <div class="claimant-cell">
                  <span class="name">{{ claim.claimantName }}</span>
                  <span class="email">{{ claim.claimantEmail }}</span>
                </div>
              </td>
              <td>
                <div class="type-cell">
                  <span class="policy">{{ claim.policyNumber }}</span>
                  <span class="type-badge" :class="claim.claimType">{{ formatClaimType(claim.claimType) }}</span>
                </div>
              </td>
              <td>{{ formatDate(claim.submittedAt) }}</td>
              <td class="bold text-success">${{ formatNumber(getPayoutAmount(claim)) }}</td>
              <td>
                <span class="status-badge" :class="claim.status">{{ formatStatus(claim.status) }}</span>
              </td>
              <td>
                <button class="btn-review" @click="toggleExpand(claim.id)">
                  {{ expandedClaimId === claim.id ? 'Close' : 'Review' }}
                </button>
              </td>
            </tr>

            <!-- Expanding Review Block -->
            <tr v-if="expandedClaimId === claim.id" class="expanding-row">
              <td colspan="7">
                <div class="expanding-content">
                  <div class="details-grid">
                    <!-- Column 1: Policy holder -->
                    <div class="details-col">
                      <h4>👤 Policyholder Contact Info</h4>
                      <p><strong>Full Name:</strong> {{ claim.claimantName }}</p>
                      <p><strong>Email Address:</strong> {{ claim.claimantEmail }}</p>
                      <p><strong>Phone Number:</strong> {{ claim.claimantPhone }}</p>
                      <p><strong>Associated Policy:</strong> {{ claim.policyNumber }}</p>
                    </div>

                    <!-- Column 2: Specific specifications -->
                    <div class="details-col">
                      <h4>📝 Claim Incident Details</h4>
                      
                      <!-- Health Specific -->
                      <div v-if="claim.claimType === 'health'">
                        <p><strong>Hospital/Clinic:</strong> {{ claim.healthDetails?.hospitalName }}</p>
                        <p><strong>Treatment Date:</strong> {{ formatDate(claim.healthDetails?.treatmentDate) }}</p>
                        <p><strong>Submitted Expenses:</strong> ${{ formatNumber(claim.healthDetails?.expensesAmount) }}</p>
                        <p class="desc-box"><strong>Medical Diagnosis:</strong> {{ claim.healthDetails?.medicalDiagnosis }}</p>
                      </div>

                      <!-- Motor Specific -->
                      <div v-else-if="claim.claimType === 'motor'">
                        <p><strong>Vehicle Plate No:</strong> {{ claim.motorDetails?.vehicleRegNo?.toUpperCase() }}</p>
                        <p><strong>Accident Location:</strong> {{ claim.motorDetails?.accidentLocation }}</p>
                        <p><strong>Incident Date:</strong> {{ formatDate(claim.motorDetails?.accidentDate) }}</p>
                        <p><strong>Police Report?</strong> <span class="capitalize">{{ claim.motorDetails?.policeReportFiled }}</span></p>
                        <p><strong>Damage Severity:</strong> <span class="capitalize">{{ claim.motorDetails?.damageSeverity }}</span></p>
                        <p><strong>Repair Estimate:</strong> ${{ formatNumber(claim.motorDetails?.repairEstimate) }}</p>
                      </div>

                      <!-- General Specific -->
                      <div v-else>
                        <p><strong>Item Name:</strong> {{ claim.generalDetails?.itemName }}</p>
                        <p><strong>Approx Purchase Date:</strong> {{ formatDate(claim.generalDetails?.purchaseDate) }}</p>
                        <p><strong>Type of Loss:</strong> <span class="capitalize">{{ formatLossType(claim.generalDetails?.lossType) }}</span></p>
                        <p><strong>Estimated Market Value:</strong> ${{ formatNumber(claim.generalDetails?.estimatedValue) }}</p>
                        <p class="desc-box"><strong>Incident Description:</strong> {{ claim.generalDetails?.incidentDescription }}</p>
                      </div>
                    </div>

                    <!-- Column 3: Documents and audit -->
                    <div class="details-col">
                      <h4>📁 Supporting Attachments</h4>
                      <div v-if="!claim.supportingDocuments || claim.supportingDocuments.length === 0" class="no-docs">
                        No attachments uploaded by claimant.
                      </div>
                      <div v-else class="mini-docs-list">
                        <div v-for="(file, i) in claim.supportingDocuments" :key="i" class="mini-doc-item">
                          <span>📄</span>
                          <span class="file-name" :title="file.name">{{ file.name }}</span>
                          <span class="file-size">({{ formatBytes(file.size) }})</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Action Console Section -->
                  <div class="action-console">
                    <div class="estimator-summary">
                      <span>Calculated Reserve:</span>
                      <strong>${{ formatNumber(getPayoutAmount(claim)) }}</strong>
                    </div>
                    
                    <div class="action-buttons-group">
                      <!-- Start Review button (if status is pending) -->
                      <button 
                        v-if="claim.status === 'pending_review'"
                        class="btn-action-panel btn-review-start"
                        @click="updateStatus(claim.id, 'under_review')"
                        :disabled="actionLoading"
                      >
                        🔎 Start Auditing
                      </button>

                      <!-- Approve / Reject buttons (if pending or under review) -->
                      <button 
                        v-if="['pending_review', 'under_review'].includes(claim.status)"
                        class="btn-action-panel btn-approve"
                        @click="updateStatus(claim.id, 'approved')"
                        :disabled="actionLoading"
                      >
                        ✅ Approve Payout
                      </button>

                      <!-- Mark Paid button (if approved) -->
                      <button 
                        v-if="claim.status === 'approved'"
                        class="btn-action-panel btn-paid"
                        @click="updateStatus(claim.id, 'paid')"
                        :disabled="actionLoading"
                      >
                        💵 Disburse Funds (Mark Paid)
                      </button>

                      <span v-if="claim.status === 'paid'" class="paid-stamp">
                        💰 Payout Settled & Completed
                      </span>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { authStore } from './authStore';

const claims = ref([]);
const searchQuery = ref('');
const activeFilter = ref('all');
const expandedClaimId = ref(null);
const actionLoading = ref(false);

const filters = [
  { value: 'all', label: 'All' },
  { value: 'pending_review', label: 'Submitted' },
  { value: 'under_review', label: 'In Review' },
  { value: 'approved', label: 'Approved' },
  { value: 'paid', label: 'Paid Out' }
];

const API_BASE = import.meta.env.PROD 
  ? 'https://shieldflow-portal.onrender.com' 
  : 'http://localhost:8080';

const loadAllClaims = async () => {
  try {
    const res = await fetch(`${API_BASE}/api/claims`, {
      headers: authStore.getAuthHeader()
    });
    if (!res.ok) throw new Error('Failed to load global claims queue');
    const data = await res.json();
    claims.value = data;
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  loadAllClaims();
});

// Stats computed counts
const pendingCount = computed(() => claims.value.filter(c => c.status === 'pending_review').length);
const reviewCount = computed(() => claims.value.filter(c => c.status === 'under_review').length);
const approvedCount = computed(() => claims.value.filter(c => c.status === 'approved').length);

// Search & Filtered claims
const filteredClaims = computed(() => {
  let list = claims.value;

  if (activeFilter.value !== 'all') {
    list = list.filter(c => c.status === activeFilter.value);
  }

  if (searchQuery.value.trim() !== '') {
    const q = searchQuery.value.toLowerCase().trim();
    list = list.filter(c => {
      return c.reference.toLowerCase().includes(q) ||
             c.claimantName.toLowerCase().includes(q) ||
             c.policyNumber.toLowerCase().includes(q) ||
             c.claimType.toLowerCase().includes(q);
    });
  }

  return list;
});

const toggleExpand = (id) => {
  expandedClaimId.value = expandedClaimId.value === id ? null : id;
};

// PATCH request to change claims status
const updateStatus = async (claimId, newStatus) => {
  actionLoading.value = true;
  try {
    const res = await fetch(`${API_BASE}/api/claims/${claimId}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...authStore.getAuthHeader()
      },
      body: JSON.stringify({ status: newStatus })
    });

    if (!res.ok) throw new Error('Status update failed');
    const updatedClaim = await res.json();
    
    // Find and update inside claims array
    const idx = claims.value.findIndex(c => c.id === claimId);
    if (idx !== -1) {
      claims.value[idx] = updatedClaim;
    }
  } catch (error) {
    console.error(error);
    alert('Failed to update claim status. Please try again.');
  } finally {
    actionLoading.value = false;
  }
};

// Formats & getters
const getPayoutAmount = (claim) => {
  if (claim.claimType === 'health') return claim.estimates?.health || 0;
  if (claim.claimType === 'motor') return claim.estimates?.motor || 0;
  return claim.estimates?.general || 0;
};

const formatClaimType = (val) => {
  if (val === 'motor') return 'Motor';
  return val.charAt(0).toUpperCase() + val.slice(1);
};

const formatLossType = (val) => {
  if (!val) return '';
  return val.replace('_', ' ');
};

const formatStatus = (status) => {
  if (status === 'pending_review') return 'Submitted';
  if (status === 'under_review') return 'Under Review';
  return status.charAt(0).toUpperCase() + status.slice(1);
};

const formatDate = (val) => {
  if (!val) return 'N/A';
  const d = new Date(val);
  return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
};

const formatNumber = (val) => {
  const parsed = parseFloat(val);
  return isNaN(parsed) ? '0.00' : parsed.toFixed(2);
};

const formatBytes = (bytes) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};
</script>

<style scoped>
.admin-container {
  animation: fadeIn 0.4s ease-out;
  width: 100%;
}

.admin-header-card {
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 1.75rem 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 15px rgba(0,0,0,0.01);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .admin-header-card {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

.admin-header-title h2 {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-heading);
  margin-bottom: 0.35rem;
}

.admin-header-title p {
  font-size: 0.9rem;
  opacity: 0.75;
  color: var(--color-text);
}

.stats-row {
  display: flex;
  gap: 1.25rem;
  flex-wrap: wrap;
}

.stat-box {
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 0.6rem 1.15rem;
  min-width: 110px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-count {
  font-size: 1.4rem;
  font-weight: 800;
  color: var(--color-heading);
}

.stat-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  opacity: 0.7;
  margin-top: 0.2rem;
}

.stat-box.pending .stat-count { color: #2b6cb0; }
.stat-box.review .stat-count { color: #b7791f; }
.stat-box.approved .stat-count { color: #276749; }

/* Filters Row */
.filters-card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  margin-bottom: 1.5rem;
}

@media (min-width: 768px) {
  .filters-card {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 100%;
}

@media (min-width: 768px) {
  .search-box {
    max-width: 350px;
  }
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.6;
}

.search-input {
  width: 100%;
  padding: 0.6rem 1rem 0.6rem 2.2rem;
  font-size: 0.9rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-text);
  outline: none;
}

.search-input:focus {
  border-color: #3182ce;
}

.filter-options {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
}

.filter-tab {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  font-weight: 600;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-text);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.filter-tab:hover {
  background-color: var(--color-background-soft);
}

.filter-tab.active {
  background-color: #3182ce;
  border-color: #3182ce;
  color: #ffffff;
}

/* Claims Table */
.table-container {
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.01);
}

.claims-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.claims-table th {
  background-color: var(--color-background-mute);
  padding: 1rem;
  font-weight: 700;
  font-size: 0.85rem;
  color: var(--color-heading);
  border-bottom: 1px solid var(--color-border);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.claims-table td {
  padding: 1.15rem 1rem;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9rem;
  color: var(--color-text);
}

.claims-table tr:hover {
  background-color: rgba(49, 130, 206, 0.02);
}

.claims-table tr.row-active {
  background-color: rgba(49, 130, 206, 0.04);
}

.bold { font-weight: 700; }
.capitalize { text-transform: capitalize; }
.text-success { color: #276749; }

.claimant-cell, .type-cell {
  display: flex;
  flex-direction: column;
}

.claimant-cell .email {
  font-size: 0.75rem;
  opacity: 0.7;
  margin-top: 0.15rem;
}

.type-cell .policy {
  font-weight: 600;
  color: var(--color-heading);
}

.type-cell .type-badge {
  font-size: 0.7rem;
  font-weight: bold;
  padding: 0.05rem 0.4rem;
  border-radius: 4px;
  width: fit-content;
  margin-top: 0.25rem;
  color: white;
}

.type-badge.health { background-color: #319795; }
.type-badge.motor { background-color: #dd6b20; }
.type-badge.general { background-color: #805ad5; }

.status-badge {
  font-size: 0.75rem;
  font-weight: bold;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
}

.status-badge.pending_review { background-color: #ebf8ff; color: #2b6cb0; }
.status-badge.under_review { background-color: #fefcbf; color: #975a16; }
.status-badge.approved { background-color: #f0fff4; color: #276749; }
.status-badge.paid { background-color: #e6fffa; color: #234e52; }

.btn-review {
  background-color: transparent;
  color: #3182ce;
  border: 1px solid #3182ce;
  padding: 0.4rem 0.85rem;
  font-size: 0.8rem;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-review:hover {
  background-color: #3182ce;
  color: white;
}

/* Expanding Row Reviewing details */
.expanding-row td {
  padding: 0 !important;
  background-color: var(--color-background-soft);
}

.expanding-content {
  padding: 2rem;
  border-bottom: 2px solid var(--color-border);
  animation: slideDown 0.25s ease-out;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

@media (min-width: 768px) {
  .details-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

.details-col h4 {
  font-size: 0.85rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-heading);
  margin-bottom: 0.85rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 0.4rem;
}

.details-col p {
  font-size: 0.85rem;
  margin-bottom: 0.5rem;
}

.desc-box {
  background-color: var(--color-background);
  padding: 0.6rem 0.8rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  font-size: 0.8rem;
  margin-top: 0.5rem;
  white-space: pre-wrap;
  line-height: 1.4;
}

.no-docs {
  font-size: 0.8rem;
  opacity: 0.7;
  font-style: italic;
}

.mini-docs-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.mini-doc-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
}

.mini-doc-item .file-name {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-heading);
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mini-doc-item .file-size {
  font-size: 0.7rem;
  opacity: 0.6;
}

/* Action Console Panel inside expander */
.action-console {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1.15rem 1.5rem;
}

.estimator-summary {
  display: flex;
  flex-direction: column;
}

.estimator-summary span {
  font-size: 0.75rem;
  font-weight: bold;
  opacity: 0.65;
  text-transform: uppercase;
}

.estimator-summary strong {
  font-size: 1.5rem;
  color: #276749;
}

.action-buttons-group {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.btn-action-panel {
  border: none;
  padding: 0.6rem 1.25rem;
  font-size: 0.85rem;
  font-weight: bold;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-review-start {
  background-color: #f6ad55;
  color: white;
}
.btn-review-start:hover:not(:disabled) { background-color: #dd6b20; }

.btn-approve {
  background-color: #48bb78;
  color: white;
}
.btn-approve:hover:not(:disabled) { background-color: #38a169; }

.btn-paid {
  background-color: #319795;
  color: white;
}
.btn-paid:hover:not(:disabled) { background-color: #2c7a7b; }

.btn-action-panel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.paid-stamp {
  font-size: 0.9rem;
  font-weight: bold;
  color: #234e52;
  background-color: #e6fffa;
  padding: 0.4rem 1rem;
  border-radius: 6px;
  border: 1px solid #b2f5ea;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background-color: var(--color-background-soft);
  border: 1px dashed var(--color-border);
  border-radius: 12px;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 0.75rem;
  display: block;
}

.empty-state h3 {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--color-heading);
  margin-bottom: 0.4rem;
}

.empty-state p {
  font-size: 0.85rem;
  opacity: 0.7;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
