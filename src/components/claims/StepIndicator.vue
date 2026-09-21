<template>
  <div class="step-indicator-container">
    <div class="steps-progress-bar">
      <div 
        class="progress-line" 
        :style="{ width: progressPercentage + '%' }"
      ></div>
    </div>
    
    <div class="steps-list">
      <div 
        v-for="(step, index) in steps" 
        :key="index" 
        class="step-item"
        :class="{ 
          'active': index === currentStep,
          'completed': index < currentStep 
        }"
      >
        <div class="step-bubble">
          <span v-if="index < currentStep" class="check-mark">✓</span>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <span class="step-label">{{ step }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  steps: {
    type: Array,
    required: true
  },
  currentStep: {
    type: Number,
    required: true
  }
});

const progressPercentage = computed(() => {
  if (props.steps.length <= 1) return 0;
  return (props.currentStep / (props.steps.length - 1)) * 100;
});
</script>

<style scoped>
.step-indicator-container {
  position: relative;
  margin-bottom: 2.5rem;
  width: 100%;
}

.steps-progress-bar {
  position: absolute;
  top: 1.5rem;
  left: 10%;
  right: 10%;
  height: 4px;
  background-color: var(--color-border);
  z-index: 1;
  border-radius: 2px;
}

.progress-line {
  height: 100%;
  background-color: #3182ce;
  transition: width 0.4s ease;
  border-radius: 2px;
}

.steps-list {
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 2;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.step-bubble {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background-color: var(--color-background-soft);
  border: 3px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: var(--color-text);
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.step-label {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text);
  text-align: center;
  transition: color 0.3s ease;
}

/* Completed Step styles */
.step-item.completed .step-bubble {
  background-color: #3182ce;
  border-color: #3182ce;
  color: #ffffff;
  transform: scale(1.05);
}

.step-item.completed .step-label {
  color: #3182ce;
}

.check-mark {
  font-size: 1.2rem;
}

/* Active Step styles */
.step-item.active .step-bubble {
  background-color: var(--color-background);
  border-color: #3182ce;
  color: #3182ce;
  box-shadow: 0 0 0 5px rgba(49, 130, 206, 0.15);
  transform: scale(1.1);
}

.step-item.active .step-label {
  color: #3182ce;
  font-weight: 700;
}

@media (max-width: 500px) {
  .step-label {
    display: none; /* Hide labels on very small mobile to prevent text collision */
  }
  .steps-progress-bar {
    left: 15%;
    right: 15%;
  }
}
</style>
