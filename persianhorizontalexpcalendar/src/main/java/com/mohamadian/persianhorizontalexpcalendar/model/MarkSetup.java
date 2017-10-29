package com.mohamadian.persianhorizontalexpcalendar.model;

import android.graphics.Color;

import com.mohamadian.persianhorizontalexpcalendar.view.cell.CustomGradientDrawable;

/**
 * Created by Tasnim on 10/27/2017.
 */

public class MarkSetup {

    private boolean today;
    private boolean selected;
    private boolean smallOval_Bottom;
    private boolean horizontalLine_Right;
    private boolean customGradientDrawable;

    private int smallOval_Bottom_Color = Color.parseColor("#AAFF3333");
    private int horizontalLine_Right_Color = Color.parseColor("#0095f3");
    private CustomGradientDrawable customGradientDrawableMark = null;

    public MarkSetup() {
        this.today = false;
        this.selected = false;
        this.smallOval_Bottom = false;
        this.horizontalLine_Right = false;
        customGradientDrawable = false;
    }

    public MarkSetup(boolean today, boolean selected) {
        this(today, selected, false, 0);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, int smallOval_Bottom_Color) {
        this(today, selected, smallOval_Bottom, false, smallOval_Bottom_Color, 0);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, boolean horizontalLine_Right, int smallOval_Bottom_Color, int horizontalLine_Right_Color) {
        this(today, selected, smallOval_Bottom, horizontalLine_Right, false, smallOval_Bottom_Color, horizontalLine_Right_Color, null);
    }

    public MarkSetup(boolean today, boolean selected, boolean smallOval_Bottom, boolean horizontalLine_Right,
                     boolean customGradientDrawable, int smallOval_Bottom_Color, int horizontalLine_Right_Color,
                     CustomGradientDrawable customGradientDrawableMark) {
        this.today = today;
        this.selected = selected;
        this.smallOval_Bottom = smallOval_Bottom;
        this.horizontalLine_Right = horizontalLine_Right;
        this.customGradientDrawable = customGradientDrawable;

        this.smallOval_Bottom_Color = smallOval_Bottom_Color;
        this.horizontalLine_Right_Color = horizontalLine_Right_Color;
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

    public boolean isHorizontalLine_Right() {
        return horizontalLine_Right;
    }

    public void setHorizontalLine_Right(boolean horizontalLine_Right, int color) {
        this.horizontalLine_Right = horizontalLine_Right;
        this.horizontalLine_Right_Color = color;
    }

    public int getHorizontalLine_RightColor() {
        return this.horizontalLine_Right_Color;
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
        return !today && !selected && !smallOval_Bottom && !horizontalLine_Right && !customGradientDrawable;
    }
}
