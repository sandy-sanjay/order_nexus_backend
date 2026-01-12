# Order Nexus API Endpoints

Use these endpoints to test your application in Postman.

## 🏠 Local Environment
**Base URL:** `http://localhost:8080`

| Action | Method | URL | Body / Notes |
| :--- | :--- | :--- | :--- |
| **Register** | `POST` | `/api/auth/register` | `{"username": "localuser", "password": "password", "role": "USER"}` |
| **Login** | `POST` | `/api/auth/login` | `{"username": "localuser", "password": "password"}` <br> **Response:** Returns `token` |
| **Get Products** | `GET` | `/api/products` | **Auth:** `Bearer <TOKEN>` |

---

## 🚀 Live Environment (Render)
**Base URL:** `https://order-nexus-backend.onrender.com`

| Action | Method | URL | Body / Notes |
| :--- | :--- | :--- | :--- |
| **Register** | `POST` | `/api/auth/register` | `{"username": "liveuser", "password": "password", "role": "USER"}` |
| **Login** | `POST` | `/api/auth/login` | `{"username": "liveuser", "password": "password"}` <br> **Response:** Returns `token` |
| **Get Products** | `GET` | `/api/products` | **Auth:** `Bearer <TOKEN>` |

---

## 🛠 Troubleshooting
- **Local Connection Refused?** Ensure Spring Boot is running (`mvn spring-boot:run`).
- **Live Connection Error?** Ensure Render environment variables are set correctly (TiDB URL, Username, Password).
