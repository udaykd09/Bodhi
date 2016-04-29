package com.udaykdungarwal.bodhi;

/**
 * Created by uday on 4/22/16.
 */
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView usertitleET, usernameND, mailIDND, activitylogTV;
    String email, regId, userName;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Email ID from Shared preferences
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        email = prefs.getString("eMailId", "");
        userName = prefs.getString("Name", "");
        regId = prefs.getString("regId", "");
        // Set Title
        usertitleET = (TextView) findViewById(R.id.usertitle);
        usertitleET.setText("Hello " + userName + " !");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context applicationContext = getApplicationContext();
                String[] plants = applicationContext.getResources().getStringArray(R.array.plants_array);
                PlantListDialog d = new PlantListDialog(HomeActivity.this, regId, email, plants, true);
                d.show();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Intent Message sent from Broadcast Receiver
        final String str = getIntent().getStringExtra("msg");

        // Check if Google Play Service is installed in Device
        // Play services is needed to handle GCM stuffs
//        if (!checkPlayServices()) {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "This device doesn't support Play services, App will not work normally",
//                    Toast.LENGTH_LONG).show();
//        }

        // When Message sent from Broadcast Receiver is not empty
        if (str != null) {
            if (str.contains("Response")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage(str);
                builder.setCancelable(false);
                builder.setTitle("Response");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (str.contains("Alert")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage(str);
                builder.setCancelable(false);
                builder.setTitle("Alert");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Soil moisture for '" + str + "' has reduced! Would you like Water it?");
                builder.setCancelable(false);
                builder.setTitle("Time to Water the Plant");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Store Activity in Log SharedPref
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(c.getTime());
                        SharedPreferences prefslog = getSharedPreferences("No log", Context.MODE_PRIVATE);
                        String past_log = prefslog.getString("log", "");
                        String updated_log = past_log + "\n" + str + "        " + formattedDate + "\n" + "Watered the plant";
                        SharedPreferences.Editor editor = prefslog.edit();
                        editor.putString("log", updated_log);
                        editor.commit();
                        System.out.println("**" + updated_log);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the negative button
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        }
    }

    // Check if Google Playservices is installed in Device or not
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(this);
//        // When Play services not found in device
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                // Show Error dialog to install Play services
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(
//                        getApplicationContext(),
//                        "This device doesn't support Play services, App will not work normally",
//                        Toast.LENGTH_LONG).show();
//                finish();
//            }
//            return false;
//        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "This device supports Play services, App will work normally",
//                    Toast.LENGTH_LONG).show();
//        }
//        return true;
//    }

    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        //checkPlayServices();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation__drawer, menu);
        usernameND = (TextView) findViewById(R.id.usernameView);
        usernameND.setText(userName);

        mailIDND = (TextView) findViewById(R.id.mailIDView);
        mailIDND.setText(email);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Context applicationContext = getApplicationContext();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.PlantsList) {
            // Open the list of Plant
            String[] plants = applicationContext.getResources().getStringArray(R.array.plants_array);
            PlantListDialog d = new PlantListDialog(HomeActivity.this, regId, email, plants, true);
            d.show();

        } else if (id == R.id.Activites) {
            SharedPreferences prefslog = getSharedPreferences("Log_Activity_file", Context.MODE_PRIVATE);
            String Full_log = prefslog.getString("log", "");
            LayoutInflater li = LayoutInflater.from(HomeActivity.this);
            LinearLayout someLayout = (LinearLayout) li.inflate(R.layout.plant_activity_log, null);
            System.out.println("**" + Full_log);
            activitylogTV = (TextView) someLayout.findViewById(R.id.ActivityLogView);
            activitylogTV.setText(Full_log);
            AlertDialog.Builder alert = new AlertDialog.Builder(someLayout.getContext());
            alert.setView(someLayout);
            alert.setTitle("Log");
            alert.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

        } else if (id == R.id.RemovePlantsList) {
            //Open Current List
            String[] plants = applicationContext.getResources().getStringArray(R.array.plants_array);
            PlantListDialog d = new PlantListDialog(HomeActivity.this, regId, email, plants, false);
            d.show();
        }

        return true;
    }

}