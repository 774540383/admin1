package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChannelEntity
import com.example.ui.theme.ColorLive
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.SecondaryDark
import com.example.viewmodel.FootballViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IptvPlayerView(viewModel: FootballViewModel) {
    val channels by viewModel.channelsList.collectAsState()
    val isAr = viewModel.appLanguage == "ar"
    val coroutineScope = rememberCoroutineScope()

    var selectedChannel by remember { mutableStateOf<ChannelEntity?>(null) }
    var streamStatus by remember { mutableStateOf("IDLE") } // IDLE, BUFFERING, PLAYING
    var currentQuality by remember { mutableStateOf("1080p (Source)") }
    var isMuted by remember { mutableStateOf(false) }

    // Baseline stream trigger
    LaunchedEffect(channels) {
        if (selectedChannel == null && channels.isNotEmpty()) {
            selectedChannel = channels.first()
        }
    }

    // Buffering effect
    LaunchedEffect(selectedChannel) {
        if (selectedChannel != null) {
            streamStatus = "BUFFERING"
            delay(1200)
            streamStatus = "PLAYING"
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Premium IPTV Player Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selectedChannel != null) {
                // Background simulated ambient stadium aura
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF0F172A), Color.Black)
                            )
                        )
                )

                // Render based on stream status
                when (streamStatus) {
                    "BUFFERING" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryDark, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isAr) "تحميل البث المباشر..." else "BUFFING STREAMS...",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    "PLAYING" -> {
                        // Media Visual Overlay
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Bar HUD
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(ColorLive)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedChannel!!.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = currentQuality, color = PrimaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Dynamic Soundwave visual
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = "Playing",
                                    tint = PrimaryDark.copy(alpha = 0.8f),
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            // Interactive Controller Layout
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { isMuted = !isMuted }) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "Mute",
                                        tint = Color.White
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconButton(onClick = {
                                        viewModel.addLog("Interactive Play: restarted stream overlay")
                                        streamStatus = "BUFFERING"
                                        coroutineScope.launch {
                                            delay(800)
                                            streamStatus = "PLAYING"
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
                                    }
                                    IconButton(onClick = {
                                        currentQuality = if (currentQuality.contains("1080p")) "720p (HD)" else "1080p (Source)"
                                    }) {
                                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Quality", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = if (isAr) "لم يتم تحديد قناة" else "No active stream selected", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Stream Category Filters
        Text(
            text = if (isAr) "القنوات المتاحة مجاناً" else "Free IPTV Feeds",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3. Channels Feed list items
        if (channels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAr) "لا توجد قنوات. استورد قائمة M3U الخاصة بك في الإعدادات." else "No channels integrated. Load M3U playlist in Admin Console.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(channels) { channel ->
                    val isCurrent = selectedChannel?.id == channel.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedChannel = channel
                            }
                            .testTag("channel_item_${channel.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0xFF1E293B) else Color.White.copy(alpha = 0.03f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isCurrent) PrimaryDark.copy(alpha = 0.2f)
                                            else Color.White.copy(alpha = 0.05f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play icon",
                                        tint = if (isCurrent) PrimaryDark else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = channel.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) PrimaryDark else Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = channel.groupGroupName,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Favorite toggle button
                            IconButton(
                                onClick = { viewModel.toggleChannelFavorite(channel) },
                                modifier = Modifier.testTag("fav_channel_${channel.id}")
                            ) {
                                Icon(
                                    imageVector = if (channel.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite star",
                                    tint = if (channel.isFavorite) Color(0xFFFBBF24) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Group workaround helper
val ChannelEntity.groupGroupName: String
    get() = this.groupName.ifEmpty { "Free IPTV Feed" }
