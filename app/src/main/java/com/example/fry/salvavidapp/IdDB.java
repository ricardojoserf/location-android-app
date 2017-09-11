package com.example.fry.salvavidapp;

/**
 * Created by Fry on 11-Sep-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class IdDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ejemplo2.db";
    public static final String TABLA_IDS = "tabla_ids";
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_MENSAJE = "mensaje";
    public static final String COLUMNA_SMS= "sms";
    public static final String COLUMNA_EMAIL = "email";
    public static final String COLUMNA_TIMER = "timer";


    private static final String SQL_CREAR_ID = "create table " + TABLA_IDS + "("
            + COLUMNA_ID + " integer primary key autoincrement, " + COLUMNA_NOMBRE + " text, " + COLUMNA_MENSAJE + " text, " + COLUMNA_SMS + " text, "
            + COLUMNA_EMAIL + " text, " +  COLUMNA_TIMER + " text);";

    public IdDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREAR_ID); }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }




    /*
        Add element to DB
     */
    public int add_element(String nombre, String mensaje, String phone, String email, String timer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE, nombre);
        values.put(COLUMNA_MENSAJE, mensaje);
        values.put(COLUMNA_SMS, phone);
        values.put(COLUMNA_EMAIL, email);
        values.put(COLUMNA_TIMER, timer);
        long newRowId = db.insert(TABLA_IDS, null,values);;
        db.close();
        return (int) newRowId;
    }


    /*
        Get all elements
     */
    public ArrayList<ArrayList<String>> getall(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_NOMBRE, COLUMNA_MENSAJE, COLUMNA_SMS, COLUMNA_EMAIL, COLUMNA_TIMER};
        Cursor cursor = db.query(TABLA_IDS, projection, null, null, null, null, null, null);
        ArrayList<ArrayList<String>> a_l_l = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                ArrayList<String> a_l = new ArrayList<>();
                for (int i=0;i<(projection.length);i++){
                    a_l.add(cursor.getString(i));
                }
                a_l_l.add(a_l);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return a_l_l;
    }


}

