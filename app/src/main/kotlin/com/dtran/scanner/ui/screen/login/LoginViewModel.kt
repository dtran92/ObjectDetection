package com.dtran.scanner.ui.screen.login

import androidx.lifecycle.ViewModel
import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.model.User
import com.dtran.scanner.data.network.service.FirebaseService
import kotlinx.coroutines.flow.Flow

class LoginViewModel(private val authenticationRepository: FirebaseService) : ViewModel() {
    fun login(email: String, password: String): Flow<Status<User>> {
        return authenticationRepository.login(email, password)
    }

    fun register(email: String, password: String): Flow<Status<Nothing>> {
        return authenticationRepository.register(email, password)
    }
}