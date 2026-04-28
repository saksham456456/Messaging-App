package com.synq.app.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SynqMotionSplash(
    isAppReady: () -> Boolean,
    onSplashFinished: () -> Unit
) {
    // Colors based on brand guidelines
    val bgColor = Color(0xFF0B0B0F)
    val accentBlue = Color(0xFF4DA6FF)
    val accentCyan = Color(0xFF00E5FF)

    // Animation states
    val transition = rememberInfiniteTransition(label = "SplashTransition")

    // Phase 1 & 2: Signal Awakening & Convergence
    // Particles moving from outside to center
    val particleProgress = remember { Animatable(0f) }

    // Phase 3 & 4: Sync Moment & Pulse
    val logoScale = remember { Animatable(0f) }
    val ringPulse = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(1f) }

    // Control the flow
    var phase by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        // Phase 1 & 2: Convergence (400ms - 900ms)
        delay(200) // Initial micro-pause
        particleProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f) // Smooth deceleration
            )
        )

        phase = 3

        // Phase 3: Sync Moment (Logo appears + Pulse starts)
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        launch {
            ringPulse.animateTo(
                targetValue = 2f,
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            ringAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(800, easing = LinearEasing)
            )
        }

        // Phase 5 & 6: Wait for app readiness, then exit
        delay(800) // Minimum time to show the logo

        // Wait until real initialization is done (or timeout to prevent infinite hangs)
        var waitTime = 0
        while (!isAppReady() && waitTime < 3000) {
            delay(100)
            waitTime += 100
        }

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.width * 0.4f

            if (phase < 3) {
                // Draw converging signals
                val p = particleProgress.value
                val currentRadius = maxRadius * (1f - p)

                // Draw 3 converging arcs/lines
                for (i in 0..2) {
                    val angleOffset = i * 120f
                    withTransform({
                        rotate(angleOffset + (p * 90f), center)
                    }) {
                        drawArc(
                            color = accentCyan.copy(alpha = 0.6f + (p * 0.4f)),
                            startAngle = 0f,
                            sweepAngle = 45f + (p * 45f), // Arcs grow as they converge
                            useCenter = false,
                            topLeft = Offset(center.x - currentRadius, center.y - currentRadius),
                            size = androidx.compose.ui.geometry.Size(currentRadius * 2, currentRadius * 2),
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            } else {
                // Draw Pulse Ring
                if (ringAlpha.value > 0f) {
                    drawCircle(
                        color = accentCyan.copy(alpha = ringAlpha.value * 0.5f),
                        radius = (size.width * 0.15f) * ringPulse.value,
                        center = center,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                // Draw Synq Minimal Logo (Two interlocking/syncing shapes)
                val scale = logoScale.value
                val logoRadius = size.width * 0.12f * scale

                // Left overlapping circle
                drawCircle(
                    color = accentBlue,
                    radius = logoRadius,
                    center = Offset(center.x - (logoRadius * 0.4f), center.y),
                    style = Stroke(width = 12.dp.toPx())
                )

                // Right overlapping circle
                drawCircle(
                    color = accentCyan,
                    radius = logoRadius,
                    center = Offset(center.x + (logoRadius * 0.4f), center.y),
                    style = Stroke(width = 12.dp.toPx())
                )
            }
        }
    }
}
