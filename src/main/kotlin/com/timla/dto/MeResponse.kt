package com.timla.dto

import com.timla.model.Company

data class MeResponse(
    val user: UserResponse,
    val company: Company
)