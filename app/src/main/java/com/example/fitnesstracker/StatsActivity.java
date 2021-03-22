package com.example.fitnesstracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


import java.util.Date;
import java.text.SimpleDateFormat;

public class StatsActivity extends AppCompatActivity {

    String TAG="STATS";
    TextView year;
    Spinner month;
    TextView day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up buttons and textviews
        setContentView(R.layout.activity_stats);
        Button test=findViewById(R.id.statTesting);
         month = (Spinner) findViewById(R.id.month_spinner);
         year=(TextView) findViewById(R.id.enter_year);
         day=(TextView) findViewById(R.id.enter_day);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(adapter);
        month.getSelectedItemPosition();
        //month is in a spinner, where the user can select the month in dropdown view,
        // the list is made so every month is aligned with their number in date format (0 is all, for yearly stats)
        SetQueryToday(); //Default setting of query is set for today
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStats(); //Get statistics data based on query parameters entered
            }
        });
    }



    private void SetQueryToday() {
        //Sets up textviews and spinner to match data for fetching todays movement data
        Date today = new Date();
        SimpleDateFormat dft=new SimpleDateFormat("yyyy/MM/dd");
        Log.i(TAG,dft.format(today));
        String [] date=dft.format(today).split("/");
        year.setText(date[0]);
        month.setSelection(Integer.parseInt(date[1]));
        day.setText(date[2]);
    }


    private void GetStats()
    {
        //Start the showstats activity, passing the query parameters with the intent
        if(year.getText().toString().equals(""))
        {
            //year must be selected regardless of query type
            Toast.makeText(this,"You must select a year!",Toast.LENGTH_SHORT).show();
            return;
        }
        try
        {
            Intent show=new Intent(StatsActivity.this,ShowStatsActivity.class);
            show.putExtra("year",Integer.parseInt(year.getText().toString()));
            show.putExtra("month",month.getSelectedItemPosition());
            if (!day.getText().toString().equals(""))
            {
                show.putExtra("day",Integer.parseInt(day.getText().toString()));
            }
            startActivity(show);
        }catch (NumberFormatException e)
        {
            Toast.makeText(this,"Incorrect input for day or year, must choose integer number!",Toast.LENGTH_SHORT).show();
        }

    }


}