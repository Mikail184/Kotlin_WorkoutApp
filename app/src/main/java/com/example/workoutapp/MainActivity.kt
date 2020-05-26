package com.example.workoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llStart.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }
        llBMI.setOnClickListener {
            val intent1 = Intent(this, BMIActivity::class.java)
            startActivity(intent1)
        }
        llHistory.setOnClickListener {
            val intent2 = Intent(this, HistoryActivity::class.java)
            startActivity(intent2)
        }
    }
}
