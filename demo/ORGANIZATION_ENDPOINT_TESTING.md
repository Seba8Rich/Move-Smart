# Comprehensive Admin-Only Security Implementation Guide

## Overview
The entire API has been secured with JWT-based authentication and role-based authorization. Only users with the `ADMIN` role can access **ALL** endpoints except for authentication endpoints (`/api/auth/**`). This includes:

- `/api/organization` - Organization management
- `/api/users` - User management  
- `/api/buses` - Bus management
- `/api/routes` - Route management
- `/api/passengerTrips` - Passenger trip management
- `/api/bus-locations` - Bus location management

## Security Features Implemented

### 1. JWT Authentication
- All API endpoints (except `/api/auth/**`) require a valid JWT token in the `Authorization` header
- Token format: `Bearer <jwt_token>`

### 2. Role-Based Authorization
- **ADMIN role**: Can access ALL API endpoints (create, read, update, delete everything)
- **USER role**: Can only create, read, and update users (`/api/users` endpoints only)
- **USER role**: Gets `403 Forbidden` for all other endpoints (organizations, buses, routes, trips, locations)
- Method-level security using `@PreAuthorize` annotations with role-based access control
- Only `/api/auth/**` endpoints remain public for registration and login

### 3. Updated User Roles
The system now supports the following user roles:
- `USER` - Normal user (can create, read, update users only; gets 403 Forbidden for all other endpoints)
- `ADMIN` - Administrator (can access ALL endpoints - create, read, update, delete everything)
- `PASSENGER` - Legacy role (gets 403 Forbidden for ALL endpoints)
- `DRIVER` - Legacy role (gets 403 Forbidden for ALL endpoints)

## Testing the Comprehensive Security Implementation

### Step 1: Create an Admin User
First, create a user with ADMIN role:

```bash
POST /api/auth/register
Content-Type: application/json

{
    "userName": "Admin User",
    "userEmail": "admin@example.com",
    "userPhoneNumber": "+1234567890",
    "userPassword": "admin123",
    "userRole": "ADMIN"
}
```

### Step 2: Create a Regular User
Create a user with USER role:

```bash
POST /api/auth/register
Content-Type: application/json

{
    "userName": "Regular User",
    "userEmail": "user@example.com",
    "userPhoneNumber": "+1234567891",
    "userPassword": "user123",
    "userRole": "USER"
}
```

### Step 3: Login as Admin
Login to get JWT token for admin:

```bash
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin@example.com",
    "password": "admin123"
}
```

Response will include JWT token:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Step 4: Test ALL Endpoints with Admin Token (Should Work)

#### Organization Management
```bash
# Create Organization
POST /api/organization
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"name": "Test Org", "address": "123 Main St", "contactNumber": "+1234567890", "email": "contact@testorg.com"}

# Get All Organizations
GET /api/organization
Authorization: Bearer <admin_jwt_token>

# Get Organization by ID
GET /api/organization/1
Authorization: Bearer <admin_jwt_token>

# Update Organization
PUT /api/organization/1
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"name": "Updated Org", "address": "456 New St", "contactNumber": "+1234567891", "email": "newcontact@testorg.com"}

# Delete Organization
DELETE /api/organization/1
Authorization: Bearer <admin_jwt_token>
```

#### User Management
```bash
# Create User
POST /api/users
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"userName": "New User", "userEmail": "newuser@example.com", "userPhoneNumber": "+1234567892", "userPassword": "password123", "userRole": "USER"}

# Get All Users
GET /api/users
Authorization: Bearer <admin_jwt_token>

# Get User by ID
GET /api/users/1
Authorization: Bearer <admin_jwt_token>

# Update User
PUT /api/users/1
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"userName": "Updated User", "userEmail": "updated@example.com", "userPhoneNumber": "+1234567893", "userPassword": "newpassword", "userRole": "USER"}

# Delete User
DELETE /api/users/1
Authorization: Bearer <admin_jwt_token>
```

