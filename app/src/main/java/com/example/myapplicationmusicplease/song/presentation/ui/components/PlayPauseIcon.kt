package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplicationmusicplease.R
import com.example.myapplicationmusicplease.song.domain.model.SongModel
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerState

@Composable
fun PlayPauseIcon(
	selectedTrack: SongModel,
	onClick: () -> Unit,
) {
	if (selectedTrack.state == PlayerState.PlayerStates.STATE_BUFFERING) {
		CircularProgressIndicator(
			modifier = Modifier
				.size(size = 48.dp)
				.padding(all = 9.dp),
			color = Color.Gray,
		)
	} else {
		IconButton(onClick = {
			onClick()
			println("PlayPauseIcon: ${selectedTrack.state}")
		}) {
			Icon(
				painter = if (selectedTrack.state == PlayerState.PlayerStates.STATE_PLAYING) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play),
				contentDescription = null,
				tint = Color.White,
				modifier = Modifier.size(180.dp)
			)
		}
	}
}