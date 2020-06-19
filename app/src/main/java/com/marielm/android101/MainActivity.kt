package com.marielm.android101

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // sets the activity's layout

        val input: EditText = findViewById(R.id.input_name) // declare input field
        val submit: Button = findViewById(R.id.submit) // declare submit button

        // set an action on pressing submit button
        submit.setOnClickListener {
            val inputName: String = input.text.toString() // get the input text

            startActivity(ResultsActivity.create(this@MainActivity, inputName))
        }
    }
}
