package com.fcascan.proyectofinal.constants

//Firebase Collections:
const val ITEMS_COLLECTION = "Items"
const val CATEGORIES_COLLECTION = "Categories"
const val GROUPS_COLLECTION = "Groups"

//Timeouts (msecs):
const val DOWNLOAD_COLLECTION_TIMEOUT = 10000L

//File size:
const val MAX_FILE_SIZE_BYTES: Long = 512 * 1024   //512KB

//Auth:
const val AUTH_DAYS_TO_EXPIRE_LOGIN = 30L
const val AUTH_DAYS_TO_FORCE_EXPIRE_LOGIN = 365L

//Actions:
const val ACTION_SCREEN_ACTIONS = 1     //First Screen that lists the actions
const val ACTION_SCREEN_CATEGORIES = 2  //Second Screen that lists the categories
const val ACTION_SCREEN_GROUPS = 3      //Third Screen that lists the groups


//Splash Screen:
const val SPLASH_SCREEN_URL = "https://avatars.githubusercontent.com/u/64813436?v=4"
const val SPLASH_TIME_OUT = 4000L    // 3 sec


//Glide Images sizes:
const val SPLASH_SCREEN_SIZE_MULTIPLIER = 0.45f
const val AVATAR_SIZE_MULTIPLIER = 0.12f
const val ITEM_AVATAR_SIZE_MULTIPLIER = 0.19f
const val ITEM_IMG_SIZE_MULTIPLIER = 0.35f