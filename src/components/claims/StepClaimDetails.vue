<template>
  <div class="step-content">
    <h2 class="step-title">Claim Specifications</h2>
    <p class="step-description">Fill out the detailed circumstances surrounding your claim. Fields adjust based on your claim type.</p>

    <!-- Health Claim Details -->
    <div v-if="claimType === 'health'" class="form-section">
      <div class="section-badge health-badge">Medical / Health Form</div>
      <div class="form-grid">
        <BaseInput
          v-model="localHealth.hospitalName"
          label="Hospital / Clinic Name"
          placeholder="e.g., General Hospital"
          required
          :error="errors.hospitalName"
        />
        <BaseInput
          v-model="localHealth.treatmentDate"
          label="Treatment / Admission Date"
          type="date"
          required
          :error="errors.treatmentDate"
        />
        <BaseInput
          v-model="localHealth.expensesAmount"
          label="Total Medical Expenses ($)"
          type="number"
          placeholder="e.g., 1500"
          required
          :error="errors.expensesAmount"
        />
        <div class="input-group full-width">
          <label class="input-label">Medical Diagnosis & Description <span class="required-star">*</span></label>
          <textarea
            v-model="localHealth.medicalDiagnosis"
            class="base-textarea"
            :class="{ 'has-error': errors.medicalDiagnosis }"
            placeholder="Please detail the medical conditions, treatment received, and diagnosis..."
          ></textarea>
          <span v-if="errors.medicalDiagnosis" class="error-message">{{ errors.medicalDiagnosis }}</span>
        </div>
      </div>
      
      <!-- Live Payout Estimator -->
      <div class="estimator-card health-estimator">
        <div class="estimator-header">
          <span class="icon">📊</span>
          <h4>Live Payout Estimator</h4>
        </div>
        <div class="estimator-body">
          <div class="estimator-row">
            <span>Expenses Submitted:</span>
            <strong>${{ formatNumber(localHealth.expensesAmount) }}</strong>
          </div>
          <div class="estimator-row">
            <span>Deductible:</span>
            <span>-$100.00</span>
          </div>
          <div class="estimator-row">
            <span>Coverage Rate:</span>
            <span>80%</span>
          </div>
          <hr class="estimator-divider"/>
          <div class="estimator-row final-row">
            <span>Estimated Payout:</span>
            <span class="payout-amount">${{ formatNumber(healthPayoutEstimate) }}</span>
          </div>
          <p class="estimator-note">* This is an automated preliminary estimate. Final payouts are subject to claims adjuster approval.</p>
        </div>
      </div>
    </div>

    <!-- Motor Vehicle Claim Details -->
    <div v-else-if="claimType === 'motor'" class="form-section">
      <div class="section-badge motor-badge">Motor Vehicle Form</div>
      <div class="form-grid">
        <BaseInput
          v-model="localMotor.vehicleRegNo"
          label="Vehicle Registration Number (Plate No.)"
          placeholder="e.g., ABC-1234"
          required
          :error="errors.vehicleRegNo"
        />
        <BaseInput
          v-model="localMotor.accidentDate"
          label="Date of Incident"
          type="date"
          required
          :error="errors.accidentDate"
        />
        <BaseInput
          v-model="localMotor.accidentLocation"
          label="Incident Location / Address"
          placeholder="e.g., 5th Ave and Main St, NY"
          required
          :error="errors.accidentLocation"
        />
        <BaseSelect
          v-model="localMotor.damageSeverity"
          label="Damage Severity"
          :options="severityOptions"
          placeholder="Select damage severity"
          required
          :error="errors.damageSeverity"
        />
        <BaseSelect
          v-model="localMotor.policeReportFiled"
          label="Was a Police Report Filed?"
          :options="yesNoOptions"
          placeholder="Select option"
          required
          :error="errors.policeReportFiled"
        />
        <BaseInput
          v-model="localMotor.repairEstimate"
          label="Repair Cost Estimate ($)"
          type="number"
          placeholder="e.g., 3000"
          required
          :error="errors.repairEstimate"
        />
      </div>

      <!-- Live Payout Estimator -->
      <div class="estimator-card motor-estimator">
        <div class="estimator-header">
          <span class="icon">🚗</span>
          <h4>Live Payout Estimator</h4>
        </div>
        <div class="estimator-body">
          <div class="estimator-row">
            <span>Repair Estimate:</span>
            <strong>${{ formatNumber(localMotor.repairEstimate) }}</strong>
          </div>
          <div class="estimator-row">
            <span>Deductible:</span>
            <span>-$250.00</span>
          </div>
          <div class="estimator-row">
            <span>Severity Coverage Rate:</span>
            <span>{{ motorCoveragePercentage }}% ({{ localMotor.damageSeverity || 'None' }})</span>
          </div>
          <hr class="estimator-divider"/>
          <div class="estimator-row final-row">
            <span>Estimated Payout:</span>
            <span class="payout-amount">${{ formatNumber(motorPayoutEstimate) }}</span>
          </div>
          <p class="estimator-note">* Subject to standard deductibles, severity caps, and verified workshop repair assessments.</p>
        </div>
      </div>
    </div>

    <!-- General / Property Claim Details -->
    <div v-else-if="claimType === 'general'" class="form-section">
      <div class="section-badge general-badge">General / Property Form</div>
      <div class="form-grid">
        <BaseInput
          v-model="localGeneral.itemName"
          label="Item / Property Description"
          placeholder="e.g., iPhone 15 Pro, Trek Bicycle"
          required
          :error="errors.itemName"
        />
        <BaseInput
          v-model="localGeneral.purchaseDate"
          label="Approximate Purchase Date"
          type="date"
          required
          :error="errors.purchaseDate"
        />
        <BaseSelect
          v-model="localGeneral.lossType"
          label="Type of Loss"
          :options="lossTypeOptions"
          placeholder="Select type of loss"
          required
          :error="errors.lossType"
        />
        <BaseInput
          v-model="localGeneral.estimatedValue"
          label="Estimated Market Value ($)"
          type="number"
          placeholder="e.g., 1000"
          required
          :error="errors.estimatedValue"
        />
        <div class="input-group full-width">
          <label class="input-label">Detailed Description of Incident <span class="required-star">*</span></label>
          <textarea
            v-model="localGeneral.incidentDescription"
            class="base-textarea"
            :class="{ 'has-error': errors.incidentDescription }"
            placeholder="Please detail how the incident occurred, location, and the state of the property..."
          ></textarea>
          <span v-if="errors.incidentDescription" class="error-message">{{ errors.incidentDescription }}</span>
        </div>
      </div>

      <!-- Live Payout Estimator -->
      <div class="estimator-card general-estimator">
        <div class="estimator-header">
          <span class="icon">🏡</span>
          <h4>Live Payout Estimator</h4>
        </div>
        <div class="estimator-body">
          <div class="estimator-row">
            <span>Item Estimated Value:</span>
            <strong>${{ formatNumber(localGeneral.estimatedValue) }}</strong>
          </div>
          <div class="estimator-row">
            <span>Deductible:</span>
            <span>-$50.00</span>
          </div>
          <div class="estimator-row">
            <span>Loss Type Coverage:</span>
            <span>{{ generalCoveragePercentage }}% ({{ localGeneral.lossType || 'None' }})</span>
          </div>
          <hr class="estimator-divider"/>
          <div class="estimator-row final-row">
            <span>Estimated Payout:</span>
            <span class="payout-amount">${{ formatNumber(generalPayoutEstimate) }}</span>
          </div>
          <p class="estimator-note">* General claims evaluate depreciation and proof-of-purchase. Estimates represent maximum allowances.</p>
        </div>
      </div>
    </div>

    <!-- Custom Drag-and-Drop / Browse Supporting Documents (Simulated) -->
    <div class="docs-section">
      <label class="input-label">Supporting Documentation (Receipts, Police Reports, Photos)</label>
      <div 
        class="dropzone" 
        :class="{ 'dragging': isDragging }"
        @dragover.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        @drop.prevent="handleDrop"
        @click="triggerFileInput"
      >
        <input 
          ref="fileInput" 
          type="file" 
          multiple 
          class="hidden-file-input" 
          @change="handleFileSelect"
        />
        <div class="dropzone-content">
          <span class="upload-icon">📁</span>
          <p class="upload-text"><strong>Drag & Drop</strong> files here or <span class="browse-link">browse</span></p>
          <p class="upload-subtext">Supported formats: PDF, JPG, PNG (Max 5MB per file)</p>
        </div>
      </div>

      <!-- File List -->
      <div v-if="localDocs.length > 0" class="file-list">
        <div 
          v-for="(doc, index) in localDocs" 
          :key="index" 
          class="file-item"
        >
          <div class="file-info">
            <span class="file-icon">📄</span>
            <div class="file-meta">
              <span class="file-name">{{ doc.name }}</span>
              <span class="file-size">{{ formatBytes(doc.size) }}</span>
            </div>
          </div>
          <button class="delete-file-btn" @click.stop="removeFile(index)" title="Remove file">×</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, computed } from 'vue';
