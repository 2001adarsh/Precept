package com.adarsh.precept.models

import com.google.firebase.firestore.FieldValue

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val uid: String,
    val deviceToken: String,
    val status: String,
    val onlineState: String
){
    constructor():this("", "","","","","", "")
    constructor(name: String, imageUrl: String, thumbImage: String, uid: String):
            this(name, imageUrl,thumbImage,uid,"","Hey There I'm using Precept", "")

}