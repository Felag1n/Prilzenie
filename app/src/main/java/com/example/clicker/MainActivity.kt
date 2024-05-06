package com.example.clicker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var button: ImageButton
    private lateinit var scoreText: TextView
    private lateinit var timerText: TextView
    private var score: Int = 0
    private var maxScore: Int = 0
    private lateinit var shared: SharedPreferences
    private lateinit var countDownTimer: CountDownTimer
    private val images = arrayOf(
        R.drawable.meme_png,
        R.drawable.meme_png3,
        R.drawable.meme_png4,
        R.drawable.meme_png5,
        R.drawable.meme_png6,
        R.drawable.meme_png7
    )

    private val thresholds = listOf(30, 90, 180, 270, 360, 400)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shared = getSharedPreferences("main", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.imageButton)
        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            maxScore = savedInstanceState.getInt("maxScore")
        } else {
            readData()
        }

        countDownTimer = object : CountDownTimer(160000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                timerText.text = "Время до сброса: ${minutes}m ${seconds}s"
                decreaseScore()
            }

            override fun onFinish() {
                timerText.text = "Время до сброса"
                score = 0
                maxScore = 0
                button.setImageResource(images[0])
                scoreText.text = getString(R.string.player_score, score.toString())
                saveData()

                restartCountdownTimer()


                askForNewScore()
            }
        }

        countDownTimer.start()

        button.setOnClickListener {
            score++
            maxScore = maxOf(maxScore, score)

            var index = 0
            for (i in thresholds.indices) {
                if (maxScore >= thresholds[i]) {
                    index = i
                }
            }

            button.setImageResource(images[index])
            scoreText.text = getString(R.string.player_score, score.toString())
            saveData()

            if (score >= 500) {

                askForNewScore()
                restartGame()
            }
        }

        scoreText.text = getString(R.string.player_score, score.toString())
    }

    private fun decreaseScore() {
        if (score > 0) {
            score-= 1 + score/100;
            scoreText.text = getString(R.string.player_score, score.toString())
            saveData()
        }
    }

    private fun restartCountdownTimer() {
        countDownTimer.cancel()
        countDownTimer.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("maxScore", maxScore)
    }

    private fun readData() {
        val prefs = getSharedPreferences("main", Context.MODE_PRIVATE)
        score = prefs.getInt("score", 0)
        maxScore = prefs.getInt("maxScore", 0)
    }

    private fun saveData() {
        val edit = shared.edit()
        edit.putInt("score", score)
        edit.putInt("maxScore", maxScore)
        edit.apply()
    }

    private fun askForNewScore() {
        Toast.makeText(this, "Всё это конец!", Toast.LENGTH_SHORT).show()
    }


    private fun restartGame() {
        score = 0
        maxScore = 0
        button.setImageResource(images[0])
        scoreText.text = getString(R.string.player_score, score.toString())
        saveData()
        restartCountdownTimer()
    }
}