import BaseInput from './BaseInput.vue';
import BaseSelect from './BaseSelect.vue';

const props = defineProps({
  claimType: {
    type: String,
    required: true
  },
  modelValue: {
    type: Object,
    required: true
  },
  errors: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue']);

// Local states for sections
const localHealth = reactive({
  hospitalName: props.modelValue.healthDetails?.hospitalName || '',
  treatmentDate: props.modelValue.healthDetails?.treatmentDate || '',
  expensesAmount: props.modelValue.healthDetails?.expensesAmount || '',
  medicalDiagnosis: props.modelValue.healthDetails?.medicalDiagnosis || ''
});

const localMotor = reactive({
  vehicleRegNo: props.modelValue.motorDetails?.vehicleRegNo || '',
  accidentDate: props.modelValue.motorDetails?.accidentDate || '',
  accidentLocation: props.modelValue.motorDetails?.accidentLocation || '',
  damageSeverity: props.modelValue.motorDetails?.damageSeverity || '',
  policeReportFiled: props.modelValue.motorDetails?.policeReportFiled || '',
  repairEstimate: props.modelValue.motorDetails?.repairEstimate || ''
});

const localGeneral = reactive({
  itemName: props.modelValue.generalDetails?.itemName || '',
  purchaseDate: props.modelValue.generalDetails?.purchaseDate || '',
  lossType: props.modelValue.generalDetails?.lossType || '',
  estimatedValue: props.modelValue.generalDetails?.estimatedValue || '',
  incidentDescription: props.modelValue.generalDetails?.incidentDescription || ''
});

const localDocs = ref(props.modelValue.supportingDocuments || []);

// Synchronize changes to parent
const syncToParent = () => {
  emit('update:modelValue', {
    ...props.modelValue,
    healthDetails: { ...localHealth },
    motorDetails: { ...localMotor },
    generalDetails: { ...localGeneral },
    supportingDocuments: [...localDocs.value],
    // Save computed estimates directly in model to display in review
    estimates: {
      health: healthPayoutEstimate.value,
      motor: motorPayoutEstimate.value,
      general: generalPayoutEstimate.value
    }
  });
};

// Deep watch sub-states to trigger sync
watch([localHealth, localMotor, localGeneral, localDocs], () => {
  syncToParent();
}, { deep: true });

// Sync changes from parent props (such as draft recovery)
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    if (newVal.healthDetails) {
      Object.assign(localHealth, newVal.healthDetails);
    }
    if (newVal.motorDetails) {
      Object.assign(localMotor, newVal.motorDetails);
    }
    if (newVal.generalDetails) {
      Object.assign(localGeneral, newVal.generalDetails);
    }
    if (newVal.supportingDocuments) {
      localDocs.value = newVal.supportingDocuments;
    }
  }
}, { deep: true });

