package com.music.jiosaavn.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaavnArtistItem(
    val id: String? = null,
    val name: String? = null,
    val role: String? = null,
    val image: String? = null
)

@Serializable
data class SaavnArtistMap(
    @SerialName("primary_artists") val primary_artists: List<SaavnArtistItem> = emptyList(),
    val artists: List<SaavnArtistItem> = emptyList()
)

@Serializable
data class SaavnMoreInfo(
    val album: String? = null,
    @SerialName("album_id") val albumId: String? = null,
    val duration: String? = null,
    @SerialName("encrypted_media_url") val encryptedMediaUrl: String? = null,
    @SerialName("artistMap") val artistMap: SaavnArtistMap? = null
)

@Serializable
data class SaavnSong(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val image: String? = null,
    @SerialName("explicit_content") val explicitContent: String? = null,
    @SerialName("more_info") val more_info: SaavnMoreInfo? = null
)

@Serializable
data class SaavnAlbum(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val image: String? = null,
    val year: String? = null,
    @SerialName("explicit_content") val explicitContent: String? = null
)

@Serializable
data class SaavnPlaylistMoreInfo(
    @SerialName("song_count") val song_count: String? = null
)

@Serializable
data class SaavnPlaylist(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val image: String? = null,
    @SerialName("more_info") val more_info: SaavnPlaylistMoreInfo? = null
)

@Serializable
data class SaavnArtist(
    val id: String? = null,
    val name: String? = null,
    val image: String? = null
)

@Serializable
data class SaavnAlbumDetails(
    val id: String? = null,
    val title: String? = null,
    val name: String? = null,
    val primary_artists: String? = null,
    val year: String? = null,
    val image: String? = null,
    val release_date: String? = null,
    val songs: List<SaavnSong> = emptyList()
)

@Serializable
data class SaavnPlaylistDetails(
    val id: String? = null,
    val title: String? = null,
    val listname: String? = null,
    val image: String? = null,
    val list_count: String? = null,
    val list: List<SaavnSong> = emptyList()
)

@Serializable
data class SaavnChart(
    val id: String? = null,
    val title: String? = null,
    val count: String? = null,
    val image: String? = null
)

@Serializable
data class SaavnSearchResponse<T>(
    val total: Int = 0,
    val start: Int = 0,
    val results: List<T> = emptyList()
)
