package com.suez.uni.petroleum.engineering.production.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suez.uni.petroleum.engineering.production.R;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class TableOutFlowActivity extends AppCompatActivity {


    private static final String LOG_TAG = TableOutFlowActivity.class.getSimpleName();
    private Context mContext;

    private LinearLayout mTableContainer;
    private LinearLayout mPwfLayout;
    private LinearLayout mDLayout;
    private TextView mNextButton;
    private TextView mBackButton;

    private double mD;
    private double mPwh;
    private double mGLR;
    private double mApi;
    private double mSG;
    private double mFw;

    private ArrayList<Double> mAllDiameters = new ArrayList<>();

    private ArrayList<Double> AllFlowRatesTableTwo = new ArrayList<>();

    private ArrayList<ArrayList<Double>> mPwfForAllDiameters = new ArrayList<>();


    private boolean mHasFe;
    private int mFeNumbers;
    private ArrayList<Double> mAllFeValues = new ArrayList<>();
    private ArrayList<Double> mAllPressures = new ArrayList<>();
    private ArrayList<Double> mAllFlowRates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_out_flow);


        mContext = TableOutFlowActivity.this;


        mTableContainer = findViewById(R.id.activity_out_flow_table_layout);
        mDLayout = findViewById(R.id.activity_out_flow_d_layout);
        mNextButton = findViewById(R.id.activity_table_out_flow_next_button);
        mBackButton = findViewById(R.id.activity_out_flow_back_button);
        mPwfLayout = findViewById(R.id.activity_out_flow_pwf_layout);


        mHasFe = getIntent().getBooleanExtra("has_fe", false);
        mAllPressures = (ArrayList<Double>) getIntent().getSerializableExtra("all_pressures");
        mFeNumbers = getIntent().getIntExtra("fe_numbers", 1);
        mAllFlowRates = (ArrayList<Double>) getIntent().getSerializableExtra("all_flow_rates");


        mD = getIntent().getDoubleExtra("d", 0);
        mPwh = getIntent().getDoubleExtra("pwh", 0);
        mGLR = getIntent().getDoubleExtra("glr", 0);
        mApi = getIntent().getDoubleExtra("api", 0);
        mSG = getIntent().getDoubleExtra("sg", 0);
        mFw = getIntent().getDoubleExtra("fw", 0);
        mAllDiameters = (ArrayList<Double>) getIntent().getSerializableExtra("all_diameter");


        createArrays();
        AddTitles();
        initializeTableValues();
        setClickingOnNextButton();
        setClickingOnBackButton();

    }


    private void AddTitles() {

        if (mAllDiameters.size() > 1) {

            mDLayout.setVisibility(View.VISIBLE);

            View line = findViewById(R.id.activity_out_flow_d_horizontal_line);
            line.setVisibility(View.VISIBLE);

            for (int i = 0; i < mAllDiameters.size(); i++) {


                LinearLayout.LayoutParams paramsForPressure = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, mAllDiameters.size());
                mPwfLayout.setLayoutParams(paramsForPressure);

                LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_fe, null, false);
                TextView titleTextView = item.findViewById(R.id.item_fe_title);
                titleTextView.setText(getString(R.string.d_with_number, (i+1)));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // or new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,100); to set fixed height
                mDLayout.addView(item, params);


                if (i == mAllDiameters.size() - 1) {
                    View view = item.findViewById(R.id.item_fe_right_line);
                    view.setVisibility(View.GONE);
                }

            }

        }




    }


    private void createArrays() {

        for (int i = 0 ; i < mAllDiameters.size() ; i++) {

            ArrayList<Double> arrayList = new ArrayList<>();
            mPwfForAllDiameters.add(arrayList);
        }


    }

    private void initializeTableValues() {


//        new StartAsyncTask().execute();


        double flowRate = 200;

        boolean isLoopFinished = false;
        for (int i = 0; i < 49; i++) {

            if(i != 0) {
                flowRate += 400;

                if (flowRate == 4200) {
                    isLoopFinished = true;
                }

            }



            AllFlowRatesTableTwo.add(flowRate);

            DecimalFormat df;
            if (mAllDiameters.size() > 2) {
                df = new DecimalFormat("#.##");
            } else {
                df = new DecimalFormat("#.##");
            }

            df.setRoundingMode(RoundingMode.CEILING);


            LinearLayout linearLayout = new LinearLayout(mContext);

            LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
            TextView textView = view.findViewById(R.id.item_table_value);
            textView.setText(String.valueOf(flowRate));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // or new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,100); to set fixed height
            linearLayout.addView(view, params);



            for (int j = 0; j < mAllDiameters.size(); j++) {

                double pwf = getPwf(flowRate, mAllDiameters.get(j));

                mPwfForAllDiameters.get(j).add(pwf);

                LinearLayout view2 = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_table, null, false);
                TextView textView2 = view2.findViewById(R.id.item_table_value);

                if (pwf == -1.0) {
                    textView2.setText("ـــــــ");
                } else {
                    textView2.setText(df.format(pwf));
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

    private double getPwf(double flowRate, double diameter) {


        writeInExcel(flowRate, diameter);

        double pwf = readFromExcel();

        return pwf;
    }


    private double readFromExcel() {

        File file = mContext.getExternalFilesDir("/excel/excel_equations.xls");

        try {

            FileInputStream fileInputStream = new FileInputStream(file);

            HSSFWorkbook workBook = new HSSFWorkbook(fileInputStream);
            workBook.setForceFormulaRecalculation(true);

            HSSFSheet sheet = workBook.getSheetAt(2);
            sheet.setForceFormulaRecalculation(true);

            Iterator<Row> rowIterator = sheet.iterator();


            Row row = sheet.getRow(49);
            Cell cell = row.getCell(4);

            FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);


            fileInputStream.close();

            return cellValue.getNumberValue();

        } catch (Exception e) {

            Log.i(LOG_TAG, "unix : Exception readXLSX method " + e);

        }

        return -1;

    }

    private void writeInExcel(double flowRate, double diameter) {

        try {

            File file = mContext.getExternalFilesDir("/excel/excel_equations.xls");

            InputStream inp = new FileInputStream(file);

            Workbook workbook = WorkbookFactory.create(inp);
            workbook.setForceFormulaRecalculation(true);

            Sheet sheet = workbook.getSheetAt(2);
            sheet.setForceFormulaRecalculation(true);



            Row rowDepth = sheet.getRow(4);
            rowDepth.getCell(2).setCellValue(mD);

            Row rowDiameter = sheet.getRow(5);
            rowDiameter.getCell(2).setCellValue(diameter);

            Row rowApi = sheet.getRow(6);
            rowApi.getCell(2).setCellValue(mApi);

            Row rowGlr = sheet.getRow(8);
            rowGlr.getCell(2).setCellValue(mGLR / 2);

            Row rowPressure = sheet.getRow(10);
            rowPressure.getCell(2).setCellValue(mPwh + 14.7);

            Row rowFlowRate = sheet.getRow(13);
            rowFlowRate.getCell(2).setCellValue(flowRate);

            Row rowWaterCut = sheet.getRow(14);
            rowWaterCut.getCell(2).setCellValue(mFw);

            Row rowSpecificGravity = sheet.getRow(9);
            rowSpecificGravity.getCell(2).setCellValue(mSG);


            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // suppose your formula is in B3
            CellReference cellReference = new CellReference(0, 1);
            Row row2 = sheet.getRow(cellReference.getRow());
            Cell cell = row2.getCell(cellReference.getCol());
            CellValue cellValue = evaluator.evaluate(cell);

            Log.i(LOG_TAG, "unix cellValue equals : " + cellValue);



            // Now this Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();



        } catch (Exception e) {

            Log.i(LOG_TAG, "unix : Exception in write method " + e);

        }


    }





    private void setClickingOnNextButton() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, GraphWellDesignActivity.class);


                intent.putExtra("all_flow_rate_two", AllFlowRatesTableTwo);
                intent.putExtra("d_numbers", mAllDiameters.size());

                for(int i = 0 ; i < mAllDiameters.size() ; i++) {
                    intent.putExtra("all_pressure_two_" + i, mPwfForAllDiameters.get(i));
                    Log.i(LOG_TAG, " unix :" +mPwfForAllDiameters.get(i) );
                }


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





    private class StartAsyncTask extends AsyncTask<Void, Void, Void> {


        public StartAsyncTask() {





        }

        @Override
        protected Void doInBackground(Void... voids) {















            return null;
        }


        private double readFromExcel() {

            File file = mContext.getExternalFilesDir("/excel/excel_equations.xls");

            try {

                FileInputStream fileInputStream = new FileInputStream(file);

                HSSFWorkbook workBook = new HSSFWorkbook(fileInputStream);
                workBook.setForceFormulaRecalculation(true);

                HSSFSheet sheet = workBook.getSheetAt(2);
                sheet.setForceFormulaRecalculation(true);

                Iterator<Row> rowIterator = sheet.iterator();


                Row row = sheet.getRow(49);
                Cell cell = row.getCell(4);

                FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);


                fileInputStream.close();

                return cellValue.getNumberValue();

            } catch (Exception e) {

                Log.i(LOG_TAG, "unix : Exception readXLSX method " + e);

            }

            return -1;

        }

        private void writeInExcel(double flowRate, double diameter) {

            try {

                File file = mContext.getExternalFilesDir("/excel/excel_equations.xls");

                InputStream inp = new FileInputStream(file);

                Workbook workbook = WorkbookFactory.create(inp);
                workbook.setForceFormulaRecalculation(true);

                Sheet sheet = workbook.getSheetAt(2);
                sheet.setForceFormulaRecalculation(true);



                Row rowDepth = sheet.getRow(4);
                rowDepth.getCell(2).setCellValue(mD);

                Row rowDiameter = sheet.getRow(5);
                rowDiameter.getCell(2).setCellValue(diameter);

                Row rowApi = sheet.getRow(6);
                rowApi.getCell(2).setCellValue(mApi);

                Row rowGlr = sheet.getRow(8);
                rowGlr.getCell(2).setCellValue(mGLR / 2);

                Row rowPressure = sheet.getRow(10);
                rowPressure.getCell(2).setCellValue(mPwh + 14.7);

                Row rowFlowRate = sheet.getRow(13);
                rowFlowRate.getCell(2).setCellValue(flowRate);

                Row rowWaterCut = sheet.getRow(14);
                rowWaterCut.getCell(2).setCellValue(mFw);

                Row rowSpecificGravity = sheet.getRow(9);
                rowSpecificGravity.getCell(2).setCellValue(mSG);


                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                // suppose your formula is in B3
                CellReference cellReference = new CellReference(0, 1);
                Row row2 = sheet.getRow(cellReference.getRow());
                Cell cell = row2.getCell(cellReference.getCol());
                CellValue cellValue = evaluator.evaluate(cell);

                Log.i(LOG_TAG, "unix cellValue equals : " + cellValue);



                // Now this Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();



            } catch (Exception e) {

                Log.i(LOG_TAG, "unix : Exception in write method " + e);

            }


        }

    }



}