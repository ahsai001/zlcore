package com.zaitunlabs.zlcore.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ahsai on 11/21/2017.
 */

public class FormValidationUtil {
    public static final String MESSAGE_PLEASE_FILL_THIS = "Please fill this";
    public static final String MESSAGE_EMAIL_IS_INVALID = "Email is invalid";

    public static final String MESSAGE_PLEASE_INSERT_WITH_VALID_FORMAT = "Please insert with valid format";
    public static final String MESSAGE_NEED_SET_REGEX_PATTERN = "You need to set Regexp Pattern first";
    public static final String MESSAGE_PLEASE_FILL_ALPHA_NUMERIC_ONLY = "Please fill with alpha numeric only";
    public static final String MESSAGE_PLEASE_FILL_NUMERIC_ONLY = "Please fill with numeric only";
    public static final String MESSAGE_VALUE_IS_DIFFERENT = "The value is different with %1$s";
    public static final String MESSAGE_DATA_MUST_BE_COUNT_DIGITS = "Data must be %1$d %2$s";
    public static final String MESSAGE_PHONE_FORMAT_IS_INVALID = "Phone format is invalid";
    public static final String MESSAGE_YOU_MUST_CHECK_THIS = "You must check this";
    public static final String MESSAGE_URL_FORMAT_IS_INVALID = "Url format is invalid";
    public static final String MESSAGE_DATE_FORMAT_IS_INVALID = "Date format is invalid";

    public static final int DefaultType = 1;
    public static final int IdleType = 2;
    public static final int UnfocusType = 3;
    private Context context;
    private int defaultValidationType;
    private List<Validator> mValidatorList;
    private ViewEnablerUtil viewEnablerUtil = null;

    public FormValidationUtil(Context context){
        this(context, DefaultType);
    }

    public FormValidationUtil(Context context, int defaultValidationType){
        this.context = context.getApplicationContext();
        mValidatorList = new ArrayList<>();
        this.defaultValidationType = defaultValidationType;
    }


    public void addValidator(Validator validator){
        mValidatorList.add(validator);
    }

    public void setEnablerView(View view){
        viewEnablerUtil = new ViewEnablerUtil(view, mValidatorList.size());
        viewEnablerUtil.init();
    }

    public void init(){
        for (Validator validator : mValidatorList){
            if(viewEnablerUtil != null) {
                validator.addOnValidationCallback(new OnValidationCallback() {
                    @Override
                    public boolean onSuccess(View view, AbstractValidatorRule validatorRule) {
                        return false;
                    }

                    @Override
                    public boolean onFailed(View view, AbstractValidatorRule validatorRule, String message) {
                        return false;
                    }

                    @Override
                    public void onComplete(View view, boolean allRuleValid) {
                        if(allRuleValid){
                            viewEnablerUtil.done(String.valueOf(view.getId()));
                        } else {
                            viewEnablerUtil.unDone(String.valueOf(view.getId()));
                        }
                    }
                }).setAlwaysShowErrorOnView(true).setValidationType(FormValidationUtil.IdleType);
            }

            int validationType = validator.getValidationType();

            if(validationType == IdleType){
                enableTypeIdleValidation(validator);
            } else if(validationType == UnfocusType){
                enableUnfocusValidation(validator);
            }
        }
    }

    private void enableUnfocusValidation(final Validator validator) {
        CommonUtil.performTaskWhenUnFocus(validator.mView, new Runnable() {
            @Override
            public void run() {
                validator.validate();
            }
        });
    }

    private void enableTypeIdleValidation(final Validator validator){
        if(validator.mView instanceof EditText) {
            CommonUtil.performTaskWhenTypeIdle((EditText) validator.mView, new Runnable() {
                @Override
                public void run() {
                    validator.validate();
                }
            });
        }
    }

    public Validator getValidator(int position) {
        if(position < mValidatorList.size()) {
            return mValidatorList.get(position);
        }
        return null;
    }

    public int getValidatorCount(){
        return mValidatorList.size();
    }

    public void removeValidator(Validator validator){
        mValidatorList.remove(validator);
    }

