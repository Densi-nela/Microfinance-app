<template>
  <div class="wizard-container">
    <!-- View Switcher Tabs (New Claim vs History) -->
    <div class="view-tabs">
      <button 
        class="view-tab-btn" 
        :class="{ 'active': activeView === 'form' }"
        @click="activeView = 'form'"
      >
        📝 File a Claim
      </button>
      <button 
        class="view-tab-btn" 
        :class="{ 'active': activeView === 'history' }"
        @click="activeView = 'history'"
      >
        📋 View Claims ({{ submittedClaimsList.length }})
      </button>
    </div>

    <!-- Active View: Claim Form -->
    <div v-if="activeView === 'form'" class="form-view-container">
      <!-- Draft Alert Bar -->
      <div v-if="isDraftRestored" class="draft-alert">
        <span class="draft-alert-text">💡 We found and restored a previously saved draft claim.</span>
        <button class="clear-draft-btn" @click="clearDraft">Discard Draft</button>
      </div>

      <!-- Step Indicator -->
      <StepIndicator 
        v-if="!submissionSuccess"
        :steps="steps" 
        :currentStep="currentStep" 
      />

      <!-- Step Content Area -->
      <div class="step-card">
        <!-- Step 1: Policy Info -->
        <StepPolicyInfo
          v-if="currentStep === 0"
          v-model="formData"
          :errors="errors"
        />

        <!-- Step 2: Claim Details -->
        <StepClaimDetails
          v-else-if="currentStep === 1"
          v-model="formData"
          :claimType="formData.claimType"
          :errors="errors"
        />

        <!-- Step 3: Review & Submit -->
        <StepReviewSubmit
          v-else-if="currentStep === 2"
          :formData="formData"
          :isSubmitting="isSubmitting"
          :submissionSuccess="submissionSuccess"
          :claimReference="claimReference"
          @submit-claim="submitClaim"
          @view-history="activeView = 'history'"
          @reset-form="resetForm"
        >
          <!-- Controlled Navigation Buttons Slot inside StepReviewSubmit -->
          <template #navigation-buttons="{ termsAgreed, triggerSubmit }">
            <div class="navigation-actions">
              <button 
                class="btn-back" 
                @click="prevStep"
                :disabled="isSubmitting"
              >
                ← Back to Details
              </button>
              <button 
                class="btn-submit-final" 
                :disabled="!termsAgreed || isSubmitting"
                @click="triggerSubmit"
              >
                🚀 Confirm & Submit Claim
              </button>
            </div>
          </template>
        </StepReviewSubmit>
      </div>

      <!-- General Navigation buttons for Steps 1 and 2 -->
      <div v-if="currentStep < 2" class="navigation-actions mt-lg">
        <button 
          class="btn-back" 
          @click="prevStep" 
          :disabled="currentStep === 0"
        >
          Back
        </button>
        <button 
          class="btn-next" 
          @click="nextStep"
        >
          Next Step →
        </button>
      </div>
    </div>

    <!-- Active View: Claims History -->
    <div v-else-if="activeView === 'history'">
      <ClaimHistory 
        :claims="submittedClaimsList"
        @start-claim="activeView = 'form'"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue';
import StepIndicator from './StepIndicator.vue';
import StepPolicyInfo from './StepPolicyInfo.vue';
import StepClaimDetails from './StepClaimDetails.vue';
import StepReviewSubmit from './StepReviewSubmit.vue';
import ClaimHistory from './ClaimHistory.vue';

// Views state
const activeView = ref('form');
const currentStep = ref(0);
const steps = ['Policy Info', 'Claim Details', 'Review & Submit'];

// Master Form State
const formData = reactive({
  policyNumber: '',
  claimType: '',
  claimantName: '',
  claimantEmail: '',
  claimantPhone: '',
  // Health claims specific fields
  healthDetails: {
    hospitalName: '',
    treatmentDate: '',
    expensesAmount: '',
    medicalDiagnosis: ''
  },
  // Motor claims specific fields
  motorDetails: {
    vehicleRegNo: '',
    accidentDate: '',
    accidentLocation: '',
    damageSeverity: '',
    policeReportFiled: '',
    repairEstimate: ''
  },
  // General claims specific fields
  generalDetails: {
    itemName: '',
    purchaseDate: '',
    lossType: '',
    estimatedValue: '',
    incidentDescription: ''
  },
  // Supporting attachments
  supportingDocuments: [],
  // Calculated Estimates
  estimates: {
    health: 0,
    motor: 0,
    general: 0
  }
});

// Validation Error State
const errors = reactive({});

// Draft saving state
const isDraftRestored = ref(false);

// Submission Simulation States
const isSubmitting = ref(false);
const submissionSuccess = ref(false);
const claimReference = ref('');
const submittedClaimsList = ref([]);

