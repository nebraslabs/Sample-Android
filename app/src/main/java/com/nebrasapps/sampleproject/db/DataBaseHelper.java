package com.nebrasapps.sampleproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The Class DataBaseHelper.
 */
public class DataBaseHelper {

    /**
     * The db.
     */
    private SQLiteDatabase db;

    /**
     * The context.
     */
    private Context context;

    /**
     * The Constant dname.
     */
    private static final String dname = "NebrasApps";

    /**
     * The Constant dbver.
     */
    private static final int dbver = 1;



    /**
     * Instantiates a new data base helper.
     *
     * @param context the context
     */
    public DataBaseHelper(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        if(db!=null) {
            if (!db.isOpen()) {
                db = openHelper.getWritableDatabase();
            }
        }
        else
        {
            db = openHelper.getWritableDatabase();
        }
    }

    /**
     * Close database.
     */
    public void closeDatabase() {
        db.close();
    }

    /**
     * Sets the db.
     *
     * @param db the new db
     */
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    public SQLiteDatabase getDb() {
        return db;
    }




    /**
     * The Class OpenHelper.
     */
    private static class OpenHelper extends SQLiteOpenHelper {

        /**
         * Instantiates a new open helper.
         *
         * @param context the context
         */
        OpenHelper(Context context) {
            super(context, dname, null, dbver);
        }

        public void onCreate(SQLiteDatabase db) {
            System.out.println("creating tables");

            db.execSQL(SqlEntries.SQL_CREATE_USERS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

          /*  db.execSQL("DROP TABLE IF EXISTS CHAT");

            db.execSQL(SqlEntries.SQL_CREATE_CHAT_DETAILS);*/

        }
    }
}
