package fm.mrc.quantumexplorers.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.ui.theme.MonigueFont

@Composable
fun CognitiveAssessmentButton(
    onNavigateToCognitiveAssessment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE1F5FE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🧠 Discover Your Learning Style",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MonigueFont,
                color = Color(0xFF0277BD)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Take a quick assessment to find your unique cognitive profile and personalized learning recommendations.",
                fontSize = 14.sp,
                fontFamily = MonigueFont,
                color = Color(0xFF01579B)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateToCognitiveAssessment,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Start Assessment",
                    fontSize = 16.sp,
                    fontFamily = MonigueFont,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
} 