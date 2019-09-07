package com.zaitunlabs.zlcore.fragments

import android.app.Activity
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseFragment
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.FormBuilderUtils
import com.zaitunlabs.zlcore.utils.FormValidationUtils

/**
 * A placeholder fragment containing a simple view.
 */
abstract class BaseFormActivityFragment : BaseFragment() {
    private var viewJson: String? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
        private set
    private var formContainer: ViewGroup? = null
    private var formBuilderUtils: FormBuilderUtils? = null
    protected abstract val imeActionLabelForLastValuableView: String

    fun setArguments(viewJson: String) {
        val b = Bundle()
        b.putString(ARG_VIEW_JSON, viewJson)
        this.arguments = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewJson = CommonUtils.getStringFragmentArgument(arguments, ARG_VIEW_JSON, null)
        swipeRefreshLayout = view.findViewById(R.id.form_swiperefreshlayout)
        formContainer = view.findViewById(R.id.form_container)
    }

    protected abstract fun handleCustomFormCreating(activity: Activity?, formBuilderUtils: FormBuilderUtils?, viewJson: String?, savedInstanceState: Bundle?): Boolean
    protected abstract fun handleCustomLogic(activity: Activity?, formBuilderUtils: FormBuilderUtils, savedInstanceState: Bundle?): Boolean
    protected abstract fun handleCustomAction(activity: Activity?, formBuilderUtils: FormBuilderUtils?, urlQueryString: String, keyValueMap: Map<String, String>, savedInstanceState: Bundle?): Boolean

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CommonUtils.setWindowSofInputModeResize(activity!!)

        formBuilderUtils = FormBuilderUtils(activity!!, savedInstanceState).withParentView(formContainer)

        if (!handleCustomFormCreating(activity, formBuilderUtils, viewJson, savedInstanceState)) {
            formBuilderUtils!!.withViewJson(viewJson)
        }

        formBuilderUtils!!.render().show()

        if (!handleCustomLogic(activity, formBuilderUtils, savedInstanceState)) {
            recognizeButtonAndEnableCustomAction(savedInstanceState)
        }
    }

    protected fun recognizeButtonAndEnableCustomAction(savedInstanceState: Bundle?) {
        if (formBuilderUtils!!.pageType!!.equals(FormBuilderUtils.PAGE_TYPE_SCROLL, ignoreCase = true) || formBuilderUtils!!.pageType!!.equals(FormBuilderUtils.PAGE_TYPE_LINEAR, ignoreCase = true)) {
            val button = formBuilderUtils!!.getLastViewForWidget("button") as Button
            button?.setOnClickListener {
                try {
                    if (formBuilderUtils!!.formValidationUtils!!.validate()) {
                        if (!handleCustomAction(activity, formBuilderUtils,
                                        formBuilderUtils!!.urlQueryStringOfAll,
                                        formBuilderUtils!!.allValueIDMap, savedInstanceState)) {
                        }
                    }
                } catch (e: FormValidationUtils.ValidatorException) {
                    e.printStackTrace()
                }
            }

            formBuilderUtils!!.setAllValuableViewWithImeNext()

            if (TextUtils.isEmpty(imeActionLabelForLastValuableView)) {
                formBuilderUtils!!.setLastValuableViewWithImeDone()
            } else {
                val lastValuableView = formBuilderUtils!!.lastValuableView
                if (lastValuableView != null && lastValuableView is EditText) {
                    lastValuableView.imeOptions = EditorInfo.IME_ACTION_DONE
                    lastValuableView.setImeActionLabel(imeActionLabelForLastValuableView, EditorInfo.IME_ACTION_DONE)
                    lastValuableView.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            try {
                                if (formBuilderUtils!!.formValidationUtils!!.validate()) {
                                    if (!handleCustomAction(activity, formBuilderUtils,
                                                    formBuilderUtils!!.urlQueryStringOfAll,
                                                    formBuilderUtils!!.allValueIDMap, savedInstanceState)) {
                                    }
                                }
                            } catch (e: FormValidationUtils.ValidatorException) {
                                e.printStackTrace()
                            }

                            return@OnEditorActionListener true
                        }
                        false
                    })
                }
            }

        } else if (formBuilderUtils!!.pageType!!.equals(FormBuilderUtils.PAGE_TYPE_STEPPER, ignoreCase = true)) {
            formBuilderUtils!!.setOnHandleCustomActionAtStepper { urlQueryString, widgetIDValueMap ->
                if (!this@BaseFormActivityFragment.handleCustomAction(activity, formBuilderUtils,
                                urlQueryString,
                                widgetIDValueMap, savedInstanceState)) {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private val ARG_VIEW_JSON = "arg_view_json"
    }
}
