package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suez.uni.petroleum.engineering.production.R;

import java.util.ArrayList;

public class OutFlowDataActivity extends AppCompatActivity {

    private static final String LOG_TAG = OutFlowDataActivity.class.getSimpleName();
    private Context mContext;

    private EditText mDepthEditText;
    private EditText mWellHeadEditText;
    private EditText mGasLiquidRatioEditText;
    private EditText mApiEditText;
    private EditText mSpecificGravityEditText;
    private EditText mWaterFractionEditText;
    private EditText mFirstDiameterEditText;
    private TextView mFirstDiameterTitle;

    private LinearLayout mDiameterContainer;

    private TextView mNextButton;
    private TextView mBackButton;
    private TextView mAddDiamterButton;

    private double mD;
    private double mPwh;
    private double mGLR;
    private double mApi;
    private double mSG;
    private double mFw;

    private ArrayList<Double> mAllDiameters = new ArrayList<>();

    private int mDiameterNumbers = 1;



    private boolean mHasFe;
    private int mFeNumbers;
    private ArrayList<Double> mAllFeValues = new ArrayList<>();
    private ArrayList<Double> mAllPressures = new ArrayList<>();
    private ArrayList<Double> mAllFlowRates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_flow_data);


        mContext = OutFlowDataActivity.this;


        mDepthEditText = findViewById(R.id.activity_out_flow_depth_field);
        mWellHeadEditText = findViewById(R.id.activity_out_flow_well_head_pressure_field);
        mGasLiquidRatioEditText = findViewById(R.id.activity_out_flow_gas_liquid_ratio_field);
        mApiEditText = findViewById(R.id.activity_out_flow_american_petroleum_institute_field);
        mSpecificGravityEditText = findViewById(R.id.activity_out_flow_gas_specific_gravity_field);
        mWaterFractionEditText = findViewById(R.id.activity_out_flow_water_fraction_field);

        mDiameterContainer = findViewById(R.id.activity_out_flow_diameters_container);
        mFirstDiameterEditText = findViewById(R.id.activity_out_flow_first_diameter_field);
        mFirstDiameterTitle = findViewById(R.id.activity_out_flow_first_diameter_title);

        mAddDiamterButton = findViewById(R.id.activity_out_flow_add_diameter_button);
        mNextButton = findViewById(R.id.activity_out_flow_next_button);
        mBackButton = findViewById(R.id.activity_out_flow_back_button);


        mHasFe = getIntent().getBooleanExtra("has_fe", false);
        mAllPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressures");
        mFeNumbers = getIntent().getIntExtra("fe_numbers", 1);
        mAllFlowRates = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates");


        setClickingOnNextButton();
        setClickingOnBackButton();
        setClickingOnAddDiameterButton();

    }


    private void setClickingOnAddDiameterButton() {

        mAddDiamterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDiameterNumbers += 1;

                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_diameter, null, false);
                TextView diameterTitleTextView = view.findViewById(R.id.item_diameter_diameter_title);
                diameterTitleTextView.setText(getString(R.string.diameter_with_number, mDiameterNumbers));
                mFirstDiameterTitle.setText(getString(R.string.diameter_with_number, 1));


                EditText editText = view.findViewById(R.id.item_diameter_diameter_field);

                if (mDiameterNumbers == 2) {
                    editText.setText("2.441");
                }

                if (mDiameterNumbers == 3) {
                    editText.setText("2.992");
                }

                ImageView deleteButton = view.findViewById(R.id.item_diameter_delete_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int position = mDiameterNumbers -1;
                        mDiameterContainer.removeViewAt(position);
                        mDiameterNumbers -= 1;

                        if (mDiameterContainer.getChildCount() == 2) {

                            mFirstDiameterTitle.setText(R.string.diameter);

                        }

                    }
                });

                mDiameterContainer.addView(view, mDiameterNumbers - 1);

            }
        });


    }


    private void setClickingOnBackButton() {

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    private void setClickingOnNextButton() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasValidation()) {

                    Intent intent = new Intent(mContext, TableOutFlowActivity.class);

                    intent.putExtra("d", mD);
                    intent.putExtra("pwh", mPwh);
                    intent.putExtra("glr", mGLR);
                    intent.putExtra("api", mApi);
                    intent.putExtra("sg", mSG);
                    intent.putExtra("fw", mFw);

                    intent.putExtra("all_diameter", mAllDiameters);


                    intent.putExtra("all_pressures", mAllPressures);
                    if (!mHasFe) {
                        intent.putExtra("all_flow_rates", mAllFlowRates);
                    } else {
                        intent.putExtra("has_fe", true);
                        intent.putExtra("fe_numbers", mFeNumbers);

                        for(int i = 0 ; i < mFeNumbers ; i++) {
                            intent.putExtra("all_flow_rates_with_fe_" + i,
                                    (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates_with_fe_" + i));
                        }

                    }


                    startActivity(intent);

                }

            }
        });

    }

    private boolean hasValidation() {

        String dString = mDepthEditText.getText().toString().trim();
        String pwhString = mWellHeadEditText.getText().toString().trim();
        String glrString = mGasLiquidRatioEditText.getText().toString().trim();
        String apiString = mApiEditText.getText().toString().trim();
        String sgString = mSpecificGravityEditText.getText().toString().trim();
        String fwString = mWaterFractionEditText.getText().toString().trim();


        if (dString.isEmpty()) {
            mDepthEditText.setError("can't be empty");
            mDepthEditText.requestFocus();
            return false;
        } else if (pwhString.isEmpty()) {
            mWellHeadEditText.setError("can't be empty");
            mWellHeadEditText.requestFocus();
            return false;
        } else if (glrString.isEmpty()) {
            mGasLiquidRatioEditText.setError("can't be empty");
            mGasLiquidRatioEditText.requestFocus();
            return false;
        } else if (apiString.isEmpty()) {
            mApiEditText.setError("can't be empty");
            mApiEditText.requestFocus();
            return false;
        } else if (sgString.isEmpty()) {
            mSpecificGravityEditText.setError("can't be empty");
            mSpecificGravityEditText.requestFocus();
            return false;
        } else if (fwString.isEmpty()) {
            mWaterFractionEditText.setError("can't be empty");
            mWaterFractionEditText.requestFocus();
            return false;
        } else if (!hasValidDiameters()) {
            return false;
        }


        mD = Double.parseDouble(dString);
        mPwh = Double.parseDouble(pwhString);
        mGLR = Double.parseDouble(glrString);
        mApi = Double.parseDouble(apiString);
        mSG = Double.parseDouble(sgString);
        mFw = Double.parseDouble(fwString);

        return true;

    }

    private boolean hasValidDiameters() {

        mAllDiameters.clear();

        String firstFe = mFirstDiameterEditText.getText().toString().trim();

        if (firstFe.isEmpty()) {
            mFirstDiameterEditText.setError("can't be empty");
            mFirstDiameterEditText.requestFocus();
            return false;
        } else {

            mAllDiameters.add(Double.parseDouble(firstFe));

            if (mDiameterNumbers > 1) {
                for (int i = 1; i < mDiameterNumbers; i++) {

                    LinearLayout linearLayoutItem = (LinearLayout) mDiameterContainer.getChildAt(i);

                    EditText editText = linearLayoutItem.findViewById(R.id.item_diameter_diameter_field);
                    String diameterString = editText.getText().toString().trim();

                    if (diameterString.isEmpty()) {
                        editText.setError("can't be empty");
                        editText.requestFocus();
                        return false;
                    } else {
                        mAllDiameters.add(Double.parseDouble(diameterString));

                    }
                }
            }

        }

        return true;
    }


}