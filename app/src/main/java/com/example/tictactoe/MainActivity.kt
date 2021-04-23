package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private val url: String = Constants.serverUrl
    private val service: APICall = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(APICall::class.java)

    private var mGame: GameService?

    init {
        mGame = GameService.getInstance(url)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGame?.connect()
        play.setOnClickListener { play() }
    }

    private fun play() {
        if (playerId.text.isNotEmpty()) {
            val id = playerId.text.toString()

            runBlocking {
                val result = service.addPlayer(id).result
                mGame?.filter = result.matchId
                mGame?.status = result
                mGame?.player = result.player
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                startActivity(intent)
            }
        }
    }


}
