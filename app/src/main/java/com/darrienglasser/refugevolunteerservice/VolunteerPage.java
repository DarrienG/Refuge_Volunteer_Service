package com.darrienglasser.refugevolunteerservice;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        TextView noReqView = (TextView) findViewById(R.id.no_req);
        RelativeLayout foundReq = (RelativeLayout) findViewById(R.id.cardLayoutId);

        Firebase.setAndroidContext(this);

        // Add back in when valid data is available
        Firebase myFirebaseRef = new Firebase("https://refuge.firebaseio.com/");

        final HelpData userInfo = null;

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    HelpData userInfo = postSnapshot.getValue(HelpData.class);
                    receivedData = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "Unable to read data");
                receivedData = false;
            }
        });

        // TODO: Poll server to see if data has been received
        if (receivedData) {
            bindViews(userInfo);

            noReqView.setVisibility(View.GONE);
        } else {
            foundReq.setVisibility(View.GONE);

        }
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
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REFRESH_ICON, Menu.NONE, "Refresh").setIcon(R.drawable.ic_refresh_icon);
        return true;
    }

    private void bindViews(HelpData userInfo) {
        TextView numText = (TextView) findViewById(R.id.numStatus);
        TextView needText = (TextView) findViewById(R.id.needStatus);
        TextView locText = (TextView) findViewById(R.id.locStatus);
        TextView timeText = (TextView) findViewById(R.id.timeStatus);

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

    }
}
