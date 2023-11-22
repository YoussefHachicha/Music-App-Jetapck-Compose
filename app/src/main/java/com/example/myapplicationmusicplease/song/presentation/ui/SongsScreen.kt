package com.example.myapplicationmusicplease.song.presentation.ui

import android.content.Context
import android.media.AudioManager
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplicationmusicplease.R
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerEvent
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerState
import com.example.myapplicationmusicplease.song.presentation.ui.components.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit,
) {

    LaunchedEffect(state.getSongs.isSuccess) {
        onEvent(PlayerEvent.InitPlayer)
    }

    LaunchedEffect(key1 = state.playerState) {
        if (state.songs.isNotEmpty())
            onEvent(PlayerEvent.UpdatePlayerState(state.playerState))
    }

    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var isSearchBarVisible = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager


    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { state.songs.size }
    )
    Scaffold(
        modifier = Modifier
            .background(color = Color(0xFF23293d))
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                keyBoardController?.hide()
                focusManager.clearFocus()
            },
    )  { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color(0xFF23293d)),
        ) {

            if (state.getSongs.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = Color(0xFF23293d)),
                ) {
                    CircularProgressIndicator(
                        color = Color.Red, modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else if (state.getSongs.isFailure) {
                Text(
                    text = stringResource(id = R.string.error_fetching_data),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                )
            } else if (state.getSongs.isSuccess) {
//				println("the state: $state")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFF23293d)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Crossfade(
                        targetState = isSearchBarVisible,
                        animationSpec = tween(durationMillis = 500),
                        label = ""
                    ) {
                        if (it.value) {
                            SearchAppBar(
                                songs = state.songs,
                                onCloseClicked = { isSearchBarVisible.value = false },
                            )
                        } else {
                            CustomTopAppBar(
                                isSong = true,
                                onSearchClicked = { isSearchBarVisible.value = true },
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 30.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        pageSpacing = (-50).dp,
                        verticalAlignment = Alignment.Top,
                        userScrollEnabled = false,
                    ) { index ->
                        val scale by animateFloatAsState(
                            targetValue = if (index == pagerState.currentPage) 1f else 0.8f,
                            label = ""
                        )
                        val opacity = if (index == pagerState.currentPage) 1f else 0.4f

                        Column(
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                alpha = opacity
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            SongItem(
                                state = state.songs[index]
                            )
                        }
                    }

                    SongProgressSlider(
                        state = state,
                        onSeekBarPositionChanged = {
                            onEvent(PlayerEvent.OnSeekBarPositionChanged(it))
                        }
                    )

                    //Scroll to selected song
                    //Todo change this later
                    LaunchedEffect(key1 = state.selectedSongIndex) {
                        pagerState.animateScrollToPage(state.selectedSongIndex)
                    }

                    SongControls(selectedSong = state.songs[state.selectedSongIndex],
                        onPreviousClick = {
                            onEvent(PlayerEvent.OnPreviousClick)
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        onPlayPauseClick = {
                            onEvent(PlayerEvent.OnPlayPauseClick)
                        },
                        onNextClick = {
                            onEvent(PlayerEvent.OnNextClick)
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        onRepeatClick = {
                            onEvent(PlayerEvent.OnRepeatClick(it))
                        },
                        onShuffleClick = { onEvent(PlayerEvent.OnShuffleClick(state.songs[state.selectedSongIndex])) },
                        onVolumeClick = {
                            val volumeDialog = AudioManager.STREAM_MUSIC
                            audioManager.adjustStreamVolume(
                                volumeDialog,
                                AudioManager.ADJUST_SAME,
                                AudioManager.FLAG_SHOW_UI
                            )
                        },
                        onMuteClick = {
                            onEvent(PlayerEvent.OnMuteClick(it))
                        }
                    )
                }
            }
        }
    }
}