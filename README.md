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

---
# About
Midnight Technician is a collaborative open-source project designed to streamline technician-customer interactions and service management. The project includes:
- Backend: Spring Boot 4 + PostgresSQL
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
    git clone https://github.com/Midnight-Technician/backend.git
    ```
2. Set up the backend
   - Rune the `docker-composer.ymll` file to initialize the Database Containers & Kafka services
   - Configure `application.properties` with your DB credentials
   - Configure PostgreSQL with the following: 
     - analytic Module: `resources/data.sql` to initialize the database tables
     - services Module: `resources/data.sql` to initialize the database tables
     - website Module: `resources/data.sql` to initialize the database tables
   - Connect to MongoDB from your Intellij Datasource panel
      - Create a new connection with the following details:
        - url: `mongodb://M_T_USER:M_T_PASSWORD@localhost:27042/midnight-technician?authSource=admin`
      - Create the databases in the MongoDB shell:
        ```shell
        use midnight-employee

        db.createCollection("employee")
        db.createCollection("midnight_employee_sequences")
     
        use midnight-ticketing
     
        db.createCollection("service_ticket")
        db.createCollection("midnight_ticket_sequences")
     
        use midnight-customer
     
        db.createCollection("customer")
        db.createCollection("midnight_customer_sequences")
     
        use midnight-customer-device
     
        db.createCollection("customer_device")
        db.createCollection("midnight_customer_device_sequences")
     
        use midnight-employee-scheduling
     
        db.createCollection("schedules")
        db.createCollection("time_records")
        db.createCollection("midnight_schedule_sequences")
         ```
3. Setting up the environment variables
    - Navigate to the example file
        - The example file can be found in the **shared module**: `.env.example`
    - Review the `.env.example` file for the required token names
    - Navigate to https://sentry.io/auth/login/ and on your dashboard create a new project
    - Create the project name as `midnight-technician` for the application conventions
    - From here you can find your `AUTH_TOKEN`, and `SENTRY_HOST` you need
    - For the `SENTRY_ENVIRONMENT` leave this as `development` for now
4. Running the Microservices:
   - Inside the Services View in Intellij, Click the + icon to add Spring Boot configuration
   - Run the applications in any order
---
# Contributing
We welcome contributions from developers of all levels!
- Clone the repository
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