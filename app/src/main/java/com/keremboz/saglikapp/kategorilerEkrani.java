package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class kategorilerEkrani extends AppCompatActivity {

    Button Ilaclarim;
    Button haplarim;
    Button asiTakvimi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategoriler_ekrani);

        Ilaclarim = findViewById(R.id.AlerjenIlaclarim);
        haplarim = findViewById(R.id.hapBildirim);
        asiTakvimi = findViewById(R.id.asiTakvimi);
    }

    public void alerjenlerGit (View view){
        Intent intent = new Intent(kategorilerEkrani.this, ilacEkrani.class);
        startActivity(intent);
    }
    public void hapBildirimGit (View view){
        Intent intent = new Intent(kategorilerEkrani.this, haplarimEkrani.class);
        startActivity(intent);
    }
    public void asiTakvimiGit (View view){
        Intent intent = new Intent(kategorilerEkrani.this, AsiTakvimi.class);
        startActivity(intent);

    }
}