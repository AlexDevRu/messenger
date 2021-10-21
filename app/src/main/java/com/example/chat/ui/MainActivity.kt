package com.example.chat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.chat.R
import io.getstream.chat.android.client.ChatClient

class MainActivity : AppCompatActivity() {

    private val client = ChatClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Chat)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        if(navController.currentDestination?.id == R.id.signInFragment) {
            val currentUser = client.getCurrentUser()
            if(currentUser != null) {
                navController.navigate(R.id.mainFragment)
            }
        }
    }
}