package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;


public class bebekAsiTakvimi extends AppCompatActivity {

    FirebaseFirestore firestoreBebekAsi;
    ArrayList<RadioGroup> AsiIlacRadioGroups;
    ArrayList<RadioGroup> AsiZamanRadioGroups;
    String firebaseEklenenAsi;
    ListenerRegistration asiListener;
    Button kaydetButton;
    TextView bebekAsiTakvimText;

    Spinner spinnerGunBebek;
    Spinner spinnerZamanBebek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bebek_asi_takvimi);

        AsiIlacRadioGroups = new ArrayList<>();
        AsiZamanRadioGroups = new ArrayList<>();
        kaydetButton = findViewById(R.id.bebekAsiKaydetButton);
        bebekAsiTakvimText = findViewById(R.id.bebekAsiTakvimi);
        firebaseEklenenAsi = "";
        firestoreBebekAsi = FirebaseFirestore.getInstance();


         spinnerGunBebek = findViewById(R.id.spinnerGunBebek);
         spinnerZamanBebek = findViewById(R.id.spinnerZamanBebek);
        String[] asilarBebek = {"Hepatit B", "BCG (Verem)", "DaBT-İPA-Hib"
                , "KPA*", "KKK", "DaBT-İPA", "OPA", "Td", "Hepatit A"
                , "Su çiçeği"};
        String[] asiZamanlarBebek = {"Doğumda", "1.Ay Sonu", "2.Ay Sonu"
                , "4.Ay Sonu", "6.Ay Sonu", "9.Ay Sonu", "12.Ay Sonu"
                , "18.Ay Sonu", "24.Ay Sonu", "48.Ay Sonu", "13 Yaş"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, asilarBebek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGunBebek.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, asiZamanlarBebek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZamanBebek.setAdapter(adapter1);

        // kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Firestore'dan daha önce eklenenleri çek ve textView'e yaz
        firestoreBebekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Bebek Aşılar").collection("Bebek Aşılar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder asilar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String asiAdi = document.getString("Asi");
                            String asiZamani = document.getString("Zaman");
                            asilar.append(asiAdi).append(" - ").append(asiZamani).append("\n");
                        }
                        bebekAsiTakvimText.setText(asilar.toString());
                    } else {
                        bebekAsiTakvimText.setText("Aşı bilgisi alınamadı.");
                    }
                });


        //Firestore'dan aşı bilgilerini anlık olarak dinle
        asiListener = firestoreBebekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Bebek Aşılar").collection("Bebek Aşılar")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        StringBuilder asilar = new StringBuilder();
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String asiAdi = dc.getDocument().getString("Aşı");
                            asilar.append(asiAdi).append("\n");
                        }
                        bebekAsiTakvimText.setText(asilar.toString());
                    } else {
                        bebekAsiTakvimText.setText("Aşı bilgisi alınamadı.");
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
    public void bebekAsiKaydet (View view) {
        String secilenAsi = spinnerGunBebek.getSelectedItem().toString();
        String secilenZaman = spinnerZamanBebek.getSelectedItem().toString();

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // İlaç bilgisini Firestore'a kaydet
        Map<String, Object> eklenenAsi = new HashMap<>();
        eklenenAsi.put("Asi", secilenAsi);
        eklenenAsi.put("Zaman", secilenZaman);

        firestoreBebekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Bebek Aşılar").collection("Bebek Aşılar").add(eklenenAsi)
                .addOnSuccessListener(documentReference -> {
                    // Başarıyla eklendiğinde kullanıcıya geri bildirim ver
                    Toast.makeText(this, "Aşı başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();

                    // Aşıları tekrar yükle ve TextView'i güncelle
                    firestoreBebekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                            .document("Bebek Aşılar").collection("Bebek Aşılar")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    StringBuilder asilar = new StringBuilder();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String asiAdi = document.getString("Asi");
                                        String asiZaman = document.getString("Zaman");
                                        asilar.append(asiAdi).append(" - ").append(asiZaman).append("\n");
                                    }
                                    bebekAsiTakvimText.setText(asilar.toString());
                                } else {
                                    bebekAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Başarısız olduğunda kullanıcıya hata mesajı göster
                    Toast.makeText(this, "Aşı kaydedilirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
