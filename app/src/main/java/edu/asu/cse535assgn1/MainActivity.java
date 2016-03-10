package edu.asu.cse535assgn1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * @author Jithin Roy
 */
public class MainActivity extends AppCompatActivity {

    private GraphView mGraphView;
    private GridLabelRenderer graphProperties;
    private boolean mIsAnimating = false;

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


    private float[] values1 = {10, 50, 60, 80, 60, 48, 00, 06, 20, 04, 02, 00, 06, 07, 8, 9, 00, 04, 02, 30, 05, 64, 89, 8, 00, 06, 05, 8, 00, 45, 00, 06, 04, 54, 03,};
    private float[] values2 = {3, 4, 5, 6, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,};


    LineGraphSeries<DataPoint> mSeries1;
    LineGraphSeries<DataPoint> mSeries2;


    private UploadWebservice uploadService;
    private DownloadWebservice downloaddService;



    private float[] values = {10, 50, 60, 80, 60, 48, 0, 6, 20, 4, 2, 0, 6, 7, 8, 9, 0, 4, 2, 30, 5, 64, 89, 8, 0, 6, 5, 8, 0, 45, 0, 6, 4, 54, 3,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        float[] values = {0};
        String[] labels = {"10", "15", "20", "25", "30", "35", "40"};
        String[] labels1 = {"1", "2", "3", "4", "5"};

        this.mGraphView = new GraphView(this);
        ViewGroup layout = (ViewGroup) findViewById(R.id.baseLayout);
        this.mGraphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(this.mGraphView);

        this.mPatientIDTextView = (TextView) findViewById(R.id.patientId);
        this.mPatientNameTextView = (TextView) findViewById(R.id.patientName);
        this.mPatientAgeTextView = (TextView) findViewById(R.id.patientAge);
        this.mSexRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mSeries1 = new LineGraphSeries<DataPoint>();
        mSeries1.setColor(Color.BLUE);
        mGraphView.addSeries(mSeries1);
        mSeries2 = new LineGraphSeries<DataPoint>();
        mSeries1.setColor(Color.WHITE);
        mGraphView.addSeries(mSeries2);

        graphProperties = new GridLabelRenderer(mGraphView);
        mGraphView.getViewport().setXAxisBoundsManual(true);
        mGraphView.getViewport().setMinX(0);
        mGraphView.getViewport().setMaxX(10);

        Intent sensorService = new Intent(MainActivity.this, SensorHandlerService.class);
        startService(sensorService);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the current state of animation so that we can
        // restart on orientation change.
        savedInstanceState.putBoolean("IsAnimating", this.mIsAnimating);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Check if the graph was already animating and call the animation function.
        this.mIsAnimating = savedInstanceState.getBoolean("IsAnimating");
        if (this.mIsAnimating) {
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

        if (mIsAnimating == false) {

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
                String tableName = name + "_" + id + "_" + age + "_" + sexString;
                DatabaseManager.sharedInstance().createTable(tableName, this);
                startAnimation();
                showMessage();

            } else {
                Toast toast = Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    public void stopButtonClicked(View view) {

        this.mIsAnimating = false;
        mIndex = 0;
        mGraphIndex = 0;
        mSeries1.resetData(new DataPoint[]{new DataPoint(0, 0)});
        mSeries2.resetData(new DataPoint[]{new DataPoint(0, 0)});
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
        uploadService = new UploadWebservice(DatabaseManager.sharedInstance().databasePath());
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
            if (mIsAnimating) {
                mHandler.postDelayed(mUpdateGraph, 120);
            }
        }
    };

    private void startAnimation() {
        this.mIsAnimating = true;
        this.mHandler.postDelayed(mUpdateGraph, 10);
    }

    private void update() {

       if (this.mIsAnimating == true) {


           if (this.mIndex == this.values1.length) {
               this.mIndex = 0;
           }
           mSeries1.appendData(new DataPoint(mGraphIndex, this.values1[this.mIndex]), true, 10);
           mSeries2.appendData(new DataPoint(mGraphIndex, this.values2[this.mIndex]), true, 10);

           mGraphIndex++;
           this.mIndex ++;
       }
        this.mGraphView.invalidate();
    }


}
