package fm.mrc.quantumexplorers.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import fm.mrc.quantumexplorers.accessibility.AccessibilityService

@Composable
fun AccessibilityFloatingButton(
    accessibilityService: AccessibilityService,
    modifier: Modifier = Modifier
) {
    val isEnabled by accessibilityService.isAccessibilityEnabled.collectAsState()
    
    FloatingActionButton(
        onClick = { accessibilityService.toggleAccessibility() },
        modifier = modifier.semantics {
            contentDescription = if (isEnabled) "Disable accessibility mode" else "Enable accessibility mode"
        }
    ) {
        Icon(
            imageVector = Icons.Default.AccessibilityNew,
            contentDescription = null,
            tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AccessibleText(
    text: String,
    accessibilityService: AccessibilityService,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onTextClick: (() -> Unit)? = null
) {
    Text(
        text = text,
        style = style,
        modifier = modifier
            .semantics {
                contentDescription = text
            }
            .clickable(enabled = onTextClick != null) {
                accessibilityService.speak(text)
                onTextClick?.invoke()
            }
    )
}

@Composable
fun AccessibilityControls(
    accessibilityService: AccessibilityService,
    modifier: Modifier = Modifier
) {
    val isEnabled by accessibilityService.isAccessibilityEnabled.collectAsState()
    val isSpeaking by accessibilityService.isSpeaking.collectAsState()

    if (isEnabled) {
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { accessibilityService.stopSpeaking() },
                enabled = isSpeaking
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Stop speaking"
                )
            }
            
            Slider(
                value = 0.8f, // Default speech rate
                onValueChange = { /* Update speech rate */ },
                modifier = Modifier.width(200.dp)
            )
        }
    }
} 