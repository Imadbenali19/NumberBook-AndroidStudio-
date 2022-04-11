package com.example.numberbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void contact(View view) {
        startActivity(new Intent(MainActivity2.this,MainActivity.class));
    }

    public void search(View view) {
        startActivity(new Intent(MainActivity2.this,MainActivity3.class));
    }
}