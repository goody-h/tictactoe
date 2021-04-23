package com.example.tictactoe

data class PlayResponse (val result: PlayStatus)

data class SubmitMoveResponse(val result: Boolean)

data class PlayStatus(
    val player: String,
    val players: Players,
    val matchId: String,
    val gameOver: Boolean,
    val turn: String,
    val board: List<List<String>>,
    val isNewUser: Boolean?,
    val winner: String?
)

data class Players(val X: String?, val O: String?)
