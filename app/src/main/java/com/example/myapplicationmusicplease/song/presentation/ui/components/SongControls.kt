package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplicationmusicplease.song.domain.model.SongModel

@Composable
fun SongControls(
	selectedSong: SongModel,
	onPreviousClick: () -> Unit = {},
	onPlayPauseClick: () -> Unit = {},
	onNextClick:  () -> Unit = {},
	onRepeatClick : (Boolean) -> Unit = {},
	onShuffleClick : () -> Unit = {},
	onVolumeClick : () -> Unit = {},
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		horizontalArrangement = Arrangement.SpaceAround,
		verticalAlignment = Alignment.CenterVertically
	) {
		val repeatState = rememberSaveable { mutableStateOf(false) }
		VolumeIcon(onClick = onVolumeClick)
		RepeatIcon(
			onClick = {
				repeatState.value = !repeatState.value
				onRepeatClick(repeatState.value)
			},
			repeat = repeatState.value
		)
		PreviousIcon(onClick = onPreviousClick)
		PlayPauseIcon(
			selectedTrack = selectedSong,
			onClick = onPlayPauseClick,
		)
		NextIcon(onClick = onNextClick)
		ShuffleIcon(onClick = onShuffleClick)
		VolumeIcon(onClick = onVolumeClick)
	}
}