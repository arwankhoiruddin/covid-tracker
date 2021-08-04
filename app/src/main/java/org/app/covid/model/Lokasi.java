package org.app.covid.model;

import java.util.Calendar;

public class Lokasi {
    private double longitude;
    private double latitude;
    private Calendar tanggalPergi;

    public Lokasi(double longitude, double latitude, Calendar tanggalPergi) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.tanggalPergi = tanggalPergi;
    }

    public Calendar getTanggalPergi() {
        return tanggalPergi;
    }

    public void setTanggalPergi(Calendar tanggalPergi) {
        this.tanggalPergi = tanggalPergi;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}
