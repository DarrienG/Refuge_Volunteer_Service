package com.darrienglasser.refugevolunteerservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Intro_Screen extends AppCompatActivity {

    private static String SENT_PREF = "sentFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SENT_PREF, 0);
        boolean haveDisplayed = settings.getBoolean("sentPref", false);

        if (!haveDisplayed) {
            setContentView(R.layout.activity_intro_screen);

            // Upon interacting with UI controls, delay any scheduled hide()
            // operations to prevent the jarring behavior of controls going away
            // while interacting with the UI.
            findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VolunteerPage.class);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SENT_PREF, true).apply();
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
