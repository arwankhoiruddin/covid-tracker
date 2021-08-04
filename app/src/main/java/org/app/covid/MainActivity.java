package org.app.covid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.app.covid.model.DatabaseHelper;
import org.app.covid.model.Lokasi;
import org.app.covid.model.Pasien;
import org.app.covid.model.PasienListAdapter;
import org.app.covid.model.StaticVars;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int dataCounter = 0;
    ListView pasienListView;
    private static TextView textViewBelumAdaData;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        StaticVars.databaseHelper = new DatabaseHelper(this);

        Button btnExportToCSV = (Button) findViewById(R.id.btnExportCSV);

        btnExportToCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Yakin mau menghapus data?")
                        .setMessage("Semua data di database dan akan dihapus lalu disimpan sebagai file csv di folder Documents")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                                String csvOutput = dir + "/pasienCovid.csv";
                                try {
                                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvOutput));
                                    StringBuilder sb = new StringBuilder();

                                    // judul csv
                                    sb.append("ID Pasien" + ",");
                                    sb.append("Nama Lengkap" + ",");
                                    sb.append("Alamat Lengkap" + ",");
                                    sb.append("Tanggal Sakit" + ",");
                                    sb.append("Tanggal Pergi" + ",");
                                    sb.append("Latitude" + ",");
                                    sb.append("Longitude" + "\n");

                                    Log.d("debug", csvOutput);
                                    Cursor res = StaticVars.databaseHelper.getAllPasien();
                                    if (res.moveToFirst()) {
                                        do {
                                            int id_pasien = res.getInt(0);
                                            String nama_pasien = res.getString(1);
                                            String alamat_lengkap = res.getString(2);
                                            String tanggalSakit = res.getString(3);

                                            Cursor resPosition = StaticVars.databaseHelper.getAllPositions(id_pasien);
                                            if (resPosition.moveToFirst()) {
                                                do {
                                                    String tanggalPergi = resPosition.getString(0);
                                                    double latitude = resPosition.getDouble(1);
                                                    double longitude = resPosition.getDouble(2);

                                                    sb.append(id_pasien + ",");
                                                    sb.append(nama_pasien + ",");
                                                    sb.append(alamat_lengkap + ",");
                                                    sb.append(tanggalSakit + ",");
                                                    sb.append(tanggalPergi + ",");
                                                    sb.append(latitude + ",");
                                                    sb.append(longitude + "\n");

                                                } while (resPosition.moveToNext());
                                            }

                                        } while (res.moveToNext());
                                    }
                                    Log.d("debug", sb.toString());
                                    bufferedWriter.write(sb.toString());
                                    bufferedWriter.close();
                                    StaticVars.databaseHelper.clearData();
                                    Toast.makeText(getApplicationContext(), "Berhasil Export ke CSV di folder Documents", Toast.LENGTH_SHORT);
                                    refresh();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Tidak", null);
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        });

        Button btnTambahPasien = (Button) findViewById(R.id.btnTambahPasien);
        textViewBelumAdaData = (TextView) findViewById(R.id.belumAdaData);

        StaticVars.updateData();

        if (StaticVars.pasienKosong.size() == 0) {
            textViewBelumAdaData.setText("Belum ada data pasien");
        } else {
            textViewBelumAdaData.setText("");
        }

        pasienListView = (ListView) findViewById(R.id.listPasien);
        StaticVars.adapterPasien = new PasienListAdapter(this, R.layout.pasien_adapter_layout, StaticVars.pasienKosong);
        pasienListView.setAdapter(StaticVars.adapterPasien);
        StaticVars.adapterPasien.notifyDataSetChanged();

        btnTambahPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVars.pasienBaru = null;
                Intent intentTambah = new Intent(MainActivity.this, TambahPasienActivity.class);
                startActivity(intentTambah);
            }
        });

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    public void refresh() {
        Intent intentTambah = new Intent(MainActivity.this, RefreshActivity.class);
        startActivity(intentTambah);
    }
}