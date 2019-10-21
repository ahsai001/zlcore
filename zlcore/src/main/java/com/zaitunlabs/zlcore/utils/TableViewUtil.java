package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zaitunlabs.zlcore.customs.DataList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahsai on 7/16/2018.
 */

public class TableViewUtil {
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


    public TableViewUtil(Context context){
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

    public TableViewUtil init(DataList<String> headRow, int borderColor, int fillColor, int tableRadiusInDp){
        this.headerColumName.clear();
        this.headerColumName.addAll(headRow.getArrayList());
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.tableRadiusInDp = tableRadiusInDp;
        return this;
    }

    public TableViewUtil setBody(List<List<String>> data){
        this.data.clear();
        this.data.addAll(data);
        return this;
    }

    public TableViewUtil addBody(List<List<String>> data){
        this.data.addAll(data);
        return this;
    }

    public TableViewUtil setHeaderResStyle(int headerResStyle) {
        this.headerResStyle = headerResStyle;
        return this;
    }

    public TableViewUtil setBodyResStyle(int bodyResStyle) {
        this.bodyResStyle = bodyResStyle;
        return this;
    }

    public TableViewUtil setHeaderColor(int headerFillColor, int headerTextColor) {
        this.headerFillColor = headerFillColor;
        this.headerTextColor = headerTextColor;
        this.isHeaderFill = true;
        return this;
    }

    public TableViewUtil setBodyColor(int bodyFillColor, int bodyTextColor) {
        this.bodyFillColor = bodyFillColor;
        this.bodyTextColor = bodyTextColor;
        this.isBodyFill = true;
        return this;
    }

    public TableViewUtil setTailColor(int tailFillColor) {
        this.tailFillColor = tailFillColor;
        this.isTailFill = true;
        return this;
    }

    public TableViewUtil render(){
        render(true);
        return this;
    }

    public TableViewUtil render(boolean createNewParentView){
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
                textView.setBackground(ViewUtil.getLeftHeadTableBackground(context, borderColor, headerFillColor,tableRadiusInDp));
            } else if(i== headerColumName.size()-1){
                textView.setBackground(ViewUtil.getRightHeadTableBackground(context, borderColor, headerFillColor,tableRadiusInDp));
            } else {
                textView.setBackground(ViewUtil.getCenterHeadTableBackground(context, borderColor, headerFillColor));
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
                    textView.setBackground(ViewUtil.getLeftBodyTableBackground(context, borderColor, bodyFillColor));
                } else if(i==rowData.size()-1){
                    textView.setBackground(ViewUtil.getRightBodyTableBackground(context, borderColor, bodyFillColor));
                } else {
                    textView.setBackground(ViewUtil.getCenterBodyTableBackground(context, borderColor, bodyFillColor));
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
                textView.setBackground(ViewUtil.getLeftTailTableBackground(context, borderColor, tailFillColor,tableRadiusInDp));
            } else if(i== headerColumName.size()-1){
                textView.setBackground(ViewUtil.getRightTailTableBackground(context, borderColor, tailFillColor,tableRadiusInDp));
            } else {
                textView.setBackground(ViewUtil.getCenterTailTableBackground(context, borderColor, tailFillColor));
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
