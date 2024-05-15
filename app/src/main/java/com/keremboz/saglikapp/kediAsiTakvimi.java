package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class kediAsiTakvimi extends AppCompatActivity {

    FirebaseFirestore firestoreKediAsi;
    ArrayList<RadioGroup> AsiIlacRadioGroups;
    ArrayList<RadioGroup> AsiZamanRadioGroups;
    String firebaseEklenenAsi;
    ListenerRegistration asiListener;
    Button kaydetButton;
    TextView kediAsiTakvimText;

    Spinner spinnerGunKedi;
    Spinner spinnerZamanKedi;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kedi_asi_takvimi);

        AsiIlacRadioGroups = new ArrayList<>();
        AsiZamanRadioGroups = new ArrayList<>();
        kaydetButton = findViewById(R.id.kediAsiKaydetButton);
        kediAsiTakvimText = findViewById(R.id.kediAsiTakvimi);
        firebaseEklenenAsi = "";
        firestoreKediAsi = FirebaseFirestore.getInstance();

        spinnerGunKedi = findViewById(R.id.spinnerGunKedi);
        spinnerZamanKedi = findViewById(R.id.spinnerZamanKedi);

        String[] asilerKedi = {"İç-Dış Parazit", "Karma", "Lösemi", "Kuduz"};
        String[] asiZamanlarKedi =  {"6-8 Hafta", "8-10 Hafta", "9-11 Hafta"
                , "10-12 Hafta", "11-13 Hafta", "12-14 Hafta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, asilerKedi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGunKedi.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, asiZamanlarKedi);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZamanKedi.setAdapter(adapter1);

        //kullanıcı UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Firestore'dan daha önce eklenenleri çek ve textView'e yaz
        firestoreKediAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Kedi Aşılar").collection("Kedi Aşılar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder asilar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String asiAdi = document.getString("Asi");
                            String asiZaman = document.getString("Zaman");
                            asilar.append(asiAdi).append(" - ").append(asiZaman).append("\n");
                        }
                        kediAsiTakvimText.setText(asilar.toString());
                    } else {
                        kediAsiTakvimText.setText("Aşı bilgisi alınamadı.");
                    }
                });
        //Firestore'dan aşı bilgilerini anlık olarak dinle
        asiListener = firestoreKediAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Kedi Aşılar").collection("Kedi Aşılar")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        StringBuilder asilar = new StringBuilder();
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String asiAdi = dc.getDocument().getString("Aşı");
                            asilar.append(asiAdi).append("\n");
                        }
                        kediAsiTakvimText.setText(asilar.toString());
                    } else {
                        kediAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
                    }
                });
    }
    @Override
    protected  void onDestroy() {
        super.onDestroy();

        if(asiListener != null) {
            asiListener.remove();
        }
    }
    public void kediAsiKaydet (View view) {
        String secilenAsi = spinnerGunKedi.getSelectedItem().toString();
        String secilenZaman = spinnerZamanKedi.getSelectedItem().toString();

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // İlaç bilgisini Firestore'a kaydet
        Map<String, Object> eklenenAsi = new HashMap<>();
        eklenenAsi.put("Asi", secilenAsi);
        eklenenAsi.put("Zaman", secilenZaman);

        firestoreKediAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Kedi Aşılar").collection("Kedi Aşılar").add(eklenenAsi)
                .addOnSuccessListener(documentReference -> {
                    // Başarıyla eklendiğinde kullanıcıya geri bildirim ver
                    Toast.makeText(this, "Aşı başarıyla kaydedildi", Toast.LENGTH_SHORT).show();

                    // Aşıları tekrar yükle ve TextView'i güncelle
                    firestoreKediAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                            .document("Kedi Aşılar").collection("Kedi Aşılar")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    StringBuilder asilar = new StringBuilder();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String asiAdi = document.getString("Asi");
                                        String asiZaman = document.getString("Zaman");
                                        asilar.append(asiAdi).append(" - ").append(asiZaman).append("\n");
                                    }
                                    kediAsiTakvimText.setText(asilar.toString());
                                } else {
                                    kediAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Başarısız olduğunda kullanıcıya hata mesajı göster
                    Toast.makeText(this, "Aşı kaydedilirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}