package com.movesmart.demo.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val message = ex.message ?: "Invalid input"
        val status = if (message.contains("Invalid credentials", ignoreCase = true)) {
            HttpStatus.UNAUTHORIZED
        } else {
            HttpStatus.BAD_REQUEST
        }
        
        return buildErrorResponse(status, message)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<Map<String, String>> {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials")
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<Map<String, String>> {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.message ?: "Invalid state")
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found")
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ResponseEntity<Map<String, String>> {
        val requestPath = ex.requestURL?.toString() ?: "unknown"
        val message = buildEndpointNotFoundMessage(requestPath)
        return buildErrorResponse(HttpStatus.NOT_FOUND, message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        val message = buildInvalidRequestBodyMessage(ex)
        val responseBody = mutableMapOf<String, Any>(
            "error" to "Bad Request",
            "message" to message
        )
        
        if (isRegistrationEndpoint(ex)) {
            addRegistrationHelpInfo(responseBody)
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val errorMessage = ex.message ?: "An unexpected error occurred"
        val (status, message) = determineErrorStatusAndMessage(errorMessage)
        return buildErrorResponse(status, message)
    }
    
    private fun buildErrorResponse(status: HttpStatus, message: String): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(status)
            .body(mapOf("error" to status.reasonPhrase, "message" to message))
    }
    
    private fun buildEndpointNotFoundMessage(requestPath: String): String {
        return when {
            isGoogleMapsEndpoint(requestPath) -> {
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
    }
    
    private fun buildInvalidRequestBodyMessage(ex: HttpMessageNotReadableException): String {
        return when (val cause = ex.cause) {
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
    }
    
    private fun isRegistrationEndpoint(ex: HttpMessageNotReadableException): Boolean {
        val message = ex.message ?: ""
        return message.contains("register", ignoreCase = true) ||
               message.contains("RegisterRequest", ignoreCase = true)
    }
    
    private fun addRegistrationHelpInfo(responseBody: MutableMap<String, Any>) {
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
    
    private fun isGoogleMapsEndpoint(path: String): Boolean {
        return path.contains("google-maps", ignoreCase = true) ||
               path.contains("geocode", ignoreCase = true)
    }
    
    private fun determineErrorStatusAndMessage(errorMessage: String): Pair<HttpStatus, String> {
        return when {
            errorMessage.contains("static resource", ignoreCase = true) -> {
                val status = HttpStatus.NOT_FOUND
                val message = if (isGoogleMapsEndpoint(errorMessage)) {
                    "The endpoint was not found. Use /api/google-maps/geocode for geocoding. " +
                    "Example: POST /api/google-maps/geocode with body {\"address\": \"your address\"} or " +
                    "GET /api/google-maps/geocode?address=your+address"
                } else {
                    "The requested resource was not found. Available endpoints: /api/auth/**, /api/users/**, " +
                    "/api/buses/**, /api/routes/**, /api/passengerTrips/**, /api/organization/**, /api/bus-locations/**"
                }
                Pair(status, message)
            }
            else -> Pair(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage)
        }
    }
}

