package com.example.fry.salvavidapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
        final ArrayList<String> list_ids = new ArrayList<>();
        for(int i=0;i<list_lists.size();i++){
            list_ids.add(String.valueOf(list_lists.get(i).get(0)));
            list_names.add(String.valueOf(list_lists.get(i).get(1)));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list_names){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(20);
                textView.setTextColor(Color.parseColor("#000000"));
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_customized);
        spinner.setAdapter(adapter);


        // Button
        Button button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(SalvavidasWidget.widget_already_exists == false) {
                    final Context context = SalvavidasWidgetConfigureActivity.this;
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    Spinner mySpinner=(Spinner) findViewById(R.id.spinner);
                    String text = mySpinner.getSelectedItem().toString();
                    String id_ = "-1";
                    for(int k=0;k<list_names.size();k++){
                        if(list_names.get(k) == text){
                            id_ = list_ids.get(k);
                        }
                    }
                    SalvavidasWidget.setId(id_, String.valueOf(mAppWidgetId), context);
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });



        if(SalvavidasWidget.widget_already_exists == false) {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
                return;
            }
        }
        else{
            onlyOne(this);
            finish();
            return;
        }

    }

    public static void onlyOne(Context context){
        Toast.makeText(context, "No more widgets allowed, please delete one." , Toast.LENGTH_SHORT).show();
    }
}