// Life cycle & local storage loader
onMounted(() => {
  loadSubmittedClaims();
  loadDraft();
});

// Dynamic API Base URL detection
const API_BASE = import.meta.env.PROD 
  ? 'https://shieldflow-portal.onrender.com' // Replace with your actual Render URL!
  : 'http://localhost:8080';

const loadSubmittedClaims = async () => {
  try {
    const res = await fetch(`${API_BASE}/api/claims`);
    if (!res.ok) throw new Error('API failed');
    const data = await res.json();
    submittedClaimsList.value = data;
    console.log('Successfully loaded claims from live Spring Boot backend!');
  } catch (error) {
    console.warn('Backend server down, falling back to LocalStorage simulation.', error);
    const claimsJson = localStorage.getItem('submitted_claims');
    if (claimsJson) {
      try {
        submittedClaimsList.value = JSON.parse(claimsJson);
      } catch (e) {
        console.error('Failed to parse submitted claims from localStorage', e);
        submittedClaimsList.value = [];
      }
    } else {
      // Inject mock historical claims if empty, to make the app feel alive and complete!
      const mockClaims = [
        {
          id: 'mock-1',
          reference: 'CLM-591048-H',
          policyNumber: 'POL-992014',
          claimType: 'health',
          claimantName: 'Sarah Jenkins',
          claimantEmail: 'sarah.j@example.com',
          claimantPhone: '+1 (555) 381-0021',
          submittedAt: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(), // 30 days ago
          status: 'paid',
          healthDetails: {
            hospitalName: 'Mercy Medical Center',
            treatmentDate: '2026-08-15',
            expensesAmount: '850.00',
            medicalDiagnosis: 'Outpatient surgery for minor wrist tendon repair, post-op physiotherapy.'
          },
          estimates: { health: 600.00 },
          supportingDocuments: [{ name: 'medical_bill_850.pdf', size: 245100 }]
        },
        {
          id: 'mock-2',
          reference: 'CLM-382914-M',
          policyNumber: 'POL-104928',
          claimType: 'motor',
          claimantName: 'John Doe',
          claimantEmail: 'john.doe@example.com',
          claimantPhone: '+1 (555) 902-1481',
          submittedAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(), // 5 days ago
          status: 'under_review',
          motorDetails: {
            vehicleRegNo: '7XYZ89',
            accidentDate: '2026-09-15',
            accidentLocation: 'Cross Street Junction, NY',
            damageSeverity: 'moderate',
            policeReportFiled: 'yes',
            repairEstimate: '2400.00'
          },
          estimates: { motor: 1720.00 },
          supportingDocuments: [{ name: 'accident_photo_front.jpg', size: 1250000 }, { name: 'police_report.pdf', size: 540000 }]
        }
      ];
      submittedClaimsList.value = mockClaims;
      localStorage.setItem('submitted_claims', JSON.stringify(mockClaims));
    }
  }
};

const loadDraft = () => {
  const draftJson = localStorage.getItem('claim_form_draft');
  if (draftJson) {
    try {
      const draft = JSON.parse(draftJson);
      // Deep merge draft into reactive formData
      Object.assign(formData, draft.data);
      currentStep.value = draft.step || 0;
      isDraftRestored.value = true;
      // Clear alert after 8 seconds
      setTimeout(() => {
        isDraftRestored.value = false;
      }, 8000);
    } catch (e) {
      console.error('Failed to restore draft from localStorage', e);
    }
  }
};

// Auto-save draft on data changes
watch(
  () => ({ step: currentStep.value, data: { ...formData } }),
  (state) => {
    // Only save draft if claim is not yet submitted successfully
    if (!submissionSuccess.value) {
      localStorage.setItem('claim_form_draft', JSON.stringify({
        step: state.step,
        data: state.data,
        updatedAt: Date.now()
      }));
    }
  },
  { deep: true }
);

const clearDraft = () => {
  localStorage.removeItem('claim_form_draft');
  isDraftRestored.value = false;
  resetForm();
};

