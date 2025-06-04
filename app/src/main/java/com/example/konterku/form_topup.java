package com.example.konterku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class form_topup extends AppCompatActivity {

    private EditText etNominal;
    private RadioGroup rgMetode;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_topup);

        etNominal = findViewById(R.id.et_nominal);
        rgMetode = findViewById(R.id.rg_metode);
        btnSubmit = findViewById(R.id.btn_submit);

        ImageView ivBack = findViewById(R.id.imageView3);
        ivBack.setOnClickListener(v -> onBackPressed());

        btnSubmit.setOnClickListener(v -> {
            String nominalStr = etNominal.getText().toString().trim();
            int selectedId = rgMetode.getCheckedRadioButtonId();

            if (nominalStr.isEmpty()) {
                etNominal.setError("Nominal tidak boleh kosong");
                etNominal.requestFocus();
                return;
            }

            if (selectedId == -1) {
                Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton rbSelected = findViewById(selectedId);
            String metode = rbSelected.getText().toString();

            if (metode.equalsIgnoreCase("QRIS")) {
                showQrisDialog(nominalStr);
            } else if (metode.equalsIgnoreCase("Bank Transfer")) {
                showInfoDialog("Bank Transfer",
                        "Silakan transfer ke rekening berikut:\n\n" +
                                "🏦 BRI\nNomor Rekening: 412701032284537\nAtas Nama: Handy Nanda Fachrizal\n\n" +
                                "Setelah melakukan transfer, klik 'Lanjut' untuk menyelesaikan top-up.",
                        nominalStr);
            } else if (metode.equalsIgnoreCase("E-Wallet")) {
                showInfoDialog("E-Wallet",
                        "Silakan kirim ke ShopeePay berikut:\n\n" +
                                "📱 0895413920668\nAtas Nama: Handy Nanda Fachrizal\n\n" +
                                "Setelah melakukan transfer, klik 'Lanjut' untuk menyelesaikan top-up.",
                        nominalStr);
            } else {
                showConfirmationDialog(metode, nominalStr);
            }
        });
    }

    private void showQrisDialog(String nominalStr) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qris, null);

        new AlertDialog.Builder(this)
                .setTitle("QRIS Payment")
                .setView(dialogView)
                .setPositiveButton("Lanjut", (dialog, which) -> handleTopUp())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showInfoDialog(String title, String message, String nominalStr) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Lanjut", (dialog, which) -> handleTopUp())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showConfirmationDialog(String metode, String nominalStr) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Top Up")
                .setMessage("Apakah kamu yakin ingin top up sebesar Rp. " + nominalStr +
                        " dengan metode " + metode + "?")
                .setPositiveButton("Ya", (dialog, which) -> handleTopUp())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void handleTopUp() {
        String nominalStr = etNominal.getText().toString().trim();
        int selectedId = rgMetode.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selectedId);
        String metode = rbSelected.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference topupRef = FirebaseDatabase.getInstance()
                .getReference("TopUp")
                .child(userId);
        String topupId = topupRef.push().getKey();
        String tanggal = getCurrentDate();

        TopUp topUp = new TopUp(topupId, nominalStr, metode, tanggal);

        topupRef.child(topupId).setValue(topUp)
                .addOnSuccessListener(unused -> updateSaldo(userId, Integer.parseInt(nominalStr)))
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Gagal menyimpan data: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
    }

    private void updateSaldo(String userId, int nominal) {
        DatabaseReference saldoRef = FirebaseDatabase.getInstance()
                .getReference("Saldo")
                .child(userId);

        saldoRef.get().addOnSuccessListener(snapshot -> {
            int saldoLama = 0;
            if (snapshot.exists() && snapshot.child("saldo").getValue() != null) {
                saldoLama = snapshot.child("saldo").getValue(Integer.class);
            }

            int saldoBaru = saldoLama + nominal;
            saldoRef.child("saldo").setValue(saldoBaru)
                    .addOnSuccessListener(unused -> {
                        clearLocalSaldoCache();
                        showSuccessDialog();
                    });
        });
    }

    private void clearLocalSaldoCache() {
        SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
        editor.remove("saldo");
        editor.apply();
    }

    private void showSuccessDialog() {
        etNominal.setText("");
        rgMetode.clearCheck();

        new AlertDialog.Builder(this)
                .setTitle("Top Up Berhasil")
                .setMessage("Saldo kamu berhasil ditambahkan.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static class TopUp {
        public String id;
        public String nominal;
        public String metode;
        public String tanggal;

        public TopUp() {}

        public TopUp(String id, String nominal, String metode, String tanggal) {
            this.id = id;
            this.nominal = nominal;
            this.metode = metode;
            this.tanggal = tanggal;
        }
    }
}
