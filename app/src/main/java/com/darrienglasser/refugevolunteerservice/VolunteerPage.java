package com.darrienglasser.refugevolunteerservice;

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

        // pollDummyData();
        pollData();
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
                // pollDummyData();
                pollData();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REFRESH_ICON, Menu.NONE, "Refresh").setIcon(R.drawable.ic_refresh_icon);
        return true;
    }

    private void bindViews() {
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
        userInfo = new HelpData("9789789789", "water", "Billerica", "5:00");
        resetViews();
    }

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
            noReqView.setVisibility(View.GONE);
            foundReq.setVisibility(View.VISIBLE);
        } else {
            foundReq.setVisibility(View.GONE);
            noReqView.setVisibility(View.VISIBLE);
        }
    }
}