// Validation Logic per step
const validateStep = (stepNumber) => {
  // Clear previous errors
  for (const key in errors) {
    delete errors[key];
  }

  let isValid = true;

  if (stepNumber === 0) {
    // Step 1 validations
    if (!formData.policyNumber) {
      errors.policyNumber = 'Policy number is required.';
      isValid = false;
    } else if (!/^POL-\d{6}$/.test(formData.policyNumber)) {
      errors.policyNumber = 'Invalid format. Must match POL-XXXXXX (e.g., POL-123456).';
      isValid = false;
    }

    if (!formData.claimType) {
      errors.claimType = 'Claim type selection is required.';
      isValid = false;
    }

    if (!formData.claimantName) {
      errors.claimantName = 'Claimant full name is required.';
      isValid = false;
    } else if (formData.claimantName.trim().length < 3) {
      errors.claimantName = 'Name must be at least 3 characters.';
      isValid = false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.claimantEmail) {
      errors.claimantEmail = 'Email address is required.';
      isValid = false;
    } else if (!emailRegex.test(formData.claimantEmail)) {
      errors.claimantEmail = 'Please enter a valid email address.';
      isValid = false;
    }

    if (!formData.claimantPhone) {
      errors.claimantPhone = 'Contact phone number is required.';
      isValid = false;
    }
  } 
  else if (stepNumber === 1) {
    // Step 2 validations (Conditional on claim type)
    if (formData.claimType === 'health') {
      const h = formData.healthDetails;
      if (!h.hospitalName) {
        errors.hospitalName = 'Hospital / Clinic name is required.';
        isValid = false;
      }
      if (!h.treatmentDate) {
        errors.treatmentDate = 'Treatment date is required.';
        isValid = false;
      }
      
      const exp = parseFloat(h.expensesAmount);
      if (!h.expensesAmount) {
        errors.expensesAmount = 'Total medical expenses are required.';
        isValid = false;
      } else if (isNaN(exp) || exp <= 0) {
        errors.expensesAmount = 'Expenses must be a positive number.';
        isValid = false;
      }

      if (!h.medicalDiagnosis || h.medicalDiagnosis.trim().length < 10) {
        errors.medicalDiagnosis = 'Please provide a medical diagnosis description (min 10 characters).';
        isValid = false;
      }
    } 
    else if (formData.claimType === 'motor') {
      const m = formData.motorDetails;
      if (!m.vehicleRegNo) {
        errors.vehicleRegNo = 'Vehicle registration plate number is required.';
        isValid = false;
      }
      if (!m.accidentDate) {
        errors.accidentDate = 'Date of incident is required.';
        isValid = false;
      }
      if (!m.accidentLocation) {
        errors.accidentLocation = 'Incident location is required.';
        isValid = false;
      }
      if (!m.damageSeverity) {
        errors.damageSeverity = 'Please select damage severity.';
        isValid = false;
      }
      if (!m.policeReportFiled) {
        errors.policeReportFiled = 'Please specify if a police report was filed.';
        isValid = false;
      }

      const est = parseFloat(m.repairEstimate);
      if (!m.repairEstimate) {
        errors.repairEstimate = 'Repair estimate cost is required.';
        isValid = false;
      } else if (isNaN(est) || est <= 0) {
        errors.repairEstimate = 'Estimate must be a positive number.';
        isValid = false;
      }
    } 
    else if (formData.claimType === 'general') {
      const g = formData.generalDetails;
      if (!g.itemName) {
        errors.itemName = 'Item description is required.';
        isValid = false;
      }
      if (!g.purchaseDate) {
        errors.purchaseDate = 'Approximate purchase date is required.';
        isValid = false;
      }
      if (!g.lossType) {
        errors.lossType = 'Please select type of loss.';
        isValid = false;
      }

      const val = parseFloat(g.estimatedValue);
      if (!g.estimatedValue) {
        errors.estimatedValue = 'Estimated item value is required.';
        isValid = false;
      } else if (isNaN(val) || val <= 0) {
        errors.estimatedValue = 'Value must be a positive number.';
        isValid = false;
      }

      if (!g.incidentDescription || g.incidentDescription.trim().length < 15) {
        errors.incidentDescription = 'Please describe the incident details (min 15 characters).';
        isValid = false;
      }
    }
  }

  return isValid;
};

