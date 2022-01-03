package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suez.uni.petroleum.engineering.production.R;
import com.suez.uni.petroleum.engineering.production.calculator.Ipr;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TableActivity extends AppCompatActivity {

    private static final String LOG_TAG = TableActivity.class.getSimpleName();
    private Context mContext;


    private LinearLayout mTableContainer;
    private LinearLayout mQlLayout;
    private LinearLayout mFeLayout;
    private TextView mNextButton;
    private TextView mBackButton;

    private boolean mIsFromWellDesign = false;

    private boolean mHasFe;
    private double mPr;
    private double mPb;
    private double mPwf;
    private double mQl;
    private ArrayList<Double> mAllFeValues = new ArrayList<>();

    private ArrayList<Double> mAllPressures = new ArrayList<>();
    private ArrayList<Double> mAllFlowRates = new ArrayList<>();

    private ArrayList<ArrayList<Double>> mAllFlowRatesWithFe = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        mContext = TableActivity.this;

        mTableContainer = findViewById(R.id.activity_table_table_layout);
        mFeLayout = findViewById(R.id.activity_table_fe_layout);
        mNextButton = findViewById(R.id.activity_table_next_button);
        mBackButton = findViewById(R.id.activity_table_back_button);
        mQlLayout = findViewById(R.id.activity_table_ql_layout);


        mIsFromWellDesign  = getIntent().getBooleanExtra("from_well_design", false);
        mHasFe = getIntent().getBooleanExtra("has_fe", false);
        mHasFe = getIntent().getBooleanExtra("has_fe", false);
        mPr = getIntent().getDoubleExtra("pr", 0);
        mPb = getIntent().getDoubleExtra("pb", 0);
        mPwf = getIntent().getDoubleExtra("pwf", 0);
        mQl = getIntent().getDoubleExtra("ql", 0);
        mAllFeValues = (ArrayList<Double>) getIntent().getSerializableExtra("all_fe_values");


        if (mHasFe) {
            createArrays();
        }


        initializeTableValues();

        checkFeExisting();


        setClickingOnNextButton();
        setClickingOnBackButton();


    }

    private void checkFeExisting() {

        boolean hasFe = getIntent().getBooleanExtra("has_fe", false);

        if (hasFe) {


            if (mAllFeValues.size() > 1) {

                mFeLayout.setVisibility(View.VISIBLE);

                View line = findViewById(R.id.activity_table_fe_horizontal_line);
                line.setVisibility(View.VISIBLE);

                for (int i = 0; i < mAllFeValues.size(); i++) {


                    LinearLayout.LayoutParams paramsForPressure = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, mAllFeValues.size());
                    mQlLayout.setLayoutParams(paramsForPressure);

                    LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_fe, null, false);
                    TextView titleTextView = item.findViewById(R.id.item_fe_title);
                    titleTextView.setText(getString(R.string.fe_with_number, (i+1)));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // or new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,100); to set fixed height
                    mFeLayout.addView(item, params);


                    if (i == mAllFeValues.size() - 1) {
                        View view = item.findViewById(R.id.item_fe_right_line);
                        view.setVisibility(View.GONE);
                    }

                }

            }

        }

    }

    private void createArrays() {

        for (int i = 0 ; i < mAllFeValues.size() ; i++) {

            ArrayList<Double> arrayList = new ArrayList<>();
            mAllFlowRatesWithFe.add(arrayList);
        }


    }


    private void initializeTableValues() {


        if(!mHasFe) {

            double pressure = mPr;

            boolean isLoopFinished = false;
            for (int i = 0; i < 49; i++) {

                if(i != 0) {
                    pressure -= 200;

                    if (pressure <= 0) {
                        pressure = 0;
                        isLoopFinished = true;
                    }

                }

                double flowRate = getQl(pressure);

                mAllPressures.add(pressure);
                mAllFlowRates.add(flowRate);


                DecimalFormat df;
                if (mAllFeValues != null) {

                    if (mAllFeValues.size() > 2) {
                        df = new DecimalFormat("#.##");
                    } else {
                        df = new DecimalFormat("#.##");
                    }

                } else {
                    df = new DecimalFormat("#.##");
                }

                df.setRoundingMode(RoundingMode.CEILING);


                LinearLayout linearLayout = new LinearLayout(mContext);

                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
                TextView textView = view.findViewById(R.id.item_table_value);
                textView.setText(String.valueOf(pressure));

                LinearLayout view2 = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
                TextView textView2 = view2.findViewById(R.id.item_table_value);
                textView2.setText(df.format(flowRate));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // or new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,100); to set fixed height
                linearLayout.addView(view, params);
                linearLayout.addView(view2, params);


                if ((i % 2) == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.table_dark));
                    view2.setBackgroundColor(getResources().getColor(R.color.table_dark));
                }

                mTableContainer.addView(linearLayout);


                if (isLoopFinished) {
                    break;
                }


            }






        } else {


            double pressure = mPr;

            boolean isLoopFinished = false;
            for (int i = 0; i < 49; i++) {

                if(i != 0) {
                    pressure -= 200;

                    if (pressure <= 0) {
                        pressure = 0;
                        isLoopFinished = true;
                    }

                }



                mAllPressures.add(pressure);
//                mAllFlowRates.add(flowRate);


                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);


                LinearLayout linearLayout = new LinearLayout(mContext);

                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
                TextView textView = view.findViewById(R.id.item_table_value);
                textView.setText(String.valueOf(pressure));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // or new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,100); to set fixed height
                linearLayout.addView(view, params);



                for (int j = 0; j < mAllFeValues.size(); j++) {

                    double flowRate = getQlWhenFeExist(pressure, mAllFeValues.get(j));

                    mAllFlowRatesWithFe.get(j).add(flowRate);

                    LinearLayout view2 = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
                    TextView textView2 = view2.findViewById(R.id.item_table_value);

                    if (flowRate == -1.0) {
                        textView2.setText("ـــــــ");
                    } else {
                        textView2.setText(df.format(flowRate));
                    }



                    linearLayout.addView(view2, params);

                    if ((i % 2) == 0) {
                        view2.setBackgroundColor(getResources().getColor(R.color.table_dark));
                    }

                }


                if ((i % 2) == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.table_dark));
                }


                mTableContainer.addView(linearLayout);


                if (isLoopFinished) {
                    break;
                }





            }




        }


    }

    private double getQl(double pTable) {

        if (!mHasFe) {

            if (mPb >= mPr) {
               return Ipr.getQTableSaturated(mQl, mPwf, mPr, pTable);
            } else {

                if (mPwf >= mPb ) {
                    return Ipr.getQTableUnderSaturatedCasePwfBiggerOrEqualPb(mQl, mPwf, mPr, pTable, mPb);
                } else {
                    return Ipr.getQTableUnderSaturatedCasePwfSmallerPb(mQl, mPwf, mPr, pTable, mPb);
                }

            }

        } else {

            return 0.0;

        }


    }


    private double getQlWhenFeExist(double pTable, double fe) {


        if (mPb >= mPr) {

            if (pTable == 0) {
                return Ipr.getQMaxActual(mQl,mPwf, mPr, mAllFeValues.get(0), fe);
            }
            return Ipr.getQTableSaturatedWithFe(mQl, mPwf, mPr, pTable, mAllFeValues.get(0), fe);
        } else {

            if (mPwf >= mPb ) {
                return Ipr.getQTableUnderSaturatedCasePwfBiggerOrEqualPbWithFe(mQl, mPwf, mPr, pTable, mPb, mAllFeValues.get(0), fe);
            } else {
                return Ipr.getQTableUnderSaturatedCasePrSmallerPbWithFe(mQl, mPwf, mPr, pTable, mPb, mAllFeValues.get(0), fe);
            }

        }

    }



    private void setClickingOnNextButton() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if (mIsFromWellDesign) {
                    intent = new Intent(mContext, OutFlowDataActivity.class);
                } else {
                    intent = new Intent(mContext, GraphActivity.class);
                }

                intent.putExtra("all_pressures", mAllPressures);

                if (!mHasFe) {
                    intent.putExtra("all_flow_rates", mAllFlowRates);
                } else {
                    intent.putExtra("has_fe", true);
                    intent.putExtra("fe_numbers", mAllFlowRatesWithFe.size());
                    for(int i = 0 ; i < mAllFlowRatesWithFe.size() ; i++) {
                        intent.putExtra("all_flow_rates_with_fe_" + i, mAllFlowRatesWithFe.get(i));
                    }

                }


                startActivity(intent);

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

}