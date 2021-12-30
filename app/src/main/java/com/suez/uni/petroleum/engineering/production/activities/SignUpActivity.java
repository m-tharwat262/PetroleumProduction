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
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class SignUpActivity extends AppCompatActivity {


    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();
    private Context mContext;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private EditText mNameEditText;
    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mSignUpButton;
    private TextView mLoginInButton;
    private ProgressDialog mProgressDialog;

    private String mName = "";
    private String mEmailAddress = "";
    private String mPassword = "";
    private String mConfirmPassword = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // initialize the main context and shared preference.
        mContext = SignUpActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        // initialize views in the layout.
        mNameEditText = findViewById(R.id.activity_sign_up_name_field);
        mEmailAddressEditText = findViewById(R.id.activity_sign_up_email_address_field);
        mPasswordEditText = findViewById(R.id.activity_sign_up_password_field);
        mConfirmPasswordEditText = findViewById(R.id.activity_sign_up_confirm_password_field);
        mSignUpButton = findViewById(R.id.activity_sign_up_sign_up_button);
        mLoginInButton = findViewById(R.id.activity_sign_up_login_button);



        // initialize variables related to firebase.
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");


        // create a dialog that will shown to the user to hint him that there is a process running.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.please_wait));
        mProgressDialog.setMessage(getString(R.string.sign_up_progress));
        mProgressDialog.setCanceledOnTouchOutside(false);



        // handle clicking on buttons in layout.
        setClickingOnSignUp();
        setClickingOnLogin();





    }



    /**
     * Check if the fields (name - email - phone number - password - confirm password) contains
     * a valid data or not.
     *
     * @return boolean value refer to if all field has a valid data inside it or not.
     */
    private boolean hasValidateData() {


        // get the data from the editText fields
        mName = mNameEditText.getText().toString().trim();
        mEmailAddress = mEmailAddressEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString();
        mConfirmPassword = mConfirmPasswordEditText.getText().toString();


        // check if the data in each field is valid or not.
        if (TextUtils.isEmpty(mName)) {

            mNameEditText.setError(getString(R.string.enter_your_name));
            mNameEditText.requestFocus();
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailAddress).matches()) {

            mEmailAddressEditText.setError(getString(R.string.not_valid_email_address));
            mEmailAddressEditText.requestFocus();
            return false;

        } else if (TextUtils.isEmpty(mPassword)) {

            mPasswordEditText.setError(getString(R.string.enter_your_password));
            mPasswordEditText.requestFocus();
            return false;

        } else if (mPassword.length() < 8) {

            mPasswordEditText.setError(getString(R.string.password_can_not_be_less_than_8));
            mPasswordEditText.requestFocus();
            return false;

        } else if (!hasCapitalCase(mPassword)) {

            mPasswordEditText.setError("must contain at lest one uppercase");
            mPasswordEditText.requestFocus();
            return false;

        } else if (!hasLowerCase(mPassword)) {

            mPasswordEditText.setError("must contain at lest one lowercase");
            mPasswordEditText.requestFocus();
            return false;

        }

        else if (!mConfirmPassword.equals(mPassword)) {

            mConfirmPasswordEditText.setError(getString(R.string.password_not_match));
            mConfirmPasswordEditText.requestFocus();
            return false;

        }

        // if all the check validation is correct then the method will return finally true.
        return true;

    }

    /**
     * Start the sing up processes which start by checking if the email and the phone that the user
     * insert is unique or not then verify the phone number.
     */
    private void startSignUpProcesses() {

        // hint the user that the sign up processes started.
        mProgressDialog.show();

        // check if the email address or the phone number that the user entered is already exist
        // and set error message to make the user change it or sign up with that data.
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean isValidEmailAddress = true;

                for(DataSnapshot ds : snapshot.getChildren()) {

                    String emailAddress = ds.child("emailAddress").getValue(String.class);

                    if (mEmailAddress.equals(emailAddress)) {

                        isValidEmailAddress = false;

                    }

                }

                if (!isValidEmailAddress) {

                    mProgressDialog.dismiss();
                    mEmailAddressEditText.setError(getString(R.string.email_address_already_exist));
                    mEmailAddressEditText.requestFocus();

                }  else {

                    firebaseSingUp();

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }

        });

    }


    /**
     * Sign up to the firebase with an email and password and after that link the phone number
     * provider to the email address provider.
     */
    private void firebaseSingUp() {

        mFirebaseAuth.createUserWithEmailAndPassword(mEmailAddress, mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                // create a User object contain the user data.
                UserObject userObject = new UserObject(mName, mEmailAddress);

                // insert the user object above to the firebase.
                // and set the preference to store that data.
                // and merge the phone number provider to email address.
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(userObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            editor.putString(getString(R.string.user_id_Key), userId);
                            editor.putString(getString(R.string.name_Key), mName);
                            editor.putString(getString(R.string.email_address_Key), mEmailAddress);
                            editor.apply();

                            // open the MainActivity layout.
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                            Toast.makeText(mContext, R.string.sign_up_successfully, Toast.LENGTH_SHORT).show();

                            // close the progress dialog and show the verification code dialog to make
                            // the user insert it.
                            mProgressDialog.dismiss();

                        } else {

                            Toast.makeText(mContext, getString(R.string.sign_up_failed), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(mContext, getString(R.string.sign_up_failed), Toast.LENGTH_SHORT).show();

                    }
                });

    }





    /**
     * Handle Clicking on login button.
     */
    private void setClickingOnLogin() {

        mLoginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // finish the activity and that will return the user to login layout.
                finish();

            }
        });

    }

    /**
     * Handle Clicking on sign up button.
     */
    private void setClickingOnSignUp() {

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if the data validation and if it's valid start the sign up process.
                // check if the user has internet connection or not.
                if (hasValidateData()) {

                    if(isNetworkAvailable()) {
                        startSignUpProcesses();
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

    private static boolean hasCapitalCase(String str) {

        char ch;

        boolean capitalFlag = false;

        for(int i=0;i < str.length();i++) {

            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            }

        }

        return capitalFlag;

    }


    private static boolean hasLowerCase(String str) {

        char ch;

        boolean lowerCaseFlag = false;

        for(int i=0;i < str.length();i++) {

            ch = str.charAt(i);

            if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }

        }

        return lowerCaseFlag;

    }

}