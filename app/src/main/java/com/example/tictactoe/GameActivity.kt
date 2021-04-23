package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GameActivity : AppCompatActivity() {
    private val url: String = Constants.serverUrl
    private val service: APICall = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(APICall::class.java)
    private val game = GameService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        if (game.hasStarted()) {
            startGame()
        } else {
            game.onStartListener = { startGame() }
        }
        game.onMoveListener = { onNewMove() }

        setViews()
    }

    private fun onNewMove() {
        runOnUiThread {
            val buttons = arrayOf(
                arrayOf(button11, button12, button13),
                arrayOf(button21, button22, button23),
                arrayOf(button31, button32, button33)
            )
            if (game.status?.player != game.player) {
                for (r in 0..2) {
                    for (c in 0..2) {
                        val play = game.status?.board?.get(r)?.get(c)
                        if (play != "") {
                            buttons[r][c].text = play
                            buttons[r][c].isEnabled = false
                        }
                    }
                }
            }
            if (game.status?.gameOver == true) {
                turn.text = "Winner is " + game.status?.winner
                end.visibility = View.VISIBLE
            } else {
                turn.text = game.status?.turn + " to Play"
            }
        }
    }

    private fun startGame() {
        runOnUiThread {
            var youX = ""
            var youO = ""

            if (game.player == "X") {
                youX = "You: "
            } else {
                youO = "You: "
            }

            playX.text = youX + game.status?.players?.X
            playO.text = youO + game.status?.players?.O
            turn.text = game.status?.turn + " to Play"
            load.visibility = View.GONE
        }
    }

    private fun setViews() {
        button11.setOnClickListener { submit(1, 1, button11) }
        button12.setOnClickListener { submit(1, 2, button12) }
        button13.setOnClickListener { submit(1, 3, button13) }
        button21.setOnClickListener { submit(2, 1, button21) }
        button22.setOnClickListener { submit(2, 2, button22) }
        button23.setOnClickListener { submit(2, 3, button23) }
        button31.setOnClickListener { submit(3, 1, button31) }
        button32.setOnClickListener { submit(3, 2, button32) }
        button33.setOnClickListener { submit(3, 3, button33) }
    }

    private fun submit(row: Int, column: Int, button: Button) {
        runBlocking {
            if (game.status?.turn == game.player) {
                val result = service.submitMove(game.player!!, game.filter!!, row -1, column -1)
                if (result.result) {
                    runOnUiThread {
                        button.text = game.player
                        button.isEnabled = false
                    }
                }
            }
        }
    }
}