package com.zaitunlabs.zlcore.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView

import com.zaitunlabs.zlcore.R

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by ahsai on 11/21/2017.
 */

class FormValidationUtils(context: Context) {
    private val context: Context
    private val mValidatorList: MutableList<Validator>

    val validatorCount: Int
        get() = mValidatorList.size

    init {
        this.context = context.applicationContext
        mValidatorList = ArrayList()
    }

    @JvmOverloads
    fun addValidator(validator: Validator, validationType: Int = DefaultType) {
        mValidatorList.add(validator)
        if (validationType == DefaultType) {
        } else if (validationType == IdleType) {
            enableTypingValidation(validator)
        } else if (validationType == UnfocusType) {
            enableUnfocusValidation(validator)
        }
    }

    private fun enableUnfocusValidation(validator: Validator) {
        CommonUtils.performTaskWhenUnFocus(validator.mView) { validator.validate() }
    }

    private fun enableTypingValidation(validator: Validator) {
        if (validator.mView is EditText) {
            CommonUtils.performTaskWhenTypeIdle((validator.mView as EditText?)!!) { validator.validate() }
        }
    }

    fun getValidator(position: Int): Validator? {
        return if (position < mValidatorList.size) {
            mValidatorList[position]
        } else null
    }

    fun removeValidator(validator: Validator) {
        mValidatorList.remove(validator)
    }

    @Throws(FormValidationUtils.ValidatorException::class)
    fun validate(): Boolean {
        var isFirsErrorFocused = false
        for (x in mValidatorList.indices) {
            val validator = mValidatorList[x]
            for (y in validator.mRuleList!!.indices) {
                val rule = validator.mRuleList!![y]
                if (validator.mView is TextView) {
                    val mTextView = validator.mView as TextView?
                    val isValid = rule.isValid(validator.mView, mTextView!!.text.toString(), validator.packet)
                    if (!isValid) {
                        mTextView.error = rule.message
                        if (!isFirsErrorFocused) {
                            mTextView.requestFocus()
                            isFirsErrorFocused = true
                        }
                        break
                    } else {
                        mTextView.error = null
                    }
                }
            }
        }

        return !isFirsErrorFocused
    }

    @Throws(FormValidationUtils.ValidatorException::class)
    fun validate(onValidationCallback: OnValidationCallback): Boolean {
        var totalRule = 0
        var totalSuccessRule = 0
        for (x in mValidatorList.indices) {
            val validator = mValidatorList[x]
            totalRule += validator.mRuleList!!.size
            for (y in validator.mRuleList!!.indices) {
                val rule = validator.mRuleList!![y]
                if (validator.mView is TextView) {
                    val mTextView = validator.mView as TextView?
                    val isValid = rule.isValid(validator.mView, mTextView!!.text.toString(), validator.packet)
                    if (!isValid) {
                        if (onValidationCallback.onFailed(validator.mView, rule, rule.message)) break
                    } else {
                        totalSuccessRule++
                        if (onValidationCallback.onSuccess(validator.mView, rule)) break
                    }
                }
            }
        }
        onValidationCallback.onComplete(null, totalRule == totalSuccessRule)
        return totalRule == totalSuccessRule
    }


    //Validator class
    class Validator {
        private var mContext: Context? = null
        private var mView: View? = null
        private var packet: Any? = null
        private var mRuleList: MutableList<AbstractValidatorRule>? = null
        private var onValidationCallback: OnValidationCallback? = null
        private var alwaysShowErrorOnView = false

        val ruleCount: Int
            get() = mRuleList!!.size

        private fun setup(context: Context, mView: View, packet: Any?) {
            this.mContext = context.applicationContext
            this.mView = mView
            this.packet = packet
            mRuleList = ArrayList()
        }

        fun getRule(position: Int): AbstractValidatorRule {
            return mRuleList!![position]
        }

        fun setPacket(packet: Any) {
            this.packet = packet
        }

        constructor(context: Context, mView: View, packet: Any) {
            setup(context, mView, packet)
        }

        constructor(context: Context, viewResourceId: Int, packet: Any) {
            val editText = (context as Activity).findViewById<View>(viewResourceId)
            setup(context, editText, packet)
        }

        constructor(context: Context, mView: View) {
            setup(context, mView, null)
        }

        constructor(context: Context, viewResourceId: Int) {
            val editText = (context as Activity).findViewById<View>(viewResourceId)
            setup(context, editText, null)
        }

