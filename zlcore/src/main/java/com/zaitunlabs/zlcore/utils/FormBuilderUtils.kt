package com.zaitunlabs.zlcore.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.models.FormArgumentModel
import com.zaitunlabs.zlcore.models.FormPropertiesModel
import com.zaitunlabs.zlcore.models.FormValidationRuleModel
import com.zaitunlabs.zlcore.models.FormViewJsonModel
import com.zaitunlabs.zlcore.models.FormWidgetModel
import com.zaitunlabs.zlcore.views.CustomVerticalStepper

import org.json.JSONObject

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

import de.hdodenhof.circleimageview.CircleImageView
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm

/**
 * Created by ahsai on 5/29/2018.
 */

class FormBuilderUtils : VerticalStepperForm {
    private var formImageView: CircleImageView? = null
    private var separatorView: View? = null
    private var formTitleView: TextView? = null
    private var formDescView: TextView? = null
    private var formScrollPanel: NestedScrollView? = null
    private var formScrollContainer: LinearLayout? = null
    private var formLinearPanel: LinearLayout? = null
    private var formStepperPanel: CustomVerticalStepper? = null
    private val activity: Activity?
    private var context: Context? = null
    private var rootView: ViewGroup? = null
    private var parentView: ViewGroup? = null
    private var layoutInflater: LayoutInflater? = null
    private var formViewJsonModel: FormViewJsonModel? = null
    var formValidationUtils: FormValidationUtils? = null
        private set
    private var validationType = FormValidationUtils.DefaultType
    private var savedInstanceState: Bundle? = null

    val pageType: String?
        get() = formViewJsonModel!!.pageType

    val allValuableWidgets: List<FormWidgetModel>
        get() {
            val resultModelList = ArrayList<FormWidgetModel>()
            for (formWidgetModel in formViewJsonModel!!.formList!!) {
                val value = getValueForWidget(formWidgetModel.id)
                if (value != null) {
                    resultModelList.add(formWidgetModel)
                }
            }
            return resultModelList
        }

    val allValueIDMap: Map<String, String>
        get() {
            val resultMap = HashMap<String, String>()
            for (formWidgetModel in formViewJsonModel!!.formList!!) {
                val value = getValueForWidget(formWidgetModel.id)
                if (value != null) {
                    resultMap[formWidgetModel.fieldName!!] = (value as String?)!!
                }
            }
            return resultMap
        }

    val urlQueryStringOfAll: String
        get() {
            val resultMap = allValueIDMap
            return DataUtils.mapToString(resultMap)
        }

    val lastValuableView: View?
        get() {
            for (i in formViewJsonModel!!.formList!!.size - 1 downTo 0) {
                val valuableView = getValuableViewForWidget(i)
                if (valuableView != null) {
                    return valuableView
                }
            }
            return null
        }

    private val POSITION_DELTA = 1000//to solved same view id in the same hirarchy

    private var onHandleCustomActionAtStepper: OnHandleCustomActionAtStepper? = null


    private val widgetBuilderMap = HashMap<String, WidgetFactory>()


    private val argumentsBuilderMap = HashMap<String, ArgumentFactory>()


    private val nativeMethodBuilderMap = HashMap<String, NativeMethodFactory>()


    private val customMethodBuilderMap = HashMap<String, CustomMethodFactory>()

    private val propertyViewSelectorBuilderMap = HashMap<String, PropertyViewSelectorFactory>()


    private val validationRuleBuilderMap = HashMap<String, ValidationRuleFactory>()

    constructor(activity: Activity, savedInstanceState: Bundle) {
        this.activity = activity
        this.context = activity.baseContext
        this.savedInstanceState = savedInstanceState
        init()
    }

    constructor(context: Context, savedInstanceState: Bundle) {
        this.context = context
        this.savedInstanceState = savedInstanceState
        init()
    }


    fun onSavedInstanceState(outState: Bundle) {
        //do nothing
    }

    private fun init() {
        layoutInflater = LayoutInflater.from(context)
        formValidationUtils = FormValidationUtils(context!!)
        initDefaultWidget()
    }

    fun getRootView(): View? {
        return rootView
    }


    fun withParentView(parentView: ViewGroup): FormBuilderUtils {
        this.parentView = parentView
        rootView = layoutInflater!!.inflate(R.layout.base_form_layout, parentView, false) as ViewGroup
        formImageView = rootView!!.findViewById(R.id.form_imageview)
        formTitleView = rootView!!.findViewById(R.id.form_titleview)
        formDescView = rootView!!.findViewById(R.id.form_descview)
        separatorView = rootView!!.findViewById(R.id.form_separatorview)
        formScrollPanel = rootView!!.findViewById(R.id.form_scroll_panel)
        formScrollContainer = rootView!!.findViewById(R.id.form_scroll_container)
        formStepperPanel = rootView!!.findViewById(R.id.form_stepper_panel)
        formLinearPanel = rootView!!.findViewById(R.id.form_linear_panel)
        return this
    }

    fun withSavedInstanceState(savedInstanceState: Bundle): FormBuilderUtils {
        this.savedInstanceState = savedInstanceState
        return this
    }


    fun withViewJson(viewJson: String): FormBuilderUtils {
        val gson = Gson()
        formViewJsonModel = gson.fromJson<FormViewJsonModel>(viewJson, FormViewJsonModel::class.java!!)
        return this
    }

    fun withViewJson(viewJsonModel: FormViewJsonModel): FormBuilderUtils {
        formViewJsonModel = viewJsonModel
        return this
    }

