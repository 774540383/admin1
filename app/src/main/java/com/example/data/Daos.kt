package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FootballDao {
    // --- Matches ---
    @Query("SELECT * FROM matches ORDER BY kickoffTime ASC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status = 'live' OR status = 'halftime' ORDER BY kickoffTime ASC")
    fun getLiveMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE matchId = :id")
    suspend fun getMatchById(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Query("DELETE FROM matches")
    suspend fun clearMatches()

    // --- Stats & Events ---
    @Query("SELECT * FROM match_stats WHERE matchId = :matchId")
    suspend fun getStatsForMatch(matchId: String): MatchStatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchStats(stats: MatchStatEntity)

    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC")
    fun getEventsForMatch(matchId: String): Flow<List<MatchEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchEvents(events: List<MatchEventEntity>)

    // --- Standings ---
    @Query("SELECT * FROM standings WHERE leagueId = :leagueId ORDER BY position ASC")
    fun getStandingsForLeague(leagueId: Int): Flow<List<StandingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandings(standings: List<StandingEntity>)

    // --- News ---
    @Query("SELECT * FROM news ORDER BY id DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsList: List<NewsEntity>)

    // --- Channels ---
    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity)

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE id = :channelId")
    suspend fun deleteChannelById(channelId: Int)
}
