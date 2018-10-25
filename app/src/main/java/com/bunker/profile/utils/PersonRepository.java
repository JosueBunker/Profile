package com.bunker.profile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bunker.profile.utils.db.DBController;
import com.bunker.profile.utils.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class PersonRepository implements Repository<Person> {

    private SQLiteOpenHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    private static PersonRepository instance;


    public static void init(Context context) { instance = new PersonRepository(context); }
    public static PersonRepository getInstance() { return instance; }

    private PersonRepository(Context context) {
        mDBHelper = new DBHelper(context);
        mDatabase = mDBHelper.getReadableDatabase();
    }

    @Override
    public void add(Person item) {
        ContentValues cv = item.getContentValues();
        mDatabase.insert(Person.Table.NAME, null, cv);
    }

    @Override
    public void update(Person item) {
        String id = item.getId().toString();
        mDatabase.update(Person.Table.NAME, item.getContentValues(), Person.Table.Cols.UUID + " = ?", new String[] {id});
    }

    @Override
    public void updateList(List<Person> items) {

    }

    @Override
    public void delete(Person item) {

    }

    @Override
    public List<Person> getAll() {
        List<Person> models = new ArrayList<>();
        Person.CustomCursor cursor = queryPerson(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                models.add(cursor.getCurrentIndex());
                cursor.moveToNext();
            }
            return models;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<Person> getSpecific(Person item) {
        return null;
    }


    /**
     * Method for creating a new voteCursor for a specific query,
     * this way is easier to convert the results to an array list of votes;
     *
     * @param where
     * @param whereArgs
     * @return
     */
    private Person.CustomCursor queryPerson(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                Person.Table.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null
        );

        return new Person.CustomCursor(cursor);
    }
}