    public boolean validate() throws ValidatorException {
        boolean isFirsErrorFocused = false;
        for (int x=0; x<mValidatorList.size(); x++){
            Validator validator = mValidatorList.get(x);
            for (int y=0; y<validator.mRuleList.size(); y++){
                AbstractValidatorRule rule = validator.mRuleList.get(y);
                if(validator.mView instanceof TextView) {
                    TextView mTextView = (TextView) validator.mView;
                    boolean isValid = rule.isValid(validator.mView, mTextView.getText().toString(), validator.packet);
                    if (!isValid) {
                        mTextView.setError(rule.getMessage());
                        if (!isFirsErrorFocused) {
                            mTextView.requestFocus();
                            isFirsErrorFocused = true;
                        }
                        break;
                    } else {
                        mTextView.setError(null);
                    }
                }
            }
        }

        return !isFirsErrorFocused;
    }

    public boolean validate(OnValidationCallback onValidationCallback) throws ValidatorException {
        int totalRule = 0;
        int totalSuccessRule = 0;
        for (int x=0; x<mValidatorList.size(); x++){
            Validator validator = mValidatorList.get(x);
            totalRule += validator.mRuleList.size();
            for (int y=0; y<validator.mRuleList.size(); y++){
                AbstractValidatorRule rule = validator.mRuleList.get(y);
                if(validator.mView instanceof TextView) {
                    TextView mTextView = (TextView) validator.mView;
                    boolean isValid = rule.isValid(validator.mView, mTextView.getText().toString(), validator.packet);
                    if (!isValid) {
                        if(onValidationCallback.onFailed(validator.mView, rule, rule.getMessage()))break;
                    } else {
                        totalSuccessRule++;
                        if(onValidationCallback.onSuccess(validator.mView, rule))break;
                    }
                }
            }
        }
        onValidationCallback.onComplete(null, totalRule == totalSuccessRule);
        return totalRule == totalSuccessRule;
    }


    //Validator class
    public static class Validator{
        private Context mContext;
        private View mView;
        private Object packet;
        private List<AbstractValidatorRule> mRuleList;
        private List<OnValidationCallback> onValidationCallbackList = new ArrayList<>();
        private boolean alwaysShowErrorOnView = false;
        private int validationType = DefaultType;

        private void setup(Context context, View mView, Object packet){
            this.mContext = context.getApplicationContext();
            this.mView = mView;
            this.packet = packet;
            mRuleList = new ArrayList<>();
        }

        public AbstractValidatorRule getRule(int position){
            return mRuleList.get(position);
        }

        public int getRuleCount(){
            return mRuleList.size();
        }

        public void setPacket(Object packet) {
            this.packet = packet;
        }

        public Validator(Context context, View mView, Object packet){
            setup(context,mView,packet);
        }

        public Validator(Context context, int viewResourceId, Object packet){
            View editText = ((Activity)context).findViewById(viewResourceId);
            setup(context,editText,packet);
        }

        public Validator(Context context, View mView){
            setup(context,mView,null);
        }

        public Validator(Context context, int viewResourceId){
            View editText = ((Activity)context).findViewById(viewResourceId);
            setup(context,editText,null);
        }

        public Validator addRule(AbstractValidatorRule validatorRule){
            mRuleList.add(validatorRule);
            return this;
        }

        public Validator addOnValidationCallback(OnValidationCallback onValidationCallback) {
            this.onValidationCallbackList.add(onValidationCallback);
            return this;
        }

        public Validator setAlwaysShowErrorOnView(boolean alwaysShowErrorOnView) {
            this.alwaysShowErrorOnView = alwaysShowErrorOnView;
            return this;
        }

        public int getValidationType() {
            return validationType;
        }

        public Validator setValidationType(int validationType) {
            this.validationType = validationType;
            return this;
        }

        public void validate(){
            int successCount = 0;
            for (int y=0; y<mRuleList.size(); y++){
                AbstractValidatorRule rule = mRuleList.get(y);
                if(mView instanceof TextView) {
                    TextView mTextView = (TextView) mView;
                    boolean isValid = false;
                    try {
                        isValid = rule.isValid(mView, mTextView.getText().toString(), packet);
                    } catch (ValidatorException e) {
                        e.printStackTrace();
                    }
                    if (!isValid) {
                        if(onValidationCallbackList.size() > 0){
                            if(alwaysShowErrorOnView){
                                mTextView.setError(rule.getMessage());
                            }
                            for (OnValidationCallback callback : onValidationCallbackList){
                                if(callback.onFailed(mView, rule, rule.getMessage()))break;
                                //TODO: need riset in break statement
                            }

                        } else {
                            mTextView.setError(rule.getMessage());
                        }
                    } else {
                        if(onValidationCallbackList.size() > 0){
                            successCount++;
                            if(alwaysShowErrorOnView){
                                mTextView.setError(null);
                            }
                            for (OnValidationCallback callback : onValidationCallbackList){
                                if(callback.onSuccess(mView, rule))break;
                                //TODO: need riset in break statement
                            }
                        } else {
                            mTextView.setError(null);
                        }
                    }
                }
            }
            if(onValidationCallbackList.size() > 0){
                for (OnValidationCallback callback : onValidationCallbackList) {
                    callback.onComplete(mView, successCount == mRuleList.size());
                }
            }
        }
    }

