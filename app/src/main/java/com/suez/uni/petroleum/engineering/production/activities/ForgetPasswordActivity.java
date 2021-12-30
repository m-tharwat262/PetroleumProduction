package com.suez.uni.petroleum.engineering.production.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.suez.uni.petroleum.engineering.production.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForgetPasswordActivity.class.getSimpleName();
    private Context mContext;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    private EditText mEmailOrPhoneEditText;
    private TextView mResetButton;
    private TextView mLoginButton;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    private String mEmailAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        // initialize the main context and shared preference.
        mContext = ForgetPasswordActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        // initialize views in the layout.
        mEmailOrPhoneEditText = findViewById(R.id.activity_reset_password_email_address_field);
        mLoginButton = findViewById(R.id.activity_reset_password_login_button);
        mResetButton = findViewById(R.id.activity_reset_password_reset_button);


        // initialize variables related to firebase.
        mFirebaseAuth = FirebaseAuth.getInstance();


        // create a dialog that will shown to the user to hint him that there is a process running.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.please_wait);
        mProgressDialog.setMessage(getString(R.string.reset_password_progress));
        mProgressDialog.setCanceledOnTouchOutside(false);



        //handle clicking on buttons in layout.
        setClickingOnReset();
        setClickingOnLogin();


    }




    /**
     * Check if the fields (email) contains a valid data or not.
     *
     * @return boolean value refer to if all field has a valid data inside it or not.
     */
    private boolean hasValidateData() {

        mEmailAddress = mEmailOrPhoneEditText.getText().toString().trim();

        // check if the data in each field is valid or not.
        if (mEmailAddress.isEmpty()) {

            mEmailOrPhoneEditText.setError(getString(R.string.enter_your_email_address));
            mEmailOrPhoneEditText.requestFocus();
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailAddress).matches()) {

            mEmailOrPhoneEditText.setError(getString(R.string.not_valid_email_address));
            mEmailOrPhoneEditText.requestFocus();
            return false;

        }

        // if all the check validation is correct then the method will return finally true.
        return true;

    }

    /**
     * Start reset password process by check first if the user enter an email address.
     */
    private void startResetPasswordProcess() {

        mProgressDialog.show();

        mFirebaseAuth.sendPasswordResetEmail(mEmailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    // show a dialog to the user hint him that the app sent him a link to his
                    // email to rest his password.
                    showResetDialog();

                } else {

                    Toast.makeText(ForgetPasswordActivity.this, R.string.reset_password_failed, Toast.LENGTH_SHORT).show();
                    mEmailOrPhoneEditText.setError(getString(R.string.not_valid_email_address));
                }

                mProgressDialog.dismiss();

            }
        });




    }


    /**
     * Show a dialog to the user hint him that the app sent him a link to his email to rest
     * his password.
     */
    private void showResetDialog() {


        Dialog dialog = new Dialog(ForgetPasswordActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView okButton = dialog.findViewById(R.id.dialog_reset_password_ok_button);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();

            }
        });

        dialog.show();

    }

    /**
     * Handle Clicking on login button.
     */
    private void setClickingOnLogin() {

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // finish the activity and that will return the user to login layout.
                finish();

            }
        });

    }

    /**
     * Handle Clicking on submit button.
     */
    private void setClickingOnReset() {

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if the data validation and if it's valid start the sign up process.
                // check if the user has internet connection or not.
                if (hasValidateData()) {

                    if(isNetworkAvailable()) {
                        startResetPasswordProcess();
                    } else {
                        Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

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