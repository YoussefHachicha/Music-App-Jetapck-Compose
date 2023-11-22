package com.example.myapplicationmusicplease.song.domain.model

import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerState

data class SongModel(
    val artist: String,
    val songPhoto: String,
    val id: String,
    val title: String,
    val url: String,
    val isSelected: Boolean = false,
    val state: PlayerState.PlayerStates = PlayerState.PlayerStates.STATE_IDLE
) {

    companion object {
        val demoItem get() = SongModel(
            artist = "Artist 1",
            songPhoto = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
            id = "1",
            title = "Audio 1",
            url = "https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav",
            isSelected = false,
            state = PlayerState.PlayerStates.STATE_IDLE
        )
    }
}