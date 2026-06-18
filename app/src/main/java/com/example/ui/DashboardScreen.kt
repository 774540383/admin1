package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchEntity
import com.example.data.NewsEntity
import com.example.data.StandingEntity
import com.example.ui.theme.*
import com.example.viewmodel.FootballViewModel

@Composable
fun DashboardScreen(viewModel: FootballViewModel) {
    val isAr = viewModel.appLanguage == "ar"
    val systemDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Force system/app Layout Direction (Arabic RTL support)
    CompositionLocalProvider(LocalLayoutDirection provides systemDirection) {
        Scaffold(
            topBar = {
                UpperDashboardHeader(viewModel)
            },
            bottomBar = {
                GlassyBottomNavigationBar(viewModel)
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BackgroundDark, Color(0xFF0F172A))
                        )
                    )
                    .padding(16.dp)
            ) {
                // Determine layout tab in sequence
                when (viewModel.selectedTab) {
                    "matches" -> MatchesTabContent(viewModel)
                    "standings" -> StandingsTabContent(viewModel)
                    "streams" -> IptvPlayerView(viewModel)
                    "news" -> NewsTabContent(viewModel)
                    "ai_studio" -> AiStudioTabContent(viewModel)
                    "admin" -> AdminTabContent(viewModel)
                }

                // Global Overlay: Match stats Bottom Sheet Modal
                if (viewModel.selectedMatchForDetails != null) {
                    MatchDetailsModal(
                        viewModel = viewModel,
                        onClose = { viewModel.selectMatch(viewModel.selectedMatchForDetails!!.copy(status = "closed_overlay")) } // workaround triggers refresh
                    )
                }
            }
        }
    }
}

