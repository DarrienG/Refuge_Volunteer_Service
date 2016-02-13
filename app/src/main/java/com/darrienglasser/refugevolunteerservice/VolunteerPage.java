package com.darrienglasser.refugevolunteerservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class VolunteerPage extends AppCompatActivity {
    private static String TAG = "VolunteerPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        TextView noReqView = (TextView) findViewById(R.id.no_req);
        RelativeLayout foundReq = (RelativeLayout) findViewById(R.id.cardLayoutId);

        Firebase.setAndroidContext(this);

        Firebase myFirebaseRef = new Firebase("https://refuge.firebaseio.com/");



        // TODO: Poll server to see if data has been received
        if (false) {
            foundReq.setVisibility(View.GONE);
        } else {
            noReqView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        // We don't want to let the user go back to the parent activity
    }
}
