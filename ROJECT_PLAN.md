# Daladala Route Information and Fare Management System

## Case Study
Dar es Salaam, Tanzania

## Objective
To help passengers search daladala routes and view fare information.

## Features

### Passenger
- Search routes
- View route details
- View fare information
- View route history

### Admin
- Add routes
- Edit routes
- Delete routes
- Update fares

## Technology Stack
- Spring Boot
- Spring Web
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

## Project Constraints
- Simple student project
- No JWT
- No Spring Security
- No Docker
- No Microservices
- No External APIs
- No Complex Design Patterns

## Planned Structure

controller
service
repository
entity
templates
static/css

## Expected Duration
3 weeks

## Project Structure(src)
src/main/java/com/daladala

├── controller
│   ├── RouteController.java
│   └── AdminController.java
│
├── service
│   ├── RouteService.java
│   └── AdminService.java
│
├── repository
│   ├── RouteRepository.java
│   ├── RouteHistoryRepository.java
│   └── AdminRepository.java
│
├── entity
│   ├── Route.java
│   ├── RouteHistory.java
│   └── Admin.java
│
└── DaladalaApplication.java

src/main/resources

├── templates
│   ├── index.html
│   ├── routes.html
│   ├── fare-info.html
│   ├── admin-login.html
│   ├── admin-dashboard.html
│   └── route-form.html
│
├── static
│   └── css
│       └── style.css
│
└── application.properties