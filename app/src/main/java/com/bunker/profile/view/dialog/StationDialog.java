package com.bunker.profile.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bunker.profile.R;
import com.bunker.profile.view.activity.ReadNfcActivity;
import com.bunker.profile.utils.Person;
import com.bunker.profile.utils.db.DBController;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class StationDialog extends DialogFragment {

    private static final String TAG = "SETTINGS_DIALOG";
    private ReadNfcActivity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.password_dialog, null))
                .setTitle("Cambio Estacion")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) getDialog().findViewById(R.id.edit_text_password);
                        String password = editText.getText().toString();
                        if(password.equals("bunker123")){
                            Log.i("Dialog","clave ok");
                            changeStationDialog((ReadNfcActivity)getActivity());
                        }else{
                            Log.i("Dialog","clave okn't");
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StationDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void changeStationDialog(final ReadNfcActivity activity){
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.station_dialog, null);
        builder.setView(dialogLayout)
                .setTitle("Elija su estación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Button button = dialogLayout.findViewById(R.id.button_delete_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Alerta")
                        .setMessage("Desea eliminar la data?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBController.getInstance().dropPersonTable();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
        Button buttonSaveData  = dialogLayout.findViewById(R.id.button_save_data);
        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, new Date(System.currentTimeMillis()).toString());
                writeFile();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }



    public void writeFile() {
        File file  = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ProfileData " + new Date(System.currentTimeMillis()).toString() + ".csv");
        Log.i(TAG, file.getParent());
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = DBController.getInstance().mDBHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM " + Person.Table.NAME, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Log.i(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
            Toast.makeText(activity, file.getParent(), Toast.LENGTH_SHORT).show();
        } catch (Exception sqlEx) {
            Log.e("ERRRRRROOOOOORRRR", sqlEx.getMessage(), sqlEx);
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
