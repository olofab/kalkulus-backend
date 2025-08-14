package com.timla.dto
import com.timla.model.UserType

data class UpdateUserRequest(
    val email: String,
    val name: String,
    val userType: UserType
)