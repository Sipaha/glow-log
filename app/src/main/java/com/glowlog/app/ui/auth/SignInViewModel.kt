package com.glowlog.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowlog.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasError: Boolean = false,
    val isSignedIn: Boolean = false
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading, error = null, hasError = false)
    }

    fun setError(message: String) {
        _uiState.value = SignInUiState(error = message, hasError = true)
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState(isLoading = true)
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                _uiState.value = SignInUiState(isSignedIn = true)
            } else {
                _uiState.value = SignInUiState(
                    error = result.exceptionOrNull()?.message,
                    hasError = true
                )
            }
        }
    }
}
