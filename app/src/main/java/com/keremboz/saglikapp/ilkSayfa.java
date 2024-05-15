package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ilkSayfa extends AppCompatActivity {

    // Button doktorGiris;
    Button girisEkrani;
    Button kayitOl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilk_sayfa);

        // doktorGiris = findViewById(R.id.doktorGiris);
        girisEkrani = findViewById(R.id.girisYap);
        kayitOl = findViewById(R.id.kayıtOl);
    }

    public void girisYap (View view){
        Intent intent = new Intent(ilkSayfa.this, girisYapEkrani.class);
        startActivity(intent);
    }
    public void kayitOl (View view){
        Intent intent = new Intent(ilkSayfa.this, kayitOlEkrani.class);
        startActivity(intent);
    }
    /*
    public void doktorGiris (View view){
        Intent intent = new Intent(ilkSayfa.this, doktorGirisYapEkrani.class);
        startActivity(intent);
    }

     */
}