    fun withViewJsonEmpty(pageTitle: String, pageType: String, logo: String, formTitle: String, formDesc: String): FormBuilderUtils {
        formViewJsonModel = FormViewJsonModel()
        formViewJsonModel!!.pageTitle = pageTitle
        formViewJsonModel!!.pageType = pageType
        formViewJsonModel!!.formTitle = formTitle
        formViewJsonModel!!.formDesc = formDesc
        formViewJsonModel!!.logo = logo
        formViewJsonModel!!.formList = ArrayList()
        return this
    }

    fun setValidationType(validationType: Int): FormBuilderUtils {
        //this config just for linear and scroll type, stepper ignore it
        this.validationType = validationType
        return this
    }


    private fun getWidgetName(widgetId: String?): String? {
        var result: String? = null
        val formWidgetModels = formViewJsonModel!!.formList
        for (formWidgetModel in formWidgetModels!!) {
            if (formWidgetModel.id!!.equals(widgetId!!, ignoreCase = true)) {
                result = formWidgetModel.widgetName
                break
            }
        }
        return result
    }

    private fun getWidgetPosition(widgetId: String?): Int {
        var result = 0
        val formWidgetModels = formViewJsonModel!!.formList
        for (x in formWidgetModels!!.indices) {
            if (formWidgetModels[x].id!!.equals(widgetId!!, ignoreCase = true)) {
                result = x
                break
            }
        }
        return result
    }

    fun getWidgetIdsForWidget(widgetName: String): List<String> {
        val formWidgetModels = formViewJsonModel!!.formList
        val widgetIdsResult = ArrayList<String>()
        for (formWidgetModel in formWidgetModels!!) {
            if (formWidgetModel.widgetName!!.equals(widgetName, ignoreCase = true)) {
                widgetIdsResult.add(formWidgetModel.id)
            }
        }
        return widgetIdsResult
    }

    fun getLastViewForWidget(widgetName: String): View? {
        val widgetIds = getWidgetIdsForWidget(widgetName)
        return if (widgetIds.size > 0) {
            getViewForWidget(widgetIds[widgetIds.size - 1])
        } else null
    }

    fun setAllValuableViewWithImeNext() {
        for (i in 0 until formViewJsonModel!!.formList!!.size) {
            val valuableView = getValuableViewForWidget(i)
            if (valuableView != null && valuableView is TextView) {
                (valuableView as TextView).imeOptions = EditorInfo.IME_ACTION_NEXT
            }
        }
    }

    fun setAllValuableViewWithData(jsonObject: JSONObject) {
        for (i in 0 until formViewJsonModel!!.formList!!.size) {
            val widgetName = formViewJsonModel!!.formList!![i].widgetName
            val fieldName = formViewJsonModel!!.formList!![i].fieldName
            val valuableView = getValuableViewForWidget(i)
            if (valuableView != null) {
                widgetBuilderMap[widgetName]!!.setWidgetValue(valuableView, jsonObject.opt(fieldName))
            }
        }
    }

