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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ahsai on 11/21/2017.
 */

public class FormValidationUtils {
    public static final int DefaultType = 1;
    public static final int IdleType = 2;
    public static final int UnfocusType = 3;
    private Context context;
    private List<Validator> mValidatorList;

    public FormValidationUtils(Context context){
        this.context = context.getApplicationContext();
        mValidatorList = new ArrayList<>();
    }
    public void addValidator(Validator validator){
        addValidator(validator,DefaultType);
    }

    public void addValidator(Validator validator, int validationType){
        mValidatorList.add(validator);
        if(validationType == DefaultType) {
        } else if(validationType == IdleType){
            enableTypingValidation(validator);
        } else if(validationType == UnfocusType){
            enableUnfocusValidation(validator);
        }
    }

    private void enableUnfocusValidation(final Validator validator) {
        CommonUtils.performTaskWhenUnFocus(validator.mView, new Runnable() {
            @Override
            public void run() {
                validator.validate();
            }
        });
    }

    private void enableTypingValidation(final Validator validator){
        if(validator.mView instanceof EditText) {
            CommonUtils.performTaskWhenTypeIdle((EditText) validator.mView, new Runnable() {
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
        private OnValidationCallback onValidationCallback;
        private boolean alwaysShowErrorOnView = false;

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

        public Validator addValidatorRule(AbstractValidatorRule validatorRule){
            mRuleList.add(validatorRule);
            return this;
        }

        public Validator setOnValidationCallback(OnValidationCallback onValidationCallback, boolean alwaysShowErrorOnView) {
            this.onValidationCallback = onValidationCallback;
            this.alwaysShowErrorOnView = alwaysShowErrorOnView;
            return this;
        }

        public Validator setOnValidationCallback(OnValidationCallback onValidationCallback){
            setOnValidationCallback(onValidationCallback, false);
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
                        if(onValidationCallback != null){
                            if(alwaysShowErrorOnView){
                                mTextView.setError(rule.getMessage());
                            }
                            if(onValidationCallback.onFailed(mView, rule, rule.getMessage()))break;
                        } else {
                            mTextView.setError(rule.getMessage());
                        }
                    } else {
                        if(onValidationCallback != null){
                            successCount++;
                            if(alwaysShowErrorOnView){
                                mTextView.setError(null);
                            }
                            if(onValidationCallback.onSuccess(mView, rule))break;
                        } else {
                            mTextView.setError(null);
                        }
                    }
                }
            }
            if(onValidationCallback != null){
                onValidationCallback.onComplete(mView, successCount == mRuleList.size());
            }
        }
    }

    public interface OnValidationCallback{
        public boolean onSuccess(View view, AbstractValidatorRule validatorRule);
        public boolean onFailed(View view, AbstractValidatorRule validatorRule, String message);
        public void onComplete(View view, boolean allRuleValid);
    }

    public static abstract class AbstractValidatorRule{
        private Context mContext;
        private String mErrorMessage;
        public AbstractValidatorRule(Context context){
            this.mContext = context.getApplicationContext();
        }
        public AbstractValidatorRule(Context context, String errorMessage){
            this.mContext = context.getApplicationContext();
            this.mErrorMessage = errorMessage;
        }
        public abstract boolean isValid(View view, String value, Object packet) throws ValidatorException;


        public String getMessage(){
            return mErrorMessage;
        }

        public Context getContext() {
            return mContext;
        }

        public void setContext(Context mContext) {
            this.mContext = mContext;
        }

        public void setErrorMessage(String mErrorMessage) {
            this.mErrorMessage = mErrorMessage;
        }
    }



    //Validation Rules definition
    public static class NotEmptyValidatorRule extends AbstractValidatorRule{
        public NotEmptyValidatorRule(Context context) {
            super(context);
            setErrorMessage("Please fill this");
        }

