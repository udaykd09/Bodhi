package com.udaykdungarwal.mygcm2;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
    TextView msgET, usertitleET;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Context applicationContext = getApplicationContext();
        // Intent Message sent from Broadcast Receiver
        String str = getIntent().getStringExtra("msg");

        // Get Email ID from Shared preferences
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String eMailId = prefs.getString("eMailId", "");
        String userName = prefs.getString("Name", "");
        // Set Title
        usertitleET = (TextView) findViewById(R.id.usertitle);

        // Check if Google Play Service is installed in Device
        // Play services is needed to handle GCM stuffs
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }

        usertitleET.setText("Hello " + userName + " !");
        // When Message sent from Broadcase Receiver is not empty
        if (str != null) {
            // Set the message
//            Intent i = new Intent(applicationContext, AlertNotification.class);
//            i.putExtra("Message", str);
//            startActivity(i);
//            finish();

            final RelativeLayout rHome = (RelativeLayout) findViewById(R.id.rHome);

            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

            // Set a message/question for alert dialog
            builder.setMessage("The Soil moisture has reduced! Would you like Water your Plant?");

            // Specify the dialog is not cancelable
            builder.setCancelable(false);

            // Set a title for alert dialog
            builder.setTitle("Time to Water the Plant");

            // Set the positive/yes button click click listener
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when click positive button
                    rHome.setBackgroundColor(Color.parseColor("#FFA4E098"));
                }
            });

            // Set the negative/no button click click listener
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

    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}
