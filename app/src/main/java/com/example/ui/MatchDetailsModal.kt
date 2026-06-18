package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
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
import com.example.ui.theme.*
import com.example.viewmodel.FootballViewModel

@Composable
fun MatchDetailsModal(viewModel: FootballViewModel, onClose: () -> Unit) {
    val match = viewModel.selectedMatchForDetails ?: return
    val stats = viewModel.selectedMatchStats
    val pred = viewModel.activeMatchPrediction
    val isAr = viewModel.appLanguage == "ar"

    var selectedSubTab by remember { mutableStateOf("overview") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Modal Container (translucent slates)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false) { }
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundDark)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Pull Bar & Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "تفاصيل المباراة" else "Match Stats & Predictions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50.dp))
                            .testTag("close_match_details")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Detail modal", tint = Color.White)
                    }
                }

                // Match Hero Score Board Layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // League badge header
                        Text(
                            text = if (isAr) match.leagueAr else match.league,
                            fontSize = 12.sp,
                            color = PrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Home Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isAr) match.homeTeamAr else match.homeTeam,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Score Block
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "${match.homeScore}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (match.status == "live") ColorLive else Color.White
                                )
                                Text(
                                    text = ":",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${match.awayScore}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (match.status == "live") ColorLive else Color.White
                                )
                            }

                            // Away Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isAr) match.awayTeamAr else match.awayTeam,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Game progress state indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (match.status == "live") ColorLive.copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (match.status) {
                                    "live" -> "${if (isAr) "مباشر" else "LIVE"} - ${match.minute}"
                                    "halftime" -> if (isAr) "بين الشوطين" else "HALFTIME"
                                    "finished" -> if (isAr) "انتهت" else "FINISHED"
                                    else -> if (isAr) "مجدولة: ${match.kickoffTime}" else "Scheduled: ${match.kickoffTime}"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (match.status == "live") ColorLive else Color.LightGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail sub tabs selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val subTabs = listOf(
                        Triple("overview", if (isAr) "التوقعات" else "Predict", Icons.Default.QueryStats),
                        Triple("stats", if (isAr) "الإحصائيات" else "Stats", Icons.Default.Info)
                    )

                    subTabs.forEach { (route, label, icon) ->
                        val isSelected = selectedSubTab == route
                        Button(
                            onClick = { selectedSubTab = route },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) PrimaryDark else Color.Transparent,
                                contentColor = if (isSelected) OnPrimaryDark else Color.Gray
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("subtab_$route"),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Content Grid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedSubTab == "overview") {
                        // Tactical Predictions Layout
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = if (isAr) "الاحتمالات الإحصائية للفوز" else "Statistical Match Outcome",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (pred != null) {
                                // Probability bars
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Home Win Row
                                    ProbabilityBar(
                                        label = if (isAr) "فوز ${match.homeTeamAr}" else "${match.homeTeam} Win",
                                        percentage = pred.homeWinProbability,
                                        color = PrimaryDark
                                    )
                                    // Draw Row
                                    ProbabilityBar(
                                        label = if (isAr) "التعادل" else "Draw Probability",
                                        percentage = pred.drawProbability,
                                        color = ColorGrayText
                                    )
                                    // Away Win Row
                                    ProbabilityBar(
                                        label = if (isAr) "فوز ${match.awayTeamAr}" else "${match.awayTeam} Win",
                                        percentage = pred.awayWinProbability,
                                        color = SecondaryDark
                                    )
                                }

                                // Key tactical analytics card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = "Soccer icon", tint = PrimaryDark, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isAr) "الرؤية التكتيكية للأداة" else "Technical AI Grounding",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (isAr) pred.keyInsightAr else pred.keyInsightEn,
                                            fontSize = 13.sp,
                                            color = Color.LightGray,
                                            lineHeight = 18.sp
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isAr) "درجة دقة التوقع:" else "Statistical Confidence Scale:",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "${pred.confidence}%",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp,
                                                color = PrimaryDark
                                            )
                                        }
                                        // Score Predict Label
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isAr) "النتيجة المتوقعة:" else "Predicted Score:",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "${pred.predictedHomeScore} - ${pred.predictedAwayScore}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp,
                                                color = SecondaryDark
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryDark)
                                }
                            }
                        }
                    } else if (selectedSubTab == "stats") {
                        // Game statistics comparisons
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = if (isAr) "إحصائيات المباراة المباشرة" else "Live Comparison Metrics",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (stats != null) {
                                StatSliderMetric(
                                    label = if (isAr) "الاستحواذ %" else "Possession %",
                                    homeVal = stats.possessionHome,
                                    awayVal = stats.possessionAway
                                )
                                StatSliderMetric(
                                    label = if (isAr) "إجمالي التسديدات" else "Total Shots",
                                    homeVal = stats.shotsHome,
                                    awayVal = stats.shotsAway
                                )
                                StatSliderMetric(
                                    label = if (isAr) "الركنية" else "Corners",
                                    homeVal = stats.cornersHome,
                                    awayVal = stats.cornersAway
                                )
                                StatSliderMetric(
                                    label = if (isAr) "الأخطاء" else "Fouls",
                                    homeVal = stats.foulsHome,
                                    awayVal = stats.foulsAway
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No stats calculated for this match yet", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProbabilityBar(label: String, percentage: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = Color.LightGray)
            Text(text = "$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun StatSliderMetric(label: String, homeVal: Int, awayVal: Int) {
    val total = (homeVal + awayVal).coerceAtLeast(1)
    val homePct = (homeVal.toFloat() / total)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$homeVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
            Text(text = label, fontSize = 12.sp, color = Color.LightGray)
            Text(text = "$awayVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryDark)
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Modern bi-directional progressive bar layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(homePct.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(PrimaryDark)
            )
            Box(
                modifier = Modifier
                    .weight((1f - homePct).coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(SecondaryDark)
            )
        }
    }
}