// Options definition
const severityOptions = [
  { value: 'minor', label: 'Minor (Scratches, small dents)' },
  { value: 'moderate', label: 'Moderate (Bumper damage, body panels)' },
  { value: 'severe', label: 'Severe (Engine failure, structural damage, towed)' }
];

const yesNoOptions = [
  { value: 'yes', label: 'Yes' },
  { value: 'no', label: 'No' }
];

const lossTypeOptions = [
  { value: 'theft', label: 'Theft / Burglary' },
  { value: 'water_damage', label: 'Water Damage' },
  { value: 'accidental_loss', label: 'Accidental Loss / Damage' }
];

// Formatting helpers
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

// --- Computed Estimates for claim types ---

// Health Estimate: 80% of expenses exceeding a $100 deductible, capped at $5,000.
const healthPayoutEstimate = computed(() => {
  const expenses = parseFloat(localHealth.expensesAmount);
  if (isNaN(expenses) || expenses <= 100) return 0;
  const payout = (expenses - 100) * 0.8;
  return Math.min(payout, 5000);
});

// Motor Estimate
const motorCoveragePercentage = computed(() => {
  switch (localMotor.damageSeverity) {
    case 'minor': return 90;
    case 'moderate': return 80;
    case 'severe': return 70;
    default: return 0;
  }
});

const motorPayoutEstimate = computed(() => {
  const estimate = parseFloat(localMotor.repairEstimate);
  if (isNaN(estimate) || estimate <= 250) return 0;
  
  const percentage = motorCoveragePercentage.value / 100;
  const payout = (estimate - 250) * percentage;
  
  // Cap based on severity
  let cap = 0;
  if (localMotor.damageSeverity === 'minor') cap = 1500;
  else if (localMotor.damageSeverity === 'moderate') cap = 5000;
  else if (localMotor.damageSeverity === 'severe') cap = 15000;

  return Math.min(payout, cap);
});

// General Estimate
const generalCoveragePercentage = computed(() => {
  switch (localGeneral.lossType) {
    case 'theft': return 100;
    case 'water_damage': return 80;
    case 'accidental_loss': return 50;
    default: return 0;
  }
});

const generalPayoutEstimate = computed(() => {
  const value = parseFloat(localGeneral.estimatedValue);
  if (isNaN(value) || value <= 50) return 0;
  
  const percentage = generalCoveragePercentage.value / 100;
  const payout = (value - 50) * percentage;
  
  // Cap based on loss type
  let cap = 0;
  if (localGeneral.lossType === 'theft') cap = 1000;
  else if (localGeneral.lossType === 'water_damage') cap = 800;
  else if (localGeneral.lossType === 'accidental_loss') cap = 500;

  return Math.min(payout, cap);
});

