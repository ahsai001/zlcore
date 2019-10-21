package com.zaitunlabs.zlcore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.fragments.InfoFragment;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.NotificationUtil;

public class MessageListActivity extends BaseActivity {
    public static final String PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableUpNavigation();

        getSupportActionBar().setTitle(R.string.zlcore_module_message_list_title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final boolean isMeid = CommonUtil.getBooleanIntent(getIntent(), PARAM_IS_MEID, false);

        showFragment(R.id.fragment, InfoFragment.class, new PostFragmentInstantiation<InfoFragment>() {
            @Override
            public void postInstantiation(InfoFragment fragment) {
                fragment.setArg(isMeid);
            }
        }, savedInstanceState, "messageList");

        NotificationUtil.handleIntentFromNotification(getIntent(), new NotificationUtil.CallBackIntentFromNotification() {
            @Override
            public void handle(Bundle data, boolean showMessagePage, long infoId) {

            }
        });

    }

    public static void start(Context context, boolean isMeid){
        Intent intent = new Intent(context,MessageListActivity.class);
        intent.putExtra(PARAM_IS_MEID, isMeid);
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
        NotificationUtil.handleIntentFromNotification(intent, new NotificationUtil.CallBackIntentFromNotification() {
            @Override
            public void handle(Bundle data, boolean showMessagePage, long infoId) {

            }
        });
    }
}
