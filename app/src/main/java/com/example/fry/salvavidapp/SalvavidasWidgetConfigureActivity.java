package com.example.fry.salvavidapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;


public class SalvavidasWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public SalvavidasWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.salvavidas_widget_configure);

        // Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        EjemploDB db = new EjemploDB(this);
        ArrayList<ArrayList<String>> list_lists = db.getall();
        final ArrayList<String> list_names = new ArrayList<>();
        for(int i=0;i<list_lists.size();i++){
            list_names.add(String.valueOf(list_lists.get(i).get(1)));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list_names);
        spinner.setAdapter(adapter);

        // Button
        Button button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Context context = SalvavidasWidgetConfigureActivity.this;
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                Spinner mySpinner=(Spinner) findViewById(R.id.spinner);
                String text = mySpinner.getSelectedItem().toString();
                SalvavidasWidget.updateAppWidget2(context, appWidgetManager, mAppWidgetId, text);
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }
}

