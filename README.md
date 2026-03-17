# 🚀 Fitness Microservices System

A scalable full-stack fitness application built using **Spring Boot Microservices architecture** with **Spring Cloud** and a **React frontend**.

---

## 🏗️ Architecture Overview

The system follows a distributed microservices architecture with centralized configuration and service discovery.

### 🔹 Core Services
- **User Service** – Manages user profiles and authentication  
- **Activity Service** – Handles fitness activities and tracking  
- **AI Service** – Provides intelligent fitness recommendations  

---

### 🔹 Infrastructure Services
- **Config Server** – Centralized configuration management  
- **Eureka Server** – Service registry and discovery  
- **API Gateway** – Entry point for all client requests  

---

### 🔹 Frontend
- Built using **React (Vite)** for responsive UI  

---

## ⚙️ Tech Stack

- **Backend:** Spring Boot, Spring Cloud  
- **Frontend:** React, Vite  
- **Service Discovery:** Eureka  
- **API Gateway:** Spring Cloud Gateway  
- **Configuration:** Spring Cloud Config  
- **Build Tool:** Maven  

---

## 🚀 How to Run

### 1. Start Infrastructure Services
```bash
cd configserver
mvn spring-boot:run
