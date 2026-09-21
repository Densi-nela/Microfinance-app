<template>
  <div class="portal-layout">
    <!-- Branded Header -->
    <header class="portal-header">
      <div class="header-content">
        <div class="brand">
          <span class="brand-logo">🛡️</span>
          <div class="brand-text">
            <h1 class="brand-name">ShieldFlow</h1>
            <span class="brand-tagline">Micro-Insurance Portal</span>
          </div>
        </div>
        
        <div class="user-profile">
          <div class="profile-avatar">JD</div>
          <div class="profile-info">
            <span class="profile-name">John Doe</span>
            <span class="profile-role">Premium Member</span>
          </div>
        </div>
      </div>
    </header>

    <!-- Subheader Portal Metrics -->
    <section class="portal-metrics-bar">
      <div class="metrics-content">
        <div class="metric-card">
          <span class="metric-icon">📜</span>
          <div class="metric-details">
            <span class="metric-label">Active Policies</span>
            <strong class="metric-value">3</strong>
          </div>
        </div>
        <div class="metric-card">
          <span class="metric-icon">⏳</span>
          <div class="metric-details">
            <span class="metric-label">Claims Processed</span>
            <strong class="metric-value">2</strong>
          </div>
        </div>
        <div class="metric-card">
          <span class="metric-icon">💰</span>
          <div class="metric-details">
            <span class="metric-label">Total Payouts</span>
            <strong class="metric-value">$600.00</strong>
          </div>
        </div>
      </div>
    </section>

    <!-- Main Content Area -->
    <main class="portal-main">
      <div class="container">
        <!-- Claim Form Wizard Container -->
        <ClaimFormWizard />
      </div>
    </main>

    <!-- Professional Portal Footer -->
    <footer class="portal-footer">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-logo">🛡️ ShieldFlow Insurance</span>
          <p class="footer-copy">© 2026 ShieldFlow Technologies. All rights reserved.</p>
        </div>
        <div class="footer-links">
          <div class="footer-link-group">
            <h5>Resources</h5>
            <a href="#">Policy Handbook</a>
            <a href="#">Claims FAQ</a>
            <a href="#">Coverage Calculator</a>
          </div>
          <div class="footer-link-group">
            <h5>Support</h5>
            <a href="#">24/7 Hotline</a>
            <a href="#">Submit Ticket</a>
            <a href="#">Help Center</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import ClaimFormWizard from './components/claims/ClaimFormWizard.vue';
</script>

<style>
/* Global resets & vars inside App.vue */
:root {
  --portal-primary: #3182ce;
  --portal-success: #38a169;
  --portal-dark: #1a202c;
  --portal-light-bg: #f7fafc;
}

body {
  background-color: #f7fafc !important; /* Force nice light grey background for portal feel */
}

/* Base App Layout styles */
.portal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.portal-header {
  background-color: #ffffff;
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1024px;
  margin: 0 auto;
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.brand-logo {
  font-size: 2.25rem;
  line-height: 1;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 1.3rem;
  font-weight: 800;
  color: var(--vt-c-indigo);
  margin: 0;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.7;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.profile-avatar {
  width: 2.5rem;
  height: 2.5rem;
  background-color: var(--portal-primary);
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.95rem;
}

.profile-info {
  display: none;
}

@media (min-width: 640px) {
  .profile-info {
    display: flex;
    flex-direction: column;
  }
}

.profile-name {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--color-heading);
}

.profile-role {
  font-size: 0.75rem;
  opacity: 0.7;
}

/* Metrics bar styles */
.portal-metrics-bar {
  background-color: #ebf8ff;
  border-bottom: 1px solid rgba(49, 130, 206, 0.15);
}

.metrics-content {
  max-width: 1024px;
  margin: 0 auto;
  padding: 0.75rem 1.5rem;
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  justify-content: space-around;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.metric-icon {
  font-size: 1.5rem;
}

.metric-details {
  display: flex;
  flex-direction: column;
}

.metric-label {
  font-size: 0.75rem;
  color: #2b6cb0;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.metric-value {
  font-size: 1.1rem;
  color: #2c5282;
  font-weight: 800;
}

/* Main Content styling */
.portal-main {
  flex: 1;
  padding: 2.5rem 1.5rem;
}

.container {
  max-width: 1024px;
  margin: 0 auto;
}

/* Footer styling */
.portal-footer {
  background-color: #1a202c;
  color: #cbd5e0;
  padding: 3rem 1.5rem;
  margin-top: auto;
  border-top: 1px solid #2d3748;
}

.footer-content {
  max-width: 1024px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  justify-content: space-between;
}

@media (min-width: 640px) {
  .footer-content {
    flex-direction: row;
    align-items: flex-start;
  }
}

.footer-brand {
  max-width: 300px;
}

.footer-logo {
  font-size: 1.1rem;
  font-weight: bold;
  color: #ffffff;
  display: block;
  margin-bottom: 0.5rem;
}

.footer-copy {
  font-size: 0.8rem;
  opacity: 0.7;
}

.footer-links {
  display: flex;
  gap: 3rem;
}

.footer-link-group h5 {
  font-size: 0.85rem;
  font-weight: bold;
  color: #ffffff;
  text-transform: uppercase;
  margin-bottom: 0.75rem;
  letter-spacing: 0.05em;
}

.footer-link-group a {
  display: block;
  color: #a0aec0;
  font-size: 0.85rem;
  margin-bottom: 0.4rem;
  text-decoration: none;
  padding: 0;
  transition: color 0.2s;
}

.footer-link-group a:hover {
  color: #ffffff;
  background-color: transparent;
}
</style>
