# Daladala Route & Fare Information System

A web-based system for searching daladala (minibus) routes and viewing fare information in **Dar es Salaam, Tanzania**.

Built with Spring Boot as a university Advanced Java Programming project.

---

## Features

### Passenger Side
- View all available daladala routes
- Search routes by keyword (area, stop name)
- Search routes by origin and destination
- View full route details including fare and intermediate stops
- View route change history

### Admin Side
- Secure login with session-based authentication
- Add new routes
- Edit existing routes and update fares
- Deactivate (soft-delete) routes
- View all change history on the dashboard

---

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2.5, Java 17 |
| Web | Spring MVC |
| Database Access | Spring Data JPA + Hibernate |
| Frontend | Thymeleaf, HTML, CSS |
| Database | MySQL |
| Build Tool | Maven |
| Code Generation | Lombok |

---

## Project Structure

```
src/main/java/com/daladala/
├── controller/
│   ├── RouteController.java      ← passenger pages
│   └── AdminController.java      ← admin pages + login
├── service/
│   ├── RouteService.java         ← route business logic
│   └── RouteHistoryService.java  ← audit logging
├── repository/
│   ├── RouteRepository.java
│   ├── RouteHistoryRepository.java
│   └── AdminRepository.java
├── entity/
│   ├── Route.java
│   ├── RouteHistory.java
│   └── Admin.java
└── DaladalaApplication.java

src/main/resources/
├── templates/
│   ├── index.html                ← home page
│   ├── routes.html               ← search + results
│   ├── route-detail.html         ← fare info + history
│   ├── admin-login.html
│   ├── admin-dashboard.html
│   └── route-form.html           ← add / edit route
├── static/css/
│   └── style.css
└── application.properties
```

---

## Prerequisites

Make sure the following are installed before running the project:

- **Java 17** — [Download](https://adoptium.net/)
- **Maven 3.6+** — [Download](https://maven.apache.org/)
- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/)
- **VS Code** with the **Extension Pack for Java** (Microsoft)

---

## Setup & Run

### Step 1 — Create the Database

1. Open **MySQL Workbench** (or any MySQL client)
2. Open the file `database.sql` from the project root
3. Run the entire script

This creates the `daladala_db` database, all tables, and loads 20 sample Dar es Salaam routes.

```sql
-- To verify, run:
USE daladala_db;
SELECT * FROM routes;
-- Should return 20 rows
```

### Step 2 — Configure Database Password

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.password=your_password_here
```

Replace `your_password_here` with your MySQL root password.

### Step 3 — Run the Application

**Option A — VS Code**
1. Open the project folder in VS Code (`File → Open Folder`)
2. Wait for Maven to import (bottom status bar shows progress)
3. Open `src/main/java/com/daladala/DaladalaApplication.java`
4. Click the **Run** button above the `main()` method

**Option B — Terminal**
```bash
mvn spring-boot:run
```

### Step 4 — Open in Browser

```
http://localhost:8080
```

---

## Admin Login

Navigate to `http://localhost:8080/admin/login`

| Username | Password |
|---|---|
| `admin` | `admin123` |
| `msimamizi` | `dar2024` |

---

## Application URLs

| URL | Description |
|---|---|
| `http://localhost:8080/` | Home page — all routes |
| `http://localhost:8080/routes` | Full route listing |
| `http://localhost:8080/routes/search` | Search routes |
| `http://localhost:8080/routes/{id}` | Route detail + fare |
| `http://localhost:8080/admin/login` | Admin login |
| `http://localhost:8080/admin/dashboard` | Admin dashboard |
| `http://localhost:8080/admin/routes/new` | Add new route |

---

## Database Tables

| Table | Purpose |
|---|---|
| `routes` | Stores all daladala routes with fare information |
| `route_history` | Audit log — records every create, update, and delete |
| `admins` | Admin accounts for system management |

---

## Sample Routes Included

The `database.sql` file loads 20 realistic Dar es Salaam routes, including:

- Kariakoo → Ubungo (800 TZS)
- Posta → Kimara via Ubungo (1,000 TZS)
- Kariakoo → Tegeta via Mwenge (1,200 TZS)
- Kariakoo → Mbezi Beach via Kimara (1,500 TZS)
- And 16 more...

---

## Notes

- Routes are **soft-deleted** — deactivating a route hides it from passengers but keeps its history intact
- Every admin action (add, edit, deactivate) is automatically logged to `route_history`
- Admin sessions are managed via `HttpSession` — no Spring Security required
- Thymeleaf cache is disabled during development for instant HTML reload

---

## Author

University Advanced Java Programming Project  
Case Study: Dar es Salaam, Tanzania
>>>>>>> 934d5e6 (Initial project commit)
