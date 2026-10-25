# 🎓 CampusConnect

**A Java-Based University Collaboration Platform**

CampusConnect is a university collaboration platform designed to help students connect with peers and mentors, resolve academic doubts, share learning resources, and collaborate on projects.

The goal is to create a collaborative academic environment where students can learn from one another, exchange knowledge, and develop their skills together.

## ✨ Features

* **👥 User Authentication** — User registration, login, and account management.
* **🎯 Peer Mentorship** — Connect students with mentors based on their skills and availability.
* **❓ Academic Doubt Solving** — Ask academic questions and share answers.
* **📚 Resource Sharing** — Upload and share learning resources.
* **💻 Project Collaboration** — Explore projects and collaborate with other students.
* **📅 Session Booking** — Book mentorship sessions and provide feedback.
* **🔔 Notifications** — Receive updates about platform activities.
* **⭐ Skill Credits** — Earn and use credits for eligible platform activities.
* **🛡️ Administration** — Administrative tools for platform management and reporting.

## 🛠️ Technology Stack

* **Backend:** Java
* **Web Technologies:** JSP, HTML, CSS, JavaScript
* **Web Application:** Java Servlet-based architecture
* **Build Tool:** Apache Maven
* **Database:** H2 Database, configured in MySQL compatibility mode
* **Application Server:** Apache Tomcat

## 📁 Project Structure

```text
CampusConnect/
├── src/
│   ├── main/
│   │   ├── java/com/campusconnect/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dao/
│   │   │   ├── exception/
│   │   │   ├── filter/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   └── util/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
├── pom.xml
├── mvnw.cmd
└── .gitignore
```

## ⚙️ Prerequisites

Before running CampusConnect, install:

* Java Development Kit (JDK) compatible with the project configuration
* Apache Maven, or use the included Maven Wrapper
* Apache Tomcat compatible with the project's Servlet API
* Git

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Farihashifa15/CampusConnect.git
cd CampusConnect
```

### 2. Configure the database

The project uses an H2 database in file mode.

The local database configuration file is intentionally excluded from Git.

Create or configure `src/main/resources/db.properties` using `src/main/resources/db.properties.example` as a reference.

Review the database URL and settings for your local environment before starting the application.

### 3. Build the project

On Windows, run:

```powershell
.\mvnw.cmd clean package
```

### 4. Deploy to Apache Tomcat

Deploy the generated WAR file from the Maven `target/` directory to your Tomcat installation.

Start Tomcat and open:

```text
http://localhost:8080/campusconnect/
```

The URL assumes the application is deployed with the context path `/campusconnect`.

## 🧪 Testing

The project includes automated tests under `src/test/`.

Run the test suite with:

```powershell
.\mvnw.cmd test
```

Review the test results before deploying changes.

## 🤝 Contributing

Contributions and suggestions are welcome!

1. Fork the repository or obtain access to the private repository.
2. Create a feature branch.
3. Make your changes.
4. Run the available tests.
5. Submit a pull request for review.

Please avoid committing local database files, credentials, uploaded documents, or generated build output.

## 🔐 Security

* Do not commit passwords, API keys, or private credentials.
* Keep local database files and uploaded user documents out of version control.
* Review configuration files before committing changes.
* Use appropriate authentication and authorization practices when deploying the application.

## 📌 Project Status

CampusConnect is under active development. Features, configuration, and deployment procedures may evolve as the project improves.

## 👩‍💻 Maintainer

**Fariha Shifa**

GitHub: [@Farihashifa15](https://github.com/Farihashifa15)

---

*Connecting students. Sharing knowledge. Growing together.*
