package com.bunker.profile.utils;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.UUID;


/**
 * Explanaiton of the attributes
 *
 * UUID mID: is a unique id for the person, this attribute is used for searching by id in the local db.
 * long mUId: this id is used by the backend for identifying the persons.
 * String mFId: is the id of the facebook profile associated with this person.
 *
 *
 * int mSyncStatus: local integer flag for marking if the person information has been synced with the backend. 1 means yes, 0 means no.
 */
public class Person {

    private UUID mId;
    private long mUId;
    private String mFbId;
    private String mName;
    private String mNfcId;
    private String mQrCode;
    private String mGender;
    private String mEmail;
    private int mAge;
    private int mColor;
    private String mPhone;
    private int mSyncStatus;


    /**
     * Constructor for a Person, when the profile is filled out in the ProfileActivity.
     * Creates a new person with all the information that the user has given.
     *
     * @param name
     * @param nfcId
     * @param gender
     * @param email
     * @param age
     * @param phone
     */
    public Person(String name, String nfcId, String gender, String email, int age, String phone) {
        this.mId = UUID.randomUUID();
        this.mUId = 0;
        this.mFbId = "";
        this.mName = name;
        this.mNfcId = nfcId;
        this.mQrCode = "";
        this.mGender = gender;
        this.mEmail = email;
        this.mAge = age;
        this.mColor = 0;
        this.mPhone = phone;
        this.mSyncStatus = 0;
    }

    /**
     * Constructor for Person, this constructor is uses in when the person has only the QR and NFC. All the rest of
     * information is left blank.
     *
     * @param nfcId
     * @param qrCode
     */
    public Person(String nfcId, String qrCode) {
        this.mId = UUID.randomUUID();
        this.mUId = 0;
        this.mFbId = "";
        this.mName = "";
        this.mNfcId = nfcId;
        this.mQrCode = qrCode;
        this.mGender = "";
        this.mEmail = "";
        this.mAge = 0;
        this.mColor = 0;
        this.mPhone = "";
        this.mSyncStatus = 0;
    }

    /**
     * Private constructor, that is used when creating an Person from data read from the DB.
     * This method is only called from the CustomCursor of this class.
     *
     * @param id
     * @param name
     * @param nfcId
     * @param qrCode
     * @param gender
     * @param email
     * @param age
     * @param phone
     */
    private Person(UUID id, String name, String nfcId, String qrCode, String gender, String email, int age, int color,  String phone, int syncStatus) {
        this.mId = id;
        this.mUId = 0;
        this.mFbId = "";
        this.mName = name;
        this.mNfcId = nfcId;
        this.mQrCode = qrCode;
        this.mGender = gender;
        this.mEmail = email;
        this.mAge = age;
        this.mColor = color;
        this.mPhone = phone;
        this.mSyncStatus = syncStatus;
    }

    public UUID getId() { return mId; }


    /**
     * Class for defining the structure of the table person.
     */
    public static final class Table {
        public static final String NAME = "person";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String NFC_ID = "nfc_id";
            public static final String QR_CODE = "qr_code";
            public static final String GENDER = "gender";
            public static final String EMAIL = "email";
            public static final String AGE = "age";
            public static final String COLOR = "color";
            public static final String PHONE = "phone";
            public static final String SYNCSTATUS = "sync_status";
        }
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Table.Cols.UUID, this.mId.toString());
        cv.put(Table.Cols.NAME, this.mName);
        cv.put(Table.Cols.NFC_ID, this.mNfcId);
        cv.put(Table.Cols.QR_CODE, this.mQrCode);
        cv.put(Table.Cols.GENDER, this.mGender);
        cv.put(Table.Cols.EMAIL, this.mEmail);
        cv.put(Table.Cols.AGE, this.mAge);
        cv.put(Table.Cols.COLOR, this.mColor);
        cv.put(Table.Cols.PHONE, this.mPhone);
        cv.put(Table.Cols.SYNCSTATUS, this.mSyncStatus);
        return cv;
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Table.NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                Table.Cols.UUID + ", " +
                Table.Cols.NAME + " VARCHAR(20), " +
                Table.Cols.NFC_ID + " VARCHAR(20), " +
                Table.Cols.QR_CODE + " VARCHAR(20), " +
                Table.Cols.GENDER + " VARCHAR(20), " +
                Table.Cols.EMAIL + " VARCHAR(20), " +
                Table.Cols.AGE + " INTEGER, " +
                Table.Cols.COLOR + " INTEGER, " +
                Table.Cols.PHONE + " VARCHAR(20), " +
                Table.Cols.SYNCSTATUS +" INTEGER)");
    }


    public static class CustomCursor extends CursorWrapper {

        public CustomCursor(Cursor cursor) {
            super(cursor);
        }

        public Person getCurrentIndex() {
            String uuid = getString(getColumnIndex(Person.Table.Cols.UUID));
            String name = getString(getColumnIndex(Table.Cols.NAME));
            String nfcId = getString(getColumnIndex(Table.Cols.NFC_ID));
            String qrCode = getString(getColumnIndex(Table.Cols.QR_CODE));
            String gender = getString(getColumnIndex(Table.Cols.GENDER));
            String email = getString(getColumnIndex(Table.Cols.EMAIL));
            int age = getInt(getColumnIndex(Table.Cols.AGE));
            int color = getInt(getColumnIndex(Table.Cols.COLOR));
            String phone = getString(getColumnIndex(Table.Cols.PHONE));
            int syncStatus = getInt(getColumnIndex(Table.Cols.SYNCSTATUS));

            return new Person(UUID.fromString(uuid), name, nfcId, qrCode, gender, email, age, color, phone, syncStatus);
        }
    }

    public String toString() {
        return mId.toString() + " " + mName + " " + mNfcId + " " + mQrCode + " " + mGender + " " + mEmail + " " + String.valueOf(mAge) + " " + mPhone;
    }

}
