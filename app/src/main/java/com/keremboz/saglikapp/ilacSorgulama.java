package com.keremboz.saglikapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ilacSorgulama extends AppCompatActivity {

    FirebaseFirestore firestore1;
    AutoCompleteTextView sorgulananIlacCompText;
    Button ilacSorgulaButton;
    String firebaseSorgulananIlac;
    Object firebaseSorgulananDeger;
    static final String TAG = ilacSorgulama.class.getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilac_sorgulama);

        sorgulananIlacCompText = findViewById(R.id.sorgulananCompText);
        ilacSorgulaButton = findViewById(R.id.ilacSorgula);
        firestore1 = FirebaseFirestore.getInstance();
        firebaseSorgulananIlac = "";
        firebaseSorgulananDeger = "";

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore'dan ilaç adlarını al
        CollectionReference ilaclarRef = firestore1.collection("İlaçlar");

        // İlaç adlarını saklamak için bir liste oluştur
        List<String> ilacAdlari = new ArrayList<>();

        // Firestore'dan belgeleri al
        ilaclarRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Belgedeki ilac_adı alanını al
                        String ilacAdi = document.getString("ilaç_adı");
                        // Eğer ilac_adı alanı boş değilse ve ilacAdi listesinde yoksa ekle
                        if (ilacAdi != null && !ilacAdlari.contains(ilacAdi)) {
                            ilacAdlari.add(ilacAdi);
                        }
                    }
                    // AutocompleteTextView'i bul ve doldur
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ilacSorgulama.this, android.R.layout.simple_dropdown_item_1line, ilacAdlari);
                    sorgulananIlacCompText.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}


        /*firestore1.collection("İlaçlar_ve_Etkin_Maddeler")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Belge varsa, tüm alanları al
                        Map<String, Object> ilacVerileri = documentSnapshot.getData();

                        // Verileri kullanabilirsiniz
                        if (ilacVerileri != null) {
                            List<String> alanlarListesi = new ArrayList<>();
                            //List<Object> valueListesi = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : ilacVerileri.entrySet()) {
                                String fieldName = entry.getKey();
                                 Object valueName = entry.getValue();
                                valueListesi.add(valueName);

                                alanlarListesi.add(fieldName);
                            }
                            // AutoCompleteTextView'e alanların listesini göstermek için adapter'ı güncelle
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_dropdown_item_1line, alanlarListesi);

                            sorgulananIlacCompText.setAdapter(adapter);
                        } else {
                            showToast("İlaç verisi boş.");
                        }
                    } else {
                        showToast("Belirtilen belge bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Belge alınırken bir hata oluştu: " + e.getMessage());
                });
*/

        /*
        // ilacSorgulaButton'a tıklama olayı dinleyicisi ekle
        ilacSorgulaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseSorgulananIlac = sorgulananIlacCompText.getText().toString().trim();
                //firebaseSorgulananDeger = sorgulananIlacCompText.getAutofillValue().getTextValue().toString().trim();
                if (!firebaseSorgulananIlac.isEmpty()) {
                    // Firestore'dan ilaçları kontrol et
                    firestore1.collection("İlaçlar").document("İlaçlar Ve Etkin Maddeler")
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    //String ilacDegeri = (String) documentSnapshot.get(firebaseSorgulananIlac);
                                    Map<String, Object> ilacVerileri = documentSnapshot.getData();
                                    if ((ilacVerileri != null && ilacVerileri.containsKey(firebaseSorgulananIlac))) {
                                        // İlaç mevcut, sorgulamaya devam et
                                        showToast("İlaç seçildi: " + firebaseSorgulananIlac);

                                        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                                                .whereEqualTo("İlaç", firebaseSorgulananIlac)
                                                //.whereEqualTo("Değer", firebaseSorgulananDeger)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        // Eğer ilaç bulunduysa, kullanıcı bu ilacı daha önce eklemiş
                                                        showAlertMessage("         !! BU İLACA ALERJİNİZ VAR !!"
                                                                + "\n" + "            ! Bu ilacı kullanamazsınız !");
                                                    } else {
                                                        // Eğer ilaç bulunamadıysa, kullanıcı bu ilacı daha önce eklememiş
                                                        showAlertMessage("             ...Bu ilaca alerjiniz yok..."
                                                                + "\n" + "              Bu ilacı kullanabilirsiniz.");
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    // İlaç çıkarılamadı
                                                    showToast("İlaç çıkarılırken bir hata oluştu.");
                                                });
                                    } else {
                                        // İlaç bulunamadı, uyarı mesajı göster
                                        showAlertMessage("Lütfen geçerli bir ilaç ismi giriniz.");
                                    }
                                } else {
                                    showToast("Belirtilen ilaç bulunamadı. Lütfen ilaç seçiniz!");
                                }
                            })
                            .addOnFailureListener(e -> {
                                showToast("Belge alınırken bir hata oluştu: " + e.getMessage());
                            });
                } else {
                    // Eğer ilaç ismi yazılmış ancak seçilmemişse uyarı ver
                    showAlertMessage("Lütfen ilaç seçiniz veya doğru bir ilaç adı giriniz.");
                }
            }
        });

    }

    private void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAlertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Uyarı")
                .setPositiveButton("Tamam", null)
                .show();
    }
    */
