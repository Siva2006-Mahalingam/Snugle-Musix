package com.music.jiosaavn

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object JioSaavnCrypto {
    private const val DES_KEY = "38346591"

    fun getHighQualityThumbnail(imageUrl: String?): String? {
        if (imageUrl.isNullOrBlank()) return null
        return imageUrl
            .replace("50x50", "500x500")
            .replace("150x150", "500x500")
            .replace("http://", "https://")
    }

    fun decryptMediaUrl(encryptedUrl: String): String? {
        return runCatching {
            val keySpec = SecretKeySpec(DES_KEY.toByteArray(Charsets.UTF_8), "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedBytes = Base64.decode(encryptedUrl.trim(), Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8).trim()
        }.getOrNull()
    }

    fun getMediaUrlWithQuality(decryptedUrl: String, bitrate: Int): String {
        val targetSuffix = "_$bitrate.mp4"
        val regex = Regex("_(12|48|96|160|320)\\.mp4")
        return if (regex.containsMatchIn(decryptedUrl)) {
            decryptedUrl.replace(regex, targetSuffix)
        } else if (decryptedUrl.endsWith(".mp4")) {
            decryptedUrl.removeSuffix(".mp4") + targetSuffix
        } else {
            decryptedUrl
        }
    }
}
