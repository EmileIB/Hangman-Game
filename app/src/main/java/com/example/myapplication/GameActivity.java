package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    String wordToGuess, editedWord = "";
    TextView guessTextView, attemptsTextView, wonOrLost, backButton;
    int attempts, initAttempts;
    int[] imagesId;
    StringBuilder currentGuess;
    Button restartGame;
    ImageView lvlImg;
    GridLayout grid;
    ArrayList<View> layoutButtons;
    private String URL = "https://drmelhemibrahim.000webhostapp.com/leaderboard.php";
    boolean isLoggedIn = !MainActivity.username.equals("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent pastIntent = getIntent();
        wordToGuess = GenerateWord();
        attempts = pastIntent.getIntExtra("attempts", 0);
        initAttempts = attempts;
        initialiseImgId();
        initializeWords();

        guessTextView = findViewById(R.id.wordToGuess);
        attemptsTextView = findViewById(R.id.attempts);
        restartGame = findViewById(R.id.restartGame);
        lvlImg = findViewById(R.id.lvlState);
        grid = findViewById(R.id.gridLayout);
        wonOrLost = findViewById(R.id.wonOrLost);
        layoutButtons = grid.getTouchables();

        lvlImg.setImageResource(imagesId[0]);

        guessTextView.setText(currentGuess);
        attemptsTextView.setText(getString(R.string.numOfAttempts, attempts));

        restartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(GameActivity.this, MainActivity.class);
                startActivity(back);
            }
        });

        setTitle("Hangman");

    }

    void initialiseImgId(){
        imagesId = new int[]{
                R.drawable.hangman0,
                R.drawable.hangman1,
                R.drawable.hangman2,
                R.drawable.hangman3,
                R.drawable.hangman4,
                R.drawable.hangman5,
                R.drawable.hangman6,
        };
    }

    void initializeWords(){
        int len = wordToGuess.length(); // play
        currentGuess = new StringBuilder(); // _ _ _ _
        editedWord = ""; // p l a y
        for (int i = 0; i < len; i++){
            if (i == len - 1) {
                currentGuess.append("_");
                editedWord += wordToGuess.charAt(i);
            }
            else {
                currentGuess.append("_ ");
                editedWord += wordToGuess.charAt(i) + " ";
            }
        }
    }

    public void onLetterClick(View view){
        int id = view.getId();
        Button x = findViewById(id);
        char letter;
        letter = x.getText().toString().toLowerCase().charAt(0);

        int len = editedWord.length();
        char currChar;
        boolean isValidLetter = false;
        for (int i = 0; i < len; i++){
            currChar = editedWord.charAt(i);
            if (letter == currChar){
                currentGuess.setCharAt(i, currChar);
                isValidLetter = true;
            }
        }

        view.setEnabled(false);

        if (isValidLetter){
            guessTextView.setText(currentGuess);
            if (editedWord.equals(currentGuess.toString())){ // won the game
                disableButtons();
                if (isLoggedIn)
                    wonOrLost.setText(getString(R.string.you_won_logged_in, (attempts == 6)? 1:2));
                else {
                    wonOrLost.setText(R.string.you_won_);
                    Toast.makeText(GameActivity.this,"Login to keep track of your record!", Toast.LENGTH_SHORT).show();
                }
                wonOrLost.setVisibility(TextView.VISIBLE);
                wonOrLost.setTextColor(getResources().getColor(R.color.green));
                updateDatabaseScore();
            }
        }
        else {
            attempts -= 1;
            attemptsTextView.setText(getString(R.string.numOfAttempts, attempts));
            handleImgs(attempts);
            if (attempts == 0){
                disableButtons();
                wonOrLost.setText(getString(R.string.you_lost, wordToGuess));
                wonOrLost.setVisibility(TextView.VISIBLE);
                wonOrLost.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    public String GenerateWord(){
        try {
            InputStream is = getAssets().open("english_words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> words = new ArrayList<String>();

            String word;
            while( (word = reader.readLine())!= null )
                words.add(word);
            String randWord = words.get(new Random().nextInt(words.size()));
            Log.i("Word to Guess", randWord);
            return randWord;

        } catch (IOException e) {
            return null;
        }
    }

    void handleImgs(int attempts){
        if (initAttempts == 6)
            lvlImg.setImageResource(imagesId[6 - attempts]);
        else {
            if (attempts == 3)
                lvlImg.setImageResource(imagesId[1]);
            else if (attempts == 2)
                lvlImg.setImageResource(imagesId[2]);
            else if (attempts == 1)
                lvlImg.setImageResource(imagesId[4]);
            else if (attempts == 0)
                lvlImg.setImageResource(imagesId[6]);
        }
    }

    private void disableButtons() {
        for (View v : layoutButtons){
            v.setEnabled(false);
        }
    }

    private void enableButtons() {
        for (View v : layoutButtons){
            v.setEnabled(true);
        }
    }

    void restartGame(){
        attempts = initAttempts;
        attemptsTextView.setText(getString(R.string.numOfAttempts, attempts));
        enableButtons();
        lvlImg.setImageResource(imagesId[0]);
        wordToGuess = GenerateWord();
        initializeWords();
        guessTextView.setText(currentGuess);
        if (wonOrLost.getVisibility() == TextView.VISIBLE)
            wonOrLost.setVisibility(TextView.GONE);
    }

    public void updateDatabaseScore(){
        String username = MainActivity.username;
        if (!isLoggedIn)
            return;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            if (response.equals("failure")) {
                Toast.makeText(GameActivity.this, "Failed to update online score!", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(GameActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show()) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("increment", (attempts == 6)? "1":"2");
                return data;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(GameActivity.this);
        requestQueue.add(stringRequest);
    }

}