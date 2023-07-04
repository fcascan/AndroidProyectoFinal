package com.fcascan.proyectofinal.repositories

import android.util.Log
import com.fcascan.proyectofinal.constants.CATEGORIES_COLLECTION
import com.fcascan.proyectofinal.constants.GROUPS_COLLECTION
import com.fcascan.proyectofinal.constants.ITEMS_COLLECTION
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.Result
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    val _TAG = "FCC#FirestoreManager"

    val _firestore = Firebase.firestore

    //Coroutines:
    suspend fun getCollectionByUserID(userID: String, collection: String): MutableList<out Any>? {
        Log.d("$_TAG - getCollectionByUserID", "Retrieving $collection collection...")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            val data = _firestore.collection(collection)
                .whereEqualTo("userID", userID)
                .get()
                .await()
                .toObjects(classType)
            Log.d("$_TAG - getCollectionByUserID", "$collection data retrieved: ${data}")
            return data
        } catch (e: Exception) {
            Log.d("$_TAG - getCollectionByUserID", "Error Message: ${e.message}")
        }
        return MutableList(0) { "" }
    }

    //CRUD:
    suspend fun <T : Any> addObjectToCollection(thing: T, collection: String, callback: (Result, String?) -> Unit) {
        Log.d("$_TAG - addObjectToCollection", "thing: $thing - collection: $collection")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            val documentID = _firestore.collection(collection)
                .add(thing)
                .await()
            documentID?.let {
                Log.d("$_TAG - addObjectToCollection", "Item added to Firestore with ID ${it.id}")
                callback(Result.SUCCESS, it.id)
            } ?: run {
                Log.e("$_TAG - addObjectToCollection", "Document ID is null")
                callback(Result.FAILURE, null)
            }
        } catch (e: Exception) {
            Log.e("$_TAG - addObjectToCollection", "Error adding document: ${e.message}")
            callback(Result.FAILURE, null)
        }
    }

    suspend fun <T : Any> updateObjectInCollection(thing: T, collection: String, callback: (Result) -> Unit) {
        Log.d("$_TAG - updateObjectInCollection", "thing: $thing - collection: $collection")
        try {
            val documentId = when (collection) {
                ITEMS_COLLECTION -> (thing as Item).documentId
                CATEGORIES_COLLECTION -> (thing as Category).documentId
                GROUPS_COLLECTION -> (thing as Group).documentId
                else -> throw Exception("Invalid collection name")
            }
            documentId?.let {_firestore.collection(collection)
                .document(documentId)
                .set(thing)
                .await()
                Log.d("$_TAG - updateObjectInCollection", "Item successfully updated in FireStore")
                callback(Result.SUCCESS)
            } ?: run {
                Log.e("$_TAG - updateObjectInCollection", "Document ID is null")
                callback(Result.FAILURE)
            }
        } catch (e: Exception) {
            Log.d("$_TAG - updateObjectInCollection", "Error Message: ${e.message}")
            callback(Result.FAILURE)
        }
    }

    suspend fun deleteObjectFromCollection(documentId: String, collection: String, callback: (Result) -> Unit) {
        Log.d("$_TAG - deleteObjectFromCollection", "documentId: $documentId - collection: $collection")
        try {
            if (collection !in listOf(ITEMS_COLLECTION, CATEGORIES_COLLECTION, GROUPS_COLLECTION)) {
                throw Exception("Invalid collection name")
            }
            try {
                val documentReference = _firestore.collection(collection).document(documentId)
                val deleteResult = documentReference.delete().await()
                Log.d("$_TAG - deleteObjectFromCollection", "DocumentSnapshot successfully deleted!")
                callback(Result.SUCCESS)
            } catch (e: Exception) {
                Log.e("$_TAG - deleteObjectFromCollection", "Error deleting document: ${e.message}")
                callback(Result.FAILURE)
            }
        } catch (e: Exception) {
            Log.d("$_TAG - deleteObjectFromCollection", "Error Message: ${e.message}")
        }
    }

    inline fun <reified T> getObjectFromCollectionByID(id: String, collection: String, crossinline callback: (T?) -> Unit) {
        Log.d("$_TAG - getObjectFromCollectionByID", "id: $id - collection: $collection")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            _firestore.collection(collection)
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    val thing = document.toObject(T::class.java)
                    callback(thing)
                }
                .addOnFailureListener { exception ->
                    Log.d("$_TAG - getObjectFromCollectionByID", "Error Message: ${exception.message}")
                    callback(null)
                }
        } catch (e: Exception) {
            Log.d("$_TAG - getObjectFromCollectionByID", "Error Message: ${e.message}")
        }
    }
}