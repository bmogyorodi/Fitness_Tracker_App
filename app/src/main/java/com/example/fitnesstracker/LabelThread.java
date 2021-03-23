package com.example.fitnesstracker;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class LabelThread implements Runnable {
    private LiveReadActivity context;
    private TextView liveRead; //Textview where data is printed
    private final AtomicBoolean running = new AtomicBoolean(false); // assists in shutting down thread

    public LabelThread(LiveReadActivity context)
    {
        this.context=context;
        liveRead=context.liveRead;
    }
    @Override
    public void run() {
        running.set(true);
        while(running.get())
        {
            ChangeLabel();
            try {
                sleep(1000);
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

    private void ChangeLabel()
    {
        //get latest classified label from the api in string format and show on screen
        AndroidNetworking.get(context.getString(R.string.api_ip)+"/").build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                liveRead.setText(response);
            }

            @Override
            public void onError(ANError anError) {
                liveRead.setText("API is not available!");
            }
        });
    }

}
