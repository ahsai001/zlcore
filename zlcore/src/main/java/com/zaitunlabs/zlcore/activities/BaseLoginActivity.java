package com.zaitunlabs.zlcore.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.api.APIResponse;
import com.zaitunlabs.zlcore.core.BaseActivity;
import com.zaitunlabs.zlcore.interfaces.LoginCallbackResult;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.utils.PermissionUtils;
import com.zaitunlabs.zlcore.utils.PrefsData;
import com.zaitunlabs.zlcore.utils.SecurityUtils;
import com.zaitunlabs.zlcore.utils.ViewUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseLoginActivity extends BaseActivity implements LoginCallbackResult{
    private static final String EXTRA_NEXT_ACTIVITY =  "nextActivity";
    private static final String EXTRA_APP_TYPE =  "appType";

    // UI references.
    private TextInputEditText mUserIDView;
    private TextInputEditText mPasswordView;
    private TextInputLayout mUserIDTILView;
    private TextInputLayout mPasswordTILView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView titleView;
    private TextView infoView;
    private Button loginButton;
    private CircleImageView iconView;
    private LinearLayout loginTypeSelectorPanel;

    PermissionUtils permissionUtils;
    boolean isOverridePreviousLogin;

    Class nextActivity;
    String appType;
    int requestCode;
    String appInfo;


    protected abstract String getUserIdHint();
    protected abstract String getUserIdFieldName();
    protected abstract String getUserIdInvalidMessage();
    protected abstract String getPasswordHint();
    protected abstract String getPasswordFieldName();
    protected abstract String getPasswordInvalidMessage();
    protected abstract String getButtonLoginText();

    protected abstract String getLoginTypeFieldName();
    protected abstract String getIconUrl();
    protected abstract int getIconResId();
    protected abstract String getLoginExplaination();
    protected abstract boolean isUserIDValid(String userId);
    protected abstract boolean isPasswordValid(String password);
    protected abstract String getLoginUrl();
    protected abstract String getAPIVersion();
    protected abstract boolean clearAllCache();
    protected abstract HashMap<String, String> getLoginTypeViewValueList();
    protected abstract String getDefaultValueLoginType();
    protected abstract String getCookedPassword(String rawPassword);
    protected abstract boolean isHandleCustomSuccessResponse();
    protected abstract void handleCustomSuccessResponse(JSONObject response, LoginCallbackResult loginCallbackResult);
    protected abstract void handleCustomData(JSONObject data);
    protected abstract boolean isHandleCustomLogin();
    protected abstract void handleCustomLogin(String appType, String username, String password, LoginCallbackResult loginCallbackResult);



    public TextInputEditText getUserIDView() {
        return mUserIDView;
    }

    public TextInputEditText getPasswordView() {
        return mPasswordView;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setBackground(int resId){
        RelativeLayout loginFormRoot = (RelativeLayout) findViewById(R.id.login_form_root);
        loginFormRoot.setBackgroundResource(resId);
    }

    public void setBackground(Drawable drawable){
        RelativeLayout loginFormRoot = (RelativeLayout) findViewById(R.id.login_form_root);
        loginFormRoot.setBackground(drawable);
    }

    public void setProgresBarTextColor(int color){
        TextView progressBarTitleView = (TextView) mProgressView.findViewById(R.id.login_progress_title);
        progressBarTitleView.setTextColor(color);
    }

    public void setProgresBarBackgroundColor(int color){
        mProgressView.setBackgroundColor(color);
    }

    public void setHeaderBackground(int resId){
        LinearLayout loginFormHeader = (LinearLayout) findViewById(R.id.login_form_header);
        loginFormHeader.setBackgroundResource(resId);
    }

    public void setHeaderBackground(Drawable drawable){
        LinearLayout loginFormHeader = (LinearLayout) findViewById(R.id.login_form_header);
        loginFormHeader.setBackground(drawable);
    }

    public void setHeaderBackgroundColor(int color){
        LinearLayout loginFormHeader = (LinearLayout) findViewById(R.id.login_form_header);
        loginFormHeader.setBackgroundColor(color);
    }

    public void setBodyBackground(int resId){
        LinearLayout loginFormBody = (LinearLayout) findViewById(R.id.login_form_body);
        loginFormBody.setBackgroundResource(resId);
    }

    public void setBodyBackground(Drawable drawable){
        LinearLayout loginFormBody = (LinearLayout) findViewById(R.id.login_form_body);
        loginFormBody.setBackground(drawable);
    }

    public void setBodyBackgroundColor(int color){
        LinearLayout loginFormBody = (LinearLayout) findViewById(R.id.login_form_body);
        loginFormBody.setBackgroundColor(color);
    }


    public void setFooterText(String text){
        TextView footerView = (TextView) findViewById(R.id.login_form_footerView);
        footerView.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(text)){
            footerView.setText(text);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nextActivity = (Class)CommonUtils.getSerializableIntent(getIntent(),EXTRA_NEXT_ACTIVITY,null);
        appType = CommonUtils.getStringIntent(getIntent(),EXTRA_APP_TYPE,null);
        requestCode = getCurrentRequestCode();
        appInfo = getLoginExplaination();


        // Set up the login form.
        mUserIDView = (TextInputEditText) findViewById(R.id.login_form_userid);
        mUserIDTILView = (TextInputLayout) findViewById(R.id.login_form_userid_textinputlayout);
        mUserIDTILView.setHint(getUserIdHint()+(TextUtils.isEmpty(appInfo)?"":"*"));


        mPasswordView = (TextInputEditText) findViewById(R.id.login_form_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordTILView = (TextInputLayout) findViewById(R.id.login_form_password_textinputlayout);
        mPasswordTILView.setHint(getPasswordHint()+(TextUtils.isEmpty(appInfo)?"":"*"));

        loginButton = (Button) findViewById(R.id.login_form_button);
        loginButton.setText(getButtonLoginText());
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginButton.setBackground(ViewUtils.getSelectableItemBackgroundWithColor(BaseLoginActivity.this,
                ContextCompat.getColor(BaseLoginActivity.this, R.color.colorPrimary)));

        titleView = (TextView) findViewById(R.id.login_form_title);
        titleView.setTextColor(ContextCompat.getColor(BaseLoginActivity.this, R.color.colorPrimary));

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        iconView = (CircleImageView) findViewById(R.id.login_form_icon);
        if(!TextUtils.isEmpty(getIconUrl()) && URLUtil.isValidUrl(getIconUrl())){
            Picasso.with(BaseLoginActivity.this).load(getIconUrl()).noPlaceholder().error(R.drawable.ic_error).into(iconView);
        } else if(getIconResId() > 0){
            Picasso.with(BaseLoginActivity.this).load(getIconResId()).noPlaceholder().into(iconView);
        }

        loginTypeSelectorPanel = (LinearLayout) findViewById(R.id.loginTypeSelectorPanel);
        if(getLoginTypeViewValueList() != null && getLoginTypeViewValueList().size() > 0) {
            Spinner loginTypeSpinner = loginTypeSelectorPanel.findViewById(R.id.loginTypeSpinner);

            List<String> spinnerViewList = new ArrayList<>();
            final List<String> spinnerValueList = new ArrayList<>();

            for (Map.Entry<String, String> data : getLoginTypeViewValueList().entrySet()){
                spinnerViewList.add(data.getKey());
                spinnerValueList.add(data.getValue());
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                    spinnerViewList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            loginTypeSpinner.setAdapter(spinnerArrayAdapter);
            loginTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    appType = spinnerValueList.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(getDefaultValueLoginType() != null) {
                loginTypeSpinner.setSelection(spinnerValueList.indexOf(getDefaultValueLoginType()));
            }

            loginTypeSelectorPanel.setVisibility(View.VISIBLE);
        } else {
            loginTypeSelectorPanel.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(appInfo)) {
            infoView = (TextView) findViewById(R.id.login_form_credential_infoView);
            infoView.setVisibility(View.VISIBLE);
            infoView.setText("*"+appInfo);
        }
    }


    protected void setLoginButtonMatchParent(){
        LinearLayout.LayoutParams newLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newLinearLayout.setMargins(0,(int)CommonUtils.getPixelFromDip(BaseLoginActivity.this, 32),0,0);
        loginButton.setLayoutParams(newLinearLayout);
    }


    public void attemptLogin() {
        permissionUtils = PermissionUtils.checkPermissionAndGo(this, 1053, new Runnable() {
            @Override
            public void run() {
                CommonUtils.hideKeyboard(BaseLoginActivity.this, mLoginFormView);

                // Reset errors.
                mUserIDView.setError(null);
                mPasswordView.setError(null);

                // Store values at the time of the login attempt.
                String userId = mUserIDView.getText().toString();
                String password = mPasswordView.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_field_required));
                    focusView = mPasswordView;
                    cancel = true;
                }

                if (!isPasswordValid(password)) {
                    mPasswordView.setError(TextUtils.isEmpty(getPasswordInvalidMessage())?
                            getString(R.string.error_invalid_password):getPasswordInvalidMessage());
                    focusView = mPasswordView;
                    cancel = true;
                }

                if (TextUtils.isEmpty(userId)) {
                    mUserIDView.setError(getString(R.string.error_field_required));
                    focusView = mUserIDView;
                    cancel = true;
                }

                if (!isUserIDValid(userId)) {
                    mUserIDView.setError(TextUtils.isEmpty(getUserIdInvalidMessage())?
                            getString(R.string.error_invalid_userid):getUserIdInvalidMessage());
                    focusView = mUserIDView;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    showProgress(true);

                    isOverridePreviousLogin = !TextUtils.isEmpty(PrefsData.getUserID())
                            && !mUserIDView.getText().toString().equals(PrefsData.getUserID());

                    //set username - password to localdata
                    String username = mUserIDView.getText().toString();
                    String cookedPassword = getCookedPassword(mPasswordView.getText().toString());
                    PrefsData.setUserID(username);
                    PrefsData.setSecret(cookedPassword);


                    if(isHandleCustomLogin()){
                        handleCustomLogin(appType, mUserIDView.getText().toString(), mPasswordView.getText().toString(), BaseLoginActivity.this);
                    } else {
                        //do hit api
                        AndroidNetworking.post(getLoginUrl())
                                .setOkHttpClient(HttpClientUtils.getHTTPClient(BaseLoginActivity.this, getAPIVersion()))
                                .addUrlEncodeFormBodyParameter(TextUtils.isEmpty(getUserIdFieldName())?"username":getUserIdFieldName(), username)
                                .addUrlEncodeFormBodyParameter(TextUtils.isEmpty(getPasswordFieldName())?"password":getPasswordFieldName(), cookedPassword)
                                .addUrlEncodeFormBodyParameter(TextUtils.isEmpty(getLoginTypeFieldName())?"loginType":getLoginTypeFieldName(), appType)
                                .setPriority(Priority.HIGH)
                                .setTag("login")
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if(isHandleCustomSuccessResponse()){
                                            handleCustomSuccessResponse(response, BaseLoginActivity.this);
                                        } else {
                                            int status = response.optInt("status");
                                            String message = response.optString("message");
                                            if (status == APIResponse.GENERIC_RESPONSE.OK) {
                                                //success login
                                                JSONObject data = response.optJSONObject("data");
                                                String token = data.optString("token", null);
                                                String name = data.optString("name", null);
                                                String phone = data.optString("phone", null);
                                                String email = data.optString("email", null);
                                                String photo = data.optString("photo", null);
                                                setSuccess(token, name, phone, email, photo);
                                                handleCustomData(data);
                                                CommonUtils.showToast(BaseLoginActivity.this, message);
                                            } else {
                                                setFailed();
                                                CommonUtils.showSnackBar(BaseLoginActivity.this, message);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        setFailed();
                                        if (anError.getErrorCode() != 0) {
                                            // received error from server
                                            // anError.getErrorCode() - the error code from server
                                            // anError.getErrorBody() - the error body from server
                                            // anError.getErrorDetail() - just an error detail

                                            CommonUtils.showSnackBar(BaseLoginActivity.this, anError.getErrorDetail());
                                        } else {
                                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                            CommonUtils.showSnackBar(BaseLoginActivity.this, anError.getErrorDetail());
                                        }
                                    }
                                });
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                CommonUtils.showToast(BaseLoginActivity.this, "Please give permission to run this application");
                finish();
            }
        }, Manifest.permission.READ_PHONE_STATE);

    }

    @Override
    public void setSuccess(String token, String name, String phone, String email, String photoUrl) {
        PrefsData.setLogin(true);
        PrefsData.setLoginType(appType);
        PrefsData.setToken(token);
        PrefsData.setName(name);
        PrefsData.setPhone(phone);
        PrefsData.setEmail(email);
        PrefsData.setPhoto(photoUrl);

        if (isOverridePreviousLogin) {
            //clear old cached
            clearAllCache();
        }

        showProgress(false);

        if(requestCode > -1){
            Intent dataIntent = new Intent();
            dataIntent.putExtra("appType", appType);
            dataIntent.putExtra("token", token);
            dataIntent.putExtra("name", name);
            dataIntent.putExtra("phone", phone);
            dataIntent.putExtra("email", email);
            setResult(Activity.RESULT_OK, dataIntent);
        } else {
            Intent nextIntent = new Intent(BaseLoginActivity.this, nextActivity);
            BaseLoginActivity.this.startActivity(nextIntent);
        }

        finish();
    }

    @Override
    public void setFailed() {
        showProgress(false);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(permissionUtils != null) {
            permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void start(Context context, String appType,Class loginClass, Class afterLoginActivity){
        Intent intent = new Intent(context,loginClass);
        intent.putExtra(EXTRA_NEXT_ACTIVITY,afterLoginActivity);
        intent.putExtra(EXTRA_APP_TYPE,appType);
        context.startActivity(intent);
    }

    public static void startForResult(Activity context, String appType, Class loginClass, int requestCode){
        Intent intent = new Intent(context,loginClass);
        intent.putExtra(EXTRA_APP_TYPE,appType);
        context.startActivityForResult(intent, requestCode);
    }

}

