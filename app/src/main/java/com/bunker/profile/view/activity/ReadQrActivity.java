package com.bunker.profile.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bunker.profile.R;
import com.bunker.profile.utils.Settings;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class ReadQrActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckBox;

    private boolean allowUserQR = Settings.getAllowUserQr();

    @Override
    public void onCreate(Bundle savedInstanceStatus) {
        super.onCreate(savedInstanceStatus);
        setContentView(R.layout.activity_readqr);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        resultTextView = (TextView) findViewById(R.id.result_text_view);
        flashlightCheckBox = (CheckBox) findViewById(R.id.flashlight_checkbox);
        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setTorchEnabled(isChecked);
            }
        });
    }

    void showErrorDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.scan_qr))
                .setMessage(getString(R.string.error_failed_to_scan_qr))
                .setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        qrCodeReaderView.startCamera();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void openNfcActivity(String qrcode, String name, String userCode) {
        Intent intent = new Intent(this, ReadNfcActivity.class);
        intent.putExtra("FROM", ReadNfcActivity.FROM_QR);
        intent.putExtra("QRCODE", qrcode);
//        intent.putExtra("NAME", name);
//        intent.putExtra("USERCODE", userCode);
        Log.i("ENTOOROR", "TROEOROEROEOR");
        startActivity(intent);
    }

    @Override
    public void onQRCodeRead(String qrcode, PointF[] points) {
        qrCodeReaderView.stopCamera();
        Toast.makeText(this, qrcode, Toast.LENGTH_SHORT).show();
//        resultTextView.setText(text);
        String[] tmps = qrcode.split("-");
        if (qrcode.length() == 10 && !qrcode.contains("-")) { // Scanned List code
            openNfcActivity(qrcode, "", "");
        } else if (tmps.length == 2 && allowUserQR) { // Scaned User Code
            String name = tmps[0].trim();
            String userCode = tmps[1].trim();
            openNfcActivity(qrcode, "", "");
        } else {
            showErrorDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
