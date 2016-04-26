package com.udaykdungarwal.bodhi;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.widget.RelativeLayout;

public class AlertNotification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_navigation__drawer);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AlertNotification.this);

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
                drawer.setBackgroundColor(Color.parseColor("#FFA4E098"));
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