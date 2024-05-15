package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class girisYapEkrani extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userName;
    TextView mainTitle;
    Button startButton;
    EditText mainNameText;
    EditText mainPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_ekrani);

        mainTitle = findViewById(R.id.mainTitle);
        mainNameText = findViewById(R.id.mainNameText);
        mainPasswordText = findViewById(R.id.mainPasswordText);
        startButton = findViewById(R.id.mainStartButton);
        mAuth = FirebaseAuth.getInstance();

        userName = "";
    }

    public void hastaGirisYap(View view) {
        userName = mainNameText.getText().toString().trim();
        String password = mainPasswordText.getText().toString().trim();

        if (!userName.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Giriş başarılı, diğer ekrana geç
                            Intent intent = new Intent(girisYapEkrani.this, kategorilerEkrani.class);
                            intent.putExtra("userInput", user.getEmail());
                            startActivity(intent);
                        } else {
                            // Giriş başarısız, kullanıcıyı uyar
                            Toast.makeText(girisYapEkrani.this, "Giriş başarısız! Kullanıcı adı veya şifre hatalı.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Boş alan uyarısı
            Toast.makeText(girisYapEkrani.this, "Lütfen kullanıcı adı ve şifreyi girin.", Toast.LENGTH_SHORT).show();
        }
    }
}


/*
package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class girisYapEkrani extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userName;
    TextView mainTitle;
    Button startButton;
    EditText mainNameText;
    EditText mainPasswordtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_ekrani);

        mainTitle = findViewById(R.id.mainTitle);
        mainNameText = findViewById(R.id.mainNameText);
        mainPasswordtext = findViewById(R.id.mainPasswordText);
        startButton = findViewById(R.id.mainStartButton);
        mAuth = FirebaseAuth.getInstance();

        userName = "";
    }
    public void hastaGirisYap(View view) {
        userName = mainNameText.getText().toString();
        String password = mainPasswordtext.getText().toString();

        // Basit bir şifre kontrolü
        if (checkCredentials(userName, password)) {
            // Kullanıcı adı ve şifre doğru, diğer ekrana geç
            Intent intent = new Intent(girisYapEkrani.this, kategorilerEkrani.class);
            intent.putExtra("userInput", userName);
            startActivity(intent);
        } else {
            // Kullanıcı adı veya şifre yanlış, kullanıcıyı uyar
            // Örneğin, bir Toast mesajı gösterebilirsiniz.
        }
    }
    private boolean checkCredentials(String username, String password) {
        // Burada gerçek bir veritabanı kontrolü yapılmalıdır.
        // Bu örnek sadece basit bir örnek amaçlıdır.
        return "kerem".equals(username) && "kerem".equals(password);
    }
}

 */