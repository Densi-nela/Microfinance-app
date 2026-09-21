<template>
  <div class="history-container">
    <div class="history-header">
      <h2>📋 My Submitted Claims</h2>
      <p>View, track, and manage all your submitted insurance claims and their current review status.</p>
    </div>

    <!-- Filters and Search -->
    <div class="filters-card">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search by claim number, hospital, vehicle plate..."
          class="search-input"
        />
      </div>
      
      <div class="filter-options">
        <button 
          v-for="filter in filterTabs" 
          :key="filter.value"
          class="filter-tab"
          :class="{ 'active': activeFilter === filter.value }"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- Claim List -->
    <div v-if="filteredClaims.length === 0" class="empty-state">
      <div class="empty-icon">📁</div>
      <h3>No claims found</h3>
      <p v-if="claims.length === 0">You haven't submitted any insurance claims yet. Complete the claim wizard to file your first claim.</p>
      <p v-else>No claims match your search query or filter criteria.</p>
      <button v-if="claims.length === 0" class="btn-primary-sm" @click="$emit('start-claim')">File a New Claim</button>
    </div>

    <div v-else class="claim-list">
      <div 
        v-for="claim in filteredClaims" 
        :key="claim.id" 
        class="claim-card"
        :class="{ 'expanded': expandedClaimId === claim.id }"
        @click="toggleExpand(claim.id)"
      >
        <div class="card-summary">
          <div class="claim-type-icon" :class="claim.claimType">
            <span v-if="claim.claimType === 'health'">🩺</span>
            <span v-else-if="claim.claimType === 'motor'">🚗</span>
            <span v-else>📦</span>
          </div>

          <div class="claim-basic-info">
            <div class="info-row-top">
              <span class="claim-ref">{{ claim.reference }}</span>
              <span class="claim-badge" :class="claim.status">{{ formatStatus(claim.status) }}</span>
            </div>
            <div class="info-row-bottom">
              <span class="claimant-name">{{ claim.claimantName }}</span>
              <span class="bullet">•</span>
              <span class="policy-num">{{ claim.policyNumber }}</span>
              <span class="bullet">•</span>
              <span class="claim-date">{{ formatDate(claim.submittedAt) }}</span>
            </div>
          </div>

          <div class="claim-payout-info">
            <span class="payout-label">Est. Payout</span>
            <strong class="payout-val">${{ formatNumber(getPayoutAmount(claim)) }}</strong>
          </div>

          <div class="expand-arrow">▼</div>
        </div>

        <!-- Expanded Details (Controlled with smooth height transitions) -->
        <div v-if="expandedClaimId === claim.id" class="card-details" @click.stop>
          <hr class="card-divider"/>
          
          <div class="details-grid">
            <div class="details-column">
              <h4>Claimant & Policy Details</h4>
              <p><strong>Email:</strong> {{ claim.claimantEmail }}</p>
              <p><strong>Phone:</strong> {{ claim.claimantPhone }}</p>
              <p><strong>Claim Type:</strong> <span class="capitalize">{{ claim.claimType }} claim</span></p>
            </div>

            <!-- Health Specific -->
            <div v-if="claim.claimType === 'health'" class="details-column">
              <h4>Medical Information</h4>
              <p><strong>Hospital:</strong> {{ claim.healthDetails.hospitalName }}</p>
              <p><strong>Treatment Date:</strong> {{ formatDate(claim.healthDetails.treatmentDate) }}</p>
              <p><strong>Total Expenses:</strong> ${{ formatNumber(claim.healthDetails.expensesAmount) }}</p>
              <p class="description-block"><strong>Diagnosis:</strong> {{ claim.healthDetails.medicalDiagnosis }}</p>
            </div>

            <!-- Motor Specific -->
            <div v-else-if="claim.claimType === 'motor'" class="details-column">
              <h4>Incident Details</h4>
              <p><strong>Registration:</strong> {{ claim.motorDetails.vehicleRegNo.toUpperCase() }}</p>
              <p><strong>Date & Location:</strong> {{ formatDate(claim.motorDetails.accidentDate) }} at {{ claim.motorDetails.accidentLocation }}</p>
              <p><strong>Severity:</strong> <span class="capitalize">{{ claim.motorDetails.damageSeverity }}</span></p>
              <p><strong>Police Report?</strong> <span class="capitalize">{{ claim.motorDetails.policeReportFiled }}</span></p>
              <p><strong>Repair Estimate:</strong> ${{ formatNumber(claim.motorDetails.repairEstimate) }}</p>
            </div>

            <!-- General Specific -->
            <div v-else class="details-column">
              <h4>Property Details</h4>
              <p><strong>Item:</strong> {{ claim.generalDetails.itemName }}</p>
              <p><strong>Purchase Date:</strong> {{ formatDate(claim.generalDetails.purchaseDate) }}</p>
              <p><strong>Type of Loss:</strong> <span class="capitalize">{{ formatLossType(claim.generalDetails.lossType) }}</span></p>
              <p><strong>Estimated Value:</strong> ${{ formatNumber(claim.generalDetails.estimatedValue) }}</p>
              <p class="description-block"><strong>Incident Description:</strong> {{ claim.generalDetails.incidentDescription }}</p>
            </div>
          </div>

          <!-- Document sublist -->
          <div v-if="claim.supportingDocuments && claim.supportingDocuments.length > 0" class="details-docs">
            <h4>Uploaded Documents</h4>
            <div class="docs-mini-grid">
              <div v-for="(file, idx) in claim.supportingDocuments" :key="idx" class="doc-mini-item">
                <span class="file-icon">📄</span>
                <span class="file-name" :title="file.name">{{ file.name }}</span>
              </div>
            </div>
          </div>

          <!-- Progress timeline -->
          <div class="claim-timeline">
            <h4>Claim Processing Progress</h4>
            <div class="timeline-steps">
              <div class="timeline-step completed">
                <div class="timeline-bubble">✓</div>
                <span>Submitted</span>
              </div>
              <div class="timeline-step" :class="{ 'completed': ['under_review', 'approved', 'paid'].includes(claim.status) }">
                <div class="timeline-bubble">2</div>
                <span>Under Review</span>
              </div>
              <div class="timeline-step" :class="{ 'completed': ['approved', 'paid'].includes(claim.status) }">
                <div class="timeline-bubble">3</div>
                <span>Approved</span>
              </div>
              <div class="timeline-step" :class="{ 'completed': claim.status === 'paid' }">
                <div class="timeline-bubble">4</div>
                <span>Paid Out</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const props = defineProps({
  claims: {
    type: Array,
    required: true,
    default: () => []
  }
});

