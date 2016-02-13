package com.darrienglasser.refugevolunteerservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class VolunteerPage extends AppCompatActivity {
    private static String TAG = "VolunteerPage";
    private boolean receivedData;
    private static final int REFRESH_ICON = 0;
    private HelpData userInfo;

    private TextView noReqView;
    private RelativeLayout foundReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        Firebase.setAndroidContext(this);

        noReqView = (TextView) findViewById(R.id.no_req);
        foundReq = (RelativeLayout) findViewById(R.id.cardLayoutId);


        pollDummyData();
        //pollData();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        // We don't want to let the user go back to the parent activity
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case REFRESH_ICON:
                Toast.makeText(
                        getApplicationContext(),
                        "Refreshing content...",
                        Toast.LENGTH_SHORT).show();
                pollDummyData();
                // pollData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REFRESH_ICON, Menu.NONE, "Refresh").setIcon(R.drawable.ic_refresh_icon);
        return true;
    }

    /**
     * Poll Firebase server for new data.
     */
    private void pollData() {
        Firebase myFirebaseRef = new Firebase("https://refuge.firebaseio.com/");

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
        resetViews();
    }

    /**
     * DEBUG helper method. Puts fake data into object we're using.
     */
    private void pollDummyData() {
        receivedData = true;
        userInfo = new HelpData("1234567890", "water", "Billerica", "5:00");
        resetViews();
    }

    /**
     * Reset views in accordance with user interaction.
     */
    private void resetViews() {
        bindViews();
        if (receivedData) {
            bindViews();

            ImageButton check = (ImageButton) findViewById(R.id.checkButton);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noReqView.setVisibility(View.VISIBLE);
                    noReqView.setText(getResources().getText(R.string.complete_string));
                    foundReq.setVisibility(View.GONE);

                }
            });

            ImageButton call = (ImageButton) findViewById(R.id.callButton);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + userInfo.getNumber()));

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

            noReqView.setVisibility(View.GONE);
            foundReq.setVisibility(View.VISIBLE);
        } else {
            foundReq.setVisibility(View.GONE);
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
                    R.string.help_num_string), userInfo.getNumber());
            String tmpNeed = String.format(getResources().getString(
                    R.string.help_num_string), userInfo.getNeed());
            String tmpLoc = String.format(getResources().getString(
                    R.string.help_num_string), userInfo.getLocation());
            String tmpTime = String.format(getResources().getString(
                    R.string.help_num_string), userInfo.getTimeStamp());

            numText.setText(tmpNum);
            needText.setText(tmpNeed);
            locText.setText(tmpLoc);
            timeText.setText(tmpTime);

        } catch (java.lang.NullPointerException e) {
            Log.d(TAG, "Unable to pull from server. Resetting views.");
            receivedData = false;
            forceReset();
        }
    }

    /**
     * Resets the view, and does not bind text. Manual way to restore default view.
     */
    private void forceReset() {
        foundReq.setVisibility(View.GONE);
        noReqView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int grantResults[]) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please grant permission to use app.", Toast.LENGTH_LONG).show();
        }
    }
}
