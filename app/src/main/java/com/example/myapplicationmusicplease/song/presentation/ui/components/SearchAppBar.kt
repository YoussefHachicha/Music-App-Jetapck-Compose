package com.example.myapplicationmusicplease.song.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationmusicplease.song.domain.model.SongModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
	songs: List<SongModel>,
	onCloseClicked: () -> Unit = {}

) {
	var searchQuery by remember { mutableStateOf(TextFieldValue()) }
	val keyboardController = LocalSoftwareKeyboardController.current
	val focusManager = LocalFocusManager.current


	val filteredSongs by remember {
		derivedStateOf {
			songs.filter {
				it.title.startsWith(searchQuery.text, ignoreCase = true) ||
						it.artist.startsWith(searchQuery.text, ignoreCase = true)
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(color = Color(0xFF23293d))
			.padding(horizontal = 8.dp)
	) {
		OutlinedTextField(
			modifier = Modifier.fillMaxWidth(),
			value = searchQuery,
			textStyle = TextStyle(
				color = Color.White,
				fontSize = 18.sp
			),
			onValueChange = { searchQuery = it },
			placeholder = {
				Text(
					text = "Search...",
					color = Color.White.copy(alpha = 0.5f)
				)
			},
			singleLine = true,
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Text,
				imeAction = ImeAction.Done
			),
			leadingIcon = {
				IconButton(onClick = { keyboardController?.show() }) {
					Icon(
						imageVector = Icons.Filled.Search,
						contentDescription = "Search Icon",
						tint = Color.White.copy(
							alpha = 0.5f
						)
					)
				}
			},
			trailingIcon = {
				IconButton(onClick = {
					keyboardController?.hide()
					focusManager.clearFocus()
					onCloseClicked()
				}) {
					Icon(
						imageVector = Icons.Filled.Close,
						contentDescription = "Close Icon",
						tint = Color.White
					)
				}
			},
			colors = TextFieldDefaults.outlinedTextFieldColors(
				unfocusedBorderColor = Color.White.copy(
					alpha = 0.5f
				),
				focusedBorderColor = Color.White,
				cursorColor = Color.White,
			),
		)

		if (searchQuery.text.isNotEmpty()) {
			Divider(
				color = Color.Gray.copy(alpha = 0.5f),
				thickness = 2.dp,
				modifier = Modifier
			)
			LazyColumn {
				items(filteredSongs) { song ->
					Text(
						modifier = Modifier
							.padding(8.dp)
							.fillMaxWidth(),
						text = song.title,
						color = Color.White,
						fontSize = 18.sp,
					)
				}
			}
		}
	}
}