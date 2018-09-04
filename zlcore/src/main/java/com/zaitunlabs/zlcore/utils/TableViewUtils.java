package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.activeandroid.annotation.Table;
import com.zaitunlabs.zlcore.customs.DataList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 7/16/2018.
 */

public class TableViewUtils {
    private Context context;
    private NestedScrollView topRootView;
    private HorizontalScrollView rootView;
    private TableLayout tableLayout;
    private List<String> headerColumName;
    private List<List<String>> data;
    private int headerResStyle;
    private int bodyResStyle;
    private int borderColor;
    private int fillColor;
    private boolean isHeaderFill = false;
    private int headerFillColor;
    private int headerTextColor;
    private boolean isBodyFill = false;
    private int bodyFillColor;
    private int bodyTextColor;
    private boolean isTailFill = false;
    private int tailFillColor;
    private int tableRadiusInDp;


    public TableViewUtils(Context context){
        this.context = context;
        createNewParentView();
        headerColumName = new ArrayList<>();
        data = new ArrayList<>();
    }


    private void createNewParentView(){
        topRootView = new NestedScrollView(context);
        rootView = new HorizontalScrollView(context);
        topRootView.addView(rootView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tableLayout = new TableLayout(context);
        rootView.addView(tableLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

    }

    public TableViewUtils init(DataList<String> headRow, int borderColor, int fillColor, int tableRadiusInDp){
        this.headerColumName.clear();
        this.headerColumName.addAll(headRow.getArrayList());
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.tableRadiusInDp = tableRadiusInDp;
        return this;
    }

    public TableViewUtils setBody(List<List<String>> data){
        this.data.clear();
        this.data.addAll(data);
        return this;
    }

    public TableViewUtils addBody(List<List<String>> data){
        this.data.addAll(data);
        return this;
    }

    public TableViewUtils setHeaderResStyle(int headerResStyle) {
        this.headerResStyle = headerResStyle;
        return this;
    }

    public TableViewUtils setBodyResStyle(int bodyResStyle) {
        this.bodyResStyle = bodyResStyle;
        return this;
    }

    public TableViewUtils setHeaderColor(int headerFillColor, int headerTextColor) {
        this.headerFillColor = headerFillColor;
        this.headerTextColor = headerTextColor;
        this.isHeaderFill = true;
        return this;
    }

    public TableViewUtils setBodyColor(int bodyFillColor, int bodyTextColor) {
        this.bodyFillColor = bodyFillColor;
        this.bodyTextColor = bodyTextColor;
        this.isBodyFill = true;
        return this;
    }

    public TableViewUtils setTailColor(int tailFillColor) {
        this.tailFillColor = tailFillColor;
        this.isTailFill = true;
        return this;
    }

    public TableViewUtils render(){
        render(true);
        return this;
    }

    public TableViewUtils render(boolean createNewParentView){
        int headerFillColor = this.isHeaderFill ? this.headerFillColor : this.fillColor;
        int bodyFillColor = this.isBodyFill ? this.bodyFillColor : this.fillColor;
        int tailFillColor = this.isTailFill ? this.tailFillColor : this.fillColor;

        if(createNewParentView){
            createNewParentView();
        } else {
            tableLayout.removeAllViews();
        }

        //header
        TableRow headerRow = new TableRow(context);
        for (int i = 0; i< headerColumName.size(); i++) {
            TextView textView = null;
            if(headerResStyle > -1) {
                ContextThemeWrapper wrappedContext = new ContextThemeWrapper(context, headerResStyle);
                textView = new TextView(wrappedContext, null, 0);
            } else {
                textView = new TextView(context);
            }
            textView.setText(headerColumName.get(i));

            if(i==0){
                textView.setBackground(ViewUtils.getLeftHeadTableBackground(context, borderColor, headerFillColor,tableRadiusInDp));
            } else if(i== headerColumName.size()-1){
                textView.setBackground(ViewUtils.getRightHeadTableBackground(context, borderColor, headerFillColor,tableRadiusInDp));
            } else {
                textView.setBackground(ViewUtils.getCenterHeadTableBackground(context, borderColor, headerFillColor));
            }

            if(isHeaderFill) {
                textView.setTextColor(headerTextColor);
            }

            headerRow.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
        tableLayout.addView(headerRow);

        //body
        for (List<String> rowData : data){
            TableRow bodyRow = new TableRow(context);
            for (int i=0;i<rowData.size();i++) {
                TextView textView = null;
                if(bodyResStyle > -1) {
                    ContextThemeWrapper wrappedContext = new ContextThemeWrapper(context, bodyResStyle);
                    textView = new TextView(wrappedContext, null, 0);
                } else {
                    textView = new TextView(context);
                }
                textView.setText(rowData.get(i));
                if(i==0){
                    textView.setBackground(ViewUtils.getLeftBodyTableBackground(context, borderColor, bodyFillColor));
                } else if(i==rowData.size()-1){
                    textView.setBackground(ViewUtils.getRightBodyTableBackground(context, borderColor, bodyFillColor));
                } else {
                    textView.setBackground(ViewUtils.getCenterBodyTableBackground(context, borderColor, bodyFillColor));
                }

                if(isBodyFill) {
                    textView.setTextColor(bodyTextColor);
                }

                bodyRow.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
            tableLayout.addView(bodyRow);
        }

        //footer
        TableRow footerRow = new TableRow(context);
        for (int i = 0; i< headerColumName.size(); i++) {
            TextView textView = null;
            if(headerResStyle > -1) {
                ContextThemeWrapper wrappedContext = new ContextThemeWrapper(context, headerResStyle);
                textView = new TextView(wrappedContext, null, 0);
            } else {
                textView = new TextView(context);
            }
            if(i==0){
                textView.setBackground(ViewUtils.getLeftTailTableBackground(context, borderColor, tailFillColor,tableRadiusInDp));
            } else if(i== headerColumName.size()-1){
                textView.setBackground(ViewUtils.getRightTailTableBackground(context, borderColor, tailFillColor,tableRadiusInDp));
            } else {
                textView.setBackground(ViewUtils.getCenterTailTableBackground(context, borderColor, tailFillColor));
            }
            footerRow.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
        tableLayout.addView(footerRow);
        return this;
    }

    public View getTableView(){
        return topRootView;
    }
}
