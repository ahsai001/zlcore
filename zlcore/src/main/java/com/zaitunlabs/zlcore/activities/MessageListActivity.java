package com.zaitunlabs.zlcore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.fragments.InfoFragment;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.InfoUtils;
import com.zaitunlabs.zlcore.utils.NotificationUtils;

public class MessageListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableUpNavigation();

        getSupportActionBar().setTitle(R.string.module_message_list_title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        showFragment(R.id.fragment, InfoFragment.class, null, savedInstanceState, "messageList");

        NotificationUtils.handleIntentFromNotification(getIntent(), new NotificationUtils.CallBackIntentFromNotification() {
            @Override
            public void showMessagesPage(Bundle bundle) {

            }
        });

    }

    public static void start(Context context){
        Intent intent = new Intent(context,MessageListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NotificationUtils.handleIntentFromNotification(intent, new NotificationUtils.CallBackIntentFromNotification() {
            @Override
            public void showMessagesPage(Bundle bundle) {

            }
        });
    }
}
