package edu.asu.cse535assgn1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.asu.cse535assgn1.models.Accelerometer;

/**
 * Handles all database related functionalities.
 *
 * Created by Jithin Roy on 3/4/16.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private DBHelper mHelper;
    private Context context;

    private String TAG = "DatabaseManager";

    private DatabaseManager() {
        // Added to avoid multiple instances
        // This is a singleton
    }

    public static DatabaseManager sharedInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseManager();
            instance.initializeDB(context);
        }
        return instance;
    }

    private void initializeDB(Context context) {
        this.context = context;
        mHelper = new DBHelper(context);
    }


    //==============================================================================
    //                       Public methods
    //==============================================================================

    /**
     * Saves the list of accelerometer data to database.
     *
     * @param list
     */
    public void saveAccelerometerList(List<Accelerometer> list) {


        SQLiteDatabase db = openDatabase();

        for (Accelerometer acc: list) {

            ContentValues values = new ContentValues();
            values.put(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_TIME_STAMP, acc.getTimestamp());
            values.put(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_X_VALUE, acc.getX());
            values.put(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Y_VALUE, acc.getY());
            values.put(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Z_VALUE, acc.getZ());

            long newRowId;
            newRowId = db.insert(AccelerometerContract.AccelerometerEntry.TABLE_NAME,
                    null,
                    values);
        }
        db.close();

    }

    /**
     * Returns a list of recent 'count' of accelerometer data from database.
     *
     * @param count Number of records to be fetched.
     * @return List of accelerometer values.
     */
    public List<Accelerometer> fetchRecentAccelerometerData(int count) {
        SQLiteDatabase db = openDatabase();

        String[] projection = {
                AccelerometerContract.AccelerometerEntry._ID,
                AccelerometerContract.AccelerometerEntry.COLUMN_NAME_TIME_STAMP,
                AccelerometerContract.AccelerometerEntry.COLUMN_NAME_X_VALUE,
                AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Y_VALUE,
                AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Z_VALUE,

        };
        String limit = Integer.toString(count);
        Cursor cursor = db.query(
                AccelerometerContract.AccelerometerEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                AccelerometerContract.AccelerometerEntry.COLUMN_NAME_TIME_STAMP,
                limit
        );

        List<Accelerometer> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Accelerometer acc = new Accelerometer();

            float timestamp = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_TIME_STAMP)
            );
            float x = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_X_VALUE)
            );
            float y = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Y_VALUE)
            );
            float z = cursor.getFloat(
                    cursor.getColumnIndexOrThrow(AccelerometerContract.AccelerometerEntry.COLUMN_NAME_Z_VALUE)
            );
            acc.setTimestamp(timestamp);
            acc.setX(x);
            acc.setY(y);
            acc.setZ(z);
            result.add(acc);
            cursor.moveToNext();
        }
        return result;
    }

    /**
     * Returns the location where the database file is currently stored.
     *
     * @return Absolute path of database location.
     */
    public String databasePath() {
        File file = context.getDatabasePath(DBHelper.DATABASE_NAME);
        Log.i(TAG, "DB path = "+file.getAbsolutePath());
        return null;
    }

    //==============================================================================
    //                          Internals
    //==============================================================================

    private SQLiteDatabase openDatabase() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        return db;
    }

    private void closeDatabase(SQLiteDatabase db) {
        db.close();
    }
}
