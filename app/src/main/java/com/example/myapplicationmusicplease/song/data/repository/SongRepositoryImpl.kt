package com.example.myapplicationmusicplease.song.data.repository

import com.example.myapplicationmusicplease.song.data.remote.SongClient
import com.example.myapplicationmusicplease.song.domain.model.SongModel
import com.example.myapplicationmusicplease.song.domain.repository.SongRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SongRepositoryImpl() : SongRepository, KoinComponent {

    private val client by inject<SongClient>()
    override suspend fun getSongs(): Result<List<SongModel>> {
        return try {
            val response = client.getSongs().map { it.toModel()}
            Result.success(response)
        } catch (e: Exception) {
            println(e.printStackTrace())
            Result.failure(e)
        }
    }

}