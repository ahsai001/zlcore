package com.zaitunlabs.zlcore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.utils.CommonUtils;

public class ReminderPopup extends BaseActivity {
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_BODY = "arg_body";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_reminder_popup);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //


        String title = CommonUtils.getStringIntent(getIntent(),ARG_TITLE, "");
        String body = CommonUtils.getStringIntent(getIntent(),ARG_BODY, "");

        if(!TextUtils.isEmpty(title)){
            TextView titleView = (TextView) findViewById(R.id.reminder_popup_titleView);
            titleView.setText(title);
        }


        if(!TextUtils.isEmpty(body)){
            TextView bodyView = (TextView) findViewById(R.id.reminder_popup_bodyView);
            bodyView.setText(body);
        }


        getSupportActionBar().setTitle(getString(R.string.zlcore_title_activity_reminder_popup));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    public static void start(Context context, String title, String body){
        Intent intent = new Intent(context.getApplicationContext(), ReminderPopup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARG_TITLE,title);
        intent.putExtra(ARG_BODY,body);

        context.startActivity(intent);
    }
}