    public interface OnValidationCallback{
        boolean onSuccess(View view, AbstractValidatorRule validatorRule);
        boolean onFailed(View view, AbstractValidatorRule validatorRule, String message);
        void onComplete(View view, boolean allRuleValid);
    }

    public static abstract class AbstractValidatorRule{
        private String mErrorMessage;
        public AbstractValidatorRule(){
        }
        public AbstractValidatorRule(String errorMessage){
            this.mErrorMessage = errorMessage;
        }
        public abstract boolean isValid(View view, String value, Object packet) throws ValidatorException;


        public String getMessage(){
            return mErrorMessage;
        }

        public void setErrorMessage(String mErrorMessage) {
            this.mErrorMessage = mErrorMessage;
        }
    }



    //Validation Rules definition
    public static class NotEmptyValidatorRule extends AbstractValidatorRule{
        public NotEmptyValidatorRule() {
            setErrorMessage(MESSAGE_PLEASE_FILL_THIS);
        }

        public NotEmptyValidatorRule(String errorMessage) {
            setErrorMessage(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            return !TextUtils.isEmpty(value);
        }
    }

    public static class EmailValidatorRule extends AbstractValidatorRule{
        private String mDomainName = "";
        public EmailValidatorRule() {
            setErrorMessage(MESSAGE_EMAIL_IS_INVALID);
        }

        public EmailValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if (!TextUtils.isEmpty(value)) {
                if (TextUtils.isEmpty(mDomainName)) {
                    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
                    Matcher matcher = pattern.matcher(value);
                    return matcher.matches();
                } else {
                    Pattern pattern = Pattern.compile(".+@" + mDomainName);
                    Matcher matcher = pattern.matcher(value);
                    return matcher.matches();
                }
            }else{
                return true;
            }
        }

        public void setDomainName(String domainName) {
            mDomainName = domainName;
        }
    }


    public static class RegExpValidatorRule extends AbstractValidatorRule{
        private Pattern mPattern;
        public RegExpValidatorRule() {
            setErrorMessage(MESSAGE_PLEASE_INSERT_WITH_VALID_FORMAT);
        }

        public RegExpValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if (mPattern != null) {
                return mPattern.matcher(value).matches();
            }
            throw new ValidatorException(MESSAGE_NEED_SET_REGEX_PATTERN);
        }

        public void setPattern(String pattern) {
            mPattern = Pattern.compile(pattern);
        }

