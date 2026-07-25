package com.stotra.sahasranamam.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.stotra.sahasranamam.data.local.AppDatabase
import com.stotra.sahasranamam.data.local.initializer.DatabaseInitializer
import com.stotra.sahasranamam.presentation.navigation.AppNavGraph
import com.stotra.sahasranamam.presentation.theme.SahasranamamTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seed database with Sri Suktam (16 verses) & Aditya Hrudayam on first run
        lifecycleScope.launch {
            DatabaseInitializer(applicationContext, db).seedDatabaseIfNeeded()
        }

        setContent {
            SahasranamamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}
