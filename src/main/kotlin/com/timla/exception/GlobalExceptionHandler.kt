package com.timla.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.timla.dto.ErrorResponse
import com.timla.model.OfferStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): Map<String, String> {
        return ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Ugyldig verdi")
        }
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NoSuchElementException): Map<String, String> {
      return mapOf("error" to (ex.message ?: "Ikke funnet"))
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidEnumValue(ex: InvalidFormatException): ResponseEntity<ErrorResponse> {
        if (ex.targetType == OfferStatus::class.java) {
            val validValues = OfferStatus.entries.joinToString(", ")
            val errorResponse = ErrorResponse(
                error = "INVALID_STATUS",
                message = "Status '${ex.value}' er ikke gyldig. Gyldige verdier: [$validValues]"
            )
            return ResponseEntity.badRequest().body(errorResponse)
        }
        
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                error = "INVALID_FORMAT",
                message = "Ugyldig format for verdi: ${ex.value}"
            )
        )
    }

}
