<template>
  <div class="step-content">
    <h2 class="step-title">Policy & Claimant Information</h2>
    <p class="step-description">Please enter your policy details and claimant contact information to begin your claim.</p>

    <div class="form-grid">
      <!-- Policy Number -->
      <BaseInput
        v-model="localState.policyNumber"
        label="Policy Number"
        placeholder="e.g., POL-123456"
        required
        :error="errors.policyNumber"
      />

      <!-- Claim Type -->
      <BaseSelect
        v-model="localState.claimType"
        label="Claim Type"
        :options="claimTypeOptions"
        placeholder="Select the type of claim"
        required
        :error="errors.claimType"
      />

      <!-- Claimant Name -->
      <BaseInput
        v-model="localState.claimantName"
        label="Claimant Full Name"
        placeholder="e.g., Jane Doe"
        required
        :error="errors.claimantName"
      />

      <!-- Claimant Email -->
      <BaseInput
        v-model="localState.claimantEmail"
        label="Claimant Email Address"
        type="email"
        placeholder="e.g., jane.doe@example.com"
        required
        :error="errors.claimantEmail"
      />

      <!-- Claimant Phone -->
      <BaseInput
        v-model="localState.claimantPhone"
        label="Claimant Phone Number"
        type="tel"
        placeholder="e.g., +1 234-567-8900"
        required
        :error="errors.claimantPhone"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, watch, ref } from 'vue';
import BaseInput from './BaseInput.vue';
import BaseSelect from './BaseSelect.vue';

const props = defineProps({
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

// Local reactive state to avoid mutating props directly
const localState = reactive({
  policyNumber: props.modelValue.policyNumber || '',
  claimType: props.modelValue.claimType || '',
  claimantName: props.modelValue.claimantName || '',
  claimantEmail: props.modelValue.claimantEmail || '',
  claimantPhone: props.modelValue.claimantPhone || ''
});

// Watch local state and emit updates to parent
watch(
  () => ({ ...localState }),
  (newValue) => {
    emit('update:modelValue', { ...props.modelValue, ...newValue });
  },
  { deep: true }
);

// Watch prop to update local state if updated from parent (e.g., during draft recovery)
watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal) {
      localState.policyNumber = newVal.policyNumber || '';
      localState.claimType = newVal.claimType || '';
      localState.claimantName = newVal.claimantName || '';
      localState.claimantEmail = newVal.claimantEmail || '';
      localState.claimantPhone = newVal.claimantPhone || '';
    }
  },
  { deep: true }
);

const claimTypeOptions = [
  { value: 'health', label: 'Health / Medical Claim' },
  { value: 'motor', label: 'Motor Vehicle Claim' },
  { value: 'general', label: 'General / Property Claim' }
];
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

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem 1.5rem;
}

@media (min-width: 640px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  /* Make Claim Type full width or span across columns */
  .form-grid > :nth-child(2) {
    grid-column: span 1;
  }
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
