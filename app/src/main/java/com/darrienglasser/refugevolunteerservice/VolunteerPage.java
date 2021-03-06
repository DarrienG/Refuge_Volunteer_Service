package com.darrienglasser.refugevolunteerservice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class VolunteerPage extends AppCompatActivity {

    /** Tag to reference the activity. */
    private static String TAG = "VolunteerPage";

    /** Whether or not data was received. */
    private boolean receivedData;

    /** Key to reference user's number from local storage. */
    private static final String NUM_VAL = "numVal";

    /**
     * Key to reference data between screen rotations. Not necessary right now, as we
     * for screen rotation to portrait,
     */
    private static final String DAT_VAL = "infoVal";

    /** Data taken from server. */
    private HelpData userInfo;

    /** String used to hold hold user's number when retrieved from local storage. */
    private String numUrl;

    /** TextView displayed when there is no data retrieved from server. */
    private RelativeLayout noReqView;

    /** Layout displayed when data can be retrieved and bound to a view. */
    private RelativeLayout foundReq;

    /** Button displayed within noReqView. */
    private Button noReqButton;

    /** Button displayed when a match is found. */
    private Button foundButton;

    private TextView noReqText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        noReqText = (TextView) findViewById(R.id.noReqText);
        noReqView = (RelativeLayout) findViewById(R.id.no_req);
        foundReq = (RelativeLayout) findViewById(R.id.cardLayoutId);
        noReqButton = (Button) findViewById(R.id.notFoundButton);
        foundButton = (Button) findViewById(R.id.foundButton);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        numUrl = settings.getString(NUM_VAL, "-1");

        noReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Snackbar.make(v, "Refreshing content...", Snackbar.LENGTH_LONG).show();
                    pollData();
                    // pollDummyData();
                } else {
                    Snackbar.make(
                            v, "Not connected to a valid network", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        resetViews();
        new pullFromServer().execute();
    }

    @Override
    public void onSaveInstanceState (Bundle saveInstanceState) {
        saveInstanceState.putSerializable(DAT_VAL, userInfo);
    }

    @Override
    public void onRestoreInstanceState (Bundle saveInstanceState) {
        Object tmp = saveInstanceState.getSerializable(DAT_VAL);
        userInfo = tmp == null ? userInfo : (HelpData) tmp;
        receivedData = userInfo != null;
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        // We don't want to let the user go back to the parent activity
    }

    /**
     * Poll Firebase server for new data. Reset data in between calls.
     */
    private void pollData() {
        new pullFromServer().execute();
        resetViews();
    }

    /**
     * DEBUG helper method. Puts fake data into object we're using.
     */
    private void pollDummyData() {
        receivedData = true;
        userInfo = new HelpData("1234567890", "water", "Billerica", 5);
        resetViews();
    }

    /**
     * Reset views in accordance with user interaction.
     */
    private void resetViews() {
        if (receivedData) {
            bindViews();

            ImageButton check = (ImageButton) findViewById(R.id.checkButton);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noReqView.setVisibility(View.VISIBLE);
                    noReqButton.setVisibility(View.VISIBLE);
                    noReqText.setText(getResources().getText(R.string.complete_string));
                    foundReq.setVisibility(View.INVISIBLE);
                    foundButton.setVisibility(View.INVISIBLE);

                }
            });

            ImageButton call = (ImageButton) findViewById(R.id.callButton);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + userInfo.getSender()));

                    if (ContextCompat.checkSelfPermission(VolunteerPage.this,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                        Log.d(TAG, "Permission not requested.");
                    } else {
                        ActivityCompat.requestPermissions(
                                VolunteerPage.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                0);
                        Log.d(TAG, "Permission requested.");
                    }
                }
            });

            noReqView.setVisibility(View.INVISIBLE);
            foundReq.setVisibility(View.VISIBLE);
            foundButton.setVisibility(View.VISIBLE);
        } else {
            foundReq.setVisibility(View.INVISIBLE);
            foundButton.setVisibility(View.INVISIBLE);
            noReqView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Bind text obtained from server to TextViews.
     */
    private void bindViews() {
        TextView numText = (TextView) findViewById(R.id.numStatus);
        TextView needText = (TextView) findViewById(R.id.needStatus);
        TextView locText = (TextView) findViewById(R.id.locStatus);
        TextView timeText = (TextView) findViewById(R.id.timeStatus);

        try {
            String tmpNum = String.format(getResources().getString(
                    R.string.help_num_string), userInfo.getSender());
            String tmpNeed = String.format(getResources().getString(
                    R.string.req_string), (userInfo.getType() + ""));
            String tmpLoc = String.format(getResources().getString(
                    R.string.tower_loc_string), userInfo.getLoc());
            String tmpTime = String.format(getResources().getString(
                    R.string.msg_time_stamp_string), userInfo.getTime());

            numText.setText(tmpNum);
            needText.setText(tmpNeed);
            locText.setText(tmpLoc);
            timeText.setText(tmpTime);

        } catch (java.lang.NullPointerException e) {
            Log.d(TAG, "Unable to pull from server or no data. Resetting views.");
            receivedData = false;
            forceReset();
        }
    }

    /**
     * Resets the view, and does not bind text. Manual way to restore default view.
     */
    private void forceReset() {
        foundReq.setVisibility(View.INVISIBLE);
        noReqView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int grantResults[]) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // All set, no need to do anything
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please grant permission to use app.", Toast.LENGTH_LONG).show();
        }
    }

    private class pullFromServer extends AsyncTask<Void,Void,Object> {
        @Override
        protected Object doInBackground(Void... params) {
            Firebase myFirebaseRef = new Firebase(
                    "https://refuge.firebaseio.com/volunteers").child(
                    numUrl).child("reqs");
            userInfo = null;

            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        userInfo = postSnapshot.getValue(HelpData.class);
                        receivedData = true;
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(TAG, "Unable to read data");
                    receivedData = false;
                }
            });
            return null;
        }
    }

    /**
     * Helper method. Determines if the user has network connectivity or not.
     *
     * @return Validity of network connection.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

