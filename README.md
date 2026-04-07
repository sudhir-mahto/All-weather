# All Weather Solution - AC Repair Service Website

**S.K Electronics** | Buxar, Bihar, India  
Phone: +91 96613 32544

A full-stack web application for an AC repair service business — static frontend with a Spring Boot REST API backend backed by MySQL.

---

## Project Structure

```
All-weather/
├── index.html              # Frontend (updated with booking/contact/reviews)
├── style.css               # Styles (with modal/form styles added)
├── script.js               # JS (with API calls for booking/contact/reviews)
├── logo.png                # Business logo
├── setup-db.sh             # Podman MySQL setup script (run this first)
├── podman-compose.yml      # Podman Compose equivalent
├── README.md               # This file
└── backend/
    ├── pom.xml
    └── src/main/
        ├── java/com/allweather/
        │   ├── AllWeatherApplication.java
        │   ├── config/
        │   │   ├── CorsConfig.java
        │   │   └── DataInitializer.java
        │   ├── controller/
        │   │   ├── BookingController.java
        │   │   ├── ContactController.java
        │   │   ├── ServiceController.java
        │   │   └── ReviewController.java
        │   ├── model/
        │   │   ├── Booking.java
        │   │   ├── Contact.java
        │   │   ├── ServiceItem.java
        │   │   └── Review.java
        │   ├── repository/
        │   │   ├── BookingRepository.java
        │   │   ├── ContactRepository.java
        │   │   ├── ServiceRepository.java
        │   │   └── ReviewRepository.java
        │   └── service/
        │       ├── BookingService.java
        │       ├── ContactService.java
        │       ├── ServiceItemService.java
        │       └── ReviewService.java
        └── resources/
            └── application.properties
```

---

## Prerequisites

- Java 17+ (check: `java -version`)
- Maven 3.8+ (check: `mvn -version`)
- Podman (macOS: `brew install podman`)

---

## Quick Start

### Step 1: Start MySQL via Podman

```bash
# First time: initialize Podman machine (macOS only)
podman machine init
podman machine start

# Run the setup script
./setup-db.sh
```

This will:
- Pull MySQL 8.0 image
- Start a container named `allweather-mysql`
- Create the `allweather_db` database
- Create user `allweather_user` with password `AllWeather@2024`
- Wait until MySQL is ready

### Step 2: Start the Spring Boot Backend

```bash
cd backend
mvn spring-boot:run
```

On first startup, the `DataInitializer` seeds the 6 default services automatically.

The API will be available at: `http://localhost:8080`

### Step 3: Open the Frontend

Open `index.html` in your browser directly, or use a local server:

```bash
# Python (from project root)
python3 -m http.server 5500

# Then visit: http://localhost:5500
```

Or use the VS Code "Live Server" extension.

---

## API Endpoints

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/bookings` | Create a new service booking |
| `GET` | `/api/bookings` | Get all bookings (admin) |
| `GET` | `/api/bookings?status=PENDING` | Filter bookings by status |
| `GET` | `/api/bookings/{id}` | Get single booking |
| `PUT` | `/api/bookings/{id}/status` | Update booking status |

**Create Booking Request Body:**
```json
{
  "customerName": "Rahul Kumar",
  "phone": "9876543210",
  "email": "rahul@example.com",
  "address": "123 Main St, Buxar, Bihar",
  "serviceType": "Split AC Repair",
  "preferredDate": "2026-04-10",
  "notes": "AC not cooling properly"
}
```

**Booking Statuses:** `PENDING`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

---

### Contacts

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/contacts` | Submit a contact message |
| `GET` | `/api/contacts` | Get all messages (admin) |
| `GET` | `/api/contacts?unread=true` | Get only unread messages |
| `GET` | `/api/contacts/{id}` | Get single message |
| `PUT` | `/api/contacts/{id}/read` | Mark as read |

**Contact Request Body:**
```json
{
  "name": "Priya Singh",
  "phone": "9876543210",
  "email": "priya@example.com",
  "message": "I need AC installation for my new home."
}
```

---

### Services

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/services` | Get all active services |
| `GET` | `/api/services?all=true` | Get all services including inactive |
| `GET` | `/api/services/{id}` | Get single service |
| `POST` | `/api/services` | Create service (admin) |
| `PUT` | `/api/services/{id}` | Update service (admin) |

---

### Reviews

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/reviews` | Get approved reviews (public) |
| `GET` | `/api/reviews?all=true` | Get all reviews (admin) |
| `GET` | `/api/reviews/{id}` | Get single review |
| `POST` | `/api/reviews` | Submit a review |
| `PUT` | `/api/reviews/{id}/approve` | Approve a review (admin) |
| `PUT` | `/api/reviews/{id}/reject` | Reject a review (admin) |

**Review Request Body:**
```json
{
  "customerName": "Amit Sharma",
  "rating": 5,
  "comment": "Excellent service! Fixed my AC within an hour.",
  "serviceType": "Split AC Repair"
}
```

---

### Health Check

```
GET http://localhost:8080/actuator/health
```

---

## Database Configuration

| Setting | Value |
|---------|-------|
| Host | localhost:3306 |
| Database | allweather_db |
| Username | allweather_user |
| Password | AllWeather@2024 |
| Root Password | RootAllWeather@2024 |

---

## Podman Commands Reference

```bash
# Start MySQL container
podman start allweather-mysql

# Stop MySQL container
podman stop allweather-mysql

# View MySQL logs
podman logs -f allweather-mysql

# Connect to MySQL shell
podman exec -it allweather-mysql mysql -u allweather_user -p allweather_db

# View all containers
podman ps -a
```

---

## Development Notes

- **JPA DDL**: Set to `update` by default — schema is created/updated without data loss on restart. Change to `create-drop` for a clean slate each restart.
- **Reviews Approval**: New reviews are hidden by default. Use `PUT /api/reviews/{id}/approve` to make them visible.
- **CORS**: All origins are allowed in dev. Restrict `CorsConfig.java` in production.
- **Security**: No authentication is implemented yet. All endpoints are public. Add Spring Security JWT for production admin routes.

---

## Building for Production

```bash
cd backend
mvn clean package -DskipTests

# Run the JAR
java -jar target/allweather-backend-1.0.0.jar
```

---

*Made for All Weather Solution | S.K Electronics | Buxar, Bihar*
