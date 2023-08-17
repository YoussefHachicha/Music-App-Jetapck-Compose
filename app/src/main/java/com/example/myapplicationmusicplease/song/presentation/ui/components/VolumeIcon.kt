package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplicationmusicplease.R

@Composable
fun VolumeIcon(onClick: () -> Unit) {
	IconButton(
		onClick = onClick
	) {
		Icon(
			painter = painterResource(id = R.drawable.volume_loud_svgrepo_com),
			contentDescription = null,
			tint = Color.White,
			modifier = Modifier.size(22.dp)
		)
	}
}

