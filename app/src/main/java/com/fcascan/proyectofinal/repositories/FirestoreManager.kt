package com.fcascan.proyectofinal.repositories

import android.util.Log
import com.fcascan.proyectofinal.constants.CATEGORIES_COLLECTION
import com.fcascan.proyectofinal.constants.GROUPS_COLLECTION
import com.fcascan.proyectofinal.constants.ITEMS_COLLECTION
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item
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
        return null
    }

    //CRUD:
    fun <T : Any> addObjectToCollection(thing: T, collection: String, callback: (String?) -> Unit) {
        Log.d("$_className - addObjectToCollection", "thing: $thing - collection: $collection")
        try {
            when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            _db.collection(collection)
                .add(thing)
                .addOnSuccessListener { documentReference ->
                    Log.d("$_className - addObjectToCollection", "DocumentSnapshot added with ID: ${documentReference.id}")
                    val thingId = documentReference.id
                    callback(thingId)
                }
                .addOnFailureListener { exception ->
                    Log.d("$_className - addObjectToCollection", "Error Message: ${exception.message}")
                    callback(null)
                }
        } catch (e: Exception) {
            Log.d("$_className - addObjectToCollection", "Error Message: ${e.message}")
        }
    }

    fun <T : Any> updateObjectInCollection(thing: T, collection: String, callback: (Boolean) -> Unit) {
        Log.d("$_className - updateObjectInCollection", "thing: $thing - collection: $collection")
        try {
            when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            _db.collection(collection)
                .document((thing as DocumentSnapshot).id)
                .set(thing)
                .addOnSuccessListener {
                    Log.d("$_className - updateObjectInCollection", "DocumentSnapshot successfully updated!")
                    callback(true)
                }
                .addOnFailureListener { exception ->
                    Log.d("$_className - updateObjectInCollection", "Error Message: ${exception.message}")
                    callback(false)
                }
        } catch (e: Exception) {
            Log.d("$_className - updateObjectInCollection", "Error Message: ${e.message}")
        }
    }

    fun deleteObjectFromCollection(thing: DocumentSnapshot, collection: String, callback: (Boolean) -> Unit) {
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