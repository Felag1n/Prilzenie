package com.example.clicker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private lateinit var button: ImageButton
    private lateinit var resetButton: Button
    private lateinit var scoreText: TextView
    private var score: Int = 0
    private var maxScore: Int = 0
    private lateinit var shared: SharedPreferences

    private val images = arrayOf(
        R.drawable.meme_png,
        R.drawable.meme_png3,
        R.drawable.meme_png4,
        R.drawable.meme_png5,
        R.drawable.meme_png6,
        R.drawable.meme_png7
    )

    private val thresholds = listOf(30, 90, 180, 270, 360, 500)

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shared = getSharedPreferences("main", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.imageButton)
        resetButton = findViewById(R.id.resetButton)
        scoreText = findViewById(R.id.scoreText)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            maxScore = savedInstanceState.getInt("maxScore")
        } else {
            readData()
        }

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
        }

        resetButton.setOnClickListener {
            score = 0
            maxScore = 0
            button.setImageResource(images[0])
            scoreText.text = getString(R.string.player_score, score.toString())
            saveData()
        }

        scoreText.text = getString(R.string.player_score, score.toString())

        handler.post(object : Runnable {
            override fun run() {
                if (score > 0) {
                    score--
                    scoreText.text = getString(R.string.player_score, score.toString())
                    saveData()
                }
                handler.postDelayed(this, 300)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("maxScore", maxScore)
    }

    private fun readData() {
        score = shared.getInt("score", score)
        maxScore = shared.getInt("maxScore", maxScore)
    }

    private fun saveData() {
        val edit = shared.edit()
        edit.putInt("score", score)
        edit.putInt("maxScore", maxScore)
        edit.apply()
    }
}