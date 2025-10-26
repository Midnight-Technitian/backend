# Midnight Technician
An open-source full-stack Java platform for technician management and customer support.

![midnight_technician logo](https://img.shields.io/badge/Mitnight%20Tecnician-Open%20Source-purple?style=for-the-badge&logo=java)
![Gradle](https://img.shields.io/badge/Gradle-9.1.0+-green?style=for-the-badge&logo=gradle)

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0--M3-brightgreen?style=flat-square&logo=spring)

![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

---
# Table of Contents
1. [About](#about)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Getting Started](#getting-started)
5. [Contributing](#contributing)
6. [License](#license)

# About
Midnight Technician is a collaborative open-source project designed to streamline technician-customer interactions and service management. The project includes:
- Backend: Spring Boot 4 + PostgreSQL
- Frontend: Thymeleaf and modern UI components
- Shared Module: Common DTOs and utilities for backend and frontend

It’s perfect for developers looking to contribute to a real-world Java/Spring Boot project while learning about role-based security, full-stack development, and modular architecture.

---
# Features
- Role-based access control (USER, TECHNICIAN, MANAGER, OWNER)
- Service ticket creation and management
- Dynamic dashboards for customers and technicians
- Modular architecture for backend, frontend, and shared code
- Fully open-source and collaborative
---
# Tech Stack

| Category      | Technology      | Version  |
|---------------|-----------------|----------|
| **Language**  | Java            | JDK 25   |
| **Backend**   | Spring Boot     | 4.0.0-M3 |
| **Security**  | Spring Security | 6        |
| **Database**  | Postgres SQL    | Latest   |
| **Database**  | Mongo DB        | Latest   |
| **Build**     | Gradle          | 9.1.0+   |

---
# Getting Started
1. Clone the repositories
    ```shell
    git clone https://github.com/Midnight-Technitian/backend.git
    git clone https://github.com/Midnight-Technitian/frontend.git
    ```
2. Set up the backend
   - Configure application.properties with your DB credentials
   - Configure your environment variables
3. Set up the frontend
   - Configure the backend API URL if needed
   - Run with your IDE or via Gradle
4. Open the app
   - Access the dashboard via http://localhost
   - Default roles are assigned to new users, and you can expand them dynamically
---
# Contributing
We welcome contributions from developers of all levels!
- Fork the repository
- Create a feature branch (git checkout -b feature/my-feature)
- Commit your changes (git commit -am 'Add new feature')
- Push to the branch (git push origin feature/my-feature)
- Open a pull request
### Tips for contributors:
- Look for issues tagged good first issue
- Follow existing code style and naming conventions
- Respect role-based access and security logic
---
# License
This project is licensed under the MIT License — see the LICENSE
file for details.