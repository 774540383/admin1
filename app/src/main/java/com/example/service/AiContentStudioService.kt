package com.example.service

import android.util.Log
import com.example.data.GeminiContent
import com.example.data.GeminiPart
import com.example.data.GeminiRequest
import com.example.data.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AiStudioContent(
    val title: String,
    val platform: String,
    val hook: String,
    val script: String,
    val caption: String,
    val hashtags: String,
    val visualSuggestions: String,
    val viralScore: Int
)

object AiContentStudioService {
    private const val TAG = "AI_Content_Studio"

    // High fidelity realistic fallbacks in Arabic and English
    fun getMockTacticalContent(type: String, isArabic: Boolean): AiStudioContent {
        return when (type) {
            "tactical" -> {
                if (isArabic) {
                    AiStudioContent(
                        title = "التحليل التكتيكي للنهج الهجومي لباريس سان جيرمان",
                        platform = "رابط تكتيكي / مقال مطول",
                        hook = "كيف فكك المدير الفني الدفاعات المتكتلة باستخدام الجناح المقلوب؟ 🧠⚽",
                        script = "المقدمة: لطالما عانى الفريق أمام كتل دفاعية منخفضة 5-4-1.\nالتحليل: التحول التكتيكي بالاعتماد على التمرير السريع ذو اللمسة الواحدة والمثلثات على الطرف الأيسر.\nالخاتمة: الفوز لم يكن محض صدفة بل نتاج تنظيم ذكي للمساحات السفلية.",
                        caption = "تكتيك متكامل ومساحات مدروسة بعناية فائقة. اقرأ التحليل التكتيكي الكامل!",
                        hashtags = "#كرة_قدم #تكتيك_مباريات #دوري_الأبطال #باريس_سان_جيرمان",
                        visualSuggestions = "تصميم جرافيكي يوضح خريطة التحركات (Heatmap) للاعبين على الطرف الأيسر مع دوائر حمراء على مساحات نصف الجناح.",
                        viralScore = 91
                    )
                } else {
                    AiStudioContent(
                        title = "The Inside-Out Wing Tactical Blueprint",
                        platform = "Medium / Blogger",
                        hook = "How modern managers decompose a deep low-block 5-4-1 using inverted fullbacks? 🧠⚽",
                        script = "Introduction: Breaking a dense defense requires extreme speed of ball transfers.\nInside Analysis: The wingers drag fullbacks out while inside midfielders exploit half-spaces dynamically.\nConclusion: Structured spatial awareness, not luck, decides the final whistle.",
                        caption = "An in-depth look at cutting-edge spatial orientation in professional football. Read our technical review!",
                        hashtags = "#Tactics #AnatomyOfSoccer #FootballAnalysis #ChampionsLeague",
                        visualSuggestions = "A clean tactical board heatmap highlighting the half-spaces and inside runs of the progressive midfielders.",
                        viralScore = 88
                    )
                }
            }
            "script" -> {
                if (isArabic) {
                    AiStudioContent(
                        title = "سيناريو فيديو قصير: سر مهارات ميسي الخفية",
                        platform = "TikTok / Reels",
                        hook = "السر المدفون خلف مهارات ميسي الرياضية التي لا تراها الكاميرات العادية! 😳👇",
                        script = "[مشهد 1 - لقطة مقربة متباطئة لميسي]: الجميع يظن أنها سرعة طبيعية، لكن الحقيقة أعقد بكثير.\n[مشهد 2 - رسم كروكي تكتيكي]: ميسي يفحص الملعب 16 مرة كل 10 ثوانٍ.\n[مشهد 3 - لقطة مذهلة ومؤثرة]: ولهذا يسكن الملعب بذكائه قبل قدميه.",
                        caption = "الأمر ليس ضربًا من السحر، بل هندسة بصرية فائقة التعقيد! شاركنا برأيك في التعليقات.",
                        hashtags = "#ميسي #صناع_المحتوى #مهارات #حقائق_كرة_القدم #ليونيل_ميسي",
                        visualSuggestions = "فيديو قصير يبدأ بزاوية متباطئة للغاية لعيون ميسي وهي تفحص الملعب قبل أن يستلم الكرة، مع مؤثرات صوتية سريعة.",
                        viralScore = 96
                    )
                } else {
                    AiStudioContent(
                        title = "Short Video Script: Messi's Hidden Scanning Radar",
                        platform = "TikTok / Shorts / Reels",
                        hook = "The hidden bio-mechanic scanner Messi uses to outsmart elite defenders before even touching the ball! 😳👇",
                        script = "[Scene 1 - Slow Motion Macro shot]: Fans think it is raw agility. But science proves otherwise.\n[Scene 2 - Animated overlay layout]: Messi scans the football ground 16 times per 10 seconds.\n[Scene 3 - Dramatic goal kick]: He acts like a supercomputer on grass.",
                        caption = "It is not just magical talent, it is military-grade scanning spatial awareness! Tag a friend who needs to see this.",
                        hashtags = "#Messi #FootballSecrets #TikTokSoccer #ScienceOfSport",
                        visualSuggestions = "Staggered visual zoom on a player's eyes scanning the environment, laid over bright high-tech green tracker graphics.",
                        viralScore = 97
                    )
                }
            }
            else -> {
                if (isArabic) {
                    AiStudioContent(
                        title = "حقائق اليوم المثيرة للجدل",
                        platform = "Instagram / Twitter",
                        hook = "هل سينهار الكيان الدفاعي الإسباني هذا العام؟ كشف حقائق مبكرة! 🚨💣",
                        script = "الأرقام تشير بوضوح إلى انخفاض نسبة استرجاع الكرات بمعدل 12% في الثلث الدفاعي الأول.",
                        caption = "علامات استفهام حادة تحوم حول المعسكر التدريبي. ما هو رأيكم تكتيكيًا؟",
                        hashtags = "#الليغا #تحليل_مباريات #دوري_إسبانيا",
                        visualSuggestions = "جدول بياني مقارن بين نسب الأهداف المستقبلة خلال آخر 3 مواسم متتالية.",
                        viralScore = 85
                    )
                } else {
                    AiStudioContent(
                        title = "Trending Daily Match Insights",
                        platform = "Instagram / Threads",
                        hook = "Is the defensive era of Spanish giants declining under new offensive pressures? 🚨💣",
                        script = "Historical records suggest ball recoveries in the clean defensive third dropped by over 12% this month.",
                        caption = "Statistics speak louder than rumors. What is your take on this sudden shift?",
                        hashtags = "#LaLiga #SpainSoccer #FootballStats",
                        visualSuggestions = "A clean comparative graphic listing clean sheets across the last 3 prominent leagues.",
                        viralScore = 83
                    )
                }
            }
        }
    }

