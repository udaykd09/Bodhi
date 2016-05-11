package com.udaykdungarwal.bodhi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
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

public class CityListDialog extends Dialog implements View.OnClickListener {

    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;

    public CityListDialog(final Context context, final String regId, final String emailID, final String[] cities) {
        super(context);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.plant_list);
        this.setTitle("Select City");
        filterText = (EditText) findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) findViewById(R.id.List);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, cities);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {

                final String city = list.getItemAtPosition(position).toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to add the city?");
                    builder.setCancelable(false);
                    builder.setTitle("Add City");
                    builder.setPositiveButton("Yes", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            storeCityinServer(context, city, regId, emailID);
                            dismiss();
                            // Store Activity in Log Sharedprefs
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            SharedPreferences prefslog = context.getSharedPreferences("Log_Activity_file", Context.MODE_PRIVATE);
                            String past_log = prefslog.getString("log", "");
                            String updated_log = past_log + "\n" + city + "        " + formattedDate + "\n" + "Updated the City";
                            SharedPreferences.Editor editor = prefslog.edit();
                            editor.putString("log", updated_log);
                            editor.commit();
                            SharedPreferences prefs = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = prefs.edit();
                            editor2.putString("citY", city);
                            editor2.commit();
                        }
                    });
                    builder.setNegativeButton("No", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something when click the negative button
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();
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

    private void storeCityinServer(final Context applicationContext, String city, String regId, String emailID) {
        // Make RESTful webservice call using AsyncHttpClient object
        final ProgressDialog prgDialog = new ProgressDialog(applicationContext);
        RequestParams params = new RequestParams();
        params.put("regId",regId);
        params.put("city", city);
        params.put("mail", emailID);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_UPDATE_CITY_URL, params,
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
                                "City added successfully with Web Server",
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