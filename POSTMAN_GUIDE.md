# Task Management API v1 - Postman Collection Guide

## 🎉 What's Been Done

### 1. ✅ API Versioning Added
All APIs now use `/api/v1/tasks` instead of `/api/tasks`

**Updated Base URL:** `http://localhost:8080/api/v1/tasks`

### 2. ✅ Complete Postman Collection Created
File: `Task-Management-API.postman_collection.json`

---

## 📦 Postman Collection Contents

### **Total Requests: 25**

The collection includes:

### 📁 **Tasks Folder (21 requests)**

#### **CRUD Operations:**
1. ✅ **Create Task** - Basic task creation
2. ✅ **Create Task - High Priority** - Priority 1 example
3. ✅ **Create Task - Medium Priority** - Priority 3 example
4. ✅ **Create Task - Low Priority** - Priority 5 example
5. ✅ **Get All Tasks** - Retrieve all tasks
6. ✅ **Get Task by ID** - Get specific task
7. ✅ **Update Task** - Modify existing task
8. ✅ **Update Task - Mark as Done** - Change status to DONE
9. ✅ **Update Task - Change Priority** - Update priority
10. ✅ **Delete Task** - Remove task

#### **Filtering Examples:**
11. ✅ **Filter by Status - TODO**
12. ✅ **Filter by Status - IN_PROGRESS**
13. ✅ **Filter by Status - DONE**
14. ✅ **Filter by Priority - High (1)**
15. ✅ **Filter by Priority - Medium (3)**
16. ✅ **Filter by Date Range**
17. ✅ **Search by Keyword** - Case-insensitive search
18. ✅ **Sort by Priority - Ascending**
19. ✅ **Sort by Due Date - Descending**
20. ✅ **Advanced Filter - Multiple Criteria**
21. ✅ **Advanced Filter - Search + Status + Sort**

### 📁 **Validation Examples Folder (4 requests)**

22. ✅ **Invalid - Missing Title** (400 error example)
23. ✅ **Invalid - Priority Out of Range** (400 error example)
24. ✅ **Invalid - Title Too Long** (400 error example)
25. ✅ **Get Task - Not Found** (404 error example)

---

##   How to Import and Use

### **Step 1: Import to Postman**

#### Option A: Import File
1. Open Postman
2. Click **Import** button (top left)
3. Select **File** tab
4. Choose: `/Users/haithem/IdeaProjects/task-managemnt/Task-Management-API.postman_collection.json`
5. Click **Import**

#### Option B: Drag & Drop
1. Open Postman
2. Drag the `Task-Management-API.postman_collection.json` file into Postman window
3. Collection will be imported automatically

### **Step 2: Set Up Environment (Optional but Recommended)**

Create a new environment in Postman:
1. Click **Environments** (left sidebar)
2. Click **+** to create new environment
3. Name it: `Task Management Local`
4. Add variables:
   - `baseUrl` = `http://localhost:8080`
   - `taskId` = (leave empty - will be auto-populated)
5. Save and select the environment

### **Step 3: Start Your Application**

```bash
cd /Users/haithem/IdeaProjects/task-managemnt
./mvnw spring-boot:run
```

Wait until you see: `Started TaskManagemntApplication`

### **Step 4: Test the APIs**

1. Open the **Task Management API v1** collection
2. Navigate to **Tasks** → **Create Task**
3. Click **Send**
4. The `taskId` will be automatically saved for subsequent requests

---

## 📋 Collection Features

### ✨ **Automatic Task ID Management**
- When you create a task, the ID is automatically saved to `{{taskId}}`
- All update/delete requests use this variable
- No need to manually copy-paste IDs

### 📝 **Pre-filled Request Bodies**
All POST/PUT requests include realistic example data:

**Example Create Task Request:**
```json
{
    "title": "Complete Project Documentation",
    "description": "Write comprehensive documentation for the task management API",
    "status": "TODO",
    "priority": 1,
    "dueDate": "2025-12-31"
}
```

