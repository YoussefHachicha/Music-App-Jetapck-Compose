package com.example.myapplicationmusicplease.song.player


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