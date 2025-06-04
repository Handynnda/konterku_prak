package com.example.konterku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class form_kuota extends AppCompatActivity {

    EditText inputNomor;
    final String metodePembayaran = "Saldo Aplikasi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_kuota);

        inputNomor = findViewById(R.id.inputnomor);

        // Daftar produk kuota (ID View, Nama Paket, Harga)
        addKuota(R.id.kuota1, "1GB", "Rp.12.000");
        addKuota(R.id.kuota2, "2GB", "Rp.17.000");
        addKuota(R.id.kuota3, "3GB", "Rp.22.000");
        addKuota(R.id.kuota4, "4GB", "Rp.27.000");
        addKuota(R.id.kuota5, "5GB", "Rp.32.000");
        addKuota(R.id.kuota6, "6GB", "Rp.37.000");
        addKuota(R.id.kuota7, "7GB", "Rp.42.000");
        addKuota(R.id.kuota8, "8GB", "Rp.47.000");
        addKuota(R.id.kuota9, "9GB", "Rp.52.000");
        addKuota(R.id.kuota10, "10GB", "Rp.57.000");
        addKuota(R.id.kuota11, "11GB", "Rp.62.000");
        addKuota(R.id.kuota12, "12GB", "Rp.67.000");
        addKuota(R.id.kuota13, "13GB", "Rp.72.000");
        addKuota(R.id.kuota14, "14GB", "Rp.77.000");
    }

    private void addKuota(int viewId, final String namaPaket, final String harga) {
        LinearLayout kuotaLayout = findViewById(viewId);

        kuotaLayout.setOnClickListener(v -> {
            String nomor = inputNomor.getText().toString().trim();

            if (nomor.isEmpty()) {
                Toast.makeText(form_kuota.this, "Masukkan nomor terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nomor.length() < 10) {
                Toast.makeText(form_kuota.this, "Nomor terlalu pendek, inputkan minimal 10 digit", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim data ke halaman pembayaran
            Intent intent = new Intent(form_kuota.this, Form_pembayaran.class);
            intent.putExtra("nomor", nomor);
            intent.putExtra("paket", "Kuota " + namaPaket + " - " + harga);
            intent.putExtra("metode", metodePembayaran);
            startActivity(intent);
        });
    }
}
