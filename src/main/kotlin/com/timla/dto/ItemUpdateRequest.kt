package com.timla.dto

import jakarta.validation.constraints.Min

data class ItemUpdateRequest(
    @field:Min(1, message = "Antall må være minst 1")
    val quantity: Int
)
