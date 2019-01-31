package com.zaitunlabs.zlcore.modules.version_history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;

public class VersionChangeHistoryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_change_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableUpNavigation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        showFragment(R.id.fragment, VersionChangeHistoryActivityFragment.class,
                new PostFragmentInstantiation<VersionChangeHistoryActivityFragment>() {
                    @Override
                    public void postInstantiation(VersionChangeHistoryActivityFragment fragment) {

                    }
                }, savedInstanceState, "versionChangeHistory");
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

    public static void start(Context context){
        Intent historyIntent = new Intent(context, VersionChangeHistoryActivity.class);
        context.startActivity(historyIntent);
    }

}
