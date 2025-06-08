package com.example.konterku;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutMeFragment extends Fragment {

    // Deklarasi variabel untuk elemen UI
    private TextView textAkun, textEmail, textNamaLengkap, textTanggal;
    private ImageView imageViewProfile;

    // Deklarasi variabel Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_about_me_fragment, container, false);

        // Inisialisasi elemen UI
        textAkun = view.findViewById(R.id.textakun);
        textEmail = view.findViewById(R.id.textemail);
        textNamaLengkap = view.findViewById(R.id.textnamalengkap);
        textTanggal = view.findViewById(R.id.texttanggal);
        imageViewProfile = view.findViewById(R.id.imageView);

        // Inisialisasi Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Menampilkan email pengguna
            String email = currentUser.getEmail();
            textEmail.setText(email);

            // Mendapatkan UID pengguna
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // Mendapatkan data pengguna dari Firebase Realtime Database
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String registrationDate = snapshot.child("registrationDate").getValue(String.class);

                        textNamaLengkap.setText(fullName != null ? fullName : "Nama tidak tersedia");
                        textTanggal.setText(registrationDate != null ? registrationDate : "Tanggal tidak tersedia");
                    } else {
                        Toast.makeText(requireContext(), "Data pengguna tidak ditemukan!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Gagal memuat data pengguna.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Anda belum login. Harap login terlebih dahulu.", Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}
