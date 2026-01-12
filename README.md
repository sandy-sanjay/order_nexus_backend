# Order Nexus Backend

Spring Boot backend for Order Nexus.

## 📚 API Configuration
**Endpoints & Testing**: See [API_ENDPOINTS.md](API_ENDPOINTS.md) for full details on Local vs Live endpoints.

## 🚀 deployment
Backend is configured to deploy on Render.

**Configuration:**
- `local` profile: Uses `application-local.properties` (Configured for TiDB).
- `prod` profile: Uses `application-prod.properties` (Configured for TiDB with Env Var overrides).

## 🏃‍♂️ Running Locally
```bash
mvn spring-boot:run
```
