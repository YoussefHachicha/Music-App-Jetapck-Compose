package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplicationmusicplease.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
	modifier: Modifier = Modifier,
	isSong: Boolean = false,
	onSearchClicked : () -> Unit = {}

	) {
	TopAppBar(
		modifier = modifier,
		title = {
			Box(modifier = Modifier.fillMaxWidth()) {
				Text(
					modifier = Modifier.align(Alignment.BottomCenter),
					text = if (isSong)"Recommended"  else "Home",
					style = TextStyle(
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = Color.White
					),
				)
			}
		},
		navigationIcon = {
			if (isSong) {
				Icon(
					painter = painterResource(id = R.drawable.back_svgrepo_com),
					contentDescription = null,
					tint = Color.White,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.size(20.dp)
				)
			} else {
				AsyncImage(
					model = "navigationIconModel",
					contentDescription = null,
					modifier = Modifier
						.size(40.dp)
						.padding(horizontal = 16.dp)
						.clip(CircleShape),
					contentScale = ContentScale.Crop
				)

			}
		},
		actions = {
			if (isSong) {
				IconButton(onClick = { onSearchClicked()}) {
					Icon(
						imageVector = Icons.Default.Search,
						contentDescription = null,
						tint = Color.White,
						modifier = Modifier
					)
				}
			} else {
				Icon(
					painter = painterResource(id = R.drawable.candle),
					contentDescription = null,
					tint = Color.White,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.size(25.dp)
				)
			}
		},
		colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color(0xFF23293d),)
	)
}


@Preview
@Composable
private fun CustomTopAppBarPreview() {

		CustomTopAppBar(
			isSong = false
		)

}

@Preview
@Composable
private fun CustomHomeTopAppBarPreview() {

		CustomTopAppBar(
			isSong = true
		)

}