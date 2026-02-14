# Google App Engine User Management System

A serverless Java application built on **Google App Engine Standard**, designed to ingest user data from Excel files, store it in **Google Cloud Datastore** (NoSQL), and migrate it to **BigQuery** (Data Warehouse) for analytics.

**Live Demo URL:** https://user-management-app-486411.el.r.appspot.com

---

## 🚀 Features & Production Readiness

### Milestone 1: Data Ingestion & Management
* **Excel Parsing:** Upload `.xlsx` files containing bulk user data.
* **NoSQL Storage:** Automatically saves parsed records into Google Cloud Datastore (Kind: `User`).
* **Authentication & Security:** Passwords are securely hashed using **BCrypt** before persistence. 
* **Scalable User Directory:** A pure Vanilla JS dashboard that fetches data using **Server-Side Cursor-Based Pagination** to handle large datasets without OOM issues.
* **Endpoint Protection:** Critical operations (Upload & Migration) are protected by an `X-Admin-Key` header verification to prevent unauthorized access and billing abuse.

### Milestone 2: BigQuery ETL Migration
* **Data Warehouse Integration:** Migration of all Datastore records to BigQuery.
* **Streaming Inserts:** Uses the BigQuery Streaming API for real-time data loading.
* **Idempotency:** Utilizes `insertId` (based on Entity keys) to prevent duplicate records if the migration is triggered multiple times.

### ⚙️ Production Enhancements Applied
Based on review feedback, the following enterprise standards were integrated:
1.  **Security:** Replaced plain-text passwords with BCrypt hashing.
2.  **Access Control:** Implemented an Admin Secret Key for API protection.
3.  **Scalability:** Replaced full-table scans with Datastore Cursors for paginated fetching.
4.  **Configuration:** Extracted hardcoded dataset and table names into Environment Variables (`appengine-web.xml`).
5.  **Robustness:** Replaced generic stack traces with standard Server Logging (`java.util.logging`).

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
    * `jbcrypt` (Password Hashing)

---

## ⚙️ Setup & Deployment

### Prerequisites
* Java JDK 17+
* Apache Maven 3.8+
* Google Cloud SDK (`gcloud` CLI)

### Environment Configuration
Before deploying, ensure you configure the environment variables in `src/main/webapp/WEB-INF/appengine-web.xml`:
* `DATASTORE_KIND`
* `BIGQUERY_DATASET`
* `BIGQUERY_TABLE`
* `ADMIN_SECRET` (Required for Upload and Migration API calls)

### Deployment to Google Cloud
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ramanjoshi90/user-management-app.git](https://github.com/ramanjoshi90/user-management-app.git)
    cd user-management-app
    ```
2.  **Stage the files:**
    ```bash
    mvn clean package appengine:stage
    ```
3.  **Deploy:**
    ```bash
    gcloud app deploy target/appengine-staging/app.yaml
    ```

---

## 📂 Project Structure

```text
src/main/java/com/usermgmt/
├── model/
│   └── User.java             # POJO classes
├── util/
│   └── PasswordUtil.java     # BCrypt hashing and verification logic
└── servlet/
    ├── UploadServlet.java    # Handles Excel Parsing, Admin Check & Datastore Writes
    ├── ListUsersServlet.java # API for Listing (Paginated) & Deleting Users
    ├── LoginServlet.java     # Authentication Logic (BCrypt verification)
    └── MigrationServlet.java # ETL Logic (Datastore -> BigQuery)
```

## 📝 Usage Guide

1.  **Ingest Data:** Go to `/index.html`, enter the **Admin Secret Key**, and upload the provided `sample_users.xlsx`.
2.  **Login:** Use credentials from the newly uploaded Excel file to log in at `/login.html`.
3.  **Manage:** View, search, and delete users at `/users.html`(supports pagination).
4.  **Migrate:** Navigate to `/migration.html`, enter the **Admin Secret Key**, and click "Start Migration" to push data to BigQuery.

---
*Developed for Technical Assignment - Feb 2026*