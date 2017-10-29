package com.mohamadian.persianhorizontalexpcalendar.view.cell;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;

import com.mohamadian.persianhorizontalexpcalendar.common.Config;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class CustomGradientDrawable extends GradientDrawable {
    private int textColor = Config.CELL_TEXT_CURRENT_MONTH_COLOR;
    private int topMargin = 5 , bottomMargin = 5, leftMargin = 5 , rightMargin = 5;
    private int layoutWidth = ViewGroup.LayoutParams.MATCH_PARENT,
            layoutHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private int layout_gravity = Gravity.CENTER;

    public CustomGradientDrawable(int shape, int color){
        super();
        setShape(shape); //GradientDrawable.LINE
        setColor(color);
    }

    public CustomGradientDrawable(int shape, int[] colors){
        super();
        setShape(shape); //GradientDrawable.LINE
        setColors(colors);
    }

    public CustomGradientDrawable setgradientType(int gradiantType){
        setGradientType(gradiantType);
        return this;
    }

    public CustomGradientDrawable setstroke(int width, int color){
        this.setStroke(width, color);
        return this;
    }

    public CustomGradientDrawable setcornerRadius(int cornerRadius){
        setCornerRadius(cornerRadius);
        return this;
    }

    public CustomGradientDrawable setcornerRadius(int[] cornerRadius){
        setcornerRadius(cornerRadius);
        return this;
    }

    public CustomGradientDrawable setTextColor(int color)
    {
        textColor = color;
        return this;
    }

    public CustomGradientDrawable setViewLayoutSize(int width, int height){
        layoutWidth = width;
        layoutHeight = height;
        return this;
    }

    public CustomGradientDrawable setViewLayoutGravity(int gravity){
        layout_gravity = gravity;
        return this;
    }

    public CustomGradientDrawable setViewMargins(int left, int top, int right, int bottom){
        leftMargin = left;
        rightMargin = right;
        topMargin = top;
        bottomMargin = bottom;
        return this;
    }

    public int getTextColor()
    {
        return textColor;
    }

    public int getLayoutWidth(){
        return layoutWidth;
    }

    public int getLayoutHeight(){
        return layoutHeight;
    }

    public int getLayoutGravity(){
        return layout_gravity;
    }

    public int[] getMargins(){
        return new int[] {leftMargin,topMargin,rightMargin,bottomMargin};
    }

}