defineEmits(['start-claim']);

const searchQuery = ref('');
const activeFilter = ref('all');
const expandedClaimId = ref(null);

const filterTabs = [
  { value: 'all', label: 'All Claims' },
  { value: 'health', label: 'Medical' },
  { value: 'motor', label: 'Motor' },
  { value: 'general', label: 'General' }
];

const filteredClaims = computed(() => {
  let list = props.claims;

  // Filter by tab type
  if (activeFilter.value !== 'all') {
    list = list.filter(c => c.claimType === activeFilter.value);
  }

  // Filter by search query
  if (searchQuery.value.trim() !== '') {
    const q = searchQuery.value.toLowerCase().trim();
    list = list.filter(c => {
      const matchRef = c.reference.toLowerCase().includes(q);
      const matchName = c.claimantName.toLowerCase().includes(q);
      const matchPolicy = c.policyNumber.toLowerCase().includes(q);
      
      let matchSpecific = false;
      if (c.claimType === 'health') {
        matchSpecific = c.healthDetails?.hospitalName?.toLowerCase().includes(q);
      } else if (c.claimType === 'motor') {
        matchSpecific = c.motorDetails?.vehicleRegNo?.toLowerCase().includes(q) || c.motorDetails?.accidentLocation?.toLowerCase().includes(q);
      } else if (c.claimType === 'general') {
        matchSpecific = c.generalDetails?.itemName?.toLowerCase().includes(q);
      }

      return matchRef || matchName || matchPolicy || matchSpecific;
    });
  }

  return list;
});

const toggleExpand = (id) => {
  if (expandedClaimId.value === id) {
    expandedClaimId.value = null;
  } else {
    expandedClaimId.value = id;
  }
};

const getPayoutAmount = (claim) => {
  if (claim.claimType === 'health') return claim.estimates?.health || 0;
  if (claim.claimType === 'motor') return claim.estimates?.motor || 0;
  return claim.estimates?.general || 0;
};

// Formats
const formatStatus = (status) => {
  if (status === 'pending_review') return 'Submitted';
  if (status === 'under_review') return 'Under Review';
  return status.charAt(0).toUpperCase() + status.slice(1);
};

const formatLossType = (val) => {
  if (!val) return '';
  return val.replace('_', ' ');
};

const formatDate = (val) => {
  if (!val) return '';
  const d = new Date(val);
  return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
};

const formatNumber = (val) => {
  const parsed = parseFloat(val);
  return isNaN(parsed) ? '0.00' : parsed.toFixed(2);
};
</script>

<style scoped>
.history-container {
  animation: fadeIn 0.4s ease-out;
  width: 100%;
}

.history-header {
  margin-bottom: 2rem;
}

.history-header h2 {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.history-header p {
  font-size: 0.95rem;
  color: var(--color-text);
  opacity: 0.85;
}

/* Filters and Search */
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
    max-width: 400px;
  }
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text);
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
  font-family: inherit;
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

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background-color: var(--color-background-soft);
  border: 1px dashed var(--color-border);
  border-radius: 10px;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.empty-state p {
  font-size: 0.9rem;
  opacity: 0.8;
  max-width: 400px;
  margin: 0 auto 1.5rem auto;
}

