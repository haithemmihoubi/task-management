# Task Management API

A production-ready RESTful API built with Spring Boot 3.5.6 and MongoDB for managing tasks with advanced filtering capabilities.

## Features

- ✅ **Complete CRUD Operations** - Create, Read, Update, Delete tasks
- ✅ **Advanced Filtering** - Filter by status, priority, date range
- ✅ **Smart Search** - Case-insensitive search across title and description
- ✅ **Flexible Sorting** - Sort by priority or due date
- ✅ **Input Validation** - Comprehensive validation with detailed error messages
- ✅ **Global Exception Handling** - Consistent error responses
- ✅ **API Versioning** - `/api/v1/` prefix for future compatibility
- ✅ **CORS Enabled** - Ready for frontend integration
- ✅ **Comprehensive Tests** - Unit tests for service and controller layers
- ✅ **Postman Collection** - 25 ready-to-use API requests

## Requirements

- Java 21
- Maven 3.6+
- MongoDB Atlas account (or local MongoDB)

##  Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: MongoDB
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Test
- **Documentation**: Postman Collection

## Installation

### 1. Clone the repository
```bash
git clone https://github.com/haithemmihoubi/task-management
cd task-managemnt
```

### 2. Configure MongoDB or use MongoDB Atlas   
#  I'm using mongo atlas for this project
- If using MongoDB Atlas, create a cluster and get the connection string.
- If using local MongoDB, ensure it's running on default port `27017`.
- 
Update `src/main/resources/application.yaml` with your MongoDB connection string:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://<username>:<password>@<cluster>.mongodb.net/taskdb
```

### 3. Build the project
```bash
./mvnw clean install
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1/tasks`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/v1/tasks
```

### Endpoints

#### Create Task
```http
POST /api/v1/tasks
Content-Type: application/json

{
    "title": "Complete documentation",
    "description": "Write API documentation",
    "status": "TODO",
    "priority": 1,
    "dueDate": "2025-12-31"
}
```

**Response**: `201 Created`
```json
{
    "id": "6700a1b2c3d4e5f678901234",
    "title": "Complete documentation",
    "description": "Write API documentation",
    "status": "TODO",
    "priority": 1,
    "dueDate": "2025-12-31"
}
```

#### Get All Tasks
```http
GET /api/v1/tasks
```

**Response**: `200 OK`
```json
[
    {
        "id": "6700a1b2c3d4e5f678901234",
        "title": "Complete documentation",
        "description": "Write API documentation",
        "status": "TODO",
        "priority": 1,
        "dueDate": "2025-12-31"
    }
]
```

#### Get Task by ID
```http
GET /api/v1/tasks/{id}
```

**Response**: `200 OK` or `404 Not Found`

#### Update Task
```http
PUT /api/v1/tasks/{id}
Content-Type: application/json

{
    "title": "Updated title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "priority": 2,
    "dueDate": "2025-11-30"
}
```

**Response**: `200 OK`

#### Delete Task
```http
DELETE /api/v1/tasks/{id}
```

**Response**: `204 No Content`

### Advanced Filtering

#### Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `status` | String | Filter by status | `TODO`, `IN_PROGRESS`, `DONE` |
| `priority` | Integer | Filter by priority | `1`, `2`, `3`, `4`, `5` |
| `dueDateFrom` | Date | Start of date range | `2025-10-01` |
| `dueDateTo` | Date | End of date range | `2025-12-31` |
| `search` | String | Search in title/description | `documentation` |
| `sortBy` | String | Sort field | `priority`, `dueDate` |
| `sortDirection` | String | Sort direction | `asc`, `desc` |

#### Examples

**Filter by status:**
```http
GET /api/v1/tasks?status=TODO
```

**Filter by priority:**
```http
GET /api/v1/tasks?priority=1
```

**Filter by date range:**
```http
GET /api/v1/tasks?dueDateFrom=2025-10-01&dueDateTo=2025-12-31
```

**Search tasks:**
```http
GET /api/v1/tasks?search=documentation
```

**Advanced multi-criteria filter:**
```http
GET /api/v1/tasks?status=TODO&priority=1&dueDateFrom=2025-10-01&sortBy=dueDate&sortDirection=asc
```

## 🧪 Testing

### Run all tests
```bash
./mvnw test
```

### Run specific test class
```bash
./mvnw test -Dtest=TaskServiceTest
```

### Test Coverage
- Service Layer: Complete coverage of CRUD operations and filtering
- Controller Layer: All endpoints tested with various scenarios
- Validation: Error handling and edge cases covered

## 📮 Postman Collection

Import the Postman collection for easy API testing:

1. Open Postman
2. Click **Import**
3. Select `Task-Management-API.postman_collection.json`
4. Collection includes 25 pre-configured requests

See [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md) for detailed instructions.

## 📊 Data Model

### Task Entity

```java
{
    "id": "string",              // MongoDB ObjectId
    "title": "string",           // Required, 1-200 characters
    "description": "string",     // Optional, max 1000 characters
    "status": "enum",            // TODO, IN_PROGRESS, DONE
    "priority": "integer",       // 1-5 (1 is highest priority)
    "dueDate": "date"           // Optional, ISO format
}
```

### Task Status Values
- `TODO` - Task not started
- `IN_PROGRESS` - Task is being worked on
- `DONE` - Task completed

### Priority Levels
- `1` - Highest priority (critical)
- `2` - High priority
- `3` - Medium priority
- `4` - Low priority
- `5` - Lowest priority

## 🔒 Validation Rules

- **Title**: Required, 1-200 characters
- **Description**: Optional, max 1000 characters
- **Status**: Required, must be valid enum value
- **Priority**: Required, must be between 1-5
- **DueDate**: Optional, must be valid date

## ⚠️ Error Responses

### 400 Bad Request - Validation Error
```json
{
    "timestamp": "2025-10-04T10:30:00",
    "status": 400,
    "error": "Validation Failed",
    "message": "Input validation failed",
    "validationErrors": {
        "title": "Title is required",
        "priority": "Priority must be between 1 and 5"
    }
}
```

### 404 Not Found
```json
{
    "timestamp": "2025-10-04T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Task not found with id: xyz"
}
```

### 500 Internal Server Error
```json
{
    "timestamp": "2025-10-04T10:30:00",
    "status": 500,
    "error": "Internal Server Error",
    "message": "An unexpected error occurred"
}
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/haithem/taskmanagemnt/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Database access
│   │   ├── model/             # Domain entities
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── exception/         # Exception handling
│   │   └── TaskManagemntApplication.java
│   └── resources/
│       └── application.yaml   # Configuration
└── test/
    └── java/com/haithem/taskmanagemnt/
        ├── controller/        # Controller tests
        └── service/           # Service tests
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Haithem Mihoubi**
- Email: haithemmihoubi1234@gmail.com
- GitHub: [@haithemmihoubi](https://github.com/haithemmihoubi)



**Built with using Spring Boot and MongoDB**