    suspend fun generateTacticalContent(
        type: String, // "tactical" or "script" or "viral"
        matchDetails: String,
        apiKey: String,
        isArabic: Boolean
    ): AiStudioContent = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getMockTacticalContent(type, isArabic)
        }

        val languagePrompt = if (isArabic) "Arabic language" else "English language"
        val prompt = """
            Create football tactical content of type: '$type' based on these details: '$matchDetails'.
            The output MUST be generated in: $languagePrompt.
            Format output strictly as a JSON object with this EXACT structure (No markdown format blocks, no ```json formatting):
            {
              "title": "Title here",
              "platform": "Suggest target platform",
              "hook": "Eye-catching hook text",
              "script": "Detailed content script or article paragraphs",
              "caption": "Social media description caption",
              "hashtags": "Space-separated hash tags e.g. #Footy #Derby",
              "visualSuggestions": "Visual layout ideas",
              "viralScore": 90
            }
        """.trimIndent()

        try {
            val response = NetworkClient.geminiService.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    )
                )
            )

            val rawOutputText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (rawOutputText != null) {
                val clean = cleanJson(rawOutputText)
                val json = JSONObject(clean)
                return@withContext AiStudioContent(
                    title = json.optString("title", "AI Tactical Analysis"),
                    platform = json.optString("platform", "Sports Stream Feed"),
                    hook = json.optString("hook", "Did you know this?"),
                    script = json.optString("script", "Content analysis..."),
                    caption = json.optString("caption", ""),
                    hashtags = json.optString("hashtags", "#FootballWorld"),
                    visualSuggestions = json.optString("visualSuggestions", ""),
                    viralScore = json.optInt("viralScore", 75)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling Gemini AI Content Studio: ${e.message}")
        }

        return@withContext getMockTacticalContent(type, isArabic)
    }

    private fun cleanJson(raw: String): String {
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
}
