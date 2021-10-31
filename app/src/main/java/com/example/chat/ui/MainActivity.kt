package com.example.chat.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.chat.R
import com.example.domain.use_cases.local.preferences.GetUserUseCase
import org.koin.android.ext.android.inject
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chat.ui.theme.ComposeTheme

/*class MainActivity : AppCompatActivity() {

    private val getUserUseCase by inject<GetUserUseCase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Chat)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        if(navController.currentDestination?.id == R.id.signInFragment) {
            val userId = getUserUseCase()
            if(userId != null) {
                navController.navigate(R.id.mainFragment, Bundle().apply {
                    putString("userId", userId)
                })
            }
        }
    }
}*/

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTheme {
                Root()
            }
        }
    }
}