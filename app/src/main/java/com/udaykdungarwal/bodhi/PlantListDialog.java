package com.udaykdungarwal.bodhi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PlantListDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    private static final String TAG = "PlantList";
    private final boolean flag;

    public PlantListDialog(final Context context, final String regId, final String emailID, final String[] plants, final boolean flag) {
        super(context);
        this.flag = flag;
        /** Design the dialog in main.xml file */
        setContentView(R.layout.plant_list);
        this.setTitle("Select Plant");
        filterText = (EditText) findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) findViewById(R.id.List);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, plants);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {

                final String plant = list.getItemAtPosition(position).toString();
                if (flag){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to add the plant?");
                    builder.setCancelable(false);
                    builder.setTitle("Add Plant");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            storePlantinServer(context, plant, regId, emailID);
                            dismiss();
                            // Store Activity in Log Sharedprefs
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            SharedPreferences prefslog = context.getSharedPreferences("Log_Activity_file", Context.MODE_PRIVATE);
                            String past_log = prefslog.getString("log", "");
                            String updated_log = past_log + "\n" + plant + "        " + formattedDate + "\n" + "Added the plant";
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
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to remove the plant?");
                    builder.setCancelable(false);
                    builder.setTitle("Remove Plant");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removePlantinServer(context, list.getItemAtPosition(position).toString(), regId, emailID);
                            dismiss();
                            // Store Activity in Log Sharedprefs
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            SharedPreferences prefslog = context.getSharedPreferences("Log_Activity_file", Context.MODE_APPEND);
                            String past_log = prefslog.getString("log", "");
                            String updated_log = past_log + "\n" + plant + "        " + formattedDate + "\n" + "Removed the plant";
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
        });
    }

    @Override
    public void onClick(View v) {

    }
    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };
    @Override
    public void onStop(){
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    private void storePlantinServer(final Context applicationContext, String plant, String regId, String emailID) {
        // Make RESTful webservice call using AsyncHttpClient object
        final ProgressDialog prgDialog = new ProgressDialog(applicationContext);
        RequestParams params = new RequestParams();
        params.put("regId",regId);
        params.put("plant",plant);
        params.put("mail", emailID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_INSERT_PLANT_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(applicationContext,
                                "Plant added successfully with Web Server",
                                Toast.LENGTH_LONG).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void removePlantinServer(final Context applicationContext, String plant, String regId, String emailID) {
        // Make RESTful webservice call using AsyncHttpClient object
        final ProgressDialog prgDialog = new ProgressDialog(applicationContext);
        RequestParams params = new RequestParams();
        params.put("regId",regId);
        params.put("plant",plant);
        params.put("mail", emailID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_DELETE_PLANT_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(applicationContext,
                                "Plant deleted successfully with Web Server",
                                Toast.LENGTH_LONG).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}