package com.fcascan.proyectofinal.entities;

import com.google.firebase.firestore.DocumentId

class Category(
    @DocumentId
    val documentId: String? = null,
    var userID: String = "",
    var name: String = "",
) {
    override fun toString(): String {
        return """Category{id: $documentId, title: $name}"""
    }
}
