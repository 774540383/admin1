package com.example.data

import android.util.Log
import com.example.data.aliases.LeagueAliases
import com.example.data.aliases.TeamAliases
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object FootballDataIntelligenceEngine {
    private const val TAG = "FDIE_Engine"

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // --- Dynamic Fallback Baselines ---
    fun getMockMatches(): List<MatchEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MatchEntity(
                matchId = "m_rm_bar_1",
                homeTeam = "Real Madrid",
                awayTeam = "Barcelona",
                homeTeamAr = "ريال مدريد",
                awayTeamAr = "برشلونة",
                homeScore = 3,
                awayScore = 2,
                minute = "FT",
                status = "finished",
                league = "La Liga",
                leagueAr = "الدوري الإسباني",
                country = "Spain",
                kickoffTime = "Yesterday 21:00",
                source = "LaLiga Live Feed",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_liv_mci_2",
                homeTeam = "Liverpool",
                awayTeam = "Manchester City",
                homeTeamAr = "ليفربول",
                awayTeamAr = "مانشستر سيتي",
                homeScore = 2,
                awayScore = 2,
                minute = "78'",
                status = "live",
                league = "Premier League",
                leagueAr = "الدوري الإنجليزي الممتاز",
                country = "England",
                kickoffTime = "Today 18:30",
                source = "SofaScore Stats",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_hil_nas_3",
                homeTeam = "Al Hilal",
                awayTeam = "Al Nassr",
                homeTeamAr = "الهلال",
                awayTeamAr = "النصر",
                homeScore = 2,
                awayScore = 1,
                minute = "45'",
                status = "live",
                league = "Saudi Pro League",
                leagueAr = "دوري روشن السعودي",
                country = "Saudi Arabia",
                kickoffTime = "Today 21:00",
                source = "SPL Official Media",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_ars_che_4",
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeTeamAr = "أرسنال",
                awayTeamAr = "تشيلسي",
                homeScore = 0,
                awayScore = 0,
                minute = "15'",
                status = "live",
                league = "Premier League",
                leagueAr = "الدوري الإنجليزي الممتاز",
                country = "England",
                kickoffTime = "Today 22:15",
                source = "Aston Opta Data",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_int_mil_5",
                homeTeam = "Inter Milan",
                awayTeam = "AC Milan",
                homeTeamAr = "إنتر ميلان",
                awayTeamAr = "ميلان",
                homeScore = 1,
                awayScore = 1,
                minute = "HT",
                status = "halftime",
                league = "Serie A",
                leagueAr = "الدوري الإيطالي",
                country = "Italy",
                kickoffTime = "Today 19:45",
                source = "SerieA Live Feed",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_ahl_zam_6",
                homeTeam = "Al Ahly",
                awayTeam = "Zamalek",
                homeTeamAr = "الأهلي المصري",
                awayTeamAr = "الزمالك",
                homeScore = 0,
                awayScore = 0,
                minute = "20:00",
                status = "scheduled",
                league = "Egyptian Premier League",
                leagueAr = "الدوري المصري الممتاز",
                country = "Egypt",
                kickoffTime = "Today 20:00",
                source = "ONTime Live Feed",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_bay_dor_7",
                homeTeam = "Bayern Munich",
                awayTeam = "Borussia Dortmund",
                homeTeamAr = "بايرن ميونخ",
                awayTeamAr = "بوروسيا دورتموند",
                homeScore = 4,
                awayScore = 2,
                minute = "FT",
                status = "finished",
                league = "Bundesliga",
                leagueAr = "الدوري الألماني",
                country = "Germany",
                kickoffTime = "Yesterday 18:30",
                source = "Bundesliga Media",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_psg_om_8",
                homeTeam = "Paris Saint-Germain",
                awayTeam = "Marseille",
                homeTeamAr = "باريس سان جيرمان",
                awayTeamAr = "مارسيليا",
                homeScore = 0,
                awayScore = 0,
                minute = "21:45",
                status = "scheduled",
                league = "Ligue 1",
                leagueAr = "الدوري الفرنسي",
                country = "France",
                kickoffTime = "Tomorrow 21:45",
                source = "Ligue1 Live Stats",
                lastUpdated = now
            ),
            MatchEntity(
                matchId = "m_atm_rma_9",
                homeTeam = "Atletico Madrid",
                awayTeam = "Real Madrid",
                homeTeamAr = "أتلتيكو مدريد",
                awayTeamAr = "ريال مدريد",
                homeScore = 0,
                awayScore = 0,
                minute = "22:00",
                status = "scheduled",
                league = "La Liga",
                leagueAr = "الدوري الإسباني",
                country = "Spain",
                kickoffTime = "Tomorrow 22:00",
                source = "LaLiga Live Feed",
                lastUpdated = now
            )
        )
    }

    fun getMockStandings(leagueId: Int): List<StandingEntity> {
        val list = mutableListOf<StandingEntity>()
        when (leagueId) {
            1 -> { // Premier League
                list.add(StandingEntity(leagueId = 1, teamId = 101, teamName = "Manchester City", teamNameAr = "مانشستر سيتي", played = 38, won = 28, drawn = 7, lost = 3, goalsFor = 96, goalsAgainst = 34, points = 91, position = 1))
                list.add(StandingEntity(leagueId = 1, teamId = 102, teamName = "Arsenal", teamNameAr = "أرسنال", played = 38, won = 28, drawn = 5, lost = 5, goalsFor = 91, goalsAgainst = 29, points = 89, position = 2))
                list.add(StandingEntity(leagueId = 1, teamId = 103, teamName = "Liverpool", teamNameAr = "ليفربول", played = 38, won = 24, drawn = 10, lost = 4, goalsFor = 86, goalsAgainst = 41, points = 82, position = 3))
                list.add(StandingEntity(leagueId = 1, teamId = 104, teamName = "Aston Villa", teamNameAr = "أستون فيلا", played = 38, won = 20, drawn = 8, lost = 10, goalsFor = 76, goalsAgainst = 61, points = 68, position = 4))
                list.add(StandingEntity(leagueId = 1, teamId = 105, teamName = "Tottenham", teamNameAr = "توتنهام", played = 38, won = 20, drawn = 6, lost = 12, goalsFor = 74, goalsAgainst = 61, points = 66, position = 5))
            }
            2 -> { // La Liga
                list.add(StandingEntity(leagueId = 2, teamId = 201, teamName = "Real Madrid", teamNameAr = "ريال مدريد", played = 38, won = 29, drawn = 8, lost = 1, goalsFor = 87, goalsAgainst = 22, points = 95, position = 1))
                list.add(StandingEntity(leagueId = 2, teamId = 202, teamName = "Barcelona", teamNameAr = "برشلونة", played = 38, won = 26, drawn = 7, lost = 5, goalsFor = 79, goalsAgainst = 44, points = 85, position = 2))
                list.add(StandingEntity(leagueId = 2, teamId = 203, teamName = "Girona", teamNameAr = "جيرونا", played = 38, won = 25, drawn = 6, lost = 7, goalsFor = 85, goalsAgainst = 46, points = 81, position = 3))
                list.add(StandingEntity(leagueId = 2, teamId = 204, teamName = "Atletico Madrid", teamNameAr = "أتلتيكو مدريد", played = 38, won = 24, drawn = 4, lost = 10, goalsFor = 70, goalsAgainst = 43, points = 76, position = 4))
            }
            3 -> { // Saudi Pro League
                list.add(StandingEntity(leagueId = 3, teamId = 301, teamName = "Al Hilal", teamNameAr = "الهلال", played = 34, won = 31, drawn = 3, lost = 0, goalsFor = 101, goalsAgainst = 23, points = 96, position = 1))
                list.add(StandingEntity(leagueId = 3, teamId = 302, teamName = "Al Nassr", teamNameAr = "النصر", played = 34, won = 26, drawn = 4, lost = 4, goalsFor = 100, goalsAgainst = 42, points = 82, position = 2))
                list.add(StandingEntity(leagueId = 3, teamId = 303, teamName = "Al Ahli", teamNameAr = "الأهلي السعودي", played = 34, won = 19, drawn = 8, lost = 7, goalsFor = 67, goalsAgainst = 35, points = 65, position = 3))
                list.add(StandingEntity(leagueId = 3, teamId = 304, teamName = "Al Ittihad", teamNameAr = "الاتحاد", played = 34, won = 16, drawn = 6, lost = 12, goalsFor = 63, goalsAgainst = 54, points = 54, position = 4))
            }
        }
        return list
    }

    fun getMockNews(): List<NewsEntity> {
        return listOf(
            NewsEntity(
                title = "Mbappe Sparkles in Training: Ready for the Ultimate Season Opener",
                titleAr = "مبابي يتألق في التدريبات: مستعد لافتتاحية الموسم الواعدة",
                description = "Kylian Mbappe has impressed coaching staff during pre-season workouts, showcasing stellar pace and surgical finishing ahead of Real Madrid's key matchup.",
                descriptionAr = "أبهر كيليان مبابي الطاقم التدريبي خلال التدريبات التحضيرية للموسم، مظهرًا سرعة فائقة وإنهاءً قاتلاً أمام مرمى الخصوم.",
                imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
                sourceUrl = "https://www.marca.com",
                publishTime = "30 mins ago",
                content = "Full content is stored in local database."
            ),
            NewsEntity(
                title = "Al-Hilal Set to Secure Groundbreaking Defensive Signing",
                titleAr = "الهلال يقترب من حسم صفقة دفاعية كبرى غير مسبوقة",
                description = "Negotiations have reached final stages. The Saudi Champions are looking to bolster their backline to safeguard their historic domestic invincibility.",
                descriptionAr = "وصلت المفاوضات إلى مراحلها النهائية. يسعى بطل الدوري السعودي لتعزيز خطوطه الخلفية للحفاظ على سجله التاريخي الخالي من الهزائم.",
                imageUrl = "https://images.unsplash.com/photo-1540747737956-378724044282?auto=format&fit=crop&w=800&q=80",
                sourceUrl = "https://www.kooora.com",
                publishTime = "2 hours ago",
                content = "Full content in database."
            ),
            NewsEntity(
                title = "Premier League Clubs Vote to Retain VAR with Critical Operations Upgrades",
                titleAr = "أندية الدوري الإنجليزي تصوت لصالح استمرار تقنية الفيديو مع تحديثات هامة",
                description = "The decision guarantees the continuation of VAR while promising to implement automated offside systems to accelerate decision-making.",
                descriptionAr = "يضمن هذا القرار استمرار تشغيل تقنية الفيديو بينما يعد بإدخال نظام التسلل شبه التلقائي لتسريع اتخاذ القرارات التحكيمية.",
                imageUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80",
                sourceUrl = "https://sky-sports.com",
                publishTime = "4 hours ago",
                content = "Details are here."
            )
        )
    }

    // --- Gemini Search Grounding API collectors ---
    suspend fun collectTodayMatchesFromAI(apiKey: String): List<MatchEntity> = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Empty Gemini API Key, using mock matches baseline.")
            return@withContext getMockMatches()
        }

        val prompt = """
            Search google for live soccer matches, today's matches, results, and fixtures.
            Focus on major leagues: Premier League, La Liga, Serie A, Champions League, Saudi Pro League, Egyptian Premier League.
            Return ONLY a valid JSON array of objects representing matches. Do NOT include markdown blocks, text descriptions, or code fences (e.g. no ```json blocks). 
            JSON Schema for each match object:
            {
              "matchId": "string-unique-id",
              "homeTeam": "home team name in English",
              "awayTeam": "away team name in English",
              "homeTeamAr": "home team name in Arabic",
              "awayTeamAr": "away team name in Arabic",
              "homeScore": integer,
              "awayScore": integer,
              "minute": "e.g. 84', HT, FT, or kickoff time like 21:00",
              "status": "scheduled" or "live" or "halftime" or "finished",
              "league": "league name in English",
              "leagueAr": "league name in Arabic",
              "country": "country name",
              "kickoffTime": "today's or kickoff schedule HH:mm"
            }
        """.trimIndent()

        try {
            val response = NetworkClient.geminiService.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    tools = listOf(GeminiTool(googleSearchRetrieval = GeminiSearchRetrieval()))
                )
            )

            val rawJsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (rawJsonText != null) {
                val cleanedJson = cleanJsonResponse(rawJsonText)
                val matches = parseMatchesJson(cleanedJson)
                if (matches.isNotEmpty()) {
                    return@withContext matches
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during AI match collection: ${e.message}", e)
        }

        return@withContext getMockMatches()
    }

    suspend fun collectNewsFromAI(apiKey: String): List<NewsEntity> = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getMockNews()
        }

        val prompt = """
            Search google for the latest football and soccer news, transfers, and match summaries.
            Return ONLY a valid JSON array of objects. Do not write markdown markers.
            JSON Schema:
            {
              "title": "News Title in English",
              "titleAr": "News Title in Arabic",
              "description": "Short description in English",
              "descriptionAr": "Short description in Arabic",
              "imageUrl": "valid sport image URL or placeholder",
              "sourceUrl": "original source URL",
              "publishTime": "relative time e.g., 2 hours ago",
              "content": "detailed summary text"
            }
        """.trimIndent()

        try {
            val response = NetworkClient.geminiService.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    tools = listOf(GeminiTool(googleSearchRetrieval = GeminiSearchRetrieval()))
                )
            )

            val rawJsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (rawJsonText != null) {
                val cleanedJson = cleanJsonResponse(rawJsonText)
                val news = parseNewsJson(cleanedJson)
                if (news.isNotEmpty()) {
                    return@withContext news
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during AI news collection: ${e.message}")
        }

        return@withContext getMockNews()
    }

    // --- Helper JSON processing ---
    private fun cleanJsonResponse(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.substring(7)
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3)
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length - 3)
        }
        return clean.trim()
    }

    private fun parseMatchesJson(jsonStr: String): List<MatchEntity> {
        val list = mutableListOf<MatchEntity>()
        try {
            val arr = JSONArray(jsonStr)
            val now = System.currentTimeMillis()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val mId = obj.optString("matchId", "id_$i")
                val home = obj.optString("homeTeam")
                val away = obj.optString("awayTeam")
                list.add(
                    MatchEntity(
                        matchId = mId,
                        homeTeam = home,
                        awayTeam = away,
                        homeTeamAr = obj.optString("homeTeamAr", TeamAliases.translate(home, true)),
                        awayTeamAr = obj.optString("awayTeamAr", TeamAliases.translate(away, true)),
                        homeScore = obj.optInt("homeScore", 0),
                        awayScore = obj.optInt("awayScore", 0),
                        minute = obj.optString("minute", "0'"),
                        status = obj.optString("status", "scheduled"),
                        league = obj.optString("league", "Other League"),
                        leagueAr = obj.optString("leagueAr", LeagueAliases.translate(obj.optString("league"), true)),
                        country = obj.optString("country", "Global"),
                        kickoffTime = obj.optString("kickoffTime", "Today"),
                        source = "FDIE AI Crawler",
                        lastUpdated = now
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse matches JSON: ${e.message}\nJSON String: $jsonStr")
        }
        return list
    }

    private fun parseNewsJson(jsonStr: String): List<NewsEntity> {
        val list = mutableListOf<NewsEntity>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    NewsEntity(
                        title = obj.optString("title"),
                        titleAr = obj.optString("titleAr"),
                        description = obj.optString("description"),
                        descriptionAr = obj.optString("descriptionAr"),
                        imageUrl = obj.optString("imageUrl", "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80"),
                        sourceUrl = obj.optString("sourceUrl", "https://news.google.com"),
                        publishTime = obj.optString("publishTime", "Just now"),
                        content = obj.optString("content", "")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse news JSON: ${e.message}")
        }
        return list
    }
}
