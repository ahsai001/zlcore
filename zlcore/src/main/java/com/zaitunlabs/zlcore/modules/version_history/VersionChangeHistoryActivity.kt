package com.zaitunlabs.zlcore.modules.version_history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseActivity

class VersionChangeHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version_change_history)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        enableUpNavigation()

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        showFragment(R.id.fragment, VersionChangeHistoryActivityFragment::class.java,
                object : BaseActivity.PostFragmentInstantiation<VersionChangeHistoryActivityFragment>() {
                    override fun postInstantiation(fragment: VersionChangeHistoryActivityFragment) {

                    }
                }, savedInstanceState, "versionChangeHistory")
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

        fun start(context: Context) {
            val historyIntent = Intent(context, VersionChangeHistoryActivity::class.java)
            context.startActivity(historyIntent)
        }
    }

}
