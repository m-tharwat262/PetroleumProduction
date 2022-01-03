package com.suez.uni.petroleum.engineering.production.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suez.uni.petroleum.engineering.production.R;
import com.suez.uni.petroleum.engineering.production.calculator.Ipr;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private FirebaseAuth mFirebaseAuth;


    private LinearLayout mIprButton;
    private LinearLayout mLogoutButton;
    private LinearLayout mHowToUseButton;
    private LinearLayout mHistoryButton;
    private LinearLayout mWellDesignButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialize the context we work at and preference.
        mContext = MainActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        mIprButton = findViewById(R.id.activity_main_ipr_button);
        mLogoutButton= findViewById(R.id.activity_main_logout_button);
        mWellDesignButton = findViewById(R.id.activity_main_well_design_button);
        mHowToUseButton = findViewById(R.id.activity_main_how_to_use_button);
        mHistoryButton = findViewById(R.id.activity_main_history_button);





        Animation animationFromLeftToRight = AnimationUtils.loadAnimation(this, R.anim.animation_from_buttom_to_top);





        setClickingOnLogout();
        setClickingOnIprButton();
        setClickingOnWellDesignButton();
        setClickingOnHowToUse();
        setClickingOnHistoryButton();



        saveExcelSheetInAppDirectory();

    }

    private void setClickingOnWellDesignButton() {

        mWellDesignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IprActivity.class);
                intent.putExtra("from_well_design", true);
                startActivity(intent);

            }
        });

    }

    private void setClickingOnHowToUse() {

        mHowToUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, HowToUseActivity.class);
                startActivity(intent);

            }
        });

    }

    private void setClickingOnHistoryButton() {

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, HistoryActivity.class);
                startActivity(intent);

            }
        });

    }

    private void setClickingOnIprButton() {

        mIprButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IprActivity.class);
                startActivity(intent);

            }
        });

    }




    private void saveExcelSheetInAppDirectory() {

        try {

            String path = mContext.getExternalFilesDir("/excel").toString();

            File outFile = new File(path, "excel_equations.xls");

            if (outFile.exists()) {
//                Toast.makeText(mContext, "the file exist", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream inputStream = getAssets().open("excel_equations.xls");

            OutputStream outputStream = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + "excel_equations.xls", e);
        }

    }







    public void write() {



        try {

            File file = mContext.getExternalFilesDir("/excel/excel_equations.xls");

            InputStream inp = new FileInputStream(file);

            Workbook workbook = WorkbookFactory.create(inp);
            workbook.setForceFormulaRecalculation(true);

            Sheet sheet = workbook.getSheetAt(0);
            sheet.setForceFormulaRecalculation(true);

            int num = sheet.getLastRowNum();
            Row row = sheet.getRow(0);
            row.getCell(0).setCellValue(5);


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

    public void readXLSX() {

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

            Log.i(LOG_TAG, "unix cell contains : " + cellValue.getNumberValue());


            fileInputStream.close();

        } catch (Exception e) {

            Log.i(LOG_TAG, "unix : Exception readXLSX method " + e);

        }
    }


    private void setClickingOnLogout() {

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable()) {
                    singOut();
                } else {
                    Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void singOut() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signOut();

        // store the user data in preference.
        editor.putString(getString(R.string.user_id_Key), getString(R.string.preference_string_empty_value));
        editor.putString(getString(R.string.name_Key), getString(R.string.preference_string_empty_value));
        editor.putString(getString(R.string.email_address_Key), getString(R.string.preference_string_empty_value));
        editor.apply();

        Toast.makeText(mContext, "Successfully Logout", Toast.LENGTH_SHORT).show();
        checkUserAlreadyLoggedIn();

    }

    private void checkUserAlreadyLoggedIn() {

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if(firebaseUser == null) {

            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();

        }

    }


    /**
     * Check if the user is connecting to internet or not.
     *
     * @return boolean refer to connecting to internet or not.
     */
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}