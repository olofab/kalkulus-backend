package com.timla.dto

data class SendOfferEmailResponse(
    val success: Boolean,
    val message: String,
    val emailSentTo: String? = null
)
