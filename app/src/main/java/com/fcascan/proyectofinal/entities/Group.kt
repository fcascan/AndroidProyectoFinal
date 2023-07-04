package com.fcascan.proyectofinal.entities

import com.google.firebase.firestore.DocumentId

class Group(
    @DocumentId
    val documentId: String? = null,
    var userID: String = "",
    var name: String = "",
) {
    override fun toString(): String {
        return """Group{id: $documentId, name: $name}"""
    }
}