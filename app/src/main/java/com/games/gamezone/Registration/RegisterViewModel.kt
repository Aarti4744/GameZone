package com.games.gamezone.Registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, age: String, email: String, phone: String, password: String, confirmPassword: String) {
        // Validation
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name is required")
            return
        }

        if (age.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Age is required")
            return
        }

        val ageInt = age.toIntOrNull()
        if (ageInt == null || ageInt <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid age")
            return
        }

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email is required")
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address")
            return
        }

        if (phone.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Phone number is required")
            return
        }

        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password is required")
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val request = RegisterRequest(
                name = name.trim(),
                age = ageInt,
                email = email.trim(),
                number = phone.trim(),
                password = password,
                confirm_password = confirmPassword
            )

            repository.registerUser(request).fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, isSuccess = false)
    }
}