// Navigation Handlers
const nextStep = () => {
  if (validateStep(currentStep.value)) {
    currentStep.value++;
  } else {
    // Scroll to the first error input smoothly
    setTimeout(() => {
      const firstError = document.querySelector('.error-message');
      if (firstError) {
        firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 50);
  }
};

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
};

// Simulated/Live submission
const submitClaim = async () => {
  isSubmitting.value = true;
  
  const payload = {
    policyNumber: formData.policyNumber,
    claimType: formData.claimType,
    claimantName: formData.claimantName,
    claimantEmail: formData.claimantEmail,
    claimantPhone: formData.claimantPhone,
    healthDetails: formData.healthDetails,
    motorDetails: formData.motorDetails,
    generalDetails: formData.generalDetails,
    supportingDocuments: formData.supportingDocuments,
    estimates: formData.estimates
  };

  try {
    const res = await fetch('http://localhost:8080/api/claims', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });
    
    if (!res.ok) throw new Error('API post failed');
    const savedClaim = await res.json();
    
    // Delay slightly to let the gorgeous StepReviewSubmit timeline simulation finish
    setTimeout(() => {
      isSubmitting.value = false;
      submissionSuccess.value = true;
      claimReference.value = savedClaim.reference;

      // Add to list and clear draft
      submittedClaimsList.value.unshift(savedClaim);
      localStorage.removeItem('claim_form_draft');
      console.log('Successfully saved new claim to live Spring Boot database!', savedClaim);
    }, 3600);
  } catch (error) {
    console.warn('Backend server down during submission, falling back to LocalStorage simulation.', error);
    
    // Fallback simulated submission logic
    const randNum = Math.floor(100000 + Math.random() * 900000);
    const claimSuffixChar = formData.claimType.charAt(0).toUpperCase();
    const refNum = `CLM-${randNum}-${claimSuffixChar}`;
    
    setTimeout(() => {
      isSubmitting.value = false;
      submissionSuccess.value = true;
      claimReference.value = refNum;

      const newClaim = {
        id: 'claim-' + Date.now(),
        reference: refNum,
        policyNumber: formData.policyNumber,
        claimType: formData.claimType,
        claimantName: formData.claimantName,
        claimantEmail: formData.claimantEmail,
        claimantPhone: formData.claimantPhone,
        submittedAt: new Date().toISOString(),
        status: 'pending_review',
        healthDetails: { ...formData.healthDetails },
        motorDetails: { ...formData.motorDetails },
        generalDetails: { ...formData.generalDetails },
        supportingDocuments: [...formData.supportingDocuments],
        estimates: { ...formData.estimates }
      };

      submittedClaimsList.value.unshift(newClaim);
      localStorage.setItem('submitted_claims', JSON.stringify(submittedClaimsList.value));
      localStorage.removeItem('claim_form_draft');
    }, 3600);
  }
};

const resetForm = () => {
  currentStep.value = 0;
  submissionSuccess.value = false;
  claimReference.value = '';
  
  // Reset reactive formData properties
  formData.policyNumber = '';
  formData.claimType = '';
  formData.claimantName = '';
  formData.claimantEmail = '';
  formData.claimantPhone = '';
  formData.healthDetails = { hospitalName: '', treatmentDate: '', expensesAmount: '', medicalDiagnosis: '' };
  formData.motorDetails = { vehicleRegNo: '', accidentDate: '', accidentLocation: '', damageSeverity: '', policeReportFiled: '', repairEstimate: '' };
  formData.generalDetails = { itemName: '', purchaseDate: '', lossType: '', estimatedValue: '', incidentDescription: '' };
  formData.supportingDocuments = [];
  formData.estimates = { health: 0, motor: 0, general: 0 };

  // Clear errors
  for (const key in errors) {
    delete errors[key];
  }
};
</script>

<style scoped>
.wizard-container {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
}

.view-tabs {
  display: flex;
  background-color: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 0.35rem;
  margin-bottom: 2rem;
  width: 100%;
}

.view-tab-btn {
  flex: 1;
  text-align: center;
  padding: 0.6rem 1rem;
  font-size: 0.95rem;
  font-weight: 600;
  border: none;
  background-color: transparent;
  color: var(--color-text);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.view-tab-btn:hover {
  background-color: rgba(49, 130, 206, 0.05);
}

.view-tab-btn.active {
  background-color: #3182ce;
  color: #ffffff;
}

/* Draft alert styling */
.draft-alert {
  background-color: #ebf8ff;
  border: 1px solid #bee3f8;
  color: #2b6cb0;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  margin-bottom: 1.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: fadeIn 0.3s ease-out;
}

.clear-draft-btn {
  background-color: transparent;
  border: 1px solid #2b6cb0;
  color: #2b6cb0;
  padding: 0.2rem 0.6rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-draft-btn:hover {
  background-color: #2b6cb0;
  color: #ffffff;
}

/* Step container Card */
.step-card {
  background-color: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 1.75rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

/* Navigation actions styling */
.navigation-actions {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
  width: 100%;
}

.mt-lg {
  margin-top: 1.5rem;
}

.btn-back {
  background-color: transparent;
  color: var(--color-text);
  border: 1px solid var(--color-border);
  padding: 0.7rem 1.25rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover:not(:disabled) {
  background-color: var(--color-background-soft);
  border-color: var(--color-border-hover);
}

.btn-back:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-next {
  background-color: #3182ce;
  color: #ffffff;
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-next:hover {
  background-color: #2b6cb0;
}

/* Review Submit custom navigation button inside StepReviewSubmit slot */
.btn-submit-final {
  background-color: #2f855a;
  color: #ffffff;
  border: none;
  padding: 0.75rem 1.75rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-submit-final:hover:not(:disabled) {
  background-color: #22543d;
}

.btn-submit-final:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
