package com.example.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    // variables for the restTimer
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    // variables for the exercises
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    // For the textToSpeak
    private var tts: TextToSpeech? = null
    private var ttsText : String = "Begin with"
    // For the sound after each exercise
    private var player: MediaPlayer? = null

    // For showing the RecyclerView
    private var exerciseAdapter: ExerciseStatusAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // For the back button on the toolbar
        setSupportActionBar(toolbar_exercise_activity)
        val actionbar = supportActionBar
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }
        // Initialize TextToSpeech()
        tts= TextToSpeech(this, this)
        // Use the List of Exercises
        exerciseList = Constants.defaultExerciseList()
        // Start the timer and check if the timer was running or not
        setupRestView()

        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        if(tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player != null){
            player!!.stop()
        }

        super.onDestroy()
    }

    // Sets various variables in the XML file accordingly and sets up the timer and its Interval
    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object: CountDownTimer(10000, 1000) {
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                // Initialise the Exercise
                setupExerciseView()
            }

            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                // This sets the progress member in the XML file to 10 and reduces it each second by 1
                progressBar.progress = 10-restProgress
                // Sets the text in XML to 10 and reduces it by 1 each second and converts it to a string
                tvTimer.text = (10 - restProgress).toString()
            }
        }.start() // To start the setRestProgressBar()
    }

    private fun setExerciseProgressBar() {
        progressBar.progress = exerciseProgress
        exerciseTimer = object: CountDownTimer(30000, 1000) {

            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                // This sets the progress member in the XML file to 10 and reduces it each second by 1
                progressBarExercise.progress = 30-exerciseProgress
                // Sets the text in XML to 10 and reduces it by 1 each second and converts it to a string
                tvExerciseTimer.text = (30 - exerciseProgress).toString()
            }
        }.start() // To start the setRestProgressBar()
    }

    private fun setupRestView() {
        try {
            // Plays the sound after each exercise and prevents it from looping
            player = MediaPlayer.create(applicationContext, R.raw.spongebob)
            player!!.isLooping = false
            player!!.start()
        }catch (e: Exception) {
            e.printStackTrace()
        }

        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE

        if(restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        upcomingExercise.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()


    }

    private fun setupExerciseView() {
        // Disables the restView visibility so it doesn't overlap the exerciseView
        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE

        if(exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        // Speaks out the current exercise name
        speakOut(ttsText+exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.ENGLISH)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The language specified is not supported!")
            }
        }else {
            Log.e("TTS", "Initializing failed!")
        }
    }
    // Function to use textToSpeak
    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView() {
        rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener{
            finish()
            customDialog.dismiss()
        }
        customDialog.tvNo.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

}
