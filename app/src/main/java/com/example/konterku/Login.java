package com.example.konterku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Login extends AppCompatActivity {

    private AutoCompleteTextView textEmail;
    private EditText textPass;
    private LinearLayout buttonLogin;
    private TextView lupaSandi, daftar;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi komponen
        textEmail = findViewById(R.id.TextEmail);
        textPass = findViewById(R.id.TextPass);
        buttonLogin = findViewById(R.id.buttonLogin);
        lupaSandi = findViewById(R.id.LupaSandi);
        daftar = findViewById(R.id.textDaftar);
        progressBar = findViewById(R.id.progressBar);

        // Set listener untuk tombol Login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString().trim();
                String password = textPass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(Login.this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
                } else {
                    loginWithFirebase(email, password);
                }
            }
        });

        // Set listener untuk "Lupa Kata Sandi"
        lupaSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Lupasandi.class);
                startActivity(intent);
            }
        });

        // Set listener untuk "Register"
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        // Setup email autocomplete
        setupEmailAutoComplete();
    }

    private void loginWithFirebase(String email, String password) {
        // Menampilkan ProgressBar saat login
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Sembunyikan ProgressBar setelah proses login selesai
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Login berhasil
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                        saveEmailHistory(email);  // Simpan email login ke SharedPreferences
                        Intent intent = new Intent(Login.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Login gagal
                        Toast.makeText(Login.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Validasi format email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Menyimpan email yang berhasil login ke SharedPreferences
    private void saveEmailHistory(String email) {
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> emailSet = prefs.getStringSet("email_history", new HashSet<>());
        emailSet.add(email);

        editor.putStringSet("email_history", emailSet);
        editor.apply();
    }

    // Setup autocomplete untuk email
    private void setupEmailAutoComplete() {
        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        Set<String> emailSet = prefs.getStringSet("email_history", new HashSet<>());

        List<String> staticEmails = Arrays.asList(
                "Handynandaf@gmail.com",
                "Handynnda@gmail.com",
                "Lailahasna@gmail.com",
                "Lailahns@gmail.com"
        );

        Set<String> combinedSet = new HashSet<>(emailSet);
        combinedSet.addAll(staticEmails);

        // ArrayAdapter dan set ke AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(combinedSet)
        );

        textEmail.setAdapter(adapter);
        textEmail.setThreshold(1);
    }
}
