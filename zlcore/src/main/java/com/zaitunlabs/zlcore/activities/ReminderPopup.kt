package com.zaitunlabs.zlcore.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.utils.CommonUtils

class ReminderPopup : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        setContentView(R.layout.activity_reminder_popup)


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)


        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { finish() }

        //


        val title = CommonUtils.getStringIntent(intent, ARG_TITLE, "")
        val body = CommonUtils.getStringIntent(intent, ARG_BODY, "")

        if (!TextUtils.isEmpty(title)) {
            val titleView = findViewById<View>(R.id.reminder_popup_titleView) as TextView
            titleView.text = title
        }


        if (!TextUtils.isEmpty(body)) {
            val bodyView = findViewById<View>(R.id.reminder_popup_bodyView) as TextView
            bodyView.text = body
        }


        supportActionBar!!.title = getString(R.string.zlcore_title_activity_reminder_popup)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        val ARG_TITLE = "arg_title"
        val ARG_BODY = "arg_body"


        fun start(context: Context, title: String, body: String) {
            val intent = Intent(context.applicationContext, ReminderPopup::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(ARG_TITLE, title)
            intent.putExtra(ARG_BODY, body)

            context.startActivity(intent)
        }
    }
}
