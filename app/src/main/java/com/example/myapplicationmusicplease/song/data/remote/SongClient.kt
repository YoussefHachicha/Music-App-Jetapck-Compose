package com.example.myapplicationmusicplease.song.data.remote

import com.example.myapplicationmusicplease.core.utils.SongUrl
import com.example.myapplicationmusicplease.song.data.remote.network.Response
import com.example.myapplicationmusicplease.song.data.remote.network.SongResponse
import com.example.myapplicationmusicplease.core.utils.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
class SongClient : KoinComponent {
    private val client: HttpClient by inject(named("NoAuth"))
    suspend fun getSongs(): List<SongResponse> = handleErrors<Response> {
        client.get(urlString = SongUrl)
    }.data
}
