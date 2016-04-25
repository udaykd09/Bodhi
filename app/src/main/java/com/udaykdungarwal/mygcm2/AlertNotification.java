package com.udaykdungarwal.mygcm2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class AlertNotification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final RelativeLayout rHome = (RelativeLayout) findViewById(R.id.rHome);

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