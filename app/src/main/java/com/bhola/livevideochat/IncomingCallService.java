package com.bhola.livevideochat;

// IncomingCallService.java
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class IncomingCallService extends Service {

    private static final long DELAY_TIME_MILLIS = 2000; // 30 seconds delay

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Handle incoming call and show UI overlay
                showIncomingCallOverlay();
            }
        }, DELAY_TIME_MILLIS);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showIncomingCallOverlay() {

//        Fragment_Calling incomingCallFragment = new Fragment_Calling();
//
////        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(android.R.id.content, incomingCallFragment).addToBackStack(null).commit();
//
//
//
//        // Create a layout parameters object for the overlay view
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                PixelFormat.TRANSLUCENT);
//
//        // Get the WindowManager
//        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//        // Create an instance of the incoming call fragment
//        Fragment_Calling incomingCallFragment = new Fragment_Calling();
//
//        // Pass data to the fragment using a Bundle
//        Bundle args = new Bundle();
//        String name = readGirlsVideo();
//        args.putString("name", name);
//        incomingCallFragment.setArguments(args);
//
//        // Create a view from the fragment layout
//        View view = incomingCallFragment.getView();
//
//        // Add the view to the WindowManager
//        if (view != null) {
//            windowManager.addView(view, params);
//        }
    }

    private String readGirlsVideo() {

        ArrayList<Girl> girlsList = new ArrayList<>();
//         Read and parse the JSON file
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray girlsArray = jsonObject.getJSONArray("girls");

            // Iterate through the girls array
            for (int i = 0; i < girlsArray.length(); i++) {
                JSONObject girlObject = girlsArray.getJSONObject(i);

                // Create a Girl object and set its properties
                Girl girl = new Girl();
                girl.setName(girlObject.getString("name"));
                girl.setAge(girlObject.getInt("age"));
                girl.setVideoUrl(girlObject.getString("videoUrl"));
                girl.setCensored(girlObject.getBoolean("censored"));
                girl.setSeen(girlObject.getBoolean("seen"));
                girl.setLiked(girlObject.getBoolean("liked"));

                // Add the Girl object to the ArrayList
                if (MyApplication.userLoggedIAs.equals("Google")) {
                    girlsList.add(girl);
                } else {
                    if (girl.isCensored()) {
                        girlsList.add(girl);
                    }
                }

            }
            Collections.shuffle(girlsList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        int randomIndex = random.nextInt(girlsList.size());
        Girl randomgirl = girlsList.get(randomIndex);
        return randomgirl.getName();

    }




    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("girls_video.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


}
