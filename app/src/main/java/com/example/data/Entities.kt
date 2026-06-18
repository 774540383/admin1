package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class LeagueEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameAr: String,
    val country: String,
    val logoUrl: String
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameAr: String,
    val logoUrl: String,
    val country: String
)

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val teamId: Int,
    val position: String,
    val goals: Int,
    val assists: Int,
    val rating: Double
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamAr: String,
    val awayTeamAr: String,
    val homeScore: Int,
    val awayScore: Int,
    val minute: String,
    val status: String, // scheduled, live, halftime, finished, postponed, cancelled, unknown
    val league: String,
    val leagueAr: String,
    val country: String,
    val kickoffTime: String,
    val source: String,
    val lastUpdated: Long
)

@Entity(tableName = "match_stats")
data class MatchStatEntity(
    @PrimaryKey val matchId: String,
    val possessionHome: Int,
    val possessionAway: Int,
    val shotsHome: Int,
    val shotsAway: Int,
    val foulsHome: Int,
    val foulsAway: Int,
    val cornersHome: Int,
    val cornersAway: Int
)

@Entity(tableName = "match_events")
data class MatchEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: String,
    val minute: Int,
    val type: String, // goal, card_yellow, card_red, substitution
    val team: String, // home or away
    val player: String,
    val detail: String
)

@Entity(tableName = "standings")
data class StandingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leagueId: Int,
    val teamId: Int,
    val teamName: String,
    val teamNameAr: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val position: Int
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val titleAr: String,
    val description: String,
    val descriptionAr: String,
    val imageUrl: String,
    val sourceUrl: String,
    val publishTime: String,
    val content: String
)

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val groupName: String, // e.g., beIN, SSC, Nile Sports
    val logoUrl: String,
    val streamUrl: String, // HLS playlist (.m3u8), MP4 or Embedded URL
    val isFavorite: Boolean = false,
    val isActive: Boolean = true
)
