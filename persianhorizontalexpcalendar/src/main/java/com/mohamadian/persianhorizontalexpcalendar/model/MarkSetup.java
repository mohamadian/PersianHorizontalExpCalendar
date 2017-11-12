package com.mohamadian.persianhorizontalexpcalendar.model;

import android.graphics.Color;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class MarkSetup {

    private boolean today;
    private boolean selected;
    private boolean smallOval_Bottom;
    private boolean VerticalLine_Right;
    private boolean customGradientDrawable;

    private int smallOval_Bottom_Color = Color.parseColor("#AAFF3333");
    private int VerticalLine_Right_Color = Color.parseColor("#0095f3");
    private CustomGradientDrawable customGradientDrawableMark = null;

    public MarkSetup() {
        this.today = false;
        this.selected = false;
        this.smallOval_Bottom = false;
        this.VerticalLine_Right = false;
        customGradientDrawable = false;
    }

    public MarkSetup(boolean today, boolean selected) {
        this(today, selected, false, 0);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, int smallOval_Bottom_Color) {
        this(today, selected, smallOval_Bottom, false, smallOval_Bottom_Color, 0);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, boolean VerticalLine_Right, int smallOval_Bottom_Color, int VerticalLine_Right_Color) {
        this(today, selected, smallOval_Bottom, VerticalLine_Right, false, smallOval_Bottom_Color, VerticalLine_Right_Color, null);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, boolean VerticalLine_Right,
                     boolean customGradientDrawable, int smallOval_Bottom_Color, int VerticalLine_Right_Color,
                     CustomGradientDrawable customGradientDrawableMark) {
        this.today = today;
        this.selected = selected;
        this.smallOval_Bottom = smallOval_Bottom;
        this.VerticalLine_Right = VerticalLine_Right;
        this.customGradientDrawable = customGradientDrawable;

        this.smallOval_Bottom_Color = smallOval_Bottom_Color;
        this.VerticalLine_Right_Color = VerticalLine_Right_Color;
        this.customGradientDrawableMark = customGradientDrawableMark;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSmallOval_Bottom() {
        return smallOval_Bottom;
    }

    public void setSmallOval_Bottom(boolean smallOval_Bottom, int color) {
        this.smallOval_Bottom = smallOval_Bottom;
        this.smallOval_Bottom_Color = color;
    }

    public int getSmallOval_BottomColor() {
        return this.smallOval_Bottom_Color;
    }

    public boolean isVerticalLine_Right() {
        return VerticalLine_Right;
    }

    public void setVerticalLine_Right(boolean VerticalLine_Right, int color) {
        this.VerticalLine_Right = VerticalLine_Right;
        this.VerticalLine_Right_Color = color;
    }

    public int getVerticalLine_RightColor() {
        return this.VerticalLine_Right_Color;
    }

    public boolean isCustomGradientDrawable() {
        return customGradientDrawable;
    }

    public void setCustomGradientDrawable(boolean customGradientDrawable, CustomGradientDrawable customGradientDrawableMark) {
        this.customGradientDrawable = customGradientDrawable;
        this.customGradientDrawableMark = customGradientDrawableMark;
    }

    public CustomGradientDrawable getCustomGradientDrawableMark() {
        return this.customGradientDrawableMark;
    }

    public boolean canBeDeleted() {
        return !today && !selected && !smallOval_Bottom && !VerticalLine_Right && !customGradientDrawable;
    }
}
