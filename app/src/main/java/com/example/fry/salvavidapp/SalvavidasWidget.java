package com.example.fry.salvavidapp;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Arrays;
import javax.mail.MessagingException;

import static android.R.attr.id;
import static android.app.PendingIntent.getActivity;


public class SalvavidasWidget extends AppWidgetProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";
    Context context0;
    AppWidgetManager appWidgetManager;
    private static String id_ = "-1";
    static boolean widget_already_exists = false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.salvavidas_widget);
            Intent intent = new Intent(context, SalvavidasWidget.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, getPendingSelfIntent(context, SYNC_CLICKED));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    /*
        When button is clicked and much more actions
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        context0 = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.salvavidas_widget);
        ComponentName watchWidget = new ComponentName(context, SalvavidasWidget.class);
        if (SYNC_CLICKED.equals(intent.getAction())) {
            widget_already_exists = true;
            if (id_ != "-1") {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }
            else{
                try{
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                }catch(Exception e){
                    Toast.makeText(context, "Delete widget and create it again please.", Toast.LENGTH_SHORT).show();
                }

            }
        }
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(intent.getAction())) {
            widget_already_exists = false;
        }
    }


    /*
    Set id of the app
    */
    static void setId(String text, Context context){
        id_ = text;
        AlarmsDB db = new AlarmsDB(context);

        ArrayList<ArrayList<String>> list_lists = db.getall();
        String alarm_name = String.valueOf(list_lists.get(Integer.parseInt(id_)-1).get(1));
        String basic_msg = String.valueOf(list_lists.get(Integer.parseInt(id_)-1).get(2));
        String phone_contact = String.valueOf(list_lists.get(Integer.parseInt(id_)-1).get(3));
        String email = String.valueOf(list_lists.get(Integer.parseInt(id_)-1).get(4));
        String timer = String.valueOf(list_lists.get(Integer.parseInt(id_)-1).get(6));

        IdDB db_ids = new IdDB(context);
        db_ids.add_element(alarm_name, basic_msg, phone_contact, email, timer);
    }


    /*
        Intent for button
     */
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    /*
        Build google api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context0)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    /*
        Send sms or email
     */
    public void send_widget() throws MessagingException {
        Location mLastLocation;
        // Get location
        if (ActivityCompat.checkSelfPermission(context0, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context0, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context0, "No permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Get lat and long, get value of edit texts and send
        if (mLastLocation != null) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lon = String.valueOf(mLastLocation.getLongitude());
            String location = "http://maps.google.com?q=" + lat + "," + lon;
            ArrayList<String> values = null;
            if(id_ != "-1"){
                int id_int = Integer.valueOf(id_) - 1;
                values = getValues(id_int);
            }
            else{
                IdDB db_ids = new IdDB(context0);
                ArrayList<ArrayList<String>> all_ids =db_ids.getall();
                values = all_ids.get(all_ids.size()-1);
            }
            if (values != null){
                String alarm_name = values.get(0);
                Toast.makeText(context0, ("Sending alarm " + String.valueOf(alarm_name) ), Toast.LENGTH_SHORT).show();
                String final_message = values.get(1) + "\nMy location: "+String.valueOf(location);
                String phone_contact = values.get(2);
                String email = values.get(3);
                String timer = values.get(4);
                int jj = 0;
                while(jj++ < 10){
                    try {
                        if(!email.equals("")){
                            sendEmailMessage(email, final_message);
                        }
                        if(!phone_contact.equals("")){
                            sendSMSMessage(phone_contact, final_message);
                        }
                        int ms = (int) (60000*Double.valueOf(timer));
                        if(ms<1000){ms=1000;}
                        Thread.sleep(ms);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /*
        Get values of an alarm given the id
     */
    public ArrayList<String> getValues(int id) {
        AlarmsDB db = new AlarmsDB(context0);
        ArrayList<ArrayList<String>> list_lists = db.getall();
        try{
            String alarm_name = String.valueOf(list_lists.get(id).get(1));
            String basic_msg = String.valueOf(list_lists.get(id).get(2));
            String phone_contact = String.valueOf(list_lists.get(id).get(3));
            String email = String.valueOf(list_lists.get(id).get(4));
            String timer = String.valueOf(list_lists.get(id).get(6));
            ArrayList<String> vals = new ArrayList<>();
            vals.add(alarm_name);
            vals.add(basic_msg);
            vals.add(phone_contact);
            vals.add(email);
            vals.add(timer);
            return vals;
        }catch(Exception e){
            return null;
        }
    }


    /*
    Get values of an alarm given the id
 */
    public static ArrayList<String> getValues2(Context context, int id) {
        AlarmsDB db = new AlarmsDB(context);
        ArrayList<ArrayList<String>> list_lists = db.getall();
        try{
            String alarm_name = String.valueOf(list_lists.get(id).get(1));
            String basic_msg = String.valueOf(list_lists.get(id).get(2));
            String phone_contact = String.valueOf(list_lists.get(id).get(3));
            String email = String.valueOf(list_lists.get(id).get(4));
            String timer = String.valueOf(list_lists.get(id).get(6));
            ArrayList<String> vals = new ArrayList<>();
            vals.add(alarm_name);
            vals.add(basic_msg);
            vals.add(phone_contact);
            vals.add(email);
            vals.add(timer);
            return vals;
        }catch(Exception e){
            return null;
        }
    }


    /*
        Send e-mail
     */
    public void sendEmailMessage(String destination, String message) throws MessagingException {
        if (ActivityCompat.checkSelfPermission(context0, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            new SendMailTask().execute("salvavidapp.mail", "qweqweqwe", Arrays.asList(destination), "Salvavidapp Message", message);
            Toast.makeText(context0, "Email sent.", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(context0, "Email not sent.", Toast.LENGTH_SHORT).show();
        }
    }


    /*
        Send SMS
     */
    protected void sendSMSMessage(String destination, String message){
        if (ActivityCompat.checkSelfPermission(context0, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(destination, null, message, null, null);
            Toast.makeText(context0, "SMS sent.", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(context0, "SMS not sent.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            send_widget();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


}
