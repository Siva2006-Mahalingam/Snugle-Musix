package com.music.jiosaavn

import android.util.Log
import com.music.jiosaavn.models.SaavnAlbum
import com.music.jiosaavn.models.SaavnAlbumDetails
import com.music.jiosaavn.models.SaavnArtist
import com.music.jiosaavn.models.SaavnChart
import com.music.jiosaavn.models.SaavnPlaylist
import com.music.jiosaavn.models.SaavnPlaylistDetails
import com.music.jiosaavn.models.SaavnSearchResponse
import com.music.jiosaavn.models.SaavnSong
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JioSaavnClient {
    private const val TAG = "JioSaavnClient"
    private const val BASE_URL = "https://www.jiosaavn.com/api.php"

    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    private val httpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 8_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 8_000
            }
            expectSuccess = false
        }
    }

    private suspend fun executeGet(params: Map<String, String>): String {
        val response = httpClient.get(BASE_URL) {
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
            header("Accept", "application/json, text/plain, */*")
            header("Accept-Language", "en-US,en;q=0.9")
            header("Cookie", "explicit_content=1")
            params.forEach { (k, v) -> parameter(k, v) }
        }
        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("JioSaavn API error: HTTP ${response.status.value}")
        }
        return response.bodyAsText()
    }

    suspend fun searchSongs(query: String, page: Int = 1, limit: Int = 20): Result<SaavnSearchResponse<SaavnSong>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "search.getResults",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "q" to query,
                "p" to page.toString(),
                "n" to limit.toString()
            )
        )
        json.decodeFromString<SaavnSearchResponse<SaavnSong>>(text)
    }

    suspend fun searchAlbums(query: String, page: Int = 1, limit: Int = 20): Result<SaavnSearchResponse<SaavnAlbum>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "search.getAlbumResults",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "q" to query,
                "p" to page.toString(),
                "n" to limit.toString()
            )
        )
        json.decodeFromString<SaavnSearchResponse<SaavnAlbum>>(text)
    }

    suspend fun searchPlaylists(query: String, page: Int = 1, limit: Int = 20): Result<SaavnSearchResponse<SaavnPlaylist>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "search.getPlaylistResults",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "q" to query,
                "p" to page.toString(),
                "n" to limit.toString()
            )
        )
        json.decodeFromString<SaavnSearchResponse<SaavnPlaylist>>(text)
    }

    suspend fun searchArtists(query: String, page: Int = 1, limit: Int = 20): Result<SaavnSearchResponse<SaavnArtist>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "search.getArtistResults",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "q" to query,
                "p" to page.toString(),
                "n" to limit.toString()
            )
        )
        json.decodeFromString<SaavnSearchResponse<SaavnArtist>>(text)
    }

    suspend fun getSongDetails(songId: String): Result<SaavnSong> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "song.getDetails",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "pids" to songId
            )
        )
        val jsonElement = json.parseToJsonElement(text)
        if (jsonElement is JsonObject && jsonElement.containsKey("songs")) {
            val songsArray = jsonElement["songs"]?.jsonArray
            val first = songsArray?.firstOrNull() ?: throw NoSuchElementException("Song not found")
            return@runCatching json.decodeFromJsonElement<SaavnSong>(first)
        } else if (jsonElement is JsonObject && jsonElement.containsKey(songId)) {
            val songObj = jsonElement[songId] ?: throw NoSuchElementException("Song not found")
            return@runCatching json.decodeFromJsonElement<SaavnSong>(songObj)
        }
        json.decodeFromString<SaavnSong>(text)
    }

    suspend fun getRecommendations(songId: String): Result<List<SaavnSong>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "reco.getrecos",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "pid" to songId
            )
        )
        val jsonElement = json.parseToJsonElement(text)
        if (jsonElement is JsonArray) {
            json.decodeFromJsonElement<List<SaavnSong>>(jsonElement)
        } else {
            emptyList()
        }
    }

    suspend fun getAlbumDetails(albumId: String): Result<SaavnAlbumDetails> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "content.getAlbumDetails",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "albumid" to albumId
            )
        )
        json.decodeFromString<SaavnAlbumDetails>(text)
    }

    suspend fun getPlaylistDetails(playlistId: String): Result<SaavnPlaylistDetails> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "playlist.getDetails",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0",
                "listid" to playlistId
            )
        )
        json.decodeFromString<SaavnPlaylistDetails>(text)
    }

    suspend fun getCharts(languages: List<String> = emptyList()): Result<List<SaavnChart>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "content.getCharts",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0"
            )
        )
        val jsonElement = json.parseToJsonElement(text)
        if (jsonElement is JsonArray) {
            json.decodeFromJsonElement<List<SaavnChart>>(jsonElement)
        } else {
            emptyList()
        }
    }

    suspend fun getTopSearches(languages: List<String> = emptyList()): Result<List<String>> = runCatching {
        val text = executeGet(
            mapOf(
                "__call" to "content.getTopSearches",
                "_format" to "json",
                "_marker" to "0",
                "api_version" to "4",
                "ctx" to "wap6dot0"
            )
        )
        val jsonElement = json.parseToJsonElement(text)
        if (jsonElement is JsonArray) {
            jsonElement.mapNotNull {
                when {
                    it is JsonObject && it.containsKey("title") -> it["title"]?.jsonPrimitive?.content
                    it is JsonObject && it.containsKey("query") -> it["query"]?.jsonPrimitive?.content
                    else -> it.jsonPrimitive.contentOrNull
                }
            }
        } else {
            emptyList()
        }
    }
}
