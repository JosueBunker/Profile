package com.bunker.profile.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bunker.profile.utils.Person;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DB_HELPER";

    private static final int  VERSION = 3;
    private static final String DATABASE_NAME = "profile.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Person.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Person.Table.NAME);
        onCreate(db);
    }
}
