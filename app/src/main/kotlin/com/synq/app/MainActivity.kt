package com.synq.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.synq.app.core.theme.SynqTheme
import com.synq.app.presentation.navigation.SynqNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SynqTheme {
                val navController = rememberNavController()
                SynqNavGraph(navController = navController)
            }
        }
    }
}
