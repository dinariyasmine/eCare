package com.example.data.model

// Request for initiating password reset and sending OTP
data class PasswordResetRequestModel(
    val email: String
)

// Request for verifying the OTP code
data class OtpVerificationModel(
    val email: String,
    val otp_code: String
)

// Request for resetting the password with OTP
data class PasswordResetModel(
    val email: String,
    val otp_code: String,
    val password: String,
    val password2: String
)

// Response models
data class MessageResponse(
    val message: String
)