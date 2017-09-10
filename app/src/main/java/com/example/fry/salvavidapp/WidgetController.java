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



public class WidgetController extends AppWidgetProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient = null;
    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";
    Context context0;
    AppWidgetManager appWidgetManager;
    RemoteViews remoteViews;
    ComponentName watchWidget;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        watchWidget = new ComponentName(context, WidgetController.class);
        remoteViews.setOnClickPendingIntent(R.id.actionButton, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        context0 = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        watchWidget = new ComponentName(context, WidgetController.class);
        if (SYNC_CLICKED.equals(intent.getAction())) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
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
    public void send_widget(RemoteViews remoteViews, AppWidgetManager appWidgetManager, ComponentName watchWidget) throws MessagingException {
        String final_msg;
        Location mLastLocation;
        // Get location
        if (ActivityCompat.checkSelfPermission(context0, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context0, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            remoteViews.setTextViewText(R.id.textView, "No permission");
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Get lat and long, get value of edit texts and send
        if (mLastLocation != null) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lon = String.valueOf(mLastLocation.getLongitude());
            String location = "http://maps.google.com?q=" + lat + "," + lon;
            ArrayList<String> values = getValues(0);
            // EjemploDB db = new EjemploDB(context0);
            // ArrayList<String> values = db.get_by_id(0);
            if (values != null){
                String alarm_name = values.get(0);
                String final_message = values.get(1) + "\nMy location: "+String.valueOf(location);
                String phone_contact = values.get(2);
                String email = values.get(3);
                // String timer = values.get(4);
                if(!email.equals("")){
                    sendEmailMessage(email, final_message);
                }
                if(!phone_contact.equals("")){
                    sendSMSMessage(phone_contact, final_message);
                }
                remoteViews.setTextViewText(R.id.textView, (String.valueOf(alarm_name)) );
                appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            }
        } else {
            remoteViews.setTextViewText(R.id.textView, "FAIL");
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }


    /*
        Get values of an alarm given the id
     */
    public ArrayList<String> getValues(int id) {
        EjemploDB db = new EjemploDB(context0);
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
            Toast.makeText(context0, "Email sent.", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(context0, "Email not sent.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context0, "SMS sent.", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(context0, "SMS not sent.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            send_widget(remoteViews, appWidgetManager, watchWidget);
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
