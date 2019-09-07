package com.zaitunlabs.zlcore.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.fragments.AppListActivityFragment
import com.zaitunlabs.zlcore.fragments.InfoFragment
import com.zaitunlabs.zlcore.utils.CommonUtils

class AppListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        enableUpNavigation()

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val isMeid = CommonUtils.getBooleanIntent(intent, PARAM_IS_MEID, false)

        showFragment(R.id.fragment, AppListActivityFragment::class.java, object : BaseActivity.PostFragmentInstantiation<AppListActivityFragment>() {
            override fun postInstantiation(fragment: AppListActivityFragment) {
                fragment.setArg(isMeid)
            }
        }, savedInstanceState, "AppListActivity")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val PARAM_IS_MEID = InfoFragment.PARAM_IS_MEID

        fun start(context: Context, isMeid: Boolean) {
            val intent = Intent(context, AppListActivity::class.java)
            intent.putExtra(PARAM_IS_MEID, isMeid)
            context.startActivity(intent)
        }
    }

}
