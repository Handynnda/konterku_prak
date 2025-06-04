package com.example.konterku;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Form_pembayaran extends AppCompatActivity {

    TextView tvNomor, tvPaket, tvPayment;
    Button btnBayar;

    String nomor, paket, metode;
    int harga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pembayaran);

        tvNomor = findViewById(R.id.tvnomor);
        tvPaket = findViewById(R.id.tvpaket);
        tvPayment = findViewById(R.id.tvPayment);
        btnBayar = findViewById(R.id.btnbayar);

        // Ambil data dari intent
        nomor = getIntent().getStringExtra("nomor");
        paket = getIntent().getStringExtra("paket");
        metode = getIntent().getStringExtra("metode");

        // Tampilkan ke tampilan
        tvNomor.setText("Nomor Pembeli: " + nomor);
        tvPaket.setText("Paket Pembelian: " + paket);
        tvPayment.setText("Metode Pembayaran: " + metode);

        btnBayar.setOnClickListener(v -> {
            if (extractHargaFromPaket(paket)) {
                if (metode.equalsIgnoreCase("Saldo Aplikasi")) {
                    kurangiSaldoDariFirebase(harga);
                } else {
                    simpanDataKeFirebase();
                    showSuccessDialog();
                }
            } else {
                showFailedDialog("Gagal membaca harga dari paket.");
            }
        });
    }

    private boolean extractHargaFromPaket(String paket) {
        try {
            String[] parts = paket.split("Rp\\.");
            if (parts.length > 1) {
                String hargaStr = parts[1].replace(".", "").trim();
                harga = Integer.parseInt(hargaStr);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void kurangiSaldoDariFirebase(int harga) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference saldoRef = FirebaseDatabase.getInstance().getReference("Saldo").child(userId);

        saldoRef.child("saldo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int saldo = snapshot.getValue(Integer.class);
                    if (saldo >= harga) {
                        int saldoBaru = saldo - harga;
                        saldoRef.child("saldo").setValue(saldoBaru)
                                .addOnSuccessListener(unused -> {
                                    simpanDataKeFirebase();

                                    // Perbarui SharedPreferences
                                    SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                                    editor.putInt("saldo", saldoBaru);
                                    editor.apply();

                                    showSuccessDialog();
                                });
                    } else {
                        showFailedDialog("Saldo tidak cukup untuk melakukan transaksi ini.");
                    }
                } else {
                    showFailedDialog("Saldo belum tersedia di sistem.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showFailedDialog("Terjadi kesalahan: " + error.getMessage());
            }
        });
    }

    // ✅ PERBAIKAN: Menyimpan ke Pembelian/{UID}/{autoID}
    private void simpanDataKeFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("Pembelian")
                .child(userId);

        String id = db.push().getKey();

        Pembelian pembelian = new Pembelian();
        pembelian.nomor = nomor;
        pembelian.paket = paket;
        pembelian.metode = metode;
        pembelian.tanggal = getCurrentDate();

        if (id != null) {
            db.child(id).setValue(pembelian);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pembayaran Berhasil");
        builder.setMessage("Transaksi berhasil. Saldo telah dikurangi sebesar Rp. " + harga);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(Form_pembayaran.this, Dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.show();
    }

    private void showFailedDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pembayaran Gagal");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    // Model Pembelian
    public static class Pembelian {
        public String nomor;
        public String paket;
        public String metode;
        public String tanggal;

        public Pembelian() {
        }
    }
}
