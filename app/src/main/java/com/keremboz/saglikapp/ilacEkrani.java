package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ilacEkrani extends AppCompatActivity {

    Button ilacEkle;
    Button ilacGor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilac_ekrani);

        ilacEkle = findViewById(R.id.ilacEkleme);
        ilacGor = findViewById(R.id.ilacSorgulaButton);

    }

    public void alerjenIlacEkle(View view){
        Intent intent = new Intent(ilacEkrani.this, ilacEkleme.class);
        startActivity(intent);
    }

    public void alerjenIlacGor(View view){
        Intent intent = new Intent(ilacEkrani.this, ilacSorgulama.class);
        startActivity(intent);
    }

}