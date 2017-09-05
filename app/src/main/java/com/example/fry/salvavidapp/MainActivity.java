package com.example.fry.salvavidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Fry on 05-Sep-17.
 */

public class MainActivity extends Activity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.lista);

            final ListView listview = (ListView) findViewById(R.id.listview);
            EjemploDB db = new EjemploDB( getApplicationContext() );
            final ArrayList<ArrayList<String>> list_lists = db.getall();
            final ArrayList<String> list_names = new ArrayList<>();
            final ArrayList<String> list_ids = new ArrayList<>();
            for(int i=0;i<list_lists.size();i++){
                list_names.add(String.valueOf(list_lists.get(i).get(0)+" , "+list_lists.get(i).get(1)));
                list_ids.add(String.valueOf(list_lists.get(i).get(0)));
            }
            final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list_names);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    final String item = (String) parent.getItemAtPosition(position);
                    // Toast.makeText(getApplicationContext(), "id: "  + id      , Toast.LENGTH_LONG).show();
                    /*
                    EjemploDB db = new EjemploDB( getApplicationContext() );
                    db.eliminar((int) id);
                    db.close();
                    */

                    changeView(list_ids, position, list_lists);
                }
            });
        }

    public void changeView(final ArrayList<String> list_ids, int position, ArrayList<ArrayList<String>> list_lists){
        Intent myIntent = new Intent(this, PantallaPrincipal.class);
        Bundle b = new Bundle();
        String real_id = list_ids.get(position);
        for (int j=0;j<list_lists.size();j++){
            if (list_lists.get(j).get(0).equals(real_id)){
                b.putString("id", real_id);
                b.putString("name", String.valueOf(list_lists.get(j).get(1)));
                b.putString("message", String.valueOf(list_lists.get(j).get(2)));
                b.putString("sms", String.valueOf(list_lists.get(j).get(3)));
                b.putString("email", String.valueOf(list_lists.get(j).get(4)));
                myIntent.putExtras(b);
                startActivity(myIntent);
                finish();
                break;
            }
        }

    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}