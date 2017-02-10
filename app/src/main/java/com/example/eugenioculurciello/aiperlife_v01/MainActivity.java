package com.example.eugenioculurciello.aiperlife_v01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the Create Game button */
    public void createGame(View view) {
        Intent intent = new Intent(this, CreateGame.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Play Game button */
    public void playGame(View view) {
        Intent intent = new Intent(this, PlayGame.class);
        startActivity(intent);
    }

}
