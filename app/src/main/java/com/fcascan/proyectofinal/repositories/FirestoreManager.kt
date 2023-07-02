package com.fcascan.proyectofinal.repositories

import android.util.Log
import com.fcascan.proyectofinal.constants.CATEGORIES_COLLECTION
import com.fcascan.proyectofinal.constants.GROUPS_COLLECTION
import com.fcascan.proyectofinal.constants.ITEMS_COLLECTION
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    val _className = "FCC#FirestoreManager"

    val _db = Firebase.firestore

    //Coroutines:
    suspend fun getCollectionByUserID(userID: String, collection: String): MutableList<out Any>? {
        Log.d("$_className - getCollectionByUserID", "Retrieving $collection collection...")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            val data = _db.collection(collection)
                .whereEqualTo("userID", userID)
                .get()
                .await()
                .toObjects(classType)
            Log.d("$_className - getCollectionByUserID", "$collection data retrieved: ${data}")
            return data
        } catch (e: Exception) {
            Log.d("$_className - getCollectionByUserID", "Error Message: ${e.message}")
        }
        return MutableList(0) { "" }
    }

    //CRUD:
    suspend fun <T : Any> addObjectToCollection(thing: T, collection: String, callback: (Result) -> Unit) {
        Log.d("$_className - addObjectToCollection", "thing: $thing - collection: $collection")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            val documentID = _db.collection(collection)
                .add(thing)
                .await()
                .get().result?.id
            Log.d("$_className - addObjectToCollection", "Item added to FireStore with ID ${documentID}")
            callback(Result.SUCCESS)
        } catch (e: Exception) {
            Log.d("$_className - addObjectToCollection", "Error Message: ${e.message}")
        }
    }

    suspend fun <T : Any> updateObjectInCollection(thing: T, collection: String, callback: (Result) -> Unit) {
        Log.d("$_className - updateObjectInCollection", "thing: $thing - collection: $collection")
        try {
            val documentId = when (collection) {
                ITEMS_COLLECTION -> (thing as Item).documentId
                CATEGORIES_COLLECTION -> (thing as Category).documentId
                GROUPS_COLLECTION -> (thing as Group).documentId
                else -> throw Exception("Invalid collection name")
            }
            _db.collection(collection)
                .document(documentId!!)
                .set(thing)
                .await()
            Log.d("$_className - updateObjectInCollection", "Item successfully updated in FireStore")
            callback(Result.SUCCESS)
        } catch (e: Exception) {
            Log.d("$_className - updateObjectInCollection", "Error Message: ${e.message}")
            callback(Result.FAILURE)
        }
    }

    suspend fun deleteObjectFromCollection(thing: DocumentSnapshot, collection: String, callback: (Boolean) -> Unit) {
        Log.d("$_className - deleteObjectFromCollection", "thing: $thing - collection: $collection")
        try {
            when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            _db.collection(collection)
                .document(thing.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("$_className - deleteObjectFromCollection", "DocumentSnapshot successfully deleted!")
                    callback(true)
                }
                .addOnFailureListener { exception ->
                    Log.d("$_className - deleteObjectFromCollection", "Error Message: ${exception.message}")
                    callback(false)
                }
        } catch (e: Exception) {
            Log.d("$_className - deleteObjectFromCollection", "Error Message: ${e.message}")
        }
    }

    inline fun <reified T> getObjectFromCollectionByID(id: String, collection: String, crossinline callback: (T?) -> Unit) {
        Log.d("$_className - getObjectFromCollectionByID", "id: $id - collection: $collection")
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            _db.collection(collection)
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    val thing = document.toObject(T::class.java)
                    callback(thing)
                }
                .addOnFailureListener { exception ->
                    Log.d("$_className - getObjectFromCollectionByID", "Error Message: ${exception.message}")
                    callback(null)
                }
        } catch (e: Exception) {
            Log.d("$_className - getObjectFromCollectionByID", "Error Message: ${e.message}")
        }
    }
}