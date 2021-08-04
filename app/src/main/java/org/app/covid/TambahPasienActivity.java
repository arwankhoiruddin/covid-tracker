package org.app.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.app.covid.model.Lokasi;
import org.app.covid.model.Pasien;
import org.app.covid.model.StaticVars;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TambahPasienActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    TextView tanggalSakit;
    StringBuffer sb;
    String markerText;
    LatLng tempLatLng;

    // fields
    String namaLengkap;
    String alamat;
    Calendar tanggal = Calendar.getInstance();
    ArrayList<Lokasi> positions = new ArrayList<>();

    // components
    EditText txtNamaLengkap;
    EditText txtAlamat;
    Button btnPilihTanggal;
    Button btnSimpanDataPasien;
    Button btnClearLocation;
    TextView txtJudul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "On create");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tambah_pasien);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        txtNamaLengkap = (EditText) findViewById(R.id.editTextNama);
        txtAlamat = (EditText) findViewById(R.id.editTextAlamat);
        tanggalSakit = (TextView) findViewById(R.id.tanggalSakit);
        txtJudul = (TextView) findViewById(R.id.text_home);
        btnPilihTanggal = (Button) findViewById(R.id.btnPilihTanggal);
        View mapFragment = (View) findViewById(R.id.map);

        DatePickerDialog.OnDateSetListener listenerRiwayat = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tanggalSakit.setText("" + dayOfMonth + "/" + (month+1) + "/" + year);
                tanggal.set(year, month+1, dayOfMonth);
            }
        };

        LocalDateTime now = LocalDateTime.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listenerRiwayat,
                now.getYear(),
                now.getMonthValue()-1,
                now.getDayOfMonth());
        btnPilihTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        btnSimpanDataPasien = (Button) findViewById(R.id.btnTambahPasien);
        btnSimpanDataPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: validation
                namaLengkap = txtNamaLengkap.getText().toString();
                alamat = txtAlamat.getText().toString();
                StaticVars.pasienBaru = new Pasien(namaLengkap, alamat, tanggal, positions);
                StaticVars.databaseHelper.insertPasien(namaLengkap, alamat, tanggal);
                StaticVars.databaseHelper.insertPosition(positions);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnClearLocation = (Button) findViewById(R.id.btnClearLocation);
        btnClearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                positions.clear();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        float zoom = 10;
        this.googleMap = googleMap;
        LatLng jogja = new LatLng(-7.8426914,110.4619592); // starting point dari Jogja
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(jogja);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jogja, zoom));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jogja, zoom));
        sb = new StringBuffer();

        DatePickerDialog.OnDateSetListener listenerMarker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                markerText = "" + dayOfMonth + "/" + (month+1) + "/" + year;
                MarkerOptions newMarker = new MarkerOptions().title(markerText);
                newMarker.position(tempLatLng);
                Calendar tanggalPergi = Calendar.getInstance();
                tanggalPergi.set(year, month+1, dayOfMonth);
                Lokasi tempLokasi = new Lokasi(tempLatLng.latitude, tempLatLng.longitude, tanggalPergi);
                positions.add(tempLokasi);
                Marker marker = googleMap.addMarker(newMarker);
                marker.showInfoWindow();
            }
        };

        LocalDateTime now = LocalDateTime.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listenerMarker,
                now.getYear(),
                now.getMonthValue()-1,
                now.getDayOfMonth());
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                datePickerDialog.show();
                tempLatLng = latLng;
            }
        });

        // kalau diklik di marker, akan menghapus marker tersebut
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        // kalau ngelihat data
        if (StaticVars.pasienBaru != null) {
            txtNamaLengkap.setText(StaticVars.pasienBaru.getNamaLengkap());
            txtAlamat.setText(StaticVars.pasienBaru.getAlamat());
            Calendar cal = StaticVars.pasienBaru.getTanggal();
            StringBuilder sb = new StringBuilder();
            sb.append(cal.get(Calendar.DAY_OF_MONTH) + "/");
            sb.append(cal.get(Calendar.MONTH) + "/");
            sb.append(cal.get(Calendar.YEAR));
            tanggalSakit.setText(sb.toString());

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    // plot the markers
                    ArrayList<Lokasi> posisi = StaticVars.pasienBaru.getPositions();
                    MarkerOptions markers;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (int i=0; i<posisi.size(); i++) {
                        Log.d("debug", StaticVars.calToString(posisi.get(i).getTanggalPergi()));
                        markers = new MarkerOptions().title(StaticVars.calToString(posisi.get(i).getTanggalPergi()));
                        LatLng tmp = new LatLng(posisi.get(i).getLongitude(), posisi.get(i).getLatitude());
                        builder.include(tmp);
                        googleMap.addMarker(markers.position(tmp));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tmp, 10));
                    }
                    LatLngBounds bounds = builder.build();
                    Log.d("debug", ""+bounds);
                    int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.moveCamera(cu);
                }
            });

            btnSimpanDataPasien.setVisibility(View.INVISIBLE);
            btnClearLocation.setVisibility(View.INVISIBLE);
            btnPilihTanggal.setVisibility(View.INVISIBLE);
            txtJudul.setText("LIHAT LOKASI PASIEN COVID");
        } else {
            btnSimpanDataPasien.setVisibility(View.VISIBLE);
            btnClearLocation.setVisibility(View.VISIBLE);
            btnPilihTanggal.setVisibility(View.VISIBLE);
            txtJudul.setText("TAMBAH LOKASI PASIEN COVID");
        }
    }
}