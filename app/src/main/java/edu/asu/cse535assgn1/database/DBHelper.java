package edu.asu.cse535assgn1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.asu.cse535assgn1.database.AccelerometerContract.AccelerometerEntry;

/**
 * Created by Jithin Roy on 3/4/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CSE_535_SENSOR.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AccelerometerEntry.TABLE_NAME + " (" +
                    AccelerometerEntry._ID + " INTEGER PRIMARY KEY," +
                    AccelerometerEntry.COLUMN_NAME_TIME_STAMP + REAL_TYPE + COMMA_SEP +
                    AccelerometerEntry.COLUMN_NAME_X_VALUE + REAL_TYPE + COMMA_SEP +
                    AccelerometerEntry.COLUMN_NAME_Y_VALUE + REAL_TYPE + COMMA_SEP +
                    AccelerometerEntry.COLUMN_NAME_Z_VALUE + REAL_TYPE +
            " )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
