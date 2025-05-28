# 📸 Albums API v1.0 - Monolithic Version

A Spring Boot application that allows users to manage albums and upload one or multiple photos to each album. Authentication is handled via JWT, and all uploaded images are stored locally.

---

## 🚀 Features

- 🔐 Secure endpoints with JWT authentication
- 👤 Role-Based Access Control (RBAC)
- 📁 Create albums and upload one or multiple photos to them
- 🗃️ Local file storage for photos
- 📃 API documentation via Swagger UI
- 🔀 Versioned REST APIs (e.g. `/api/v1/albums`)
- 🧪 Basic unit testing setup

---

## 🧱 Domain Entities

- **Account** – Represents a user account (used for authentication and ownership)
- **Album** – A user-created collection that holds uploaded photos
- **Photo** – A file belonging to an album, stored locally

---

## 📦 Technologies Used

- Spring Boot 3.4.5
- Java 21
- Spring Web
- Spring Data JPA
- Spring Security + OAuth2 Resource Server
- Lombok
- H2 Database (in-memory dev DB)
- SpringDoc OpenAPI (Swagger)
- Apache Commons Imaging & Imgscalr (for image processing)

---
## 🔁 Microservice Implementation

The microservices-based refactor of this application is being developed here:

👉 [Albums API Microservices Version](https://github.com/your-username/albums-api-microservices)

---

## 🔮 Future Improvements

Below are some enhancements and improvements planned for the project:

- ✅ Versioned APIs  
- ✅ Role-Based Access Control (RBAC)  
- ☁️ Migrate photo storage to Amazon S3 or other cloud provider  
- 🐘 Replace in-memory H2 with PostgreSQL or MySQL  
- 🧪 Increase unit and integration test coverage  
- 🛡️ Improve token management (e.g., refresh tokens)  
- 🧵 Async image upload processing with message queue (Kafka/RabbitMQ)  
- 🌍 Support internationalization (i18n) for messages and errors  
- 📊 Add performance monitoring and logging (e.g., Prometheus + Grafana)  
- 🧰 Add CI/CD pipeline for automated deployments  
- 📤 Export albums/photos metadata to JSON or CSV  
- 🧩 Create reusable modules for shared logic in future microservices  

---

## 🔧 Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+

### Run Locally

```bash
git clone https://github.com/your-username/albums-api-v1.0-monolith.git
cd albums-api-v1.0-monolith
mvn spring-boot:run

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork the repo and open a pull request.

Please follow standard best practices and write clean, testable code.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Amadou Sarjo Jallow**  
For questions or support, feel free to open an issue on GitHub.
