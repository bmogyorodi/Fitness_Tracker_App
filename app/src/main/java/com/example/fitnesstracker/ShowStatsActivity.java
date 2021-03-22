package com.example.fitnesstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

public class ShowStatsActivity extends AppCompatActivity {
//activity to show statistics which were queried by front activity StatsActivity
    PieChart pieChart;
    TextView tvWalk,tvJog,tvRun,tvCycle;
    TextView queryTitle;
    String TAG="STATSHOW";
    int running=0;
    int walking=0;
    int jogging=0;
    int cycling=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);
        tvCycle=findViewById(R.id.tvCycle);
        tvRun=findViewById(R.id.tvRun);
        tvJog=findViewById(R.id.tvJog);
        tvWalk=findViewById(R.id.tvWalk);
        queryTitle=findViewById(R.id.query_title_text);
        pieChart = findViewById(R.id.piechart);
        //setPieData();
        AndroidNetworking.initialize(getApplicationContext()); //initialize to fetch data from api later
        int year=getIntent().getIntExtra("year",2021); //fetching year, month and day from intent based on which query is made
        int month=getIntent().getIntExtra("month",3);
        int day=getIntent().getIntExtra("day",0);
        SelectQuery(year,month,day); //Carries out query and prints data on piechart and other cardview
    }

    private void SelectQuery(int year, int month, int day) {
        CreateQueryTitle(year,month,day); //Create query title to show what the data is for
        //select query type based on input parameters
        if(month==0) //means all was selected so year query is selected (sum of all month)
        {
            YearQuery(year);
            return;
        }
        if(day==0) // if day was not selected it is interpreted as a month query
        {
            MonthQuery(year,month);
            return;
        }
        DayQuery(year,month,day); //otherwise query a specific day
    }

    private void CreateQueryTitle(int year, int month, int day) {
        //Choose query type and create string of format Query type: yyyy/mm/dd, cut shorter if necessary
        String queryType="Day Record";
        if(day==0)
        {
            queryType="Month Record";
        }
        if(month==0)
        {
            queryType="Year Record";
        }
        String yearS=String.valueOf(year);
        String monthS= month==0 ? "" : "/"+String.valueOf(month);
        String dayS= Objects.equals(monthS, "") ||day==0 ? "":"/"+String.valueOf(day);
        String queryMessage=queryType+" : "+yearS+monthS+dayS;
        queryTitle.setText(queryMessage);
    }

    private void DayQuery(int year, int month, int day) {

        //sends get request to api with sent in parameters
        AndroidNetworking.get("http://35.189.84.173:2333/day")
                .addQueryParameter("year",String.valueOf(year))
                .addQueryParameter("month",String.valueOf(month))
                .addQueryParameter("day",String.valueOf(day))
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, String.valueOf(response.length()));
                if (response.length()==4)
                {
                    try {
                        running=response.getInt(0);
                        walking=response.getInt(1);
                        jogging=response.getInt(2);
                        cycling=response.getInt(3); //sending data to activity variables
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                setPieData(); //displaying data

            }

            @Override
            public void onError(ANError anError) {
                APIUnavailableMessage();
            }
        });
    }

    private void MonthQuery(int year, int month) {
        //same as above but with month, different path and parameters
        AndroidNetworking.get("http://35.189.84.173:2333/month")
                .addQueryParameter("year",String.valueOf(year))
                .addQueryParameter("month",String.valueOf(month))
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length()==4)
                {
                    try {
                        running=response.getInt(0);
                        walking=response.getInt(1);
                        jogging=response.getInt(2);
                        cycling=response.getInt(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                setPieData();

            }

            @Override
            public void onError(ANError anError) {
                APIUnavailableMessage();
            }
        });

    }

    private void YearQuery(int year) {
        //same as above but with year, different path and parameters
        AndroidNetworking.get("http://35.189.84.173:2333/year")
                .addQueryParameter("year",String.valueOf(year))
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length()==4)
                {
                    try {
                        running=response.getInt(0);
                        walking=response.getInt(1);
                        jogging=response.getInt(2);
                        cycling=response.getInt(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{}
                setPieData();

            }

            @Override
            public void onError(ANError anError) {
                APIUnavailableMessage();
            }
        });
    }

    private void setPieData() { // create chart based on Movement data
        tvJog.setText(String.valueOf(jogging)+" m");
        tvWalk.setText(String.valueOf(walking)+" m");
        tvRun.setText(String.valueOf(running)+" m");
        tvCycle.setText(String.valueOf(cycling)+" m");
        pieChart.clearChart();// clear it in case it was drawn before, just to avoid error, (not possible)
        pieChart.addPieSlice( new PieModel("Jogging",jogging, Color.parseColor("#66BB6A")));
        pieChart.addPieSlice( new PieModel("Cycling",cycling,Color.parseColor("#29B6F6")));
        pieChart.addPieSlice( new PieModel("Walking",walking,Color.parseColor("#FFA726")));
        pieChart.addPieSlice( new PieModel("Running",running, Color.parseColor("#EF5350")));


        pieChart.startAnimation();
    }
    private void APIUnavailableMessage() //Send toast message if API is not connected
    {
        Toast.makeText(this,"API is not available! Check connection!",Toast.LENGTH_SHORT).show();

    }
}