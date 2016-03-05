package edu.asu.cse535assgn1.database;

import android.content.Context;

import java.util.List;

import edu.asu.cse535assgn1.models.Accelerometer;

/**
 * Handles all database related functionalities.
 *
 * Created by Jithin Roy on 3/4/16.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private DatabaseManager() {
        // Added to avoid multiple instances
        // This is a singleton
    }

    public static DatabaseManager sharedInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
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

    }

    /**
     * Returns a list of recent 'count' of accelerometer data from database.
     *
     * @param count Number of records to be fetched.
     * @return List of accelerometer values.
     */
    public List<Accelerometer> fetchRecentAccelerometerData(int count) {
        return null;
    }

    /**
     * Returns the location where the database file is currently stored.
     *
     * @return Absolute path of database location.
     */
    public String databasePath() {
        return null;
    }

    //==============================================================================
    //                          Internals
    //==============================================================================

    private void createDatabase() {

    }

    private void openDatabase() {

    }

    private void closeDatabase() {

    }
}
