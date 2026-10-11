# User Login Platform — Local Setup (No Docker)

Two Spring Boot microservices, a PostgreSQL database, and a React + Vite frontend. **Docker is not required.** Run PostgreSQL locally on port `5432`. Flyway is not used; Hibernate `ddl-auto: update` creates/updates the entity tables for local development. Request DTO validation remains enabled.

| Component | Port | Database / role | Responsibility |
|---|---:|---|---|
| `auth-service` | 8081 | `authdb` / `auth` | Login, registration, credential storage, JWT issuing |
| `user-service` | 8082 | `userdb` / `user` | Profile storage, user/admin listing, CSV export |
| React frontend | 3000 | — | Login, registration, dashboard |

## Prerequisites

- JDK 21 (this codebase uses Spring Boot 3.3 and Java 21)
- Maven 3.9+
- PostgreSQL 14+ installed and running locally on port `5432`
- Node.js 20+ and npm

## 1. Configure PostgreSQL

Open `psql` as a PostgreSQL administrator and run the setup script. Example on Windows (adjust the PostgreSQL bin path if your version differs):

```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d postgres -f .\database\setup-local.sql
```

The script creates two local databases and roles: `authdb` (`auth` / `auth`) and `userdb` (`user` / `user`). These are development credentials only; change them for any shared or production environment. If you prefer different credentials, update the environment variables below.

## 2. Start user-service

In terminal 1, from the project root:

```powershell
cd user-service
mvn spring-boot:run
```

Wait for `Started UserServiceApplication` on port `8082`.

## 3. Start auth-service

In terminal 2, from the project root:

```powershell
cd auth-service
mvn spring-boot:run
```

Wait for `Started AuthServiceApplication` on port `8081`. Auth-service calls user-service during registration/login, so start user-service first.

## 4. Start the frontend

In terminal 3, from the project root:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000`.

## Database configuration overrides

The defaults in each service's `src/main/resources/application.yml` point to local PostgreSQL on port `5432`. Override them with environment variables if needed:

- Auth service: `AUTH_DB_URL`, `AUTH_DB_USER`, `AUTH_DB_PASSWORD`
- User service: `USER_DB_URL`, `USER_DB_USER`, `USER_DB_PASSWORD`
- Shared JWT signing key: `JWT_SECRET` (must be identical for both services; use a strong random secret)
- Internal service API key: `INTERNAL_API_KEY` (must match in both services)
- User-service base URL: `USER_SERVICE_URL` (default `http://localhost:8082`)

## Assignment behavior

- Login submits email/password as JSON and checks the credential table.
- Existing accounts receive a successful response with role `U` or `A`; the UI can request the current user's profile or, for an admin, all profiles.
- New users register with first name, last name, email, mobile number, and password. Credentials and profile data are stored in separate tables/services, linked by email.
- Registration/login DTOs use Bean Validation and controllers use `@Valid`; invalid requests return HTTP 400 with structured validation errors. Duplicate email is rejected. Passwords are BCrypt-hashed; the raw password is never stored.
- Admins can download the user list as CSV.

## Promote a registered user to admin

After registering, run this command in `psql` connected to `userdb` as a database owner/admin:

```sql
UPDATE user_info SET user_type = 'A' WHERE email = 'admin@example.com';
```

Log out and log in again to refresh the JWT role.

## Notes

- The project intentionally has no Docker dependency. It expects native PostgreSQL installed and running locally.
- Flyway and migration files are not used. Hibernate `ddl-auto: update` is for local development only; production deployments should use controlled schema migrations and `ddl-auto: validate`.
- The codebase targets Java 21 / Spring Boot 3.3. It is not Java 8 compatible in its current form.
# user_Dashboard
