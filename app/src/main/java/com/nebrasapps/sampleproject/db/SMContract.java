package com.nebrasapps.sampleproject.db;

import android.provider.BaseColumns;

public final class SMContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SMContract() {
    }




    public static abstract class UsersEntry  implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_EMAIL= "email";
        public static final String COLUMN_PASSWORD = "password";

        }



}
