package com.zaitunlabs.zlcore.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.zaitunlabs.zlcore.utils.CommonUtil;
import com.zaitunlabs.zlcore.utils.FormBuilderUtil;
import com.zaitunlabs.zlcore.utils.FormValidationUtil;

import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class BaseFormActivityFragment extends BaseFragment {
    private static final String ARG_VIEW_JSON = "arg_view_json";
    private String viewJson;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewGroup formContainer;
    private FormBuilderUtil formBuilderUtil;

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

        viewJson = CommonUtil.getStringFragmentArgument(getArguments(), ARG_VIEW_JSON, null);
        swipeRefreshLayout = view.findViewById(R.id.form_swiperefreshlayout);
        formContainer = view.findViewById(R.id.form_container);
    }


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    protected abstract void handleInit(Activity activity, FormBuilderUtil formBuilderUtil, @Nullable Bundle savedInstanceState);
    protected abstract boolean handleCustomFormCreating(Activity activity, FormBuilderUtil formBuilderUtil, String viewJson, @Nullable Bundle savedInstanceState);
    protected abstract boolean handleCustomLogic(Activity activity, FormBuilderUtil formBuilderUtil, @Nullable Bundle savedInstanceState);
    protected abstract boolean handleCustomAction(Activity activity, FormBuilderUtil formBuilderUtil, String urlQueryString, Map<String, String> keyValueMap, @Nullable Bundle savedInstanceState);
    protected abstract String getImeActionLabelForLastValuableView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonUtil.setWindowSofInputModeResize(getActivity());

        formBuilderUtil = new FormBuilderUtil(getActivity(), savedInstanceState).withParentView(formContainer);

        handleInit(getActivity(), formBuilderUtil, savedInstanceState);

        if(!handleCustomFormCreating(getActivity(), formBuilderUtil, viewJson, savedInstanceState)){
            formBuilderUtil.withViewJson(viewJson);
        }

        formBuilderUtil.render().show();

        if(!handleCustomLogic(getActivity(), formBuilderUtil, savedInstanceState)){
            recognizeButtonAndEnableCustomAction(savedInstanceState);
        }
    }

    protected void recognizeButtonAndEnableCustomAction(@Nullable final Bundle savedInstanceState){
        if(formBuilderUtil.getPageType().equalsIgnoreCase(FormBuilderUtil.PAGE_TYPE_SCROLL) ||
                formBuilderUtil.getPageType().equalsIgnoreCase(FormBuilderUtil.PAGE_TYPE_LINEAR)) {
            Button button = (Button) formBuilderUtil.getLastViewForWidget("button");
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (formBuilderUtil.getFormValidationUtils().validate()) {
                                if(!handleCustomAction(getActivity(), formBuilderUtil,
                                        formBuilderUtil.getUrlQueryStringOfAll(),
                                        formBuilderUtil.getAllValueIDMap(), savedInstanceState)) {
                                }
                            }
                        } catch (FormValidationUtil.ValidatorException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            formBuilderUtil.setAllValuableViewWithImeNext();

            if(TextUtils.isEmpty(getImeActionLabelForLastValuableView())){
                formBuilderUtil.setLastValuableViewWithImeDone();
            } else {
                View lastValuableView = formBuilderUtil.getLastValuableView();
                if (lastValuableView != null && lastValuableView instanceof EditText) {
                    ((EditText) lastValuableView).setImeOptions(EditorInfo.IME_ACTION_DONE);
                    ((EditText) lastValuableView).setImeActionLabel(getImeActionLabelForLastValuableView(), EditorInfo.IME_ACTION_DONE);
                    ((EditText) lastValuableView).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == EditorInfo.IME_ACTION_DONE) {
                                try {
                                    if (formBuilderUtil.getFormValidationUtils().validate()) {
                                        if (!handleCustomAction(getActivity(), formBuilderUtil,
                                                formBuilderUtil.getUrlQueryStringOfAll(),
                                                formBuilderUtil.getAllValueIDMap(), savedInstanceState)) {
                                        }
                                    }
                                } catch (FormValidationUtil.ValidatorException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }

        } else if(formBuilderUtil.getPageType().equalsIgnoreCase(FormBuilderUtil.PAGE_TYPE_STEPPER)) {
            formBuilderUtil.setOnHandleCustomActionAtStepper(new FormBuilderUtil.OnHandleCustomActionAtStepper() {
                @Override
                public void handleCustomAction(String urlQueryString, Map<String, String> widgetIDValueMap) {
                    if (!BaseFormActivityFragment.this.handleCustomAction(getActivity(), formBuilderUtil,
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
