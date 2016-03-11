package edu.asu.cse535assgn1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.asu.cse535assgn1.database.DatabaseManager;
import edu.asu.cse535assgn1.models.Accelerometer;
import edu.asu.cse535assgn1.models.Patient;
import edu.asu.cse535assgn1.sensor.SensorHandlerService;
import edu.asu.cse535assgn1.webservices.DownloadWebservice;
import edu.asu.cse535assgn1.webservices.UploadWebservice;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * @author Jithin Roy
 */
public class MainActivity extends AppCompatActivity {

    private GraphView mGraphView;
    private GridLabelRenderer graphProperties;
    private boolean isTableMade = false;

    private TextView mPatientIDTextView;
    private TextView mPatientNameTextView;
    private TextView mPatientAgeTextView;
    private RadioGroup mSexRadioGroup;

    private Patient mPatient;

    private int mIndex = 0;
    private int mGraphIndex = 0;
    private Handler mHandler = new Handler();

    private List<Float> mCurrentGraphValues = new ArrayList<>() ;
    private List<Float> mCurrentGraphValues2 = new ArrayList<>() ;
    private List<Accelerometer> accelerometerList = new ArrayList<Accelerometer>() ;

    LineGraphSeries<DataPoint> mSeriesX;
    LineGraphSeries<DataPoint> mSeries2;
    LineGraphSeries<DataPoint> mSeries3;


    private UploadWebservice uploadService;
    private DownloadWebservice downloadService;

    String TAG = "SENSOR";

    String tableName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);


        this.mGraphView = new GraphView(this);
        ViewGroup layout = (ViewGroup) findViewById(R.id.baseLayout);
        this.mGraphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(this.mGraphView);

        this.mPatientIDTextView = (TextView) findViewById(R.id.patientId);
        this.mPatientNameTextView = (TextView) findViewById(R.id.patientName);
        this.mPatientAgeTextView = (TextView) findViewById(R.id.patientAge);
        this.mSexRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mSeriesX = new LineGraphSeries<DataPoint>();
        mSeriesX.setColor(Color.BLUE);
        mGraphView.addSeries(mSeriesX);
        mSeries2 = new LineGraphSeries<DataPoint>();
        mSeries2.setColor(Color.WHITE);
        mGraphView.addSeries(mSeries2);
        mSeries3 = new LineGraphSeries<DataPoint>();
        mSeries3.setColor(Color.YELLOW);
        mGraphView.addSeries(mSeries3);


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mGraphView);
        staticLabelsFormatter.setHorizontalLabels(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        mGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        Intent sensorService = new Intent(MainActivity.this, SensorHandlerService.class); // PSK: this could be an issue. Service should start at OnStartClick.
        startService(sensorService);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the current state of animation so that we can
        // restart on orientation change.
        savedInstanceState.putBoolean("IsAnimating", this.isTableMade);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Check if the graph was already animating and call the animation function.
        this.isTableMade = savedInstanceState.getBoolean("IsAnimating");
        if (this.isTableMade) {
            startAnimation();
        }
    }

    public void startButtonClicked(View view) {

        // Dismiss the keyboard first.
        View keyview = this.getCurrentFocus();
        if (keyview != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyview.getWindowToken(), 0);
        }

        if (isTableMade == false) {

            String id = this.mPatientIDTextView.getText().toString();
            String name = this.mPatientNameTextView.getText().toString();
            String ageString = this.mPatientAgeTextView.getText().toString();
            int age = 0;
            if (ageString.equals("") == false && ageString.length()<=3) {
                try {
                    age = Integer.parseInt(ageString);
                } catch (Exception ex) {
                    Toast toast = Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT);
                    toast.show();
                }

            } else {

                Toast toast = Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT);
                toast.show();
            }


            boolean isMale = true;
            if (this.mSexRadioGroup.getCheckedRadioButtonId() == R.id.radio_female) {
                   isMale = false;
            }
            this.mPatient = new Patient(id,name,age,isMale);

            // Check if all values are entered.
            if (age > 0 &&
                    id != null && !id.equals("")
                    && name != null && !name.equals("")) {

                String sexString = "Female";
                if (isMale) {
                    sexString = "Male";
                }
                tableName = name + "_" + id + "_" + age + "_" + sexString;
                DatabaseManager.sharedInstance().createTable(tableName, this);
                startAnimation();
                showMessage();

            } else {
                Toast toast = Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        else{
            update();
        }
    }

    public void stopButtonClicked(View view) {

        //this.isTableMade = false;
        mIndex = 0;
        mGraphIndex = 0;
        mSeriesX.resetData(new DataPoint[]{new DataPoint(0, 0)});
        mSeries2.resetData(new DataPoint[]{new DataPoint(0, 0)});
        mSeries3.resetData(new DataPoint[]{new DataPoint(0, 0)});
    }

    /**
     * Initiate the download of database from the server.
     * @param view
     */
    public void downloadButtonClicked(View view) {

    }

    /**
     * Initiate upload of database to the server.
     * @param view
     */
    public void uploadButtonClicked(View view) {
        uploadService = new UploadWebservice(DatabaseManager.sharedInstance().databaseAbsolutePath());
        uploadService.startUpload();
    }

    /**
     * @author Prameet Kohli
     */
    private void showMessage() {
        String message = null;

        if (this.mPatient.getId() != null && this.mPatient.getId().equals("") == false) {
            message = " Patient Id: " + this.mPatient.getId();

            if (this.mPatient.getName() != null && this.mPatient.getName().equals("") == false) {
                message += " Name: " + this.mPatient.getName();
            }

            if (this.mPatient.getAge() > 0 ) {
                message += " Age: " + this.mPatient.getAge();
            }

            if (this.mPatient.isMale()) {
                message += " (Male)";
            } else {
                message += " (Female)";
            }
        }
        if (message != null) {
            Toast.makeText(this,message, Toast.LENGTH_LONG).show();
        }
    }

    private Runnable mUpdateGraph = new Runnable() {
        public void run() {
            update();
            if (isTableMade) {
                mHandler.postDelayed(mUpdateGraph, 10000);
            }
        }
    };

    private void startAnimation() {
        this.isTableMade = true;
        this.mHandler.postDelayed(mUpdateGraph, 10);
    }

    private void update() {

       if (this.isTableMade == true) {

           int i;
           accelerometerList.clear();
           accelerometerList.addAll(0, DatabaseManager.sharedInstance().fetchRecentAccelerometerData(10));

           DataPoint[] toAdd_X = new DataPoint[accelerometerList.size()];
           DataPoint[] toAdd_Y = new DataPoint[accelerometerList.size()];
           DataPoint[] toAdd_Z = new DataPoint[accelerometerList.size()];
           Log.i("MainActivity", "Showing values count "+accelerometerList.size());

           for(i=0; i< accelerometerList.size(); i++){

               toAdd_X[i] = new DataPoint(i,accelerometerList.get(i).getX());
               toAdd_Y[i] = new DataPoint(i,accelerometerList.get(i).getY());
               toAdd_Z[i] = new DataPoint(i,accelerometerList.get(i).getZ());

           }
           if(accelerometerList.size() > 0) {

               mSeriesX.resetData(toAdd_X);
               mSeries2.resetData(toAdd_Y);
               mSeries3.resetData(toAdd_Z);

           }
           //mGraphIndex+=i;
           Log.v(TAG, "Graph Index " + mGraphIndex);
       }
        Log.v(TAG, "Update Called. Size: " + accelerometerList.size());
        //this.mGraphView.invalidate();
    }


}
