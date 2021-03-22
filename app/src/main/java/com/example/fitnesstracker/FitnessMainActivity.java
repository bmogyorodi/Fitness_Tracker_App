package com.example.fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FitnessMainActivity extends AppCompatActivity {
    //Main menu containing buttons that lead to live read activity and Statistics
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_main);

        Button toLive=findViewById(R.id.button_toLive);
        Button toStats=findViewById(R.id.button_toStats);

        toLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FitnessMainActivity.this,LiveReadActivity.class);
                startActivity(intent);
            }
        });
        toStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FitnessMainActivity.this,StatsActivity.class);
                startActivity(intent);
            }
        });
    }
}