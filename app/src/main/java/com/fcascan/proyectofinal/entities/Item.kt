package com.fcascan.proyectofinal.entities

import com.google.firebase.firestore.DocumentId

data class Item(
    @DocumentId
    val documentId: String? = null,
    var userID: String? = null,
    var title: String? = null,
    var description: String? = null,
    var categoryID: String? = null,
    var groupID: String? = null
) {
    override fun toString(): String {
        return """Item{id: $documentId, title: $title}"""
    }
}