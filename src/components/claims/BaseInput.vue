<template>
  <div class="input-group">
    <label v-if="label" class="input-label">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>
    
    <input
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      class="base-input"
      :class="{ 'has-error': error }"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    
    <span v-if="error" class="error-message">{{ error }}</span>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text'
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

.base-input {
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-text);
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
  font-family: inherit;
}

.base-input:focus {
  border-color: #3182ce;
  box-shadow: 0 0 0 3px rgba(49, 130, 206, 0.25);
}

.base-input:disabled {
  background-color: var(--color-background-soft);
  cursor: not-allowed;
  opacity: 0.7;
}

.base-input.has-error {
  border-color: #e53e3e;
}

.base-input.has-error:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.25);
}

.error-message {
  color: #e53e3e;
  font-size: 0.8rem;
  margin-top: 0.35rem;
  font-weight: 500;
}
</style>