#### Bus Management
```bash
# Create Bus
POST /api/buses
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"plateNumber": "ABC123", "capacity": 50, "route": "Route A"}

# Get All Buses
GET /api/buses
Authorization: Bearer <admin_jwt_token>

# Get Bus by ID
GET /api/buses/1
Authorization: Bearer <admin_jwt_token>

# Update Bus
PUT /api/buses/1
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"plateNumber": "XYZ789", "capacity": 60, "route": "Route B"}

# Delete Bus
DELETE /api/buses/1
Authorization: Bearer <admin_jwt_token>
```

#### Route Management
```bash
# Create Route
POST /api/routes
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"startStation": "Station A", "endStation": "Station B", "distanceKm": 10.5, "busId": 1}

# Get All Routes
GET /api/routes
Authorization: Bearer <admin_jwt_token>

# Get Route by ID
GET /api/routes/1
Authorization: Bearer <admin_jwt_token>

# Update Route
PUT /api/routes/1
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"startStation": "Station C", "endStation": "Station D", "distanceKm": 15.2, "busId": 1}

# Delete Route
DELETE /api/routes/1
Authorization: Bearer <admin_jwt_token>
```

#### Passenger Trip Management
```bash
# Create Passenger Trip
POST /api/passengerTrips
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"userId": 1, "routeId": 1, "busId": 1, "startStation": "Station A", "endStation": "Station B", "tripStatus": "ACTIVE"}

# Get All Passenger Trips
GET /api/passengerTrips
Authorization: Bearer <admin_jwt_token>

# Get Passenger Trip by ID
GET /api/passengerTrips/1
Authorization: Bearer <admin_jwt_token>

# Update Passenger Trip
PUT /api/passengerTrips/1
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"userId": 1, "routeId": 1, "busId": 1, "startStation": "Station C", "endStation": "Station D", "tripStatus": "COMPLETED"}

# Delete Passenger Trip
DELETE /api/passengerTrips/1
Authorization: Bearer <admin_jwt_token>
```

#### Bus Location Management
```bash
# Create Bus Location
POST /api/bus-locations
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
{"latitude": 40.7128, "longitude": -74.0060, "timestamp": "2024-01-01T12:00:00Z"}
```

### Step 5: Test with Regular User Token (Mixed Permissions)

#### Login as Regular User
```bash
POST /api/auth/login
Content-Type: application/json
{
    "username": "user@example.com",
    "password": "user123"
}
```

#### Test USER endpoints (Should Work - 200/201 OK)
```bash
# Create User (USER role can do this)
POST /api/users
Authorization: Bearer <user_jwt_token>
Content-Type: application/json
{"userName": "New User by User", "userEmail": "newuserbyuser@example.com", "userPhoneNumber": "+1234567892", "userPassword": "password123", "userRole": "USER"}

# Get All Users (USER role can do this)
GET /api/users
Authorization: Bearer <user_jwt_token>

# Get User by ID (USER role can do this)
GET /api/users/1
Authorization: Bearer <user_jwt_token>

# Update User (USER role can do this)
PUT /api/users/1
Authorization: Bearer <user_jwt_token>
Content-Type: application/json
{"userName": "Updated by User", "userEmail": "updatedbyuser@example.com", "userPhoneNumber": "+1234567893", "userPassword": "newpassword", "userRole": "USER"}
```

#### Test NON-USER endpoints (Should Get 403 Forbidden)
```bash
# Try Organization endpoint
POST /api/organization
Authorization: Bearer <user_jwt_token>
Content-Type: application/json
{"name": "Unauthorized Org", "address": "789 Unauthorized St", "contactNumber": "+1234567892", "email": "unauthorized@example.com"}

# Try Bus endpoint
GET /api/buses
Authorization: Bearer <user_jwt_token>

# Try Route endpoint
GET /api/routes
Authorization: Bearer <user_jwt_token>

# Try Passenger Trip endpoint
GET /api/passengerTrips
Authorization: Bearer <user_jwt_token>

# Try Bus Location endpoint
POST /api/bus-locations
Authorization: Bearer <user_jwt_token>
Content-Type: application/json
{"latitude": 40.7128, "longitude": -74.0060, "timestamp": "2024-01-01T12:00:00Z"}

# Try to Delete User (USER role cannot do this)
DELETE /api/users/1
Authorization: Bearer <user_jwt_token>
```

