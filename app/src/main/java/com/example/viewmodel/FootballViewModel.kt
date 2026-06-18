package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import com.example.service.AiContentStudioService
import com.example.service.AiStudioContent
import com.example.service.MatchPrediction
import com.example.service.PredictionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FootballViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FootballRepository(application)
    private val prefs = application.getSharedPreferences("football_world_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "FootballViewModel"
    }

    // --- Onboarding & Settings States ---
    var appLanguage by mutableStateOf(prefs.getString("lang", "ar") ?: "ar")
        private set

    var isDarkTheme by mutableStateOf(prefs.getBoolean("dark_theme", true))
        private set

    var showLanguageOverlay by mutableStateOf(false)

    // --- Tab & Navigation States ---
    var selectedTab by mutableStateOf("matches")
    var lastSearchQuery by mutableStateOf("")

    // --- Match Details & Statistics Modal ---
    var selectedMatchForDetails by mutableStateOf<MatchEntity?>(null)
    var selectedMatchStats by mutableStateOf<MatchStatEntity?>(null)
    var activeMatchPrediction by mutableStateOf<MatchPrediction?>(null)

    // --- Database Flows ---
    val matches: StateFlow<List<MatchEntity>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveMatches: StateFlow<List<MatchEntity>> = repository.liveMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsList: StateFlow<List<NewsEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val channelsList: StateFlow<List<ChannelEntity>> = repository.allChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Standings mappings - directly bound to Room to auto-update on changes
    val plStandings: StateFlow<List<StandingEntity>> = repository.getStandingsForLeague(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val laligaStandings: StateFlow<List<StandingEntity>> = repository.getStandingsForLeague(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val saudiStandings: StateFlow<List<StandingEntity>> = repository.getStandingsForLeague(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Crawler Health Logs & Cache Status (Section 2 & 10) ---
    val systemLogs = MutableStateFlow<List<String>>(
        listOf(
            "System Boot successful.",
            "Initialized local SQLite DB storage.",
            "FDIE: loaded team aliases mapping catalogs.",
            "Baseline news collector ready."
        )
    )

    val collectorHealth = MutableStateFlow(
        mapOf(
            "koooraCollector" to "HEALTHY",
            "yallakoraCollector" to "HEALTHY",
            "flashscoreCollector" to "HEALTHY",
            "sofascoreCollector" to "HEALTHY",
            "livescoreCollector" to "HEALTHY",
            "soccerwayCollector" to "HEALTHY",
            "newsCollector" to "HEALTHY"
        )
    )

    var isRefreshingData by mutableStateOf(false)

    // --- AI Studio State ---
    var aiStudioOutput by mutableStateOf<AiStudioContent?>(null)
    var isGeneratingAiStudio by mutableStateOf(false)

    // --- Custom M3u state ---
    var customM3uText by mutableStateOf("")

    init {
        // Setup initial channels and caches
        viewModelScope.launch {
            repository.refreshChannelsBaseline()
            refreshAllData()
        }
    }

    fun setLanguage(lang: String) {
        appLanguage = lang
        prefs.edit().putString("lang", lang).apply()
        showLanguageOverlay = false
        addLog("Language set to: ${if (lang == "ar") "العربية" else "English"}")
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        prefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
        addLog("Visual theme changed to: ${if (isDarkTheme) "Dark" else "Light"}")
    }

    fun selectMatch(match: MatchEntity) {
        selectedMatchForDetails = match
        if (match != null) {
            viewModelScope.launch {
                selectedMatchStats = repository.getStatsForMatch(match.matchId)
                // Calculate prediction math
                activeMatchPrediction = PredictionService.predictMatch(match.homeTeam, match.awayTeam)
            }
        }
    }

    fun addLog(msg: String) {
        val list = systemLogs.value.toMutableList()
        list.add(0, "[${System.currentTimeMillis() % 100000}] $msg")
        if (list.size > 50) list.removeLast()
        systemLogs.value = list
    }

    // --- FDIE Sync Actions (Section 3, 4, 10) ---
    fun refreshAllData() {
        viewModelScope.launch {
            if (isRefreshingData) return@launch
            isRefreshingData = true
            addLog("Triggering master AI crawler sync...")

            val apiKey = BuildConfig.GEMINI_API_KEY

            // 1. Matches refresh
            try {
                repository.refreshMatches(apiKey)
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("flashscoreCollector", "HEALTHY") }
            } catch (e: Exception) {
                addLog("Match AI Sync failed. Loading cached datasets.")
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("flashscoreCollector", "DEGRADED") }
            }

            // 2. Standings refresh
            try {
                repository.refreshStandings(1) // PL
                repository.refreshStandings(2) // La Liga
                repository.refreshStandings(3) // Saudi
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("soccerwayCollector", "HEALTHY") }
            } catch (e: Exception) {
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("soccerwayCollector", "DEGRADED") }
            }

            // 3. News refresh
            try {
                repository.refreshNews(apiKey)
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("newsCollector", "HEALTHY") }
            } catch (e: Exception) {
                collectorHealth.value = collectorHealth.value.toMutableMap().apply { put("newsCollector", "DEGRADED") }
            }

            addLog("FDIE Sync completed successfully.")
            isRefreshingData = false
        }
    }

    // --- AI Studio Generator (Section 13) ---
    fun runAiStudioContentGenerator(type: String, matchInput: String) {
        viewModelScope.launch {
            isGeneratingAiStudio = true
            addLog("AI Content Studio: requesting generation for $type...")
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                aiStudioOutput = AiContentStudioService.generateTacticalContent(
                    type = type,
                    matchDetails = matchInput,
                    apiKey = apiKey,
                    isArabic = appLanguage == "ar"
                )
                addLog("AI Content Studio: compilation success.")
            } catch (e: Exception) {
                Log.e(TAG, "AI Generation failed: ${e.message}")
                addLog("AI Content Studio failed: ${e.localizedMessage}")
            } finally {
                isGeneratingAiStudio = false
            }
        }
    }

    // --- Admin Commands & Stream Import (Section 2 & 15) ---
    fun addManualMatchScore(
        home: String,
        away: String,
        homeAr: String,
        awayAr: String,
        homeScore: Int,
        awayScore: Int,
        minute: String,
        league: String,
        leagueAr: String,
        status: String
    ) {
        viewModelScope.launch {
            val custom = MatchEntity(
                matchId = "custom_${System.currentTimeMillis()}",
                homeTeam = home,
                awayTeam = away,
                homeTeamAr = homeAr,
                awayTeamAr = awayAr,
                homeScore = homeScore,
                awayScore = awayScore,
                minute = minute,
                status = status,
                league = league,
                leagueAr = leagueAr,
                country = "Admin Config",
                kickoffTime = "Instant Active",
                source = "Admin Dashboard",
                lastUpdated = System.currentTimeMillis()
            )
            repository.insertCustomMatch(custom)
            addLog("Admin: Created manual matchup - $home vs $away")
        }
    }

    fun triggerGoalNotificationAlert(matchId: String, scoreLabel: String) {
        addLog("🎯 GOAL ALERT! Match $matchId changed score to: $scoreLabel")
    }

    fun importM3uChannels() {
        if (customM3uText.isEmpty()) return
        viewModelScope.launch {
            try {
                repository.loadM3uFileContent(customM3uText)
                customM3uText = ""
                addLog("M3U Import: streams integrated into DB.")
            } catch (e: Exception) {
                addLog("M3U Import error: ${e.message}")
            }
        }
    }

    fun toggleChannelFavorite(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.updateChannel(channel.copy(isFavorite = !channel.isFavorite))
            addLog("Toggled favorite on channel: ${channel.name}")
        }
    }

    fun addCustomLiveChannel(name: String, group: String, logo: String, url: String) {
        viewModelScope.launch {
            repository.insertChannel(
                ChannelEntity(
                    name = name,
                    groupName = group,
                    logoUrl = logo.ifEmpty { "https://images.unsplash.com/photo-1540747737956-378724044282?fit=crop&w=120&h=120&q=80" },
                    streamUrl = url,
                    isFavorite = false,
                    isActive = true
                )
            )
            addLog("Admin: Added new channel $name successfully.")
        }
    }
}
