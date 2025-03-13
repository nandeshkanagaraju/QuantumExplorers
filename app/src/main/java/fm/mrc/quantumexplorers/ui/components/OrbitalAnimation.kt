package fm.mrc.quantumexplorers.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrbitalAnimation(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbital")
    
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val orbitScale = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 4

        // Draw background glow
        drawCircle(
            color = secondaryColor.copy(alpha = 0.2f),
            radius = radius * 1.8f,
            center = Offset(radius * 0.5f, radius * 0.5f)
        )
        
        drawCircle(
            color = secondaryColor.copy(alpha = 0.15f),
            radius = radius * 1.2f,
            center = Offset(size.width - radius * 0.8f, size.height - radius * 0.8f)
        )

        // Draw nucleus (central atom)
        drawCircle(
            color = primaryColor,
            radius = radius * 0.3f,
            center = center
        )

        // Draw orbital path
        drawCircle(
            color = secondaryColor.copy(alpha = 0.5f),
            radius = radius * orbitScale.value,
            center = center,
            style = Stroke(width = 2f)
        )

        // Draw electrons
        val angleInRadians = Math.toRadians(rotation.value.toDouble())
        val electronRadius = radius * orbitScale.value
        
        // First electron
        val x1 = center.x + cos(angleInRadians) * electronRadius
        val y1 = center.y + sin(angleInRadians) * electronRadius
        drawCircle(
            color = primaryColor,
            radius = 8f,
            center = Offset(x1.toFloat(), y1.toFloat())
        )

        // Second electron (opposite side)
        val x2 = center.x + cos(angleInRadians + Math.PI) * electronRadius
        val y2 = center.y + sin(angleInRadians + Math.PI) * electronRadius
        drawCircle(
            color = primaryColor,
            radius = 8f,
            center = Offset(x2.toFloat(), y2.toFloat())
        )

        // Draw electron trail effects
        repeat(8) { i ->
            val trailAngle = angleInRadians - (i * Math.PI / 8)
            val trailX = center.x + cos(trailAngle) * electronRadius
            val trailY = center.y + sin(trailAngle) * electronRadius
            drawCircle(
                color = primaryColor.copy(alpha = 0.15f - (i * 0.02f)),
                radius = 6f,
                center = Offset(trailX.toFloat(), trailY.toFloat())
            )
        }
    }
} 