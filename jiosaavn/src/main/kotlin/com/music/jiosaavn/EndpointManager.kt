package com.music.jiosaavn

object EndpointManager {
    val SERVERS = listOf(
        "https://saavn.echomusic.fun",
        "https://jiosaavn-api.mac-adityadav9532.workers.dev",
        "https://jiosaavn-api.pc-adityadav9532.workers.dev"
    )

    var currentServerIndex = 0
        private set

    fun getCurrentServer(): String {
        return SERVERS[currentServerIndex]
    }

    fun reportSuccess(server: String) {
        val index = SERVERS.indexOf(server)
        if (index != -1 && currentServerIndex != index) {
            currentServerIndex = index
        }
    }

    fun reportFailure(failedServer: String, reason: String = "") {
        val failedIndex = SERVERS.indexOf(failedServer)
        if (failedIndex != -1 && failedIndex == currentServerIndex) {
            currentServerIndex = (currentServerIndex + 1) % SERVERS.size
        }
    }

    fun resetToPrimary() {
        currentServerIndex = 0
    }
}
