package com.zaitunlabs.zlcore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.fragments.AppListActivityFragment;
import com.zaitunlabs.zlcore.fragments.InfoFragment;
import com.zaitunlabs.zlcore.utils.CommonUtils;

public class AppListActivity extends BaseActivity {
    public static final String PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
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

        final boolean isMeid = CommonUtils.getBooleanIntent(getIntent(), PARAM_IS_MEID, false);

        showFragment(R.id.fragment, AppListActivityFragment.class, new PostFragmentInstantiation<AppListActivityFragment>() {
            @Override
            public void postInstantiation(AppListActivityFragment fragment) {
                fragment.setArg(isMeid);
            }
        }, savedInstanceState, "AppListActivity");
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

    public static void start(Context context, boolean isMeid){
        Intent intent = new Intent(context,AppListActivity.class);
        intent.putExtra(PARAM_IS_MEID, isMeid);
        context.startActivity(intent);
    }

}
