package edu.asu.cse535assgn1.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;



import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import edu.asu.cse535assgn1.database.DatabaseManager;
import edu.asu.cse535assgn1.models.Accelerometer;

public class SensorHandlerService extends Service implements SensorEventListener {


    private SensorManager accelManager;
    private Sensor senseAccel;

    int index = 0;


    List<Accelerometer> accelerometerList = new ArrayList<>();

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        accelManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //================================================================================
    //              Data manipulation methods
    //================================================================================

//    public void insertData(float x, float y, float z){
//        try {
//
//            String path = Environment.getExternalStorageDirectory() + "/databaseFolder/myDB";
//            db = SQLiteDatabase.openOrCreateDatabase(path, null);
//            db.beginTransaction();
//            try {
//                java.util.Date date= new java.util.Date();
//                db.execSQL( "insert into "+tableName+" (Timestamp, X, Y, Z) values ('"+new Timestamp(date.getTime())+"','"+x+"', '"+y+"', '"+z+"' );" );
//
//                db.setTransactionSuccessful(); //commit your changes
//            } catch (SQLiteException e) {
//                //report problem
//            } finally {
//                db.endTransaction();
//            }
//        }
//        catch (SQLException e){
//
//        }
//    }

    private void insertData(List<Accelerometer>list) {
        if (DatabaseManager.sharedInstance().isDBAvialable()) {
            DatabaseManager.sharedInstance().saveAccelerometerList(list);
        }
        list.clear();
    }

    //================================================================================
    //              Accelerometer methods
    //================================================================================

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            Accelerometer acc = new Accelerometer();
            acc.setTimestamp(sensorEvent.timestamp);
            acc.setX(sensorEvent.values[0]);
            acc.setY(sensorEvent.values[1]);
            acc.setZ(sensorEvent.values[2]);

            accelerometerList.add(acc);


            if(index >= 127){
                index = 0;
                accelManager.unregisterListener(this);
                insertData(accelerometerList);
                accelManager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
