package com.arashafsharpour.framework.ui.calendar;

import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.arashafsharpour.framework.BaseApplication;
import com.arashafsharpour.framework.activity.ViewManager;
import com.arashafsharpourframework.params.LinearParams;
import com.arashafsharpour.framework.ui.text.AppText;
import com.arashafsharpour.framework.util.PersianCalendar;

class AppCalendarWeek extends LinearLayout {

    private AppCalendar calendar;
    private ViewManager context;

    public AppCalendarWeek(ViewManager context, final AppCalendar calendar) {
        super(context);
        this.calendar = calendar;
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(LinearParams.get(-1, context.toolbar_size
                , new int[]{context.medium, 0, context.medium, 0}));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setWeeks();
                calendar.setList();
                calendar.isInitiated = true;
            }
        });
    }

    public void setWeeks() {
        removeAllViews();
        for (int i = 0; i < 7; i++) {
            calendar.size = (int) (1f * getWidth() / 7f);
            if (calendar.language == BaseApplication.Language.EN) {
                addView(day(i, calendar.size));
            } else {
                addView(day(6 - i, calendar.size));
            }
        }
    }

    private AppText day(int i, int size) {
        AppText tv = new AppText(context);
        tv.setTextSize(1, 13);
        tv.setSingleLine();
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(LinearParams.get(size, -1));
        tv.setTextColor(Color.GRAY);
        if (calendar.language == BaseApplication.Language.EN) {
            tv.setText(PersianCalendar.getEnDayChar(i));
        } else {
            tv.setText(PersianCalendar.getDayChar(i));
        }
        return tv;
    }
}
