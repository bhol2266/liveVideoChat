package com.bhola.livevideochat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private int CAMERA_PERMISSION_REQUEST_CODE = 123;
    final int NOTIFICATION_REQUEST_CODE = 112;
    public static TextView badge_text;
    public static int unreadMessage_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startVideoBtn = findViewById(R.id.startVideoBtn);
        startVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    startActivity(new Intent(MainActivity.this, CameraActivity.class));
                }
            }
        });

        initializeBottonFragments();
        askForNotificationPermission(); //Android 13 and higher

    }

    private void initializeBottonFragments() {
        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setAdapter(new PagerAdapter(MainActivity.this));
        TabLayout tabLayout = findViewById(R.id.tabLayout);


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.videocall);

                        View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.videocall);
                        tab.setCustomView(view1);

                        //By default tab 0 will be selected to change the tint of that tab
                        View tabView = tab.getCustomView();
                        ImageView tabIcon = tabView.findViewById(R.id.icon);
                        tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.themeColor));
                        break;
                    case 1:
                        tab.setIcon(R.drawable.chat);


                        View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.chat);
                        tab.setCustomView(view2);
                        unreadMessage_count = getUndreadMessage_Count();

                        badge_text = view2.findViewById(R.id.badge_text);
                        badge_text.setVisibility(View.GONE);

                        if (!Fragment_Messenger.retreive_sharedPreferences(MainActivity.this)) {
                            //logged in first time
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //First time
//                                        badge_text.setVisibility(View.VISIBLE);
//                                        badge_text.setText("1");
//                                        badge_text.setBackgroundResource(R.drawable.badge_background);
//                                        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.message_received);
//                                        mediaPlayer.start();

                                }
                            }, 3000);

                        } else {
                            if (unreadMessage_count != 0) {
                                badge_text.setVisibility(View.VISIBLE);
                                badge_text.setText(String.valueOf(unreadMessage_count));
                                badge_text.setBackgroundResource(R.drawable.badge_background);

                            } else {
                                badge_text.setVisibility(View.GONE);
                            }
                        }

                        break;


                    case 2:
                        tab.setIcon(R.drawable.info_2);


                        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.info_2);
                        tab.setCustomView(view3);
                        break;
                    default:
                        tab.setIcon(R.drawable.user2);
                        View view4 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.user2);
                        tab.setCustomView(view4);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the custom view of the selected tab
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    // Find the ImageView in the custom view
                    ImageView tabIcon = tabView.findViewById(R.id.icon);

                    // Set the background tint color for the selected tab
                    tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.themeColor));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Get the custom view of the unselected tab
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    // Find the ImageView in the custom view
                    ImageView tabIcon = tabView.findViewById(R.id.icon);

                    // Set the background tint color for the unselected tab
                    tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, com.google.android.ads.mediationtestsuite.R.color.gmts_light_gray));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab reselected, no action needed
            }
        });
    }

    private int getUndreadMessage_Count() {
        SharedPreferences sharedPreferences = getSharedPreferences("messenger_chats", MainActivity.this.MODE_PRIVATE);
        int count = sharedPreferences.getInt("unreadMessage_Count", 0);
        return count;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera setup
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            } else {
                // Camera permission denied, handle it gracefully (e.g., display a message or disable camera functionality)
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == NOTIFICATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
            } else {
                // Explain to the user that the feature is unavailable because
                // the feature requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
            }
        }
    }

    private void askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Allow Notification for Daily new Stories ", Toast.LENGTH_LONG).show();
                    }
                }, 1000);
            }
        }
    }


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

}