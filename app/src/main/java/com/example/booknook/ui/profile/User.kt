package com.example.booknook.ui.profile

import java.io.Serializable
import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("userId")
    var userId: String = "",
    @PropertyName("username")
    var username: String = "",
    @PropertyName("displayName")
    var displayName: String = "",
    @PropertyName("aboutMe")
    var aboutMe: String = ""
    ) : Serializable