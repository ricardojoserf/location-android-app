package com.example.fry.salvavidapp;

import android.support.annotation.NonNull;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.mail.MessagingException;
import android.net.Uri;


public class PantallaPrincipal extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    Hashtable<String, String> contacts_info = new Hashtable<String, String>();
    ArrayList<String> contacts_names = new ArrayList<String>();
    private final int CODE_PERMISSIONS_1 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET};
        checkPermissions(permissions, CODE_PERMISSIONS_1);
        getContacts();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, contacts_names);
        final AutoCompleteTextView text_name_contact = (AutoCompleteTextView) findViewById(R.id.editText3);
        text_name_contact.setAdapter(adapter);
        final EditText text_phone_contact = (EditText) findViewById(R.id.editText);
        text_name_contact .addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(s.length() != 0){
                    for(int i=0; i<contacts_info.size();i++){
                        if(text_name_contact.getText().toString().equals(contacts_names.get(i))) {
                            text_phone_contact.setText(contacts_info.get(contacts_names.get(i)));
                        }
                    }
                }
            }
        });
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mGoogleApiClient != null) {
                    //timerCoord();
                    showCoord();
                }
                else{
                    createGoogleApiClient();
                }
            }
        });
    }


    /*
        Check permissions
     */
    public void checkPermissions(String[] permissions, int permission_codes){
        for(int i=0;i<permissions.length;i++) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSIONS_1);
            }
        }
    }


    /*
        Funcitonality after granting permission(s)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CODE_PERMISSIONS_1:
            {
                getContacts();
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /*
        Creating Google Api Client
     */
    public void createGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    /*
        Timer every 5 seconds
     */
    public void timerCoord(){
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run(){
                showCoord();
            }
        };
        timer.scheduleAtFixedRate(t,0,10000);
    }


    /*
        Show coord. Send it in the future
     */
    public void showCoord(){
        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Get lat and long, get value of edit texts and send
        if (mLastLocation != null) {
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lon = String.valueOf(mLastLocation.getLongitude());
            // String location = "http://maps.google.com?q=" + lat + "," + lon;
            String location = lat + "," + lon;
            EditText messageBox = (EditText)findViewById(R.id.editText2);
            String message = messageBox.getText().toString();
            String final_message = message + "\nMy location: "+String.valueOf(location);
            EditText destinationBox = (EditText)findViewById(R.id.editText);
            String destination = destinationBox.getText().toString();
            EditText emailBox = (EditText)findViewById(R.id.editText4);
            String email = emailBox.getText().toString();

            // SMS
            CheckBox check_sms = (CheckBox) findViewById(R.id.checkBox);
            if (check_sms.isChecked()){
                check_sms.setChecked(false);
                sendSMSMessage(destination, final_message);
            }
            // E-mail
            CheckBox check_email = (CheckBox) findViewById(R.id.checkBox2);
            if (check_email.isChecked()){
                try {
                    sendEmailMessage(email, final_message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*
        Get phone contacts
     */
    public void getContacts(){
        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (! contacts_info.containsKey(name)) {
                contacts_info.put(name, phoneNumber);
                contacts_names.add(name);
            }
        }
        phones.close();
    }


    /*
        Send SMS
     */
    protected void sendSMSMessage(String destination, String message){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destination, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }


    /*
        Send e-mail
     */
    public void sendEmailMessage(String destination, String message) throws MessagingException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            new SendMailTask(this).execute("salvavidapp.mail", "qweqweqwe", Arrays.asList(destination), "Salvavidapp Message", message);
        }catch(Exception e){}
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        //timerCoord();
        showCoord();
    }


    @Override
    public void onConnectionSuspended(int i) {}


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


}