        public NotEmptyValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            return !TextUtils.isEmpty(value);
        }
    }

    public static class EmailValidatorRule extends AbstractValidatorRule{
        private String mDomainName = "";
        public EmailValidatorRule(Context context) {
            super(context);
            setErrorMessage("Email is invalid");
        }

        public EmailValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
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
        public RegExpValidatorRule(Context context) {
            super(context);
            setErrorMessage("Please insert with the valid format");
        }

        public RegExpValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if (mPattern != null) {
                return mPattern.matcher(value).matches();
            }
            throw new ValidatorException("You need to set Regexp Pattern first");
        }

        public void setPattern(String pattern) {
            mPattern = Pattern.compile(pattern);
        }

        public void setPattern(Pattern pattern) {
            mPattern = pattern;
        }
    }


    public static class AlphaNumericValidatorRule extends AbstractValidatorRule{
        public AlphaNumericValidatorRule(Context context) {
            super(context);
            setErrorMessage("Please fill with alpha numeric only");
        }

        public AlphaNumericValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return TextUtils.isDigitsOnly(value);
        }
    }

    public static class NumericValidatorRule extends AbstractValidatorRule{
        public NumericValidatorRule(Context context) {
            super(context);
            setErrorMessage("Please fill with numeric only");
        }

        public NumericValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
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
        public SameValueValidatorRule(Context context, String comparedFieldName) {
            super(context);
            this.comparedFieldName = comparedFieldName;
            setErrorMessage("The value is different with "+comparedFieldName);
        }

        public SameValueValidatorRule(Context context, String errorMessage, String comparedFieldName) {
            super(context, errorMessage);
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
        public CountValidatorRule(Context context, int count) {
            super(context);
            setErrorMessage("Data must be "+count+(count>1?" digits":" digit"));
            this.count = count;
        }

        public CountValidatorRule(Context context, String errorMessage, int count) {
            super(context, errorMessage);
            this.count = count;
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            return value.length() == count;
        }
    }

    public static class PhoneValidatorRule extends AbstractValidatorRule{
        public PhoneValidatorRule(Context context) {
            super(context);
            setErrorMessage("Phone format is invalid");
        }

        public PhoneValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return Patterns.PHONE.matcher(value).matches();
        }
    }

    public static class MustCheckedValidatorRule extends AbstractValidatorRule{
        public MustCheckedValidatorRule(Context context) {
            super(context);
            setErrorMessage("You must check this");
        }

        public MustCheckedValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
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
        public URLValidatorRule(Context context) {
            super(context);
            setErrorMessage("Url format is invalid");
        }

        public URLValidatorRule(Context context, String errorMessage) {
            super(context, errorMessage);
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            if(TextUtils.isEmpty(value))return true;
            return Patterns.WEB_URL.matcher(value).matches();
        }
    }

    public static class DateValidatorRule extends AbstractValidatorRule{
        private String dateFormat;
        private Locale locale;
        public DateValidatorRule(Context context, String dateFormat, Locale locale) {
            super(context);
            setErrorMessage("Date format is invalid");
            this.dateFormat = dateFormat;
            this.locale = locale;
        }

        public DateValidatorRule(Context context, String errorMessage, String dateFormat, Locale locale) {
            super(context, errorMessage);
            this.dateFormat = dateFormat;
            this.locale = locale;
        }

        @Override
        public boolean isValid(View view, String value, Object packet) throws ValidatorException {
            try {
                DateFormat df = new SimpleDateFormat(dateFormat, locale);
                df.setLenient(false);
                df.parse(value);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    }



    public static class ValidatorException extends java.lang.Exception {
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

    public Validator addNotEmptyValidatorForView(Context context, View view){
        Validator newValidator = new FormValidationUtils.Validator(context, view);
        newValidator.addValidatorRule(new NotEmptyValidatorRule(context));
        addValidator(newValidator);
        return newValidator;
    }

    public Validator addNotEmptyValidatorForView(Context context, View view, String errorMessage){
        Validator newValidator = new FormValidationUtils.Validator(context, view);
        newValidator.addValidatorRule(new NotEmptyValidatorRule(context, errorMessage));
        addValidator(newValidator);
        return newValidator;
    }

    public Validator addEmailValidatorForView(Context context, View view){
        Validator newValidator = new FormValidationUtils.Validator(context, view);
        newValidator.addValidatorRule(new EmailValidatorRule(context));
        addValidator(newValidator);
        return newValidator;
    }

    public Validator addEmailValidatorForView(Context context, View view, String errorMessage){
        Validator newValidator = new FormValidationUtils.Validator(context, view);
        newValidator.addValidatorRule(new EmailValidatorRule(context, errorMessage));
        addValidator(newValidator);
        return newValidator;
    }
}