        fun addValidatorRule(validatorRule: AbstractValidatorRule): Validator {
            mRuleList!!.add(validatorRule)
            return this
        }

        fun setOnValidationCallback(onValidationCallback: OnValidationCallback, alwaysShowErrorOnView: Boolean): Validator {
            this.onValidationCallback = onValidationCallback
            this.alwaysShowErrorOnView = alwaysShowErrorOnView
            return this
        }

        fun setOnValidationCallback(onValidationCallback: OnValidationCallback): Validator {
            setOnValidationCallback(onValidationCallback, false)
            return this
        }

        fun validate() {
            var successCount = 0
            for (y in mRuleList!!.indices) {
                val rule = mRuleList!![y]
                if (mView is TextView) {
                    val mTextView = mView as TextView?
                    var isValid = false
                    try {
                        isValid = rule.isValid(mView, mTextView!!.text.toString(), packet)
                    } catch (e: ValidatorException) {
                        e.printStackTrace()
                    }

                    if (!isValid) {
                        if (onValidationCallback != null) {
                            if (alwaysShowErrorOnView) {
                                mTextView!!.error = rule.message
                            }
                            if (onValidationCallback!!.onFailed(mView, rule, rule.message)) break
                        } else {
                            mTextView!!.error = rule.message
                        }
                    } else {
                        if (onValidationCallback != null) {
                            successCount++
                            if (alwaysShowErrorOnView) {
                                mTextView!!.error = null
                            }
                            if (onValidationCallback!!.onSuccess(mView, rule)) break
                        } else {
                            mTextView!!.error = null
                        }
                    }
                }
            }
            if (onValidationCallback != null) {
                onValidationCallback!!.onComplete(mView, successCount == mRuleList!!.size)
            }
        }
    }

    interface OnValidationCallback {
        fun onSuccess(view: View?, validatorRule: AbstractValidatorRule): Boolean
        fun onFailed(view: View?, validatorRule: AbstractValidatorRule, message: String?): Boolean
        fun onComplete(view: View?, allRuleValid: Boolean)
    }

    abstract class AbstractValidatorRule {
        var context: Context? = null
        var message: String? = null
            private set

        constructor(context: Context) {
            this.context = context.applicationContext
        }

        constructor(context: Context, errorMessage: String) {
            this.context = context.applicationContext
            this.message = errorMessage
        }

        @Throws(FormValidationUtils.ValidatorException::class)
        abstract fun isValid(view: View?, value: String, packet: Any?): Boolean

        fun setErrorMessage(mErrorMessage: String) {
            this.message = mErrorMessage
        }
    }


