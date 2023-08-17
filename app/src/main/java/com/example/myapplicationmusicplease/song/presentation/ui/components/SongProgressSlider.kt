package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerEvent
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerState

@Composable
fun SongProgressSlider(
	state  : PlayerState,
	onSeekBarPositionChanged: (Long) -> Unit = {},
	onEvent : (PlayerEvent) -> Unit = {}
) {
	val playbackStateValue = state.playbackState
	var currentMediaProgress = playbackStateValue.currentPlaybackPosition.toFloat()
	var currentPosTemp by rememberSaveable { mutableStateOf(0f) }

	Slider(
		value = if (currentPosTemp == 0f) currentMediaProgress else currentPosTemp,
		onValueChange = { currentPosTemp = it },
		onValueChangeFinished = {
			currentMediaProgress = currentPosTemp
			currentPosTemp = 0f
			onSeekBarPositionChanged(currentMediaProgress.toLong())
		},
		valueRange = 0f..playbackStateValue.currentTrackDuration.toFloat(),
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 20.dp)
	)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 20.dp),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = playbackStateValue.currentPlaybackPosition.formatTime(),
			style = TextStyle(
				color = Color.White,
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold
			),
		)
		Text(
			text = playbackStateValue.currentTrackDuration.formatTime(),
			style = TextStyle(
				color = Color.White,
				fontSize = 12.sp,
				fontWeight = FontWeight.Bold
			),
		)
	}
}

fun Long.formatTime(): String {
	val totalSeconds = this / 1000
	val minutes = totalSeconds / 60
	val remainingSeconds = totalSeconds % 60
	return String.format("%02d:%02d", minutes, remainingSeconds)
}