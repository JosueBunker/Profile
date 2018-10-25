package com.bunker.profile.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bunker.profile.R;
import com.bunker.profile.utils.NFCUtils;
import com.bunker.profile.utils.Person;
import com.bunker.profile.utils.PersonRepository;
import com.bunker.profile.utils.db.DBController;
import com.bunker.profile.view.activity.ProfileActivity;
import com.bunker.profile.view.dialog.StationDialog;

public class ReadNfcActivity extends AppCompatActivity {

    private static final String TAG = "READ_NFC_ACTIVITY";

    public static final String FROM_MAIN = "MAIN_ACTIVYT";
    public static final String FROM_QR = "QR_ACTIVITY";

    private NfcAdapter mNfcAdapter;

    private String mCalledFrom;
    private String mQrCode;

    private String mNfcId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readnfc);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mCalledFrom = getIntent().getStringExtra("FROM");
        if (mCalledFrom.equals(FROM_QR)) {
            mQrCode = getIntent().getStringExtra("QR_CODE");
        }
    }

    /**
     * Method for handling the action of reading a nfc card.
     * Is called when the device has successfully read a card.
     */
    public void handleNfcId() {
        if (mCalledFrom.equals(FROM_QR)) {
            PersonRepository.getInstance().add(new Person(mNfcId, mQrCode));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(ProfileActivity.NFC_KEY, mNfcId);
            startActivity(intent);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);

    }

    @Override
    public void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            MifareUltralight miFare = MifareUltralight.get(tag);
            mNfcId = NFCUtils.getTagId(miFare);
            if (!mNfcId.equals("")) {
                handleNfcId();
            }
        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{new String[]{"android.nfc.tech.Ndef"}, new String[]{"android.nfc.tech.MifareUltralight"}};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
