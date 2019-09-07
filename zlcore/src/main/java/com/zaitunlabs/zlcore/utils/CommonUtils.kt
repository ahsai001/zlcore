package com.zaitunlabs.zlcore.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.app.Service
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.PowerManager
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import android.util.Base64

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.zaitunlabs.zlcore.BuildConfig
import com.zaitunlabs.zlcore.R

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.UUID

object CommonUtils {

    val INFORMATION_TYPE_TOAST = 1
    val INFORMATION_TYPE_SNACKBAR = 2
    val INFORMATION_TYPE_DIALOG = 3

    val modelNumber: String
        get() {
            var model = ""
            try {
                model = CommonUtils.urlEncode(Build.MODEL)
            } catch (e: UnsupportedEncodingException) {
                model = Build.MODEL
            }

            return model
        }

    ////e.printStackTrace();
    val osVersion: String
        get() {
            var osVersion = ""
            try {
                osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE)
            } catch (e: UnsupportedEncodingException) {
            }

            return osVersion
        }

    ////e.printStackTrace();
    val userAgent: String
        get() {
            var userAgent = ""
            try {
                userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"))
            } catch (e: UnsupportedEncodingException) {
            }

            return userAgent
        }

    val indonesianLocale: Locale
        get() = Locale("in", "ID")


    internal var isWifiConn = false
    internal var isMobileConn = false


    private var uniqueID: String? = null
    private val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"


    val libraryVersionName: String
        get() = BuildConfig.VERSION_NAME

    val libraryVersionCode: Int
        get() = BuildConfig.VERSION_CODE

    fun getPackageName(ctx: Context): String {
        return ctx.packageName
    }

    fun getIDResource(ctx: Context, folder: String, filename: String): Int {
        var fn: String? = null
        if (filename.contains(".")) {
            fn = filename.substring(0, filename.lastIndexOf('.'))
        } else {
            fn = filename
        }
        return ctx.resources.getIdentifier(fn, folder,
                getPackageName(ctx))
    }

    fun getIntArrayResource(context: Context, folder: String,
                            filename: String): IntArray? {
        var x: IntArray? = null
        try {
            val clazz = Class.forName(getPackageName(context) + ".R$"
                    + folder)
            try {
                var fn: String? = null
                if (filename.contains(".")) {
                    fn = filename.substring(0, filename.lastIndexOf('.'))
                } else {
                    fn = filename
                }
                if (clazz != null)
                    x = clazz.getDeclaredField(fn!!).get(null) as IntArray
            } catch (e: IllegalArgumentException) {
                //e.printStackTrace();
            } catch (e: SecurityException) {
                //e.printStackTrace();
            } catch (e: IllegalAccessException) {
                //e.printStackTrace();
            } catch (e: NoSuchFieldException) {
                //e.printStackTrace();
            }

        } catch (e: ClassNotFoundException) {
            //e.printStackTrace();
        }

        return x
    }

    fun getIntResource(context: Context, folder: String,
                       filename: String): Int {
        var x = 0
        try {
            val clazz = Class.forName(getPackageName(context) + ".R$"
                    + folder)
            try {
                var fn: String? = null
                if (filename.contains(".")) {
                    fn = filename.substring(0, filename.lastIndexOf('.'))
                } else {
                    fn = filename
                }
                if (clazz != null)
                    x = clazz.getDeclaredField(fn!!).getInt(null)
            } catch (e: IllegalArgumentException) {
                //e.printStackTrace();
            } catch (e: SecurityException) {
                //e.printStackTrace();
            } catch (e: IllegalAccessException) {
                //e.printStackTrace();
            } catch (e: NoSuchFieldException) {
                //e.printStackTrace();
            }

        } catch (e: ClassNotFoundException) {
            //e.printStackTrace();
        }

        return x
    }

    fun getBooleanResource(context: Context, folder: String,
                           filename: String): Boolean {
        var x = false
        try {
            val clazz = Class.forName(getPackageName(context) + ".R$"
                    + folder)
            try {
                var fn: String? = null
                if (filename.contains(".")) {
                    fn = filename.substring(0, filename.lastIndexOf('.'))
                } else {
                    fn = filename
                }
                if (clazz != null)
                    x = clazz.getDeclaredField(fn!!).getBoolean(null)
            } catch (e: IllegalArgumentException) {
                //e.printStackTrace();
            } catch (e: SecurityException) {
                //e.printStackTrace();
            } catch (e: IllegalAccessException) {
                //e.printStackTrace();
            } catch (e: NoSuchFieldException) {
                //e.printStackTrace();
            }

        } catch (e: ClassNotFoundException) {
            //e.printStackTrace();
        }

        return x
    }


    fun showInfo(context: Context, title: String, msg: String, nextCode: Runnable?): androidx.appcompat.app.AlertDialog {
        var alert: androidx.appcompat.app.AlertDialog? = null
        val builder = androidx.appcompat.app.AlertDialog.Builder(context, com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
        builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
                context.getString(R.string.zlcore_general_wording_close)) { dialog, id ->
            dialog.cancel()
            nextCode?.run()
        }
        alert = builder.create()
        alert.setTitle(title)
        alert.show()
        return alert
    }

    fun showInfo(context: Context?, title: String, msg: String): androidx.appcompat.app.AlertDialog {
        var alert: androidx.appcompat.app.AlertDialog? = null
        val builder = androidx.appcompat.app.AlertDialog.Builder(context!!, com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
        builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
                context.getString(R.string.zlcore_general_wording_close)) { dialog, id -> dialog.cancel() }
        alert = builder.create()
        alert.setTitle(title)
        alert.show()
        return alert
    }

    fun showGlobalInfo(context: Context, title: String, msg: String): androidx.appcompat.app.AlertDialog {
        var alert: androidx.appcompat.app.AlertDialog? = null
        val builder = androidx.appcompat.app.AlertDialog.Builder(context, com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
        builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
                context.getString(R.string.zlcore_general_wording_close)) { dialog, id -> dialog.cancel() }
        alert = builder.create()
        alert.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        alert.setCanceledOnTouchOutside(false)
        alert.setTitle(title)
        alert.show()
        return alert
    }


    fun showDialog1Option(context: Context, title: String, msg: String, strOption1: String, option1: Runnable): androidx.appcompat.app.AlertDialog {
        return showDialog3OptionWithIcon(context, null, title, msg, strOption1, option1, null, null, null, null)
    }

    fun showDialog2Option(context: Context, title: String, msg: String, strOption1: String, option1: Runnable, strOption2: String, option2: Runnable): androidx.appcompat.app.AlertDialog {
        return showDialog3OptionWithIcon(context, null, title, msg, strOption1, option1, strOption2, option2, null, null)
    }

    fun showDialog3Option(context: Context, title: String, msg: String, strOption1: String, option1: Runnable, strOption2: String, option2: Runnable, strOption3: String, option3: Runnable): androidx.appcompat.app.AlertDialog {
        return showDialog3OptionWithIcon(context, null, title, msg, strOption1, option1, strOption2, option2, strOption3, option3)
    }

    fun showDialog3OptionWithIcon(context: Context, icon: Drawable?, title: String, msg: String, strOption1: String, option1: Runnable, strOption2: String?, option2: Runnable?, strOption3: String?, option3: Runnable?): androidx.appcompat.app.AlertDialog {
        return showDialog3OptionWithIcon(context, icon, title, msg, strOption1, option1, true, strOption2, option2, true, strOption3, option3, true)
    }

    fun showDialog3OptionWithIcon(context: Context, icon: Drawable?, title: String, msg: String, strOption1: String?, option1: Runnable?, dismissByOption1: Boolean, strOption2: String?, option2: Runnable?, dismissByOption2: Boolean, strOption3: String?, option3: Runnable?, dismissByOption3: Boolean): androidx.appcompat.app.AlertDialog {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context,
                com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
        builder.setMessage(fromHtml(msg)).setCancelable(false)
        if (icon != null) {
            builder.setIcon(icon)
        }

        if (strOption2 != null) {
            builder.setNeutralButton(strOption2, null)
        }

        if (strOption1 != null) {
            builder.setPositiveButton(strOption1, null)
        }

        if (strOption3 != null) {
            builder.setNegativeButton(strOption3, null)
        }

        val alert = builder.create()
        alert.setTitle(title)
        alert.show()

        //set custom button
        if (strOption2 != null) {
            alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                option2?.run()
                if (dismissByOption2) alert.dismiss()
            }
        }

        if (strOption1 != null) {
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                option1?.run()
                if (dismissByOption1) alert.dismiss()
            }
        }

        if (strOption3 != null) {
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                option3?.run()
                if (dismissByOption3) alert.dismiss()
            }

        }

        return alert
    }

    fun showDialog2OptionCustomView(context: Context, customView: View,
                                    title: String,
                                    strOption1: String, option1: Runnable,
                                    strOption2: String, option2: Runnable): androidx.appcompat.app.AlertDialog {
        return showDialog2OptionCustomView(context, customView, title, strOption1, option1, true, strOption2, option2, true)
    }


    fun showDialog2OptionCustomView(context: Context, customView: View,
                                    title: String,
                                    strOption1: String?, option1: Runnable?, dismissByOption1: Boolean,
                                    strOption2: String?, option2: Runnable?, dismissByOption2: Boolean): androidx.appcompat.app.AlertDialog {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context,
                com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
        builder.setCancelable(false)
        builder.setView(customView)

        if (strOption2 != null) {
            builder.setNeutralButton(strOption2, null)
        }

        if (strOption1 != null) {
            builder.setPositiveButton(strOption1, null)
        }

        val alert = builder.create()

        alert.setTitle(title)
        alert.show()

        //set custom button
        if (strOption2 != null) {
            alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                option2?.run()
                if (dismissByOption2) alert.dismiss()
            }
        }

        if (strOption1 != null) {
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                option1?.run()
                if (dismissByOption1) alert.dismiss()
            }
        }

        return alert
    }

    fun fromHtml(html: String?): Spanned {
        val result: Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            result = Html.fromHtml(html)
        }
        return result
    }

    // get String from intent
    fun getStringIntent(intent: Intent?, name: String,
                        defaultvalue: String): String {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getStringExtra(name)
        }
        return retval
    }

    // get boolean from intent
    fun getBooleanIntent(intent: Intent?, name: String,
                         defaultvalue: Boolean): Boolean {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getBooleanExtra(name, defaultvalue)
        }
        return retval
    }

    // get Array String from intent
    fun getArrayStringIntent(intent: Intent?, name: String, defaultvalue: Array<String>): Array<String> {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getStringArrayExtra(name)
        }
        return retval
    }

    // get Array String from intent
    fun getArrayListStringIntent(intent: Intent?, name: String, defaultvalue: ArrayList<*>): ArrayList<*> {
        var retval: ArrayList<*> = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getStringArrayListExtra(name)
        }
        return retval
    }

    // get int from intent
    fun getIntIntent(intent: Intent?, name: String, defaultvalue: Int): Int {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getIntExtra(name, 0)
        }
        return retval
    }

    fun getParcelableIntent(intent: Intent?, name: String, defaultvalue: Parcelable): Parcelable {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getParcelableExtra(name)
        }
        return retval
    }

    fun getSerializableIntent(intent: Intent?, name: String, defaultvalue: Serializable): Serializable {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getSerializableExtra(name)
        }
        return retval
    }

    // get long from intent
    fun getLongIntent(intent: Intent?, name: String, defaultvalue: Long): Long {
        var retval = defaultvalue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getLongExtra(name, 0)
        }
        return retval
    }

    // get Bundle from intent
    fun getBundleIntent(intent: Intent?, name: String, defaltValue: Bundle): Bundle {
        var retval = defaltValue
        if (intent != null) {
            if (intent.hasExtra(name))
                retval = intent.getBundleExtra(name)
        }
        return retval
    }

    // get String from Argument Fragment
    fun getStringFragmentArgument(argument: Bundle?, name: String, defaultValue: String?): String? {
        var retval = defaultValue
        if (argument != null) {
            retval = argument.getString(name)
            if (retval == null) return defaultValue
        }
        return retval
    }

    // get int from Argument Fragment
    fun getIntFragmentArgument(argument: Bundle?, name: String, defaultValue: Int): Int {
        var retval = defaultValue
        if (argument != null) {
            retval = argument.getInt(name, defaultValue)
        }
        return retval
    }

    // get long from Argument Fragment
    fun getLongFragmentArgument(argument: Bundle?, name: String, defaultValue: Long): Long {
        var retval = defaultValue
        if (argument != null) {
            retval = argument.getLong(name, defaultValue)
        }
        return retval
    }

    fun getStringArrayListFragmentArgument(argument: Bundle?, name: String, defaultValue: ArrayList<String>): ArrayList<String>? {
        var retval: ArrayList<String>? = defaultValue
        if (argument != null) {
            retval = argument.getStringArrayList(name)
        }
        return retval
    }

    // get boolean from Argument Fragment
    fun getBooleanFragmentArgument(argument: Bundle?, name: String, defaultValue: Boolean): Boolean {
        var retval = defaultValue
        if (argument != null) {
            retval = argument.getBoolean(name, defaultValue)
        }
        return retval
    }

    // get Serializable from Argument Fragment
    fun getSerializableFragmentArgument(argument: Bundle?, name: String, defaultValue: Serializable?): Serializable? {
        var retval = defaultValue
        if (argument != null) {
            retval = argument.getSerializable(name)
        }
        return retval
    }

    fun getScreenWidth(context: Context): Int {
        val display = (context as Activity).windowManager
                .defaultDisplay
        return display.width
    }

    fun getScreenHeight(context: Context): Int {
        val display = (context as Activity).windowManager
                .defaultDisplay
        return display.height
    }

    /**
     * function to retrieve height of application (screenheight decrease by  height of bar)
     * @param context context of current activity or application
     * @return height of application
     */
    fun getAppHeight(context: Context): Int {
        return getScreenHeight(context) - getStatusBarHeight(context)
    }

    fun getStatusBarHeight(ctx: Context): Int {
        var result = 0
        val resourceId = ctx.resources.getIdentifier("status_bar_height",
                "dimen", "android")
        if (resourceId > 0) {
            result = ctx.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun isActivityFullScreen(ctx: Context): Boolean {
        return (ctx as Activity).window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    fun getFontHeight(text: String): Float {
        /*
		 * Paint tp = new Paint(); Rect bounds = new Rect(); //this will just
		 * retrieve the bounding rect for text tp.getTextBounds(text, 0,
		 * text.length(), bounds); int textHeight = bounds.height();
		 * Paint.FontMetrics metrics = tp.getFontMetrics(); int totalHeight =
		 * (int) (metrics.top - metrics.bottom);
		 */
        return 40f
    }

    fun getTextLength(text: String, fontsize: Float, tf: Typeface): Float {
        val p = Paint()
        p.textSize = fontsize
        p.typeface = tf
        return p.measureText(text)
    }

    fun getHeightSPercent(ctx: Context, percent: Float): Int {
        var pixel = 0f
        val screenheight = getScreenHeight(ctx)
        pixel = screenheight * percent / 100
        return pixel.toInt()
    }

    fun getWidthSPercent(ctx: Context, percent: Float): Int {
        var pixel = 0f
        val screenwidth = getScreenWidth(ctx)
        pixel = screenwidth * percent / 100
        return pixel.toInt()
    }

    fun getHeightPercent(ctx: Context, height: Int, percent: Float): Int {
        var pixel = 0f
        pixel = height * percent / 100
        return pixel.toInt()
    }

    fun getWidthPercent(ctx: Context, width: Int, percent: Float): Int {
        var pixel = 0f
        pixel = width * percent / 100
        return pixel.toInt()
    }


    fun getWidthRatio(context: Context): Double {
        return (getScreenWidth(context) / 100).toDouble()
    }

    fun getHeightRatio(context: Context): Double {
        val isFullScreen = isActivityFullScreen(context)
        return ((getScreenHeight(context) - if (isFullScreen) 0 else getStatusBarHeight(context)) / 100).toDouble()
    }

    fun getPercentWidthFromPixel(context: Context, pixelWidth: Float): Int {
        val widthRatio = getWidthRatio(context)
        return (pixelWidth / widthRatio).toInt()
    }

    fun getPercentHeightFromPixel(context: Context, pixelHeight: Float): Int {
        val heightRatio = getHeightRatio(context)
        return (pixelHeight / heightRatio).toInt()
    }

    fun getUpperRegionPercentage(percentFromCurrentRegion: Double, currentRegionPercentFromUpper: Double): Double {
        var result = 0.0
        try {
            result = percentFromCurrentRegion * currentRegionPercentFromUpper / 100
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        return result
    }

    fun setLayoutAnim_slidedownfromtop(panel: ViewGroup) {
        val set = AnimationSet(true)

        var animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 100
        set.addAnimation(animation)

        animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        animation.setDuration(300)
        set.addAnimation(animation)

        val controller = LayoutAnimationController(set, 0.1f)
        panel.layoutAnimation = controller
    }

    fun setViewAnim_Appearslidedownfromtop(view: View?, durationInMilis: Long): Animation {
        val set = AnimationSet(true)

        var animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = durationInMilis
        set.addAnimation(animation)

        animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        animation.setDuration(durationInMilis)
        set.addAnimation(animation)

        if (view != null) {
            val anim = view.animation
            if (anim != null) {
                anim.cancel()
                anim.reset()
            }
            view.clearAnimation()
            view.animation = set
        }
        return set
    }

    fun setViewAnim_disappearslideupfromBottom(view: View?, durationInMilis: Long): Animation {
        val set = AnimationSet(true)

        var animation: Animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = durationInMilis
        set.addAnimation(animation)

        animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f
        )
        animation.setDuration(durationInMilis)
        set.addAnimation(animation)

        if (view != null) {
            val anim = view.animation
            if (anim != null) {
                anim.cancel()
                anim.reset()
            }
            view.clearAnimation()
            view.animation = set
        }
        return set
    }


    fun getImageDimension(context: Context, resourceID: Int): Point {
        val options = Options()
        options.inJustDecodeBounds = true
        //Returns null, sizes are in the options variable
        BitmapFactory.decodeResource(context.resources, resourceID, options)
        val width = options.outWidth
        val height = options.outHeight
        return Point(width, height)
    }

    fun sendEmail(ctx: Context, feedbackTitle: String, bodyEmailInHTML: String, TO: Array<String>, CC: Array<String>?): Boolean {
        val email = Intent(Intent.ACTION_SEND)
        email.putExtra(Intent.EXTRA_EMAIL, TO)
        if (CC != null)
            email.putExtra(Intent.EXTRA_CC, CC)
        email.putExtra(Intent.EXTRA_TEXT, fromHtml(bodyEmailInHTML))
        email.type = "message/rfc822"
        try {
            if (CommonUtils.isApplicationContext(ctx)) {
                val intent = PendingIntent.getActivity(ctx, 22, Intent.createChooser(email, feedbackTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0)
                try {
                    intent.send()
                } catch (e: CanceledException) {
                    //e.printStackTrace();
                    return false
                }

            } else {
                ctx.startActivity(Intent.createChooser(email, feedbackTitle))
            }
            return true
        } catch (ex: android.content.ActivityNotFoundException) {
            return false
        }

    }

    fun isOdd(`val`: Int): Boolean {
        return `val` and 0x01 != 0
    }

    @JvmOverloads
    fun getBitmapFromView(v: View, viewWidth: Int, viewHeight: Int, screenshotX: Int = 0, screenshotY: Int = 0, screenshotWidth: Int = viewWidth, screenshotHeight: Int = viewHeight): Bitmap? {
        var bmp: Bitmap? = null
        v.isDrawingCacheEnabled = true
        v.measure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        if (screenshotWidth != 0 && screenshotHeight != 0) {
            bmp = Bitmap.createBitmap(v.drawingCache, screenshotX, screenshotY, screenshotWidth, screenshotHeight)
        }
        v.isDrawingCacheEnabled = false
        return bmp
    }

    fun sendEmail(context: Context, to: String, title: String, body: String, sendTitle: String?): Boolean {
        var sendTitle = sendTitle
        val uriText = "mailto:" + to +
                "?subject=" + Uri.encode(title) +
                "&body=" + Uri.encode(body)

        val uri = Uri.parse(uriText)
        val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        if (sendTitle == null)
            sendTitle = context.getString(R.string.zlcore_common_utils_send_email)
        try {

            if (CommonUtils.isApplicationContext(context)) {
                val intent = PendingIntent.getActivity(context, 22, Intent.createChooser(emailIntent, sendTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0)
                try {
                    intent.send()
                } catch (e: CanceledException) {
                    //e.printStackTrace();
                    return false
                }

            } else {
                context.startActivity(Intent.createChooser(emailIntent, sendTitle))
            }
            return true
        } catch (ex: android.content.ActivityNotFoundException) {
            return false
        }

    }

    fun shareContent(context: Context, shareTitle: String?, subject: String, body: String): Boolean {
        var shareTitle = shareTitle
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        if (shareTitle == null)
            shareTitle = context.getString(R.string.zlcore_common_utils_default_share_title)
        try {
            if (CommonUtils.isApplicationContext(context)) {
                val intent = PendingIntent.getActivity(context, 22, Intent.createChooser(shareIntent, shareTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0)
                try {
                    intent.send()
                } catch (e: CanceledException) {
                    //e.printStackTrace();
                    return false
                }

            } else {
                context.startActivity(Intent.createChooser(shareIntent, shareTitle))
            }
            return true
        } catch (ex: android.content.ActivityNotFoundException) {
            return false
        }

    }

    fun sendEmailWithAttachment(context: Context, recipient_address: Array<String>, title: String, body: String, sendTitle: String?, files: Array<File>): Boolean {
        var sendTitle = sendTitle
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "multipart/mixed"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient_address)

        val uris = ArrayList<Uri>()
        for (i in files.indices) {
            uris.add(Uri.fromFile(files[i]))
        }

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        if (sendTitle == null)
            sendTitle = context.getString(R.string.zlcore_common_utils_send_email)
        try {
            if (CommonUtils.isApplicationContext(context)) {
                val intent = PendingIntent.getActivity(context, 22, Intent.createChooser(emailIntent, sendTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0)
                try {
                    intent.send()
                } catch (e: CanceledException) {
                    //e.printStackTrace();
                    return false
                }

            } else {
                context.startActivity(Intent.createChooser(emailIntent, sendTitle))
            }
            return true
        } catch (ex: android.content.ActivityNotFoundException) {
            return false
        }

    }

    fun isApplicationContext(context: Context): Boolean {
        return if (context is Application)
            true
        else
            false
    }

    fun isActivityContext(context: Context): Boolean {
        return if (context is Activity)
            true
        else
            false
    }

    fun isServiceContext(context: Context): Boolean {
        return if (context is Service)
            true
        else
            false
    }


    fun getVersionCode(context: Context): Int {
        var v = -1
        try {
            v = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: NameNotFoundException) {
            //e.printStackTrace();
        }

        return v
    }

    fun getVersionName(context: Context): String {
        var v = ""
        try {
            v = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: NameNotFoundException) {
            //e.printStackTrace();
        }

        return v
    }

    fun openBrowser(context: Context, link: String): Boolean {
        var link = link
        if (TextUtils.isEmpty(link)) return false
        if (!link.startsWith("http://") && !link.startsWith("https://"))
            link = "http://$link"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)

        try {
            if (CommonUtils.isApplicationContext(context)) {
                val intent = PendingIntent.getActivity(context, 22, Intent.createChooser(i, "open link with :").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0)
                try {
                    intent.send()
                } catch (e: CanceledException) {
                    //e.printStackTrace();
                    return false
                }

            } else {
                context.startActivity(i)
            }
            return true
        } catch (ex: android.content.ActivityNotFoundException) {
            return false
        }

    }


    fun openUrlWithPackageName(context: Context, url: String, packageName: String): Boolean {
        val i = Intent(Intent.ACTION_VIEW)
        try {
            i.setPackage(packageName)
            i.data = Uri.parse(url)
            if (i.resolveActivity(context.packageManager) != null) {
                context.startActivity(i)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return openBrowser(context, url)
    }

    fun getViewDimension(v: View): Rect {

        /* //cara 1 asynchronous
		 	final TextView tv = (TextView)findViewById(R.id.image_test);
			ViewTreeObserver vto = tv.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			    @Override
			    public void onGlobalLayout() {
			        LayerDrawable ld = (LayerDrawable)tv.getBackground();
			        ld.setLayerInset(1, 0, tv.getHeight() / 2, 0, 0);

			        ViewTreeObserver obs = tv.getViewTreeObserver();

			        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			            obs.removeOnGlobalLayoutListener(this);
			        } else {
			            obs.removeGlobalOnLayoutListener(this);
			        }
			    }

			});
		 */


        /* //cara 2 asynchronous
		final View v;
		v.post(new Runnable() {
	        @Override
	        public void run() {
	            v.getHeight();
	            v.getWidth();
	            v.getMeasuredHeight();
	            v.getMeasuredWidth();
	        }
	    });
	    */


        // cara 3 synchronous
        val result = Rect()
        v.measure(0, 0)
        result.left = 0
        result.top = 0
        result.right = 0 + v.measuredWidth
        result.bottom = 0 + v.measuredHeight
        return result
    }

    fun getScreenSizeCategory(context: Context): String {
        var screenLayout = context.resources.configuration.screenLayout
        screenLayout = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        when (screenLayout) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> return "small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> return "normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> return "large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
            -> return "xlarge"
            else -> return "undefined"
        }
    }

    fun getDisplayMetricsDensityDPI(context: Context): Int {
        //dot per inch
        val density = context.resources.displayMetrics.densityDpi
        when (density) {
            DisplayMetrics.DENSITY_LOW -> DebugUtils.logI("DisplayMetrics", "LDPI : $density")
            DisplayMetrics.DENSITY_MEDIUM -> DebugUtils.logI("DisplayMetrics", "MHDPI : $density")
            DisplayMetrics.DENSITY_HIGH -> DebugUtils.logI("DisplayMetrics", "HDPI : $density")
            DisplayMetrics.DENSITY_XHIGH -> DebugUtils.logI("DisplayMetrics", "XHDPI : $density")
            DisplayMetrics.DENSITY_XXHIGH -> DebugUtils.logI("DisplayMetrics", "XXHDPI : $density")
            DisplayMetrics.DENSITY_XXXHIGH -> DebugUtils.logI("DisplayMetrics", "XXXHDPI : $density")
        }
        if (density < DisplayMetrics.DENSITY_LOW) {
            DebugUtils.logI("DisplayMetrics", "Very Low LDPI: $density")
        }
        if (density > DisplayMetrics.DENSITY_XXXHIGH) {
            DebugUtils.logI("DisplayMetrics", "Very High XXXHDPI : $density")
        }

        return density
    }


    fun getDisplayMetricsDensityDPIInString(context: Context): String {
        //dot per inch
        val density = context.resources.displayMetrics.densityDpi
        var densityString = ""
        when (density) {
            DisplayMetrics.DENSITY_LOW -> {
                DebugUtils.logI("DisplayMetrics", "LDPI : $density")
                densityString = "LDPI"
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                DebugUtils.logI("DisplayMetrics", "MDPI : $density")
                densityString = "MDPI"
            }
            DisplayMetrics.DENSITY_HIGH -> {
                DebugUtils.logI("DisplayMetrics", "HDPI : $density")
                densityString = "HDPI"
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                DebugUtils.logI("DisplayMetrics", "XHDPI : $density")
                densityString = "XHDPI"
            }
            DisplayMetrics.DENSITY_XXHIGH -> {
                DebugUtils.logI("DisplayMetrics", "XXHDPI : $density")
                densityString = "XXHDPI"
            }
            DisplayMetrics.DENSITY_XXXHIGH -> {
                DebugUtils.logI("DisplayMetrics", "XXXHDPI : $density")
                densityString = "XXXHDPI"
            }
        }
        if (density < DisplayMetrics.DENSITY_LOW) {
            DebugUtils.logI("DisplayMetrics", "Very Low LDPI: $density")
            densityString = "Very Low LDPI"
        }
        if (density > DisplayMetrics.DENSITY_XXXHIGH) {
            DebugUtils.logI("DisplayMetrics", "Very High XXXHDPI : $density")
            densityString = "Very High XXXHDPI"
        }

        return densityString
    }

    fun getDisplayMetricsScaledDensity(context: Context): Float {
        //untuk density 160 nilainya 1, 120 = 0.75, dll
        val scaleddensity = context.resources.displayMetrics.scaledDensity
        DebugUtils.logI("DisplayMetrics", "scaleddensity : $scaleddensity")
        return scaleddensity
    }


    fun getDisplayMetricsDensity(context: Context): Float {
        //untuk density 160 nilainya 1, 120 = 0.75, dll
        val density = context.resources.displayMetrics.density
        DebugUtils.logI("DisplayMetrics", "density : $density")
        return density
    }

    fun getDisplayMetricsRealXDensity(context: Context): Float {
        //pixel per inch
        val xdpi = context.resources.displayMetrics.xdpi
        DebugUtils.logI("DisplayMetrics", "xdpi : $xdpi")
        return xdpi
    }


    fun getDisplayMetricsRealYDensity(context: Context): Float {
        //pixel per inch
        val ydpi = context.resources.displayMetrics.ydpi
        DebugUtils.logI("DisplayMetrics", "ydpi : $ydpi")
        return ydpi
    }

    fun getDisplayMetricsScreenHeight(context: Context): Int {
        //pixel per inch
        val heightPixels = context.resources.displayMetrics.heightPixels
        DebugUtils.logI("DisplayMetrics", "heightPixels : $heightPixels")
        return heightPixels
    }


    fun getDisplayMetricsScreenWidth(context: Context): Int {
        //pixel per inch
        val widthPixels = context.resources.displayMetrics.widthPixels
        DebugUtils.logI("DisplayMetrics", "widthPixels : $widthPixels")
        return widthPixels
    }

    fun getIntAttrValue(context: Context, attr: Int): Int {
        var value = 0
        val styledAttributes = context.theme.obtainStyledAttributes(
                intArrayOf(attr))
        value = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return value
    }


    fun getActionBarHeight(context: Context): Int {
        var mActionBarSize = 0
        val styledAttributes = context.theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize))
        mActionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return mActionBarSize
    }

    fun getActionBarHeight2(context: Context): Int {
        var mActionBarSize = 0
        val tv = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarSize = TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        }
        return mActionBarSize
    }

    fun getNavigationHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }


    fun getNavigationBarSize(context: Context): Point {
        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()

        // navigation bar is not present
    }

    fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        if (Build.VERSION.SDK_INT >= 17) {
            display.getSize(size)
        }
        return size
    }

    fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = Display::class.java!!.getMethod("getRawWidth").invoke(display)
                size.y = Display::class.java!!.getMethod("getRawHeight").invoke(display)
            } catch (e: IllegalAccessException) {
                //e.printStackTrace();
            } catch (e: InvocationTargetException) {
                //e.printStackTrace();
            } catch (e: NoSuchMethodException) {
                //e.printStackTrace();
            }

        }


        return size
    }


    fun showToast(context: Context?, message: String): Toast? {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
            return toast
        }
        return null
    }


    fun getDipFromPixel2(context: Context, sizeInPixel: Int): Int {
        val density = context.resources.displayMetrics.density
        return (sizeInPixel / density).toInt()
    }

    fun getPixelFromDip2(context: Context, sizeInDip: Int): Int {
        val density = context.resources.displayMetrics.density
        return (sizeInDip * density + 0.5f).toInt()
    }

    fun getDipFromPixel(context: Context, sizeInPixel: Int): Float {
        val densityDPI = context.resources.displayMetrics.densityDpi.toFloat()
        return sizeInPixel * DisplayMetrics.DENSITY_DEFAULT / densityDPI
    }

    fun getPixelFromDip(context: Context, sizeInDip: Int): Float {
        val densityDPI = context.resources.displayMetrics.densityDpi.toFloat()
        return sizeInDip * densityDPI / DisplayMetrics.DENSITY_DEFAULT
    }


    fun getPixelFromComplexUnit(context: Context?, complexUnitType: Int, sizeInComplexUnit: Int): Float {
        return if (context == null) {
            0f
        } else TypedValue.applyDimension(complexUnitType, sizeInComplexUnit.toFloat(), context.resources.displayMetrics)
    }

    fun makeSnackBarMultiLine(snackbar: Snackbar, line: Int) {
        val snackbarView = snackbar.view
        val textView = snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.maxLines = line
    }

    @JvmOverloads
    fun showSnackBar(context: Context, message: String, isIndefinite: Boolean = false): Snackbar {
        return showSnackBar(context, message, context.getString(R.string.zlcore_default_snackbar_dismiss), isIndefinite)
    }

    fun showSnackBar(context: Context, message: String, dismissString: String, isIndefinite: Boolean): Snackbar {
        return showSnackBar(context, context.getString(R.string.zlcore_general_coordinator_layout_tagname), message, dismissString, isIndefinite)
    }

    private fun getCoordLayout(rootView: View, coordinatorLayoutTagname: String): View? {
        var coordView: View? = rootView.findViewWithTag(coordinatorLayoutTagname)
        if (coordView == null) {
            coordView = rootView.findViewById(android.R.id.content)
            if (coordView !is CoordinatorLayout) {
                if (coordView is ViewGroup) {
                    coordView = coordView.getChildAt(0)
                }
            }
        }
        return coordView
    }

    private fun getCoordLayout(rootView: View, coordinatorLayoutId: Int): View? {
        var coordView: View? = rootView.findViewById(coordinatorLayoutId)
        if (coordView == null) {
            coordView = rootView.findViewById(android.R.id.content)
            if (coordView !is CoordinatorLayout) {
                if (coordView is ViewGroup) {
                    coordView = coordView.getChildAt(0)
                }
            }
        }
        return coordView
    }


    fun showSnackBar(context: Context, coordinatorLayoutTagname: String, message: String, dismissString: String?, isIndefinite: Boolean): Snackbar {
        val rootView = (context as Activity).window.decorView.rootView
        val coordView = getCoordLayout(rootView, coordinatorLayoutTagname)
        val snackbar = Snackbar.make(coordView
                ?: rootView, message, if (isIndefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG).setAction(dismissString
                ?: "dismiss") {
            //do nothing, just dismiss
        }
        makeSnackBarMultiLine(snackbar, 30)
        snackbar.show()
        return snackbar
    }


    fun showSnackBar(context: Context, coordinatorLayoutId: Int, message: String, dismissString: String?): Snackbar {
        val rootView = (context as Activity).window.decorView.rootView
        val coordView = getCoordLayout(rootView, coordinatorLayoutId)
        val snackbar = Snackbar.make(coordView
                ?: rootView, message, Snackbar.LENGTH_LONG).setAction(dismissString ?: "dismiss") {
            //do nothing, just dismiss
        }
        makeSnackBarMultiLine(snackbar, 30)
        snackbar.show()
        return snackbar
    }

    fun showSnackBar(fragment: Fragment, coordinatorLayoutTagname: String, message: String, dismissString: String?): Snackbar {
        val rootView = fragment.view
        val coordView = getCoordLayout(rootView!!, coordinatorLayoutTagname)
        val snackbar = Snackbar.make(coordView
                ?: rootView, message, Snackbar.LENGTH_LONG).setAction(dismissString ?: "dismiss") {
            //do nothing, just dismiss
        }
        makeSnackBarMultiLine(snackbar, 30)
        snackbar.show()
        return snackbar
    }

    fun showSnackBar(fragment: Fragment, coordinatorLayoutTagname: String, message: String, dismissString: String?, runAfterClicked: Runnable?): Snackbar {
        val rootView = fragment.view
        val coordView = getCoordLayout(rootView!!, coordinatorLayoutTagname)
        val snackbar = Snackbar.make(coordView
                ?: rootView, message, Snackbar.LENGTH_LONG).setAction(dismissString ?: "dismiss") {
            runAfterClicked?.run()
        }
        makeSnackBarMultiLine(snackbar, 30)
        snackbar.show()
        return snackbar
    }

    fun showInformation(type: Int, context: Any?, message: String) {
        if (context != null) {
            if (type == INFORMATION_TYPE_TOAST) {
                if (context is Fragment) {
                    showToast(context.context, message)
                } else if (context is Context) {
                    showToast(context as Context?, message)
                }
            } else if (type == INFORMATION_TYPE_SNACKBAR) {
                if (context is Fragment) {
                    showSnackBar((context as Fragment?)!!,
                            context.getString(R.string.zlcore_general_coordinator_layout_tagname),
                            message,
                            context.getString(R.string.zlcore_snackbar_action_name_dismiss))
                } else if (context is Context) {
                    showSnackBar(context as Context?,
                            (context as Fragment).getString(R.string.zlcore_general_coordinator_layout_tagname),
                            message,
                            (context as Fragment).getString(R.string.zlcore_snackbar_action_name_dismiss), false)
                }
            } else if (type == INFORMATION_TYPE_DIALOG) {
                if (context is Fragment) {
                    showInfo(context.context, context.context!!.getString(R.string.zlcore_general_wording_information_title), message)
                } else if (context is Context) {
                    showInfo(context as Context?, (context as Fragment).context!!.getString(R.string.zlcore_general_wording_information_title), message)
                }
            } else {
                if (context is Fragment) {
                    showToast(context.context, message)
                } else if (context is Context) {
                    showToast(context as Context?, message)
                }
            }
        }
    }

    fun isAppInForeground(context: Context): Boolean {
        // Get the Activity Manager
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        //List< ActivityManager.RunningTaskInfo > task = manager.getRunningTasks(1);

        // Get the info we need for comparison.
        //ComponentName componentInfo = task.get(0).topActivity;

        //if(componentInfo.getPackageName().equals(PackageName)) return true;


        val process = manager.runningAppProcesses
        // Check if it matches our package name.
        return if (process[0].processName == context.packageName) true else false

        // If not then our app is not on the foreground.
    }


    fun getDetailContactWithNumber(context: Context, phoneNumber: String): Cursor? {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null
        }

        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        return context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI), null, null, null)
    }

    fun getContactIDWithNumber(context: Context, phoneNumber: String): Long {
        var result: Long = -1
        var contactDetail = getDetailContactWithNumber(context, phoneNumber)
        if (contactDetail != null && contactDetail.moveToFirst()) {
            result = contactDetail.getLong(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
        }
        if (contactDetail != null) {
            contactDetail.close()
            contactDetail = null
        }
        return result
    }


    fun getContactThumbnailPhotoURIWithNumber(context: Context, phoneNumber: String): String? {
        var result: String? = null
        var contactDetail = getDetailContactWithNumber(context, phoneNumber)
        if (contactDetail != null && contactDetail.moveToFirst()) {
            result = contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI))
        }
        if (contactDetail != null) {
            contactDetail.close()
            contactDetail = null
        }
        return result
    }

    fun getContactPhotoURIWithNumber(context: Context, phoneNumber: String): String? {
        var result: String? = null
        var contactDetail = getDetailContactWithNumber(context, phoneNumber)
        if (contactDetail != null && contactDetail.moveToFirst()) {
            result = contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI))
        }
        if (contactDetail != null) {
            contactDetail.close()
            contactDetail = null
        }
        return result
    }

    fun getContactNameWithNumber(context: Context, phoneNumber: String): String? {
        var result: String? = null
        var contactDetail = getDetailContactWithNumber(context, phoneNumber)
        if (contactDetail != null && contactDetail.moveToFirst()) {
            result = contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }
        if (contactDetail != null) {
            contactDetail.close()
            contactDetail = null
        }
        return result
    }


    fun getDrawableFromURI(context: Context, uri: Uri): Drawable? {
        var result: Drawable? = null
        try {
            var inputStream = context.contentResolver.openInputStream(uri)
            result = Drawable.createFromStream(inputStream, uri.toString())
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                }

                inputStream = null
            }
        } catch (e: FileNotFoundException) {
        }

        return result
    }


    fun getChargingState(context: Context): HashMap<String, Any> {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)

        val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val batteryPct = level / scale.toFloat()

        val result = HashMap<String, Any>()
        result["isCharging"] = isCharging
        result["usbCharge"] = usbCharge
        result["acCharge"] = acCharge
        result["batteryPct"] = batteryPct
        return result
    }


    fun addContact(context: Context, phoneNumber: String) {
        val contactIntent = Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, ContactsContract.Contacts.CONTENT_URI)
        contactIntent.data = Uri.parse("tel:$phoneNumber")//Add the mobile number here
        //contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, "smartcall-x"); //ADD contact name here
        context.startActivity(contactIntent)
    }


    fun copyPlainTextToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.primaryClip = clip
    }

    fun getPlainTextFromClipboard(context: Context): CharSequence? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip ?: return null

        val item = clipData.getItemAt(0)
        var pasteData: CharSequence? = item.text
        if (pasteData != null) {
            return pasteData
        } else {
            val pasteUri = item.uri
            if (pasteUri != null) {

                pasteData = pasteUri.toString()

                /*
				String MIME_TYPE_CONTACT = "vnd.android.cursor.item/vnd.example.contact";
				ContentResolver cr = context.getContentResolver();
				String uriMimeType = cr.getType(pasteUri);

				if (uriMimeType != null) {
					if (uriMimeType.equals(MIME_TYPE_CONTACT)) {
						Cursor pasteCursor = cr.query(pasteUri, null, null, null, null);
						if (pasteCursor != null) {
							if (pasteCursor.moveToFirst()) {
								// get the data from the Cursor here. The code will vary according to the
								// format of the data model.
							}
						}
						pasteCursor.close();
					}
				}
				*/

                return pasteData
            }
        }

        return null
    }

    fun setWindowSofInputMode(activity: Activity, softInputMode: Int) {
        activity.window.setSoftInputMode(softInputMode)
    }

    fun setWindowSofInputModeResize(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun setEditTextDoneKey(editText: EditText) {
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    fun setEditTextNextKey(editText: EditText) {
        editText.imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    fun showKeyboard(context: Context, view: View) {
        if (view.requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(context: Context) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(context: Context) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    /**
     * Method to check network availability
     *
     * @param context
     * @return true or false
     */
    fun isInternetConnected(context: Context): Boolean {
        val manager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo ?: return false
        return if (info.state != NetworkInfo.State.CONNECTED) {
            false
        } else true
    }

    fun getAndroidID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    fun getMeid(context: Context): String {
        val keyName = "cached-meid"
        var cachedMeid = Prefs.with(context).getString(keyName, "")
        if (TextUtils.isEmpty(cachedMeid)) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cachedMeid = tm.meid
            } else {
                cachedMeid = tm.deviceId
            }
            if (cachedMeid != null) {
                Prefs.with(context).save(keyName, cachedMeid)
            } else {
                cachedMeid = ""
            }
        }
        return cachedMeid
    }

    fun getImei(context: Context): String {
        val keyName = "cached-imei"
        var cachedImei = Prefs.with(context).getString(keyName, "")
        if (TextUtils.isEmpty(cachedImei)) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cachedImei = tm.imei
            } else {
                cachedImei = tm.deviceId
            }
            if (cachedImei != null) {
                Prefs.with(context).save(keyName, cachedImei)
            } else {
                cachedImei = ""
            }
        }
        return cachedImei
    }

    fun getCurrentDeviceLanguage(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 24) {
            context.resources.configuration.locales.get(0).language
        } else {
            context.resources.configuration.locale.language
        }
    }

    fun getCurrentDeviceCountry(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 24) {
            context.resources.configuration.locales.get(0).country
        } else {
            context.resources.configuration.locale.country
        }
    }

    fun getCurrentDeviceLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= 24) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }


    fun openAppSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    @JvmOverloads
    fun showLoadingDialog(context: Context, message: String? = null,
                          isIndeterminate: Boolean = true, cancelable: Boolean = false): androidx.appcompat.app.AlertDialog {
        var message = message
        if (TextUtils.isEmpty(message)) {
            message = context.getString(R.string.zlcore_warning_please_wait)
        }
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setView(R.layout.progressbar_horizontal_left)
        builder.setCancelable(cancelable)
        val dialog = builder.create()
        dialog.show()
        (dialog.findViewById<View>(R.id.progress_custom_progressbar) as ProgressBar).isIndeterminate = isIndeterminate
        (dialog.findViewById<View>(R.id.progress_custom_textview) as TextView).text = message
        return dialog
    }

    @JvmOverloads
    fun showLoadingSnackBar(context: Context, message: String? = null, isIndeterminate: Boolean = true, cancelable: Boolean = false): Snackbar {
        var message = message
        if (TextUtils.isEmpty(message)) {
            message = context.getString(R.string.zlcore_warning_please_wait)
        }
        val rootView = (context as Activity).window.decorView.rootView
        val snackbar = Snackbar.make(rootView, message!!,
                if (isIndeterminate) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        // Hide the text
        val textView = snackbarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        val color = textView.currentTextColor
        textView.visibility = View.INVISIBLE

        val customProgressView = LayoutInflater.from(context).inflate(R.layout.progressbar_horizontal_left, null)
        val newTextView = customProgressView.findViewById<TextView>(R.id.progress_custom_textview)
        newTextView.text = message
        newTextView.setTextColor(color)
        snackbarLayout.addView(customProgressView)

        if (cancelable) {
            snackbar.setAction(context.getString(R.string.zlcore_general_wording_dismiss)) {
                //do nothing, just dismiss
            }
        }
        snackbar.show()
        return snackbar
    }


    fun showPopupViewAsDropDown(context: Context,
                                popupView: View,
                                anchorView: View,
                                width: Int, height: Int,
                                gravity: Int, xOff: Int, yOff: Int,
                                outsideTouchable: Boolean,
                                dismissListener: PopupWindow.OnDismissListener): PopupWindow {
        val popupWindow = PopupWindow(popupView, if (width == 0) ViewGroup.LayoutParams.WRAP_CONTENT else width, if (height == 0) ViewGroup.LayoutParams.WRAP_CONTENT else height)

        popupWindow.animationStyle = android.R.style.Animation_Toast
        //popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.popup_chat_background));

        popupWindow.isOutsideTouchable = outsideTouchable
        popupWindow.isFocusable = outsideTouchable

        if (width > 0) {
            popupWindow.width = width
        }

        if (height > 0) {
            popupWindow.height = height
        }

        popupWindow.setOnDismissListener(dismissListener)
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED

        //popupWindow.showAsDropDown(anchorView, xOff, yOff);
        PopupWindowCompat.showAsDropDown(popupWindow, anchorView, xOff, yOff, gravity)
        return popupWindow
    }

    fun showPopupViewAtLocation(context: Context,
                                popupView: View,
                                parentView: View,
                                width: Int, height: Int,
                                gravity: Int,
                                xOff: Int, yOff: Int,
                                outsideTouchable: Boolean,
                                dismissListener: PopupWindow.OnDismissListener): PopupWindow {
        val popupWindow = PopupWindow(popupView, if (width == 0) ViewGroup.LayoutParams.WRAP_CONTENT else width, if (height == 0) ViewGroup.LayoutParams.WRAP_CONTENT else height)

        popupWindow.animationStyle = android.R.style.Animation_Toast
        //popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.popup_chat_background));

        popupWindow.isOutsideTouchable = outsideTouchable
        popupWindow.isFocusable = outsideTouchable

        if (width > 0) {
            popupWindow.width = width
        }

        if (height > 0) {
            popupWindow.height = height
        }

        popupWindow.setOnDismissListener(dismissListener)
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE

        popupWindow.showAtLocation(parentView, gravity, xOff, yOff)
        return popupWindow
    }

    fun showPopupMenu(context: Context,
                      menuResId: Int,
                      anchorView: View,
                      dismissListener: PopupMenu.OnDismissListener,
                      menuItemClickListener: PopupMenu.OnMenuItemClickListener): PopupMenu {
        val popup = PopupMenu(context, anchorView)
        popup.menuInflater.inflate(menuResId, popup.menu)
        popup.setOnDismissListener(dismissListener)
        popup.setOnMenuItemClickListener(menuItemClickListener)
        popup.show()
        return popup
    }

    fun openPlayStore(context: Context, packageFullName: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageFullName")))
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageFullName")))
        }

    }


    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null

        private fun setOnDateSetListener(onDateSetListener: DatePickerDialog.OnDateSetListener) {
            this.onDateSetListener = onDateSetListener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val defaultDate = CommonUtils.getSerializableFragmentArgument(arguments, ARG_DEFAULT_DATE, null) as Date?
            if (defaultDate != null) {
                c.time = defaultDate
            }
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(activity!!, this, year, month, day)
        }

        override fun onDateSet(datePicker: DatePicker, i: Int, i1: Int, i2: Int) {
            if (onDateSetListener != null) {
                onDateSetListener!!.onDateSet(datePicker, i, i1, i2)
            }
        }

        companion object {
            private val ARG_DEFAULT_DATE = "arg_default_date"

            fun newInstance(onDateSetListener: DatePickerDialog.OnDateSetListener, defaultDate: Date): DatePickerFragment {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.setOnDateSetListener(onDateSetListener)
                val arguments = Bundle()
                arguments.putSerializable(ARG_DEFAULT_DATE, defaultDate)
                datePickerFragment.arguments = arguments
                return datePickerFragment
            }
        }
    }

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        private var onTimeSetListener: TimePickerDialog.OnTimeSetListener? = null

        fun setOnTimeSetListener(onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
            this.onTimeSetListener = onTimeSetListener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val defaultTime = CommonUtils.getSerializableFragmentArgument(arguments, ARG_DEFAULT_TIME, null) as Date?
            if (defaultTime != null) {
                c.time = defaultTime
            }
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            return TimePickerDialog(activity, this, hour, minute,
                    DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(timePicker: TimePicker, i: Int, i1: Int) {
            if (onTimeSetListener != null) {
                onTimeSetListener!!.onTimeSet(timePicker, i, i1)
            }
        }

        companion object {
            private val ARG_DEFAULT_TIME = "arg_default_time"

            fun newInstance(onTimeSetListener: TimePickerDialog.OnTimeSetListener, defaultTime: Date): TimePickerFragment {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.setOnTimeSetListener(onTimeSetListener)
                val arguments = Bundle()
                arguments.putSerializable(ARG_DEFAULT_TIME, defaultTime)
                timePickerFragment.arguments = arguments
                return timePickerFragment
            }
        }
    }


    class AlertDialogFrament : DialogFragment() {
        private var title: String? = null
        private var message: String? = null
        private var strOption1: String? = null
        private var strOption2: String? = null
        private var strOption3: String? = null
        private var dismissByOption1: Boolean = false
        private var dismissByOption2: Boolean = false
        private var dismissByOption3: Boolean = false
        private var listenerFragmentTag: String? = null
        private var requestCode: Int = 0

        interface OnAlertDialogFragmentListener {
            fun onClick(activity: Activity?, alertDialogFrament: AlertDialogFrament, which: Int, requestCode: Int)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            listenerFragmentTag = CommonUtils.getStringFragmentArgument(arguments, ARG_LISTENER_FRAGMENT_TAG, null)
            if (!TextUtils.isEmpty(listenerFragmentTag)) {
                //set new target
                val listenerFragment = fragmentManager!!.findFragmentByTag(listenerFragmentTag)
                setTargetFragment(listenerFragment, requestCode)
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            title = CommonUtils.getStringFragmentArgument(arguments, ARG_TITLE, null)
            message = CommonUtils.getStringFragmentArgument(arguments, ARG_MESSAGE, null)
            strOption1 = CommonUtils.getStringFragmentArgument(arguments, ARG_STR_OPTION1, null)
            strOption2 = CommonUtils.getStringFragmentArgument(arguments, ARG_STR_OPTION2, null)
            strOption3 = CommonUtils.getStringFragmentArgument(arguments, ARG_STR_OPTION3, null)
            dismissByOption1 = CommonUtils.getBooleanFragmentArgument(arguments, ARG_DISMISS_OPTION1, true)
            dismissByOption2 = CommonUtils.getBooleanFragmentArgument(arguments, ARG_DISMISS_OPTION2, true)
            dismissByOption3 = CommonUtils.getBooleanFragmentArgument(arguments, ARG_DISMISS_OPTION3, true)
            requestCode = CommonUtils.getIntFragmentArgument(arguments, ARG_REQUEST_CODE, 0)

            val builder = androidx.appcompat.app.AlertDialog.Builder(activity!!,
                    com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle)
            builder.setMessage(fromHtml(message)).setCancelable(false)


            if (strOption2 != null) {
                builder.setNeutralButton(strOption2, null)
            }

            if (strOption1 != null) {
                builder.setPositiveButton(strOption1, null)
            }

            if (strOption3 != null) {
                builder.setNegativeButton(strOption3, null)
            }

            builder.setTitle(title)
            return builder.create()
        }

        override fun onResume() {
            try {
                val targetFragment = targetFragment
                val listener = (targetFragment ?: activity) as OnAlertDialogFragmentListener
                val alertDialog = dialog as AlertDialog?
                alertDialog!!.setTitle(title)
                //set custom button
                if (strOption2 != null) {
                    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                        listener.onClick(this@AlertDialogFrament.activity, this@AlertDialogFrament, OPTION2, requestCode)
                        if (dismissByOption2) alertDialog.dismiss()
                    }
                }

                if (strOption1 != null) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        listener.onClick(this@AlertDialogFrament.activity, this@AlertDialogFrament, OPTION1, requestCode)
                        if (dismissByOption1) alertDialog.dismiss()
                    }
                }

                if (strOption3 != null) {
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        listener.onClick(this@AlertDialogFrament.activity, this@AlertDialogFrament, OPTION3, requestCode)
                        if (dismissByOption3) alertDialog.dismiss()
                    }

                }
            } catch (e: ClassCastException) {
                throw RuntimeException("Please implement AlerDialogFragmentListener")
            }


            super.onResume()
        }

        companion object {
            private val ARG_TITLE = "arg_title"
            private val ARG_MESSAGE = "arg_message"
            private val ARG_STR_OPTION1 = "arg_str_option1"
            private val ARG_STR_OPTION2 = "arg_str_option2"
            private val ARG_STR_OPTION3 = "arg_str_option3"
            private val ARG_DISMISS_OPTION1 = "arg_dismiss_option1"
            private val ARG_DISMISS_OPTION2 = "arg_dismiss_option2"
            private val ARG_DISMISS_OPTION3 = "arg_dismiss_option3"
            private val ARG_LISTENER_FRAGMENT_TAG = "arg_listener_fragment_tag"
            val OPTION1 = 1
            val OPTION2 = 2
            val OPTION3 = 3
            private val ARG_REQUEST_CODE = "arg_request_code"

            fun newInstance(listenerFragmentTag: String, requestCode: Int, icon: Drawable?, title: String, msg: String, strOption1: String, dismissByOption1: Boolean, strOption2: String?, dismissByOption2: Boolean, strOption3: String?, dismissByOption3: Boolean): AlertDialogFrament {
                val alertDialogFrament = AlertDialogFrament()
                val arguments = Bundle()
                arguments.putString(ARG_TITLE, title)
                arguments.putString(ARG_MESSAGE, msg)
                arguments.putString(ARG_STR_OPTION1, strOption1)
                arguments.putString(ARG_STR_OPTION2, strOption2)
                arguments.putString(ARG_STR_OPTION3, strOption3)
                arguments.putBoolean(ARG_DISMISS_OPTION1, dismissByOption1)
                arguments.putBoolean(ARG_DISMISS_OPTION2, dismissByOption2)
                arguments.putBoolean(ARG_DISMISS_OPTION3, dismissByOption3)
                arguments.putString(ARG_LISTENER_FRAGMENT_TAG, listenerFragmentTag)
                arguments.putInt(ARG_REQUEST_CODE, requestCode)
                alertDialogFrament.arguments = arguments
                return alertDialogFrament
            }
        }
    }

    fun showDatePicker(savedInstanceState: Bundle, fragmentManager: FragmentManager, tag: String, onDateSetListener: DatePickerDialog.OnDateSetListener, onDismissListener: DialogInterface.OnDismissListener, defautlDate: Date): DatePickerFragment {
        return showDatePicker(savedInstanceState, false, fragmentManager, tag, onDateSetListener, onDismissListener, defautlDate)
    }

    fun showDatePicker(savedInstanceState: Bundle?, ignoreStateOrAlwaysCreateNew: Boolean, fragmentManager: FragmentManager, tag: String, onDateSetListener: DatePickerDialog.OnDateSetListener, onDismissListener: DialogInterface.OnDismissListener, defautlDate: Date): DatePickerFragment {
        var datePickerFragment: DatePickerFragment? = null
        if (savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
            datePickerFragment = DatePickerFragment.newInstance(onDateSetListener, defautlDate)
            datePickerFragment.showNow(fragmentManager, tag)
            datePickerFragment.dialog!!.setOnDismissListener(onDismissListener)
        } else {
            datePickerFragment = fragmentManager.findFragmentByTag(tag) as DatePickerFragment?
            if (datePickerFragment == null) {
                return showDatePicker(null, true, fragmentManager, tag, onDateSetListener, onDismissListener, defautlDate)
            } else {
                datePickerFragment.setOnDateSetListener(onDateSetListener)
                datePickerFragment.showNow(fragmentManager, tag)
                datePickerFragment.dialog!!.setOnDismissListener(onDismissListener)
            }
        }
        return datePickerFragment
    }

    fun showTimePicker(savedInstanceState: Bundle, fragmentManager: FragmentManager, tag: String, onTimeSetListener: TimePickerDialog.OnTimeSetListener, onDismissListener: DialogInterface.OnDismissListener, defaultTime: Date): TimePickerFragment {
        return showTimePicker(savedInstanceState, false, fragmentManager, tag, onTimeSetListener, onDismissListener, defaultTime)
    }

    fun showTimePicker(savedInstanceState: Bundle?, ignoreStateOrAlwaysCreateNew: Boolean, fragmentManager: FragmentManager, tag: String, onTimeSetListener: TimePickerDialog.OnTimeSetListener, onDismissListener: DialogInterface.OnDismissListener, defaultTime: Date): TimePickerFragment {
        var timePickerFragment: TimePickerFragment? = null
        if (savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
            timePickerFragment = TimePickerFragment.newInstance(onTimeSetListener, defaultTime)
            timePickerFragment.showNow(fragmentManager, tag)
            timePickerFragment.dialog!!.setOnDismissListener(onDismissListener)
        } else {
            timePickerFragment = fragmentManager.findFragmentByTag(tag) as TimePickerFragment?
            if (timePickerFragment == null) {
                return showTimePicker(null, true, fragmentManager, tag, onTimeSetListener, onDismissListener, defaultTime)
            } else {
                timePickerFragment.setOnTimeSetListener(onTimeSetListener)
                timePickerFragment.showNow(fragmentManager, tag)
                timePickerFragment.dialog!!.setOnDismissListener(onDismissListener)
            }
        }
        return timePickerFragment
    }

    fun showDialogFragment1Option(savedInstanceState: Bundle, ignoreStateOrAlwaysCreateNew: Boolean, activityOrFragment: Any, tag: String, listenerFragmentTag: String, requestCode: Int,
                                  title: String, msg: String, strOption1: String): AlertDialogFrament {
        return showDialogFragment3Option(savedInstanceState, ignoreStateOrAlwaysCreateNew, activityOrFragment, tag, listenerFragmentTag, requestCode, null, title, msg, strOption1, null, null)
    }

    fun showDialogFragment2Option(savedInstanceState: Bundle, ignoreStateOrAlwaysCreateNew: Boolean, activityOrFragment: Any, tag: String, listenerFragmentTag: String, requestCode: Int,
                                  title: String, msg: String, strOption1: String, strOption2: String): AlertDialogFrament {
        return showDialogFragment3Option(savedInstanceState, ignoreStateOrAlwaysCreateNew, activityOrFragment, tag, listenerFragmentTag, requestCode, null, title, msg, strOption1, strOption2, null)
    }


    fun showDialogFragment3Option(savedInstanceState: Bundle, ignoreStateOrAlwaysCreateNew: Boolean, activityOrFragment: Any, tag: String, listenerFragmentTag: String, requestCode: Int,
                                  icon: Drawable?, title: String, msg: String, strOption1: String, strOption2: String?, strOption3: String?): AlertDialogFrament {
        return showDialogFragment3OptionWithIcon(savedInstanceState, ignoreStateOrAlwaysCreateNew, activityOrFragment, tag, listenerFragmentTag, requestCode, icon, title, msg, strOption1, true, strOption2, true, strOption3, true)
    }

    fun showDialogFragment3OptionWithIcon(savedInstanceState: Bundle?, ignoreStateOrAlwaysCreateNew: Boolean, activityOrFragment: Any, tag: String, listenerFragmentTag: String, requestCode: Int,
                                          icon: Drawable?, title: String, msg: String, strOption1: String, dismissByOption1: Boolean, strOption2: String?, dismissByOption2: Boolean, strOption3: String?, dismissByOption3: Boolean): AlertDialogFrament {
        var alertDialogFrament: AlertDialogFrament? = null
        var fragmentManager: FragmentManager? = null
        if (activityOrFragment is AppCompatActivity) {
            fragmentManager = activityOrFragment.supportFragmentManager
        } else {
            fragmentManager = (activityOrFragment as Fragment).fragmentManager
        }
        if (savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
            alertDialogFrament = AlertDialogFrament.newInstance(listenerFragmentTag, requestCode, icon, title, msg, strOption1, dismissByOption1, strOption2, dismissByOption2, strOption3, dismissByOption3)
            alertDialogFrament.show(fragmentManager!!, tag)
        } else {
            alertDialogFrament = fragmentManager!!.findFragmentByTag(tag) as AlertDialogFrament?
            if (alertDialogFrament == null) {
                return showDialogFragment3OptionWithIcon(null, true, activityOrFragment, tag, listenerFragmentTag, requestCode, icon, title, msg, strOption1, dismissByOption1, strOption2, dismissByOption2, strOption3, dismissByOption3)
            }
        }
        return alertDialogFrament
    }

    fun makeTextViewUnderlined(textView: TextView) {
        val content = SpannableString(textView.text)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = content
    }

    private class IdleMonitorTextWatcher : TextWatcher {
        internal var delay: Long = 300 // 0.5 seconds after user stops typing
        internal var last_text_edit: Long = 0
        internal var handler = Handler()
        private var taskWillDoWhenIdle: Runnable? = null
        private val input_finish_checker = Runnable {
            if (System.currentTimeMillis() > last_text_edit + delay - 500) {
                if (taskWillDoWhenIdle != null) taskWillDoWhenIdle!!.run()
            }
        }

        constructor(taskWillDoWhenIdle: Runnable) {
            this.taskWillDoWhenIdle = taskWillDoWhenIdle
        }

        constructor(taskWillDoWhenIdle: Runnable, idleTimeInMS: Int) {
            this.taskWillDoWhenIdle = taskWillDoWhenIdle
            this.delay = idleTimeInMS.toLong()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            handler.removeCallbacks(input_finish_checker)
        }

        override fun afterTextChanged(s: Editable) {
            if (s.length >= 0) {
                last_text_edit = System.currentTimeMillis()
                handler.postDelayed(input_finish_checker, delay)
            }
        }
    }

    private class CountMonitorTextWatcher(private val taskWillDoWhenDone: Runnable, private val count: Int) : TextWatcher {
        internal var delay: Long = 300 // 0.5 seconds after user stops typing
        internal var handler = Handler()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (s.length == count) {
                handler.postDelayed(taskWillDoWhenDone, delay)
            }
        }
    }

    fun performTaskWhenUnFocus(view: View, taskWillDoWhenDone: Runnable?) {
        if (taskWillDoWhenDone != null) {
            view.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
                if (!focus) {
                    taskWillDoWhenDone.run()
                }
            }
        } else {
            view.onFocusChangeListener = null
        }
    }


    fun performTaskWhenTypeReachCount(editText: EditText, count: Int, taskWillDoWhenDone: Runnable) {
        editText.addTextChangedListener(CountMonitorTextWatcher(taskWillDoWhenDone, count))
    }

    fun performTaskWhenTypeIdle(editText: EditText, taskWillDoWhenIdle: Runnable) {
        editText.addTextChangedListener(IdleMonitorTextWatcher(taskWillDoWhenIdle))
    }

    fun performTaskWhenTypeIdle(editText: EditText, taskWillDoWhenIdle: Runnable, idleTimeInMS: Int) {
        editText.addTextChangedListener(IdleMonitorTextWatcher(taskWillDoWhenIdle, idleTimeInMS))
    }

    fun showContactPicker(activityOrFragment: Any, requestCode: Int) {
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        if (activityOrFragment is Activity) {
            activityOrFragment.startActivityForResult(contactPickerIntent, requestCode)
        } else if (activityOrFragment is Fragment) {
            activityOrFragment.startActivityForResult(contactPickerIntent, requestCode)
        }
    }

    class ContactPickerResult {
        var phoneNumber: String? = null
        var name: String? = null

        constructor(phoneNumber: String, name: String) {
            this.phoneNumber = phoneNumber
            this.name = name
        }

        constructor() {}
    }

    fun handleContactPicker(activityOrFragment: Any, targetRequestCode: Int, requestCode: Int, resultCode: Int, data: Intent): ContactPickerResult? {
        var contactPickerResult: ContactPickerResult? = null
        if (targetRequestCode == requestCode) {
            //handle result
            if (resultCode == Activity.RESULT_OK) {
                var activity: Activity? = null
                if (activityOrFragment is Activity) {
                    activity = activityOrFragment
                } else if (activityOrFragment is Fragment) {
                    activity = activityOrFragment.activity
                }
                var cursor: Cursor? = null
                try {
                    contactPickerResult = ContactPickerResult()
                    val uri = data.data
                    cursor = activity!!.contentResolver.query(uri!!, null, null, null, null)
                    cursor!!.moveToFirst()
                    val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    contactPickerResult.phoneNumber = cursor.getString(phoneIndex)
                    contactPickerResult.name = cursor.getString(nameIndex)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (cursor != null && !cursor.isClosed) {
                        cursor.close()
                    }
                }
            }

        }
        return contactPickerResult
    }

    fun getCurrencyString(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = '.'
        val pattern = "#,###.###"
        val decimalFormat = java.text.DecimalFormat(pattern, symbols)
        decimalFormat.groupingSize = 3
        return decimalFormat.format(value)
    }

    fun convertDate(inputDate: String, inputFormat: String, outputFormat: String, locale: Locale?): String? {
        /*

            G 	Era designator (before christ, after christ)
            y 	Year (e.g. 12 or 2012). Use either yy or yyyy.
            M 	Month in year. Number of M's determine length of format (e.g. MM, MMM or MMMMM)
            d 	Day in month. Number of d's determine length of format (e.g. d or dd)
            h 	Hour of day, 1-12 (AM / PM) (normally hh)
            H 	Hour of day, 0-23 (normally HH)
            m 	Minute in hour, 0-59 (normally mm)
            s 	Second in minute, 0-59 (normally ss)
            S 	Millisecond in second, 0-999 (normally SSS)
            E 	Day in week (e.g Monday, Tuesday etc.)
            D 	Day in year (1-366)
            F 	Day of week in month (e.g. 1st Thursday of December)
            w 	Week in year (1-53)
            W 	Week in month (0-5)
            a 	AM / PM marker
            k 	Hour in day (1-24, unlike HH's 0-23)
            K 	Hour in day, AM / PM (0-11)
            z 	Time Zone
            ' 	Escape for text delimiter
            ' 	Single quote

            Symbol  Meaning                Kind         Example
			D       day in year             Number        189
			E       day of week             Text          E/EE/EEE:Tue, EEEE:Tuesday, EEEEE:T
			F       day of week in month    Number        2 (2nd Wed in July)
			G       era designator          Text          AD
			H       hour in day (0-23)      Number        0
			K       hour in am/pm (0-11)    Number        0
			L       stand-alone month       Text          L:1 LL:01 LLL:Jan LLLL:January LLLLL:J
			M       month in year           Text          M:1 MM:01 MMM:Jan MMMM:January MMMMM:J
			S       fractional seconds      Number        978
			W       week in month           Number        2
			Z       time zone (RFC 822)     Time Zone     Z/ZZ/ZZZ:-0800 ZZZZ:GMT-08:00 ZZZZZ:-08:00
			a       am/pm marker            Text          PM
			c       stand-alone day of week Text          c/cc/ccc:Tue, cccc:Tuesday, ccccc:T
			d       day in month            Number        10
			h       hour in am/pm (1-12)    Number        12
			k       hour in day (1-24)      Number        24
			m       minute in hour          Number        30
			s       second in minute        Number        55
			w       week in year            Number        27
			G       era designator          Text          AD
			y       year                    Number        yy:10 y/yyy/yyyy:2010
			z       time zone               Time Zone     z/zz/zzz:PST zzzz:Pacific Standard

         */
        var outputDateString: String? = null
        val input = SimpleDateFormat(inputFormat, locale ?: Locale.getDefault())
        val output = SimpleDateFormat(outputFormat, locale ?: Locale.getDefault())
        try {
            val oneWayTripDate = input.parse(inputDate)
            outputDateString = output.format(oneWayTripDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateString
    }


    fun getCurrentTimeInString(locale: Locale): String {
        return getCurrentTimeInString("yyyy-MM-dd HH:mm:ss", locale)
    }

    fun getCurrentTimeInString(format: String, locale: Locale?): String {
        val df = SimpleDateFormat(format, locale ?: Locale.getDefault())
        return df.format(Calendar.getInstance().time)
    }

    fun hasNetworkConnection(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        isWifiConn = networkInfo != null && networkInfo.isConnected
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        isMobileConn = networkInfo != null && networkInfo.isConnected
        return isWifiConn || isMobileConn
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun showNotification(context: Context, title: String, content: String, nextActivity: Class<*>,
                         data: HashMap<String, Any>, appNameResId: Int, iconResId: Int, autocancel: Boolean, isHeadsUp: Boolean) {
        NotificationUtils.showNotification(context, title, content, nextActivity, data, appNameResId, iconResId, autocancel, isHeadsUp)
    }

    fun showNotification(context: Context, title: String, content: String, nextActivity: Class<*>,
                         data: HashMap<String, Any>, appNameResId: Int, iconResId: Int, notifID: Int, pendingIntentAction: String, autocancel: Boolean, isHeadsUp: Boolean) {
        NotificationUtils.showNotification(context, title, content, nextActivity, data, appNameResId, iconResId, notifID, pendingIntentAction, autocancel, isHeadsUp)
    }

    fun showNotification(context: Context, title: String, content: String, imageUrl: String,
                         nextIntentType: Int, nextIntent: Intent,
                         deleteIntentType: Int, deleteIntent: Intent,
                         data: Map<String, Any>, appNameResId: Int, iconResId: Int, notifID: Int, pendingIntentAction: String, autocancel: Boolean, isHeadsUp: Boolean) {
        NotificationUtils.showNotification(context, title, content, imageUrl, nextIntentType, nextIntent, deleteIntentType, deleteIntent, data, appNameResId, iconResId, notifID, pendingIntentAction, autocancel, isHeadsUp)
    }

    fun showNotification(context: Context, title: String, content: String, imageUrl: String,
                         nextPendingIntent: PendingIntent,
                         deletePendingIntent: PendingIntent,
                         fullScreenPendingIntent: PendingIntent,
                         soundUri: Uri,
                         appNameResId: Int, iconResId: Int, notifID: Int,
                         autocancel: Boolean, isHeadsUp: Boolean) {
        NotificationUtils.showNotification(context, title, content, imageUrl, nextPendingIntent, deletePendingIntent, fullScreenPendingIntent, soundUri, appNameResId, iconResId, notifID, autocancel, isHeadsUp)
    }


    fun runCodeInWakeLock(context: Context, tag: String, runnable: Runnable) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag)
        wl.acquire()
        runnable.run()
        wl.release()
    }


    fun getRealPathFromMediaStoreUri(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    fun showFilePickerOpenDocument(activityOrFragment: Any, mimeType: String, requestCode: Int) {
/*
			image/*
			audio/*
			video/*
			dll

		 */
		val intent = Intent()
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
{
intent.setAction(Intent.ACTION_OPEN_DOCUMENT)
}
else
{
intent.setAction(Intent.ACTION_GET_CONTENT)
}
intent.addCategory(Intent.CATEGORY_OPENABLE)
intent.setType(mimeType)
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(intent, requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(intent, requestCode)
}
}

 fun showFilePickerGetContent(activityOrFragment:Any, mimeType:String, requestCode:Int) {
 /*
			image/*
			audio/*
			video/*
			dll

		 */
		val intent = Intent()
intent.setAction(Intent.ACTION_GET_CONTENT)
intent.addCategory(Intent.CATEGORY_OPENABLE)
intent.setType(mimeType)
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(intent, requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(intent, requestCode)
}
}

 fun handleFilePickerData(activityOrFragment:Any, targetRequestCode:Int, requestCode:Int, resultCode:Int, data:Intent):Uri? {
var fileResultUri:Uri? = null
if (targetRequestCode == requestCode)
{
if (resultCode == Activity.RESULT_OK)
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
}
fileResultUri = data.getData()
}
}
return fileResultUri
}

 fun handleFilePickerDataString(activityOrFragment:Any, targetRequestCode:Int, requestCode:Int, resultCode:Int, data:Intent):String? {
var fileResultUri:String? = null
if (targetRequestCode == requestCode)
{
if (resultCode == Activity.RESULT_OK)
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
}
fileResultUri = data.getDataString()
}
}
return fileResultUri
}

 fun showFilePickerFromMediaStore(activityOrFragment:Any, mediaStoreUri:Uri, requestCode:Int) {
 /*
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		MediaStore.Images.Media.INTERNAL_CONTENT_URI
		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		MediaStore.Audio.Media.INTERNAL_CONTENT_URI
		MediaStore.Video.Media.EXTERNAL_CONTENT_URI
		MediaStore.Video.Media.INTERNAL_CONTENT_URI
		*/
		val pickPhoto = Intent(Intent.ACTION_PICK, mediaStoreUri)
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(pickPhoto, requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(pickPhoto, requestCode)
}
}


 fun handleFilePickerFromMediaStore(activityOrFragment:Any, targetRequestCode:Int, requestCode:Int, resultCode:Int, data:Intent):String? {
var imageResultStringPath:String? = null
if (targetRequestCode == requestCode)
{
if (resultCode == Activity.RESULT_OK)
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
imageResultStringPath = getRealPathFromMediaStoreUri(activity, data.getData())
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
imageResultStringPath = getRealPathFromMediaStoreUri(fragment.getActivity()!!, data.getData())
}
}
}
return imageResultStringPath
}


 fun showImageCapture(activityOrFragment:Any, requestCode:Int) {
val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(takePicture, requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(takePicture, requestCode)
}
}

 fun showVideoCapture(activityOrFragment:Any, requestCode:Int) {
val takePicture = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(takePicture, requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(takePicture, requestCode)
}
}

 fun handleImageVideoCapture(activityOrFragment:Any, targetRequestCode:Int, requestCode:Int, resultCode:Int, data:Intent):String? {
var imageResultStringPath:String? = null
if (targetRequestCode == requestCode)
{
if (resultCode == Activity.RESULT_OK)
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
imageResultStringPath = getRealPathFromMediaStoreUri(activity, data.getData())
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
imageResultStringPath = getRealPathFromMediaStoreUri(fragment.getActivity()!!, data.getData())
}
}

}
return imageResultStringPath
}

 fun getBitmapFromURL(strURL:String):Bitmap? {
try
{
val url = URL(strURL)
val connection = url.openConnection() as HttpURLConnection
connection.setDoInput(true)
connection.connect()
val input = connection.getInputStream()
return BitmapFactory.decodeStream(input)
}
catch (e:IOException) {
e.printStackTrace()
return null
}

}


 fun showDeviceSpecs(context:Context) {
var data = ""
data += "screenHeight in pixel : " + getScreenHeight(context) + "\n"
data += "screenHeight in dip : " + getDipFromPixel(context, getScreenHeight(context)) + "\n"

data += "screenWidth in pixel : " + getScreenWidth(context) + "\n"
data += "screenWidth in dip : " + getDipFromPixel(context, getScreenWidth(context)) + "\n"

data += "statusBar in pixel : " + getStatusBarHeight(context) + "\n"
data += "statusBar in dip : " + getDipFromPixel(context, getStatusBarHeight(context)) + "\n"

data += "actionBar in pixel : " + getActionBarHeight(context) + "\n"
data += "actionBar in dip : " + getDipFromPixel(context, getActionBarHeight(context)) + "\n"

data += "navigationBar in pixel : " + getNavigationHeight(context) + "\n"
data += "navigationBar in dip : " + getDipFromPixel(context, getNavigationHeight(context)) + "\n"


data += "\n\n"
data += "screen size type : " + getScreenSizeCategory(context) + "\n"
data += "density type : " + getDisplayMetricsDensityDPIInString(context) + "\n"
data += "density : " + getDisplayMetricsDensity(context) + "\n"
data += "scaled density : " + getDisplayMetricsScaledDensity(context) + "\n"
data += "density dot per inch : " + getDisplayMetricsDensityDPI(context) + "\n"
data += "density pixel per inch x : " + getDisplayMetricsRealXDensity(context) + "\n"
data += "density pixel per inch y : " + getDisplayMetricsRealYDensity(context) + "\n"
data += "\n\n"
data += "height screen : " + getDisplayMetricsScreenHeight(context) + "\n"
data += "width screen : " + getDisplayMetricsScreenWidth(context) + "\n"

data += "\n\n"
data += "1 dip =  " + getPixelFromDip(context, 1) + " pixel" + "\n"
data += "\n\n"
data += "1 px =  " + getDipFromPixel(context, 1) + " dip" + "\n"

showInfo(context, "Device Specs", data)
}

 fun prettifyUrl(url:String):String {
var prettyUrl = url
if (!Patterns.WEB_URL.matcher(url).matches())
{
try
{
prettyUrl = CommonUtils.urlEncode(url)
}
catch (e:UnsupportedEncodingException) {
e.printStackTrace()
}

}
return prettyUrl
}

