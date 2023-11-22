package com.example.myapplicationmusicplease.song.presentation.bussines_logic

import com.example.myapplicationmusicplease.song.domain.model.SongModel


data class PlayerState(
    val getSongs: GetSongs = GetSongs(),
    val errorMessage: String = "",
    val songs : List<SongModel> = emptyList(),
    val isSongPlay: Boolean = false,
    val selectedSongModel: SongModel? = null,
    val selectedSongIndex: Int = 0,
    val playbackState: PlaybackState = PlaybackState(0L, 10L),
    val isAutoSetOnPlay: Boolean = false,
    val isRepeat: Boolean = false,
    val playerState: PlayerStates = PlayerStates.STATE_IDLE
) {
    data class GetSongs(
        val isLoading: Boolean = false,
        val isFailure: Boolean = false,
        val isUnauthorized: Boolean = false,
        val isSuccess: Boolean = false,
    )
    enum class PlayerStates {
        STATE_IDLE,
        STATE_READY,
        STATE_BUFFERING,
        STATE_ERROR,
        STATE_END,
        STATE_PLAYING,
        STATE_PAUSE,
        STATE_NEXT_TRACK,
        STATE_REPEAT_MODE_ONE,
        STATE_REPEAT_MODE_ALL,
    }
    data class PlaybackState(
        val currentPlaybackPosition: Long,
        val currentTrackDuration: Long
    )
}