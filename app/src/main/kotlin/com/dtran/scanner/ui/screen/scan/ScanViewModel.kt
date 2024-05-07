package com.dtran.scanner.ui.screen.scan

import androidx.lifecycle.ViewModel
import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow

class ScanViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    fun getItem(): Flow<Status<List<String>>> = firebaseRepository.getItem()
}