### 🔍 **Comprehensive Filter Examples**
- Single filters (status, priority, date)
- Combined filters (status + priority + dates)
- Search functionality
- Sorting options
- Real-world use cases

### ⚠️ **Error Handling Examples**
Test validation and error scenarios:
- Missing required fields
- Invalid priority values
- Field length violations
- 404 Not Found cases

---

## 📊 Quick Test Workflow

### **Basic CRUD Test:**
1. **Tasks** → **Create Task** → Send
2. **Tasks** → **Get All Tasks** → Send
3. **Tasks** → **Get Task by ID** → Send
4. **Tasks** → **Update Task** → Send
5. **Tasks** → **Delete Task** → Send

### **Advanced Filtering Test:**
1. Create 3-4 tasks with different statuses and priorities
2. Test **Filter by Status - TODO**
3. Test **Filter by Priority - High (1)**
4. Test **Search by Keyword**
5. Test **Advanced Filter - Multiple Criteria**

### **Validation Test:**
1. Try **Validation Examples** → **Invalid - Missing Title**
2. Check error response format
3. Verify 400 Bad Request status

---

## 🎯 All API Endpoints

### **Base URL:** `http://localhost:8080/api/v1/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks` | Create new task |
| GET | `/api/v1/tasks` | Get all tasks (with filters) |
| GET | `/api/v1/tasks/{id}` | Get task by ID |
| PUT | `/api/v1/tasks/{id}` | Update task |
| DELETE | `/api/v1/tasks/{id}` | Delete task |

### **Query Parameters for GET /api/v1/tasks:**
- `status` - Filter by status (TODO, IN_PROGRESS, DONE)
- `priority` - Filter by priority (1-5)
- `dueDateFrom` - Start date (ISO format: 2025-10-01)
- `dueDateTo` - End date (ISO format: 2025-12-31)
- `search` - Search keyword (title/description)
- `sortBy` - Sort field (dueDate, priority)
- `sortDirection` - Sort direction (asc, desc)

---

## 📖 Example API Calls

### **1. Create a High Priority Task**
```bash
POST http://localhost:8080/api/v1/tasks
Content-Type: application/json

{
    "title": "Critical Bug Fix",
    "description": "Fix production issue",
    "status": "IN_PROGRESS",
    "priority": 1,
    "dueDate": "2025-10-05"
}
```

### **2. Get TODO Tasks with Priority 1**
```bash
GET http://localhost:8080/api/v1/tasks?status=TODO&priority=1
```

### **3. Search for Tasks**
```bash
GET http://localhost:8080/api/v1/tasks?search=documentation
```

### **4. Get Tasks in Date Range, Sorted**
```bash
GET http://localhost:8080/api/v1/tasks?dueDateFrom=2025-10-01&dueDateTo=2025-12-31&sortBy=priority&sortDirection=asc
```

---

## 🔧 Troubleshooting

### **Collection Variables Not Working?**
- Ensure you've selected the environment
- Check that `baseUrl` is set to `http://localhost:8080`

### **404 Errors?**
- Verify API path includes `/v1`: `/api/v1/tasks`
- Check that Spring Boot app is running

### **Task ID Not Auto-Saving?**
- The first "Create Task" request has a test script
- Make sure to use that specific request to create tasks
- Check the **Tests** tab to see the auto-save script

### **Connection Refused?**
- Ensure Spring Boot app is running
- Check it's running on port 8080
- Verify MongoDB connection is working

---

##  Ready to Use!

Your Postman collection is complete with:
- ✅ All 25 API requests
- ✅ Realistic sample data
- ✅ Automatic variable management
- ✅ Error handling examples
- ✅ Advanced filtering scenarios
- ✅ Full documentation

**Next Steps:**
1. Import the collection to Postman
2. Start your Spring Boot application
3. Start testing your APIs!

Enjoy your Task Management API! 🚀

