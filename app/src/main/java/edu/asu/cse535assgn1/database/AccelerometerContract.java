package edu.asu.cse535assgn1.database;


import android.provider.BaseColumns;

/**
 * Created by Jithin Roy on 3/5/16.
 */
public final class AccelerometerContract {

    public AccelerometerContract() {}

    public static abstract  class AccelerometerEntry implements BaseColumns {

        public static final String COLUMN_NAME_TIME_STAMP = "timestamp";
        public static final String COLUMN_NAME_X_VALUE = "xvalue";
        public static final String COLUMN_NAME_Y_VALUE = "yvalue";
        public static final String COLUMN_NAME_Z_VALUE = "zvalue";
    }
}