// --- Simulated Drag & Drop file upload handler ---
const isDragging = ref(false);
const fileInput = ref(null);

const triggerFileInput = () => {
  fileInput.value.click();
};

const processFiles = (files) => {
  for (let i = 0; i < files.length; i++) {
    const file = files[i];
    // Limit to 5MB
    if (file.size > 5 * 1024 * 1024) {
      alert(`File "${file.name}" exceeds the 5MB size limit.`);
      continue;
    }
    
    // Add file simulator structure (can't save real File objects in JSON/LocalStorage easily)
    localDocs.value.push({
      name: file.name,
      size: file.size,
      type: file.type,
      lastModified: file.lastModified
    });
  }
};

const handleDrop = (e) => {
  isDragging.value = false;
  const files = e.dataTransfer.files;
  processFiles(files);
};

const handleFileSelect = (e) => {
  const files = e.target.files;
  processFiles(files);
};

const removeFile = (index) => {
  localDocs.value.splice(index, 1);
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

.form-section {
  position: relative;
  border: 1px solid var(--color-border);
  background-color: var(--color-background-soft);
  border-radius: 12px;
  padding: 2rem 1.5rem 1.5rem 1.5rem;
  margin-bottom: 2rem;
}

.section-badge {
  position: absolute;
  top: -12px;
  left: 20px;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
  font-weight: bold;
  border-radius: 20px;
  color: #ffffff;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.health-badge { background-color: #319795; }
.motor-badge { background-color: #dd6b20; }
.general-badge { background-color: #805ad5; }

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem 1.5rem;
}

@media (min-width: 640px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.full-width {
  grid-column: span 1;
}

@media (min-width: 640px) {
  .full-width {
    grid-column: span 2;
  }
}

.input-label {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--color-heading);
}

.required-star {
  color: #e53e3e;
}

.base-textarea {
  width: 100%;
  height: 120px;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
  font-family: inherit;
  resize: vertical;
}

.base-textarea:focus {
  border-color: #3182ce;
  box-shadow: 0 0 0 3px rgba(49, 130, 206, 0.25);
}

.base-textarea.has-error {
  border-color: #e53e3e;
}

.error-message {
  color: #e53e3e;
  font-size: 0.8rem;
  margin-top: 0.35rem;
  font-weight: 500;
}

/* Estimator styles */
.estimator-card {
  margin-top: 1.5rem;
  border-radius: 10px;
  padding: 1.25rem;
  background-color: var(--color-background);
  border-left: 4px solid;
}

.health-estimator { border-color: #319795; }
.motor-estimator { border-color: #dd6b20; }
.general-estimator { border-color: #805ad5; }

.estimator-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.estimator-header h4 {
  font-weight: bold;
  font-size: 1rem;
  color: var(--color-heading);
}

.estimator-body {
  font-size: 0.9rem;
}

.estimator-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.4rem;
}

.estimator-divider {
  border: 0;
  border-top: 1px dashed var(--color-border);
  margin: 0.75rem 0;
}

.final-row {
  font-size: 1.1rem;
  font-weight: bold;
  color: var(--color-heading);
}

.payout-amount {
  color: #2f855a;
  font-size: 1.25rem;
}

.estimator-note {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.65;
  margin-top: 0.75rem;
  font-style: italic;
}

/* File Dropzone styles */
.docs-section {
  margin-top: 2rem;
}

.dropzone {
  border: 2px dashed var(--color-border);
  border-radius: 10px;
  padding: 2rem;
  text-align: center;
  background-color: var(--color-background-soft);
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropzone:hover, .dropzone.dragging {
  border-color: #3182ce;
  background-color: rgba(49, 130, 206, 0.05);
}

.hidden-file-input {
  display: none;
}

.upload-icon {
  font-size: 2rem;
  display: block;
  margin-bottom: 0.5rem;
}

.upload-text {
  font-size: 0.95rem;
  color: var(--color-text);
}

.browse-link {
  color: #3182ce;
  font-weight: bold;
  text-decoration: underline;
}

.upload-subtext {
  font-size: 0.8rem;
  opacity: 0.7;
  margin-top: 0.25rem;
}

.file-list {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1rem;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.file-icon {
  font-size: 1.5rem;
}

.file-meta {
  display: flex;
  flex-direction: column;
}

.file-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-heading);
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 0.75rem;
  opacity: 0.7;
}

.delete-file-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #e53e3e;
  cursor: pointer;
  padding: 0 0.5rem;
  line-height: 1;
}

.delete-file-btn:hover {
  font-weight: bold;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
