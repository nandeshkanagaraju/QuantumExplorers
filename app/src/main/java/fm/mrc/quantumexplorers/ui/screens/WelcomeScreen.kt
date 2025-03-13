package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.ui.components.OrbitalAnimation
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import fm.mrc.quantumexplorers.ui.theme.MonigueFont
import fm.mrc.quantumexplorers.ui.theme.ConthicFont
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import fm.mrc.quantumexplorers.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.TextButton

@Composable
fun WelcomeScreen(
    onBeginJourney: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo section
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Title section with Monigue font
        Text(
            text = "QUANTUM\nEXPLORERS",
            fontSize = 40.sp,
            fontFamily = MonigueFont,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp,
            lineHeight = 48.sp
        )

        // Orbital Animation
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            OrbitalAnimation(
                modifier = Modifier.size(300.dp),
                primaryColor = Color(0xFF6B4EFF),
                secondaryColor = Color(0xFFE6E1FF)
            )
        }

        // Bottom section with authentication buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            // Email Sign In/Up Button
            Button(
                onClick = onBeginJourney,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text(
                    text = "Sign In with Email",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Google Sign In Button
            Button(
                onClick = { /* No action for now */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Sign In with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Microsoft Sign In Button
            Button(
                onClick = { /* TODO: Implement Microsoft Sign In */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F2F2F),
                    contentColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_microsoft),
                        contentDescription = "Microsoft Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Continue with Microsoft",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Sign Up Link
            TextButton(
                onClick = onSignUpClick
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = Color(0xFF6B4EFF),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
} 