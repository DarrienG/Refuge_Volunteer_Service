package com.darrienglasser.refugevolunteerservice;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Intro_Screen extends AppCompatActivity {

    /** Key used with settings to see if user sent in their number. */
    private static String SENT_PREF = "sentFile";

    /** The user's number, once entered. */
    private static String NUM_VAL = "numVal";
    int tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean haveDisplayed = settings.getBoolean(SENT_PREF, false);

        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.READ_PHONE_STATE}, tmp);

        Firebase.setAndroidContext(this);



        // Validate user if they have not already gone through validation process
        if (!haveDisplayed) {
            setContentView(R.layout.activity_intro_screen);

            final Firebase myFirebaseRef = new Firebase("https://refuge.firebaseio.com/");

            findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VolunteerPage.class);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SENT_PREF, true).apply();
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    Firebase sendDetails = myFirebaseRef.child("volunteers")
                            .child(tm.getLine1Number());
                    sendDetails.child("number").setValue(1);
                    editor.putString(NUM_VAL, tm.getLine1Number()).apply();
                    startActivity(intent);
                    finishActivity(0);
                }
            });
        } else {
            // Skip to volunteer page otherwise
            startActivity(new Intent(getApplicationContext(), VolunteerPage.class));
            finishActivity(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int grantResults[]) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findViewById(R.id.send_button).setClickable(true);
        } else {
            if (findViewById(R.id.send_button).getVisibility() == View.VISIBLE){
                findViewById(R.id.send_button).setClickable(false);
            }
        }
    }
}