@Throws(UnsupportedEncodingException::class)
 fun urlEncode(url:String?):String {
var result:String? = null
result = URLEncoder.encode(url, "UTF-8").replace("+", "%20")
return result
}


 fun callNumber(activity:Context, number:String):Boolean {
val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number))
if (callIntent.resolveActivity(activity.getPackageManager()) != null)
{
activity.startActivity(callIntent)
return true
}
return false
}


 fun navigateGMaps(activity:Context, latLong:String) {
val gmmIntentUri = Uri.parse("google.navigation:q=" + latLong)
val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
mapIntent.setPackage("com.google.android.apps.maps")
if (mapIntent.resolveActivity(activity.getPackageManager()) != null)
{
activity.startActivity(mapIntent)
}
else
{
openBrowser(activity, "https://www.google.com/maps?daddr=" + latLong)
}
}



 fun showPlacePicker(activityOrFragment:Any, requestCode:Int) {
val builder = PlacePicker.IntentBuilder()
try
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
activity.startActivityForResult(builder.build(activity), requestCode)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
fragment.startActivityForResult(builder.build(fragment.getActivity()!!), requestCode)
}
}
catch (e:GooglePlayServicesRepairableException) {
 //e.printStackTrace();
		}
catch (e:GooglePlayServicesNotAvailableException) {
 //e.printStackTrace();
		}

}

 fun handlePlacePicker(activityOrFragment:Any, targetRequestCode:Int, requestCode:Int, resultCode:Int, data:Intent):Place? {
var place:Place? = null
if (targetRequestCode == requestCode)
{
if (resultCode == Activity.RESULT_OK)
{
if (activityOrFragment is Activity)
{
val activity = (activityOrFragment as Activity)
place = PlacePicker.getPlace(activity, data)
}
else if (activityOrFragment is Fragment)
{
val fragment = (activityOrFragment as Fragment)
place = PlacePicker.getPlace(fragment.getActivity()!!, data)
}
}
}
return place
}


 fun toCamelCase(inputString:String):String {
var result = ""
if (inputString.length == 0)
{
return result
}
val firstChar = inputString.get(0)
val firstCharToUpperCase = Character.toUpperCase(firstChar)
result = result + firstCharToUpperCase
for (i in 1 until inputString.length)
{
val currentChar = inputString.get(i)
val previousChar = inputString.get(i - 1)
if (previousChar == ' ')
{
val currentCharToUpperCase = Character.toUpperCase(currentChar)
result = result + currentCharToUpperCase
}
else
{
val currentCharToLowerCase = Character.toLowerCase(currentChar)
result = result + currentCharToLowerCase
}
}
return result
}

 fun toSentenceCase(inputString:String):String {
var result = ""
if (inputString.length == 0)
{
return result
}
val firstChar = inputString.get(0)
val firstCharToUpperCase = Character.toUpperCase(firstChar)
result = result + firstCharToUpperCase
var terminalCharacterEncountered = false
val terminalCharacters = charArrayOf('.', '?', '!')
for (i in 1 until inputString.length)
{
val currentChar = inputString.get(i)
if (terminalCharacterEncountered)
{
if (currentChar == ' ')
{
result = result + currentChar
}
else
{
val currentCharToUpperCase = Character.toUpperCase(currentChar)
result = result + currentCharToUpperCase
terminalCharacterEncountered = false
}
}
else
{
val currentCharToLowerCase = Character.toLowerCase(currentChar)
result = result + currentCharToLowerCase
}
for (j in terminalCharacters.indices)
{
if (currentChar == terminalCharacters[j])
{
terminalCharacterEncountered = true
break
}
}
}
return result
}


 fun disableWindowInteraction(activity:Activity) {
activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

 fun enableWindowInteraction(activity:Activity) {
activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

@Synchronized  fun getRandomUUID(context:Context):String {
if (uniqueID == null)
{
val sharedPrefs = context.getSharedPreferences(
PREF_UNIQUE_ID, Context.MODE_PRIVATE)
uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)

if (uniqueID == null)
{
uniqueID = UUID.randomUUID().toString()
val editor = sharedPrefs.edit()
editor.putString(PREF_UNIQUE_ID, uniqueID)
editor.commit()
}
}

return uniqueID
}



 fun getDayName(calendar:Calendar, locale:Locale?):String {
return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
(if (locale == null) Locale.getDefault() else locale))
}


 fun encodeBase64(plainText:String):String {
val data = plainText.toByteArray(Charset.forName("UTF-8"))
return android.util.Base64.encodeToString(data, Base64.DEFAULT)
}

 fun decodeBase64(encodedText:String):String {
val data = Base64.decode(encodedText, Base64.DEFAULT)
return String(data, Charset.forName("UTF-8"))
}

 fun showBottomSheetDialog(context:Context, view:View?, layoutResId:Int):BottomSheetDialog {
val bottomSheetDialog = BottomSheetDialog(context)
if (view != null)
{
bottomSheetDialog.setContentView(view)
}
else
{
bottomSheetDialog.setContentView(layoutResId)
}
bottomSheetDialog.show()
return bottomSheetDialog
}

 fun showBottomSheetDialogFragment(fragmentManager:FragmentManager, layoutResId:Int, tag:String):CustomBottomSheetDialogFragment {
val customBottomSheetDialogFragment = CustomBottomSheetDialogFragment()
val argument = Bundle()
argument.putInt(CustomBottomSheetDialogFragment.ARG_LAYOUT_KEY, layoutResId)
customBottomSheetDialogFragment.setArguments(argument)
customBottomSheetDialogFragment.show(fragmentManager, tag)
return customBottomSheetDialogFragment
}

 class CustomBottomSheetDialogFragment:BottomSheetDialogFragment() {
private var rootView:View? = null
private var layoutResId:Int = 0

public override fun onCreate(savedInstanceState:Bundle?) {
super.onCreate(savedInstanceState)
layoutResId = getIntFragmentArgument(getArguments(), ARG_LAYOUT_KEY, -1)
}

public override fun setupDialog(dialog:Dialog, style:Int) {
super.setupDialog(dialog, style)
rootView = LayoutInflater.from(getContext()).inflate(layoutResId, null)
dialog.setContentView(rootView!!)
}

companion object {
 val ARG_LAYOUT_KEY = "arg_layout_id"
}
}

 fun getIndonesianPriceString(priceNumber:Int):String {
val localeID = indonesianLocale
val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
val formatedPrice = formatRupiah.format(priceNumber.toDouble())
val symbol = Currency.getInstance(localeID).getSymbol(localeID)
return formatedPrice.replace(symbol, symbol + " ") + ",-"
}


 fun getUriOfResFile(context:Context, folder:String, fileName:String) {
val uri = Uri.parse((ContentResolver.SCHEME_ANDROID_RESOURCE
+ "://" + context.getPackageName() + "/" + folder + "/" + fileName))
}

 fun changeLocale(context:Context, lang:String) {
val locale = Locale(lang)
val res = context.getResources()
val dm = res.getDisplayMetrics()
val conf = res.getConfiguration()
conf.locale = locale
res.updateConfiguration(conf, dm)
}


