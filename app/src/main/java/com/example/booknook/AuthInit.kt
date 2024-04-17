package com.example.booknook.ui.profile


import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.booknook.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

// BASE CODE FROM FC #6 FIREBASE AUTH
class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>) {

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)

        } else {
            //viewModel.updateUser()
        }
    }
}
