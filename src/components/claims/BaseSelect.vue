<template>
  <div class="input-group">
    <label v-if="label" class="input-label">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>
    
    <select
      :value="modelValue"
      :disabled="disabled"
      class="base-select"
      :class="{ 'has-error': error }"
      @change="$emit('update:modelValue', $event.target.value)"
    >
      <option v-if="placeholder" value="" disabled selected>{{ placeholder }}</option>
      <option 
        v-for="option in normalizedOptions" 
        :key="option.value" 
        :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>
    
    <span v-if="error" class="error-message">{{ error }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  options: {
    type: Array,
    required: true,
    default: () => []
  },
  placeholder: {
    type: String,
    default: ''
  },
  required: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  }
});

defineEmits(['update:modelValue']);

// Normalize options to always be an array of { value, label }
const normalizedOptions = computed(() => {
  return props.options.map(option => {
    if (typeof option === 'object' && option !== null) {
      return {
        value: option.value !== undefined ? option.value : option.label,
        label: option.label || option.value
      };
    }
    return { value: option, label: option };
  });
});
</script>

<style scoped>
.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.25rem;
  width: 100%;
}

.input-label {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--color-heading);
  display: flex;
  align-items: center;
}

.required-star {
  color: #e53e3e;
  margin-left: 0.25rem;
}

.base-select {
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
  font-family: inherit;
  appearance: none; /* remove default arrow */
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%234a5568' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><polyline points='6 9 12 15 18 9'></polyline></svg>");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1.2rem;
  padding-right: 2.5rem;
}

.base-select:focus {
  border-color: #3182ce;
  box-shadow: 0 0 0 3px rgba(49, 130, 206, 0.25);
}

.base-select:disabled {
  background-color: var(--color-background-soft);
  cursor: not-allowed;
  opacity: 0.7;
}

.base-select.has-error {
  border-color: #e53e3e;
}

.base-select.has-error:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.25);
}

.error-message {
  color: #e53e3e;
  font-size: 0.8rem;
  margin-top: 0.35rem;
  font-weight: 500;
}
</style>
