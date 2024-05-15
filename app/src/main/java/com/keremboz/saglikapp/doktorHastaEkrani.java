package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class doktorHastaEkrani extends AppCompatActivity {

    EditText hastaKimlikNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doktor_hasta_ekrani);

        hastaKimlikNumber = findViewById(R.id.hastaKimlikNumber);
    }
}