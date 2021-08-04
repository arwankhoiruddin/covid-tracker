package org.app.covid.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.app.covid.MainActivity;
import org.app.covid.R;
import org.app.covid.TambahPasienActivity;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PasienListAdapter extends ArrayAdapter<Pasien> {

    private static final String TAG = "PasienListAdapter";

    private Context mContext;
    int mResource;

    String namaLengkap;
    String alamat;
    Calendar tanggalSakit;
    ArrayList<Lokasi> positions;

    public PasienListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Pasien> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        namaLengkap = getItem(position).getNamaLengkap();
        alamat = getItem(position).getAlamat();
        tanggalSakit = getItem(position).getTanggal();
        positions = getItem(position).getPositions();

        Pasien pasien = new Pasien(namaLengkap, alamat, tanggalSakit, positions);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textNama = (TextView) convertView.findViewById(R.id.textNama);
        TextView textAlamat = (TextView) convertView.findViewById(R.id.textAlamat);
        TextView textTanggalSakit = (TextView) convertView.findViewById(R.id.textTanggalSakit);
        Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("Yakin mau menghapus data?")
                        .setMessage("Data akan dihapus. Silakan klik tombol refresh setelah menghapus")
                        .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StaticVars.databaseHelper.deleteData(textNama.getText().toString());
                            }
                        })
                        .setNegativeButton("tidak", null);
                AlertDialog alert = dialogBuilder.create();
                alert.show();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textViewNama = (TextView) v.findViewById(R.id.textNama);

                String nama = textViewNama.getText().toString();
                Log.d("debug", "nama " + nama);
                Cursor res = StaticVars.databaseHelper.getPasien(nama);

                if (res.moveToFirst()) {
                    int id_pasien = res.getInt(0);
                    String alamat_lengkap = res.getString(2);
                    String tanggalSakit = res.getString(3);
                    Calendar cal = StaticVars.stringToCal(tanggalSakit);

                    ArrayList<Lokasi> positions = new ArrayList<>();
                    Cursor resPosition = StaticVars.databaseHelper.getAllPositions(id_pasien);
                    if (resPosition.moveToFirst()) {
                        do {
                            double latitude = resPosition.getDouble(1);
                            double longitude = resPosition.getDouble(2);
                            Calendar tanggalPergi = StaticVars.stringToCal(resPosition.getString(0));
                            Lokasi tempLokasi = new Lokasi(longitude, latitude, tanggalPergi);
                            positions.add(tempLokasi);
                        } while (resPosition.moveToNext());
                    }
                    StaticVars.pasienBaru = new Pasien(nama, alamat_lengkap, cal, positions);
                }
                Intent intent = new Intent(getContext(), TambahPasienActivity.class);
                mContext.startActivity(intent);
            }
        });

        textNama.setText(namaLengkap);
        textAlamat.setText(alamat);

        int dd = tanggalSakit.get(Calendar.DAY_OF_MONTH);
        int mm = tanggalSakit.get(Calendar.MONTH);
        int yyyy = tanggalSakit.get(Calendar.YEAR);

        textTanggalSakit.setText("Tanggal sakit: " + dd + "/" + mm + "/" + yyyy);

        return convertView;
    }
}
