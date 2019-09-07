package com.zaitunlabs.zlcore.activities

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView

import com.ms.square.android.expandabletextview.ExpandableTextView
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.models.InformationModel
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.DateStringUtils
import com.squareup.picasso.Picasso

import java.util.Calendar
import java.util.Date

class InfoPopup : BaseActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mViewPager: ViewPager? = null

    private var action: Class<*>? = null
    private var extraData: Bundle? = null
    private var infoList: List<InformationModel>? = null
    private var infoPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
        }

        if (Build.VERSION.SDK_INT >= 27) {
            setTurnScreenOn(true)
        }

        val keyguardManager = getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissError() {
                    super.onDismissError()
                }

                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                }

                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                }
            })
        }
        setContentView(R.layout.activity_info_popup)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        action = CommonUtils.getSerializableIntent(intent, EXTRA_CLASS, null) as Class<*>
        extraData = CommonUtils.getBundleIntent(intent, EXTRA_DATA, Bundle()) as Bundle

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val info = infoList!![infoPosition]
            goAction(this@InfoPopup, action, extraData, info.id!!)
        }

        infoList = InformationModel.allUnreadInfo


        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter
        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                infoPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        val counter = ""
        supportActionBar!!.setTitle(getString(R.string.zlcore_title_activity_info_popup) + " (" + infoList!!.size + ") ")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        /*
        infoList.clear();
        List<InformationModel> newList = InformationModel.getAllUnreadInfo();
        infoList.addAll(newList);

        getSupportActionBar().setTitle(getString(R.string.zlcore_title_activity_info_popup)+" ("+infoList.size()+") ");

        mSectionsPagerAdapter.notifyDataSetChanged();
        */

        //to make new notif turn screen on
        finish()
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class InfoPopupFragment : Fragment() {

        internal var titleView: TextView
        internal var bodyView: ExpandableTextView
        internal var imageView: ImageView
        internal var timeView: TextView

        internal var action: Class<*>? = null
        internal var page: Int = 0
        internal var infoId: Long = 0

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_info_popup, container, false)
            titleView = rootView.findViewById<View>(R.id.info_popup_title_row) as TextView
            bodyView = rootView.findViewById<View>(R.id.info_popup_body_row) as ExpandableTextView
            imageView = rootView.findViewById<View>(R.id.info_popup_image_row) as ImageView
            timeView = rootView.findViewById<View>(R.id.info_popup_time_row) as TextView
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            titleView.text = CommonUtils.getStringFragmentArgument(arguments, ARG_TITLE, "")

            val collapseStatus = SparseBooleanArray()
            collapseStatus.put(0, true)

            bodyView.setText(CommonUtils.getStringFragmentArgument(arguments, ARG_BODY, ""), collapseStatus, 0)

            timeView.text = DateStringUtils.getDateTimeInString(CommonUtils.getSerializableFragmentArgument(arguments, ARG_TIME, Calendar.getInstance().time) as Date, null)

            val photo = CommonUtils.getStringFragmentArgument(arguments, ARG_PHOTO, "")
            if (!TextUtils.isEmpty(photo) && URLUtil.isValidUrl(photo)) {
                imageView.visibility = View.VISIBLE
                Picasso.get().load(photo).error(R.drawable.ic_error).into(imageView)
            } else {
                imageView.visibility = View.GONE
                imageView.setImageBitmap(null)
            }


            action = CommonUtils.getSerializableFragmentArgument(arguments, ARG_ACTION, null) as Class<*>

            page = CommonUtils.getIntFragmentArgument(arguments, ARG_PAGE, -1)

            infoId = CommonUtils.getLongFragmentArgument(arguments, ARG_INFO_ID, -1)

            view.tag = infoId

            view.setOnClickListener { view ->
                if (action != null) {
                    goAction(activity!!, action, (activity as InfoPopup).extraData, view.tag as Long)
                }
            }
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            private val ARG_TITLE = "title"
            private val ARG_BODY = "body"
            private val ARG_PHOTO = "photo"
            private val ARG_URL = "url"
            private val ARG_TIME = "time"

            private val ARG_ACTION = "action"
            private val ARG_PAGE = "page"
            private val ARG_INFO_ID = "info_id"
            fun newInstance(sectionNumber: Int, title: String?, body: String?, photo: String?, url: String?, time: Date, action: Class<*>?, page: Int, infoId: Long): InfoPopupFragment {
                val fragment = InfoPopupFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putString(ARG_TITLE, title)
                args.putString(ARG_BODY, body)
                args.putString(ARG_PHOTO, photo)
                args.putString(ARG_URL, url)
                args.putSerializable(ARG_TIME, time)
                args.putSerializable(ARG_ACTION, action)
                args.putInt(ARG_PAGE, page)
                args.putLong(ARG_INFO_ID, infoId)
                fragment.arguments = args
                return fragment
            }
        }

    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val info = infoList!![position]
            val body = info.body
            return InfoPopupFragment.newInstance(position, info.title, body, info.photoUrl, info.infoUrl, info.timestamp, action, 1, info.id!!)
        }


        override fun getCount(): Int {
            return infoList!!.size
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return infoList!![position].title
        }
    }

    companion object {
        val EXTRA_CLASS = "extra_class"
        val EXTRA_DATA = "extra_data"
        val EXTRA_INFO_ID = "extra_info_id"


        private fun goAction(activity: Activity, action: Class<*>?, extraData: Bundle?, infoId: Long) {
            val intent = Intent(activity.applicationContext, action)
            intent.putExtra(EXTRA_DATA, extraData)
            if (infoId > -1) intent.putExtra(EXTRA_INFO_ID, infoId)
            activity.startActivity(intent)
            activity.finish()
        }


        fun start(context: Context, actionClass: Class<*>, extraData: Bundle) {
            val intent = Intent(context.applicationContext, InfoPopup::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_CLASS, actionClass)
            intent.putExtra(EXTRA_DATA, extraData)

            context.startActivity(intent)
        }
    }
}
