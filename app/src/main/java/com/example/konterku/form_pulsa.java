package com.example.konterku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class form_pulsa extends AppCompatActivity {

    EditText inputNomor;
    final String metodePembayaran = "Saldo Aplikasi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pulsa);

        inputNomor = findViewById(R.id.inputnomor);

        // Set listener ke masing-masing produk
        setClickListener(R.id.Produk1, "Pulsa 5rb", "Rp.6.650");
        setClickListener(R.id.Produk2, "Pulsa 10rb", "Rp.11.550");
        setClickListener(R.id.Produk3, "Pulsa 15rb", "Rp.16.100");
        setClickListener(R.id.Produk4, "Pulsa 20rb", "Rp.21.000");
        setClickListener(R.id.Produk5, "Pulsa 25rb", "Rp.26.000");
        setClickListener(R.id.Produk6, "Pulsa 30rb", "Rp.31.500");
        setClickListener(R.id.Produk7, "Pulsa 40rb", "Rp.40.500");
        setClickListener(R.id.Produk8, "Pulsa 50rb", "Rp.49.500");
        setClickListener(R.id.Produk9, "Pulsa 60rb", "Rp.60.500");
        setClickListener(R.id.Produk10, "Pulsa 70rb", "Rp.70.500");
        setClickListener(R.id.Produk11, "Pulsa 75rb", "Rp.75.500");
        setClickListener(R.id.Produk12, "Pulsa 80rb", "Rp.80.500");
        setClickListener(R.id.Produk13, "Pulsa 90rb", "Rp.90.500");
        setClickListener(R.id.Produk14, "Pulsa 100rb", "Rp.100.500");
    }

    private void setClickListener(int viewId, final String namaPaket, final String harga) {
        LinearLayout produkLayout = findViewById(viewId);
        produkLayout.setOnClickListener(v -> {
            String nomor = inputNomor.getText().toString().trim();

            if (nomor.isEmpty()) {
                Toast.makeText(form_pulsa.this, "Masukkan nomor terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nomor.length() < 10) {
                Toast.makeText(form_pulsa.this, "Nomor terlalu pendek, inputkan minimal 10 digit", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(form_pulsa.this, Form_pembayaran.class);
            intent.putExtra("nomor", nomor);
            intent.putExtra("paket", namaPaket + " - " + harga);
            intent.putExtra("metode", metodePembayaran);
            startActivity(intent);
        });
    }
}
