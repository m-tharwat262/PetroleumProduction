package com.suez.uni.petroleum.engineering.production.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suez.uni.petroleum.engineering.production.R;
import com.suez.uni.petroleum.engineering.production.models.UserObject;


public class LoginActivity extends AppCompatActivity {


    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private Context mContext;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private TextView mSignUpButton;
    private TextView mForgetPasswordButton;
    private TextView mLoginButton;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;






    private String mEmailAddress = "";
    private String mPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user is already login or not before continue to execute the rest of code.
        mFirebaseAuth = FirebaseAuth.getInstance();

        // initialize the context we work at and preference.
        mContext = LoginActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        checkUserAlreadyLoggedIn();


        // initialize views in the layout.
        mEmailAddressEditText = findViewById(R.id.activity_login_email_address_field);
        mPasswordEditText = findViewById(R.id.activity_login_password_field);
        mLoginButton = findViewById(R.id.activity_login_login_button);
        mSignUpButton = findViewById(R.id.activity_login_sign_up_button);
        mForgetPasswordButton = findViewById(R.id.activity_login_forget_password_button);


        // create a dialog that will shown to the user to hint him that there is a process running.
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(R.string.please_wait);
        mProgressDialog.setMessage(getString(R.string.login_progress));
        mProgressDialog.setCanceledOnTouchOutside(false);


        // initialize variables related to firebase.
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // handle clicking on buttons in layout.
        setClickingOnLogin();
        setClickingOnForgetPassword();
        setClickingOnSignUp();

    }


    /**
     * Check if the user is already login or not.
     * in case he logged in the activity direct him to the MainActivity.
     */
    private void checkUserAlreadyLoggedIn() {

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if(firebaseUser != null) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

    }

    /**
     * Check if the fields (email or phone number - password) contains a valid data or not.
     *
     * @return boolean value refer to if all field has a valid data inside it or not.
     */
    private boolean hasValidData() {

        // get data from editText fields.
        mEmailAddress = mEmailAddressEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString();

        // check the data validation.
        if (TextUtils.isEmpty(mEmailAddress)) {

            mEmailAddressEditText.setError(getString(R.string.enter_your_email_address));
            mEmailAddressEditText.requestFocus();
            return false;

        } if (!Patterns.EMAIL_ADDRESS.matcher(mEmailAddress).matches()) {

            mEmailAddressEditText.setError(getString(R.string.not_valid_email_address));
            mEmailAddressEditText.requestFocus();
            return false;

        } else if (TextUtils.isEmpty(mPassword)) {

            mPasswordEditText.setError(getString(R.string.enter_your_password));
            mPasswordEditText.requestFocus();
            return false;

        }

        // if all the check validation is correct then the method will return finally true.
        return true;

    }




    /**
     * Login to the firebase by the email and password.
     * And Save the user profile pic if it's exist in the firebase.
     * Then redirect the user to the main activity.
     */
    private void firebaseLogin() {

        // hint the user that the login processes started.
        mProgressDialog.show();

        // try make a connection to the firebase and login with the email and the password.
        mFirebaseAuth.signInWithEmailAndPassword(mEmailAddress, mPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        // get the user unique id from the firebase.
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // get the data from the realtime database and store it in preference.
                        // get the profile pic if there is a one in the storage database.
                        mDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                UserObject userObject = snapshot.getValue(UserObject.class);

                                if (userObject != null) {

                                    // get the user data in variables.
                                    String realName = userObject.getName();
                                    String emailAddress = userObject.getEmailAddress();

                                    // store the user data in preference.
                                    editor.putString(getString(R.string.user_id_Key), userId);
                                    editor.putString(getString(R.string.name_Key), realName);
                                    editor.putString(getString(R.string.email_address_Key), emailAddress);

                                    editor.apply();


                                    // send the user to the MainActivity.
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    finish();
                                    startActivity(intent);

                                    Toast.makeText(mContext, R.string.login_successfully, Toast.LENGTH_SHORT).show();

                                    mProgressDialog.dismiss();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                                Log.i(LOG_TAG, "Can't get the data from the server");

                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {

                    // if login failed we will search to know if the error is in the email address
                    // or the password
                    @Override
                    public void onFailure(Exception e) {

                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                boolean isValidEmailAddress = false;

                                // search in all user data to to find the email address in the
                                // realtime database.
                                for(DataSnapshot ds : snapshot.getChildren()) {

                                    String emailInFirebase = ds.child("emailAddress").getValue(String.class);

                                    if (emailInFirebase.equals(mEmailAddress)) {

                                        isValidEmailAddress = true;

                                    }

                                }

                                // if the phone number not exist in the firebase means that the phone number not correct.
                                if (isValidEmailAddress) {

                                    mPasswordEditText.setError(getString(R.string.wrong_password));
                                    mPasswordEditText.requestFocus();

                                } else {

                                    mEmailAddressEditText.setError(getString(R.string.wrong_email_address));
                                    mEmailAddressEditText.requestFocus();

                                }

                                mProgressDialog.dismiss();

                            }


                            @Override
                            public void onCancelled(DatabaseError error) {

                                Log.i(LOG_TAG, "unix");


                            }

                        });

                    }
                });



    }





    /**
     * Handle Clicking on Login button.
     */
    private void setClickingOnLogin() {

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if the data validation and if it's valid start the login process.
                // check if the user has internet connection or not.
                if(hasValidData()) {

                    if(isNetworkAvailable()) {
                        firebaseLogin();
                    } else {
                        Toast.makeText(mContext, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    /**
     * Handle Clicking on forget password button.
     */
    private void setClickingOnForgetPassword() {

        mForgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send the user to a new activity to reset his password.
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);

            }
        });

    }

    /**
     * Handle Clicking on forget sign up button.
     */
    private void setClickingOnSignUp() {

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send the user to a new activity to make him create a new account.
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

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