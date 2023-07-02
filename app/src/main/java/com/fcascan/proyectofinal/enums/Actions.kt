package com.fcascan.proyectofinal.enums

enum class Actions(val title: String, val description: String) {
    ACTION1("Record New Audio", "Record New Audio and save it locally and in the cloud"),
    ACTION2("Import Audio from device", "Import audio from internal memory and save it to the cloudand locally"),
    ACTION3("See All Categories", "List all categories for editing or creating new ones"),
    ACTION4("See All Groups", "List all groups for editing or creating new ones")
}