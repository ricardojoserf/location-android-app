package com.example.fry.salvavidapp;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.mail.MessagingException;
import static com.example.fry.salvavidapp.R.string.minutes;

/**
 * Created by Fry on 08-Sep-17.
 */

public class WidgetController extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            EjemploDB db = new EjemploDB(context);
            String alarm_name = "";
            try{
                final ArrayList<ArrayList<String>> list_lists = db.getall();
                final ArrayList<String> list_names = new ArrayList<>();
                final ArrayList<String> list_ids = new ArrayList<>();
                for (int j = 0; j < list_lists.size(); j++) {
                    list_names.add(String.valueOf(list_lists.get(j).get(1)));
                    list_ids.add(String.valueOf(list_lists.get(j).get(0)));
                }
                alarm_name = list_names.get(0);
                String alarm_id = list_ids.get(0);
                String basic_msg = "", phone_contact = "", email = "", timer = "";
                for (int j = 0; j < list_lists.size(); j++) {
                    if (list_lists.get(j).get(0).equals(alarm_id)) {
                        basic_msg = String.valueOf(list_lists.get(j).get(2));
                        phone_contact = String.valueOf(list_lists.get(j).get(3));
                        email = String.valueOf(list_lists.get(j).get(4));
                        timer = String.valueOf(list_lists.get(j).get(6));
                        break;
                    }
                }
                if(!basic_msg.equals("") && !phone_contact.equals("")){
                    SmsManager smsManager = SmsManager.getDefault();
                    ///// smsManager.sendTextMessage(phone_contact, null, basic_msg, null, null);
                }
            }catch(Exception e){
                alarm_name="No alarms";
            }
             /*
            PantallaPrincipal clasePrincipal = new PantallaPrincipal();
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            String final_msg = clasePrincipal.get_final_message(basic_msg, mLastLocation);
            // clasePrincipal.sendSMSMessage(phone_contact, final_msg);
            try {
                clasePrincipal.sendEmailMessage(email, final_msg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            */
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
            remoteViews.setTextViewText(R.id.textView, alarm_name);
            Intent intent = new Intent(context, WidgetController.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

    }

}
