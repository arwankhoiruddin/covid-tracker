package org.app.covid.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.app.covid.R;

import java.util.ArrayList;
import java.util.Calendar;

public class LokasiListAdapter extends ArrayAdapter<Lokasi> {
    private static final String TAG = "LokasiListAdapter";

    private Context mContext;
    int mResource;

    public LokasiListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Lokasi> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        double latitude = getItem(position).getLatitude();
        double longitude = getItem(position).getLongitude();
        Calendar tanggalPergi = getItem(position).getTanggalPergi();

        Lokasi lokasi = new Lokasi(latitude, longitude, tanggalPergi);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textLatitude = (TextView) convertView.findViewById(R.id.latitude);
        TextView textLongitude = (TextView) convertView.findViewById(R.id.longitude);

        textLatitude.setText(latitude + "");
        textLongitude.setText(longitude + "");

        return convertView;
    }
}
