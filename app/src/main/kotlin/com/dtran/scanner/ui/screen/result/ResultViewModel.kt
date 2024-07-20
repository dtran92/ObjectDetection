package com.dtran.scanner.ui.screen.result

import androidx.lifecycle.ViewModel
import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.service.FirebaseService
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.flow.Flow

class ResultViewModel(private val firebaseRepository: FirebaseService) : ViewModel() {
    fun uploadPhoto(label: String, byteArray: ByteArray): Flow<Status<StorageMetadata>> {
        return firebaseRepository.uploadImage(label, byteArray)
    }
}