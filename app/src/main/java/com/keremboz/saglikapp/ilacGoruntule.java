package com.keremboz.saglikapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ilacGoruntule extends AppCompatActivity {

    TextView alerjenIlaclarTextView;
    EditText ilacSorgula;
    Button ilacSorgulaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilac_goruntule);

        ilacSorgulaButton = findViewById(R.id.ilacSorgulaButton);
        ilacSorgula = findViewById(R.id.ilacSorgulaEditText);

        // Intent üzerinden gelen veriyi al
        Intent intent = getIntent();
        if (intent != null) {
            String alerjenIlaclar = intent.getStringExtra("alerjenIlaclar");

            // Eğer veri varsa, TextView'e set et
            if (alerjenIlaclar != null && !alerjenIlaclar.isEmpty()) {
                alerjenIlaclarTextView.setText(alerjenIlaclar);
            }
        }

        // alerjenData.txt dosyasını oku ve içeriği TextView'e ekle
        String dosyaIcerik = readAlerjenData();
        alerjenIlaclarTextView.append("\n" + dosyaIcerik);

        // EditText'e Enter tuşu dinleyicisi ekleme
        ilacSorgula.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Enter tuşuna basıldığında ve basma işlemi ACTION_DOWN olduğunda kontrol et
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // EditText'ten alınan veriyi al
                    String sorgulanacakAlerjen = ilacSorgula.getText().toString();

                    // Alerjenler TextView'in içerisinden çıkar ve duruma göre mesaj göster
                    if (removeAlerjenIlac(sorgulanacakAlerjen)) {
                        showToast("Alerjen başarıyla çıkarıldı.");
                    } else {
                        showToast("Alerjiniz Yok");
                    }

                    // Alerjenleri güncellenmiş haliyle dosyaya kaydet
                    saveAlerjenIlacData();

                    return true;
                }
                return false;
            }
        });
    }

    private String readAlerjenData() {
        StringBuilder dosyaIcerik = new StringBuilder();

        try {
            // AlerjenData.txt dosyasını aç
            FileInputStream fis = openFileInput("alerjenData.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;

            // Dosyanın içeriğini oku
            while ((line = br.readLine()) != null) {
                dosyaIcerik.append(line).append("\n");
            }

            // Kaynakları temizle
            br.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dosyaIcerik.toString();
    }

    private boolean removeAlerjenIlac(String cikarilacakAlerjen) {
        // Alerjenleri içeren metni al
        String alerjenMetin = alerjenIlaclarTextView.getText().toString();

        // Eğer cikarilacakAlerjen alerjenMetin içerisinde varsa kaldır
        if (alerjenMetin.contains(cikarilacakAlerjen)) {
            alerjenMetin = alerjenMetin.replace(cikarilacakAlerjen, "");

            // TextView'i güncelle
            alerjenIlaclarTextView.setText(alerjenMetin);

            return true;
        } else {
            return false;
        }
    }

    private void saveAlerjenIlacData() {
        // Güncellenmiş alerjenleri dosyaya kaydet
        String updatedAlerjenData = alerjenIlaclarTextView.getText().toString();

        try {
            FileOutputStream fos = openFileOutput("alerjenData.txt", MODE_PRIVATE);
            fos.write(updatedAlerjenData.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}