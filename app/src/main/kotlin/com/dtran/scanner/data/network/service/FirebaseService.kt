package com.dtran.scanner.data.network.service

import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.model.Item
import com.dtran.scanner.data.network.model.User
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.flow.Flow

interface FirebaseService {
    fun login(email: String, password: String): Flow<Status<User>>

    fun uploadImage(label: String, byteArray: ByteArray): Flow<Status<StorageMetadata>>

    fun getList(): Flow<Status<List<Item>>>

    fun getItem(): Flow<Status<List<String>>>

    fun removeItem(item: Item): Flow<Status<Nothing>>

    fun register(email: String, password: String): Flow<Status<Nothing>>
}