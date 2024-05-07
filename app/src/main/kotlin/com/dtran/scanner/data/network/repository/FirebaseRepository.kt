package com.dtran.scanner.data.network.repository

import android.util.Log
import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.model.Item
import com.dtran.scanner.data.network.model.User
import com.dtran.scanner.data.network.service.FirebaseService
import com.dtran.scanner.util.Constant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.time.Instant

class FirebaseRepository : FirebaseService {
    override fun login(email: String, password: String): Flow<Status<User>> = flow {
        emit(Status.Loading())
        val task = Firebase.auth.signInWithEmailAndPassword(email, password).await()
        emit(Status.Success(data = User(task.user?.uid ?: "", email = task.user?.email ?: email)))
    }.catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message ?: ""))
    }

    override fun uploadImage(label: String, byteArray: ByteArray): Flow<Status<StorageMetadata>> = flow {
        emit(Status.Loading())
        val newId = Instant.now().toString()
        val imgRef = Firebase.storage.reference.child("${Firebase.auth.currentUser?.uid}/$newId")
        val uploadTask = imgRef.putBytes(byteArray).await()
        Firebase.firestore.collection(Constant.USER_COLLECTION).document(Firebase.auth.currentUser?.uid ?: "").update(
            Constant.ITEMS_FIELD,
            FieldValue.arrayUnion(Item(text = label, url = imgRef.downloadUrl.await().toString(), id = newId))
        ).await()
        emit(Status.Success(data = uploadTask.metadata))
    }.catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message ?: ""))
    }

    override fun getList(): Flow<Status<List<Item>>> = flow {
        emit(Status.Loading())
        val getDataTask =
            Firebase.firestore.collection(Constant.USER_COLLECTION).document(Firebase.auth.currentUser?.uid ?: "").get()
                .await()
        val itemList = getDataTask.get(Constant.ITEMS_FIELD) as List<*>?
        val checkedList = itemList?.filterIsInstance<HashMap<String, String>>()?.map {
            Item(
                text = it[Constant.ITEM_TEXT] ?: "", url = it[Constant.ITEM_URL] ?: "", id = it[Constant.ITEM_ID] ?: ""
            )
        }
        println(checkedList)
        emit(Status.Success(data = checkedList ?: emptyList()))
    }.flowOn(Dispatchers.IO).catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message ?: ""))
    }

    override fun getItem(): Flow<Status<List<String>>> = flow {
        emit(Status.Loading())
        val task =
            Firebase.firestore.collection(Constant.ITEM_COLLECTION).document(Constant.ITEM_DOCUMENT).get().await()
        val itemList = task.get(Constant.ITEM_TO_FIND_FIELD) as List<*>?
        val checkedList = itemList?.filterIsInstance<String>()
        emit(Status.Success(data = checkedList ?: emptyList()))
    }.flowOn(Dispatchers.IO).catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message ?: ""))
    }

    override fun removeItem(item: Item): Flow<Status<Nothing>> = flow {
        emit(Status.Loading())
        Firebase.firestore.collection(Constant.USER_COLLECTION).document(Firebase.auth.currentUser?.uid ?: "").update(
            Constant.ITEMS_FIELD, FieldValue.arrayRemove(item)
        ).await()
        Firebase.storage.reference.child("${Firebase.auth.currentUser?.uid}/${item.id}").delete().await()
        emit(Status.Success(data = null))
    }.catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message ?: ""))
    }

    override fun register(email: String, password: String): Flow<Status<Nothing>> = flow {
        emit(Status.Loading())
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        Firebase.firestore.collection(Constant.USER_COLLECTION).document(Firebase.auth.currentUser?.uid ?: "").set(
            mapOf(Constant.ITEMS_FIELD to emptyList<Item>()),
            SetOptions.merge()
        ).await()
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
        emit(Status.Success(data = null))
    }.catch {
        Log.e("FirebaseRepository", it.stackTraceToString())
        emit(Status.Error(error = it.message))
    }
}
