package org.app.covid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class RefreshActivity extends AppCompatActivity {

    // nothing to do here. Just need to refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        Intent intentTambah = new Intent(RefreshActivity.this, MainActivity.class);
        startActivity(intentTambah);
    }
}