private fun getMetaDataString(context:Context, name:String):String? {
val pm = context.getPackageManager()
var value:String? = null

try
{
val ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
value = ai.metaData.getString(name)
}
catch (e:Exception) {
Log.d(CommonUtils::class.java!!.getSimpleName(), "Couldn't find config value: " + name)
}

return value
}

private fun getMetaDataInteger(context:Context, name:String):Int? {
val pm = context.getPackageManager()
var value:Int? = null

try
{
val ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
value = ai.metaData.getInt(name)
}
catch (e:Exception) {
Log.d(CommonUtils::class.java!!.getSimpleName(), "Couldn't find config value: " + name)
}

return value
}

 fun getMetaDataBoolean(context:Context, name:String):Boolean {
val pm = context.getPackageManager()
var value = false

try
{
val ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
value = ai.metaData.getBoolean(name)
}
catch (e:Exception) {
Log.d(CommonUtils::class.java!!.getSimpleName(), "Couldn't find config value: " + name)
}

return value
}


 fun substringBetween(start:String, end:String, input:String):String {
val startIndex = input.indexOf(start)
val endIndex = input.indexOf(end, startIndex + start.length)
if (startIndex == -1 || endIndex == -1)
return input
else
return input.substring(startIndex + start.length, endIndex).trim({ it <= ' ' })
}


 fun runAutoStartupPage(context:Context) {

try
{
val intent = Intent()
val manufacturer = android.os.Build.MANUFACTURER
if ("xiaomi".equals(manufacturer, ignoreCase = true))
{
intent.setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"))
}
else if ("oppo".equals(manufacturer, ignoreCase = true))
{
intent.setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"))
}
else if ("vivo".equals(manufacturer, ignoreCase = true))
{
intent.setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"))
}
else if ("Letv".equals(manufacturer, ignoreCase = true))
{
intent.setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"))
}
else if ("Honor".equals(manufacturer, ignoreCase = true))
{
intent.setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"))
}

val list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
if (list.size > 0)
{
context.startActivity(intent)
}
}
catch (e:Exception) {
Log.e("exc", (e).toString())
}

}

