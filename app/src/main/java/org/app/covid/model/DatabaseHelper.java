package org.app.covid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dataPasien.db";
    public static final String TABLE_PASIEN = "tablePasien";
    public static final String TABLE_LOCATION = "tableLocation";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("debug", "database helper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PASIEN +
                "(ID_PASIEN INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAMA_PASIEN TEXT," +
                "ALAMAT_PASIEN TEXT," +
                "TANGGAL_SAKIT DATETIME)");
        db.execSQL("CREATE TABLE " + TABLE_LOCATION +
                "(ID_LOCATION INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID_PASIEN INTEGER," +
                "TANGGAL_PERGI DATETIME," +
                "LATITUDE DOUBLE," +
                "LONGITUDE DOUBLE)");
    }

    public Cursor getAllPasien() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_PASIEN, null);
    }

    public Cursor getAllPositions(int id_pasien) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select TANGGAL_PERGI, LATITUDE, LONGITUDE from " + TABLE_LOCATION + " where ID_PASIEN= ?", new String[] {id_pasien+""});
    }

    public Cursor getPasien(String namaLengkap) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_PASIEN + " where NAMA_PASIEN= ?", new String[] {namaLengkap});

    }

    public boolean insertPasien(String namaLengkap, String alamat, Calendar tanggalSakit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAMA_PASIEN", namaLengkap);
        contentValues.put("ALAMAT_PASIEN", alamat);

        String tmpTanggal = tanggalSakit.get(Calendar.DAY_OF_MONTH) + "/" +
                tanggalSakit.get(Calendar.MONTH) + "/" +
                tanggalSakit.get(Calendar.YEAR);
        contentValues.put("TANGGAL_SAKIT", tmpTanggal);

        long result = db.insert(TABLE_PASIEN, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public void clearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PASIEN);
        db.execSQL("delete from " + TABLE_LOCATION);
    }

    public int deleteData(String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID_PASIEN from " + TABLE_PASIEN + " where NAMA_PASIEN = ?", new String[] {nama});
        res.moveToFirst();
        int id_pasien = res.getInt(0);
        db.delete(TABLE_PASIEN, "ID_PASIEN = ?", new String[] {""+ id_pasien});
        db.delete(TABLE_LOCATION, "ID_PASIEN = ?", new String[] {"" + id_pasien});
        return 1;
    }

    public boolean insertPosition(ArrayList<Lokasi> positions) {
        boolean berhasil = true;
        int idPasienTerakhir;

        // find number of data first
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID_PASIEN from " + TABLE_PASIEN, null);
        res.moveToLast();
        idPasienTerakhir = res.getInt(0);

        // insert all positions
        for (int i=0; i<positions.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID_PASIEN", idPasienTerakhir);

            Calendar tanggalPergi = positions.get(i).getTanggalPergi();
            String tmpTanggal = tanggalPergi.get(Calendar.DAY_OF_MONTH) + "/" +
                    tanggalPergi.get(Calendar.MONTH) + "/" +
                    tanggalPergi.get(Calendar.YEAR);
            contentValues.put("TANGGAL_PERGI", tmpTanggal);
            contentValues.put("LATITUDE", positions.get(i).getLatitude());
            contentValues.put("LONGITUDE", positions.get(i).getLongitude());
            long result = db.insert(TABLE_LOCATION, null, contentValues);
            if (result == -1)
                berhasil = berhasil && false;
            else
                berhasil = berhasil && true;
        }
        return berhasil;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASIEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }
}
