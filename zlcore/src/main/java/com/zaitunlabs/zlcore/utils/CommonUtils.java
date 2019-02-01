package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.zaitunlabs.zlcore.BuildConfig;
import com.zaitunlabs.zlcore.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CommonUtils {

	public static String getPackageName(Context ctx) {
		return ctx.getPackageName();
	}

	public static int getIDResource(Context ctx, String folder, String filename) {
		String fn = null;
		if (filename.contains(".")) {
			fn = filename.substring(0, filename.lastIndexOf('.'));
		} else {
			fn = filename;
		}
		return ctx.getResources().getIdentifier(fn, folder,
				getPackageName(ctx));
	}

	public static int[] getIntArrayResource(Context context, String folder,
                                            String filename) {
		int[] x = null;
		try {
			Class clazz = Class.forName(getPackageName(context) + ".R$"
					+ folder);
			try {
				String fn = null;
				if (filename.contains(".")) {
					fn = filename.substring(0, filename.lastIndexOf('.'));
				} else {
					fn = filename;
				}
				if (clazz != null)
					x = (int[]) clazz.getDeclaredField(fn).get(null);
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (SecurityException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		return x;
	}

	public static int getIntResource(Context context, String folder,
                                     String filename) {
		int x = 0;
		try {
			Class clazz = Class.forName(getPackageName(context) + ".R$"
					+ folder);
			try {
				String fn = null;
				if (filename.contains(".")) {
					fn = filename.substring(0, filename.lastIndexOf('.'));
				} else {
					fn = filename;
				}
				if (clazz != null)
					x = (int) clazz.getDeclaredField(fn).getInt(null);
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (SecurityException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		return x;
	}

	public static boolean getBooleanResource(Context context, String folder,
                                             String filename) {
		boolean x = false;
		try {
			Class clazz = Class.forName(getPackageName(context) + ".R$"
					+ folder);
			try {
				String fn = null;
				if (filename.contains(".")) {
					fn = filename.substring(0, filename.lastIndexOf('.'));
				} else {
					fn = filename;
				}
				if (clazz != null)
					x = (boolean) clazz.getDeclaredField(fn).getBoolean(null);
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
			} catch (SecurityException e) {
				//e.printStackTrace();
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (NoSuchFieldException e) {
				//e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		return x;
	}


	public static androidx.appcompat.app.AlertDialog showInfo(Context context, String title, String msg, final Runnable nextCode) {
		androidx.appcompat.app.AlertDialog alert = null;
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
		builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
				context.getString(R.string.zlcore_general_wording_close), new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						if(nextCode != null){
							nextCode.run();
						}
					}
				});
		alert = builder.create();
		alert.setTitle(title);
		alert.show();
		return alert;
	}

	public static androidx.appcompat.app.AlertDialog showInfo(Context context, String title, String msg) {
		androidx.appcompat.app.AlertDialog alert = null;
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
		builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
				context.getString(R.string.zlcore_general_wording_close), new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alert = builder.create();
		alert.setTitle(title);
		alert.show();
		return alert;
	}

	public static androidx.appcompat.app.AlertDialog showGlobalInfo(Context context, String title, String msg) {
		androidx.appcompat.app.AlertDialog alert = null;
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
		builder.setMessage(fromHtml(msg)).setCancelable(false).setPositiveButton(
				context.getString(R.string.zlcore_general_wording_close), new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.setCanceledOnTouchOutside(false);
		alert.setTitle(title);
		alert.show();
		return alert;
	}


	public static androidx.appcompat.app.AlertDialog showDialog1Option(Context context, String title, String msg, String strOption1, final Runnable option1) {
		return showDialog3OptionWithIcon(context, null, title,msg,strOption1,option1,null,null,null,null);
	}

	public static androidx.appcompat.app.AlertDialog showDialog2Option(Context context, String title, String msg, String strOption1, final Runnable option1, String strOption2, final Runnable option2) {
		return showDialog3OptionWithIcon(context, null, title,msg,strOption1,option1,strOption2,option2,null,null);
	}

	public static androidx.appcompat.app.AlertDialog showDialog3Option(Context context, String title, String msg, String strOption1, final Runnable option1, String strOption2, final Runnable option2, String strOption3, final Runnable option3) {
		return showDialog3OptionWithIcon(context, null, title,msg,strOption1,option1,strOption2,option2,strOption3,option3);
	}

	public static androidx.appcompat.app.AlertDialog showDialog3OptionWithIcon(Context context, Drawable icon, String title, String msg, String strOption1, final Runnable option1, String strOption2, final Runnable option2, String strOption3, final Runnable option3) {
		return showDialog3OptionWithIcon(context,icon,title,msg,strOption1,option1,true,strOption2,option2,true,strOption3,option3,true);
	}

	public static androidx.appcompat.app.AlertDialog showDialog3OptionWithIcon(Context context, Drawable icon, String title, String msg, String strOption1, final Runnable option1, final boolean dismissByOption1, String strOption2, final Runnable option2, final boolean dismissByOption2, String strOption3, final Runnable option3, final boolean dismissByOption3) {
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,
				com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
		builder.setMessage(fromHtml(msg)).setCancelable(false);
		if(icon != null) {
			builder.setIcon(icon);
		}

		if(strOption2 != null) {
			builder.setNeutralButton(strOption2, null);
		}

		if(strOption1 != null) {
			builder.setPositiveButton(strOption1, null);
		}

		if(strOption3 != null) {
			builder.setNegativeButton(strOption3, null);
		}

		final androidx.appcompat.app.AlertDialog alert = builder.create();
		alert.setTitle(title);
		alert.show();

		//set custom button
		if(strOption2 != null) {
			alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (option2 != null) {
						option2.run();
					}
					if(dismissByOption2) alert.dismiss();
				}
			});
		}

		if(strOption1 != null) {
			alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (option1 != null) {
						option1.run();
					}
					if(dismissByOption1) alert.dismiss();
				}
			});
		}

		if(strOption3 != null) {
			alert.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (option3 != null) {
						option3.run();
					}
					if(dismissByOption3) alert.dismiss();
				}
			});

		}

		return alert;
	}

	public static androidx.appcompat.app.AlertDialog showDialog2OptionCustomView(Context context, View customView,
                                                                                 String title,
                                                                                 String strOption1, final Runnable option1,
                                                                                 String strOption2, final Runnable option2) {
		return showDialog2OptionCustomView(context,customView,title,strOption1,option1,true,strOption2,option2,true);
	}


	public static androidx.appcompat.app.AlertDialog showDialog2OptionCustomView(Context context, View customView,
                                                                                 String title,
                                                                                 String strOption1, final Runnable option1, final boolean dismissByOption1,
                                                                                 String strOption2, final Runnable option2, final boolean dismissByOption2) {
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,
				com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
		builder.setCancelable(false);
		builder.setView(customView);

		if(strOption2 != null) {
			builder.setNeutralButton(strOption2, null);
		}

		if(strOption1 != null) {
			builder.setPositiveButton(strOption1, null);
		}

		final androidx.appcompat.app.AlertDialog alert = builder.create();

		alert.setTitle(title);
		alert.show();

		//set custom button
		if(strOption2 != null) {
			alert.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (option2 != null) {
						option2.run();
					}
					if(dismissByOption2) alert.dismiss();
				}
			});
		}

		if(strOption1 != null) {
			alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (option1 != null) {
						option1.run();
					}
					if(dismissByOption1) alert.dismiss();
				}
			});
		}

		return alert;
	}

	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html){
		Spanned result;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
		} else {
			result = Html.fromHtml(html);
		}
		return result;
	}

	// get String from intent
	public static String getStringIntent(Intent intent, String name,
                                         String defaultvalue) {
		String retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getStringExtra(name);
		}
		return retval;
	}

	// get boolean from intent
	public static boolean getBooleanIntent(Intent intent, String name,
                                           boolean defaultvalue) {
		boolean retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getBooleanExtra(name, defaultvalue);
		}
		return retval;
	}

	// get Array String from intent
	public static String[] getArrayStringIntent(Intent intent, String name, String[] defaultvalue) {
		String[] retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getStringArrayExtra(name);
		}
		return retval;
	}

	// get Array String from intent
	public static ArrayList getArrayListStringIntent(Intent intent, String name, ArrayList defaultvalue) {
		ArrayList retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getStringArrayListExtra(name);
		}
		return retval;
	}

	// get int from intent
	public static int getIntIntent(Intent intent, String name, int defaultvalue) {
		int retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getIntExtra(name, 0);
		}
		return retval;
	}

	public static Parcelable getParcelableIntent(Intent intent, String name, Parcelable defaultvalue) {
		Parcelable retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getParcelableExtra(name);
		}
		return retval;
	}

	public static Serializable getSerializableIntent(Intent intent, String name, Serializable defaultvalue) {
		Serializable retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getSerializableExtra(name);
		}
		return retval;
	}

	// get long from intent
	public static long getLongIntent(Intent intent, String name, long defaultvalue) {
		long retval = defaultvalue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getLongExtra(name, 0);
		}
		return retval;
	}

	// get Bundle from intent
	public static Bundle getBundleIntent(Intent intent, String name, Bundle defaltValue) {
		Bundle retval = defaltValue;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getBundleExtra(name);
		}
		return retval;
	}

	// get String from Argument Fragment
	public static String getStringFragmentArgument(Bundle argument, String name, String defaultValue) {
		String retval = defaultValue;
		if (argument != null) {
				retval = argument.getString(name);
				if(retval == null)return defaultValue;
		}
		return retval;
	}

	// get int from Argument Fragment
	public static int getIntFragmentArgument(Bundle argument, String name, int defaultValue) {
		int retval = defaultValue;
		if (argument != null) {
			retval = argument.getInt(name, defaultValue);
		}
		return retval;
	}

	// get long from Argument Fragment
	public static long getLongFragmentArgument(Bundle argument, String name, long defaultValue) {
		long retval = defaultValue;
		if (argument != null) {
			retval = argument.getLong(name, defaultValue);
		}
		return retval;
	}

	// get boolean from Argument Fragment
	public static boolean getBooleanFragmentArgument(Bundle argument, String name, boolean defaultValue) {
		boolean retval = defaultValue;
		if (argument != null) {
			retval = argument.getBoolean(name, defaultValue);
		}
		return retval;
	}

	// get Serializable from Argument Fragment
	public static Serializable getSerializableFragmentArgument(Bundle argument, String name, Serializable defaultValue) {
		Serializable retval = defaultValue;
		if (argument != null) {
			retval = argument.getSerializable(name);
		}
		return retval;
	}

	public static int getScreenWidth(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		return display.getHeight();
	}

	/**
	 * function to retrieve height of application (screenheight decrease by  height of bar)
	 * @param context context of current activity or application
	 * @return height of application
	 */
	public static int getAppHeight(Context context) {
		return (getScreenHeight(context) - getStatusBarHeight(context));
	}

	public static int getStatusBarHeight(Context ctx) {
		int result = 0;
		int resourceId = ctx.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = ctx.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	public static boolean isActivityFullScreen(Context ctx){
		boolean isfullScreen = (((Activity)ctx).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
		return isfullScreen;
	}

	public static float getFontHeight(String text) {
		/*
		 * Paint tp = new Paint(); Rect bounds = new Rect(); //this will just
		 * retrieve the bounding rect for text tp.getTextBounds(text, 0,
		 * text.length(), bounds); int textHeight = bounds.height();
		 * Paint.FontMetrics metrics = tp.getFontMetrics(); int totalHeight =
		 * (int) (metrics.top - metrics.bottom);
		 */
		return 40;
	}

	public static float getTextLength(String text, float fontsize, Typeface tf) {
		Paint p = new Paint();
		p.setTextSize(fontsize);
		p.setTypeface(tf);
		return p.measureText(text);
	}

	public static int getHeightSPercent(Context ctx, float percent) {
		float pixel = 0;
		int screenheight = getScreenHeight(ctx);
		pixel = (screenheight * percent) / 100;
		return (int) pixel;
	}

	public static int getWidthSPercent(Context ctx, float percent) {
		float pixel = 0;
		int screenwidth = getScreenWidth(ctx);
		pixel = (screenwidth * percent) / 100;
		return (int) pixel;
	}

	public static int getHeightPercent(Context ctx, int height, float percent) {
		float pixel = 0;
		pixel = (height * percent) / 100;
		return (int) pixel;
	}

	public static int getWidthPercent(Context ctx, int width, float percent) {
		float pixel = 0;
		pixel = (width * percent) / 100;
		return (int) pixel;
	}
	

	
	public static double getWidthRatio(Context context){
		return (double)(getScreenWidth(context) / 100);
	}
	
	public static double getHeightRatio(Context context){
		boolean isFullScreen = isActivityFullScreen(context);
		return (double)((getScreenHeight(context) - (isFullScreen ? 0 : getStatusBarHeight(context)))/ 100);
	}
	
	public static int getPercentWidthFromPixel(Context context, float pixelWidth){
		double widthRatio = getWidthRatio(context);
		return (int)(pixelWidth/widthRatio);
	}
	
	public static int getPercentHeightFromPixel(Context context, float pixelHeight){
		double heightRatio = getHeightRatio(context);
		return (int)(pixelHeight/heightRatio);
	}
	
	public static double getUpperRegionPercentage(double percentFromCurrentRegion, double currentRegionPercentFromUpper){
		double result = 0;
		try {
			result = (percentFromCurrentRegion * currentRegionPercentFromUpper) / 100;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return result;
	}
	
	public static void setLayoutAnim_slidedownfromtop(ViewGroup panel) {
		  AnimationSet set = new AnimationSet(true);
		  
		  Animation animation = new AlphaAnimation(0.0f, 1.0f);
		  animation.setDuration(100);
		  set.addAnimation(animation);

		  animation = new TranslateAnimation(
		      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		      Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
		  );
		  animation.setDuration(300);
		  set.addAnimation(animation);

		  LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
		  panel.setLayoutAnimation(controller);
	}
	
	public static Animation setViewAnim_Appearslidedownfromtop(View view, long durationInMilis) {
		  AnimationSet set = new AnimationSet(true);
		  
		  Animation animation = new AlphaAnimation(0.0f, 1.0f);
		  animation.setDuration(durationInMilis);
		  set.addAnimation(animation);

		  animation = new TranslateAnimation(
		      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		      Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
		  );
		  animation.setDuration(durationInMilis);
		  set.addAnimation(animation);

		  if(view != null){
			  Animation anim = view.getAnimation();
			  if(anim != null){
				  anim.cancel();
				  anim.reset();
			  }
			  view.clearAnimation();
			  view.setAnimation(set);
		  }
		  return set;
	}
	public static Animation setViewAnim_disappearslideupfromBottom(View view, long durationInMilis) {
		  AnimationSet set = new AnimationSet(true);
		  
		  Animation animation = new AlphaAnimation(1.0f, 0.0f);
		  animation.setDuration(durationInMilis);
		  set.addAnimation(animation);

		  animation = new TranslateAnimation(
		      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f
		  );
		  animation.setDuration(durationInMilis);
		  set.addAnimation(animation);
		  
		  if(view != null){
			  Animation anim = view.getAnimation();
			  if(anim != null){
				  anim.cancel();
				  anim.reset();
			  }
			  view.clearAnimation();
			  view.setAnimation(set);
		  }
		  return set;
	}
	

	
	public static Point getImageDimension(Context context, int resourceID){
		Options options = new Options();
		options.inJustDecodeBounds = true;
		//Returns null, sizes are in the options variable
		BitmapFactory.decodeResource(context.getResources(), resourceID, options);
		int width = options.outWidth;
		int height = options.outHeight;
		return new Point(width, height);
	}
	
	public static boolean sendEmail(Context ctx, String feedbackTitle, String bodyEmailInHTML, String[] TO, String[] CC){
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, TO);
			if(CC != null)
				email.putExtra(Intent.EXTRA_CC, CC);
			email.putExtra(Intent.EXTRA_TEXT, fromHtml(bodyEmailInHTML));
			email.setType("message/rfc822");
			try {
				if(CommonUtils.isApplicationContext(ctx)){
					PendingIntent intent = PendingIntent.getActivity(ctx, 22, Intent.createChooser(email, feedbackTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
					try {
						intent.send();
					} catch (CanceledException e) {
						//e.printStackTrace();
						return false;
					}
				}else{
					ctx.startActivity(Intent.createChooser(email, feedbackTitle));
				}
				return true;
			} catch (android.content.ActivityNotFoundException ex) {
				return false;
			}
	}
	public static boolean isOdd( int val ) { return (val & 0x01) != 0; }

	
	public static Bitmap getBitmapFromView(View v, int viewWidth, int viewHeight){
		return getBitmapFromView(v, viewWidth, viewHeight, 0, 0, viewWidth, viewHeight);
	}
	
	public static Bitmap getBitmapFromView(View v, int viewWidth, int viewHeight, int screenshotX, int screenshotY, int screenshotWidth, int screenshotHeight){
		Bitmap bmp = null;
		v.setDrawingCacheEnabled(true);
		v.measure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		if(screenshotWidth != 0 && screenshotHeight != 0){
			bmp = Bitmap.createBitmap(v.getDrawingCache(), screenshotX, screenshotY, screenshotWidth, screenshotHeight);
		}
		v.setDrawingCacheEnabled(false);
		return bmp;
	}
	
	public static boolean sendEmail(Context context, String to, String title, String body, String sendTitle){
		String uriText =
			    "mailto:"+to+ 
			    "?subject=" + Uri.encode(title) +
			    "&body=" + Uri.encode(body);

		Uri uri = Uri.parse(uriText);
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
		if(sendTitle == null)
			sendTitle = context.getString(R.string.zlcore_common_utils_send_email);
		try {
			
			if(CommonUtils.isApplicationContext(context)){
				PendingIntent intent = PendingIntent.getActivity(context, 22, Intent.createChooser(emailIntent, sendTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
				try {
					intent.send();
				} catch (CanceledException e) {
					//e.printStackTrace();
					return false;
				}
			}else{
				context.startActivity(Intent.createChooser(emailIntent, sendTitle));
			}
			return true;
		} catch (android.content.ActivityNotFoundException ex) {
			return false;
		}
		
	}
	
	public static boolean shareContent(Context context, String shareTitle, String subject, String body){
		Intent shareIntent=new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		shareIntent.putExtra(Intent.EXTRA_TEXT, body);
		if(shareTitle == null)
			shareTitle = context.getString(R.string.zlcore_common_utils_default_share_title);
		try{
			if(CommonUtils.isApplicationContext(context)){
				PendingIntent intent = PendingIntent.getActivity(context, 22, Intent.createChooser(shareIntent, shareTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
				try {
					intent.send();
				} catch (CanceledException e) {
					//e.printStackTrace();
					return false;
				}
			}else{
				context.startActivity(Intent.createChooser(shareIntent, shareTitle));
			}
			return true;
		} catch (android.content.ActivityNotFoundException ex) {
			return false;
		}
	}
	
	public static boolean sendEmailWithAttachment(Context context, String[] recipient_address, String title, String body, String sendTitle, File[] files){
		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("multipart/mixed");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		emailIntent.putExtra(Intent.EXTRA_TEXT, body);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient_address);
		                                
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for(int i = 0 ; i < files.length ; i++){
			uris.add(Uri.fromFile(files[i]));
		}

		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		if(sendTitle == null)
			sendTitle = context.getString(R.string.zlcore_common_utils_send_email);
		try{
			if(CommonUtils.isApplicationContext(context)){
				PendingIntent intent = PendingIntent.getActivity(context, 22, Intent.createChooser(emailIntent, sendTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
				try {
					intent.send();
				} catch (CanceledException e) {
					//e.printStackTrace();
					return false;
				}
			}else{
				context.startActivity(Intent.createChooser(emailIntent, sendTitle));
			}
			return true;
		} catch (android.content.ActivityNotFoundException ex) {
			return false;
		}
	}
	
	public static boolean isApplicationContext(Context context){
		if(context instanceof Application)
			return true;
		else
			return false;
	}
	
	public static boolean isActivityContext(Context context){
		if(context instanceof Activity)
			return true;
		else
			return false;
	}
	
	public static boolean isServiceContext(Context context){
		if(context instanceof Service)
			return true;
		else
			return false;
	}
	
	
	public static int getVersionCode(Context context) {
	    int v = -1;
	    try {
			v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
		}
	    return v;
	}
	
	public static String getVersionName(Context context) {
	    String v = "";
	    try {
			v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
		}
	    return v;
	}
	
	public static boolean openBrowser(Context context, String link){
		if(TextUtils.isEmpty(link))return false;
		if(!link.startsWith("http://") && !link.startsWith("https://"))
			link = "http://"+link;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(link));

		try{
			if(CommonUtils.isApplicationContext(context)){
				PendingIntent intent = PendingIntent.getActivity(context, 22, Intent.createChooser(i, "open link with :").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
				try {
					intent.send();
				} catch (CanceledException e) {
					//e.printStackTrace();
					return false;
				}
			}else{
				context.startActivity(i);
			} 
			return true;
		}catch (android.content.ActivityNotFoundException ex) {
			return false;
		}
	}


	public static boolean openUrlWithPackageName(Context context, String url, String packageName){
		Intent i = new Intent(Intent.ACTION_VIEW);
		try {
			i.setPackage(packageName);
			i.setData(Uri.parse(url));
			if (i.resolveActivity(context.getPackageManager()) != null) {
				context.startActivity(i);
				return true;
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		return openBrowser(context, url);
	}
	
	public static Rect getViewDimension(View v){
		
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
		Rect result = new Rect();
		v.measure(0, 0);
		result.left = 0;
		result.top = 0;
		result.right = 0 + v.getMeasuredWidth();
		result.bottom = 0 + v.getMeasuredHeight();
		return result;
	}

	public static String getScreenSizeCategory(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout;
		screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

		switch (screenLayout) {
			case Configuration.SCREENLAYOUT_SIZE_SMALL:
				return "small";
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				return "normal";
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				return "large";
			case Configuration.SCREENLAYOUT_SIZE_XLARGE: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
				return "xlarge";
			default:
				return "undefined";
		}
	}
	
	public static int getDisplayMetricsDensityDPI(Context context){
		//dot per inch
		 int density= context.getResources().getDisplayMetrics().densityDpi;
		 switch(density)
		 {
		 case DisplayMetrics.DENSITY_LOW:
			 DebugUtils.logI("DisplayMetrics", "LDPI : "+density);
		     break;
		 case DisplayMetrics.DENSITY_MEDIUM:
			 DebugUtils.logI("DisplayMetrics", "MHDPI : "+density);
		     break;
		 case DisplayMetrics.DENSITY_HIGH:
			 DebugUtils.logI("DisplayMetrics", "HDPI : "+density);
		     break;
		 case DisplayMetrics.DENSITY_XHIGH:
			 DebugUtils.logI("DisplayMetrics", "XHDPI : "+density);
		     break;
		 case DisplayMetrics.DENSITY_XXHIGH:
			 DebugUtils.logI("DisplayMetrics", "XXHDPI : "+density);
			 break;
		 case DisplayMetrics.DENSITY_XXXHIGH:
			 DebugUtils.logI("DisplayMetrics", "XXXHDPI : "+density);
			 break;
		 }
		if(density < DisplayMetrics.DENSITY_LOW){
			DebugUtils.logI("DisplayMetrics", "Very Low LDPI: "+density);
		}
		 if(density > DisplayMetrics.DENSITY_XXXHIGH){
			 DebugUtils.logI("DisplayMetrics", "Very High XXXHDPI : "+density);
		 }

		 return density;
	}


	public static String getDisplayMetricsDensityDPIInString(Context context){
		//dot per inch
		int density= context.getResources().getDisplayMetrics().densityDpi;
		String densityString = "";
		switch(density)
		{
			case DisplayMetrics.DENSITY_LOW:
				DebugUtils.logI("DisplayMetrics", "LDPI : "+density);
				densityString = "LDPI";
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				DebugUtils.logI("DisplayMetrics", "MDPI : "+density);
				densityString = "MDPI";
				break;
			case DisplayMetrics.DENSITY_HIGH:
				DebugUtils.logI("DisplayMetrics", "HDPI : "+density);
				densityString = "HDPI";
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				DebugUtils.logI("DisplayMetrics", "XHDPI : "+density);
				densityString = "XHDPI";
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				DebugUtils.logI("DisplayMetrics", "XXHDPI : "+density);
				densityString = "XXHDPI";
				break;
			case DisplayMetrics.DENSITY_XXXHIGH:
				DebugUtils.logI("DisplayMetrics", "XXXHDPI : "+density);
				densityString = "XXXHDPI";
				break;
		}
		if(density < DisplayMetrics.DENSITY_LOW){
			DebugUtils.logI("DisplayMetrics", "Very Low LDPI: "+density);
			densityString = "Very Low LDPI";
		}
		if(density > DisplayMetrics.DENSITY_XXXHIGH){
			DebugUtils.logI("DisplayMetrics", "Very High XXXHDPI : "+density);
			densityString = "Very High XXXHDPI";
		}

		return densityString;
	}
	
	public static float getDisplayMetricsScaledDensity(Context context){
		//untuk density 160 nilainya 1, 120 = 0.75, dll
		 float scaleddensity= context.getResources().getDisplayMetrics().scaledDensity;
		 DebugUtils.logI("DisplayMetrics", "scaleddensity : "+scaleddensity);
		 return scaleddensity;
	}


	public static float getDisplayMetricsDensity(Context context){
		//untuk density 160 nilainya 1, 120 = 0.75, dll
		float density= context.getResources().getDisplayMetrics().density;
		DebugUtils.logI("DisplayMetrics", "density : "+density);
		return density;
	}

	public static float getDisplayMetricsRealXDensity(Context context){
		//pixel per inch
		float xdpi= context.getResources().getDisplayMetrics().xdpi;
		DebugUtils.logI("DisplayMetrics", "xdpi : "+xdpi);
		return xdpi;
	}


	public static float getDisplayMetricsRealYDensity(Context context){
		//pixel per inch
		float ydpi= context.getResources().getDisplayMetrics().ydpi;
		DebugUtils.logI("DisplayMetrics", "ydpi : "+ydpi);
		return ydpi;
	}

	public static int getDisplayMetricsScreenHeight(Context context){
		//pixel per inch
		int heightPixels= context.getResources().getDisplayMetrics().heightPixels;
		DebugUtils.logI("DisplayMetrics", "heightPixels : " + heightPixels);
		return heightPixels;
	}


	public static int getDisplayMetricsScreenWidth(Context context){
		//pixel per inch
		int widthPixels= context.getResources().getDisplayMetrics().widthPixels;
		DebugUtils.logI("DisplayMetrics", "widthPixels : " + widthPixels);
		return widthPixels;
	}

	public static int getIntAttrValue(Context context, int attr){
		int value = 0;
		final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
				new int[] { attr });
		value = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		return value;
	}


	public static int getActionBarHeight(Context context){
		int mActionBarSize = 0;
		final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.actionBarSize });
		mActionBarSize = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		return mActionBarSize;
	}

	public static int getActionBarHeight2(Context context){
		int mActionBarSize = 0;
		TypedValue tv = new TypedValue();
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
			mActionBarSize = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
		}
		return mActionBarSize;
	}

	public static int getNavigationHeight(Context context){
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}



	public static Point getNavigationBarSize(Context context) {
		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);

		// navigation bar on the right
		if (appUsableSize.x < realScreenSize.x) {
			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
		}

		// navigation bar at the bottom
		if (appUsableSize.y < realScreenSize.y) {
			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
		}

		// navigation bar is not present
		return new Point();
	}

	public static Point getAppUsableScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= 17) {
			display.getSize(size);
		}
		return size;
	}

	public static Point getRealScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else if (Build.VERSION.SDK_INT >= 14) {
			try {
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (InvocationTargetException e) {
				//e.printStackTrace();
			} catch (NoSuchMethodException e) {
				//e.printStackTrace();
			}
		}


		return size;
	}





	public static Toast showToast(Context context, String message){
		if(context != null) {
			Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			toast.show();
			return toast;
		}
		return null;
	}


	public static int getDipFromPixel2(Context context, int sizeInPixel){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) ((sizeInPixel/density));
	}

	public static int getPixelFromDip2(Context context, int sizeInDip) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) ((sizeInDip*density) + 0.5f);
	}

	public static float getDipFromPixel(Context context, int sizeInPixel){
		float densityDPI = context.getResources().getDisplayMetrics().densityDpi;
		return ((sizeInPixel* DisplayMetrics.DENSITY_DEFAULT)/densityDPI);
	}

	public static float getPixelFromDip(Context context, int sizeInDip) {
		float densityDPI = context.getResources().getDisplayMetrics().densityDpi;
		return ((sizeInDip*densityDPI)/ DisplayMetrics.DENSITY_DEFAULT);
	}



	public static float getPixelFromComplexUnit(Context context, int complexUnitType, int sizeInComplexUnit){
		if(context == null){
			return 0;
		}
		return TypedValue.applyDimension(complexUnitType, sizeInComplexUnit, context.getResources().getDisplayMetrics());
	}

	public static void makeSnackBarMultiLine(Snackbar snackbar, int line){
		View snackbarView = snackbar.getView();
		TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
		textView.setMaxLines(line);
	}

	public static Snackbar showSnackBar(Context context, String message){
		return showSnackBar(context,message, false);
	}

	public static Snackbar showSnackBar(Context context, String message, boolean isIndefinite){
		return showSnackBar(context,message,context.getString(R.string.zlcore_default_snackbar_dismiss),isIndefinite);
	}

	public static Snackbar showSnackBar(Context context, String message, String dismissString, boolean isIndefinite){
		return showSnackBar(context, context.getString(R.string.zlcore_general_coordinator_layout_tagname),message,dismissString, isIndefinite);
	}

	private static View getCoordLayout(View rootView, String coordinatorLayoutTagname){
		View coordView = rootView.findViewWithTag(coordinatorLayoutTagname);
		if(coordView == null){
			coordView = rootView.findViewById(android.R.id.content);
			if(!(coordView instanceof CoordinatorLayout)){
				if(coordView instanceof ViewGroup){
					coordView = ((ViewGroup)coordView).getChildAt(0);
				}
			}
		}
		return coordView;
	}

	private static View getCoordLayout(View rootView, int coordinatorLayoutId){
		View coordView = rootView.findViewById(coordinatorLayoutId);
		if(coordView == null){
			coordView = rootView.findViewById(android.R.id.content);
			if(!(coordView instanceof CoordinatorLayout)){
				if(coordView instanceof ViewGroup){
					coordView = ((ViewGroup)coordView).getChildAt(0);
				}
			}
		}
		return coordView;
	}


	public static Snackbar showSnackBar(Context context, String coordinatorLayoutTagname, String message, String dismissString, boolean isIndefinite){
		View rootView = ((Activity)context).getWindow().getDecorView().getRootView();
		View coordView = getCoordLayout(rootView,coordinatorLayoutTagname);
		Snackbar snackbar = Snackbar.make(coordView==null?rootView:coordView, message, (isIndefinite?Snackbar.LENGTH_INDEFINITE:Snackbar.LENGTH_LONG)).setAction(dismissString == null ? "dismiss" : dismissString, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//do nothing, just dismiss
			}
		});
		makeSnackBarMultiLine(snackbar,30);
		snackbar.show();
		return snackbar;
	}


	public static Snackbar showSnackBar(Context context, int coordinatorLayoutId, String message, String dismissString){
		View rootView = ((Activity)context).getWindow().getDecorView().getRootView();
		View coordView = getCoordLayout(rootView,coordinatorLayoutId);
		Snackbar snackbar = Snackbar.make(coordView==null?rootView:coordView, message, Snackbar.LENGTH_LONG).setAction(dismissString == null ? "dismiss" : dismissString, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//do nothing, just dismiss
			}
		});
		makeSnackBarMultiLine(snackbar,30);
		snackbar.show();
		return snackbar;
	}

	public static Snackbar showSnackBar(Fragment fragment, String coordinatorLayoutTagname, String message, String dismissString){
		View rootView = fragment.getView();
		View coordView = getCoordLayout(rootView,coordinatorLayoutTagname);
		Snackbar snackbar = Snackbar.make(coordView==null?rootView:coordView, message, Snackbar.LENGTH_LONG).setAction(dismissString == null ? "dismiss" : dismissString, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//do nothing, just dismiss
			}
		});
		makeSnackBarMultiLine(snackbar,30);
		snackbar.show();
		return snackbar;
	}

	public static Snackbar showSnackBar(Fragment fragment, String coordinatorLayoutTagname, String message, String dismissString, final Runnable runAfterClicked){
		View rootView = fragment.getView();
		View coordView = getCoordLayout(rootView,coordinatorLayoutTagname);
		Snackbar snackbar = Snackbar.make(coordView==null?rootView:coordView, message, Snackbar.LENGTH_LONG).setAction(dismissString == null ? "dismiss" : dismissString, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (runAfterClicked != null) {
					runAfterClicked.run();
				}
			}
		});
		makeSnackBarMultiLine(snackbar,30);
		snackbar.show();
		return snackbar;
	}

	public static final int INFORMATION_TYPE_TOAST = 1;
	public static final int INFORMATION_TYPE_SNACKBAR = 2;
	public static final int INFORMATION_TYPE_DIALOG = 3;

	public static void showInformation(int type, Object context, String message){
		if(context != null) {
			if (type == INFORMATION_TYPE_TOAST) {
				if (context instanceof Fragment) {
					showToast(((Fragment) context).getContext(), message);
				} else if (context instanceof Context) {
					showToast((Context) context, message);
				}
			} else if (type == INFORMATION_TYPE_SNACKBAR) {
				if (context instanceof Fragment) {
					showSnackBar((Fragment) context,
							((Fragment) context).getString(R.string.zlcore_general_coordinator_layout_tagname),
							message,
							((Fragment) context).getString(R.string.zlcore_snackbar_action_name_dismiss));
				} else if (context instanceof Context) {
					showSnackBar((Context) context,
							((Fragment) context).getString(R.string.zlcore_general_coordinator_layout_tagname),
							message,
							((Fragment) context).getString(R.string.zlcore_snackbar_action_name_dismiss),false);
				}
			} else if (type == INFORMATION_TYPE_DIALOG) {
				if (context instanceof Fragment) {
					showInfo(((Fragment) context).getContext(), ((Fragment) context).getContext().getString(R.string.zlcore_general_wording_information_title), message);
				} else if (context instanceof Context) {
					showInfo((Context) context, ((Fragment) context).getContext().getString(R.string.zlcore_general_wording_information_title), message);
				}
			} else {
				if (context instanceof Fragment) {
					showToast(((Fragment) context).getContext(), message);
				} else if (context instanceof Context) {
					showToast((Context) context, message);
				}
			}
		}
	}

	public static boolean isAppInForeground(Context context){
		// Get the Activity Manager
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);



		// Get a list of running tasks, we are only interested in the last one,
		// the top most so we give a 1 as parameter so we only get the topmost.
		//List< ActivityManager.RunningTaskInfo > task = manager.getRunningTasks(1);

		// Get the info we need for comparison.
		//ComponentName componentInfo = task.get(0).topActivity;

		//if(componentInfo.getPackageName().equals(PackageName)) return true;




		List< ActivityManager.RunningAppProcessInfo > process = manager.getRunningAppProcesses();
		// Check if it matches our package name.
		if(process.get(0).processName.equals(context.getPackageName())) return true;

		// If not then our app is not on the foreground.
		return false;
	}


	public static Cursor getDetailContactWithNumber(Context context, String phoneNumber){
		if(TextUtils.isEmpty(phoneNumber)){
			return null;
		}

		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		return context.getContentResolver().query(uri, new String[]{
				ContactsContract.PhoneLookup._ID,
				ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.PhoneLookup.PHOTO_URI,
				ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI},null,null,null);
	}

	public static long getContactIDWithNumber(Context context, String phoneNumber){
		long result = -1;
		Cursor contactDetail = getDetailContactWithNumber(context, phoneNumber);
		if(contactDetail != null && contactDetail.moveToFirst()) {
			result = contactDetail.getLong(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
		}
		if(contactDetail != null){
			contactDetail.close();
			contactDetail = null;
		}
		return result;
	}


	public static String getContactThumbnailPhotoURIWithNumber(Context context, String phoneNumber){
		String result = null;
		Cursor contactDetail = getDetailContactWithNumber(context, phoneNumber);
		if(contactDetail != null && contactDetail.moveToFirst()){
			result = contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
		}
		if(contactDetail != null){
			contactDetail.close();
			contactDetail = null;
		}
		return result;
	}

	public static String getContactPhotoURIWithNumber(Context context, String phoneNumber){
		String result = null;
		Cursor contactDetail = getDetailContactWithNumber(context,phoneNumber);
		if(contactDetail != null && contactDetail.moveToFirst()) {
			result =  contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI));
		}
		if(contactDetail != null){
			contactDetail.close();
			contactDetail = null;
		}
		return result;
	}

	public static String getContactNameWithNumber(Context context, String phoneNumber){
		String result = null;
		Cursor contactDetail = getDetailContactWithNumber(context,phoneNumber);
		if(contactDetail != null && contactDetail.moveToFirst()) {
			result = contactDetail.getString(contactDetail.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		if(contactDetail != null){
			contactDetail.close();
			contactDetail = null;
		}
		return result;
	}


	public static Drawable getDrawableFromURI(Context context, Uri uri){
		Drawable result = null;
		try {
			InputStream inputStream = context.getContentResolver().openInputStream(uri);
			result = Drawable.createFromStream(inputStream, uri.toString() );
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {}
				inputStream = null;
			}
		} catch (FileNotFoundException e) {}
		return result;
	}



	public static HashMap<String, Object> getChargingState(Context context){
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);

		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

		int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;

		HashMap<String, Object> result = new HashMap<>();
		result.put("isCharging", isCharging);
		result.put("usbCharge", usbCharge);
		result.put("acCharge", acCharge);
		result.put("batteryPct", batteryPct);
		return result;
	}


	public static void addContact(Context context, String phoneNumber){
		Intent contactIntent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, ContactsContract.Contacts.CONTENT_URI);
		contactIntent.setData(Uri.parse("tel:" + phoneNumber));//Add the mobile number here
		//contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, "smartcall-x"); //ADD contact name here
		context.startActivity(contactIntent);
	}



	public static void copyPlainTextToClipboard(Context context, String label, String text){
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(label, text);
		clipboard.setPrimaryClip(clip);
	}

	public static CharSequence getPlainTextFromClipboard(Context context){
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = clipboard.getPrimaryClip();

		if(clipData == null){
			return null;
		}

		ClipData.Item item = clipData.getItemAt(0);
		CharSequence pasteData = item.getText();
		if (pasteData != null) {
			return pasteData;
		} else {
			Uri pasteUri = item.getUri();
			if (pasteUri != null) {

				pasteData = pasteUri.toString();

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

				return pasteData;
			}
		}

		return null;
	}

	public static void setWindowSofInputMode(Activity activity, int softInputMode){
		activity.getWindow().setSoftInputMode(softInputMode);
	}

	public static void setWindowSofInputModeResize(Activity activity){
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	public static void setEditTextDoneKey(EditText editText){
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
	}

	public static void setEditTextNextKey(EditText editText){
		editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
	}

	public static void showKeyboard(Context context, View view){
		if(view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public static void hideKeyboard(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void hideKeyboard(Activity activity){
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * Method to check network availability
	 *
	 * @param context
	 * @return true or false
	 */
	public static boolean isInternetConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		if (info.getState() != NetworkInfo.State.CONNECTED) {
			return false;
		}
		return true;
	}

	public static String getModelNumber(){
		String model = "";
		try {
			model = CommonUtils.urlEncode(Build.MODEL);
		} catch (UnsupportedEncodingException e) {
			model = Build.MODEL;
		}
		return model;
	}

	public static String getAndroidID(Context context){
		return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
	}

	public static String getOSVersion(){
		String osVersion = "";
		try {
			osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE);
		} catch (UnsupportedEncodingException e) {
			////e.printStackTrace();
		}
		return osVersion;
	}

	public static String getUserAgent(){
		String userAgent = "";
		try {
			userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"));
		} catch (UnsupportedEncodingException e) {
			////e.printStackTrace();
		}
		return userAgent;
	}


	public static String getMeid(Context context){
		String keyName = "cached-meid";
		String cachedMeid = Prefs.with(context).getString(keyName,"");
		if(TextUtils.isEmpty(cachedMeid)) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				cachedMeid = tm.getMeid();
			} else {
				cachedMeid = tm.getDeviceId();
			}
			if(cachedMeid != null) {
				Prefs.with(context).save(keyName, cachedMeid);
			}else{
				cachedMeid = "";
			}
		}
		return cachedMeid;
	}

	public static String getImei(Context context){
		String keyName = "cached-imei";
		String cachedImei = Prefs.with(context).getString(keyName,"");
		if(TextUtils.isEmpty(cachedImei)) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				cachedImei = tm.getImei();
			} else {
				cachedImei = tm.getDeviceId();
			}
			if(cachedImei != null) {
				Prefs.with(context).save(keyName, cachedImei);
			}else{
				cachedImei = "";
			}
		}
		return cachedImei;
	}

	public static String getCurrentDeviceLanguage(Context context){
		if(Build.VERSION.SDK_INT >= 24){
			return context.getResources().getConfiguration().getLocales().get(0).getLanguage();
		} else {
			return context.getResources().getConfiguration().locale.getLanguage();
		}
	}

	public static String getCurrentDeviceCountry(Context context){
		if(Build.VERSION.SDK_INT >= 24){
			return context.getResources().getConfiguration().getLocales().get(0).getCountry();
		} else {
			return context.getResources().getConfiguration().locale.getCountry();
		}
	}

	public static Locale getIndonesianLocale(){
		return new Locale("in", "ID");
	}

	public static Locale getCurrentDeviceLocale(Context context){
		if(Build.VERSION.SDK_INT >= 24){
			return context.getResources().getConfiguration().getLocales().get(0);
		} else {
			return context.getResources().getConfiguration().locale;
		}
	}


	public static void openAppSetting(Context context){
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", context.getPackageName(), null);
		intent.setData(uri);
		context.startActivity(intent);
	}

	public static androidx.appcompat.app.AlertDialog showLoadingDialog(Context context){
		return showLoadingDialog(context,null, true, false);
	}

	public static androidx.appcompat.app.AlertDialog showLoadingDialog(Context context, String message){
		return showLoadingDialog(context,message, true, false);
	}

	public static androidx.appcompat.app.AlertDialog showLoadingDialog(Context context, String message,
                                                                       boolean isIndeterminate, boolean cancelable){
		if(TextUtils.isEmpty(message)){
			message = context.getString(R.string.zlcore_warning_please_wait);
		}
		androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
		builder.setView(R.layout.progressbar_horizontal_left);
		builder.setCancelable(cancelable);
		AlertDialog dialog = builder.create();
		dialog.show();
		((ProgressBar)dialog.findViewById(R.id.progress_custom_progressbar)).setIndeterminate(isIndeterminate);
		((TextView)dialog.findViewById(R.id.progress_custom_textview)).setText(message);
		return dialog;
	}

	public static Snackbar showLoadingSnackBar(Context context){
		return showLoadingSnackBar(context,null, true,false);
	}

	public static Snackbar showLoadingSnackBar(Context context, String message){
		return showLoadingSnackBar(context,message, true,false);
	}

	public static Snackbar showLoadingSnackBar(Context context, String message, boolean isIndeterminate, boolean cancelable){
		if(TextUtils.isEmpty(message)){
			message = context.getString(R.string.zlcore_warning_please_wait);
		}
		View rootView = ((Activity)context).getWindow().getDecorView().getRootView();
		Snackbar snackbar = Snackbar.make(rootView, message,
				(isIndeterminate?Snackbar.LENGTH_INDEFINITE:Snackbar.LENGTH_LONG));

		Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout)snackbar.getView();

		// Hide the text
		TextView textView = (TextView) snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
		int color = textView.getCurrentTextColor();
		textView.setVisibility(View.INVISIBLE);

		View customProgressView = LayoutInflater.from(context).inflate(R.layout.progressbar_horizontal_left,null);
		TextView newTextView = customProgressView.findViewById(R.id.progress_custom_textview);
		newTextView.setText(message);
		newTextView.setTextColor(color);
		snackbarLayout.addView(customProgressView);

		if(cancelable) {
			snackbar.setAction(context.getString(R.string.zlcore_general_wording_dismiss), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//do nothing, just dismiss
				}
			});
		}
		snackbar.show();
		return snackbar;
	}


	public static PopupWindow showPopupWindow(Context context,
											  View popupView,
											  View anchorView,
											  boolean outsideTouchable,
											  PopupWindow.OnDismissListener dismissListener) {
		PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(outsideTouchable);
		popupWindow.setOnDismissListener(dismissListener);
		popupWindow.showAsDropDown(anchorView);
		return popupWindow;
	}

	public static PopupWindow showPopupWindow(Context context,
											  View popupView,
											  View parentView,
											  int gravity,
											  int xOff, int yOff,
											  boolean outsideTouchable,
											  PopupWindow.OnDismissListener dismissListener) {
		PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(outsideTouchable);
		popupWindow.setOnDismissListener(dismissListener);
		popupWindow.showAtLocation(parentView, gravity, xOff,yOff);
		return popupWindow;
	}

	public static PopupMenu showPopup(Context context,
									  int menuResId,
									  View anchorView,
									  PopupMenu.OnDismissListener dismissListener,
									  PopupMenu.OnMenuItemClickListener menuItemClickListener){
		PopupMenu popup = new PopupMenu(context, anchorView);
		popup.getMenuInflater().inflate(menuResId, popup.getMenu());
		popup.setOnDismissListener(dismissListener);
		popup.setOnMenuItemClickListener(menuItemClickListener);
		popup.show();
		return popup;
	}

	public static void openPlayStore(Context context, String packageFullName) {
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + packageFullName)));
		} catch (android.content.ActivityNotFoundException e) {
			context.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + packageFullName)));
		}
	}


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
		private static final String ARG_DEFAULT_DATE = "arg_default_date";
		private DatePickerDialog.OnDateSetListener onDateSetListener;

		private void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener){
			this.onDateSetListener = onDateSetListener;
		}

		public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener onDateSetListener, Date defaultDate){
			DatePickerFragment datePickerFragment = new DatePickerFragment();
			datePickerFragment.setOnDateSetListener(onDateSetListener);
			Bundle arguments = new Bundle();
			arguments.putSerializable(ARG_DEFAULT_DATE, defaultDate);
			datePickerFragment.setArguments(arguments);
			return datePickerFragment;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			Date defaultDate = (Date)CommonUtils.getSerializableFragmentArgument(getArguments(), ARG_DEFAULT_DATE, null);
			if(defaultDate != null){
				c.setTime(defaultDate);
			}
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
			if(onDateSetListener != null){
				onDateSetListener.onDateSet(datePicker, i, i1, i2);
			}
		}
	}

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		private static final String ARG_DEFAULT_TIME = "arg_default_time";
		private TimePickerDialog.OnTimeSetListener onTimeSetListener;

		public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
			this.onTimeSetListener = onTimeSetListener;
		}

		public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener onTimeSetListener, Date defaultTime){
			TimePickerFragment timePickerFragment = new TimePickerFragment();
			timePickerFragment.setOnTimeSetListener(onTimeSetListener);
			Bundle arguments = new Bundle();
			arguments.putSerializable(ARG_DEFAULT_TIME, defaultTime);
			timePickerFragment.setArguments(arguments);
			return timePickerFragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			Date defaultTime = (Date)CommonUtils.getSerializableFragmentArgument(getArguments(), ARG_DEFAULT_TIME, null);
			if(defaultTime != null){
				c.setTime(defaultTime);
			}
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker timePicker, int i, int i1) {
			if(onTimeSetListener != null) {
				onTimeSetListener.onTimeSet(timePicker, i, i1);
			}
		}
	}


	public static class AlertDialogFrament extends DialogFragment{
		private static final String ARG_TITLE = "arg_title";
		private static final String ARG_MESSAGE = "arg_message";
		private static final String ARG_STR_OPTION1 = "arg_str_option1";
		private static final String ARG_STR_OPTION2 = "arg_str_option2";
		private static final String ARG_STR_OPTION3 = "arg_str_option3";
		private static final String ARG_DISMISS_OPTION1 = "arg_dismiss_option1";
		private static final String ARG_DISMISS_OPTION2 = "arg_dismiss_option2";
		private static final String ARG_DISMISS_OPTION3 = "arg_dismiss_option3";
		private static final String ARG_LISTENER_FRAGMENT_TAG = "arg_listener_fragment_tag";
		public static final int OPTION1 = 1;
		public static final int OPTION2 = 2;
		public static final int OPTION3 = 3;
		private static final String ARG_REQUEST_CODE = "arg_request_code";
		private String title;
		private String message;
		private String strOption1;
		private String strOption2;
		private String strOption3;
		private boolean dismissByOption1;
		private boolean dismissByOption2;
		private boolean dismissByOption3;
		private String listenerFragmentTag;
		private int requestCode;

		public interface OnAlertDialogFragmentListener {
			void onClick(Activity activity, AlertDialogFrament alertDialogFrament, int which, int requestCode);
		}

		public static AlertDialogFrament newInstance(String listenerFragmentTag, int requestCode, Drawable icon, String title, String msg, String strOption1, final boolean dismissByOption1, String strOption2, final boolean dismissByOption2, String strOption3, final boolean dismissByOption3){
			AlertDialogFrament alertDialogFrament = new AlertDialogFrament();
			Bundle arguments = new Bundle();
			arguments.putString(ARG_TITLE, title);
			arguments.putString(ARG_MESSAGE, msg);
			arguments.putString(ARG_STR_OPTION1, strOption1);
			arguments.putString(ARG_STR_OPTION2, strOption2);
			arguments.putString(ARG_STR_OPTION3, strOption3);
			arguments.putBoolean(ARG_DISMISS_OPTION1, dismissByOption1);
			arguments.putBoolean(ARG_DISMISS_OPTION2, dismissByOption2);
			arguments.putBoolean(ARG_DISMISS_OPTION3, dismissByOption3);
			arguments.putString(ARG_LISTENER_FRAGMENT_TAG, listenerFragmentTag);
			arguments.putInt(ARG_REQUEST_CODE, requestCode);
			alertDialogFrament.setArguments(arguments);
			return alertDialogFrament;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			listenerFragmentTag = CommonUtils.getStringFragmentArgument(getArguments(), ARG_LISTENER_FRAGMENT_TAG, null);
			if(!TextUtils.isEmpty(listenerFragmentTag)){
				//set new target
				Fragment listenerFragment = getFragmentManager().findFragmentByTag(listenerFragmentTag);
				setTargetFragment(listenerFragment, requestCode);
			}
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			title = CommonUtils.getStringFragmentArgument(getArguments(), ARG_TITLE, null);
			message = CommonUtils.getStringFragmentArgument(getArguments(), ARG_MESSAGE, null);
			strOption1 = CommonUtils.getStringFragmentArgument(getArguments(), ARG_STR_OPTION1, null);
			strOption2 = CommonUtils.getStringFragmentArgument(getArguments(), ARG_STR_OPTION2, null);
			strOption3 = CommonUtils.getStringFragmentArgument(getArguments(), ARG_STR_OPTION3, null);
			dismissByOption1 = CommonUtils.getBooleanFragmentArgument(getArguments(), ARG_DISMISS_OPTION1, true);
			dismissByOption2 = CommonUtils.getBooleanFragmentArgument(getArguments(), ARG_DISMISS_OPTION2, true);
			dismissByOption3 = CommonUtils.getBooleanFragmentArgument(getArguments(), ARG_DISMISS_OPTION3, true);
			requestCode = CommonUtils.getIntFragmentArgument(getArguments(), ARG_REQUEST_CODE, 0);

			androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity(),
					com.zaitunlabs.zlcore.R.style.AppCompatAlertDialogStyle);
			builder.setMessage(fromHtml(message)).setCancelable(false);


			if(strOption2 != null) {
				builder.setNeutralButton(strOption2, null);
			}

			if(strOption1 != null) {
				builder.setPositiveButton(strOption1, null);
			}

			if(strOption3 != null) {
				builder.setNegativeButton(strOption3, null);
			}

			builder.setTitle(title);
			return builder.create();
		}

		@Override
		public void onResume() {
			try{
				Fragment targetFragment = getTargetFragment();
				final OnAlertDialogFragmentListener listener = (OnAlertDialogFragmentListener) (targetFragment == null ? getActivity():targetFragment);
				final AlertDialog alertDialog = (AlertDialog) getDialog();
				alertDialog.setTitle(title);
				//set custom button
				if(strOption2 != null) {
					alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							listener.onClick(AlertDialogFrament.this.getActivity(), AlertDialogFrament.this, OPTION2, requestCode);
							if(dismissByOption2) alertDialog.dismiss();
						}
					});
				}

				if(strOption1 != null) {
					alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							listener.onClick(AlertDialogFrament.this.getActivity(), AlertDialogFrament.this, OPTION1, requestCode);
							if(dismissByOption1) alertDialog.dismiss();
						}
					});
				}

				if(strOption3 != null) {
					alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							listener.onClick(AlertDialogFrament.this.getActivity(), AlertDialogFrament.this, OPTION3, requestCode);
							if(dismissByOption3) alertDialog.dismiss();
						}
					});

				}
			} catch (ClassCastException e){
				throw new RuntimeException("Please implement AlerDialogFragmentListener");
			}


			super.onResume();
		}
	}

	public static DatePickerFragment showDatePicker(Bundle savedInstanceState, FragmentManager fragmentManager, String tag, DatePickerDialog.OnDateSetListener onDateSetListener, Date defautlDate) {
		return showDatePicker(savedInstanceState, false, fragmentManager, tag, onDateSetListener, defautlDate);
	}

	public static DatePickerFragment showDatePicker(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, FragmentManager fragmentManager, String tag, DatePickerDialog.OnDateSetListener onDateSetListener, Date defautlDate){
		DatePickerFragment datePickerFragment = null;
		if(savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
			datePickerFragment = DatePickerFragment.newInstance(onDateSetListener, defautlDate);
			datePickerFragment.show(fragmentManager, tag);
		} else {
			datePickerFragment = (DatePickerFragment) fragmentManager.findFragmentByTag(tag);
			if(datePickerFragment == null){
				return showDatePicker(null, true, fragmentManager, tag, onDateSetListener, defautlDate);
			} else {
				datePickerFragment.setOnDateSetListener(onDateSetListener);
			}
		}
		return datePickerFragment;
	}

	public static TimePickerFragment showTimePicker(Bundle savedInstanceState, FragmentManager fragmentManager, String tag, TimePickerDialog.OnTimeSetListener onTimeSetListener, Date defaultTime) {
		return showTimePicker(savedInstanceState, false, fragmentManager, tag, onTimeSetListener, defaultTime);
	}

	public static TimePickerFragment showTimePicker(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, FragmentManager fragmentManager, String tag, TimePickerDialog.OnTimeSetListener onTimeSetListener, Date defaultTime){
		TimePickerFragment timePickerFragment = null;
		if(savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
			timePickerFragment = TimePickerFragment.newInstance(onTimeSetListener, defaultTime);
			timePickerFragment.show(fragmentManager, tag);
		} else {
			timePickerFragment = (TimePickerFragment) fragmentManager.findFragmentByTag(tag);
			if(timePickerFragment == null){
				return showTimePicker(null, true, fragmentManager, tag, onTimeSetListener, defaultTime);
			} else {
				timePickerFragment.setOnTimeSetListener(onTimeSetListener);
			}
		}
		return timePickerFragment;
	}

	public static AlertDialogFrament showDialogFragment1Option(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, Object activityOrFragment, String tag, String listenerFragmentTag, int requestCode,
															   String title, String msg, String strOption1) {
		return showDialogFragment3Option(savedInstanceState,ignoreStateOrAlwaysCreateNew,activityOrFragment,tag,listenerFragmentTag,requestCode, null, title,msg,strOption1,null,null);
	}

	public static AlertDialogFrament showDialogFragment2Option(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, Object activityOrFragment, String tag, String listenerFragmentTag, int requestCode,
															   String title, String msg, String strOption1, String strOption2) {
		return showDialogFragment3Option(savedInstanceState,ignoreStateOrAlwaysCreateNew,activityOrFragment,tag,listenerFragmentTag,requestCode,null, title,msg,strOption1,strOption2,null);
	}


	public static AlertDialogFrament showDialogFragment3Option(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, Object activityOrFragment, String tag, String listenerFragmentTag, int requestCode,
															   Drawable icon,String title, String msg, String strOption1, String strOption2, String strOption3) {
		return showDialogFragment3OptionWithIcon(savedInstanceState,ignoreStateOrAlwaysCreateNew,activityOrFragment,tag,listenerFragmentTag,requestCode, icon,title,msg,strOption1,true,strOption2,true,strOption3,true);
	}

	public static AlertDialogFrament  showDialogFragment3OptionWithIcon(Bundle savedInstanceState, boolean ignoreStateOrAlwaysCreateNew, Object activityOrFragment, String tag, String listenerFragmentTag, int requestCode,
													 Drawable icon, String title, String msg, String strOption1, final boolean dismissByOption1, String strOption2, final boolean dismissByOption2, String strOption3, final boolean dismissByOption3){
		AlertDialogFrament alertDialogFrament = null;
		FragmentManager fragmentManager = null;
		if(activityOrFragment instanceof AppCompatActivity){
			fragmentManager = ((AppCompatActivity)activityOrFragment).getSupportFragmentManager();
		} else {
			fragmentManager = ((Fragment)activityOrFragment).getFragmentManager();
		}
		if(savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
			alertDialogFrament = AlertDialogFrament.newInstance(listenerFragmentTag,requestCode, icon, title, msg, strOption1, dismissByOption1, strOption2, dismissByOption2, strOption3, dismissByOption3);
			alertDialogFrament.show(fragmentManager, tag);
		} else {
			alertDialogFrament = (AlertDialogFrament) fragmentManager.findFragmentByTag(tag);
			if(alertDialogFrament == null){
				return showDialogFragment3OptionWithIcon(null, true, activityOrFragment, tag, listenerFragmentTag,requestCode, icon, title, msg, strOption1, dismissByOption1, strOption2, dismissByOption2, strOption3, dismissByOption3);
			}
		}
		return alertDialogFrament;
	}

	public static void makeTextViewUnderlined(TextView textView){
		SpannableString content = new SpannableString(textView.getText());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		textView.setText(content);
	}

	private static class IdleMonitorTextWatcher implements TextWatcher {
		long delay = 300; // 0.5 seconds after user stops typing
		long last_text_edit = 0;
		Handler handler = new Handler();
		private Runnable taskWillDoWhenIdle;
		private Runnable input_finish_checker = new Runnable() {
			public void run() {
				if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
					if(taskWillDoWhenIdle != null)taskWillDoWhenIdle.run();
				}
			}
		};

		public IdleMonitorTextWatcher(Runnable taskWillDoWhenIdle){
			this.taskWillDoWhenIdle = taskWillDoWhenIdle;
		}

		public IdleMonitorTextWatcher(Runnable taskWillDoWhenIdle, int idleTimeInMS){
			this.taskWillDoWhenIdle = taskWillDoWhenIdle;
			this.delay = idleTimeInMS;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			handler.removeCallbacks(input_finish_checker);
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() >= 0) {
				last_text_edit = System.currentTimeMillis();
				handler.postDelayed(input_finish_checker, delay);
			}
		}
	}

	private static class CountMonitorTextWatcher implements TextWatcher {
		long delay = 300; // 0.5 seconds after user stops typing
		Handler handler = new Handler();
		private Runnable taskWillDoWhenDone;
		private int count;


		public CountMonitorTextWatcher(Runnable taskWillDoWhenDone, int count){
			this.taskWillDoWhenDone = taskWillDoWhenDone;
			this.count = count;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == count) {
				handler.postDelayed(taskWillDoWhenDone, delay);
			}
		}
	}

	public static void performTaskWhenUnFocus(View view, final Runnable taskWillDoWhenDone){
		if(taskWillDoWhenDone != null) {
			view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean focus) {
					if (!focus) {
						taskWillDoWhenDone.run();
					}
				}
			});
		} else {
			view.setOnFocusChangeListener(null);
		}
	}


	public static void performTaskWhenTypeReachCount(EditText editText, int count, Runnable taskWillDoWhenDone){
		editText.addTextChangedListener(new CountMonitorTextWatcher(taskWillDoWhenDone,count));
	}

	public static void performTaskWhenTypeIdle(EditText editText, Runnable taskWillDoWhenIdle){
		editText.addTextChangedListener(new IdleMonitorTextWatcher(taskWillDoWhenIdle));
	}

	public static void performTaskWhenTypeIdle(EditText editText, Runnable taskWillDoWhenIdle, int idleTimeInMS){
		editText.addTextChangedListener(new IdleMonitorTextWatcher(taskWillDoWhenIdle, idleTimeInMS));
	}

	public static void showContactPicker(Object activityOrFragment, int requestCode){
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(contactPickerIntent, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(contactPickerIntent, requestCode);
		}
	}

	public static class ContactPickerResult {
		private String phoneNumber;
		private String name;
		public ContactPickerResult(String phoneNumber, String name){
			this.phoneNumber = phoneNumber;
			this.name = name;
		}
		public ContactPickerResult(){
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public String getName() {
			return name;
		}
	}

	public static ContactPickerResult handleContactPicker(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		ContactPickerResult contactPickerResult = null;
		if(targetRequestCode == requestCode){
			//handle result
			if(resultCode == Activity.RESULT_OK){
				Activity activity = null;
				if(activityOrFragment instanceof Activity) {
					activity = ((Activity)activityOrFragment);
				} else if(activityOrFragment instanceof Fragment){
					activity = ((Fragment) activityOrFragment).getActivity();
				}
				Cursor cursor = null;
				try {
					contactPickerResult = new ContactPickerResult();
					Uri uri = data.getData();
					cursor = activity.getContentResolver().query(uri, null, null, null, null);
					cursor.moveToFirst();
					int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
					contactPickerResult.setPhoneNumber(cursor.getString(phoneIndex));
					contactPickerResult.setName(cursor.getString(nameIndex));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(cursor != null && !cursor.isClosed()){
						cursor.close();
					}
				}
			}

		}
		return contactPickerResult;
	}

	public static String getCurrencyString(double value){
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');
		String pattern = "#,###.###";
		java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat(pattern, symbols);
		decimalFormat.setGroupingSize(3);
		return decimalFormat.format(value);
	}

	public static String convertDate(String inputDate, String inputFormat, String outputFormat, Locale locale){
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
		String outputDateString = null;
		SimpleDateFormat input = new SimpleDateFormat(inputFormat, (locale == null?Locale.getDefault():locale));
		SimpleDateFormat output = new SimpleDateFormat(outputFormat, (locale == null?Locale.getDefault():locale));
		try {
			Date oneWayTripDate = input.parse(inputDate);
			outputDateString = output.format(oneWayTripDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outputDateString;
	}


	public static String getCurrentTimeInString(Locale locale){
		return getCurrentTimeInString("yyyy-MM-dd HH:mm:ss", locale);
	}

	public static String getCurrentTimeInString(String format, Locale locale){
		SimpleDateFormat df = new SimpleDateFormat(format, (locale == null?Locale.getDefault():locale));
		return df.format(Calendar.getInstance().getTime());
	}


	static boolean isWifiConn = false;
	static boolean isMobileConn = false;

	public static boolean hasNetworkConnection(Context context){
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		isWifiConn = (networkInfo != null && networkInfo.isConnected());
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		isMobileConn = (networkInfo != null && networkInfo.isConnected());
		return isWifiConn || isMobileConn;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public static void showNotification(Context context, String title, String content, Class nextActivity,
										HashMap<String, String> data, int appNameResId, int iconResId, boolean autocancel, boolean isHeadsUp){
		NotificationUtils.showNotification(context,title,content,nextActivity,data,appNameResId,iconResId, autocancel, isHeadsUp);
	}

	public static void showNotification(Context context, String title, String content, Class nextActivity,
										HashMap<String, String> data, int appNameResId, int iconResId,int notifID, String pendingIntentAction, boolean autocancel, boolean isHeadsUp){
		NotificationUtils.showNotification(context,title,content,nextActivity,data,appNameResId,iconResId,notifID,pendingIntentAction, autocancel, isHeadsUp);
	}


	public static void runCodeInWakeLock(Context context,String tag, Runnable runnable){
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
		wl.acquire();
		runnable.run();
		wl.release();
	}


	public static String getRealPathFromMediaStoreUri(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public static void showFilePickerOpenDocument(Object activityOrFragment, String mimeType, int requestCode){
		/*
			image/*
			audio/*
			video/*
			dll

		 */
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(mimeType);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(intent, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(intent, requestCode);
		}
	}

	public static void showFilePickerGetContent(Object activityOrFragment, String mimeType, int requestCode){
		/*
			image/*
			audio/*
			video/*
			dll

		 */
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(mimeType);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(intent, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(intent, requestCode);
		}
	}

	public static Uri handleFilePicker(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		Uri fileResultUri = null;
		if(targetRequestCode == requestCode){
			if(resultCode == Activity.RESULT_OK){
				if(activityOrFragment instanceof Activity) {
					Activity activity = ((Activity)activityOrFragment);
				} else if(activityOrFragment instanceof Fragment){
					Fragment fragment = ((Fragment) activityOrFragment);
				}
				fileResultUri = data.getData();
			}
		}
		return fileResultUri;
	}

	public static String handleFilePickerString(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		String fileResultUri = null;
		if(targetRequestCode == requestCode){
			if(resultCode == Activity.RESULT_OK){
				if(activityOrFragment instanceof Activity) {
					Activity activity = ((Activity)activityOrFragment);
				} else if(activityOrFragment instanceof Fragment){
					Fragment fragment = ((Fragment) activityOrFragment);
				}
				fileResultUri = data.getDataString();
			}
		}
		return fileResultUri;
	}

	public static void showFilePickerFromMediaStore(Object activityOrFragment, Uri mediaStoreUri, int requestCode){
		/*
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		MediaStore.Images.Media.INTERNAL_CONTENT_URI
		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		MediaStore.Audio.Media.INTERNAL_CONTENT_URI
		MediaStore.Video.Media.EXTERNAL_CONTENT_URI
		MediaStore.Video.Media.INTERNAL_CONTENT_URI
		*/
		Intent pickPhoto = new Intent(Intent.ACTION_PICK, mediaStoreUri);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(pickPhoto, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(pickPhoto, requestCode);
		}
	}


	public static String handleFilePickerFromMediaStore(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		String imageResultStringPath = null;
		if(targetRequestCode == requestCode){
			if(resultCode == Activity.RESULT_OK){
				if(activityOrFragment instanceof Activity) {
					Activity activity = ((Activity)activityOrFragment);
					imageResultStringPath = getRealPathFromMediaStoreUri(activity,data.getData());
				} else if(activityOrFragment instanceof Fragment){
					Fragment fragment = ((Fragment) activityOrFragment);
					imageResultStringPath = getRealPathFromMediaStoreUri(fragment.getActivity(),data.getData());
				}
			}
		}
		return imageResultStringPath;
	}


	public static void showImageCapture(Object activityOrFragment, int requestCode){
		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(takePicture, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(takePicture, requestCode);
		}
	}

	public static void showVideoCapture(Object activityOrFragment, int requestCode){
		Intent takePicture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if(activityOrFragment instanceof Activity) {
			Activity activity = ((Activity)activityOrFragment);
			activity.startActivityForResult(takePicture, requestCode);
		} else if(activityOrFragment instanceof Fragment){
			Fragment fragment = ((Fragment) activityOrFragment);
			fragment.startActivityForResult(takePicture, requestCode);
		}
	}

	public static String handleImageVideoCapture(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		String imageResultStringPath = null;
		if(targetRequestCode == requestCode){
			if(resultCode == Activity.RESULT_OK){
				if(activityOrFragment instanceof Activity) {
					Activity activity = ((Activity)activityOrFragment);
					imageResultStringPath = getRealPathFromMediaStoreUri(activity,data.getData());
				} else if(activityOrFragment instanceof Fragment){
					Fragment fragment = ((Fragment) activityOrFragment);
					imageResultStringPath = getRealPathFromMediaStoreUri(fragment.getActivity(),data.getData());
				}
			}

		}
		return imageResultStringPath;
	}

	public static Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			return BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void showDeviceSpecs(Context context){
		String data = "";
		data += "screenHeight in pixel : " + getScreenHeight(context) + "\n";
		data += "screenHeight in dip : " + getDipFromPixel(context, getScreenHeight(context)) + "\n";

		data += "screenWidth in pixel : " + getScreenWidth(context) + "\n";
		data += "screenWidth in dip : " +getDipFromPixel(context, getScreenWidth(context)) + "\n";

		data += "statusBar in pixel : " + getStatusBarHeight(context) + "\n";
		data += "statusBar in dip : " + getDipFromPixel(context, getStatusBarHeight(context)) + "\n";

		data += "actionBar in pixel : " + getActionBarHeight(context) + "\n";
		data += "actionBar in dip : " + getDipFromPixel(context, getActionBarHeight(context)) + "\n";

		data += "navigationBar in pixel : " + getNavigationHeight(context) + "\n";
		data += "navigationBar in dip : " + getDipFromPixel(context, getNavigationHeight(context)) + "\n";


		data += "\n\n";
		data += "screen size type : " + getScreenSizeCategory(context) + "\n";
		data += "density type : " + getDisplayMetricsDensityDPIInString(context) + "\n";
		data += "density : " + getDisplayMetricsDensity(context) + "\n";
		data += "scaled density : " + getDisplayMetricsScaledDensity(context) + "\n";
		data += "density dot per inch : " + getDisplayMetricsDensityDPI(context) + "\n";
		data += "density pixel per inch x : " + getDisplayMetricsRealXDensity(context) + "\n";
		data += "density pixel per inch y : " + getDisplayMetricsRealYDensity(context) + "\n";
		data += "\n\n";
		data += "height screen : " + getDisplayMetricsScreenHeight(context) + "\n";
		data += "width screen : " + getDisplayMetricsScreenWidth(context) + "\n";

		data += "\n\n";
		data += "1 dip =  " + getPixelFromDip(context, 1) + " pixel" +"\n";
		data += "\n\n";
		data += "1 px =  " + getDipFromPixel(context, 1) + " dip" +"\n";

		showInfo(context, "Device Specs", data);
	}

	public static String prettifyUrl(String url){
		String prettyUrl = url;
		if (!Patterns.WEB_URL.matcher(url).matches()) {
			try {
				prettyUrl = CommonUtils.urlEncode(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return prettyUrl;
	}

	public static String urlEncode(String url) throws UnsupportedEncodingException {
		String result = null;
		result = URLEncoder.encode(url,"UTF-8").replace("+", "%20");
		return result;
	}


	public static boolean callNumber(Context activity, String number){
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		if (callIntent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivity(callIntent);
			return true;
		}
		return false;
	}


	public static void navigateGMaps(Context activity, String latLong){
		Uri gmmIntentUri = Uri.parse("google.navigation:q="+latLong);
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivity(mapIntent);
		}else{
			openBrowser(activity,"https://www.google.com/maps?daddr="+latLong);
		}
	}



	public static void showPlacePicker(Object activityOrFragment, int requestCode){
		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
		try {
			if(activityOrFragment instanceof Activity) {
				Activity activity = ((Activity)activityOrFragment);
				activity.startActivityForResult(builder.build(activity), requestCode);
			} else if(activityOrFragment instanceof Fragment){
				Fragment fragment = ((Fragment) activityOrFragment);
				fragment.startActivityForResult(builder.build(fragment.getActivity()), requestCode);
			}
		} catch (GooglePlayServicesRepairableException e) {
			//e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			//e.printStackTrace();
		}
	}

	public static Place handlePlacePicker(Object activityOrFragment, int targetRequestCode, int requestCode, int resultCode, Intent data){
		Place place = null;
		if(targetRequestCode == requestCode){
			if(resultCode == Activity.RESULT_OK){
				if(activityOrFragment instanceof Activity) {
					Activity activity = ((Activity)activityOrFragment);
					place = PlacePicker.getPlace(activity, data);
				} else if(activityOrFragment instanceof Fragment){
					Fragment fragment = ((Fragment) activityOrFragment);
					place = PlacePicker.getPlace(fragment.getActivity(), data);
				}
			}
		}
		return place;
	}


	public static String toCamelCase(String inputString) {
		String result = "";
		if (inputString.length() == 0) {
			return result;
		}
		char firstChar = inputString.charAt(0);
		char firstCharToUpperCase = Character.toUpperCase(firstChar);
		result = result + firstCharToUpperCase;
		for (int i = 1; i < inputString.length(); i++) {
			char currentChar = inputString.charAt(i);
			char previousChar = inputString.charAt(i - 1);
			if (previousChar == ' ') {
				char currentCharToUpperCase = Character.toUpperCase(currentChar);
				result = result + currentCharToUpperCase;
			} else {
				char currentCharToLowerCase = Character.toLowerCase(currentChar);
				result = result + currentCharToLowerCase;
			}
		}
		return result;
	}

	public static String toSentenceCase(String inputString) {
		String result = "";
		if (inputString.length() == 0) {
			return result;
		}
		char firstChar = inputString.charAt(0);
		char firstCharToUpperCase = Character.toUpperCase(firstChar);
		result = result + firstCharToUpperCase;
		boolean terminalCharacterEncountered = false;
		char[] terminalCharacters = {'.', '?', '!'};
		for (int i = 1; i < inputString.length(); i++) {
			char currentChar = inputString.charAt(i);
			if (terminalCharacterEncountered) {
				if (currentChar == ' ') {
					result = result + currentChar;
				} else {
					char currentCharToUpperCase = Character.toUpperCase(currentChar);
					result = result + currentCharToUpperCase;
					terminalCharacterEncountered = false;
				}
			} else {
				char currentCharToLowerCase = Character.toLowerCase(currentChar);
				result = result + currentCharToLowerCase;
			}
			for (int j = 0; j < terminalCharacters.length; j++) {
				if (currentChar == terminalCharacters[j]) {
					terminalCharacterEncountered = true;
					break;
				}
			}
		}
		return result;
	}


	public static void disableWindowInteraction(Activity activity){
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	public static void enableWindowInteraction(Activity activity){
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}


	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	public synchronized static String getRandomUUID(Context context) {
		if (uniqueID == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(
					PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.commit();
			}
		}

		return uniqueID;
	}


	public static String getLibraryVersionName(){
		return BuildConfig.VERSION_NAME;
	}

	public static int getLibraryVersionCode(){
		return BuildConfig.VERSION_CODE;
	}



	public static String getDayName(Calendar calendar, Locale locale){
		return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
				(locale == null ? Locale.getDefault() : locale));
	}
}