@Throws(IOException::class, OutOfMemoryError::class)
 fun getBase64StringFromUri(context:Context, uri:Uri):String {
var base64 = ""
val inputStream = context.getContentResolver().openInputStream(uri)
val result = ByteArrayOutputStream()
val buffer = ByteArray(1024)
val length:Int
while ((length = inputStream!!.read(buffer)) != -1)
{
result.write(buffer, 0, length)
}
base64 = Base64.encodeToString(result.toByteArray(), 0, result.size(),
Base64.DEFAULT)
result.close()
return base64
}

@Throws(FileNotFoundException::class)
 fun decodeSampledBitmapFromUri(context:Context, imageUri:Uri,
reqWidth:Int, reqHeight:Int):Bitmap? {

 // First decode with inJustDecodeBounds=true to check dimensions
		val options = BitmapFactory.Options()
options.inJustDecodeBounds = true

BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options)
 // Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

 // Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false
return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options)
}

 fun calculateInSampleSize(
options:BitmapFactory.Options, reqWidth:Int, reqHeight:Int):Int {
 // Raw height and width of image
		val height = options.outHeight
val width = options.outWidth
var inSampleSize = 1

if (height > reqHeight || width > reqWidth)
{

val halfHeight = height / 2
val halfWidth = width / 2

 // Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while (((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth))
{
inSampleSize *= 2
}
}

return inSampleSize
}

 fun addStringWaterMark(src:Bitmap, backgroundColor:Int, textColor:Int, stringWaterMark:String):Bitmap {
val left = 30
val top = 30
val w = src.getWidth()
val h = src.getHeight()
val result = Bitmap.createBitmap(w, h, src.getConfig())
val canvas = Canvas(result)
canvas.drawBitmap(src, 0f, 0f, null)

val fm = Paint.FontMetrics()
val mTxtPaint = Paint()
mTxtPaint.setColor(backgroundColor)
mTxtPaint.setTextSize(18.0f)
mTxtPaint.getFontMetrics(fm)
val margin = 10
canvas.drawRect((left - margin).toFloat(), top + fm.top - margin,
left.toFloat() + mTxtPaint.measureText(stringWaterMark) + margin.toFloat(), (top.toFloat() + fm.bottom
+ margin.toFloat()), mTxtPaint)
mTxtPaint.setColor(textColor)

canvas.drawText(stringWaterMark, left.toFloat(), top.toFloat(), mTxtPaint)

return result
}

 fun getResizedBitmap(image:Bitmap, maxSize:Int):Bitmap {
var width = image.getWidth()
var height = image.getHeight()

val bitmapRatio = width.toFloat() / height.toFloat()
if (bitmapRatio > 1)
{
width = maxSize
height = (width / bitmapRatio).toInt()
}
else
{
height = maxSize
width = (height * bitmapRatio).toInt()
}
return Bitmap.createScaledBitmap(image, width, height, true)
}

@Throws(IOException::class, OutOfMemoryError::class)
 fun getBase64StringFromBitmap(bitmap:Bitmap):String {
var temp = ""
val baos = ByteArrayOutputStream()
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
val b = baos.toByteArray()
temp = Base64.encodeToString(b, Base64.DEFAULT)
baos.close()
return temp
}
}
