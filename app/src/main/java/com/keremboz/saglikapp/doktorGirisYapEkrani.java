package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class doktorGirisYapEkrani extends AppCompatActivity {

    String userName;
    TextView mainTitle5;
    Button girisYapButton;
    EditText doktorNameText;
    EditText doktorPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doktor_giris_yap_ekrani);

        mainTitle5 = findViewById(R.id.mainTitle5);
        doktorNameText = findViewById(R.id.doktorNameText);
        doktorPasswordText = findViewById(R.id.doktorPasswordText);
        girisYapButton = findViewById(R.id.doktorStartButton);

        userName = "";
    }
    public void doktorGirisYap (View view){
        userName = doktorNameText.getText().toString();
        String password = doktorPasswordText.getText().toString();

        if(checkCredentials(userName, password)){
            Intent intent = new Intent(doktorGirisYapEkrani.this, doktorHastaEkrani.class);
            intent.putExtra("userInput", userName);
            startActivity(intent);
        }else {

        }
    }
    private boolean checkCredentials(String userName, String password) {
        return "5454".equals(userName) && "5454".equals(password);
    }
}