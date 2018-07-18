package com.arashafsharpour.framework.ui.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.arashafsharpour.R;
import com.arashafsharpour.framework.BaseApplication;
import com.arashafsharpour.framework.BaseView;
import com.arashafsharpour.framework.activity.ViewManager;
import com.arashafsharpour.framework.model.Date;
import com.arashafsharpour.framework.params.LinearParams;
import com.arashafsharpour.framework.params.RelativeParams;
import com.arashafsharpour.framework.ui.text.AppText;
import com.arashafsharpour.framework.util.PersianCalendar;
import com.arashafsharpour.model.Result;

class AppCalendarList extends BaseView {

    private static final int DOT = +5484728;
    private static final int PRICE = +8757;
    private static final int TV = +5578;

    private AppCalendar calendar;
    private ViewManager context;

    private ProgressBar progressBar;
    private LinearLayout layout;
    private int[] dateFrame;

    private View selectedView;

    private boolean isCurrentMonth;
    private int currentDay;
    private int monthDays;
    private int firstDay;

    private enum SelectionType {
        PRESELECTED, SELECTED, NOT
    }

    public AppCalendarList(ViewManager context, AppCalendar calendar) {
        super(context);
        this.calendar = calendar;
        this.context = context;
        setLayoutParams(LinearParams.get(-1, 0, 1f, new int[]{medium, 0, medium, 0}));
        addView(box());
        addView(progress());
    }

    private View box() {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(RelativeParams.get(-1, -2));
        scrollView.setVerticalScrollBarEnabled(false);

        layout = new LinearLayout(context);
        layout.setLayoutParams(new ScrollView.LayoutParams(-1, -2));
        layout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(layout);
        return scrollView;
    }

    private View progress() {
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(RelativeParams.get(-2, -2, CENTER_IN_PARENT));
        return progressBar;
    }

    public void reset() {
        dateFrame = PersianCalendar.getDates(calendar.current, calendar.language);
        if (layout.getChildCount() != 0) {
            layout.removeAllViews();
        }
        progressBar.setVisibility(VISIBLE);
        firstDay = PersianCalendar.getFirstDay(dateFrame, calendar.language);
        monthDays = PersianCalendar.getMonthDays(dateFrame, calendar.language);
        isCurrentMonth = (calendar.current == 0);
        currentDay = dateFrame[2];
    }

    private View[] createViews() {
        View[] views = new View[6];
        for (int i = 0; i < 6; i++) {
            views[i] = createRow(i);
        }
        return views;
    }

