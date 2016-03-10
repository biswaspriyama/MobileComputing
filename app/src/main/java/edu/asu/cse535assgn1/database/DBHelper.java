package edu.asu.cse535assgn1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.asu.cse535assgn1.database.AccelerometerContract.AccelerometerEntry;

/**
 * Created by Jithin Roy on 3/4/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;


    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private String tableName = "";


    public DBHelper(Context context, String accelorometerTableName) {
        super(context, accelorometerTableName, null, DATABASE_VERSION);
        tableName = accelorometerTableName;
        Log.i("Test",accelorometerTableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Test","Oncreate");
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE AccelerometerTable " + " (" +
                        AccelerometerEntry._ID + " INTEGER PRIMARY KEY," +
                        AccelerometerEntry.COLUMN_NAME_TIME_STAMP + REAL_TYPE + COMMA_SEP +
                        AccelerometerEntry.COLUMN_NAME_X_VALUE + REAL_TYPE + COMMA_SEP +
                        AccelerometerEntry.COLUMN_NAME_Y_VALUE + REAL_TYPE + COMMA_SEP +
                        AccelerometerEntry.COLUMN_NAME_Z_VALUE + REAL_TYPE +
                        " )";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