**Expected Response for NON-USER endpoints (403 Forbidden):**
```json
{
    "timestamp": "2024-01-01T12:00:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/api/[endpoint]"
}
```

**Expected Response for USER endpoints (200/201 OK):**
```json
{
    "userId": 1,
    "userName": "Updated by User",
    "userEmail": "updatedbyuser@example.com",
    "userPhoneNumber": "+1234567893",
    "userRole": "USER"
}
```

### Step 6: Test Without Token (Should Get 401 Unauthorized for ALL)

```bash
# Try ANY endpoint without Authorization header
POST /api/organization
Content-Type: application/json
{"name": "No Token Org", "address": "999 No Token St", "contactNumber": "+1234567893", "email": "notoken@example.com"}

GET /api/users
GET /api/buses
GET /api/routes
GET /api/passengerTrips
POST /api/bus-locations
```

**Expected Response for ALL endpoints:**
```json
{
    "timestamp": "2024-01-01T12:00:00.000+00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource",
    "path": "/api/[endpoint]"
}
```

## Complete Endpoint Summary

| Endpoint | Method | Admin Access | User Access | No Token |
|----------|--------|--------------|-------------|----------|
| `/api/organization` | POST/GET/PUT/DELETE | ✅ 201/200 OK | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/users` | POST/GET/PUT | ✅ 201/200 OK | ✅ 201/200 OK | ❌ 401 Unauthorized |
| `/api/users` | DELETE | ✅ 200 OK | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/buses` | POST/GET/PUT/DELETE | ✅ 201/200 OK | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/routes` | POST/GET/PUT/DELETE | ✅ 201/200 OK | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/passengerTrips` | POST/GET/PUT/DELETE | ✅ 201/200 OK | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/bus-locations` | POST | ✅ 201 Created | ❌ 403 Forbidden | ❌ 401 Unauthorized |
| `/api/auth/**` | POST | ✅ Open to All | ✅ Open to All | ✅ Open to All |

### Permission Summary:
- **ADMIN**: Full access to ALL endpoints (create, read, update, delete everything)
- **USER**: Can only create, read, and update users; delete users and all other operations are forbidden
- **PASSENGER/DRIVER**: No access to any endpoints (403 Forbidden)
- **No Token**: No access to any endpoints (401 Unauthorized)

## Security Implementation Details

### JWT Token Structure
The JWT token now includes role information in the claims:
```json
{
  "sub": "admin@example.com",
  "role": "ADMIN",
  "iat": 1640995200,
  "exp": 1641081600
}
```

### Authorization Flow
1. User authenticates via `/api/auth/login` or `/api/auth/register`
2. JWT token is generated with role claims
3. Token is included in Authorization header for protected endpoints
4. Spring Security validates token and extracts user details
5. Method-level security checks user's role against `@PreAuthorize` annotations
6. Access granted only if user has `ADMIN` role

### Key Files Modified
- `User.kt` - Added ADMIN and USER roles
- `AuthController.kt` - Fixed JWT token generation with role claims
- `UserService.kt` - Added method to find user by email or phone
- `Organization.kt` - Updated entity with proper ID generation
- `OrganizationController.kt` - Added `@PreAuthorize` annotations
- `SecurityConfig.kt` - Enabled method-level security

This implementation ensures that only authenticated users with ADMIN role can manage organizations, while regular users are properly restricted with 403 Forbidden responses.