    private View createRow(int rowNum) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        for (int i = 0; i < 7; i++) {
            if (calendar.language == BaseApplication.Language.EN) {
                layout.addView(item(rowNum, i));
            } else {
                layout.addView(item(rowNum, 6 - i));
            }
        }
        return layout;
    }

    private View item(int rowNum, int i) {
        final int value = getDayValue(rowNum, i);
        boolean[] flags = getFlags(value);
        boolean isCurrentDay = flags[0];
        boolean isDeprecated = flags[1];
        Object[] status = getStatus(value, isDeprecated);
        isDeprecated = (boolean) status[0];
        SelectionType type = (SelectionType) status[1];
        if (value == -2) {
            Space space = new Space(context);
            space.setLayoutParams(LinearParams.get(calendar.size, calendar.size, Gravity.CENTER_VERTICAL));
            return space;
        } else {
            double minimumCost = -1;
            final boolean isMultiDetailed = calendar.withRequest && isCurrentMonth
                    && (minimumCost = hasMinimumCost(value)) != -1;
            final RelativeLayout layout = new RelativeLayout(context);
            layout.addView(text(value, isDeprecated, type));
            if (isMultiDetailed) {
                layout.setLayoutParams(LinearParams.get(calendar.size, (calendar.size * 2)));
                layout.addView(price(minimumCost));
            } else {
                layout.setLayoutParams(LinearParams.get(calendar.size, calendar.size));
            }
            layout.addView(dot(isCurrentDay));
            if (isDeprecated && isMultiDetailed) {
                layout.setBackgroundResource(R.drawable.empty_selected_rectangle);
            } else if (!isDeprecated) {
                switch (type) {
                    case NOT:
                        clear(layout, false);
                        break;
                    case PRESELECTED:
                        preSelected(layout);
                        break;
                    case SELECTED:
                        fill(layout, value, false);
                        break;
                }
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fill(layout, value, true);
                    }
                });
            }
            return layout;
        }
    }


    private double hasMinimumCost(int value) {
        for (Date date : calendar.costMap.keySet()) {
            if (date != null) {
                if (calendar.language == BaseApplication.Language.FA && date.getPersian()[2] == value) {
                    return calendar.costMap.get(date);
                } else if (calendar.language == BaseApplication.Language.EN && date.getGeorgian()[2] == value) {
                    return calendar.costMap.get(date);
                }
            }
        }
        return -1;
    }

    private Object[] getStatus(int value, boolean isDeprecated) {
        SelectionType type = SelectionType.NOT;
        if (value != -2 && !isDeprecated && calendar.selectedBefore.size() > 0) {
            for (AppCalendar.CalendarSelectType calendarSelectType : calendar.selectedBefore.keySet()) {
                Date selectedDate = calendar.selectedBefore.get(calendarSelectType);

                boolean isLower;
                boolean isSame;
                int[] subject = new int[]{dateFrame[0], dateFrame[1], value};

                if (calendar.language == BaseApplication.Language.FA) {
                    isSame = PersianCalendar.isSame(subject, selectedDate.getPersian());
                    isLower = PersianCalendar.isLower(subject, selectedDate.getPersian());
                } else {
                    subject = new int[]{dateFrame[0], dateFrame[1], value};
                    isSame = PersianCalendar.isSame(subject, selectedDate.getGeorgian());
                    isLower = PersianCalendar.isLower(subject, selectedDate.getGeorgian());
                }

                if (calendarSelectType == AppCalendar.CalendarSelectType.BEGIN
                        && calendar.item == AppCalendar.CalendarSelectType.END) {
                    if (isSame) {
                        type = SelectionType.PRESELECTED;
                    } else if (isLower) {
                        isDeprecated = true;
                    }
                } else if (calendarSelectType == AppCalendar.CalendarSelectType.END
                        && calendar.item == AppCalendar.CalendarSelectType.BEGIN) {
                    if (isSame) {
                        type = SelectionType.PRESELECTED;
                    } else if (!isLower) {
                        isDeprecated = true;
                    }
                } else if (calendarSelectType == AppCalendar.CalendarSelectType.BEGIN
                        && calendar.item == AppCalendar.CalendarSelectType.BEGIN) {
                    if (isSame) {
                        type = SelectionType.SELECTED;
                    }
                } else if (calendarSelectType == AppCalendar.CalendarSelectType.END
                        && calendar.item == AppCalendar.CalendarSelectType.END) {
                    if (isSame) {
                        type = SelectionType.SELECTED;
                    }
                }
            }
        }
        return new Object[]{isDeprecated, type};
    }

    private boolean[] getFlags(int value) {
        boolean isCurrentDay = false, isBefore = false;
        if (isCurrentMonth) {
            if (currentDay == value) {
                isCurrentDay = true;
            } else if (value < currentDay) {
                isBefore = true;
            }
        }
        return new boolean[]{isCurrentDay, isBefore};
    }

    private int getDayValue(int rowNum, int i) {
        int index;
        if (calendar.language == BaseApplication.Language.EN) {
            index = i + 1 + (rowNum * 6) + rowNum;
        } else {
            index = i + 1 + (rowNum * 6) + rowNum;
        }
        int value = -2;
        if (index >= firstDay) {
            value = index - firstDay + 1;
        }
        if (value > monthDays) {
            value = -2;
        }
        return value;
    }

    private void fill(View view, int value, boolean onClick) {
        if (calendar.selected == null) {
            calendar.selected = new Date();
        }
        if (onClick) {
            if (selectedView != null) {
                if (selectedView.getTag() != null) {
                    switch ((SelectionType) selectedView.getTag()) {
                        case PRESELECTED:
                            preSelected(selectedView);
                            break;
                        default:
                            clear(selectedView, true);
                            break;
                    }
                } else {
                    clear(selectedView, true);
                }
            }
        } else {
            view.setTag(SelectionType.SELECTED);
        }
        View tag = view.findViewById(DOT);
        AppText text = view.findViewById(TV);
        text.setTextColor(Color.WHITE);

        AppText price = view.findViewById(PRICE);
        if (price != null) {
            price.setTextColor(color(R.color.colorAccent));
            view.setBackgroundResource(R.drawable.selected_rectangle);
        } else {
            view.setBackgroundResource(R.drawable.selected_circle);
        }
        if (tag.getTag() != null) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(getResources().getColor(R.color.white));
            tag.setBackgroundDrawable(drawable);
        }
        if (calendar.language == BaseApplication.Language.FA) {
            calendar.selected.setPersian(new int[]{dateFrame[0], dateFrame[1], value});
            calendar.selected.setGeorgian(PersianCalendar.toGeorgian(calendar.selected.getPersian()));
        } else {
            calendar.selected.setGeorgian(new int[]{dateFrame[0], dateFrame[1], value});
            calendar.selected.setPersian(PersianCalendar.toPersian(calendar.selected.getGeorgian()));
        }
        selectedView = (view);
    }

    private void clear(View view, boolean onClick) {
        if (view == null) {
            return;
        }
        if (!onClick) {
            view.setTag(SelectionType.NOT);
        }
        View tag = view.findViewById(DOT);
        AppText text = view.findViewById(TV);
        text.setTextColor(Color.DKGRAY);

        AppText price = view.findViewById(PRICE);
        if (price != null) {
            price.setTextColor(color(R.color.colorPrimary));
            view.setBackgroundResource(R.drawable.empty_selected_rectangle);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(context.getWideRipple());
            } else {
                view.setBackgroundResource(context.getBackground());
            }
        }
        if (tag.getTag() != null) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(Color.DKGRAY);
            tag.setBackgroundDrawable(drawable);
        }
    }

    private void preSelected(View view) {
        view.setTag(SelectionType.PRESELECTED);
        View tag = view.findViewById(DOT);
        AppText text = view.findViewById(TV);
        text.setTextColor(Color.DKGRAY);

        AppText price = view.findViewById(PRICE);
        if (price != null) {
            price.setTextColor(color(R.color.colorPrimary));
            view.setBackgroundResource(R.drawable.pre_selected_rectangle);
        } else {
            view.setBackgroundResource(R.drawable.pre_selected_circle);
        }
        if (tag.getTag() != null) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(Color.DKGRAY);
            tag.setBackgroundDrawable(drawable);
        }
    }

    private View dot(boolean isCurrentDay) {
        int sizeM = (int) (1f * calendar.size / 7f);
        View dot = new View(context);
        dot.setId(DOT);
        dot.setLayoutParams(RelativeParams.get(sizeM, sizeM, new int[]{0, calendar.size - context.medium, 0, 0}
                , RelativeLayout.CENTER_HORIZONTAL));
        if (isCurrentDay) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(Color.DKGRAY);
            dot.setBackgroundDrawable(drawable);
            dot.setTag(dot.toString());
        } else {
            dot.setBackgroundResource(R.color.transparent);
        }
        return dot;
    }

    private View price(double value) {
        AppText text = new AppText(context);
        text.setId(PRICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            text.setElevation(2);
        }
        text.setLayoutParams(RelativeParams.get(-1, calendar.size, BELOW, TV));
        text.setMaxLines(2);
        text.setTextColor(Color.BLUE);
        text.setTextSize(1, 12);
        text.setLineSpacing(0, 0.7f);
        text.setGravity(Gravity.CENTER);
        text.setText(Result.format(value / 1000));
        if (calendar.language == BaseApplication.Language.FA) {
            text.append("\nت");
        } else {
            text.append("\nt");
        }
        return text;
    }

    private View text(int value, boolean isDeprecated, SelectionType type) {
        AppText text = new AppText(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            text.setElevation(2);
        }
        text.setId(TV);
        text.setLayoutParams(RelativeParams.get(-1, calendar.size));
        text.setSingleLine();
        if (type == SelectionType.SELECTED) {
            text.setTextColor(Color.WHITE);
        } else {
            text.setTextColor(Color.GRAY);
        }
        text.setTextSize(1, 16);
        text.setGravity(Gravity.CENTER);
        if (isDeprecated) {
            text.setAlpha(0.5f);
        }
        if (calendar.language == BaseApplication.Language.FA) {
            text.setTypeface(context.getTypeface());
        } else {
            text.setTypeface(Typeface.DEFAULT);
        }
        text.setText(String.valueOf(value));
        return text;
    }

    private void fetch(View[] views) {
        for (View view : views) {
            layout.addView(view);
        }
        progressBar.setVisibility(GONE);
        YoYo.with(Techniques.FadeInUp).duration(150).playOn(layout);
    }

    public void setView() {
        YoYo.with(Techniques.FadeIn).duration(100).playOn(progressBar);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj != null) {
                    View[] views = (View[]) msg.obj;
                    fetch(views);
                    calendar.isProcessing = false;
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.obj = createViews();
                handler.sendMessage(message);
            }
        }.start();
    }
}