        public void setPattern(Pattern pattern) {
            mPattern = pattern;
        }
    }


    public static class AlphaNumericValidatorRule extends AbstractValidatorRule{
        public AlphaNumericValidatorRule() {
            setErrorMessage(MESSAGE_PLEASE_FILL_ALPHA_NUMERIC_ONLY);
        }

        public AlphaNumericValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return TextUtils.isDigitsOnly(value);
        }
    }

    public static class NumericValidatorRule extends AbstractValidatorRule{
        public NumericValidatorRule() {
            setErrorMessage(MESSAGE_PLEASE_FILL_NUMERIC_ONLY);
        }

        public NumericValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            if (value == null || value.length() == 0)
                return false;
            for (int i = 0; i < value.length(); i++) {
                if (!Character.isDigit(value.charAt(i)))
                    return false;
            }
            return true;
        }
    }

    public static class SameValueValidatorRule extends AbstractValidatorRule{
        private String comparedFieldName;
        public SameValueValidatorRule(String comparedFieldName) {
            this.comparedFieldName = comparedFieldName;
            setErrorMessage(String.format(MESSAGE_VALUE_IS_DIFFERENT,comparedFieldName));
        }

        public SameValueValidatorRule(String errorMessage, String comparedFieldName) {
            super(errorMessage);
            this.comparedFieldName = comparedFieldName;
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            View comparedView = (View)packet;
            String comparedValue = ((TextView)comparedView).getText().toString();
            return value.equals(comparedValue);
        }
    }

    public static class CountValidatorRule extends AbstractValidatorRule{
        private int count;
        public CountValidatorRule(int count) {
            setErrorMessage(String.format(MESSAGE_DATA_MUST_BE_COUNT_DIGITS,count,(count>1?"digits":"digit")));
            this.count = count;
        }

        public CountValidatorRule(String errorMessage, int count) {
            super(errorMessage);
            this.count = count;
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            return value.length() == count;
        }
    }

    public static class PhoneValidatorRule extends AbstractValidatorRule{
        public PhoneValidatorRule() {
            setErrorMessage(MESSAGE_PHONE_FORMAT_IS_INVALID);
        }

        public PhoneValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return Patterns.PHONE.matcher(value).matches();
        }
    }

    public static class MustCheckedValidatorRule extends AbstractValidatorRule{
        public MustCheckedValidatorRule(Context context) {
            setErrorMessage(MESSAGE_YOU_MUST_CHECK_THIS);
        }

        public MustCheckedValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(view instanceof CheckBox){
                if(((CheckBox)view).isChecked()){
                    return true;
                }else{
                    //invalid state :
                    //1. remove error message after checked
                    ((CheckBox)view).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                buttonView.setError(null);
                                buttonView.setFocusableInTouchMode(false);
                            }
                        }
                    });
                    ((CheckBox)view).setFocusableInTouchMode(true);

                }
            }
            return false;
        }
    }


    public static class URLValidatorRule extends AbstractValidatorRule{
        public URLValidatorRule() {
            setErrorMessage(MESSAGE_URL_FORMAT_IS_INVALID);
        }

        public URLValidatorRule(String errorMessage) {
            super(errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return Patterns.WEB_URL.matcher(value).matches();
        }
    }

    public static class DateValidatorRule extends AbstractValidatorRule{
        private String dateFormat;
        private TimeZone timeZone;
        private Locale locale;
        public DateValidatorRule(String dateFormat, TimeZone timeZone, Locale locale) {
            setErrorMessage(MESSAGE_DATE_FORMAT_IS_INVALID);
            this.dateFormat = dateFormat;
            this.timeZone = timeZone;
            this.locale = locale;
        }

        public DateValidatorRule(String errorMessage, String dateFormat, TimeZone timeZone, Locale locale) {
            super(errorMessage);
            this.dateFormat = dateFormat;
            this.timeZone = timeZone;
            this.locale = locale;
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            try {
                DateFormat df = new SimpleDateFormat(dateFormat, locale);
                df.setLenient(false);
                if(timeZone != null) {
                    df.setTimeZone(timeZone);
                }
                df.parse(value);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    }



    public static class ValidatorException extends Exception {
        public ValidatorException() {
            super();
        }

        public ValidatorException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ValidatorException(String detailMessage) {
            super(detailMessage);
        }

        public ValidatorException(Throwable throwable) {
            super(throwable);
        }
    }


    //helper method
    public Validator addNotEmptyValidatorForView(View view){
       return addNotEmptyValidatorForView(view, null);
    }

    public Validator addNotEmptyValidatorForView(View view, String errorMessage){
        return addNotEmptyValidatorForView(defaultValidationType, view, errorMessage);
    }

    public Validator addNotEmptyValidatorForView(int validationType, View view){
        return addNotEmptyValidatorForView(validationType, view, null);
    }

    public Validator addNotEmptyValidatorForView(int validationType, View view, String errorMessage){
        Validator newValidator = new FormValidationUtil.Validator(context, view);
        newValidator.addRule(TextUtils.isEmpty(errorMessage)?new NotEmptyValidatorRule():new NotEmptyValidatorRule(errorMessage));
        newValidator.setValidationType(validationType);
        addValidator(newValidator);
        return newValidator;
    }


    public Validator addNewValidatorForView(View view){
        return addNewValidatorForView(defaultValidationType, view);
    }

    public Validator addNewValidatorForView(int validationType, View view){
        Validator newValidator = new FormValidationUtil.Validator(context, view);
        newValidator.setValidationType(validationType);
        addValidator(newValidator);
        return newValidator;
    }
}
