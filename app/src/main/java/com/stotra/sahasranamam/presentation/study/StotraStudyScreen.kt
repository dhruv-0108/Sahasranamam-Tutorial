package com.stotra.sahasranamam.presentation.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stotra.sahasranamam.domain.model.Pada
import com.stotra.sahasranamam.presentation.theme.GoldAccent
import com.stotra.sahasranamam.presentation.theme.SandhiHighlight
import com.stotra.sahasranamam.presentation.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StotraStudyScreen(
    viewModel: StotraStudyViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val currentShloka = state.shlokas.getOrNull(state.currentShlokaIndex)
    var showVerseSelector by remember { mutableStateOf(false) }
    var isVerseExpanded by remember(state.currentShlokaIndex) { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.onPause()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showVerseSelector = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Stotra / Suktam",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentShloka != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Verse ${currentShloka.shlokaNumber} of ${state.shlokas.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Verse",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentShloka != null) {
                        IconButton(onClick = { viewModel.toggleBookmark() }) {
                            Icon(
                                imageVector = if (currentShloka.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite/Bookmark",
                                tint = if (currentShloka.isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (currentShloka != null) {
                AudioControlBar(
                    isPlaying = state.isPlayingAudio,
                    playbackSpeed = state.playbackSpeed,
                    onPlayPauseClick = { viewModel.toggleAudioPlayback() },
                    onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                    onPreviousClick = { viewModel.previousShloka() },
                    onNextClick = { viewModel.nextShloka() },
                    hasPrevious = state.currentShlokaIndex > 0,
                    hasNext = state.currentShlokaIndex < state.shlokas.size - 1
                )
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SaffronPrimary)
            }
        } else if (currentShloka == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No verses available.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Floating/Pinned Sanskrit Verse Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isVerseExpanded = !isVerseExpanded }
                            .padding(16.dp)
                    ) {
                        // Header row: Title + Collapse Arrow + Language Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "मूल श्लोक (Verse Text)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                                
                                val rotationState by animateFloatAsState(targetValue = if (isVerseExpanded) 180f else 0f)
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = if (isVerseExpanded) "Collapse Verse" else "Expand Verse",
                                    tint = SaffronPrimary,
                                    modifier = Modifier.rotate(rotationState)
                                )
                            }
                            
                            // Language Toggle Button
                            FilterChip(
                                selected = state.showHindiMeaning,
                                onClick = { viewModel.toggleLanguageMeaning() },
                                label = {
                                    Text(
                                        if (state.showHindiMeaning) "Hindi (हिंदी)" else "English",
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }

                        // Full Verse details (visible when expanded)
                        AnimatedVisibility(visible = isVerseExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "— ॐ श्रीं ॐ —",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Original Full Verse
                                Text(
                                    text = currentShloka.fullSanskrit,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 34.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // IAST Transliteration
                                Text(
                                    text = currentShloka.iastTransliteration,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Collapsed snippet view (visible when collapsed)
                        AnimatedVisibility(visible = !isVerseExpanded) {
                            val snippet = if (currentShloka.fullSanskrit.length > 40) {
                                currentShloka.fullSanskrit.take(40) + "..."
                            } else {
                                currentShloka.fullSanskrit
                            }
                            Text(
                                text = snippet,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Scrollable lazy list for translations and padas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    // 1. Shloka Meaning Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleAudioPlayback() },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Verse Meaning",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SaffronPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (state.showHindiMeaning && !currentShloka.meaningHindi.isNullOrBlank()) {
                                        currentShloka.meaningHindi
                                    } else {
                                        currentShloka.meaningEnglish
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // 2. Word-by-Word Breakdown Section
                    if (currentShloka.padas.isNotEmpty()) {
                        item {
                            Text(
                                text = "Word-by-Word Sandhi Breakdown (पदच्छेदानुसार विवरण)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(currentShloka.padas) { pada ->
                            PadaCardItem(
                                pada = pada,
                                onPlayClick = { viewModel.playPadaAudio(pada) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showVerseSelector && state.shlokas.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showVerseSelector = false },
            title = {
                Text(
                    text = "Go to Verse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary
                )
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 54.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.shlokas.size) { index ->
                            val isSelected = index == state.currentShlokaIndex
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        viewModel.selectShloka(index)
                                        showVerseSelector = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVerseSelector = false }) {
                    Text("Close", color = SaffronPrimary)
                }
            }
        )
    }
}

@Composable
fun PadaCardItem(
    pada: Pada,
    onPlayClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Word Pronunciation",
                        tint = SaffronPrimary
                    )
                }

                Text(
                    text = pada.sanskritCombined,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            
            if (!pada.sandhiRuleName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = SaffronPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = pada.sandhiRuleName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val hasSandhi = pada.sanskritSplit.isNotBlank() && 
                pada.sanskritSplit.replace(" ", "").replace("+", "").replace("्", "") != 
                pada.sanskritCombined.replace(" ", "").replace("्", "")

            if (hasSandhi) {
                Text(
                    text = pada.sanskritSplit,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = SandhiHighlight,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = pada.iast,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Meaning: ${pada.meaning}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            if (!pada.sandhiRuleExplanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rule: ${pada.sandhiRuleExplanation}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AudioControlBar(
    isPlaying: Boolean,
    playbackSpeed: Float,
    onPlayPauseClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    hasPrevious: Boolean,
    hasNext: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Dropdown Button
            TextButton(
                onClick = {
                    val nextSpeed = when (playbackSpeed) {
                        0.85f -> 0.7f
                        0.7f -> 0.5f
                        0.5f -> 0.25f
                        0.25f -> 1.0f
                        else -> 0.85f
                    }
                    onSpeedChange(nextSpeed)
                }
            ) {
                Text(text = "${playbackSpeed}x Speed", fontWeight = FontWeight.Bold)
            }

            // Media Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousClick,
                    enabled = hasPrevious
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Verse")
                }

                FloatingActionButton(
                    onClick = onPlayPauseClick,
                    containerColor = SaffronPrimary,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause"
                    )
                }

                IconButton(
                    onClick = onNextClick,
                    enabled = hasNext
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Verse")
                }
            }
        }
    }
}
