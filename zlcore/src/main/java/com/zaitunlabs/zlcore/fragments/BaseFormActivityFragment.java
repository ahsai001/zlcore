package com.zaitunlabs.zlcore.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.core.BaseFragment;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.FormBuilderUtils;
import com.zaitunlabs.zlcore.utils.FormValidationUtils;
import com.zaitunlabs.zlcore.utils.ViewUtils;

import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseFormActivityFragment extends BaseFragment {
    private static final String ARG_VIEW_JSON = "arg_view_json";
    private String viewJson;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewGroup formContainer;
    private FormBuilderUtils formBuilderUtils;

    public BaseFormActivityFragment() {
    }

    public void setArguments(String viewJson){
        Bundle b = new Bundle();
        b.putString(ARG_VIEW_JSON, viewJson);
        this.setArguments(b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewJson = CommonUtils.getStringFragmentArgument(getArguments(), ARG_VIEW_JSON, null);
        swipeRefreshLayout = view.findViewById(R.id.form_swiperefreshlayout);
        formContainer = view.findViewById(R.id.form_container);
    }


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    protected abstract boolean handleCustomFormCreating(Activity activity, FormBuilderUtils formBuilderUtils, String viewJson, @Nullable Bundle savedInstanceState);
    protected abstract boolean handleCustomLogic(Activity activity, FormBuilderUtils formBuilderUtils, @Nullable Bundle savedInstanceState);
    protected abstract boolean handleCustomAction(Activity activity, FormBuilderUtils formBuilderUtils, String urlQueryString, Map<String, String> keyValueMap, @Nullable Bundle savedInstanceState);
    protected abstract String getImeActionLabelForLastValuableView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonUtils.setWindowSofInputModeResize(getActivity());

        formBuilderUtils = new FormBuilderUtils(getActivity(), savedInstanceState).withParentView(formContainer);

        if(!handleCustomFormCreating(getActivity(), formBuilderUtils, viewJson, savedInstanceState)){
            formBuilderUtils.withViewJson(viewJson);
        }

        formBuilderUtils.render().show();

        if(!handleCustomLogic(getActivity(), formBuilderUtils, savedInstanceState)){
            recognizeButtonAndEnableCustomAction(savedInstanceState);
        }
    }

    protected void recognizeButtonAndEnableCustomAction(@Nullable final Bundle savedInstanceState){
        if(formBuilderUtils.getPageType().equalsIgnoreCase(FormBuilderUtils.PAGE_TYPE_SCROLL) ||
                formBuilderUtils.getPageType().equalsIgnoreCase(FormBuilderUtils.PAGE_TYPE_LINEAR)) {
            Button button = (Button)formBuilderUtils.getLastViewForWidget("button");
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (formBuilderUtils.getFormValidationUtils().validate()) {
                                if(!handleCustomAction(getActivity(), formBuilderUtils,
                                        formBuilderUtils.getUrlQueryStringOfAll(),
                                        formBuilderUtils.getAllValueIDMap(), savedInstanceState)) {
                                }
                            }
                        } catch (FormValidationUtils.ValidatorException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            formBuilderUtils.setAllValuableViewWithImeNext();

            if(TextUtils.isEmpty(getImeActionLabelForLastValuableView())){
                formBuilderUtils.setLastValuableViewWithImeDone();
            } else {
                View lastValuableView = formBuilderUtils.getLastValuableView();
                if (lastValuableView != null && lastValuableView instanceof EditText) {
                    ((EditText) lastValuableView).setImeOptions(EditorInfo.IME_ACTION_DONE);
                    ((EditText) lastValuableView).setImeActionLabel(getImeActionLabelForLastValuableView(), EditorInfo.IME_ACTION_DONE);
                    ((EditText) lastValuableView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == EditorInfo.IME_ACTION_DONE) {
                                try {
                                    if (formBuilderUtils.getFormValidationUtils().validate()) {
                                        if (!handleCustomAction(getActivity(), formBuilderUtils,
                                                formBuilderUtils.getUrlQueryStringOfAll(),
                                                formBuilderUtils.getAllValueIDMap(), savedInstanceState)) {
                                        }
                                    }
                                } catch (FormValidationUtils.ValidatorException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }

        } else if(formBuilderUtils.getPageType().equalsIgnoreCase(FormBuilderUtils.PAGE_TYPE_STEPPER)) {
            formBuilderUtils.setOnHandleCustomActionAtStepper(new FormBuilderUtils.OnHandleCustomActionAtStepper() {
                @Override
                public void handleCustomAction(String urlQueryString, Map<String, String> widgetIDValueMap) {
                    if (!BaseFormActivityFragment.this.handleCustomAction(getActivity(), formBuilderUtils,
                            urlQueryString,
                            widgetIDValueMap, savedInstanceState)) {
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
