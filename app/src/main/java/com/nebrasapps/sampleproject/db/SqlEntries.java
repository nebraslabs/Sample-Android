package com.nebrasapps.sampleproject.db;



public class SqlEntries {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE_NOT_NULL = " TEXT NOT NULL";
    private static final String COMMA_SEP = ",";

    // CREATE QUERY FOR USER TABLE
    public static final String SQL_CREATE_USERS =
            "CREATE TABLE " + SMContract.UsersEntry.TABLE_NAME + " (" +
                    SMContract.UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SMContract.UsersEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    SMContract.UsersEntry.COLUMN_PASSWORD + TEXT_TYPE +
                    // Any other options for the CREATE command
                    " )";

}
