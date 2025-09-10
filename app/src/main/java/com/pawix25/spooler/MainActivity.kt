package com.pawix25.spooler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pawix25.spooler.data.PrintJob as PrintJobData
import com.pawix25.spooler.ui.screens.SpoolListScreen
import com.pawix25.spooler.ui.screens.AddSpoolScreen
import com.pawix25.spooler.ui.screens.SpoolDetailsScreen
import com.pawix25.spooler.viewmodel.SpoolViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpoolerTheme {
                val viewModel: SpoolViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("spools") }
                var selectedSpoolId by remember { mutableStateOf<Int?>(null) }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        when (targetState) {
                            "add_spool" -> slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { -it / 3 },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(300))
                            
                            "spool" -> slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(350, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(350)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { -it / 3 },
                                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(350))
                            
                            else -> slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(300)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(300))
                        }
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        "spools" -> {
                            SpoolListScreen(
                                spools = viewModel.spools.collectAsState().value,
                                onAddSpool = { currentScreen = "add_spool" },
                                onSpoolClick = { spool ->
                                    selectedSpoolId = spool.id
                                    currentScreen = "spool"
                                }
                            )
                        }
                        "add_spool" -> {
                            AddSpoolScreen(
                                onSave = { spool ->
                                    viewModel.addSpool(spool)
                                    currentScreen = "spools"
                                },
                                onBack = { currentScreen = "spools" }
                            )
                        }
                        "spool" -> {
                            selectedSpoolId?.let { spoolId ->
                                SpoolDetailsScreen(
                                    spoolId = spoolId,
                                    onBack = { currentScreen = "spools" },
                                    onAddPrint = { printJob ->
                                        viewModel.addPrintJob(printJob)
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpoolerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = Color(0xFF42A5F5), // Modern blue
            secondary = Color(0xFF26C6DA), // Cyan accent
            tertiary = Color(0xFFFF7043), // Orange accent
            background = Color(0xFF0F0F0F), // Deep black
            surface = Color(0xFF1A1A1A), // Dark surface
            surfaceVariant = Color(0xFF2A2A2A), // Lighter dark surface
            primaryContainer = Color(0xFF1565C0), // Dark primary container
            error = Color(0xFFEF5350), // Soft red
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFF000000),
            onTertiary = Color(0xFF000000),
            onBackground = Color(0xFFFFFFFF),
            onSurface = Color(0xFFFFFFFF),
            onSurfaceVariant = Color(0xFFE0E0E0),
            onPrimaryContainer = Color(0xFFFFFFFF),
            onError = Color(0xFF000000),
            outline = Color(0xFF424242)
        )
        else -> lightColorScheme(
            primary = Color(0xFF1976D2), // Deep blue
            secondary = Color(0xFF0097A7), // Teal
            tertiary = Color(0xFFE65100), // Deep orange
            background = Color(0xFFFAFAFA), // Light gray background
            surface = Color(0xFFFFFFFF), // Pure white
            surfaceVariant = Color(0xFFF5F5F5), // Light surface variant
            primaryContainer = Color(0xFFE3F2FD), // Light blue container
            error = Color(0xFFD32F2F), // Standard red
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFFFFFFFF),
            onTertiary = Color(0xFFFFFFFF),
            onBackground = Color(0xFF1A1A1A),
            onSurface = Color(0xFF1A1A1A),
            onSurfaceVariant = Color(0xFF424242),
            onPrimaryContainer = Color(0xFF0D47A1),
            onError = Color(0xFFFFFFFF),
            outline = Color(0xFFBDBDBD)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}