package com.zaitunlabs.zlcore.utils;

import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.zaitunlabs.zlcore.views.CustomRecylerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ahsai on 7/13/2018.
 */

public class ViewBindingUtil<T extends View> {
    private Map<Integer, Class<T>> idViewClassMaps;
    private Map<Integer, View> idViewMaps;
    private View parentView;

    public ViewBindingUtil(){
        idViewMaps = new HashMap<>();
        idViewClassMaps = new HashMap<>();
    }

    public ViewBindingUtil<T> setParentView(View parentView){
        this.parentView = parentView;
        return this;
    }

    public ViewBindingUtil<T> init(){
        findViews(parentView);
        return this;
    }

    public static <T extends View> ViewBindingUtil<T> initWithParentView(View parentView){
        ViewBindingUtil<T> viewBindingUtil = new ViewBindingUtil<T>();
        viewBindingUtil.setParentView(parentView);
        viewBindingUtil.findViews(viewBindingUtil.parentView);
        return viewBindingUtil;
    }

    private void findViews(View view){
        if(view.getId() > -1) {
            idViewMaps.put(view.getId(), view);
            idViewClassMaps.put(view.getId(), (Class<T>) view.getClass());
        }
        if(view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for(int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                findViews(child);
            }
        }
    }

    public T getViewWithId(int id){
        if(idViewMaps.containsKey(id)){
            return idViewClassMaps.get(id).cast(idViewMaps.get(id));
        }
        return null;
    }

    public <T extends View> T getViewWithId(int id, Class<T> clazz){
        if(idViewMaps.containsKey(id)){
            return clazz.cast(idViewMaps.get(id));
        }
        return null;
    }

    public TextView getTextView(int id){
        return getViewWithId(id, TextView.class);
    }

    public Button getButton(int id){
        return getViewWithId(id, Button.class);
    }

    public ImageButton getImageButton(int id){
        return getViewWithId(id, ImageButton.class);
    }

    public CardView getCardView(int id){
        return getViewWithId(id, CardView.class);
    }

    public LinearLayout getLinearLayout(int id){
        return getViewWithId(id, LinearLayout.class);
    }

    public TableLayout getTableLayout(int id){
        return getViewWithId(id, TableLayout.class);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(int id){
        return getViewWithId(id, SwipeRefreshLayout.class);
    }

    public NestedScrollView getNestedScrollView(int id){
        return getViewWithId(id, NestedScrollView.class);
    }

    public HorizontalScrollView getHorizontalScrollView(int id){
        return getViewWithId(id, HorizontalScrollView.class);
    }

    public CustomRecylerView getCustomRecylerView(int id){
        return getViewWithId(id, CustomRecylerView.class);
    }

    public RecyclerView getRecyclerView(int id){
        return getViewWithId(id, RecyclerView.class);
    }

    public EditText getEditText(int id){
        return getViewWithId(id, EditText.class);
    }
}
