package org.app.covid.model;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class StaticVars {
    public static Pasien pasienBaru;
    public static DatabaseHelper databaseHelper;
    public static PasienListAdapter adapterPasien;
    public static ArrayList<Pasien> pasienKosong;

    public static void updateData() {
        // update data pasien from database
        Log.d("debug", "Updating the data");
        StaticVars.pasienKosong = new ArrayList<>();
        Cursor res = StaticVars.databaseHelper.getAllPasien();
        if (res.moveToFirst()) {
            res.moveToFirst();
            do {
                String namaPasien = res.getString(1);
                String alamatPasien = res.getString(2);
                String tanggalSakit = res.getString(3);
                String[] tanggals = tanggalSakit.split("/");
                Calendar sakit = Calendar.getInstance();
                sakit.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tanggals[0]));
                sakit.set(Calendar.MONTH, Integer.parseInt(tanggals[1]));
                sakit.set(Calendar.YEAR, Integer.parseInt(tanggals[2]));
                StaticVars.pasienKosong.add(new Pasien(namaPasien, alamatPasien, sakit, null));
            } while (res.moveToNext());
        }
        Log.d("debug", "Jumlah pasien: " + pasienKosong.size());
    }

    public static String calToString(Calendar cal) {
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.DAY_OF_MONTH) + "/");
        sb.append(cal.get(Calendar.MONTH) + "/");
        sb.append(cal.get(Calendar.YEAR));
        return sb.toString();
    }

    public static Calendar stringToCal(String tanggal) {
        Calendar cal = Calendar.getInstance();
        String[] tanggals = tanggal.split("/");
        cal.set(Calendar.YEAR, Integer.parseInt(tanggals[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(tanggals[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tanggals[0]));
        return cal;
    }
}