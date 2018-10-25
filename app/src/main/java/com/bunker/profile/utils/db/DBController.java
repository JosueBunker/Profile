package com.bunker.profile.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bunker.profile.utils.Person;

public class DBController {

    private static final String TAG = "DB_CONTROLLER";

    public static DBController instance;

    public SQLiteOpenHelper mDBHelper;
    public SQLiteDatabase mDatabase;

    public static void init(Context context) { instance = new DBController(context); }
    public static DBController getInstance() { return instance; }

    private DBController(Context context) {
        mDBHelper = new DBHelper(context);
        mDatabase = mDBHelper.getWritableDatabase();
    }


    /**
     * Method for inserting a single Checkin to the database.
     *
     * @param p
     */
    public void insert(Person p) {
        ContentValues cv = p.getContentValues();
        long s = mDatabase.insert(Person.Table.NAME, null, cv);
        Log.i(TAG, p.toString());
    }



    /**
     *  Deletes all info in the database
     */
    public void dropPersonTable() {
        mDatabase.execSQL("DROP TABLE IF EXISTS " + Person.Table.NAME);
        Person.createTable(mDatabase);
        Log.i(TAG, "Tabla de Person borrada");
    }
}
