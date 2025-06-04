package com.example.konterku;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HistoriFragment extends Fragment {

    RecyclerView recyclerTopup, recyclerPembelian;
    List<TopUp> topUpList;
    List<Pembelian> pembelianList;
    TopUpAdapter topUpAdapter;
    PembelianAdapter pembelianAdapter;

    DatabaseReference dbTopup, dbPembelian;
    FirebaseAuth auth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_histori_fragment, container, false);

        recyclerTopup = view.findViewById(R.id.recyclerTopup);
        recyclerPembelian = view.findViewById(R.id.recyclerPembelian);

        recyclerTopup.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPembelian.setLayoutManager(new LinearLayoutManager(getContext()));

        topUpList = new ArrayList<>();
        pembelianList = new ArrayList<>();

        topUpAdapter = new TopUpAdapter(topUpList);
        pembelianAdapter = new PembelianAdapter(pembelianList);

        recyclerTopup.setAdapter(topUpAdapter);
        recyclerPembelian.setAdapter(pembelianAdapter);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            dbTopup = FirebaseDatabase.getInstance().getReference("TopUp").child(userId);
            dbPembelian = FirebaseDatabase.getInstance().getReference("Pembelian").child(userId);

            // Listener TopUp
            dbTopup.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    topUpList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        TopUp topUp = data.getValue(TopUp.class);
                        if (topUp != null) {
                            topUpList.add(topUp);
                        }
                    }
                    topUpAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Gagal mengambil data TopUp", Toast.LENGTH_SHORT).show();
                }
            });

            // Listener Pembelian
            dbPembelian.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pembelianList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Pembelian pembelian = data.getValue(Pembelian.class);
                        if (pembelian != null) {
                            pembelianList.add(pembelian);
                        }
                    }
                    pembelianAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Gagal mengambil data Pembelian", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User belum login", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    // MODEL CLASS
    public static class TopUp {
        public String nominal;
        public String metode;
        public String tanggal;

        public TopUp() {}
    }

    public static class Pembelian {
        public String nomor;
        public String paket;
        public String metode;
        public String tanggal;

        public Pembelian() {}
    }

    // ADAPTER TOPUP
    public class TopUpAdapter extends RecyclerView.Adapter<TopUpAdapter.ViewHolder> {

        private List<TopUp> list;

        public TopUpAdapter(List<TopUp> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topup, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TopUp item = list.get(position);
            holder.tvNominal.setText("Rp. " + item.nominal);
            holder.tvMetode.setText("Metode: " + item.metode);
            holder.tvTanggal.setText("Tanggal: " + item.tanggal);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNominal, tvMetode, tvTanggal;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNominal = itemView.findViewById(R.id.tvNominal);
                tvMetode = itemView.findViewById(R.id.tvMetode);
                tvTanggal = itemView.findViewById(R.id.tvTanggal);
            }
        }
    }

    // ADAPTER PEMBELIAN
    public class PembelianAdapter extends RecyclerView.Adapter<PembelianAdapter.ViewHolder> {

        private List<Pembelian> list;

        public PembelianAdapter(List<Pembelian> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pembelian, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pembelian item = list.get(position);
            holder.tvNomor.setText("Nomor: " + item.nomor);
            holder.tvPaket.setText("Paket: " + item.paket);
            holder.tvMetode.setText("Metode: " + item.metode);
            holder.tvTanggal.setText("Tanggal: " + item.tanggal);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNomor, tvPaket, tvMetode, tvTanggal;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNomor = itemView.findViewById(R.id.tvNomor);
                tvPaket = itemView.findViewById(R.id.tvPaket);
                tvMetode = itemView.findViewById(R.id.tvMetode);
                tvTanggal = itemView.findViewById(R.id.tvTanggal);
            }
        }
    }
}
