package com.example.service

import kotlin.math.abs
import kotlin.random.Random

data class TeamStats(
    val attackStrength: Double, // 1 to 10
    val defenseStrength: Double, // 1 to 10
    val recentForm: List<Char>,  // e.g. ['W', 'W', 'D', 'L', 'W']
    val avgGoalsScored: Double,
    val avgGoalsConceded: Double
)

data class MatchPrediction(
    val homeWinProbability: Int,
    val drawProbability: Int,
    val awayWinProbability: Int,
    val predictedHomeScore: Int,
    val predictedAwayScore: Int,
    val confidence: Int, // percentage 1-100
    val keyInsightEn: String,
    val keyInsightAr: String
)

object PredictionService {

    // Precompiled data for leading clubs
    private val teamData = mapOf(
        "Real Madrid" to TeamStats(9.4, 8.8, listOf('W', 'W', 'D', 'W', 'W'), 2.4, 0.8),
        "Barcelona" to TeamStats(9.0, 7.9, listOf('W', 'L', 'W', 'W', 'D'), 2.2, 1.1),
        "Liverpool" to TeamStats(9.2, 8.5, listOf('W', 'W', 'W', 'D', 'L'), 2.3, 0.9),
        "Manchester City" to TeamStats(9.6, 8.6, listOf('D', 'W', 'W', 'W', 'W'), 2.5, 1.0),
        "Arsenal" to TeamStats(9.1, 9.2, listOf('W', 'W', 'D', 'W', 'D'), 2.1, 0.7),
        "Chelsea" to TeamStats(8.2, 7.5, listOf('D', 'W', 'L', 'W', 'D'), 1.8, 1.5),
        "Al Hilal" to TeamStats(9.5, 9.0, listOf('W', 'W', 'W', 'D', 'W'), 2.9, 0.7),
        "Al Nassr" to TeamStats(9.1, 8.0, listOf('W', 'D', 'W', 'W', 'L'), 2.6, 1.1),
        "Al Ahly" to TeamStats(8.9, 8.8, listOf('W', 'W', 'W', 'W', 'D'), 2.0, 0.6),
        "Zamalek" to TeamStats(8.1, 7.8, listOf('W', 'D', 'L', 'W', 'W'), 1.6, 1.1)
    )

    private val defaultStats = TeamStats(7.5, 7.5, listOf('W', 'D', 'L', 'D', 'W'), 1.2, 1.2)

    fun predictMatch(homeName: String, awayName: String): MatchPrediction {
        val home = teamData[homeName] ?: teamData.entries.find { homeName.contains(it.key, true) }?.value ?: defaultStats
        val away = teamData[awayName] ?: teamData.entries.find { awayName.contains(it.key, true) }?.value ?: defaultStats

        // Calculations
        // 1. Home advantage bonus (adds to attack, reduces average conceded)
        val homeAdvantageFactor = 0.15
        val effectiveHomeAttack = home.attackStrength * (1.0 + homeAdvantageFactor)
        val effectiveAwayAttack = away.attackStrength

        // 2. Relative attack vs defense weights
        val homeAdv = effectiveHomeAttack - away.defenseStrength
        val awayAdv = effectiveAwayAttack - home.defenseStrength

        // 3. Recent form scoring (W = 3pts, D = 1pt, L = 0pts)
        val homeFormScore = home.recentForm.sumOf { if (it == 'W') 3 else if (it == 'D') 1 else 0 }
        val awayFormScore = away.recentForm.sumOf { if (it == 'W') 3 else if (it == 'D') 1 else 0 }

        // 4. Probability distribution
        var homeWeight = 35.0 + (homeAdv * 5.0) + (homeFormScore - awayFormScore) * 1.5
        var awayWeight = 35.0 + (awayAdv * 5.0) + (awayFormScore - homeFormScore) * 1.5
        var drawWeight = 30.0 - abs(homeWeight - awayWeight) * 0.4

        // Bound checking
        if (homeWeight < 10) homeWeight = 10.0
        if (awayWeight < 10) awayWeight = 10.0
        if (drawWeight < 10) drawWeight = 10.0

        val total = homeWeight + awayWeight + drawWeight
        val homePct = ((homeWeight / total) * 100).toInt()
        val awayPct = ((awayWeight / total) * 100).toInt()
        val drawPct = 100 - homePct - awayPct

        // 5. Predicted Scores (simulated with standard soccer scoring models)
        val expHomeGoals = (home.avgGoalsScored * 1.1 + away.avgGoalsConceded) / 2.0
        val expAwayGoals = (away.avgGoalsScored + home.avgGoalsConceded * 0.9) / 2.0

        var predHome = expHomeGoals.toInt()
        var predAway = expAwayGoals.toInt()

        // Distribute based on winning probability to make score look coherent
        if (homePct > awayPct + 15 && predHome <= predAway) {
            predHome = predAway + 1
        } else if (awayPct > homePct + 15 && predAway <= predHome) {
            predAway = predHome + 1
        }

        // Clip unrealistic high margins
        if (predHome > 5) predHome = 3
        if (predAway > 5) predAway = 2

        // 6. Confidence Score
        val confidence = (50 + abs(homePct - awayPct) * 0.8 + (homeFormScore + awayFormScore) * 0.5).toInt().coerceIn(55, 95)

        // 7. Tactical Insight
        val keyInsightEn: String
        val keyInsightAr: String

        if (homePct > awayPct + 10) {
            keyInsightEn = "Home turf advantage puts $homeName in a stellar offensive posture. Their high press will likely disrupt $awayName's build-up play from the back."
            keyInsightAr = "تمنح أفضلية الأرض والجماهير فريق $homeName ميزة هجومية واضحة. من المتوقع أن يضغطوا بقوة لتعطيل بناء اللعب المنظم لـ $awayName من الخلف."
        } else if (awayPct > homePct + 10) {
            keyInsightEn = "$awayName enters this matchup with an extremely superior defensive discipline and devastating fast counter-attacks, making them robust on the road."
            keyInsightAr = "يدخل $awayName المواجهة بتنظيم دفاعي حديدي وهجمات مرتدة خاطفة فائقة السرعة، مما يمنحه الأفضلية لتجاوز العقبة الجماهيرية خارج الأرض."
        } else {
            keyInsightEn = "A highly tactical midfield battle is anticipated. Both squads are structurally balanced, indicating a conservative stalemate is the most plausible outcome."
            keyInsightAr = "يُتوقع معركة تكتيكية طاحنة في خط الوسط. كلا الفريقين متوازنان هيكلياً، مما يجعل نتيجة التعادل الحذر الخيار الأكثر منطقية تكتيكياً."
        }

        return MatchPrediction(
            homeWinProbability = homePct,
            drawProbability = drawPct,
            awayWinProbability = awayPct,
            predictedHomeScore = predHome,
            predictedAwayScore = predAway,
            confidence = confidence,
            keyInsightEn = keyInsightEn,
            keyInsightAr = keyInsightAr
        )
    }
}
