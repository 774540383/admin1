package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.SecondaryDark
import com.example.viewmodel.FootballViewModel

@Composable
fun OnboardingScreen(viewModel: FootballViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF064E3B), // Deep Forest Green
                        BackgroundDark,
                        Color(0xFF0F172A)  // Deep slate
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Elegant Asymmetric Sporty Logo Layer
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryDark.copy(alpha = 0.2f))
                    .border(2.dp, PrimaryDark, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "Football World Logo",
                    tint = PrimaryDark,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FOOTBALL WORLD",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "عالم كرة القدم",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Premium live scores, sports statistics, Arabic IPTV feeds, match predictions & tactical AI tools at your fingertips.",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Glassmorphic bilingual chooser card Selection
            Text(
                text = "CHOOSE YOUR LANGUAGE / اختر لغتك",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Arabic Language Card (RTL Intent)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.setLanguage("ar") }
                        .padding(16.dp)
                        .testTag("lang_ar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "العربية",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "عرض باتجاه اليمين RTL",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // English Language Card (LTR Intent)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.setLanguage("en") }
                        .padding(16.dp)
                        .testTag("lang_en_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "English",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Standard LTR view",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language setting indicator",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
