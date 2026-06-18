package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class FootballRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.footballDao()

    companion object {
        private const val TAG = "FootballRepository"
    }

    // --- Matches ---
    val allMatches: Flow<List<MatchEntity>> = dao.getAllMatches()
    val liveMatches: Flow<List<MatchEntity>> = dao.getLiveMatches()

    suspend fun getMatchById(id: String): MatchEntity? {
        return dao.getMatchById(id)
    }

    suspend fun refreshMatches(apiKey: String) {
        try {
            Log.d(TAG, "Refreshing matches via FDIE...")
            val list = FootballDataIntelligenceEngine.collectTodayMatchesFromAI(apiKey)
            if (list.isNotEmpty()) {
                dao.insertMatches(list)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing matches: ${e.message}")
            // Double fallback insert if empty
            val current = allMatches.firstOrNull()
            if (current.isNullOrEmpty()) {
                dao.insertMatches(FootballDataIntelligenceEngine.getMockMatches())
            }
        }
    }

    suspend fun insertCustomMatch(match: MatchEntity) {
        dao.insertMatches(listOf(match))
    }

    // --- Stats & Events ---
    suspend fun getStatsForMatch(matchId: String): MatchStatEntity {
        val existing = dao.getStatsForMatch(matchId)
        if (existing != null) return existing

        // Create realistic mock stats based on team names
        val homeScore = matchId.hashCode() % 3 + 1
        val awayScore = (matchId.hashCode() / 2) % 3
        val stats = MatchStatEntity(
            matchId = matchId,
            possessionHome = if (homeScore > awayScore) 55 else 45,
            possessionAway = if (homeScore > awayScore) 45 else 55,
            shotsHome = 12 + homeScore * 2,
            shotsAway = 8 + awayScore * 2,
            foulsHome = 10,
            foulsAway = 11,
            cornersHome = 6,
            cornersAway = 4
        )
        dao.insertMatchStats(stats)
        return stats
    }

    fun getEventsForMatch(matchId: String): Flow<List<MatchEventEntity>> {
        return dao.getEventsForMatch(matchId)
    }

    suspend fun insertMatchEvents(events: List<MatchEventEntity>) {
        dao.insertMatchEvents(events)
    }

    // --- Standings ---
    fun getStandingsForLeague(leagueId: Int): Flow<List<StandingEntity>> {
        return dao.getStandingsForLeague(leagueId)
    }

    suspend fun refreshStandings(leagueId: Int) {
        try {
            val list = FootballDataIntelligenceEngine.getMockStandings(leagueId)
            dao.insertStandings(list)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading standings: ${e.message}")
        }
    }

    // --- News ---
    val allNews: Flow<List<NewsEntity>> = dao.getAllNews()

    suspend fun refreshNews(apiKey: String) {
        try {
            Log.d(TAG, "Refreshing sports news...")
            val list = FootballDataIntelligenceEngine.collectNewsFromAI(apiKey)
            if (list.isNotEmpty()) {
                dao.insertNews(list)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing news: ${e.message}")
            val current = allNews.firstOrNull()
            if (current.isNullOrEmpty()) {
                dao.insertNews(FootballDataIntelligenceEngine.getMockNews())
            }
        }
    }

    // --- Channels ---
    val allChannels: Flow<List<ChannelEntity>> = dao.getAllChannels()

    suspend fun refreshChannelsBaseline() {
        val list = allChannels.firstOrNull()
        if (list.isNullOrEmpty()) {
            val baseline = listOf(
                ChannelEntity(
                    name = "beIN Sports 1 HD",
                    groupName = "beIN Channels",
                    logoUrl = "https://images.unsplash.com/photo-1540747737956-378724044282?fit=crop&w=120&h=120&q=80",
                    streamUrl = "https://playertest.longtailvideo.com/adaptive/oceans/oceans.m3u8",
                    isFavorite = true,
                    isActive = true
                ),
                ChannelEntity(
                    name = "SSC Sports 1 HD",
                    groupName = "SSC Network",
                    logoUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?fit=crop&w=120&h=120&q=80",
                    streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                    isFavorite = false,
                    isActive = true
                ),
                ChannelEntity(
                    name = "OnTime Sports 1",
                    groupName = "OnTime Network",
                    logoUrl = "https://images.unsplash.com/photo-1518063319789-7217e6706b04?fit=crop&w=120&h=120&q=80",
                    streamUrl = "https://playertest.longtailvideo.com/adaptive/oceans/oceans.m3u8",
                    isFavorite = false,
                    isActive = true
                )
            )
            for (c in baseline) {
                dao.insertChannel(c)
            }
        }
    }

    suspend fun insertChannel(channel: ChannelEntity) {
        dao.insertChannel(channel)
    }

    suspend fun updateChannel(channel: ChannelEntity) {
        dao.updateChannel(channel)
    }

    suspend fun deleteChannel(channelId: Int) {
        dao.deleteChannelById(channelId)
    }

    suspend fun loadM3uFileContent(m3uContent: String) {
        val lines = m3uContent.lineSequence().toList()
        var currentName = ""
        var currentGroup = "Imported"
        var currentLogo = ""
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                // Parse Name
                val namePart = trimmed.substringAfterLast(",")
                if (namePart.isNotEmpty()) {
                    currentName = namePart
                }
                // Parse Logo
                if (trimmed.contains("tvg-logo=\"")) {
                    currentLogo = trimmed.substringAfter("tvg-logo=\"").substringBefore("\"")
                }
                // Parse Group
                if (trimmed.contains("group-title=\"")) {
                    currentGroup = trimmed.substringAfter("group-title=\"").substringBefore("\"")
                }
            } else if (trimmed.startsWith("http")) {
                if (currentName.isNotEmpty()) {
                    val entity = ChannelEntity(
                        name = currentName,
                        groupName = currentGroup,
                        logoUrl = currentLogo.ifEmpty { "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?fit=crop&w=120&h=120&q=80" },
                        streamUrl = trimmed,
                        isFavorite = false,
                        isActive = true
                    )
                    dao.insertChannel(entity)
                    // Reset
                    currentName = ""
                    currentLogo = ""
                    currentGroup = "Imported"
                }
            }
        }
    }
}