.btn-primary-sm {
  background-color: #3182ce;
  color: #ffffff;
  border: none;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: bold;
  cursor: pointer;
}

/* Claim Cards list */
.claim-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.claim-card {
  border: 1px solid var(--color-border);
  border-radius: 10px;
  background-color: var(--color-background);
  cursor: pointer;
  transition: all 0.2s ease;
  overflow: hidden;
}

.claim-card:hover {
  border-color: rgba(49, 130, 206, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.card-summary {
  display: flex;
  align-items: center;
  padding: 1.25rem;
  gap: 1rem;
}

.claim-type-icon {
  width: 2.75rem;
  height: 2.75rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  flex-shrink: 0;
}

.claim-type-icon.health { background-color: rgba(49, 151, 149, 0.1); }
.claim-type-icon.motor { background-color: rgba(221, 107, 32, 0.1); }
.claim-type-icon.general { background-color: rgba(128, 90, 213, 0.1); }

.claim-basic-info {
  flex: 1;
}

.info-row-top {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.25rem;
}

.claim-ref {
  font-weight: 700;
  color: var(--color-heading);
  font-size: 1rem;
}

.claim-badge {
  font-size: 0.75rem;
  font-weight: bold;
  padding: 0.15rem 0.6rem;
  border-radius: 12px;
}

/* Badge colors */
.claim-badge.pending_review { background-color: #ebf8ff; color: #2b6cb0; }
.claim-badge.under_review { background-color: #fefcbf; color: #975a16; }
.claim-badge.approved { background-color: #f0fff4; color: #276749; }
.claim-badge.paid { background-color: #e6fffa; color: #234e52; }

.info-row-bottom {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.4rem;
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.75;
}

.bullet {
  font-size: 0.6rem;
}

.claim-payout-info {
  text-align: right;
  display: flex;
  flex-direction: column;
  margin-right: 0.5rem;
}

.payout-label {
  font-size: 0.75rem;
  opacity: 0.7;
}

.payout-val {
  font-size: 1.1rem;
  color: #2f855a;
}

.expand-arrow {
  font-size: 0.75rem;
  opacity: 0.5;
  transition: transform 0.2s;
}

.claim-card.expanded .expand-arrow {
  transform: rotate(180deg);
}

/* Expanded Card styling */
.card-details {
  padding: 0 1.25rem 1.25rem 1.25rem;
  cursor: default;
  animation: slideDown 0.25s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-divider {
  border: 0;
  border-top: 1px solid var(--color-border);
  margin: 0 0 1.25rem 0;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

@media (min-width: 640px) {
  .details-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.details-column h4 {
  font-size: 0.85rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-heading);
  margin-bottom: 0.75rem;
}

.details-column p {
  font-size: 0.85rem;
  margin-bottom: 0.4rem;
  color: var(--color-text);
}

.description-block {
  background-color: var(--color-background-soft);
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  margin-top: 0.5rem;
  white-space: pre-wrap;
}

/* Expanded documents list */
.details-docs {
  margin-top: 1.5rem;
}

.details-docs h4 {
  font-size: 0.85rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-heading);
  margin-bottom: 0.75rem;
}

.docs-mini-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.doc-mini-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  padding: 0.35rem 0.6rem;
  border-radius: 4px;
}

.doc-mini-item .file-icon {
  font-size: 1rem;
}

.doc-mini-item .file-name {
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--color-text);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Timeline progress on claims */
.claim-timeline {
  margin-top: 1.5rem;
  border-top: 1px solid var(--color-border);
  padding-top: 1.25rem;
}

.claim-timeline h4 {
  font-size: 0.85rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.timeline-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  max-width: 450px;
}

.timeline-steps::before {
  content: '';
  position: absolute;
  top: 0.8rem;
  left: 5%;
  right: 5%;
  height: 2px;
  background-color: var(--color-border);
  z-index: 1;
}

.timeline-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
  flex: 1;
}

.timeline-bubble {
  width: 1.6rem;
  height: 1.6rem;
  border-radius: 50%;
  background-color: var(--color-background);
  border: 2px solid var(--color-border);
  font-size: 0.75rem;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text);
  transition: all 0.3s ease;
}

.timeline-step span {
  font-size: 0.75rem;
  margin-top: 0.4rem;
  font-weight: 500;
  color: var(--color-text);
  opacity: 0.7;
}

.timeline-step.completed .timeline-bubble {
  background-color: #3182ce;
  border-color: #3182ce;
  color: white;
}

.timeline-step.completed span {
  color: #3182ce;
  font-weight: bold;
  opacity: 1;
}

.capitalize { text-transform: capitalize; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
