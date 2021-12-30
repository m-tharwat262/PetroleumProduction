package com.suez.uni.petroleum.engineering.production.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suez.uni.petroleum.engineering.production.R;


public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Context mContext;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private FirebaseAuth mFirebaseAuth;


    private LinearLayout mProfileButton;
    private LinearLayout mLogoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialize the context we work at and preference.
        mContext = MainActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        mProfileButton = findViewById(R.id.activity_main_profile_button);
        mLogoutButton= findViewById(R.id.activity_main_logout_button);


        TextView welcome = findViewById(R.id.welcome);


        Animation animationFromLeftToRight = AnimationUtils.loadAnimation(this, R.anim.animation_from_buttom_to_top);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isFirstLogIn = pref.getBoolean("first_login_in", true);
                if (!isFirstLogIn) {
                    welcome.setText("ويلكم من جديد يا خول");
                }

                welcome.setVisibility(View.VISIBLE);
                welcome.setAnimation(animationFromLeftToRight);

                editor.putBoolean("first_login_in", false);
                editor.apply();

            }
        }, 1000);

        setClickingOnLogout();
        setClickingOnProfileButton();
    }


    private void setClickingOnProfileButton() {

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "متضغطنيش تاني انا مش شغال", Toast.LENGTH_SHORT).show();


            }
        });

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
        editor.putBoolean("first_login_in", true);
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