    fun setLastValuableViewWithImeDone() {
        val valuableView = lastValuableView
        if (valuableView != null && valuableView is TextView) {
            valuableView.imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }


    // ======== Start of Get Root View of Widget ========== //
    fun getViewForWidget(widgetId: String?): View {
        return rootView!!.findViewWithTag(widgetId)
    }


    fun getViewForWidget(position: Int): View {
        val widgetId = formViewJsonModel!!.formList!![position].id
        return getViewForWidget(widgetId)
    }
    // ======== End of Get Root View of Widget ========== //


    // ======== Start of Get Valuable View of Widget ========== //

    fun getValuableViewForWidget(widgetId: String): View? {
        val widgetView = getViewForWidget(widgetId)
        val widgetName = getWidgetName(widgetId)
        val viewIdForValue = widgetBuilderMap.get(widgetName)!!.viewIdForValue
        var valuableView: View? = widgetView.findViewById(viewIdForValue)
        if (valuableView == null) {
            val position = getWidgetPosition(widgetId)
            valuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return valuableView
    }

    fun getValuableViewForWidget(widgetView: View, widgetId: String?): View? {
        val widgetName = getWidgetName(widgetId)
        val viewIdForValue = widgetBuilderMap.get(widgetName)!!.viewIdForValue
        var valuableView: View? = widgetView.findViewById(viewIdForValue)
        if (valuableView == null) {
            val position = getWidgetPosition(widgetId)
            valuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return valuableView
    }

    fun getValuableViewForWidget(position: Int): View? {
        val widgetView = getViewForWidget(position)
        val formWidgetModel = formViewJsonModel!!.formList!![position]
        val viewIdForValue = widgetBuilderMap[formWidgetModel.widgetName]!!.viewIdForValue
        var valuableView: View? = widgetView.findViewById(viewIdForValue)
        if (valuableView == null) {
            valuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return valuableView
    }

    fun getValuableViewForWidget(widgetView: View, position: Int): View? {
        val formWidgetModel = formViewJsonModel!!.formList!![position]
        val viewIdForValue = widgetBuilderMap[formWidgetModel.widgetName]!!.viewIdForValue
        var valuableView: View? = widgetView.findViewById(viewIdForValue)
        if (valuableView == null) {
            valuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return valuableView
    }

    // ======== End of Get Valuable View of Widget ========== //


    // ======== Start of Get Value of Widget ========== //

    fun getValueForWidget(widgetId: String?): Any? {
        val widgetView = getViewForWidget(widgetId)
        val widgetName = getWidgetName(widgetId)
        val viewIdForValue = widgetBuilderMap.get(widgetName)!!.viewIdForValue
        var widgetValuableView: View? = widgetView.findViewById(viewIdForValue)
        if (widgetValuableView == null) {
            val position = getWidgetPosition(widgetId)
            widgetValuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return widgetBuilderMap.get(widgetName)!!.getWidgetValue(widgetValuableView)
    }

    fun getValueForWidget(position: Int): Any {
        val widgetView = getViewForWidget(position)
        val formWidgetModel = formViewJsonModel!!.formList!![position]
        val viewIdForValue = widgetBuilderMap[formWidgetModel.widgetName]!!.viewIdForValue
        var widgetValuableView: View? = widgetView.findViewById(viewIdForValue)
        if (widgetValuableView == null) {
            widgetValuableView = widgetView.findViewById(viewIdForValue + position + POSITION_DELTA)
        }
        return widgetBuilderMap[formWidgetModel.widgetName]!!.getWidgetValue(widgetValuableView)
    }

    // ======== End of Get Value of Widget ========== //


    fun setEnablerIfMandatoryDoneOnView(targetView: View) {
        //need run setValidationType(FormValidationUtils.IdleType)
        val viewEnablerUtils = ViewEnablerUtils(targetView, 0)
        val formValidationUtils = formValidationUtils
        var targetReportTotal = 0
        for (x in 0 until formValidationUtils!!.validatorCount) {
            val validator = formValidationUtils!!.getValidator(x)
            for (y in 0 until validator!!.ruleCount) {
                if (validator!!.getRule(y) is FormValidationUtils.NotEmptyValidatorRule) {
                    validator.setOnValidationCallback(object : FormValidationUtils.OnValidationCallback {
                        override fun onSuccess(view: View?, validatorRule: FormValidationUtils.AbstractValidatorRule): Boolean {
                            return false
                        }

                        override fun onFailed(view: View?, validatorRule: FormValidationUtils.AbstractValidatorRule, message: String?): Boolean {
                            return false
                        }

                        override fun onComplete(view: View?, allRuleValid: Boolean) {
                            if (allRuleValid) {
                                viewEnablerUtils.done()
                            } else {
                                viewEnablerUtils.unDone()
                            }
                        }
                    }, true)
                    targetReportTotal++
                }
            }
        }
        viewEnablerUtils.setTargetedDoneTotal(targetReportTotal).init()
    }


    class WidgetBuilder(widgetName: String, id: String) {
        internal var formBuilderUtils: FormBuilderUtils
        internal var formWidgetModel: FormWidgetModel

        init {
            formWidgetModel = FormWidgetModel()
            formWidgetModel.widgetName = widgetName
            formWidgetModel.id = id
            formWidgetModel.properties = ArrayList()
            formWidgetModel.validation = ArrayList()
        }

        private fun setFormBuilderUtils(formBuilderUtils: FormBuilderUtils) {
            this.formBuilderUtils = formBuilderUtils
        }

        fun addToFormBuilder() {
            this.formBuilderUtils.formViewJsonModel!!.formList!!.add(formWidgetModel)
        }

        fun setId(id: String): WidgetBuilder {
            formWidgetModel.id = id
            return this
        }

        fun setLabel(label: String): WidgetBuilder {
            formWidgetModel.label = label
            return this
        }

        fun setSubLabel(subLabel: String): WidgetBuilder {
            formWidgetModel.subLabel = subLabel
            return this
        }

        fun setProperty(methodName: String, propertyList: ArgumentValueList): WidgetBuilder {
            val formPropertiesModel = FormPropertiesModel()
            formPropertiesModel.propKey = methodName
            val propertiesArgModelList = ArrayList<FormArgumentModel>()
            for ((key, value) in propertyList.arguments) {
                val formArgumentModel = FormArgumentModel()
                formArgumentModel.argType = key
                formArgumentModel.argValue = value
                propertiesArgModelList.add(formArgumentModel)
            }
            formPropertiesModel.setPropArg(propertiesArgModelList)
            formWidgetModel.properties!!.add(formPropertiesModel)
            return this
        }

        fun setValidationRule(ruleName: String, errorMessage: String, argumentValueList: ArgumentValueList): WidgetBuilder {
            val formValidationRuleModel = FormValidationRuleModel()
            formValidationRuleModel.ruleName = ruleName
            formValidationRuleModel.errorMessage = errorMessage
            val argModelList = ArrayList<FormArgumentModel>()
            for ((key, value) in argumentValueList.arguments) {
                val formArgumentModel = FormArgumentModel()
                formArgumentModel.argType = key
                formArgumentModel.argValue = value
                argModelList.add(formArgumentModel)
            }
            formValidationRuleModel.ruleArgs = argModelList
            formWidgetModel.validation!!.add(formValidationRuleModel)
            return this
        }
    }


    fun createWidget(widgetName: String, id: String): WidgetBuilder {
        val widgetBuilder = WidgetBuilder(widgetName, id)
        widgetBuilder.setFormBuilderUtils(this)
        return widgetBuilder
    }


    class ArgumentValueList {
        var arguments: HashMap<String, Any>
            internal set

        init {
            arguments = HashMap()
        }

        fun add(propType: String, propValue: Any): ArgumentValueList {
            arguments[propType] = propValue
            return this
        }
    }


    fun show() {
        parentView!!.addView(rootView)
    }

    fun render(): FormBuilderUtils {
        val pageTitle = formViewJsonModel!!.pageTitle
        val pageType = formViewJsonModel!!.pageType
        val logo = formViewJsonModel!!.logo
        val formtitle = formViewJsonModel!!.formTitle
        val formdesc = formViewJsonModel!!.formDesc
        val widgetList = formViewJsonModel!!.formList

        if (activity != null) {
            (activity as AppCompatActivity).supportActionBar!!.title = pageTitle
        }

        var needShowSeparator = false

        if (!TextUtils.isEmpty(logo) && URLUtil.isValidUrl(logo)) {
            Picasso.get().load(logo).into(formImageView)
            needShowSeparator = true
            formImageView!!.visibility = View.VISIBLE
        } else {
            formImageView!!.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(formtitle)) {
            formTitleView!!.text = formtitle
            needShowSeparator = true
            formTitleView!!.visibility = View.VISIBLE
        } else {
            formTitleView!!.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(formdesc)) {
            formDescView!!.text = formdesc
            needShowSeparator = true
            formDescView!!.visibility = View.VISIBLE
        } else {
            formDescView!!.visibility = View.GONE
        }

        separatorView!!.visibility = if (needShowSeparator) View.VISIBLE else View.GONE

        val viewContainer = getViewContainer(pageType!!)

        if (viewContainer is LinearLayout) {
            setupLinearPanel(viewContainer, widgetList!!)
        } else if (viewContainer is CustomVerticalStepper) {
            setupStepperPanel(viewContainer, widgetList!!)
        }

        return this
    }

    private fun getItemView(widgetList: ArrayList<FormWidgetModel>, position: Int): View? {
        val formWidgetModel = widgetList[position]
        val widgetName = formWidgetModel.widgetName
        var currentView: View? = null
        if (widgetBuilderMap.containsKey(widgetName)) {
            currentView = getWidgetFromBuilder(context, formWidgetModel, position)
        }
        return currentView
    }

    private fun setupLinearPanel(viewContainer: ViewGroup, widgetList: ArrayList<FormWidgetModel>) {
        for (i in widgetList.indices) {
            val currentView = getItemView(widgetList, i)
            if (currentView != null) {
                viewContainer.addView(currentView)
            }
        }

        val titleSteps = arrayOfNulls<String>(0)
        val subTitleSteps = arrayOfNulls<String>(0)

        VerticalStepperFormLayout.Builder.newInstance(formStepperPanel, titleSteps, this, activity)
                .stepsSubtitles(subTitleSteps)
                .init()
    }

    private fun setupStepperPanel(viewContainer: ViewGroup, widgetList: ArrayList<FormWidgetModel>) {
        val colorPrimary = ContextCompat.getColor(context!!.applicationContext, R.color.colorPrimary)
        val colorPrimaryDark = ContextCompat.getColor(context!!.applicationContext, R.color.colorPrimaryDark)

        val titleSteps = arrayOfNulls<String>(widgetList.size)
        val subTitleSteps = arrayOfNulls<String>(widgetList.size)
        for (i in widgetList.indices) {
            val formWidgetModel = widgetList[i]
            titleSteps[i] = formWidgetModel.label
            subTitleSteps[i] = formWidgetModel.subLabel

        }

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(formStepperPanel, titleSteps, this, activity)
                .stepsSubtitles(subTitleSteps)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .materialDesignInDisabledSteps(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .init()


        formStepperPanel!!.makeTitleMultiLine()

        formStepperPanel!!.setActionOnTitleAndSubTitle { }
    }

    private fun getWidgetFromBuilder(context: Context?, formWidgetModel: FormWidgetModel, position: Int): View {
        val widgetID = formWidgetModel.id
        val widgetName = formWidgetModel.widgetName

        val typeList = ArrayList<Class<*>>()
        val valueList = ArrayList<Any>()
        handleArgument(context, formWidgetModel.data, typeList, valueList)

        val widgetView = widgetBuilderMap[widgetName]!!.getWidgetView(context, layoutInflater, rootView, valueList)

        val formPropertiesModels = formWidgetModel.properties
        implementProperties(widgetView, widgetName, formPropertiesModels)

        val formValidationRuleModels = formWidgetModel.validation
        implementValidation(widgetView, widgetID, formValidationRuleModels, position)

        setupLabelView(widgetView, widgetName, formWidgetModel.label!! + if (isWidgetMandatory(formValidationRuleModels)) "*" else "")
        setupSubLabelView(widgetView, widgetName, formWidgetModel.subLabel)

        widgetView.tag = widgetID//mandatory

        val valuableView = getValuableViewForWidget(widgetView, widgetID)
        if (valuableView != null) {
            valuableView.id = valuableView.id + position + POSITION_DELTA
        }
        return widgetView
    }

    private fun isWidgetMandatory(formValidationRuleModels: List<FormValidationRuleModel>?): Boolean {
        if (formValidationRuleModels == null) return false
        for (formValidationRuleModel in formValidationRuleModels) {
            if (formValidationRuleModel.ruleName!!.equals("notempty", ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun setupLabelView(widgetView: View, widgetName: String?, label: String?) {
        val viewIdLabelView = widgetBuilderMap.get(widgetName)!!.viewIdForLabel
        var labelView: View? = widgetView
        if (viewIdLabelView > 0) {
            labelView = widgetView.findViewById(viewIdLabelView)
        }
        if (labelView != null && label != null) {
            widgetBuilderMap.get(widgetName)!!.setLabel(labelView, label)
        }
    }

    private fun setupSubLabelView(widgetView: View, widgetName: String?, subLabel: String?) {
        val viewIdSubLabelView = widgetBuilderMap.get(widgetName)!!.viewIdForSubLabel
        var subLabelView: View? = widgetView
        if (viewIdSubLabelView > 0) {
            subLabelView = widgetView.findViewById(viewIdSubLabelView)
        }
        if (subLabelView != null && subLabel != null) {
            widgetBuilderMap.get(widgetName)!!.setSubLabel(subLabelView, subLabel)
        }
    }

    private fun getCustomWidget(context: Context, formWidgetModel: FormWidgetModel): View? {
        val widgetID = formWidgetModel.id
        val widgetName = formWidgetModel.widgetName
        val formPropertiesModels = formWidgetModel.properties

        var customObject: View? = null
        try {
            val customViewClass = Class.forName(widgetName!!)
            val constructor = customViewClass.getConstructor(Context::class.java!!)
            customObject = constructor.newInstance(context) as View

            implementProperties(customObject, widgetName, formPropertiesModels)

            customObject.id = widgetID!!.hashCode()
            customObject.tag = widgetID
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return customObject
    }


    private fun handleCustomArgument(context: Context?, argType: String?, argValue: Any?, typeList: MutableList<Class<*>>, valueList: MutableList<Any>): Boolean {
        if (argumentsBuilderMap.containsKey(argType)) {
            typeList.add(argumentsBuilderMap.get(argType)!!.getType(argType))
            valueList.add(argumentsBuilderMap.get(argType)!!.getValue(argValue))
            return true
        } else if (argType!!.equals("selectableitem", ignoreCase = true)) {
            val drawable = ViewUtils.getSelectableItemBackgroundWithColor(context, Color.parseColor(argValue as String?))
            typeList.add(Drawable::class.java)
            valueList.add(drawable)
            return true
        } else if (argType.equals("selectableItemBorderLess", ignoreCase = true)) {
            val drawable = ViewUtils.getSelectableItemBackgroundBorderLessWithColor(context, Color.parseColor(argValue as String?))
            typeList.add(Drawable::class.java)
            valueList.add(drawable)
            return true
        } else if (argType.equals("color", ignoreCase = true)) {
            typeList.add(Int::class.javaPrimitiveType)
            valueList.add(Color.parseColor(argValue as String?))
            return true
        } else if (argType.equals("int", ignoreCase = true)) {
            typeList.add(Int::class.javaPrimitiveType)
            if (argValue is Double) {
                valueList.add(argValue.toInt())
            } else if (argValue is Float) {
                valueList.add(argValue.toInt())
            } else if (argValue is Int) {
                valueList.add(argValue)
            } else {
                valueList.add(argValue)
            }
            return true
        } else if (argType.equals("boolean", ignoreCase = true)) {
            typeList.add(Boolean::class.javaPrimitiveType)
            valueList.add(argValue as Boolean)
            return true
        } else if (argType.equals("string", ignoreCase = true)) {
            typeList.add(String::class.java)
            valueList.add(argValue as String?)
            return true
        } else if (argType.equals("chars", ignoreCase = true)) {
            typeList.add(CharSequence::class.java)
            valueList.add(argValue as CharSequence?)
            return true
        } else if (argType.equals("double", ignoreCase = true)) {
            typeList.add(Double::class.javaPrimitiveType)
            valueList.add(argValue as Double)
            return true
        } else if (argType.equals("float", ignoreCase = true)) {
            typeList.add(Float::class.javaPrimitiveType)
            valueList.add(argValue as Float)
            return true
        } else if (argType.equals("viewwithwidgetid", ignoreCase = true)) {
            typeList.add(View::class.java)
            valueList.add(getValuableViewForWidget(argValue as String?))
            return true
        }

        return false
    }

    private fun handleArgument(context: Context?, formArgumentModels: List<FormArgumentModel>?, typeList: MutableList<Class<*>>, valueList: MutableList<Any>) {
        if (formArgumentModels == null) return
        for (x in formArgumentModels.indices) {
            val argType = formArgumentModels[x].argType
            val argValue = formArgumentModels[x].argValue

            if (!handleCustomArgument(context, argType, argValue, typeList, valueList)) {
                try {
                    val thisClass = Class.forName(argType!!)
                    if (Int::class.java == thisClass) {
                        typeList.add(Int::class.javaPrimitiveType)
                        if (argValue is Double) {
                            valueList.add(argValue.toInt())
                        } else if (argValue is Float) {
                            valueList.add(argValue.toInt())
                        } else if (argValue is Int) {
                            valueList.add(argValue)
                        } else {
                            valueList.add(argValue)
                        }
                    } else if (Boolean::class.java == thisClass) {
                        typeList.add(Boolean::class.javaPrimitiveType)
                        valueList.add(argValue as Boolean)
                    } else if (Double::class.java == thisClass) {
                        typeList.add(Boolean::class.javaPrimitiveType)
                        valueList.add(argValue as Double)
                    } else if (Float::class.java == thisClass) {
                        typeList.add(Boolean::class.javaPrimitiveType)
                        valueList.add(argValue as Float)
                    } else {
                        typeList.add(thisClass)
                        valueList.add(argValue)
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

            }
        }
    }


    private fun implementProperties(targetView: View, widgetName: String?, formPropertiesModels: List<FormPropertiesModel>?) {
        if (formPropertiesModels == null) return
        for (formPropertiesModel in formPropertiesModels) {
            val formArgumentModels = formPropertiesModel.propArgs

            val typeList = ArrayList<Class<*>>()
            val valueList = ArrayList<Any>()

            var methodName = formPropertiesModel.propKey
            if (nativeMethodBuilderMap.containsKey(methodName)) {
                methodName = nativeMethodBuilderMap[methodName]!!.getRealMethod(methodName)
            } else if (!methodName!!.startsWith(".")) {
                if (!methodName.startsWith("set")) {
                    methodName = "set" + CommonUtils.toCamelCase(methodName)
                }
            } else {
                methodName = methodName.replace(".", "")
            }

            handleArgument(targetView.context, formArgumentModels, typeList, valueList)


            var viewIds: List<Int>? = null
            if (propertyViewSelectorBuilderMap.containsKey("$widgetName:$methodName")) {
                viewIds = propertyViewSelectorBuilderMap["$widgetName:$methodName"]!!.viewIds
            }

            if (viewIds != null && viewIds.size > 0) {
                for (viewId in viewIds) {
                    val target = targetView.findViewById<View>(viewId)
                    runMethod(target, widgetName, methodName, typeList, valueList)
                }
            } else {
                runMethod(targetView, widgetName, methodName, typeList, valueList)
            }
        }
    }

    private fun implementValidation(targetView: View, widgetId: String?, formValidationRuleModels: List<FormValidationRuleModel>?, position: Int) {
        if (formValidationRuleModels == null) return
        if (formValidationRuleModels.size > 0) {
            val valuableView = getValuableViewForWidget(targetView, widgetId)
            if (valuableView != null) {
                val validator = FormValidationUtils.Validator(targetView.context, valuableView)
                for (formValidationRuleModel in formValidationRuleModels) {
                    val formArgumentModels = formValidationRuleModel.ruleArgs

                    val typeList = ArrayList<Class<*>>()
                    val valueList = ArrayList<Any>()

                    handleArgument(targetView.context, formArgumentModels, typeList, valueList)

                    val ruleName = formValidationRuleModel.ruleName
                    if (validationRuleBuilderMap.containsKey(ruleName)) {
                        val validatorRule = validationRuleBuilderMap[ruleName]!!.getValidationRule(targetView.context, validator, typeList, valueList)
                        if (!TextUtils.isEmpty(formValidationRuleModel.errorMessage)) {
                            validatorRule.setErrorMessage(formValidationRuleModel.errorMessage)
                        }
                        validator.addValidatorRule(validatorRule)
                    }
                }

                if (formViewJsonModel!!.pageType!!.equals(PAGE_TYPE_STEPPER, ignoreCase = true)) {
                    validator.setOnValidationCallback(object : FormValidationUtils.OnValidationCallback {
                        override fun onSuccess(view: View?, validatorRule: FormValidationUtils.AbstractValidatorRule): Boolean {
                            return false
                        }

                        override fun onFailed(view: View?, validatorRule: FormValidationUtils.AbstractValidatorRule, message: String?): Boolean {
                            formStepperPanel!!.setStepAsUncompleted(position, message)
                            return true
                        }

                        override fun onComplete(view: View?, allRuleValid: Boolean) {
                            if (allRuleValid) {
                                formStepperPanel!!.setStepAsCompleted(position)
                            }
                        }
                    })
                    formValidationUtils!!.addValidator(validator, FormValidationUtils.IdleType)
                } else {
                    formValidationUtils!!.addValidator(validator, validationType)
                }
            }
        }
    }

    private fun runMethod(`object`: View, widgetName: String?, methodName: String?, typeList: List<Class<*>>, valueList: List<Any>) {
        if (customMethodBuilderMap.containsKey("$widgetName:$methodName")) {
            customMethodBuilderMap["$widgetName:$methodName"]!!.runMethod(`object`.context, `object`, valueList)
        } else {
            try {
                val method = `object`.javaClass.getMethod(methodName!!, *typeList.toTypedArray<Class<*>>())
                if (method != null) {
                    method.invoke(`object`, *valueList.toTypedArray<Any>())
                }
            } catch (e: IllegalAccessException) {
                DebugUtils.logD("formbuilder", "$widgetName-IllegalAccessException:$methodName")
            } catch (e: InvocationTargetException) {
                DebugUtils.logD("formbuilder", "$widgetName-InvocationTargetException:$methodName")
            } catch (e: NoSuchMethodException) {
                DebugUtils.logD("formbuilder", "$widgetName-NoSuchMethodException:$methodName")
            }

        }
    }


    private fun getViewContainer(pageType: String): ViewGroup? {
        if (pageType.equals(PAGE_TYPE_STEPPER, ignoreCase = true) && activity != null) {
            formStepperPanel!!.visibility = View.VISIBLE
            return formStepperPanel
        } else if (pageType.equals(PAGE_TYPE_LINEAR, ignoreCase = true)) {
            formLinearPanel!!.visibility = View.VISIBLE
            return formLinearPanel
        }

        //default is scroll
        formScrollPanel!!.visibility = View.VISIBLE
        return formScrollContainer
    }

    override fun createStepContentView(stepNumber: Int): View? {
        val widgetList = formViewJsonModel!!.formList
        return getItemView(widgetList!!, stepNumber)
    }

    override fun onStepOpening(stepNumber: Int) {
        if (formViewJsonModel!!.pageType!!.equals(PAGE_TYPE_STEPPER, ignoreCase = true)) {
            val validator = formValidationUtils!!.getValidator(stepNumber)
            if (validator != null) {
                validator.validate()
            } else {
                formStepperPanel!!.setStepAsCompleted(stepNumber)
            }
        }
    }

    override fun sendData() {
        if (formViewJsonModel!!.pageType!!.equals(PAGE_TYPE_STEPPER, ignoreCase = true)) {
            if (onHandleCustomActionAtStepper != null) {
                onHandleCustomActionAtStepper!!.handleCustomAction(urlQueryStringOfAll, allValueIDMap)
            }
        }
    }

    fun setOnHandleCustomActionAtStepper(onHandleCustomActionAtStepper: OnHandleCustomActionAtStepper) {
        this.onHandleCustomActionAtStepper = onHandleCustomActionAtStepper
    }

    interface OnHandleCustomActionAtStepper {
        fun handleCustomAction(urlQueryString: String, widgetIDValueMap: Map<String, String>)
    }

    fun registerNewWidgetFactory(widgetName: String, widgetBuilder: WidgetFactory) {
        widgetBuilderMap[widgetName] = widgetBuilder
    }

    abstract class WidgetFactory {
        abstract val viewIdForLabel: Int
        abstract val viewIdForSubLabel: Int
        abstract val viewIdForValue: Int
        abstract fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View
        abstract fun setLabel(labelView: View, label: String)
        abstract fun setSubLabel(subLabelView: View, subLabel: String)
        abstract fun getWidgetValue(widgetValuableView: View?): Any
        abstract fun setWidgetValue(widgetValuableView: View, value: Any)
    }

    fun registerNewArgumentFactory(argName: String, argumentFactory: ArgumentFactory) {
        argumentsBuilderMap[argName] = argumentFactory
    }

    abstract class ArgumentFactory {
        abstract fun getType(propType: String?): Class<*>
        abstract fun getValue(propValue: Any?): Any
    }

    fun registerNewNativeMethodFactory(methodName: String, nativeMethodFactory: NativeMethodFactory) {
        nativeMethodBuilderMap[methodName] = nativeMethodFactory
    }

    abstract class NativeMethodFactory {
        abstract fun getRealMethod(methodName: String?): String
    }

    fun registerNewCustomMethodFactory(widgetName: String, methodName: String, customMethodFactory: CustomMethodFactory) {
        customMethodBuilderMap["$widgetName:$methodName"] = customMethodFactory
    }

    abstract class CustomMethodFactory {
        abstract fun runMethod(context: Context, view: View, arguments: List<Any>): Any
    }

    fun registerNewPropertyViewSelectorFactory(widgetName: String, methodName: String, propertyViewSelectorFactory: PropertyViewSelectorFactory) {
        propertyViewSelectorBuilderMap["$widgetName:$methodName"] = propertyViewSelectorFactory
    }

    abstract class PropertyViewSelectorFactory {
        abstract val viewIds: List<Int>
    }

    fun registerNewValidationRuleFactory(validationRuleyName: String, validationRuleFactory: ValidationRuleFactory) {
        validationRuleBuilderMap[validationRuleyName] = validationRuleFactory
    }

    abstract class ValidationRuleFactory {
        abstract fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule
    }


    private fun initDefaultWidget() {
        //Widget Factory
        registerNewWidgetFactory("edittext", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = 0

            override val viewIdForSubLabel: Int
                get() = 0

            override val viewIdForValue: Int
                get() = R.id.widget_edittext_edit

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                return layoutInflater!!.inflate(R.layout.base_form_edittext, parentView, false) as TextInputLayout
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as TextInputLayout).hint = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any {
                return (widgetValuableView as TextInputEditText).text!!.toString()
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {
                (widgetValuableView as EditText).setText(value as String)
            }
        })

        registerNewWidgetFactory("edittext2", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = R.id.base_form_edittext_labelView

            override val viewIdForSubLabel: Int
                get() = 0

            override val viewIdForValue: Int
                get() = R.id.base_form_edittext_edittextView

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                return layoutInflater!!.inflate(R.layout.base_form_edittext_label, parentView, false)
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as TextView).text = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any {
                return (widgetValuableView as EditText).text.toString()
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {
                (widgetValuableView as EditText).setText(value as String)
            }
        })

        registerNewWidgetFactory("date", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = R.id.base_form_edittext_labelView

            override val viewIdForSubLabel: Int
                get() = 0

            override val viewIdForValue: Int
                get() = R.id.base_form_edittext_edittextView

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                val editTextLayout = layoutInflater!!.inflate(R.layout.base_form_edittext_label, parentView, false)
                val editText = editTextLayout.findViewById<EditText>(viewIdForValue)
                editText.setText(DateStringUtils.getDateTimeInString(data[1] as String, Calendar.getInstance().time, data[2] as Locale))
                ViewUtils.enableDatePicker(editText, data[1] as String, data[2] as Locale,
                        (activity as AppCompatActivity).supportFragmentManager, data[0] as String, true, null, false)

                return editTextLayout
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as TextView).text = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any {
                return (widgetValuableView as EditText).text.toString()
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {
                (widgetValuableView as EditText).setText(value as String)
            }
        })


        registerNewWidgetFactory("button", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = 0

            override val viewIdForSubLabel: Int
                get() = 0
            override val viewIdForValue: Int
                get() = 0

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                return Button(context)
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as Button).text = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any? {
                return null
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {

            }
        })

        registerNewWidgetFactory("spinner", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = R.id.base_form_spinner_labelView

            override val viewIdForSubLabel: Int
                get() = 0

            override val viewIdForValue: Int
                get() = R.id.base_form_spinner_spinnerView

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                return layoutInflater!!.inflate(R.layout.base_form_spinner_label_leftright, parentView, false)
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as TextView).text = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any? {
                val position = (widgetValuableView as Spinner).selectedItemPosition
                val valueList = widgetValuableView.tag as ArrayList<String> ?: return null
                return valueList[position]
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {

            }
        })


        registerNewWidgetFactory("spinner2", object : WidgetFactory() {

            override val viewIdForLabel: Int
                get() = R.id.base_form_spinner_labelView

            override val viewIdForSubLabel: Int
                get() = 0

            override val viewIdForValue: Int
                get() = R.id.base_form_spinner_spinnerView

            override fun getWidgetView(context: Context?, layoutInflater: LayoutInflater?, parentView: ViewGroup?, data: List<Any>): View {
                return layoutInflater!!.inflate(R.layout.base_form_spinner_label_updown, parentView, false)
            }

            override fun setLabel(labelView: View, label: String) {
                (labelView as TextView).text = label
            }

            override fun setSubLabel(subLabelView: View, subLabel: String) {

            }

            override fun getWidgetValue(widgetValuableView: View?): Any? {
                val position = (widgetValuableView as Spinner).selectedItemPosition
                val valueList = widgetValuableView.tag as ArrayList<String> ?: return null
                return valueList[position]
            }

            override fun setWidgetValue(widgetValuableView: View, value: Any) {

            }
        })

        //Native Method Factory
        registerNewNativeMethodFactory("inputtype", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setInputType"
            }
        })

        registerNewNativeMethodFactory("textcolor", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setTextColor"
            }
        })

        registerNewNativeMethodFactory("backgroundcolor", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setBackgroundColor"
            }
        })

        registerNewNativeMethodFactory("counter", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setCounterEnabled"
            }
        })

        registerNewNativeMethodFactory("countermax", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setCounterMaxLength"
            }
        })

