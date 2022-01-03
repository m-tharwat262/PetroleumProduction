package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.suez.uni.petroleum.engineering.production.R;
import com.suez.uni.petroleum.engineering.production.calculator.Ipr;

import java.util.ArrayList;
import java.util.Collections;

public class IprActivity extends AppCompatActivity {

    private static final String LOG_TAG = IprActivity.class.getSimpleName();
    private Context mContext;

    private SwitchMaterial mSkinSwitchButton;
    private LinearLayout mFlowRateLayout;
    private TextView mCalculateFeButton;
    private TextView mFirstFeTitleTextView;
    private TextView mAddFeButton;
    private TextView mNextButton;
    private TextView mBackButton;

    private EditText mPReservoirEditText;
    private EditText mPBubbleEditText;
    private EditText mPwfEditText;
    private EditText mQlEditText;
    private EditText mFirstFeEditText;

    private double mPr;
    private double mPb;
    private double mPwf;
    private double mQl;
    private boolean mHasSkinFactor = false;

    private boolean mIsFromWellDesign = false;

    private int mFeNumbers;
    private ArrayList<Double> mAllFeValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipr);

        mContext = IprActivity.this;

        mSkinSwitchButton = findViewById(R.id.activity_ipr_skin_switch_button);

        mFlowRateLayout = findViewById(R.id.activity_ipr_flow_efficiency_layout);
        mCalculateFeButton = findViewById(R.id.activity_ipr_calculate_fe_button);
        mAddFeButton = findViewById(R.id.activity_ipr_add_another_fe_value_button);
        mFirstFeTitleTextView = findViewById(R.id.activity_ipr_fe_one_title);
        mFirstFeEditText = findViewById(R.id.activity_ipr_fe_one_field);


        mNextButton = findViewById(R.id.activity_ipr_next_button);
        mBackButton = findViewById(R.id.activity_ipr_back_button);

        mPReservoirEditText = findViewById(R.id.activity_ipr_average_reservoir_pressure_field);
        mPBubbleEditText = findViewById(R.id.activity_ipr_bubble_point_pressure);
        mPwfEditText = findViewById(R.id.activity_ipr_test_point_pressure);
        mQlEditText = findViewById(R.id.activity_ipr_test_point_flow_rate);


        mIsFromWellDesign  = getIntent().getBooleanExtra("from_well_design", false);

        setClickingOnSkinSwitchButton();
        setClickingOnCalculateFeButton();
        setClickingOnAddFeButton();
        setClickingOnNextButton();
        setClickingOnBackButton();
        setClickingOnSkinSwitchButton();

    }

    private void setClickingOnAddFeButton() {

        mAddFeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFeNumbers += 1;
//                mAllFeValues.add(-1.0);

                LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_flow_efficiency, null, false);
                TextView feTitleTextView = view.findViewById(R.id.item_flow_efficiency_title);
                feTitleTextView.setText(getString(R.string.flow_efficiency_with_number, mFeNumbers));
                mFirstFeTitleTextView.setText(getString(R.string.flow_efficiency_with_number, 1));

                ImageView deleteButton = view.findViewById(R.id.item_flow_efficiency_delete_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int position = mFeNumbers -1;
                        mFlowRateLayout.removeViewAt(position);
                        mFeNumbers -= 1;
//                        mAllFeValues.remove(position);

//                        mFlowRateLayout.removeView(view);

                        if (mFlowRateLayout.getChildCount() == 2) {

                            mFirstFeTitleTextView.setText(R.string.flow_efficiency);

                        }

                    }
                });

                EditText feEditText = view.findViewById(R.id.item_flow_efficiency_field);

                TextView calculateFeButton = view.findViewById(R.id.item_flow_efficiency_fe_button);
                calculateFeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        displayFeDialog(feEditText);

                    }
                });



                mFlowRateLayout.addView(view, mFeNumbers - 1);

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

                    Intent intent = new Intent(mContext, TableActivity.class);
                    intent.putExtra("pr", mPr);
                    intent.putExtra("pb", mPb);
                    intent.putExtra("pwf", mPwf);
                    intent.putExtra("ql", mQl);

                    if (mHasSkinFactor) {
                        intent.putExtra("has_fe", true);
                        intent.putExtra("all_fe_values", mAllFeValues);
                    }

                    intent.putExtra("from_well_design", mIsFromWellDesign);
                    startActivity(intent);
                    mAllFeValues.clear();

                }

            }
        });

    }

    private boolean hasValidation() {

        String prString = mPReservoirEditText.getText().toString().trim();
        String pbString = mPBubbleEditText.getText().toString().trim();
        String pwfString = mPwfEditText.getText().toString().trim();
        String qlString = mQlEditText.getText().toString().trim();


        if (prString.isEmpty()) {
            mPReservoirEditText.setError("can't be empty");
            mPReservoirEditText.requestFocus();
            return false;
        } else if (pbString.isEmpty()) {
            mPBubbleEditText.setError("can't be empty");
            mPBubbleEditText.requestFocus();
            return false;
        } else if (pwfString.isEmpty()) {
            mPwfEditText.setError("can't be empty");
            mPwfEditText.requestFocus();
            return false;
        }  else if (qlString.isEmpty()) {
            mQlEditText.setError("can't be empty");
            mQlEditText.requestFocus();
            return false;
        }

//        else if (Double.parseDouble(prString) < 0) {
//            mPReservoirEditText.setError("can't be negative value");
//            mPReservoirEditText.requestFocus();
//            return false;
//        } else if (Double.parseDouble(pbString) < 0) {
//            mPBubbleEditText.setError("can't be negative value");
//            mPBubbleEditText.requestFocus();
//            return false;
//        } else if (Double.parseDouble(pwfString) < 0) {
//            mPwfEditText.setError("can't be negative value");
//            mPwfEditText.requestFocus();
//            return false;
//        }  else if (Double.parseDouble(qlString) < 0) {
//            mQlEditText.setError("can't be negative value");
//            mQlEditText.requestFocus();
//            return false;
//        }




        else if (mHasSkinFactor) {

            boolean isValid = hasValidFeData();
            if (!isValid) {
                return false;
            }
        }


        mPr = Double.parseDouble(prString);
        mPb = Double.parseDouble(pbString);
        mPwf = Double.parseDouble(pwfString);
        mQl = Double.parseDouble(qlString);

        return true;

    }

    private boolean hasValidFeData() {

        String firstFe = mFirstFeEditText.getText().toString().trim();

        if (firstFe.isEmpty()) {
            mFirstFeEditText.setError("can't be empty");
            mFirstFeEditText.requestFocus();
            return false;
        } else if (Double.parseDouble(firstFe) < 0) {
            mFirstFeEditText.setError("can't be negative value");
            mFirstFeEditText.requestFocus();
            return false;
        } else {

            mAllFeValues.add(Double.parseDouble(firstFe));

            if (mFeNumbers > 1) {
                for (int i = 1; i < mFeNumbers; i++) {

                    LinearLayout linearLayoutItem = (LinearLayout) mFlowRateLayout.getChildAt(i);

                    EditText editText = linearLayoutItem.findViewById(R.id.item_flow_efficiency_field);
                    String feString = editText.getText().toString().trim();

                    if (feString.isEmpty()) {
                        editText.setError("can't be empty");
                        editText.requestFocus();
                        mAllFeValues.clear();
                        return false;
                    }  else if (Double.parseDouble(firstFe) < 0) {
                        editText.setError("can't be negative value");
                        editText.requestFocus();
                        return false;
                    } else {
                        mAllFeValues.add(Double.parseDouble(feString));

                    }
                }
            }

        }

        return true;
    }


    private void setClickingOnCalculateFeButton() {



        mCalculateFeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(!hasValidation()) {
//                    return;
//                }

                mHasSkinFactor = false;
                if(!hasValidation()) {

                    mHasSkinFactor = true;
                    return;

                }
                mHasSkinFactor = true;
                displayFeDialog(mFirstFeEditText);

            }
        });

    }

    private void displayFeDialog(EditText feEditText) {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_calculate_fe);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        CheckBox skinFactorCheckBox = dialog.findViewById(R.id.dialog_calculate_fe_skin_factor_check_box);
        CheckBox anotherTestPointCheckBox = dialog.findViewById(R.id.dialog_calculate_fe_another_test_point_check_box);
        LinearLayout skinFactorLayout = dialog.findViewById(R.id.dialog_calculate_fe_skin_factor_layout);
        LinearLayout flowRateLayout = dialog.findViewById(R.id.dialog_calculate_fe_flow_rate_layout);
        LinearLayout pressureLayout = dialog.findViewById(R.id.dialog_calculate_fe_pressure_layout);
        EditText skinFactorEditText = dialog.findViewById(R.id.dialog_calculate_fe_skin_factor_field);
        EditText flowRateEditText = dialog.findViewById(R.id.dialog_calculate_fe_flow_rate_field);
        EditText pressureEditText = dialog.findViewById(R.id.dialog_calculate_pressure_field);
        TextView calculateButton = dialog.findViewById(R.id.dialog_calculate_fe_button);


        skinFactorLayout.setVisibility(View.VISIBLE);

        skinFactorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    skinFactorLayout.setVisibility(View.VISIBLE);
                    flowRateLayout.setVisibility(View.GONE);
                    pressureLayout.setVisibility(View.GONE);
                    anotherTestPointCheckBox.setChecked(false);
                } else {
                    skinFactorLayout.setVisibility(View.GONE);
                }

            }
        });



        anotherTestPointCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    flowRateLayout.setVisibility(View.VISIBLE);
                    pressureLayout.setVisibility(View.VISIBLE);
                    skinFactorLayout.setVisibility(View.GONE);
                    skinFactorCheckBox.setChecked(false);
                } else {
                    flowRateLayout.setVisibility(View.GONE);
                    pressureLayout.setVisibility(View.GONE);
                }

            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!skinFactorCheckBox.isChecked() && !anotherTestPointCheckBox.isChecked()) {
                    Toast.makeText(IprActivity.this, "select one from above", Toast.LENGTH_SHORT).show();
                } else {

                    if (skinFactorCheckBox.isChecked()) {

                        mHasSkinFactor = false;
                        if (hasValidation()) {
                            mHasSkinFactor = true;

                            String sString = skinFactorEditText.getText().toString().trim();
                            if (sString.isEmpty()) {
                                skinFactorEditText.setError("insert the value first");
                                skinFactorEditText.requestFocus();
                                return;
                            }
                            double s = Double.parseDouble(sString);
                            double fe = Ipr.getFe(mQl, mPwf, mPr, s, -1, -1);

//                            mAllFeValues.remove((mFeNumbers - 1));
//                            mAllFeValues.add((mFeNumbers - 1), fe);

                            feEditText.setText(String.valueOf(fe));
                            Toast.makeText(IprActivity.this, "calculated successfully", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }


                    } else {

                        String flowRateString = flowRateEditText.getText().toString().trim();
                        String pressureString = pressureEditText.getText().toString().trim();

                        if (flowRateString.isEmpty()) {
                            flowRateEditText.setError("insert the value first");
                            flowRateEditText.requestFocus();
                            return;
                        } else if (pressureString.isEmpty()) {
                            pressureEditText.setError("insert the value first");
                            pressureEditText.requestFocus();
                        }

                        double flowRate = Double.parseDouble(flowRateString);
                        double pressure = Double.parseDouble(pressureString);
                        double fe = Ipr.getFe(mQl, mPwf, mPr, -1, flowRate, pressure);

//                        mAllFeValues.remove((mFeNumbers - 1));
//                        mAllFeValues.add((mFeNumbers - 1), fe);

                        feEditText.setText(String.valueOf(fe));
                        Toast.makeText(IprActivity.this, "calculated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }

//                Log.i(LOG_TAG, "the arrayList is : " + mAllFeValues);


            }
        });

        dialog.show();

    }


    private void setClickingOnSkinSwitchButton() {

        mSkinSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mHasSkinFactor = true;
                    mFlowRateLayout.setVisibility(View.VISIBLE);
                    mFeNumbers = 1;
//                    mAllFeValues.add(-1.0);
                } else {
                    mHasSkinFactor = false;
                    mFlowRateLayout.setVisibility(View.GONE);
                    mFeNumbers = 0;
//                    mAllFeValues.clear();

                    //TODO: delete all extra fe.
                }

            }
        });

    }


}