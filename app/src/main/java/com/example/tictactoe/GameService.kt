package com.example.tictactoe

import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.net.URISyntaxException
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter
import io.socket.engineio.client.EngineIOException

class GameService private constructor(url: String?) {
    var socket: Socket? = null
    var filter: String? = null
    var status: PlayStatus? = null
    var player: String? = null

    var onStartListener: (() -> Unit)? = null
    var onMoveListener: (() -> Unit)? = null

    init {
        try {
            socket = IO.socket(url)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun hasStarted() = status?.players?.X != null && status?.players?.O != null

    fun connect() {
        socket?.on("join", onUserJoin)
        socket?.on("submitMove", onNewMove)
        socket?.on(Socket.EVENT_CONNECT, onConnect)
        socket?.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket?.connect()
    }

    private val onError: Emitter.Listener = Emitter.Listener { args ->
        val data = args[0] as EngineIOException
        Log.d("socket.event.error", data.cause.toString())
    }

    private val onConnect: Emitter.Listener = Emitter.Listener {
        Log.d("socket.event.connect", "new connection")
    }

    private val onUserJoin: Emitter.Listener = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        Log.d("join", data.toString())
        val gson = Gson()
        val status: PlayStatus = gson.fromJson(data.toString(), PlayStatus::class.java)

        if (status.matchId == filter) {
            this.status = status
            if (hasStarted()) {
                onStartListener?.invoke()
            }
        }
    }

    private val onNewMove: Emitter.Listener = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        Log.d("submit", data.toString())
        val gson = Gson()
        val status: PlayStatus = gson.fromJson(data.toString(), PlayStatus::class.java)
        this.status = status

        if (status.matchId == filter) {
            this.status = status
            onMoveListener?.invoke()
        }
    }


    companion object {
        private var instance: GameService? = null

        fun getInstance(url: String? = null): GameService {
            if (instance == null){
                instance =
                    GameService(url);
            }
            return instance!!
        }
    }
}