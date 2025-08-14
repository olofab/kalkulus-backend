package com.timla.dto

data class RegisterCompanyRequest(
    val name: String,
    val email: String,
    val password: String,
    val companyName: String,
    val organizationNumber: String,
    val industry: String
)
