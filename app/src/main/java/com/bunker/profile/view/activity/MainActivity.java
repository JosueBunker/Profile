package com.bunker.profile.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bunker.profile.R;
import com.bunker.profile.utils.PersonRepository;
import com.bunker.profile.utils.db.DBController;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSION_CAMERA = 2;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBController.init(this);
        PersonRepository.init(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        checkForServicesAndPermissions();
    }

    private void checkForServicesAndPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permision for write external storage not Granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permision for camera not Granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
        } else if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, getResources().getString(R.string.NfcNotEnabled), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            initUi();
        }
    }

    public void initUi() {
        LinearLayout splash = (LinearLayout) findViewById(R.id.splash_activity);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_main_activity);
        splash.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForServicesAndPermissions();
                } else {
                    Log.d(TAG, "Permission denied.");
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_LONG).show();
                }
            }
            case MY_PERMISSION_CAMERA: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForServicesAndPermissions();
                } else {
                    Log.d(TAG, "Permission denied.");
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_LONG).show();
                    checkForServicesAndPermissions();
                }
            }
        }
    }


    public void openNfcActivity(View v) {
        Intent intent = new Intent(this, ReadNfcActivity.class);
        startActivity(intent);
    }

    public void openQrActivity(View v) {
        Intent intent = new Intent(this, ReadQrActivity.class);
        startActivity(intent);
    }

    public void openReportsActivity(View v) {
        Intent intent = new Intent(this, ReportsActivity.class);
        startActivity(intent);
    }

}
