<template>
  <div class="step-content">
    <!-- Main Review View -->
    <div v-if="!submissionSuccess" class="review-view">
      <h2 class="step-title">Review & Submit</h2>
      <p class="step-description">Verify that all claim information is accurate before submitting to the underwriting team.</p>

      <div class="review-sections">
        <!-- Claimant & Policy -->
        <div class="review-card">
          <div class="card-header">
            <h3>👤 Policy & Claimant Information</h3>
          </div>
          <div class="card-body grid-2-col">
            <div class="review-field">
              <span class="field-label">Policy Number</span>
              <span class="field-valueHighlight">{{ formData.policyNumber }}</span>
            </div>
            <div class="review-field">
              <span class="field-label">Claim Type</span>
              <span class="field-value capitalize">{{ formatClaimType(formData.claimType) }}</span>
            </div>
            <div class="review-field">
              <span class="field-label">Full Name</span>
              <span class="field-value">{{ formData.claimantName }}</span>
            </div>
            <div class="review-field">
              <span class="field-label">Email Address</span>
              <span class="field-value">{{ formData.claimantEmail }}</span>
            </div>
            <div class="review-field">
              <span class="field-label">Phone Number</span>
              <span class="field-value">{{ formData.claimantPhone }}</span>
            </div>
          </div>
        </div>

        <!-- Incident Details (Dynamic based on Claim Type) -->
        <div class="review-card">
          <div class="card-header">
            <h3>📝 Incident & Details</h3>
          </div>
          
          <!-- Health Details Summary -->
          <div v-if="formData.claimType === 'health'" class="card-body">
            <div class="grid-2-col">
              <div class="review-field">
                <span class="field-label">Hospital / Clinic</span>
                <span class="field-value">{{ formData.healthDetails.hospitalName }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Treatment Date</span>
                <span class="field-value">{{ formatDate(formData.healthDetails.treatmentDate) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Submitted Expenses</span>
                <span class="field-valueHighlight font-lg">${{ formatNumber(formData.healthDetails.expensesAmount) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Estimated Payout</span>
                <span class="payout-highlight">${{ formatNumber(formData.estimates?.health) }}</span>
              </div>
            </div>
            <div class="review-field full-width">
              <span class="field-label">Diagnosis & Description</span>
              <p class="field-paragraph">{{ formData.healthDetails.medicalDiagnosis }}</p>
            </div>
          </div>

          <!-- Motor Details Summary -->
          <div v-else-if="formData.claimType === 'motor'" class="card-body">
            <div class="grid-2-col">
              <div class="review-field">
                <span class="field-label">Vehicle Registration</span>
                <span class="field-value uppercase">{{ formData.motorDetails.vehicleRegNo }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Accident Date</span>
                <span class="field-value">{{ formatDate(formData.motorDetails.accidentDate) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Accident Location</span>
                <span class="field-value">{{ formData.motorDetails.accidentLocation }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Damage Severity</span>
                <span class="field-value capitalize">{{ formData.motorDetails.damageSeverity }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Police Report Filed?</span>
                <span class="field-value capitalize">{{ formData.motorDetails.policeReportFiled }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Repair Estimate</span>
                <span class="field-valueHighlight font-lg">${{ formatNumber(formData.motorDetails.repairEstimate) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Estimated Payout</span>
                <span class="payout-highlight">${{ formatNumber(formData.estimates?.motor) }}</span>
              </div>
            </div>
          </div>

          <!-- General Details Summary -->
          <div v-else-if="formData.claimType === 'general'" class="card-body">
            <div class="grid-2-col">
              <div class="review-field">
                <span class="field-label">Item Description</span>
                <span class="field-value">{{ formData.generalDetails.itemName }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Approx Purchase Date</span>
                <span class="field-value">{{ formatDate(formData.generalDetails.purchaseDate) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Type of Loss</span>
                <span class="field-value capitalize">{{ formatLossType(formData.generalDetails.lossType) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Estimated Market Value</span>
                <span class="field-valueHighlight font-lg">${{ formatNumber(formData.generalDetails.estimatedValue) }}</span>
              </div>
              <div class="review-field">
                <span class="field-label">Estimated Payout</span>
                <span class="payout-highlight">${{ formatNumber(formData.estimates?.general) }}</span>
              </div>
            </div>
            <div class="review-field full-width">
              <span class="field-label">Description of Loss Event</span>
              <p class="field-paragraph">{{ formData.generalDetails.incidentDescription }}</p>
            </div>
          </div>
        </div>

        <!-- Supporting Docs Summary -->
        <div class="review-card">
          <div class="card-header">
            <h3>📁 Uploaded Supporting Documents</h3>
          </div>
          <div class="card-body">
            <div v-if="formData.supportingDocuments.length === 0" class="no-docs">
              <p>No documents uploaded. <em>(It is recommended to upload files to speed up processing, but not mandatory).</em></p>
            </div>
            <div v-else class="summary-file-list">
              <div v-for="(file, index) in formData.supportingDocuments" :key="index" class="summary-file-item">
                <span class="file-icon">📄</span>
                <div class="file-meta">
                  <span class="file-name">{{ file.name }}</span>
                  <span class="file-size">{{ formatBytes(file.size) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Consent Agreement -->
      <div class="terms-container">
        <label class="checkbox-label" :class="{ 'has-error': termsError }">
          <input 
            type="checkbox" 
            v-model="termsAgreed" 
            class="terms-checkbox"
            @change="termsError = false"
          />
          <span class="checkbox-custom"></span>
          <span class="terms-text">
            I hereby declare that the details provided are true, complete, and accurate. I understand that submitting false claims is subject to policy termination and legal prosecution under insurance regulations.
          </span>
        </label>
        <p v-if="termsError" class="terms-error-message">You must agree to the declaration statement to submit your claim.</p>
      </div>

      <!-- Action Area for Submission -->
      <div class="action-footer">
        <slot name="navigation-buttons" :termsAgreed="termsAgreed" :triggerSubmit="handleSubmit"></slot>
      </div>
    </div>

    <!-- Submission Simulation & Loader -->
    <div v-else-if="isSubmitting" class="submitting-overlay">
      <div class="spinner-container">
        <div class="spinner"></div>
        <div class="pulse-ring"></div>
      </div>
      <h3 class="processing-title">Processing Claim Submission</h3>
      <p class="processing-subtitle">{{ activeProcessingStep }}</p>
      <div class="progress-bar-container">
        <div class="progress-bar" :style="{ width: submittingProgress + '%' }"></div>
      </div>
    </div>

    <!-- GORGEOUS Success Screen -->
    <div v-else class="success-screen">
      <div class="success-checkmark">
        <div class="check-icon">
          <span class="icon-line line-tip"></span>
          <span class="icon-line line-long"></span>
          <div class="icon-circle"></div>
          <div class="icon-fix"></div>
        </div>
      </div>
      
      <h2 class="success-title">Claim Submitted Successfully!</h2>
      <p class="success-subtitle">Your claim is now under review by our underwriting department.</p>

      <div class="ref-box">
        <span class="ref-label">CLAIM REFERENCE NUMBER</span>
        <strong class="ref-number">{{ claimReference }}</strong>
        <span class="copy-hint" @click="copyRef">Click to copy reference</span>
      </div>

      <div class="next-steps">
        <h4>What Happens Next?</h4>
        <ul>
          <li>
            <span class="step-num">1</span>
            <div class="step-info">
              <h5>Claim Intake & Validation</h5>
              <p>Our claims adjusters will review your uploaded documents and policy status (approx. 24-48 hours).</p>
            </div>
          </li>
          <li>
            <span class="step-num">2</span>
            <div class="step-info">
              <h5>Verification & Estimating</h5>
              <p>We may contact you or the third-party providers (hospitals, workshops) if additional details are needed.</p>
            </div>
          </li>
          <li>
            <span class="step-num">3</span>
            <div class="step-info">
              <h5>Payout Settlement</h5>
              <p>Once approved, payouts are processed instantly using your registered payment methods.</p>
            </div>
          </li>
        </ul>
      </div>

      <div class="success-buttons">
        <button class="btn-primary" @click="$emit('view-history')">View My Claims</button>
        <button class="btn-secondary" @click="$emit('reset-form')">Submit Another Claim</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  isSubmitting: {
    type: Boolean,
    default: false
  },
  submissionSuccess: {
    type: Boolean,
    default: false
  },
  claimReference: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['submit-claim', 'view-history', 'reset-form']);

const termsAgreed = ref(false);
const termsError = ref(false);

const handleSubmit = () => {
  if (!termsAgreed.value) {
    termsError.value = true;
    return false;
  }
  emit('submit-claim');
  runSimulatedSteps();
  return true;
};

// Simulated submission progress loading indicators
const submittingProgress = ref(0);
const activeProcessingStep = ref('Establishing secure connection...');
const processingSteps = [
  { p: 15, msg: 'Encrypting claim information...' },
  { p: 35, msg: 'Uploading and scanning supporting documents...' },
  { p: 60, msg: 'Calculating policy parameters & preliminary reserve...' },
  { p: 85, msg: 'Generating immutable claim transaction record...' },
  { p: 100, msg: 'Underwriting database synchronized successfully!' }
];

const runSimulatedSteps = () => {
  submittingProgress.value = 0;
  activeProcessingStep.value = 'Establishing secure connection...';
  
  processingSteps.forEach((step, idx) => {
    setTimeout(() => {
      submittingProgress.value = step.p;
      activeProcessingStep.value = step.msg;
    }, (idx + 1) * 600);
  });
};

// Formatting utilities
const formatClaimType = (val) => {
  if (!val) return '';
  if (val === 'motor') return 'Motor Vehicle Claim';
  return val.charAt(0).toUpperCase() + val.slice(1) + ' Claim';
};

const formatLossType = (val) => {
  if (!val) return '';
  return val.replace('_', ' ');
};

const formatDate = (val) => {
  if (!val) return 'Not Provided';
  try {
    const d = new Date(val);
    if (isNaN(d.getTime())) return val;
    return d.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
  } catch {
    return val;
  }
};

const formatNumber = (val) => {
  const parsed = parseFloat(val);
  return isNaN(parsed) ? '0.00' : parsed.toFixed(2);
};

const formatBytes = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

const copyRef = () => {
  if (!props.claimReference) return;
  navigator.clipboard.writeText(props.claimReference);
  alert('Claim Reference copied to clipboard!');
};
</script>

<style scoped>
.step-content {
  animation: fadeIn 0.4s ease-out;
}

.step-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.step-description {
  font-size: 0.95rem;
  color: var(--color-text);
  opacity: 0.85;
  margin-bottom: 2rem;
}

.review-sections {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.review-card {
  border: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  border-radius: 10px;
  overflow: hidden;
}

.card-header {
  background-color: var(--color-background-mute);
  padding: 0.75rem 1.25rem;
  border-bottom: 1px solid var(--color-border);
}

.card-header h3 {
  font-size: 0.95rem;
  font-weight: bold;
  color: var(--color-heading);
  margin: 0;
}

.card-body {
  padding: 1.25rem;
}

.grid-2-col {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
}

@media (min-width: 640px) {
  .grid-2-col {
    grid-template-columns: repeat(2, 1fr);
  }
}

.review-field {
  display: flex;
  flex-direction: column;
}

.review-field.full-width {
  grid-column: span 1;
}

@media (min-width: 640px) {
  .review-field.full-width {
    grid-column: span 2;
  }
}

.field-label {
  font-size: 0.75rem;
  font-weight: bold;
  color: var(--color-text);
  opacity: 0.65;
  text-transform: uppercase;
  margin-bottom: 0.25rem;
}

.field-value {
  font-size: 0.95rem;
  color: var(--color-text);
  font-weight: 500;
}

.field-valueHighlight {
  font-size: 0.95rem;
  color: var(--color-heading);
  font-weight: 700;
}

.payout-highlight {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2f855a;
}

.font-lg {
  font-size: 1.1rem;
}

.field-paragraph {
  font-size: 0.9rem;
  line-height: 1.5;
  color: var(--color-text);
  background-color: var(--color-background);
  padding: 0.75rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
  margin-top: 0.25rem;
  white-space: pre-wrap;
}

.capitalize { text-transform: capitalize; }
.uppercase { text-transform: uppercase; }

.no-docs {
  font-size: 0.9rem;
  color: var(--color-text);
  opacity: 0.8;
  text-align: center;
  padding: 0.5rem 0;
}

.summary-file-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem;
}

@media (min-width: 640px) {
  .summary-file-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

.summary-file-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0.75rem;
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 6px;
}

.file-icon {
  font-size: 1.25rem;
}

.file-meta {
  display: flex;
  flex-direction: column;
}

.file-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-heading);
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 0.7rem;
  opacity: 0.7;
}

/* Consent Agreement styling */
.terms-container {
  margin-top: 2rem;
  padding: 1.25rem;
  background-color: rgba(49, 130, 206, 0.03);
  border: 1px solid rgba(49, 130, 206, 0.15);
  border-radius: 8px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  cursor: pointer;
  user-select: none;
}

.terms-checkbox {
  opacity: 0;
  position: absolute;
  width: 0;
  height: 0;
}

.checkbox-custom {
  position: relative;
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid var(--color-border);
  border-radius: 4px;
  background-color: var(--color-background);
  transition: all 0.2s ease;
  flex-shrink: 0;
  margin-top: 0.15rem;
}

.terms-checkbox:checked + .checkbox-custom {
  background-color: #3182ce;
  border-color: #3182ce;
}

.terms-checkbox:checked + .checkbox-custom::after {
  content: "";
  position: absolute;
  left: 0.3rem;
  top: 0.1rem;
  width: 0.35rem;
  height: 0.6rem;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label.has-error .checkbox-custom {
  border-color: #e53e3e;
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.2);
}

.terms-text {
  font-size: 0.85rem;
  line-height: 1.5;
  color: var(--color-text);
}

.terms-error-message {
  color: #e53e3e;
  font-size: 0.8rem;
  margin-top: 0.5rem;
  font-weight: 600;
  padding-left: 2rem;
}

/* Submission simulation styling */
.submitting-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background-color: var(--color-background-soft);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  min-height: 400px;
}

.spinner-container {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 2rem;
}

.spinner {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 6px solid var(--color-border);
  border-top-color: #3182ce;
  animation: spin 1s linear infinite;
}

.pulse-ring {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border: 2px solid rgba(49, 130, 206, 0.3);
  border-radius: 50%;
  animation: pulse 1.5s ease-out infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0% { transform: scale(0.95); opacity: 0.8; }
  100% { transform: scale(1.1); opacity: 0; }
}

.processing-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.processing-subtitle {
  font-size: 0.95rem;
  color: #3182ce;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.progress-bar-container {
  width: 100%;
  max-width: 300px;
  height: 6px;
  background-color: var(--color-border);
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background-color: #3182ce;
  transition: width 0.3s ease;
}

/* Success Screen CSS */
.success-screen {
  text-align: center;
  padding: 3rem 1.5rem;
  animation: scaleIn 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

.success-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #2f855a;
  margin: 1.5rem 0 0.5rem 0;
}

.success-subtitle {
  font-size: 0.95rem;
  color: var(--color-text);
  opacity: 0.8;
  margin-bottom: 2rem;
}

/* Success Checkmark Keyframe Animation */
.success-checkmark {
  width: 80px;
  height: 80px;
  margin: 0 auto;
}

.success-checkmark .check-icon {
  width: 80px;
  height: 80px;
  position: relative;
  border-radius: 50%;
  box-sizing: content-box;
  border: 4px solid #48bb78;
}

.success-checkmark .check-icon::before,
.success-checkmark .check-icon::after {
  content: '';
  height: 100px;
  position: absolute;
  background: var(--color-background);
  transform: rotate(-45deg);
}

.success-checkmark .check-icon::before {
  border-radius: 100px 0 0 100px;
  top: -7px;
  left: -33px;
  transform-origin: 100% 50%;
  transform: rotate(-45deg);
}

.success-checkmark .check-icon::after {
  border-radius: 0 100px 100px 0;
  top: -8px;
  left: 30px;
  transform-origin: 0% 50%;
  transform: rotate(-45deg);
}

.success-checkmark .check-icon .icon-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  position: absolute;
  z-index: 10;
}

.success-checkmark .check-icon .icon-fix {
  width: 5px;
  height: 5px;
  background-color: var(--color-background);
  position: absolute;
  left: 28px;
  top: 70px;
  z-index: 1;
}

.success-checkmark .check-icon .icon-line {
  height: 5px;
  background-color: #48bb78;
  display: block;
  border-radius: 2px;
  position: absolute;
  z-index: 10;
}

.success-checkmark .check-icon .icon-line.line-tip {
  width: 25px;
  left: 14px;
  top: 46px;
  transform: rotate(45deg);
  animation: icon-line-tip 0.75s;
}

.success-checkmark .check-icon .icon-line.line-long {
  width: 47px;
  right: 8px;
  top: 38px;
  transform: rotate(-45deg);
  animation: icon-line-long 0.75s;
}

@keyframes icon-line-tip {
  0% { width: 0; left: 1px; top: 19px; }
  54% { width: 0; left: 1px; top: 19px; }
  70% { width: 50px; left: -8px; top: 37px; }
  84% { width: 17px; left: 21px; top: 48px; }
  100% { width: 25px; left: 14px; top: 46px; }
}

@keyframes icon-line-long {
  0% { width: 0; right: 46px; top: 54px; }
  65% { width: 0; right: 46px; top: 54px; }
  84% { width: 55px; right: 0px; top: 35px; }
  100% { width: 47px; right: 8px; top: 38px; }
}

/* Ref Box styling */
.ref-box {
  background-color: var(--color-background-soft);
  border: 1px dashed var(--color-border);
  border-radius: 8px;
  padding: 1.25rem;
  margin-bottom: 2.5rem;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
}

.ref-label {
  font-size: 0.75rem;
  font-weight: bold;
  opacity: 0.65;
  letter-spacing: 0.05em;
  margin-bottom: 0.25rem;
}

.ref-number {
  font-size: 1.6rem;
  color: var(--color-heading);
  letter-spacing: 1px;
}

.copy-hint {
  font-size: 0.75rem;
  color: #3182ce;
  cursor: pointer;
  margin-top: 0.4rem;
  text-decoration: underline;
}

.copy-hint:hover {
  color: #2b6cb0;
}

/* Next steps list styling */
.next-steps {
  text-align: left;
  max-width: 500px;
  margin: 0 auto 3rem auto;
  border-top: 1px solid var(--color-border);
  padding-top: 1.5rem;
}

.next-steps h4 {
  font-weight: 700;
  font-size: 1.05rem;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.next-steps ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.next-steps li {
  display: flex;
  gap: 1rem;
}

.step-num {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  background-color: rgba(49, 130, 206, 0.1);
  color: #3182ce;
  font-weight: bold;
  font-size: 0.85rem;
  flex-shrink: 0;
  margin-top: 0.15rem;
}

.step-info h5 {
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--color-heading);
  margin: 0 0 0.2rem 0;
}

.step-info p {
  font-size: 0.85rem;
  color: var(--color-text);
  opacity: 0.8;
  margin: 0;
}

/* Buttons container in Success view */
.success-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-primary {
  background-color: #3182ce;
  color: #ffffff;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #2b6cb0;
}

.btn-secondary {
  background-color: transparent;
  color: var(--color-text);
  border: 1px solid var(--color-border);
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background-color: var(--color-background-soft);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
