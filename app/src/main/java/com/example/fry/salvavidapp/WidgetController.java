package com.example.fry.salvavidapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

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
            final ArrayList<ArrayList<String>> list_lists = db.getall();

            final ArrayList<String> list_names = new ArrayList<>();
            final ArrayList<String> list_ids = new ArrayList<>();
            for(int j=0;j<list_lists.size();j++){
                list_names.add(String.valueOf(list_lists.get(j).get(1)));
                list_ids.add(String.valueOf(list_lists.get(j).get(0)));
            }
            String alarm_name = list_names.get(0);
            String alarm_id   = list_ids.get(0);

            PantallaPrincipal clasePrincipal = new PantallaPrincipal();
            Bundle b = new Bundle();
            for (int j=0;j<list_lists.size();j++){
                if (list_lists.get(j).get(0).equals(alarm_id)){
                    b.putString("id", alarm_id);
                    b.putString("name", String.valueOf(list_lists.get(j).get(1)));
                    b.putString("message", String.valueOf(list_lists.get(j).get(2)));
                    b.putString("sms", String.valueOf(list_lists.get(j).get(3)));
                    b.putString("email", String.valueOf(list_lists.get(j).get(4)));
                    b.putString("contacto", String.valueOf(list_lists.get(j).get(5)));
                    b.putString("timer", String.valueOf(list_lists.get(j).get(6)));
                    break;
                }
            }
            Toast.makeText(context, "aa", Toast.LENGTH_SHORT);
            // clasePrincipal.send();

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