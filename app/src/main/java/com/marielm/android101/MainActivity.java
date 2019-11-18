package com.marielm.android101;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // sets the activity's layout

        final EditText input = findViewById(R.id.input_name); // declare input field
        Button submit = findViewById(R.id.submit); // declare submit button

        // set an action on pressing submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String inputName = input.getText().toString(); // get the input text

                startActivity(ResultsActivity.create(MainActivity.this, inputName));

            }
        });
    }
}
