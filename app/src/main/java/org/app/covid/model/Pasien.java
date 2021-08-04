package org.app.covid.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Pasien {
    private String namaLengkap;
    private String alamat;
    private Calendar tanggalSakit;
    private ArrayList<Lokasi> positions;

    public Pasien(String namaLengkap, String alamat, Calendar tanggalSakit, ArrayList<Lokasi> positions) {
        this.namaLengkap = namaLengkap;
        this.alamat = alamat;
        this.tanggalSakit = tanggalSakit;
        this.positions = positions;
    }

    public ArrayList<Lokasi> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Lokasi> positions) {
        this.positions = positions;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Calendar getTanggal() {
        return tanggalSakit;
    }

    public void setTanggal(Calendar tanggal) {
        this.tanggalSakit = tanggal;
    }
}
