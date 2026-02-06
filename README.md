# Google App Engine User Management System

A serverless Java application built on **Google App Engine Standard**, designed to ingest user data from Excel files, store it in **Google Cloud Datastore** (NoSQL), and migrate it to **BigQuery** (Data Warehouse) for analytics.

**Live Demo URL:** https://user-management-app-486411.el.r.appspot.com

---

## 🚀 Features

### Milestone 1: Data Ingestion & Management
* **Excel Parsing:** Upload `.xlsx` files containing bulk user data.
* **NoSQL Storage:** Automatically saves parsed records into Google Cloud Datastore (Kind: `User`).
* **User Directory:** A dashboard to view, filter, and delete users.
* **Authentication:** Simple login system that verifies credentials against the stored Datastore records.
* **Native JS UI:** No frameworks (React/Angular) used; pure Vanilla JavaScript for all frontend logic.

### Milestone 2: BigQuery ETL Migration
* **Data Warehouse Integration:** One-click migration of all Datastore records to BigQuery.
* **Streaming Inserts:** Uses the BigQuery Streaming API for real-time data loading.
* **Error Handling:** Provides detailed logs of successful insertions and row-level errors.

---

## 🛠️ Tech Stack

* **Cloud Platform:** Google App Engine (Standard Environment)
* **Language:** Java 17 (Jakarta EE 8 / Servlet API 3.1)
* **Build Tool:** Apache Maven
* **Databases:**
    * **Transactional:** Google Cloud Datastore (NoSQL)
    * **Analytical:** Google BigQuery
* **Libraries:**
    * `apache-poi` (Excel Processing)
    * `google-cloud-datastore` (DB Client)
    * `google-cloud-bigquery` (Data Warehouse Client)
    * `gson` (JSON Serialization)

---

## ⚙️ Setup & Deployment

### Prerequisites
* Java JDK 17+
* Apache Maven 3.8+
* Google Cloud SDK (`gcloud` CLI)

### Local Development
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ramanjoshi90/user-management-app.git](https://github.com/ramanjoshi90/user-management-app.git)
    cd user-management-app
    ```

2.  **Build the project:**
    ```bash
    mvn clean package
    ```

### Deployment to Google Cloud
This project uses the `gcloud` CLI for deployment.

1.  **Stage the files:**
    ```bash
    mvn package appengine:stage
    ```

2.  **Deploy:**
    ```bash
    gcloud app deploy target/appengine-staging/app.yaml
    ```

---

## 📂 Project Structure

src/main/java/com/usermgmt/
├── model/
│   └── User.java                 # POJO classes
└── servlet/
    ├── UploadServlet.java        # Handles Excel Parsing & Datastore Writes
    ├── ListUsersServlet.java     # API for Listing & Deleting Users
    ├── LoginServlet.java         # Authentication Logic
    └── MigrationServlet.java     # ETL Logic (Datastore → BigQuery)

## 📝 Usage Guide

1.  **Ingest Data:** Go to `/index.html` and upload the provided `sample_users.xlsx`.
2.  **Login:** Use credentials from the Excel file to log in at `/login.html`.
3.  **Manage:** View and delete users at `/users.html`.
4.  **Migrate:** Navigate to `/migration.html` and click "Start Migration" to push data to BigQuery.

---
*Developed for Technical Assignment - Feb 2026*