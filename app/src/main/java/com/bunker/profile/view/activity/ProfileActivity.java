package com.bunker.profile.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bunker.profile.R;
import com.bunker.profile.utils.Person;
import com.bunker.profile.utils.PersonRepository;
import com.bunker.profile.utils.db.DBController;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "PROFILE_ACTIVITY";
    public static final String NFC_KEY = "NFC_KEY";

    private EditText mEditName;
    private Spinner mSpinnerGender;
    private EditText mEditEmail;
    private EditText mEditAge;
    private EditText mEditPhone;
    private Button mButtonSave;

    private String mNfcId;
    private String mGender;
    private String mName;
    private String mEmail;
    private String mAge;
    private String mPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        mNfcId = getIntent().getStringExtra(NFC_KEY);

        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditAge = (EditText) findViewById(R.id.edit_age);
        mEditPhone = (EditText) findViewById(R.id.edit_phone);

        mSpinnerGender = (Spinner) findViewById(R.id.spinner_gender);
        mSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGender = mSpinnerGender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButtonSave = (Button) findViewById(R.id.button_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = mEditName.getText().toString();
                mEmail = mEditEmail.getText().toString();
                mAge = mEditAge.getText().toString();
                mPhone = mEditPhone.getText().toString();
                if (validateData()) {
                    DBController.getInstance().insert(new Person(mName, mNfcId, mGender, mEmail, Integer.parseInt(mAge), mPhone));
                    PersonRepository.getInstance().add(new Person(mName, mNfcId, mGender, mEmail, Integer.parseInt(mAge), mPhone));
                }
                Toast.makeText(getApplicationContext(), "Usuario Registrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateData() {
        if (mGender.equals(getResources().getStringArray(R.array.gender)[0])) {
            Toast.makeText(this, "Seleccione un género", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mAge.equals(""))
            mAge = "0";

        return true;
    }

}
