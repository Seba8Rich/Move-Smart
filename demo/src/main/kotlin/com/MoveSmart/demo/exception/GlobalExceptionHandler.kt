package com.movesmart.demo.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Invalid input")))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(mapOf("error" to "Conflict", "message" to (ex.message ?: "Invalid state")))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "Not Found", "message" to (ex.message ?: "Resource not found")))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ResponseEntity<Map<String, String>> {
        val requestPath = ex.requestURL?.toString() ?: "unknown"
        val message = when {
            requestPath.contains("google-maps", ignoreCase = true) || 
            requestPath.contains("geocode", ignoreCase = true) -> {
                "The endpoint '$requestPath' was not found. " +
                "Use /api/google-maps/geocode for geocoding requests. " +
                "Example: POST /api/google-maps/geocode with body {\"address\": \"your address\"} or " +
                "GET /api/google-maps/geocode?address=your+address"
            }
            else -> {
                "The endpoint '$requestPath' was not found. " +
                "Available endpoints: /api/auth/**, /api/users/**, /api/buses/**, /api/routes/**, " +
                "/api/passengerTrips/**, /api/organization/**, /api/bus-locations/**, /api/google-maps/**"
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "Not Found", "message" to message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        val cause = ex.cause
        val message = when (cause) {
            is InvalidFormatException -> {
                val fieldName = cause.path.firstOrNull()?.fieldName ?: "field"
                val value = cause.value
                val targetType = cause.targetType?.simpleName ?: "unknown type"
                "Invalid value '$value' for field '$fieldName'. Expected type: $targetType. " +
                        "Please check that all numeric fields (id, userId, routeId, busId) are sent as numbers, not strings."
            }
            else -> {
                val originalMessage = ex.message ?: "Invalid request body"
                "Invalid request body format. $originalMessage"
            }
        }
        
        // Check if this is a registration endpoint
        val isRegistrationEndpoint = ex.message?.contains("register", ignoreCase = true) == true ||
                                    ex.message?.contains("RegisterRequest", ignoreCase = true) == true
        
        val responseBody = mutableMapOf<String, Any>(
            "error" to "Bad Request",
            "message" to message
        )
        
        if (isRegistrationEndpoint) {
            responseBody["expectedFields"] = mapOf(
                "userName" to "String (required) - User's full name",
                "userPhoneNumber" to "String (required) - Phone number as string, e.g. '+1234567890'",
                "userPassword" to "String (required) - Password, minimum 6 characters",
                "userEmail" to "String (optional) - Email address"
            )
            responseBody["example"] = mapOf(
                "userName" to "John Passenger",
                "userEmail" to "john@example.com",
                "userPhoneNumber" to "+1234567890",
                "userPassword" to "password123"
            )
            responseBody["commonMistakes"] = listOf(
                "Using 'name' instead of 'userName'",
                "Using 'phoneNumber' instead of 'userPhoneNumber'",
                "Sending phoneNumber as number instead of string",
                "Missing required field 'userPassword'",
                "Including unexpected fields like 'id', 'location'"
            )
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val errorMessage = ex.message ?: "An unexpected error occurred"
        val status = when {
            errorMessage.contains("static resource", ignoreCase = true) -> {
                if (errorMessage.contains("google-maps", ignoreCase = true) || 
                    errorMessage.contains("geocode", ignoreCase = true)) {
                    HttpStatus.NOT_FOUND
                } else {
                    HttpStatus.NOT_FOUND
                }
            }
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        
        val message = when {
            errorMessage.contains("static resource", ignoreCase = true) && 
            (errorMessage.contains("google-maps", ignoreCase = true) || 
             errorMessage.contains("geocode", ignoreCase = true)) -> {
                "The endpoint was not found. Use /api/google-maps/geocode for geocoding. " +
                "Example: POST /api/google-maps/geocode with body {\"address\": \"your address\"} or " +
                "GET /api/google-maps/geocode?address=your+address"
            }
            errorMessage.contains("static resource", ignoreCase = true) -> {
                "The requested resource was not found. Available endpoints: /api/auth/**, /api/users/**, " +
                "/api/buses/**, /api/routes/**, /api/passengerTrips/**, /api/organization/**, /api/bus-locations/**"
            }
            else -> errorMessage
        }
        
        return ResponseEntity.status(status)
            .body(mapOf("error" to status.reasonPhrase, "message" to message))
    }
}

