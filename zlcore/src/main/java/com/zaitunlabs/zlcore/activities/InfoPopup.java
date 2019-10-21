package com.zaitunlabs.zlcore.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.models.InformationModel;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.DateStringUtil;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InfoPopup extends BaseActivity {
    public static final String EXTRA_CLASS = "extra_class";
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_INFO_ID = "extra_info_id";
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private Class action;
    private Bundle extraData;
    private List<InformationModel> infoList;
    private int infoPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
        }

        if (Build.VERSION.SDK_INT >= 27) {
            setTurnScreenOn(true);
        }

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissError() {
                    super.onDismissError();
                }

                @Override
                public void onDismissSucceeded() {
                    super.onDismissSucceeded();
                }

                @Override
                public void onDismissCancelled() {
                    super.onDismissCancelled();
                }
            });
        }
        setContentView(R.layout.activity_info_popup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        action = (Class) CommonUtil.getSerializableIntent(getIntent(),EXTRA_CLASS,null);
        extraData = (Bundle) CommonUtil.getBundleIntent(getIntent(),EXTRA_DATA,new Bundle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformationModel info = infoList.get(infoPosition);
                goAction(InfoPopup.this,action,extraData,info.getId());
            }
        });

        infoList = InformationModel.getAllUnreadInfo();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                infoPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        String counter = "";
        getSupportActionBar().setTitle(getString(R.string.zlcore_title_activity_info_popup)+" ("+infoList.size()+") ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /*
        infoList.clear();
        List<InformationModel> newList = InformationModel.getAllUnreadInfo();
        infoList.addAll(newList);

        getSupportActionBar().setTitle(getString(R.string.zlcore_title_activity_info_popup)+" ("+infoList.size()+") ");

        mSectionsPagerAdapter.notifyDataSetChanged();
        */

        //to make new notif turn screen on
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class InfoPopupFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_TITLE = "title";
        private static final String ARG_BODY = "body";
        private static final String ARG_PHOTO = "photo";
        private static final String ARG_URL = "url";
        private static final String ARG_TIME = "time";

        private static final String ARG_ACTION = "action";
        private static final String ARG_PAGE = "page";
        private static final String ARG_INFO_ID = "info_id";

        TextView titleView;
        ExpandableTextView bodyView;
        ImageView imageView;
        TextView timeView;

        Class action;
        int page;
        long infoId;


        public InfoPopupFragment() {
        }
        public static InfoPopupFragment newInstance(int sectionNumber, String title, String body, String photo, String url, Date time, Class action, int page, long infoId) {
            InfoPopupFragment fragment = new InfoPopupFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_TITLE, title);
            args.putString(ARG_BODY, body);
            args.putString(ARG_PHOTO, photo);
            args.putString(ARG_URL, url);
            args.putSerializable(ARG_TIME, time);
            args.putSerializable(ARG_ACTION, action);
            args.putInt(ARG_PAGE, page);
            args.putLong(ARG_INFO_ID, infoId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info_popup, container, false);
            titleView = (TextView) rootView.findViewById(R.id.info_popup_title_row);
            bodyView = (ExpandableTextView) rootView.findViewById(R.id.info_popup_body_row);
            imageView = (ImageView) rootView.findViewById(R.id.info_popup_image_row);
            timeView = (TextView) rootView.findViewById(R.id.info_popup_time_row);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            titleView.setText(CommonUtil.getStringFragmentArgument(getArguments(),ARG_TITLE,""));

            SparseBooleanArray collapseStatus = new SparseBooleanArray();
            collapseStatus.put(0,true);

            bodyView.setText(CommonUtil.getStringFragmentArgument(getArguments(),ARG_BODY,""),collapseStatus,0);

            timeView.setText(DateStringUtil.getDateTimeInString((Date) CommonUtil.getSerializableFragmentArgument(getArguments(),ARG_TIME, Calendar.getInstance().getTime()), null));

            String photo = CommonUtil.getStringFragmentArgument(getArguments(),ARG_PHOTO,"");
            if(!TextUtils.isEmpty(photo) && URLUtil.isValidUrl(photo)){
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(photo).error(R.drawable.ic_error).into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
            }


            action = (Class) CommonUtil.getSerializableFragmentArgument(getArguments(),ARG_ACTION,null);

            page = CommonUtil.getIntFragmentArgument(getArguments(),ARG_PAGE,-1);

            infoId = CommonUtil.getLongFragmentArgument(getArguments(),ARG_INFO_ID, -1);

            view.setTag(infoId);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(action != null) {
                        goAction(getActivity(),action, ((InfoPopup)getActivity()).extraData, (long)view.getTag());
                    }
                }
            });
        }

    }


    private static void goAction(Activity activity, Class action, Bundle extraData, long infoId){
        Intent intent = new Intent(activity.getApplicationContext(), action);
        intent.putExtra(EXTRA_DATA,extraData);
        if(infoId > -1)intent.putExtra(EXTRA_INFO_ID,infoId);
        activity.startActivity(intent);
        activity.finish();
    }



    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            InformationModel info = infoList.get(position);
            String body = info.getBody();
            return InfoPopupFragment.newInstance(position,info.getTitle(),body,info.getPhotoUrl(),info.getInfoUrl(),info.timestamp, action,1, info.getId());
        }


        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return infoList.get(position).getTitle();
        }
    }


    public static void start(Context context, Class actionClass, Bundle extraData){
        Intent intent = new Intent(context.getApplicationContext(), InfoPopup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_CLASS,actionClass);
        intent.putExtra(EXTRA_DATA,extraData);

        context.startActivity(intent);
    }
}
