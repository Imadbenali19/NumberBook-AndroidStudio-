package com.example.numberbook;

import static com.example.numberbook.R.drawable.ic_baseline_call_24;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.ContactModel;


public class MainActivity3 extends AppCompatActivity {
    final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "Trace response : ";
    private static final String ErroTAG = "Trace Error : ";
    RequestQueue requestQueue;
    String url = "http://10.0.2.2:8081/contacts";
    //String url = "http://192.168.181.167:8081/contacts";

    Button searchbtn;
    EditText numberPhone;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        numberPhone = findViewById(R.id.editTextPhone);
        searchbtn = findViewById(R.id.btnSearch);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);



        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("first_time", true)) {
            Log.d("Comments", "First time");
            List<HashMap<String,String>> contacts = getContacts(this.getContentResolver());

            saveContacts(contacts);
           settings.edit().putBoolean("first_time", false).commit();
        }
        //Toast.makeText(this, contacts.toString(), Toast.LENGTH_LONG).show();
    }

    private void saveContacts(List<HashMap<String, String>> contacts) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                url+"/addBulk", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response: ", "Contacts added succesfully");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorResponse: ", "Error trying to add contacts");
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Gson gson = new Gson();
                try {
                    return gson.toJson(contacts).toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        requestQueue.add(request);
    }

    private List<HashMap<String, String>> getContacts(ContentResolver contentResolver) {
        List<HashMap<String,String>> contactsList = new ArrayList();


        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {

            HashMap<String,String> contact = new HashMap<>();

            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contact.put("name",name);
            contact.put("number",phoneNumber);
            contact.put("country","+212");

            contactsList.add(contact);
        }
        phones.close();

        return contactsList;
    }

    public void search(View view) {

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET,
                url+"/"+ccp.getSelectedCountryCodeWithPlus()+"/"+numberPhone.getText(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("response: ", "Contact Found successfully! ");

                Type type = new TypeToken<ContactModel>(){}.getType();
                ContactModel contact = new Gson().fromJson(response, type);
                if(contact != null){
                    //Toast.makeText(getApplicationContext(), contact.getName()+" country code: "+contact.getCountry(), Toast.LENGTH_LONG).show();
                    Log.d("Contact: ", contact.toString());



                    AlertDialog dialog = new AlertDialog.Builder(MainActivity3.this)
                            .setTitle("Number: ("+contact.getCountry()+") "+numberPhone.getText())
                            .setMessage("Name: "+contact.getName())
                            .setPositiveButton("Appel !", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent appel = new Intent(Intent.ACTION_DIAL, Uri.parse(("tel:" + numberPhone.getText())));
                                    startActivity(appel);
                                }
                            })
                            .setNegativeButton("Ok", null)
                            .setNeutralButton("Message !", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent msg = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+numberPhone.getText()));
                                    startActivity(msg);
                                }
                            })
                            .create();
                    dialog.show();


                }else{
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity3.this)
                            .setTitle("Contact not found !")
                            .setMessage("Try again ")
                            .setNegativeButton("Ok", null)
                            .create();
                    dialog.show();
                    //Toast.makeText(getApplicationContext(), "No contact with this number exists", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorResponse: ", "Error trying to get contact");
            }
        });
        requestQueue.add(request);
        /*
        List<HashMap<String,String>> contacts = getContacts(this.getContentResolver());
        Toast.makeText(this,contacts.toString(),Toast.LENGTH_LONG).show();
        saveContacts();*/
    }

}