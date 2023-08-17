package com.example.myapplicationmusicplease.song.presentation.bussines_logic

import com.example.myapplicationmusicplease.song.domain.model.SongModel


data class PlayerState(
    val getSongs: GetSongs = GetSongs(),
    val errorMessage: String = "",
    val songs : MutableList<SongModel> = mutableListOf(),
    val isSongPlay: Boolean = false,
    val selectedSongModel: SongModel? = null,
    val selectedSongIndex: Int = 0,
    val playbackState: PlaybackState = PlaybackState(0L, 10L),
    val isAutoSetOnPlay: Boolean = false,
    val isRepeat: Boolean = false,
    ) {
    data class GetSongs(
        val isLoading: Boolean = false,
        val isFailure: Boolean = false,
        val isUnauthorized: Boolean = false,
        val isSuccess: Boolean = false,
    )
    data class PlaybackState(
        val currentPlaybackPosition: Long,
        val currentTrackDuration: Long
    )
}