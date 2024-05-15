package com.keremboz.saglikapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ilacEkleme extends AppCompatActivity {

    FirebaseFirestore firestore1;
    AutoCompleteTextView alerjenIlaclar;
    Button firebaseEkle;
    Button firebaseCikar;
    TextView alerjenMaddelerText;
    String firebaseEklenenIlac;
    ListenerRegistration ilacListener;
    List<String> ilacListesi;
    private static final String TAG = "MainActivity";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilac_ekleme);

        alerjenIlaclar = findViewById(R.id.alerjenMaddeler);
        firebaseEkle = findViewById(R.id.firebaseEkle);
        firebaseCikar = findViewById(R.id.firebaseCikar);
        alerjenMaddelerText = findViewById(R.id.alerjenMaddelerText);
        firestore1 = FirebaseFirestore.getInstance();
        firebaseEklenenIlac = "";
        ilacListesi = new ArrayList<>();

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore'dan daha önce eklenen ilaçları çek ve TextView'e ekle
        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder ilaclar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ilacAdi = document.getString("İlaç");
                            ilaclar.append(ilacAdi).append("\n");
                        }
                        alerjenMaddelerText.setText(ilaclar.toString());
                    } else {
                        alerjenMaddelerText.setText("İlaç bilgileri alınamadı.");
                    }
                });

        // Firestore'dan ilaç bilgilerini anlık olarak dinle
        ilacListener = firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        StringBuilder ilaclar = new StringBuilder();
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String ilacAdi = dc.getDocument().getString("İlaç");
                            ilaclar.append(ilacAdi).append("\n");
                        }
                        alerjenMaddelerText.setText(ilaclar.toString());
                    } else {
                        alerjenMaddelerText.setText("İlaç bilgileri alınamadı.");
                    }
                });
        // Firestore'dan ilaç adlarını al
        CollectionReference ilaclarRef = firestore1.collection("İlaçlar");

// İlaç adlarını saklamak için bir liste oluştur
        List<String> ilacAdlari = new ArrayList<>();

