package com.yjp.functions.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import com.yjp.functions.ui.theme.FunctionsTheme
import com.yjp.functions.ui.youtube.YoutubeScreen
import com.yjp.functions.ui.youtube.YoutubeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: YoutubeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FunctionsTheme {
                YoutubeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.Companion.safeDrawing),
                )
            }
        }
    }
}