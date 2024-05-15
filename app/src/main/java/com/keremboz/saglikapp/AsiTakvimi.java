package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AsiTakvimi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asi_takvimi);
    }
    public void bebekAsiTakvimiGit (View view){
        Intent intent = new Intent(AsiTakvimi.this, bebekAsiTakvimi.class);
        startActivity(intent);
    }
    public void kopekAsiTakvimiGit (View view){
        Intent intent = new Intent(AsiTakvimi.this, kopekAsiTakvimi.class);
        startActivity(intent);
    }
    public void kediAsiTakvimiGit (View view){
        Intent intent = new Intent(AsiTakvimi.this, kediAsiTakvimi.class);
        startActivity(intent);
    }
}