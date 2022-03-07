package ru.dubr.sharedpreferences

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Toast
import androidx.core.view.forEach
import ru.dubr.sharedpreferences.databinding.ActivityMainBinding
import kotlin.properties.Delegates
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    private val listOfId = mutableListOf<Int>()

    private var number1 by Delegates.notNull<Int>()
    private var number2 by Delegates.notNull<Int>()
    private var answerResult by Delegates.notNull<Int>()
    private var highScore by Delegates.notNull<Int>()
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        preferences = getPreferences(MODE_PRIVATE)
        highScore = preferences.getInt(KEY_HIGH_SCORE, 0)


        binding.root.forEach {
            if (it is Button) {
                listOfId.add(it.id)
            }
        }

        setTimer()
        updateUi()
        setupButtons()
    }

    private fun setTimer() {
        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                binding.timerTextView.text = seconds.toString()
            }

            override fun onFinish() {
                Toast.makeText(applicationContext, "timer is finished", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }

    private fun setQuestion() {
        number1 = Random.nextInt(100)
        number2 = Random.nextInt(100)

        answerResult = number1 + number2
    }

    private fun setupButtons() {

        val randomButtonId = listOfId.random()
        val sum = answerResult.toString()

        binding.root.forEach { it ->
            if (it is Button) {
                when (it.id) {
                    randomButtonId -> it.text = sum
                    else -> it.text = Random.nextInt(200).toString()
                }

                it.setOnClickListener {
                    if (it.id == randomButtonId) {
                        Toast.makeText(applicationContext, "right", Toast.LENGTH_SHORT).show()
                        nextQuestion()
                    } else {
                        Toast.makeText(applicationContext, "wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun nextQuestion() {
        score += 5
        if (score > highScore) {
            highScore = score
            preferences.edit()
                .putInt(KEY_HIGH_SCORE, highScore)
                .apply()

            updateUi()
            setupButtons()
        }
    }

    private fun updateUi() {
        setQuestion()
        binding.highScoreTextView.text = highScore.toString()
        binding.scoreTextView.text = score.toString()
        binding.questionTextView.text = getString(R.string.question, number1, number2)
    }

    companion object {

        private const val KEY_HIGH_SCORE = "KEY_NAME"
        private const val UNDEFINED_HIGH_SCORE = -1
    }
}