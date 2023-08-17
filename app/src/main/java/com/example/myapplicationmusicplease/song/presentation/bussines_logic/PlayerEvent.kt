package com.example.myapplicationmusicplease.song.presentation.bussines_logic

sealed class PlayerEvent {
    data class UpdateSelectedSong(val selectedSongIndex: Int) : PlayerEvent()
    data class UpdatePlaybackState(
        val currentPlaybackPosition: Long,
        val currentTrackDuration: Long
    ) : PlayerEvent()

    object InitPlayer : PlayerEvent()

    data class SetToRepeat(val isRepeat: Boolean) : PlayerEvent()
}