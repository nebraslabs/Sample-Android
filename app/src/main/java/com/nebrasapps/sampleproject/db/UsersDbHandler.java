package com.nebrasapps.sampleproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.sql.SQLException;

/**
 * Created by NebrasApps.com on 01/10/2017
 * https://github.com/nebrasapps/Sample-Android/
 */
public class UsersDbHandler extends SMContract.UsersEntry {
    private SQLiteDatabase database;

    /**
     * The _instance.
     */
    private static UsersDbHandler _instance = null;

    /**
     * The database helper.
     */
    private DataBaseHelper databaseHelper;
    /**
     * Instantiates a new user dao.
     *
     * @param context the context
     */
    public UsersDbHandler(Context context) {
        databaseHelper = new DataBaseHelper(context);
        database = databaseHelper.getDb();
    }

    /**
     * Gets the single instance of UserDAO.
     *
     * @param context the context
     * @return single instance of UserDAO
     */
    public static UsersDbHandler getInstance(Context context) {
        if (_instance == null) {
            _instance = new UsersDbHandler(context);
            return _instance;
        } else {
            return _instance;
        }
    }

    // insert if user not exists in db
    public long insertUser(String email,String password) throws SQLException {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);
        long dbAdded = database.insert(TABLE_NAME, _ID, cv);

        Log.i("userDBHAndler", "~User Record Inserted successfully.--" + dbAdded);
        return dbAdded;
    }



    public int checkUserExistence(String email) {
        int uid =-1;
        String selectquery = "select * from users where email="
                + "'"
                + email
                + "'" +  "";

        Cursor cursor = database.rawQuery(selectquery, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return -1;
        } else {
            while (cursor.moveToNext()) {
                uid = cursor.getInt(0);
            }
            cursor.close();
        }

        return uid;

    }
    public int checkUserValidation(String email,String password) {
        int uid =0;
        String selectquery = "select * from users where email="
                + "'"
                + email
                + "'" + " AND password= " + "'"
                + password
                + "'" + "";

        Cursor cursor = database.rawQuery(selectquery, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        } else {
            while (cursor.moveToNext()) {
                uid = cursor.getInt(0);
            }
            cursor.close();
        }

        return uid;

    }
}


