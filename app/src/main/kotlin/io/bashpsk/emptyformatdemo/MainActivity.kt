package io.bashpsk.emptyformatdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.bashpsk.emptyformatdemo.theme.EmptyFormatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            EmptyFormatTheme {

                FormatDemoScreen()
            }
        }
    }
}