package com.example.fitnesstracker;

import android.util.Log;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class ConnectionCheckThread implements Runnable{
    private TextView status; //text to show status, which changes based on api response
    private final AtomicBoolean running = new AtomicBoolean(false); //boolean to stop running thread

    public ConnectionCheckThread(TextView statusText)
    {
        status=statusText;

    }


    @Override
    public void run() {
            running.set(true);
            while(running.get())
            {
                CheckConnection();
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        Log.i("T","Thread-Stops");
        }
        public void stop()
        {
            running.set(false);
        }

    private void CheckConnection() {
        //Fetch whether board is connected properly
        AndroidNetworking.get("http://35.189.84.173:2333/connect").build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                status.setText(response);
            }

            @Override
            public void onError(ANError anError) {
                status.setText("Something went wrong, check if api is active!");
            }
        });
    }

}