// Firestore'dan belgeleri al
        ilaclarRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Belgedeki ilaç adı alanını al
                    String ilacAdi = document.getString("ilac_adi");
                    // Eğer ilaç adı alanı boş değilse ve ilacAdlari listesinde yoksa ekle
                    if (ilacAdi != null && !ilacAdlari.contains(ilacAdi)) {
                        ilacAdlari.add(ilacAdi);
                    }
                }

                // AutocompleteTextView'i bul ve doldur
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ilacEkleme.this, android.R.layout.simple_dropdown_item_1line, ilacAdlari);
                alerjenIlaclar.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listener'ı kapat
        if (ilacListener != null) {
            ilacListener.remove();
        }
    }
    /*@SuppressLint("SetTextI18n")
    public void firebaseEkle(View view) {
        firebaseEklenenIlac = alerjenIlaclar.getText().toString().trim();
        if (firebaseEklenenIlac.isEmpty()) {
            showToast("Lütfen ilaç adı giriniz.");
            return;
        }
        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Belirli bir ilaç listesinden ilacın mevcut olup olmadığını kontrol et
        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .whereEqualTo("İlaç", firebaseEklenenIlac)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // İlaç zaten eklenmiş, uyarı ver
                        showToast("Bu ilaç zaten listenizde bulunmaktadır.");
                    } else {
                        // İlaç daha önce eklenmemiş, ekleyebiliriz
                        firestore1.collection("İlaçlar").document("İlaçlar Ve Etkin Maddeler")
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String ilacDegeri = (String) documentSnapshot.get(firebaseEklenenIlac);
                                        if (ilacDegeri != null) {
                                            // İlaç değerini alın
                                            Map<String, Object> eklenenIlac = new HashMap<>();
                                            eklenenIlac.put("İlaç", firebaseEklenenIlac);
                                            eklenenIlac.put("Değer", ilacDegeri);

                                            // İlaç belgesini Eklenen İlaç koleksiyonuna ekleyin
                                            firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                                                    .add(eklenenIlac)
                                                    .addOnSuccessListener(documentReference -> {
                                                        // İlaç başarıyla eklendi
                                                        showToast("İlaç başarıyla eklendi.");
                                                        // Yeni listeyi güncelle
                                                        updateIlacListesi(userId);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // İlaç eklenemedi
                                                        showToast("İlaç eklenirken bir hata oluştu.");
                                                    });
                                        } else {
                                            // İlaç değeri bulunamadı
                                            showDialog("Hata", "İlaç değeri bulunamadı."+"\n"+"Lütfen geçerli bir ilaç seçiniz.");
                                        }
                                    } else {
                                        // Belge bulunamadı
                                        showToast("İlaç belgesi bulunamadı.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Belge alınamadı
                                    showToast("Belge alınırken bir hata oluştu: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // İlaç listesi alınamadı
                    showToast("İlaç listesi alınırken bir hata oluştu.");
                });
    }
*/
    @SuppressLint("SetTextI18n")
    public void firebaseEkle(View view) {
        // AutoCompleteTextView'ten seçilen ilacı al
        String yeniIlac = alerjenIlaclar.getText().toString().trim();

        // Eğer ilaç adı boşsa işlem yapma
        if (yeniIlac.isEmpty()) {
            showToast("Lütfen bir ilaç adı girin.");
            return;
        }

        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore'dan ilacı kontrol et
        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .whereEqualTo("İlaç", yeniIlac)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // İlaç zaten eklenmiş, kullanıcıya uyarı ver
                        showToast("Bu ilaç zaten listenizde bulunmaktadır.");
                    } else {
                        // İlaç daha önce eklenmemiş, Firestore'a ekleyebiliriz
                        Map<String, Object> yeniIlacMap = new HashMap<>();
                        yeniIlacMap.put("İlaç", yeniIlac);

                        // İlaç adını Firestore'a ekle
                        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                                .add(yeniIlacMap)
                                .addOnSuccessListener(documentReference -> {
                                    // İlaç başarıyla eklendi
                                    showToast("İlaç başarıyla eklendi.");

                                    // İlaç adını ve etkin maddeleri TextView'e yaz
                                    StringBuilder ilacText = new StringBuilder("İlaç Adı: " + yeniIlac);
                                    alerjenMaddelerText.setText(ilacText.toString());

                                    // Yeni listeyi güncelle
                                    updateIlacListesi(userId);
                                })
                                .addOnFailureListener(e -> {
                                    // İlaç eklenemedi
                                    showToast("İlaç eklenirken bir hata oluştu: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // İlaç listesi alınamadı
                    showToast("İlaç listesi alınırken bir hata oluştu.");
                });
    }


    @SuppressLint("SetTextI18n")
    public void firebaseCikar(View view) {
        String ilacCikarilacak = alerjenIlaclar.getText().toString().trim();
        // Eğer ilaç adı boşsa işlem yapma
        if (ilacCikarilacak.isEmpty()) {
            showToast("Lütfen bir ilaç adı girin.");
            return;
        }
        // Kullanıcının UID'sini al
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Firestore'dan ilacı kaldır
        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .whereEqualTo("İlaç", ilacCikarilacak)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // İlaç listede bulunamadı, kullanıcıya uyarı göster
                        showDialog("Hata", "İlaç listenizde bu ilaç bulunamadı.");
                    } else {
                        // İlaç listeden başarıyla kaldırıldı
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        showToast("İlaç listenizden kaldırıldı.");
                        // Güncel listeyi yeniden yükle
                        updateIlacListesi(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // İlaç çıkarılamadı
                    showToast("İlaç çıkarılırken bir hata oluştu.");
                });
    }

    // Güncel ilaç listesini yenileme fonksiyonu
    private void updateIlacListesi(String userId) {
        firestore1.collection("Kullanicilar").document(userId).collection("Eklenen İlaç")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder ilaclar = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ilac = document.getString("İlaç");
                            ilaclar.append(ilac).append("\n");
                        }
                        alerjenMaddelerText.setText(ilaclar.toString());
                    } else {
                        alerjenMaddelerText.setText("İlaç bilgisi alınamadı.");
                    }
                });
    }
    private void showToast (String message){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    // Hata mesajı gösteren yardımcı fonksiyon
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
