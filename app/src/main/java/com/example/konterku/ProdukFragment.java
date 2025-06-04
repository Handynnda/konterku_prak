package com.example.konterku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProdukFragment extends Fragment {

    TextView textIsi;
    ImageView buttonTopup;

    LinearLayout buttonPaxis, buttonPxl, buttonPindosat, buttonPtri, buttonPtelkomsel;
    LinearLayout buttonKaxis, buttonKxl, buttonKindosat, buttonKtri, buttonKtelkomsel;

    DatabaseReference saldoRef;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_produk_fragment, container, false);

        textIsi = view.findViewById(R.id.textisi);
        buttonTopup = view.findViewById(R.id.buttontopup);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        saldoRef = FirebaseDatabase.getInstance().getReference("Saldo").child(userId);

        saldoRef.child("saldo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int saldo = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                textIsi.setText("Rp. " + saldo);

                SharedPreferences.Editor editor = requireActivity()
                        .getSharedPreferences("user_data", getContext().MODE_PRIVATE).edit();
                editor.putInt("saldo", saldo);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textIsi.setText("Rp. 0");
            }
        });

        buttonTopup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), form_topup.class);
            startActivity(intent);
        });

        // Inisialisasi dan arahkan tombol
        initButton(view);
        return view;
    }

    private void initButton(View view) {
        buttonPaxis = view.findViewById(R.id.buttonPaxis);
        buttonPxl = view.findViewById(R.id.buttonPxl);
        buttonPindosat = view.findViewById(R.id.buttonPindosat);
        buttonPtri = view.findViewById(R.id.buttonPtri);
        buttonPtelkomsel = view.findViewById(R.id.buttonPtelkomsel);

        buttonKaxis = view.findViewById(R.id.buttonKaxis);
        buttonKxl = view.findViewById(R.id.buttonKxl);
        buttonKindosat = view.findViewById(R.id.buttonKindosat);
        buttonKtri = view.findViewById(R.id.buttonKtri);
        buttonKtelkomsel = view.findViewById(R.id.buttonKtelkomsel);

        setIntent(buttonPaxis, form_pulsa.class);
        setIntent(buttonPxl, form_pulsa.class);
        setIntent(buttonPindosat, form_pulsa.class);
        setIntent(buttonPtri, form_pulsa.class);
        setIntent(buttonPtelkomsel, form_pulsa.class);

        setIntent(buttonKaxis, form_kuota.class);
        setIntent(buttonKxl, form_kuota.class);
        setIntent(buttonKindosat, form_kuota.class);
        setIntent(buttonKtri, form_kuota.class);
        setIntent(buttonKtelkomsel, form_kuota.class);
    }

    private void setIntent(LinearLayout layout, Class<?> targetActivity) {
        layout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), targetActivity);
            startActivity(intent);
        });
    }
}