    //Validation Rules definition
    class NotEmptyValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_please_fill_this))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            return !TextUtils.isEmpty(value)
        }
    }

    class EmailValidatorRule : AbstractValidatorRule {
        private var mDomainName = ""

        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_email_is_invalid))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            if (!TextUtils.isEmpty(value)) {
                if (TextUtils.isEmpty(mDomainName)) {
                    val pattern = Pattern.compile(".+@.+\\.[a-z]+")
                    val matcher = pattern.matcher(value)
                    return matcher.matches()
                } else {
                    val pattern = Pattern.compile(".+@$mDomainName")
                    val matcher = pattern.matcher(value)
                    return matcher.matches()
                }
            } else {
                return true
            }
        }

        fun setDomainName(domainName: String) {
            mDomainName = domainName
        }
    }


    class RegExpValidatorRule : AbstractValidatorRule {
        private var mPattern: Pattern? = null

        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_please_insert_with_valid_format))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            if (mPattern != null) {
                return mPattern!!.matcher(value).matches()
            }
            throw ValidatorException(view!!.context.getString(R.string.zlcore_form_validations_utils_need_set_regex_pattern))
        }

        fun setPattern(pattern: String) {
            mPattern = Pattern.compile(pattern)
        }

        fun setPattern(pattern: Pattern) {
            mPattern = pattern
        }
    }


    class AlphaNumericValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_please_fill_alpha_numeric_only))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            return if (TextUtils.isEmpty(value)) true else TextUtils.isDigitsOnly(value)
        }
    }

    class NumericValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_please_fill_numeric_only))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String?, packet: Any?): Boolean {
            if (TextUtils.isEmpty(value)) return true
            if (value == null || value.length == 0)
                return false
            for (i in 0 until value.length) {
                if (!Character.isDigit(value[i]))
                    return false
            }
            return true
        }
    }

    class SameValueValidatorRule : AbstractValidatorRule {
        private var comparedFieldName: String? = null

        constructor(context: Context, comparedFieldName: String) : super(context) {
            this.comparedFieldName = comparedFieldName
            setErrorMessage(String.format(context.getString(R.string.zlcore_form_validations_utils_value_is_different), comparedFieldName))
        }

        constructor(context: Context, errorMessage: String, comparedFieldName: String) : super(context, errorMessage) {
            this.comparedFieldName = comparedFieldName
        }

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            val comparedView = packet as View?
            val comparedValue = (comparedView as TextView).text.toString()
            return value == comparedValue
        }
    }

    class CountValidatorRule : AbstractValidatorRule {
        private var count: Int = 0

        constructor(context: Context, count: Int) : super(context) {
            setErrorMessage(String.format(context.getString(R.string.zlcore_form_validations_utils_data_must_be_count_digits), count, if (count > 1) "digits" else "digit"))
            this.count = count
        }

        constructor(context: Context, errorMessage: String, count: Int) : super(context, errorMessage) {
            this.count = count
        }

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            return value.length == count
        }
    }

    class PhoneValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_phone_format_is_invalid))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            return if (TextUtils.isEmpty(value)) true else Patterns.PHONE.matcher(value).matches()
        }
    }

    class MustCheckedValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_you_must_check_this))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            if (view is CheckBox) {
                if (view.isChecked) {
                    return true
                } else {
                    //invalid state :
                    //1. remove error message after checked
                    view.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            buttonView.error = null
                            buttonView.isFocusableInTouchMode = false
                        }
                    }
                    view.isFocusableInTouchMode = true

                }
            }
            return false
        }
    }


    class URLValidatorRule : AbstractValidatorRule {
        constructor(context: Context) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_url_format_is_invalid))
        }

        constructor(context: Context, errorMessage: String) : super(context, errorMessage) {}

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            return if (TextUtils.isEmpty(value)) true else Patterns.WEB_URL.matcher(value).matches()
        }
    }

    class DateValidatorRule : AbstractValidatorRule {
        private var dateFormat: String? = null
        private var locale: Locale? = null

        constructor(context: Context, dateFormat: String, locale: Locale) : super(context) {
            setErrorMessage(context.getString(R.string.zlcore_form_validations_utils_date_format_is_invalid))
            this.dateFormat = dateFormat
            this.locale = locale
        }

        constructor(context: Context, errorMessage: String, dateFormat: String, locale: Locale) : super(context, errorMessage) {
            this.dateFormat = dateFormat
            this.locale = locale
        }

        @Throws(FormValidationUtils.ValidatorException::class)
        override fun isValid(view: View?, value: String, packet: Any?): Boolean {
            try {
                val df = SimpleDateFormat(dateFormat, locale)
                df.isLenient = false
                df.parse(value)
                return true
            } catch (e: ParseException) {
                return false
            }

        }
    }


    class ValidatorException : java.lang.Exception {
        constructor() : super() {}

        constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}

        constructor(detailMessage: String) : super(detailMessage) {}

        constructor(throwable: Throwable) : super(throwable) {}
    }


    //helper method

    fun addNotEmptyValidatorForView(context: Context, view: View): Validator {
        val newValidator = FormValidationUtils.Validator(context, view)
        newValidator.addValidatorRule(NotEmptyValidatorRule(context))
        addValidator(newValidator)
        return newValidator
    }

    fun addNotEmptyValidatorForView(context: Context, view: View, errorMessage: String): Validator {
        val newValidator = FormValidationUtils.Validator(context, view)
        newValidator.addValidatorRule(NotEmptyValidatorRule(context, errorMessage))
        addValidator(newValidator)
        return newValidator
    }

    fun addEmailValidatorForView(context: Context, view: View): Validator {
        val newValidator = FormValidationUtils.Validator(context, view)
        newValidator.addValidatorRule(EmailValidatorRule(context))
        addValidator(newValidator)
        return newValidator
    }

    fun addEmailValidatorForView(context: Context, view: View, errorMessage: String): Validator {
        val newValidator = FormValidationUtils.Validator(context, view)
        newValidator.addValidatorRule(EmailValidatorRule(context, errorMessage))
        addValidator(newValidator)
        return newValidator
    }

    companion object {
        val DefaultType = 1
        val IdleType = 2
        val UnfocusType = 3
    }
}
