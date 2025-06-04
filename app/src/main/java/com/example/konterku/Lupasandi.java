package com.example.konterku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Lupasandi extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private ImageView kembali;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupasandi);

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inisialisasi komponen UI
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        kembali = findViewById(R.id.imageback); // Pastikan ID di XML adalah "imageback"

        // Tombol reset password
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(Lupasandi.this, "Masukkan email Anda", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Lupasandi.this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Lupasandi.this, "Gagal mengirim email reset: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Tombol kembali ke halaman login
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Lupasandi.this, Login.class); // Ganti "Login.class" dengan nama Activity login kamu
                startActivity(intent);
                finish(); // Supaya activity ini tidak bisa kembali lewat tombol back
            }
        });
    }
}