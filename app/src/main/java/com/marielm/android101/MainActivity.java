package com.marielm.android101;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private String baseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // sets the activity's layout

        final EditText input = findViewById(R.id.input_name); // declare input field
        Button submit = findViewById(R.id.submit); // declare submit button

        baseMessage = getString(R.string.hi_there_name); // our base greeting message

        // set an action on pressing submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String inputName = input.getText().toString(); // get the input text

                startActivity(ResultsActivity.create(MainActivity.this, inputName));

            }
        });
    }
}
