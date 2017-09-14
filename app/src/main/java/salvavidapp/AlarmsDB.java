package salvavidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class AlarmsDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ejemplo.db";
    public static final String TABLA_NOMBRES = "nombres";
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_MENSAJE = "mensaje";
    public static final String COLUMNA_SMS= "sms";
    public static final String COLUMNA_EMAIL = "email";
    public static final String COLUMNA_CONTACTO = "contacto";
    public static final String COLUMNA_TIMER = "timer";


    private static final String SQL_CREAR_ALARM = "create table " + TABLA_NOMBRES + "("
            + COLUMNA_ID + " integer primary key autoincrement, " + COLUMNA_NOMBRE + " text, " + COLUMNA_MENSAJE + " text, " + COLUMNA_SMS + " text, "
            + COLUMNA_CONTACTO  + " text, "+  COLUMNA_EMAIL + " text, "
            +  COLUMNA_TIMER + " text);";

    public AlarmsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREAR_ALARM); }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /*
        Add element to DB
     */
    public int add_element(String nombre, String mensaje, String phone, String email, String contacto, String timer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE, nombre);
        values.put(COLUMNA_MENSAJE, mensaje);
        values.put(COLUMNA_SMS, phone);
        values.put(COLUMNA_EMAIL, email);
        values.put(COLUMNA_CONTACTO, contacto);
        values.put(COLUMNA_TIMER, timer);
        long newRowId = db.insert(TABLA_NOMBRES, null,values);;
        db.close();
        return (int) newRowId;
    }

    /*
        Get element by id
     */
    public ArrayList<String> get_by_id(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_ID, COLUMNA_NOMBRE, COLUMNA_MENSAJE, COLUMNA_SMS, COLUMNA_EMAIL, COLUMNA_CONTACTO, COLUMNA_TIMER};
        ArrayList<String> a_l = new ArrayList<>();
        Cursor cursor = db.query(TABLA_NOMBRES, projection, " _id = ?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.moveToFirst()){
            do{
                for (int i=0;i<(projection.length);i++){
                    a_l.add(cursor.getString(i));
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return a_l;
    }

    /*
        Get all elements
     */
    public ArrayList<ArrayList<String>> getall(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMNA_ID, COLUMNA_NOMBRE, COLUMNA_MENSAJE, COLUMNA_SMS, COLUMNA_EMAIL, COLUMNA_CONTACTO, COLUMNA_TIMER};
        Cursor cursor = db.query(TABLA_NOMBRES, projection, null, null, null, null, null, null);
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


    /*
        Delete element from DB
     */
    public boolean eliminar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLA_NOMBRES, " _id = ?", new String[] { String.valueOf (id ) });
            db.close();
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    /*
        Update element in DB
     */
    public void update_element (int id, String nombre, String mensaje, String phone, String email, String contacto, String timer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre",nombre);
        values.put("mensaje",mensaje);
        values.put("sms",phone);
        values.put("email",email);
        values.put("contacto",contacto);
        values.put("timer",timer);
        int i = db.update(TABLA_NOMBRES, values, " _id = ?", new String[] { String.valueOf( id ) });
        db.close();
    }


}
