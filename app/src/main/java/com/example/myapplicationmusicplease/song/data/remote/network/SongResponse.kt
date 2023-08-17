package com.example.myapplicationmusicplease.song.data.remote.network

import com.example.myapplicationmusicplease.song.domain.model.SongModel
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val data: List<SongResponse>
)

@Serializable
data class SongResponse(
    val artist: String,
    val artwork: String,
    val id: String,
    val title: String,
    val url: String
){
    fun toModel() = SongModel(
        artist = artist,
        songPhoto = artwork,
        id = id,
        title = title,
        url = url
    )
}
