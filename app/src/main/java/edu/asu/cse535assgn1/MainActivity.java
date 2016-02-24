package edu.asu.cse535assgn1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.asu.cse535assgn1.lib.GraphView;
import edu.asu.cse535assgn1.models.Patient;

/**
 * @author Jithin Roy
 */
public class MainActivity extends AppCompatActivity {

    private GraphView mGraphView;
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


    private float[] values = {10, 50, 60, 80, 60, 48, 0, 6, 20, 4, 2, 0, 6, 7, 8, 9, 0, 4, 2, 30, 5, 64, 89, 8, 0, 6, 5, 8, 0, 45, 0, 6, 4, 54, 3,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        float[] values = {0};
        String[] labels = {"10","15","20","25","30","35","40"};
        String[] labels1 = {"1","2","3","4","5"};

        this.mGraphView = new GraphView(this,values,"Graph",labels,labels1, GraphView.LINE);
        ViewGroup layout = (ViewGroup) findViewById(R.id.baseLayout);
        this.mGraphView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(this.mGraphView);

        this.mPatientIDTextView = (TextView)findViewById(R.id.patientId);
        this.mPatientNameTextView = (TextView)findViewById(R.id.patientName);
        this.mPatientAgeTextView = (TextView)findViewById(R.id.patientAge);
        this.mSexRadioGroup = (RadioGroup)findViewById(R.id.radio_group);
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
        if (mIsAnimating == false) {

            String id = this.mPatientIDTextView.getText().toString();
            String name = this.mPatientNameTextView.getText().toString();
            String ageString = this.mPatientAgeTextView.getText().toString();
            int age = 0;
            if (ageString.equals("") == false && ageString.length()<=3) {
               // System.out.println("before "+ageString);
                age = Integer.parseInt(ageString);
               // System.out.println("after "+ageString);
            }
            boolean isMale = true;
            if (this.mSexRadioGroup.getCheckedRadioButtonId() == R.id.radio_female) {
                   isMale = false;
            }
            this.mPatient = new Patient(id,name,age,isMale);

            startAnimation();
            showMessage();
        }

    }

    public void stopButtonClicked(View view) {

        this.mIsAnimating = false;
        float[] values = {0};
        this.mGraphView.setValues(values);
        this.mGraphView.invalidate();

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

           if (this.mIndex == this.values.length) {
               this.mIndex = 0;
           }

           if (this.mGraphIndex < 10) {
               this.mCurrentGraphValues.add(this.mGraphIndex, this.values[this.mIndex]);
               this.mGraphIndex++;
           } else {
               this.mCurrentGraphValues.remove(0);
               this.mCurrentGraphValues.add(this.values[this.mIndex]);
           }

           float[] array = new float[10];
           for (int i = 0; i < this.mCurrentGraphValues.size(); i++) {
               array[i] = this.mCurrentGraphValues.get(i);
           }

           this.mGraphView.setValues(array);
           this.mIndex++;
       }
        this.mGraphView.invalidate();
    }


}