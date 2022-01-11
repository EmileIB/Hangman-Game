package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    RadioButton easy, hard;
    TextView loginTv;
    public static String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startGame = findViewById(R.id.startGame);
        easy = findViewById(R.id.easy);
        hard = findViewById(R.id.hard);
        loginTv = findViewById(R.id.loginTv);

        startGame.setOnClickListener(view -> {
            if (!easy.isChecked() && !hard.isChecked())
                Toast.makeText(MainActivity.this, "Please choose a difficulty", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("attempts", getAttempts());
                startActivity(intent);
            }
        });
        setTitle("Hangman");
        if (!username.equals(""))
            loginTv.setVisibility(TextView.GONE);
        else
            loginTv.setVisibility(TextView.VISIBLE);
    }

    public int getAttempts(){
        if (easy.isChecked())
            return 6;
        else
            return 4;
    }

    public void login(View view){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void leaderboard(View view){
        Intent i = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(i);
    }

}