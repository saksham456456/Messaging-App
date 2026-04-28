package com.synq.app.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synq.app.R
import com.synq.app.core.theme.TealAccent
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(onAnimationFinished: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Logo pops in and scales up smoothly
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { overshootingEasing(it) }
            )
        )
        // Subtitle fades in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        // Pause to let user appreciate the animation
        delay(800)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_synq_logo),
                contentDescription = "Synq Logo",
                tint = Color.Unspecified, // Keep the colors from the XML
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Synq",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = TealAccent,
                modifier = Modifier.scale(scale.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connect instantly. Always in sync.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}

// Custom easing function to give the logo a slight "pop/bounce" effect when appearing
private fun overshootingEasing(t: Float): Float {
    val tension = 2.0f
    val t1 = t - 1.0f
    return t1 * t1 * ((tension + 1) * t1 + tension) + 1.0f
}
