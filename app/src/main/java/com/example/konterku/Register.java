package com.example.konterku;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextTanggalRegister;
    private AppCompatButton buttonRegister;
    private TextView tombolMasuk;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi komponen UI
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextTanggalRegister = findViewById(R.id.editTextTanggalRegister);
        buttonRegister = findViewById(R.id.daftar);
        tombolMasuk = findViewById(R.id.tombol_1);

        // Set listener untuk memilih tanggal
        editTextTanggalRegister.setFocusable(false);
        editTextTanggalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Format DD/MM/YYYY
                                String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                                editTextTanggalRegister.setText(formattedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Set listener untuk tombol "Daftar Sekarang"
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil nilai input dari EditText
                String fullName = editTextFullName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String registrationDate = editTextTanggalRegister.getText().toString();

                // Validasi input
                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(Register.this, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Alamat email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Kata sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(Register.this, "Kata sandi harus memiliki minimal 6 karakter", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Kata sandi dan konfirmasi tidak cocok", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(registrationDate)) {
                    Toast.makeText(Register.this, "Tanggal register belum dipilih", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proses registrasi
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                    HashMap<String, String> userMap = new HashMap<>();
                                    userMap.put("fullName", fullName);
                                    userMap.put("email", email);
                                    userMap.put("registrationDate", registrationDate);

                                    databaseReference.setValue(userMap)
                                            .addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Register.this, Login.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Register.this, "Gagal menyimpan data pengguna.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(Register.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Listener tombol masuk
        tombolMasuk.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });
    }
}
