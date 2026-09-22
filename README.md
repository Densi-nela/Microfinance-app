# 🛡️ ShieldFlow Micro-Insurance Portal

[![Live Demo](https://img.shields.io/badge/demo-live-brightgreen?style=for-the-badge)](https://microfinance-app-1.onrender.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4fc08d?style=for-the-badge&logo=vuedotjs)](https://vuejs.org)

**ShieldFlow** is a modern, responsive, and resilient full-stack micro-insurance claims portal. It streamlines the filing, valuation, and tracking of insurance claims for **Health**, **Motor**, and **General Property** policies.

👉 **Access the Live Cloud Deployment**: **[https://microfinance-app-1.onrender.com](https://microfinance-app-1.onrender.com)**

---

## ⚡ Features

*   **Intelligent Claim Stepper Wizard (Vue 3)**: A seamless 3-step form wizard that prevents input overload by grouping data logically (Policy Info, Dynamic Specifications, Review & Submit).
*   **Dynamic Sub-Form Engine**: The portal dynamically swaps input forms depending on your selected claim type, capturing specific parameters (e.g. Hospital & Doctor names for Health, Vehicle Plates & Severity for Motor).
*   **Live Payout Estimators**: Computes claims using active rules, deductibles, percentage coverages, and severity-specific caps in real-time as you type.
*   **Offline-Resilient Syncer**: If the backend goes offline, the frontend auto-saves progress locally (using LocalStorage) and runs on local simulations, ensuring the application remains 100% operational.
*   **Transactional Kotlin Spring Boot API**: A robust Java 11 / Spring Boot 2.7.18 REST API that handles transactional routing, validating inputs, and generating immutable claim records.
*   **Polymorphic DB Storage**: Merges core SQL indices with schema-less dynamic JSONB properties inside a physical H2 database on disk.
*   **Claims Dashboard & Timeline Tracking**: Lets users search and filter past claims. Expanding cards reveal detailed claim attributes and track real-time underwriting milestones (*Submitted ➔ Under Review ➔ Approved ➔ Paid Out*).

---

## 🏗️ System Architecture

```text
[ Vue 3 Client (Vercel/Browser) ]
              │
              │  (HTTP GET/POST Requests)
              ▼
[ Spring Boot REST API (Render / Docker) ]
              │
              │  (Spring Data JPA / Hibernate)
              ▼
[ Permanent H2 SQL Database (./data/shieldflow.mv.db) ]
```

---

## 🚀 Local Development Setup

Follow these steps to run both your frontend and backend servers locally.

### Prerequisites
*   **Node.js** (v18+)
*   **Java 11 JDK** & **Maven**

### 1. Run the Spring Boot Backend (H2 Database)

1.  Navigate to the `backend` folder:
    ```sh
    cd backend
    ```
2.  Start the application:
    ```sh
    mvn spring-boot:run
    ```
    The server will start on port **`8080`**. A physical database file will automatically be created on disk inside **`backend/data/shieldflow.mv.db`**.

3.  **Inspect the Database**: Open your browser to `http://localhost:8080/h2-console`. Set JDBC URL to `jdbc:h2:file:./data/shieldflow` (user `sa`, password `password`) to browse tables.

### 2. Run the Vue.js Frontend

1.  Navigate back to the root directory:
    ```sh
    cd ..
    ```
2.  Install dependencies:
    ```sh
    npm install
    ```
3.  Start the Vite dev server:
    ```sh
    npm run dev
    ```
    Open `http://localhost:5173` to explore your portal locally!

---

## 📦 Cloud Deployment Details

*   **Platform**: [Render.com](https://render.com) (Deployed inside a secure OpenJDK 11 Docker container).
*   **Unified Build**: The Vue frontend is compiled (`npm run build`) and packed into Spring Boot's static folder (`backend/src/main/resources/static`) so both services run on a single Render server.
*   **Persistent Storage**: Connects to a Render Persistent Mount Disk on `/data` to save the H2 database permanently across deploys.
