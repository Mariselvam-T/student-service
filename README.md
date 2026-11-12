# Student Service

Part of the **Student Registration Microservices System** (SEZG583 Scalable Services, BITS Pilani WILP).

| | |
|---|---|
| Author | Mariselvam (2025MT03120) |
| Port | 8081 |
| Stack | Java 17, Spring Boot 3.2, Spring Data JPA, H2, Maven, Docker, Kubernetes |
| Companion repo | `course-service` (calls this service over REST) |

## What it does
Owns all student data: create, read, update and delete students. The Course Service calls
`GET /api/students/{id}` to check that a student exists before registering him/her to a course.

## REST API
| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/students` | Create student | 201 |
| GET | `/api/students` | Get all students | 200 |
| GET | `/api/students/{id}` | Get one student | 200 / 404 |
| PUT | `/api/students/{id}` | Update student | 200 / 404 / 409 |
| DELETE | `/api/students/{id}` | Delete student | 204 / 404 |

Request body (POST / PUT):
```json
{ "name": "Arun Prakash", "email": "arun.prakash@example.com", "department": "Software Engineering" }
```
Errors: 400 (validation), 404 (not found), 409 (duplicate email). Health: `/actuator/health`. H2 console: `/h2-console` (JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, empty password).

## Prerequisites
Java 17+, Maven 3.8+, Docker Desktop (for container steps).

## Run locally
```bash
mvn clean package
java -jar target/student-service.jar
# or: mvn spring-boot:run
curl http://localhost:8081/actuator/health
```

## Docker
```bash
mvn clean package
docker build -t student-service:1.0 .
docker network create sreg-net
docker run -d --name student-service --network sreg-net -p 8081:8081 student-service:1.0
docker logs student-service
```

## Kubernetes
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/student-service-deployment.yaml
kubectl apply -f k8s/student-service-service.yaml
kubectl get pods -n student-registration
kubectl port-forward -n student-registration svc/student-service 8081:8081
```
Optional autoscaling: `kubectl apply -f k8s/student-service-hpa.yaml` (needs metrics-server).

## Project structure
```
student-service/
├── .github/workflows/ci.yml
├── .gitignore
├── Dockerfile
├── pom.xml
├── README.md
├── k8s/
│   ├── namespace.yaml
│   ├── student-service-deployment.yaml
│   ├── student-service-service.yaml
│   └── student-service-hpa.yaml
└── src/
    ├── main/java/com/bits/sreg/student/
    │   ├── StudentServiceApplication.java
    │   ├── controller/StudentController.java
    │   ├── service/StudentService.java
    │   ├── repository/StudentRepository.java
    │   ├── entity/Student.java
    │   ├── dto/{StudentRequest, StudentResponse}.java
    │   └── exception/{ResourceNotFoundException, ConflictException, ErrorResponse, GlobalExceptionHandler}.java
    ├── main/resources/application.properties
    └── test/java/com/bits/sreg/student/StudentServiceApplicationTests.java
```

## Known limitation
H2 runs in memory, so data is lost on restart and is not shared between replicas. A production
version would use a shared database such as PostgreSQL.
