package com.fcascan.proyectofinal.entities

data class Item(
    var ID: String? = null,
    var userID: String? = null,
    var title: String? = null,
    var description: String? = null,
    var categoryID: String? = null,
    var groupID: String? = null
) {
    override fun toString(): String {
        return """Item: $ID - $title"""
    }
}