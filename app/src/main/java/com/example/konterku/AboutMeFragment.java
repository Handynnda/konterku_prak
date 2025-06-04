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
    private Button buttonGantiSandi, buttonHapusAkun;

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
        buttonGantiSandi = view.findViewById(R.id.buttongantisandi);
        buttonHapusAkun = view.findViewById(R.id.buttonhapusakun);

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

        // Fungsi ganti sandi
        buttonGantiSandi.setOnClickListener(v -> {
            if (currentUser != null) {
                String email = currentUser.getEmail();
                if (!TextUtils.isEmpty(email)) {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Email reset sandi telah dikirim ke " + email, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(requireContext(), "Gagal mengirim email reset sandi.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Fungsi hapus akun
        buttonHapusAkun.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Konfirmasi");
            builder.setMessage("Apakah Anda yakin ingin menghapus akun ini?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    databaseReference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Akun berhasil dihapus.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(requireContext(), Login.class));
                                    requireActivity().finish();
                                } else {
                                    Toast.makeText(requireContext(), "Gagal menghapus akun: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Gagal menghapus data pengguna dari database.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        return view;
    }
}
