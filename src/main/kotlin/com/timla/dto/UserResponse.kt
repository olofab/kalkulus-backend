package com.timla.dto

import com.timla.model.UserType

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val companyId: Long,
    val userType: UserType,
    val isAdmin: Boolean
)