// --- Dynamic Header block ---
@Composable
fun UpperDashboardHeader(viewModel: FootballViewModel) {
    val isAr = viewModel.appLanguage == "ar"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = "Football World logo",
                tint = PrimaryDark,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAr) "العالمي لكرة القدم" else "Football World",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            // Theme toggle
            IconButton(
                onClick = { viewModel.toggleTheme() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50.dp))
            ) {
                Icon(
                    imageVector = if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Theme switcher",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Language picker
            Button(
                onClick = { viewModel.setLanguage(if (isAr) "en" else "ar") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    contentColor = PrimaryDark
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("header_lang_button")
            ) {
                Text(text = if (isAr) "EN" else "عربي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Sync/Refresh
            IconButton(
                onClick = { viewModel.refreshAllData() },
                modifier = Modifier.background(
                    if (viewModel.isRefreshingData) PrimaryDark.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(50.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Refresh data",
                    tint = if (viewModel.isRefreshingData) PrimaryDark else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// --- Tab 1: Today's matches list (Section 1) ---
@Composable
fun MatchesTabContent(viewModel: FootballViewModel) {
    val allList by viewModel.matches.collectAsState()
    val isAr = viewModel.appLanguage == "ar"
    var showOnlyLive by remember { mutableStateOf(false) }

    val filteredList = allList.filter {
        if (showOnlyLive) it.status == "live" || it.status == "halftime" else true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle live filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showOnlyLive = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showOnlyLive) PrimaryDark else Color.White.copy(alpha = 0.05f),
                    contentColor = if (!showOnlyLive) OnPrimaryDark else Color.Gray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("all_matches_filter")
            ) {
                Text(text = if (isAr) "الكل" else "All Today", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showOnlyLive = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showOnlyLive) ColorLive else Color.White.copy(alpha = 0.05f),
                    contentColor = if (showOnlyLive) Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("live_matches_filter")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (showOnlyLive) Color.White else ColorLive)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isAr) "مباشر" else "Live Match Badge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Render Search Query
        OutlinedTextField(
            value = viewModel.lastSearchQuery,
            onValueChange = { viewModel.lastSearchQuery = it },
            placeholder = { Text(text = if (isAr) "ابحث عن المباريات أو الفرق..." else "Search matches or clubs...", color = Color.Gray) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.03f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                focusedBorderColor = PrimaryDark,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("match_search_input")
        )

        val searchedGames = filteredList.filter {
            it.homeTeam.contains(viewModel.lastSearchQuery, true) ||
                    it.awayTeam.contains(viewModel.lastSearchQuery, true) ||
                    it.homeTeamAr.contains(viewModel.lastSearchQuery, true) ||
                    it.awayTeamAr.contains(viewModel.lastSearchQuery, true) ||
                    it.league.contains(viewModel.lastSearchQuery, true) ||
                    it.leagueAr.contains(viewModel.lastSearchQuery, true)
        }

        if (searchedGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = "None matches icon", tint = Color.Gray, modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAr) "لا توجد مباريات جارية بهذه البيانات" else "No matches matched your filters today.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(searchedGames) { match ->
                    MatchCardItem(match, isAr) {
                        viewModel.selectMatch(match)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCardItem(match: MatchEntity, isAr: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("match_card_${match.matchId}"),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Upper row: League + Live badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) match.leagueAr else match.league,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                if (match.status == "live") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ColorLive.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(ColorLive)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isAr) "مباشر" else "LIVE"} ${match.minute}",
                                color = ColorLive,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (match.status == "halftime") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAr) "بين الشوطين" else "HALFTIME",
                            color = PrimaryDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (match.status == "finished") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAr) "انتهت" else "FINISHED",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = match.kickoffTime,
                        fontSize = 11.sp,
                        color = SecondaryDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Club Names & Scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Club
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(PrimaryDark.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = match.homeTeam.take(1), color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) match.homeTeamAr else match.homeTeam,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Scores Box
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${match.homeScore}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (match.status == "live") ColorLive else Color.White
                    )
                    Text(text = "-", color = Color.Gray, fontSize = 16.sp)
                    Text(
                        text = "${match.awayScore}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (match.status == "live") ColorLive else Color.White
                    )
                }

                // Away Club
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) match.awayTeamAr else match.awayTeam,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(SecondaryDark.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = match.awayTeam.take(1), color = SecondaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${if (isAr) "المصدر: " else "Source: "} ${match.source}",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- Tab 2: Standings tables (Section 1) ---
@Composable
fun StandingsTabContent(viewModel: FootballViewModel) {
    val pl by viewModel.plStandings.collectAsState()
    val laliga by viewModel.laligaStandings.collectAsState()
    val saudi by viewModel.saudiStandings.collectAsState()
    val isAr = viewModel.appLanguage == "ar"

    var selectedLeagueId by remember { mutableStateOf(1) } // 1: PL, 2: LaLiga, 3: Saudi

    Column(modifier = Modifier.fillMaxSize()) {
        // League selector tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val leagues = listOf(
                Pair(1, if (isAr) "الدوري الإنجليزي" else "Premier League"),
                Pair(2, if (isAr) "الدوري الإسباني" else "La Liga"),
                Pair(3, if (isAr) "دوري روشن" else "Saudi SPL")
            )

            leagues.forEach { (id, name) ->
                Button(
                    onClick = { selectedLeagueId = id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedLeagueId == id) PrimaryDark else Color.White.copy(alpha = 0.05f),
                        contentColor = if (selectedLeagueId == id) OnPrimaryDark else Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("league_tab_$id"),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val currentList = when (selectedLeagueId) {
            1 -> pl
            2 -> laliga
            else -> saudi
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = if (isAr) "جاري استرجاع الجداول من محرك FDIE..." else "Updating live tables via FDIE...", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            // Render Table
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header metric row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "#", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                        Text(text = if (isAr) "النادي" else "Club Name", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(text = "PL", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        Text(text = "GD", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                        Text(text = "PTS", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                    }

                    Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentList) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Position
                                Text(
                                    text = "${row.position}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (row.position <= 4) PrimaryDark else Color.White,
                                    modifier = Modifier.width(24.dp)
                                )
                                // Team Name
                                Text(
                                    text = if (isAr) row.teamNameAr else row.teamName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Played games
                                Text(
                                    text = "${row.played}",
                                    fontSize = 12.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.width(28.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Goal diff
                                val diff = row.goalsFor - row.goalsAgainst
                                Text(
                                    text = "${if (diff > 0) "+" else ""}$diff",
                                    fontSize = 12.sp,
                                    color = if (diff > 0) PrimaryDark else Color.LightGray,
                                    modifier = Modifier.width(28.dp),
                                    textAlign = TextAlign.Center
                                )
                                // Points
                                Text(
                                    text = "${row.points}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (row.position <= 4) PrimaryDark else SecondaryDark,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Tab 3: News Grid tab (Section 1) ---
@Composable
fun NewsTabContent(viewModel: FootballViewModel) {
    val news by viewModel.newsList.collectAsState()
    val isAr = viewModel.appLanguage == "ar"

    if (news.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryDark)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(news) { article ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Title
                        Text(
                            text = if (isAr) article.titleAr else article.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        Text(
                            text = if (isAr) article.descriptionAr else article.description,
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
                            Text(text = article.publishTime, fontSize = 11.sp, color = Color.Gray)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryDark.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "TRANSFER NEWS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Tab 4: AI Strategic Content Studio (Section 13) ---
@Composable
fun AiStudioTabContent(viewModel: FootballViewModel) {
    val isAr = viewModel.appLanguage == "ar"
    var matchInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("tactical") } // "tactical" | "script" | "viral"

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = if (isAr) "استوديو توليد المحتوى التكتيكي AI" else "AI Content Sport Studio",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = Color.White
        )
        Text(
            text = if (isAr) "ولّد منشورات رياضية، ومقالات تكتيكية، وسيناريوهات تيك توك بجلب بيانات جوجل فورياً!" else "Generate viral analytics briefs or shorts scripts powered by Gemini Search Grounding.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Type Selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val types = listOf(
                Pair("tactical", if (isAr) "تقرير تكتيكي" else "Tactical Post"),
                Pair("script", if (isAr) "سيناريو فيديو" else "TikTok Script"),
                Pair("viral", if (isAr) "فكرة تغريدة" else "Viral Tweet")
            )

            types.forEach { (type, name) ->
                val isSelected = selectedType == type
                Button(
                    onClick = { selectedType = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) SecondaryDark else Color.White.copy(alpha = 0.05f),
                        contentColor = if (isSelected) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("ai_type_$type"),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Match Input Field
        OutlinedTextField(
            value = matchInput,
            onValueChange = { matchInput = it },
            placeholder = { Text(text = if (isAr) "ادخل سياق المباراة (مثال: ريال مدريد يفوز على برشلونة 3-2)..." else "Enter match context e.g., Liverpool beats Chelsea with a last-minute volley...", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.03f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                focusedBorderColor = SecondaryDark,
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .testTag("ai_match_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Trigger Button
        Button(
            onClick = { viewModel.runAiStudioContentGenerator(selectedType, matchInput) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark, contentColor = OnPrimaryDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_ai_studio"),
            enabled = !viewModel.isGeneratingAiStudio
        ) {
            if (viewModel.isGeneratingAiStudio) {
                CircularProgressIndicator(color = OnPrimaryDark, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isAr) "جاري التحليل وتوليد الأفكار..." else "Analyzing Grounding Feeds...")
            } else {
                Text(text = if (isAr) "ولّد محتوى تكتيكي بالذكاء الاصطناعي 🚀" else "Generate Grounded AI Content 🚀", fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Output Screen
        val aiOut = viewModel.aiStudioOutput
        if (aiOut != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SecondaryDark.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = aiOut.title, fontWeight = FontWeight.Black, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryDark.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "${if (isAr) "شهرة وانتشار " else "VIRAL "} ${aiOut.viralScore}%", fontWeight = FontWeight.Bold, color = PrimaryDark, fontSize = 10.sp)
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.05f))

                    Text(text = if (isAr) "🎯 الفكرة والصنارة (Hook):" else "🎯 Hooks & Entry Points:", fontWeight = FontWeight.Bold, color = SecondaryDark, fontSize = 13.sp)
                    Text(text = aiOut.hook, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)

                    Text(text = if (isAr) "✍️ نص المحتوى (Script / Article):" else "✍️ Main Core Content Script:", fontWeight = FontWeight.Bold, color = SecondaryDark, fontSize = 13.sp)
                    Text(text = aiOut.script, color = Color.LightGray, fontSize = 13.sp, lineHeight = 18.sp)

                    Text(text = if (isAr) "📱 العنوان الترويجي والوسوم (Caption):" else "📱 Promotion Caption:", fontWeight = FontWeight.Bold, color = SecondaryDark, fontSize = 13.sp)
                    Text(text = "${aiOut.caption}\n${aiOut.hashtags}", color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)

                    Text(text = if (isAr) "🎨 فكرة التصميم المرئي المصاحب:" else "🎨 Visual Recommendations:", fontWeight = FontWeight.Bold, color = SecondaryDark, fontSize = 13.sp)
                    Text(text = aiOut.visualSuggestions, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

// --- Tab 5: Admin Panel & crawler state checks (Section 2 & 15) ---
@Composable
fun AdminTabContent(viewModel: FootballViewModel) {
    val isAr = viewModel.appLanguage == "ar"
    val logs by viewModel.systemLogs.collectAsState()
    val health by viewModel.collectorHealth.collectAsState()

    // Forms states
    var homeTeam by remember { mutableStateOf("") }
    var awayTeam by remember { mutableStateOf("") }
    var homeScore by remember { mutableStateOf("0") }
    var awayScore by remember { mutableStateOf("0") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // System Health Section
        item {
            Text(text = if (isAr) "حالة مجمعات البيانات (FDIE)" else "FDIE System Health Status", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    health.forEach { (collector, state) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = collector, color = Color.LightGray, fontSize = 12.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (state == "HEALTHY") Color(0xFF065F46) else Color(0xFF991B1B)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = state,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state == "HEALTHY") Color(0xFF34D399) else Color(0xFFFCA5A5)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Score Admin Creator (Section 2 & 11)
        item {
            Text(text = if (isAr) "لوحة التحكم: إضافة نتيجة فورية" else "Developer Console: Inject Instat Score Alert", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = homeTeam,
                            onValueChange = { homeTeam = it },
                            placeholder = { Text(text = "Home Club") },
                            modifier = Modifier.weight(1f).testTag("admin_home_team")
                        )
                        OutlinedTextField(
                            value = awayTeam,
                            onValueChange = { awayTeam = it },
                            placeholder = { Text(text = "Away Club") },
                            modifier = Modifier.weight(1f).testTag("admin_away_team")
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = homeScore,
                            onValueChange = { homeScore = it },
                            placeholder = { Text(text = "0") },
                            modifier = Modifier.weight(1f).testTag("admin_home_score")
                        )
                        OutlinedTextField(
                            value = awayScore,
                            onValueChange = { awayScore = it },
                            placeholder = { Text(text = "0") },
                            modifier = Modifier.weight(1f).testTag("admin_away_score")
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.addManualMatchScore(
                                home = homeTeam, away = awayTeam,
                                homeAr = homeTeam, awayAr = awayTeam,
                                homeScore = homeScore.toIntOrNull() ?: 0, awayScore = awayScore.toIntOrNull() ?: 0,
                                minute = "88'", league = "Champions League", leagueAr = "دوري الأبطال",
                                status = "live"
                            )
                            viewModel.triggerGoalNotificationAlert("custom_game", "$homeTeam $homeScore - $awayScore $awayTeam")
                            homeTeam = ""
                            awayTeam = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                        modifier = Modifier.fillMaxWidth().testTag("admin_submit")
                    ) {
                        Text(text = if (isAr) "إضافة النتيجة وتفعيل التنبيه 📣" else "Submit and fire goal notification alert 📣", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // IPTV M3U Importer (Section 15)
        item {
            Text(text = if (isAr) "استيراد قائمة تشغيل قنوات IPTV (.M3U)" else "IPTV Playlist (.M3U) uploader terminal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = viewModel.customM3uText,
                        onValueChange = { viewModel.customM3uText = it },
                        placeholder = { Text(text = "#EXTM3U\n#EXTINF:-1 tvg-logo=\"some_logo.png\" group-title=\"beIN\",beIN Sports 1\nhttp://your-iptv-server-link.com/live/some...") },
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("admin_m3u_input")
                    )

                    Button(
                        onClick = { viewModel.importM3uChannels() },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryDark),
                        modifier = Modifier.fillMaxWidth().testTag("admin_m3u_submit")
                    ) {
                        Text(text = if (isAr) "بدء فحص واستيراد القنوات 🚀" else "Import Streams Playlist 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Logs Section
        item {
            Text(text = if (isAr) "سجلات تشغيل نظام ذكاء البيانات" else "FDIE System Runtime logs Console", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(12.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                color = Color(0xFF34D399),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Glassmorphic local navigation bar ---
@Composable
fun GlassyBottomNavigationBar(viewModel: FootballViewModel) {
    val isAr = viewModel.appLanguage == "ar"
    val navItems = listOf(
        Triple("matches", if (isAr) "مباريات" else "Fixtures", Icons.Default.SportsSoccer),
        Triple("standings", if (isAr) "ترتيب" else "Standings", Icons.Default.FormatListNumbered),
        Triple("streams", if (isAr) "البث" else "Live IPTV", Icons.Default.Tv),
        Triple("news", if (isAr) "أخبار" else "News", Icons.Default.Article),
        Triple("ai_studio", if (isAr) "ذكاء AI" else "AI Studio", Icons.Default.AutoAwesome),
        Triple("admin", if (isAr) "لوحة" else "Console", Icons.Default.Construction)
    )

    NavigationBar(
        containerColor = Color(0xFF0F172A).copy(alpha = 0.9f),
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        navItems.forEach { (route, label, icon) ->
            val isSelected = viewModel.selectedTab == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.selectedTab = route },
                icon = {
                    Icon(imageVector = icon, contentDescription = label)
                },
                label = {
                    Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OnPrimaryDark,
                    selectedTextColor = PrimaryDark,
                    indicatorColor = PrimaryDark,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}