        registerNewNativeMethodFactory("focus", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setFocusable"
            }
        })

        registerNewNativeMethodFactory("click", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setClickable"
            }
        })

        registerNewNativeMethodFactory("enabled", object : NativeMethodFactory() {
            override fun getRealMethod(methodName: String?): String {
                return "setEnabled"
            }
        })


        //Properties Factory
        registerNewArgumentFactory("array", object : ArgumentFactory() {
            override fun getType(propType: String?): Class<*> {
                return ArrayList<*>::class.java
            }

            override fun getValue(propValue: Any?): Any? {
                return propValue
            }
        })
        registerNewArgumentFactory("locale", object : ArgumentFactory() {
            override fun getType(propType: String?): Class<*> {
                return Locale::class.java
            }

            override fun getValue(propValue: Any?): Any {
                val propValueArray = (propValue as String).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                return Locale(propValueArray[0], propValueArray[1])
            }
        })

        //View selector method
        registerNewPropertyViewSelectorFactory("edittext", "setInputType", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.widget_edittext_edit)
                    return widgetIds
                }
        })

        registerNewPropertyViewSelectorFactory("edittext2", "setInputType", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_edittext_edittextView)
                    return widgetIds
                }
        })

        registerNewPropertyViewSelectorFactory("date", "setInputType", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_edittext_edittextView)
                    return widgetIds
                }
        })

        registerNewPropertyViewSelectorFactory("edittext2", "setEnabled", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_edittext_edittextView)
                    return widgetIds
                }
        })

        registerNewPropertyViewSelectorFactory("edittext2", "setReadonly", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_edittext_edittextView)
                    return widgetIds
                }
        })


        registerNewPropertyViewSelectorFactory("spinner", "setData", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_spinner_spinnerView)
                    return widgetIds
                }
        })

        registerNewPropertyViewSelectorFactory("spinner2", "setData", object : PropertyViewSelectorFactory() {
            override val viewIds: List<Int>
                get() {
                    val widgetIds = ArrayList<Int>()
                    widgetIds.add(R.id.base_form_spinner_spinnerView)
                    return widgetIds
                }
        })


        //Custom Method Factory
        registerNewCustomMethodFactory("spinner", "setData", object : CustomMethodFactory() {
            override fun runMethod(context: Context, view: View, arguments: List<Any>): Any? {
                FormCommonUtils.setSpinnerList(context, view as Spinner, arguments[0] as List<String>, arguments[1] as List<String>, null)
                return null
            }
        })
        registerNewCustomMethodFactory("spinner2", "setData", object : CustomMethodFactory() {
            override fun runMethod(context: Context, view: View, arguments: List<Any>): Any? {
                FormCommonUtils.setSpinnerList(context, view as Spinner, arguments[0] as List<String>, arguments[1] as List<String>, null)
                return null
            }
        })

        registerNewCustomMethodFactory("edittext2", "setReadonly", object : CustomMethodFactory() {
            override fun runMethod(context: Context, view: View, arguments: List<Any>): Any? {
                view.isFocusable = false
                view.isClickable = true
                return null
            }
        })

        //validationRuleFactory
        registerNewValidationRuleFactory("fixcount", object : ValidationRuleFactory() {
            override fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule {
                return FormValidationUtils.CountValidatorRule(context, valueList[0] as Int)
            }
        })

        registerNewValidationRuleFactory("notempty", object : ValidationRuleFactory() {
            override fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule {
                return FormValidationUtils.NotEmptyValidatorRule(context)
            }
        })

        registerNewValidationRuleFactory("email", object : ValidationRuleFactory() {
            override fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule {
                return FormValidationUtils.EmailValidatorRule(context)
            }
        })
        registerNewValidationRuleFactory("url", object : ValidationRuleFactory() {
            override fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule {
                return FormValidationUtils.URLValidatorRule(context)
            }
        })
        registerNewValidationRuleFactory("date", object : ValidationRuleFactory() {
            override fun getValidationRule(context: Context, validator: FormValidationUtils.Validator, typeList: List<Class<*>>, valueList: List<Any>): FormValidationUtils.AbstractValidatorRule {
                return FormValidationUtils.DateValidatorRule(context, valueList[0] as String, valueList[1] as Locale)
            }
        })
    }

    companion object {
        val PAGE_TYPE_STEPPER = "stepper"
        val PAGE_TYPE_SCROLL = "scroll"
        val PAGE_TYPE_LINEAR = "linear"
    }

}
