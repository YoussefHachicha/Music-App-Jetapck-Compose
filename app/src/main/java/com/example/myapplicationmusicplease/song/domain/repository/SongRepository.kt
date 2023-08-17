package com.example.myapplicationmusicplease.song.domain.repository

import com.example.myapplicationmusicplease.song.domain.model.SongModel

interface SongRepository {
    suspend fun getSongs(): Result<List<SongModel>>
}