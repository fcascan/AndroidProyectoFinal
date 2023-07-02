package com.fcascan.proyectofinal.enums

enum class ActionsScreen(val id: Int, val title: String) {
    SCREEN_ACTIONS(1, "Actions"),        //First Screen that lists the actions
    SCREEN_CATEGORIES(2, "Categories"),  //Second Screen that lists the categories
    SCREEN_GROUPS(3, "Groups"),         //Third Screen that lists the groups
    SCREEN_EASTER_EGG(99, "Easter Egg");

    fun getActionById(id: Int): ActionsScreen {
        return when (id) {
            1 -> SCREEN_ACTIONS
            2 -> SCREEN_CATEGORIES
            3 -> SCREEN_GROUPS
            else -> SCREEN_EASTER_EGG
        }
    }

    fun getNextAction(): ActionsScreen {
        return when (this) {
            SCREEN_ACTIONS -> SCREEN_CATEGORIES
            SCREEN_CATEGORIES -> SCREEN_GROUPS
            SCREEN_GROUPS -> SCREEN_ACTIONS
            else -> SCREEN_ACTIONS
        }
    }
}