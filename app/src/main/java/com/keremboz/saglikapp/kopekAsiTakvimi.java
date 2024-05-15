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

public class kopekAsiTakvimi extends AppCompatActivity {

    FirebaseFirestore firestoreKopekAsi;
    ArrayList<RadioGroup> AsiIlacRadioGroups;
    ArrayList<RadioGroup> AsiZamanRadioGroups;
    String firebaseEklenenAsi;
    ListenerRegistration asiListener;
    Button kaydetButton;
    TextView kopekAsiTakvimText;

    Spinner spinnerGunKopek;
    Spinner spinnerZamanKopek;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kopek_asi_takvimi);

        AsiIlacRadioGroups = new ArrayList<>();
        AsiZamanRadioGroups = new ArrayList<>();
        kaydetButton = findViewById(R.id.kopekAsiKaydetButton);
        kopekAsiTakvimText = findViewById(R.id.kopekAsiTakvimi);
        firebaseEklenenAsi = "";
        firestoreKopekAsi = FirebaseFirestore.getInstance();

        spinnerGunKopek = findViewById(R.id.spinnerGunKopek);
        spinnerZamanKopek = findViewById(R.id.spinnerZamanKopek);

        String[] asilarKopek = {"İç-Dış Parazit", "Karma", "Bronşin", "Corona Virüs", "Lyme", "Kuduz", "Mantar"};
        String[] asiZamanlarKopek = {"6. Hafta", "8. Hafta", "9. Hafta", "10. Hafta", "11. Hafta", "12. Hafta"
                                    , "13. Hafta", "14. Hafta", "15. Hafta", "16. Hafta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, asilarKopek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGunKopek.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,asiZamanlarKopek);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZamanKopek.setAdapter(adapter1);

        //kullanıcı UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Firestore'dan daha önce eklenenleri çek ve textView'e yaz
        firestoreKopekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Köpek Aşılar").collection("Köpek Aşılar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder asilar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String asiAdi = document.getString("Asi");
                            String asiZaman = document.getString("Zaman");
                            asilar.append(asiAdi).append(" - ").append(asiZaman).append("\n");
                        }
                        kopekAsiTakvimText.setText(asilar.toString());
                    } else {
                        kopekAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
                    }
                });
        //Firestore'dan aşı bilgilerini anlık olarak dinle
        asiListener = firestoreKopekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Köpek Aşılar").collection("Köpek Aşılar")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        StringBuilder asilar = new StringBuilder();
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String asiAdi = dc.getDocument().getString("Aşı");
                            asilar.append(asiAdi).append("\n");
                        }
                        kopekAsiTakvimText.setText(asilar.toString());
                    } else {
                        kopekAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
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
    public void kopekAsiKaydet (View view) {
        String secilenAsi = spinnerGunKopek.getSelectedItem().toString();
        String secilenZaman = spinnerZamanKopek.getSelectedItem().toString();

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // İlaç bilgisini Firestore'a kaydet
        Map<String, Object> eklenenAsi = new HashMap<>();
        eklenenAsi.put("Asi", secilenAsi);
        eklenenAsi.put("Zaman", secilenZaman);

        firestoreKopekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                .document("Köpek Aşılar").collection("Köpek Aşılar").add(eklenenAsi)
                .addOnSuccessListener(documentReference -> {
                    // Başarıyla eklendiğinde kullanıcıya geri bildirim ver
                    Toast.makeText(this, "Aşı başarıyla kaydedildi", Toast.LENGTH_SHORT).show();

                    // Aşıları tekrar yükle ve TextView'i güncelle
                    firestoreKopekAsi.collection("Kullanicilar").document(userId).collection("Aşılar")
                            .document("Köpek Aşılar").collection("Köpek Aşılar")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    StringBuilder asilar = new StringBuilder();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String asiAdi = document.getString("Asi");
                                        String asiZaman = document.getString("Zaman");
                                        asilar.append(asiAdi).append(" - ").append(asiZaman).append("\n");
                                    }
                                    kopekAsiTakvimText.setText(asilar.toString());
                                } else {
                                    kopekAsiTakvimText.setText("Aşı bilgisi alınamadı. ");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // Başarısız olduğunda kullanıcıya hata mesajı göster
                    Toast.makeText(this, "Aşı kaydedilirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}