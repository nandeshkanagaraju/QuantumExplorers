package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.model.profileDefinitions
import fm.mrc.quantumexplorers.ui.theme.MonigueFont

@Composable
fun ResultsScreen(
    profileName: String,
    onBackToHome: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = profileDefinitions[profileName]
    var expandedSection by remember { mutableStateOf("strengths") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6B4EFF).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎯 YOUR COGNITIVE PROFILE",
                    fontSize = 28.sp,
                    fontFamily = MonigueFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6B4EFF)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = getProfileEmoji(profileName) + " " + profileName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D2D2D)
                )
            }
        }

        profile?.let {
            // Strengths Section
            ExpandableProfileSection(
                title = "💪 Strengths",
                items = it.strengths,
                isExpanded = expandedSection == "strengths",
                onToggle = { expandedSection = if (expandedSection == "strengths") "" else "strengths" },
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
                iconColor = Color(0xFF4CAF50),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSection = if (expandedSection == "strengths") "" else "strengths" }
            )
            
            // Weaknesses Section
            ExpandableProfileSection(
                title = "⚠️ Weaknesses",
                items = it.weaknesses,
                isExpanded = expandedSection == "weaknesses",
                onToggle = { expandedSection = if (expandedSection == "weaknesses") "" else "weaknesses" },
                backgroundColor = Color(0xFFF44336).copy(alpha = 0.1f),
                iconColor = Color(0xFFF44336),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSection = if (expandedSection == "weaknesses") "" else "weaknesses" }
            )
            
            // Areas to Explore Section
            ExpandableProfileSection(
                title = "🚀 Areas to Explore",
                items = it.areasToImprove,
                isExpanded = expandedSection == "explore",
                onToggle = { expandedSection = if (expandedSection == "explore") "" else "explore" },
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f),
                iconColor = Color(0xFF2196F3),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSection = if (expandedSection == "explore") "" else "explore" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B4EFF)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Go to Dashboard", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B4EFF)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Back to Home", fontSize = 16.sp)
        }
    }
}

@Composable
private fun ExpandableProfileSection(
    title: String,
    items: List<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = iconColor
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = iconColor
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "✦",
                                fontSize = 16.sp,
                                color = iconColor,
                                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                            )
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getProfileEmoji(profileName: String): String {
    return when (profileName) {
        "Memory Master" -> "📌"
        "Focus Finder" -> "🔎"
        "Problem Solver" -> "🎯"
        "Perception Pro" -> "👀"
        "Language Leader" -> "🗣️"
        "Active Learner" -> "🎮"
        else -> "🎯"
    }
} 