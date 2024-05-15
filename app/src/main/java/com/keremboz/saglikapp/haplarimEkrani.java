package com.keremboz.saglikapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class haplarimEkrani extends AppCompatActivity {

    Button kaydetButton;
    Button kaldirButton;
    EditText ilacAdiEditText;
    TextView ilacListTextView;
    Spinner spinner1_1;
    Spinner spinner1_2;

    FirebaseFirestore firestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haplarim_ekrani);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        kaydetButton = findViewById(R.id.kaydetilac);
        kaldirButton = findViewById(R.id.kaldirilac);
        ilacAdiEditText = findViewById(R.id.ilacAdiEditText);
        ilacListTextView = findViewById(R.id.ilacListTextView);
        spinner1_1 = findViewById(R.id.spinner1_1);
        spinner1_2 = findViewById(R.id.spinner1_2);

        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};
        String[] vakitler = {"Sabah", "Öğlen", "Akşam"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gunler);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1_1.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vakitler);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1_2.setAdapter(adapter1);

        // Firestore'dan verileri çek ve TextView'e ekle
        firestore.collection("Kullanicilar").document(userId).collection("Haplar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder ilaclar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ilacAdi = document.getString("ilacAdi");
                            String gun = document.getString("gun");
                            String zaman = document.getString("zaman");
                            ilaclar.append("İlaç Adı: ").append(ilacAdi).append("- Gün: ").append(gun).append("- Zaman: ").append(zaman).append("\n");
                        }
                        ilacListTextView.setText(ilaclar.toString());
                    } else {
                        Toast.makeText(haplarimEkrani.this, "İlaç bilgileri alınamadı.", Toast.LENGTH_SHORT).show();
                    }
                });

        kaydetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ilacAdi = ilacAdiEditText.getText().toString();
                String secilenGun = spinner1_1.getSelectedItem().toString();
                String secilenZaman = spinner1_2.getSelectedItem().toString();

                if (!ilacAdi.isEmpty()) {
                    String yeniIlac = "İlaç Adı: " + ilacAdi + "- Gün: " + secilenGun + "- Zaman: " + secilenZaman + "\n";
                    String eskiIlaclar = ilacListTextView.getText().toString();
                    String yeniListe = eskiIlaclar + yeniIlac;
                    ilacListTextView.setText(yeniListe);
                    ilacAdiEditText.setText(""); // EditText'i temizle

                    // Firebase Firestore'a ilacı kaydet
                    Map<String, Object> ilac = new HashMap<>();
                    ilac.put("ilacAdi", ilacAdi);
                    ilac.put("gun", secilenGun);
                    ilac.put("zaman", secilenZaman);

                    firestore.collection("Kullanicilar").document(userId).collection("Haplar").add(ilac)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(haplarimEkrani.this, "İlaç başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(haplarimEkrani.this, "İlaç eklenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(haplarimEkrani.this, "İlaç adı boş olamaz.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        kaldirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Seçilen ilacı kaldırma işlemi burada
                // İlaç adını EditText'ten al
                String ilacAdi = ilacAdiEditText.getText().toString();
                // Eğer ilacAdi boşsa işlem yapma
                if (ilacAdi.isEmpty()) {
                    Toast.makeText(haplarimEkrani.this, "Lütfen bir ilaç adı girin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Seçilen gün ve zaman bilgilerini spinnerlardan al
                String secilenGun = spinner1_1.getSelectedItem().toString();
                String secilenZaman = spinner1_2.getSelectedItem().toString();

                // Firestore'dan ilacı ve seçilen gün ve zaman bilgilerini kaldır
                firestore.collection("Kullanicilar").document(userId).collection("Haplar")
                        .whereEqualTo("ilacAdi", ilacAdi)
                        .whereEqualTo("gun", secilenGun)
                        .whereEqualTo("zaman", secilenZaman)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete();
                            }
                            Toast.makeText(haplarimEkrani.this, "İlaç listenizden kaldırıldı.", Toast.LENGTH_SHORT).show();
                            // TextView'dan ilacı kaldır
                            String eskiIlaclar = ilacListTextView.getText().toString();
                            String ilacToRemove = "İlaç Adı: " + ilacAdi + "- Gün: " + secilenGun + "- Zaman: " + secilenZaman + "\n";
                            String yeniListe = eskiIlaclar.replace(ilacToRemove, "");
                            ilacListTextView.setText(yeniListe);
                        })
                        .addOnFailureListener(e -> {
                            // İlaç çıkarılamadı
                            Toast.makeText(haplarimEkrani.this, "İlaç çıkarılırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                        });

            }
        });
    }

}
