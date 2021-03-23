package com.example.fitnesstracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.security.NetworkSecurityPolicy;
import android.util.Log;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.OkHttpClient;

public class LiveReadActivity extends AppCompatActivity {

    String TAG="LIVE";
    TextView liveRead;
    TextView status;
    TextView tdRun,tdJog,tdWalk,tdCycle;
    int running=0;
    int walking=0;
    int jogging=0;
    int cycling=0;
    Timer t;
    LabelThread labelReader;
    ConnectionCheckThread statusReader;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_read);

        //Setting up textview

        liveRead=findViewById(R.id.live_predictionText);
        status=findViewById(R.id.status_text);
        tdCycle=findViewById(R.id.todayCycle);
        tdJog=findViewById(R.id.todayJog);
        tdRun=findViewById(R.id.todayRun);
        tdWalk=findViewById(R.id.todayWalk);

        //Setting up different threads
        // Timer t: Fetches Todays data from database every 30 second
        // label t : Fetches latest classfied label from api
        // status t: Checks whether board is connected
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                FetchToday();
            }
        }, 0, 30000);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AndroidNetworking.initialize(getApplicationContext());
        statusReader=new ConnectionCheckThread(this);
        Thread statusT=new Thread(statusReader);
        statusT.start();
        labelReader=new LabelThread(this);
        Thread labelT=new Thread(labelReader);
        labelT.start();




    }

    @Override
    protected void onStop() {
        super.onStop();
        //shut down threads on stopping activity so no unnecesarily calls are made to api
        t.cancel();
        labelReader.stop();
        statusReader.stop();


    }

    @Override
    protected void onResume() {
        super.onResume();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                FetchToday();
            }
        }, 0, 30000);
        statusReader=new ConnectionCheckThread(this);
        Thread statusT=new Thread(statusReader);
        statusT.start();
        labelReader=new LabelThread(this);
        Thread labelT=new Thread(labelReader);
        labelT.start();
    }

    public void setTodayData() { // create chart based on Movement data
        tdJog.setText(String.valueOf(jogging)+" m");
        tdWalk.setText(String.valueOf(walking)+" m");
        tdRun.setText(String.valueOf(running)+" m");
        tdCycle.setText(String.valueOf(cycling)+" m");

    }
    private void FetchToday()
    {   //Fetch date and query days data
        Date today = new Date();
        SimpleDateFormat dft=new SimpleDateFormat("yyyy/MM/dd");
        Log.i(TAG,dft.format(today));
        String [] date=dft.format(today).split("/");
        int year=Integer.parseInt(date[0]);
        int month= Integer.parseInt(date[1]);
        int day=Integer.parseInt(date[2]);
        DayQuery(year,month,day);


    }
    private void DayQuery(int year, int month, int day) {

        //Fetching todays motion data from API
        AndroidNetworking.get(getString(R.string.api_ip)+"/day")
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
                        cycling=response.getInt(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{}
                setTodayData(); //show data in table

            }

            @Override
            public void onError(ANError anError) {


            }
        });
    }

}