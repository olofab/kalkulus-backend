package com.timla.dto
import com.timla.model.UserType

data class CreateUserRequest(
    val email: String,
    val name: String,
    val password: String,
    val userType: UserType
)