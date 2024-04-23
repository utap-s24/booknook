package com.example.booknook

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booknook.databinding.ActivityMainBinding
import com.example.booknook.ui.profile.AuthInit
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_boards, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val thisAuth = FirebaseAuth.getInstance()
        thisAuth.addAuthStateListener {
            if (thisAuth.currentUser == null) {
                AuthInit(viewModel, signInLauncher)
                navController.navigate(R.id.navigation_home)
            } else {
                viewModel.initProfile()
                viewModel.fetchAllUsers()
            }
        }

        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in, check if it's a new account
                val isNewUser = user.metadata?.creationTimestamp == user.metadata?.lastSignInTimestamp
                if (isNewUser) {
                    // This is a new user account
                    viewModel.initNewProfile()
                    Log.d("authStateListener", "New user account created")
                } else {
                    // This is an existing user
                    Log.d("authStateListener", "Existing user signed in")
                }
            }
        }

        thisAuth.addAuthStateListener(authStateListener)


    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // AI CONTRIBUTION
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}