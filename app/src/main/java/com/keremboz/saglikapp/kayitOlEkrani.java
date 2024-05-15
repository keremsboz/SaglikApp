package com.keremboz.saglikapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class kayitOlEkrani extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    EditText kayitNameText, kayitKimlikNumber, kayitEmailText, kayitPasswordNumber;
    CheckBox checkBoxErkek, checkBoxKadin;
    String kayitNameString, kayitKimlikNumberString, kayitEmailTextString, kayitPasswordNumberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ekrani);

        kayitNameText = findViewById(R.id.kayitNameText);
        kayitKimlikNumber = findViewById(R.id.kayitKimlikNumber);
        kayitEmailText = findViewById(R.id.kayitEmailText);
        kayitPasswordNumber = findViewById(R.id.kayitPasswordNumber);
        checkBoxErkek = findViewById(R.id.checkBoxErkek);
        checkBoxKadin = findViewById(R.id.checkBoxKadin);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        kayitNameString = "";
        kayitKimlikNumberString = "";
        kayitEmailTextString = "";
        kayitPasswordNumberString = "";
    }
    public void onClick(View v) {
        if (checkBoxErkek.isChecked() && checkBoxKadin.isChecked()) {
            checkBoxKadin.setChecked(false);
            checkBoxErkek.setChecked(false);
        }
    }

    public void kayitOl(View view) {
        kayitNameString = kayitNameText.getText().toString().trim();
        kayitKimlikNumberString = kayitKimlikNumber.getText().toString().trim();
        kayitEmailTextString = kayitEmailText.getText().toString().trim();
        kayitPasswordNumberString = kayitPasswordNumber.getText().toString().trim();

        if (checkBoxErkek.isChecked() || checkBoxKadin.isChecked()) {
            mAuth.createUserWithEmailAndPassword(kayitEmailTextString, kayitPasswordNumberString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Kullanıcı başarıyla kaydedildi, Firestore'a diğer bilgileri ekle
                                String userId = mAuth.getCurrentUser().getUid();
                                Map<String, Object> userNames = new HashMap<>();
                                userNames.put("Ad", kayitNameString);
                                userNames.put("TC Kimlik", kayitKimlikNumberString);
                                userNames.put("Email", kayitEmailTextString);
                                userNames.put("Hasta Cinsiyet", checkBoxErkek.isChecked() ? "Erkek" : "Kadın");

                                firestore.collection("Kullanicilar").document(userId)
                                        .set(userNames)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(kayitOlEkrani.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(kayitOlEkrani.this, girisYapEkrani.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(kayitOlEkrani.this, "Firestore'a bilgiler eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(kayitOlEkrani.this, "Kayıt sırasında bir hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Kayıt sırasında bir hata oluştu", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Cinsiyet Seçimi Yapınız", Toast.LENGTH_LONG).show();
        }
    }
}




    /*public void kayitOl(View view){
        kayitEmailTextString = kayitEmailText.getText().toString().trim();
        kayitPasswordNumberString = kayitPasswordNumber.getText().toString().trim();
        kayitNameString = kayitNameText.getText().toString().trim();
        kayitKimlikNumberString = kayitKimlikNumber.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(kayitEmailTextString, kayitPasswordNumberString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Kullanıcı başarıyla kaydedildi, Firestore'a diğer bilgileri ekle
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> userNames = new HashMap<>();
                            userNames.put("Ad", kayitNameString);
                            userNames.put("TC Kimlik", kayitKimlikNumberString);
                            if (checkBoxErkekB && !checkBoxKadinB){
                                userNames.put("Hasta Cinsiyet","Erkek");
                            } else if (checkBoxKadinB && !checkBoxErkekB) {
                                userNames.put("Hasta Cinsiyet","Kadın");
                            } else {
                                checkBoxErkekB = false;
                                checkBoxKadinB = false;
                                Toast.makeText(getApplicationContext(),"Boş Alanları Doldurunuz",Toast.LENGTH_LONG).show();
                            }


                            firestore.collection("Hasta Kayıt").document(userId)
                                    .set(userNames)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(kayitOlEkrani.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(kayitOlEkrani.this, "Firestore'a bilgiler eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(kayitOlEkrani.this, "Kayıt sırasında bir hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*Map<String, Object> userNames = new HashMap<>();
        userNames.put("Hasta Adı", kayitNameString);
        userNames.put("Hasta TC",kayitKimlikNumberString);


        firestore.collection("Hasta Kayıt").add(mAuth);

        //Intent intent = new Intent(kayitOlEkrani.this,girisYapEkrani.class);
        //startActivity(intent);
    }
    */
/*
    public void onClick(View v) {
        if (checkBoxErkek.isChecked() && checkBoxKadin.isChecked()) {
            checkBoxKadin.setChecked(false);
            checkBoxErkek.setChecked(false);
        }
        checkBoxErkekB = checkBoxErkek.isChecked();
        checkBoxKadinB = checkBoxKadin.isChecked();
    }
}
*/