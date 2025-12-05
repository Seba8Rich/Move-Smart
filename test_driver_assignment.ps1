# Test script for driver assignment to bus
# Make sure the Spring Boot application is running on http://localhost:8080

$baseUrl = "http://localhost:8080/api"

Write-Host "=== Testing Driver Assignment to Bus ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Get admin token (assuming you have an admin user)
Write-Host "Step 1: Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    userEmail = "admin@example.com"  # Replace with your admin email
    userPassword = "Admin123"        # Replace with your admin password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $adminToken = $loginResponse.token
    Write-Host "✓ Admin login successful" -ForegroundColor Green
} catch {
    Write-Host "✗ Admin login failed. Please create an admin first or update credentials." -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $adminToken"
    "Content-Type" = "application/json"
}

# Step 2: Get all buses to find an available bus
Write-Host ""
Write-Host "Step 2: Fetching all buses..." -ForegroundColor Yellow
try {
    $busesResponse = Invoke-RestMethod -Uri "$baseUrl/buses" -Method GET -Headers $headers
    Write-Host "✓ Found $($busesResponse.Count) bus(es)" -ForegroundColor Green
    
    # Find a bus without a driver
    $availableBus = $busesResponse | Where-Object { $null -eq $_.driver } | Select-Object -First 1
    
    if ($null -eq $availableBus) {
        Write-Host "No available bus found. Creating a new bus..." -ForegroundColor Yellow
        
        # Create a new bus
        $newBusBody = @{
            plateNumber = "TEST" + (Get-Random -Minimum 100 -Maximum 999) + "X"
            capacity = 50
            route = "Test Route"
        } | ConvertTo-Json
        
        $newBus = Invoke-RestMethod -Uri "$baseUrl/buses" -Method POST -Body $newBusBody -Headers $headers
        $availableBus = $newBus
        Write-Host "✓ Created new bus with ID: $($newBus.id)" -ForegroundColor Green
    } else {
        Write-Host "✓ Found available bus with ID: $($availableBus.id), Plate: $($availableBus.plateNumber)" -ForegroundColor Green
    }
    
    $busId = $availableBus.id
} catch {
    Write-Host "✗ Failed to fetch/create bus" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Register a driver and assign to the bus
Write-Host ""
Write-Host "Step 3: Registering driver and assigning to bus ID: $busId..." -ForegroundColor Yellow
$driverBody = @{
    userName = "TestDriver" + (Get-Random -Minimum 100 -Maximum 999)
    userEmail = "driver" + (Get-Random -Minimum 1000 -Maximum 9999) + "@test.com"
    userPhoneNumber = "+250" + (Get-Random -Minimum 700000000 -Maximum 799999999)
    userPassword = "Driver123"
    busId = $busId
} | ConvertTo-Json

try {
    $driverResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register/driver" -Method POST -Body $driverBody -ContentType "application/json"
    Write-Host "✓ Driver registered successfully!" -ForegroundColor Green
    Write-Host "  Token: $($driverResponse.token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Driver registration failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}

# Step 4: Verify the driver is assigned to the bus
Write-Host ""
Write-Host "Step 4: Verifying driver assignment..." -ForegroundColor Yellow
try {
    $busesAfter = Invoke-RestMethod -Uri "$baseUrl/buses" -Method GET -Headers $headers
    $updatedBus = $busesAfter | Where-Object { $_.id -eq $busId } | Select-Object -First 1
    
    if ($null -ne $updatedBus -and $null -ne $updatedBus.driver) {
        Write-Host "✓ SUCCESS! Driver is assigned to the bus!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Bus Details:" -ForegroundColor Cyan
        Write-Host "  ID: $($updatedBus.id)" -ForegroundColor White
        Write-Host "  Plate Number: $($updatedBus.plateNumber)" -ForegroundColor White
        Write-Host "  Route: $($updatedBus.route)" -ForegroundColor White
        Write-Host ""
        Write-Host "Driver Details:" -ForegroundColor Cyan
        Write-Host "  User ID: $($updatedBus.driver.userId)" -ForegroundColor White
        Write-Host "  Name: $($updatedBus.driver.userName)" -ForegroundColor White
        Write-Host "  Email: $($updatedBus.driver.userEmail)" -ForegroundColor White
        Write-Host "  Phone: $($updatedBus.driver.userPhoneNumber)" -ForegroundColor White
        Write-Host "  Role: $($updatedBus.driver.userRole)" -ForegroundColor White
    } else {
        Write-Host "✗ FAILED! Driver is NOT assigned to the bus." -ForegroundColor Red
        Write-Host "Bus driver field: $($updatedBus.driver)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Failed to verify driver assignment" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Test Completed Successfully ===" -ForegroundColor Green

