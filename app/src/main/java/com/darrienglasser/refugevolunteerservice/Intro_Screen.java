package com.darrienglasser.refugevolunteerservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import com.firebase.client.Firebase;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Intro_Screen extends AppCompatActivity {

    private static String SENT_PREF = "sentFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean haveDisplayed = settings.getBoolean(SENT_PREF, false);

        if (!haveDisplayed) {
            setContentView(R.layout.activity_intro_screen);

            Firebase.setAndroidContext(this);
            final Firebase myFirebaseRef = new Firebase("https://refuge.firebaseio.com/");

            // Upon interacting with UI controls, delay any scheduled hide()
            // operations to prevent the jarring behavior of controls going away
            // while interacting with the UI.
            findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VolunteerPage.class);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SENT_PREF, true).apply();
                    Firebase sendDetails = myFirebaseRef.child("volunteers");
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    sendDetails.setValue(tm.getLine1Number());
                    startActivity(intent);
                    finishActivity(0);
                }
            });
        } else {
            //setContentView(R.layout.activity_volunteer_page);
            startActivity(new Intent(getApplicationContext(), VolunteerPage.class));
            finishActivity(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}
