package com.arashafsharpour.framework.ui.calendar;

import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;

import com.arashafsharpour.R;
import com.arashafsharpour.framework.BaseApplication;
import com.arashafsharpour.framework.activity.ViewManager;
import com.arashafsharpour.framework.params.LinearParams;

class AppCalendarFunctions extends LinearLayout {

    private AppCalendar calendar;
    private ViewManager context;
    private Button languageFunction;

    public AppCalendarFunctions(ViewManager context, AppCalendar calendar) {
        super(context);
        this.calendar = calendar;
        this.context = context;
        setLayoutParams(LinearParams.get(-1, context.toolbar_size
                , new int[]{context.medium, 0, context.medium, 0}));
        int size = calendar.width - (context.medium * 2);
        size = (int) (1f * size / 3f) - context.line;
        addView(function(0, size));
        addView(line());
        addView(function(1, size));
        addView(line());
        addView(function(2, size));
    }


    private View line() {
        Space view = new Space(context);
        view.setLayoutParams(LinearParams.get(0, -1, 1f));
        return view;
    }

    private View function(final int rule, int size) {
        final AppCompatButton button = new AppCompatButton(context);
        button.setTextColor(Color.WHITE);
        button.setTextSize(1, 13);
        button.setTypeface(context.getTypeface());
        button.setSupportBackgroundTintList
                (context.getColorState(context.color(R.color.colorAccent), context.color(R.color.colorPrimary)));
        button.setLayoutParams(LinearParams.get(size, -1, Gravity.CENTER_VERTICAL));
        if (rule == 2) {
            button.setText("انتخاب");
        } else if (rule == 0) {
            button.setText("بازگشت");
        } else {
            languageFunction = button;
            button.setLineSpacing(0, 0.8f);
            if (calendar.language == BaseApplication.Language.EN) {
                button.setText("تقویم\nشمسی");
            } else {
                button.setText("تقویم\nمیلادی");
            }
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rule == 2) {
                    submit();
                } else if (rule == 0) {
                    calendar.dismiss();
                } else {
                    changeLanguage();
                }
            }
        });
        return button;
    }

    private void submit() {
        if (calendar.callBack != null) {
            if (calendar.selected != null) {
                calendar.callBack.onResult(calendar.selected.getPersian()
                        , calendar.selected.getGeorgian(), calendar.language);
                calendar.dismiss();
            }
        }
    }

    public void changeLanguage() {
        if (calendar.isProcessing) {
            return;
        }
        if (calendar.language == BaseApplication.Language.EN) {
            calendar.language = BaseApplication.Language.FA;
            languageFunction.setText("تقویم\nمیلادی");
        } else {
            calendar.language = BaseApplication.Language.EN;
            languageFunction.setText("تقویم\nشمسی");
        }
        calendar.pager.regenerate();
        calendar.current = 0;
        calendar.weekBox.setWeeks();
        calendar.setList();
        calendar.selected = null;
    }
}
