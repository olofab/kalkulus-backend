package com.timla.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendOfferEmailRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    val subject: String? = null,
    val message: String? = null
)
