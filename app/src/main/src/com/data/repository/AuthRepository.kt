package com.data.repository

import com.data.security.TokenManager
import com.util.NetworkResult

class AuthRepository(
    private val tokenManager: TokenManager
) {
    suspend fun register(email: String, password: String, name: String? = null): NetworkResult<Unit> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return NetworkResult.Error(
                    message = "Email và mật khẩu không được để trống",
                    type = NetworkResult.ErrorType.CLIENT
                )
            }

            saveLocalSession(userId = email.trim())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Registration error: ${e.message}",
                type = NetworkResult.ErrorType.CLIENT
            )
        }
    }

    suspend fun login(email: String, password: String): NetworkResult<Unit> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                return NetworkResult.Error(
                    message = "Email và mật khẩu không được để trống",
                    type = NetworkResult.ErrorType.CLIENT
                )
            }

            saveLocalSession(userId = email.trim())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Login error: ${e.message}",
                type = NetworkResult.ErrorType.CLIENT
            )
        }
    }

    suspend fun loginWithOAuth(
        provider: String,
        idToken: String?,
        accessToken: String?,
        email: String,
        name: String? = null
    ): NetworkResult<Unit> {
        return try {
            val userId = email.trim().ifBlank { provider.trim().ifBlank { "oauth_user" } }
            saveLocalSession(userId = userId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "OAuth login error: ${e.message}",
                type = NetworkResult.ErrorType.CLIENT
            )
        }
    }

    suspend fun requestPhoneOtp(phone: String): NetworkResult<Unit> {
        return if (phone.isBlank()) {
            NetworkResult.Error(
                message = "Số điện thoại không được để trống",
                type = NetworkResult.ErrorType.CLIENT
            )
        } else {
            NetworkResult.Success(Unit)
        }
    }

    suspend fun verifyPhoneOtp(phone: String, otp: String, name: String? = null): NetworkResult<Unit> {
        return try {
            if (phone.isBlank()) {
                return NetworkResult.Error(
                    message = "Số điện thoại không được để trống",
                    type = NetworkResult.ErrorType.CLIENT
                )
            }

            if (otp.length != 6 || !otp.all { it.isDigit() }) {
                return NetworkResult.Error(
                    message = "OTP không hợp lệ",
                    type = NetworkResult.ErrorType.CLIENT
                )
            }

            saveLocalSession(userId = phone.trim())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "OTP verification error: ${e.message}",
                type = NetworkResult.ErrorType.CLIENT
            )
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }

    fun hasValidToken(): Boolean {
        return tokenManager.hasToken() && !tokenManager.isAccessTokenExpired()
    }

    private fun saveLocalSession(userId: String) {
        tokenManager.saveAccessToken("local_access_token_$userId")
        tokenManager.saveRefreshToken("local_refresh_token_$userId")

        // Lưu hạn đăng nhập 30 ngày.
        val thirtyDaysInSeconds = 30L * 24L * 60L * 60L
        tokenManager.saveTokenExpiry(thirtyDaysInSeconds)

        tokenManager.saveUserId(userId)
    }
}