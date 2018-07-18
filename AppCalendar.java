package com.arashafsharpour.framework.ui.calendar;

import android.animation.Animator;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;

import java.util.HashMap;

import com.arashafsharpour.R;
import com.arashafsharpour.framework.BaseActivity;
import com.arashafsharpour.framework.BaseApplication;
import com.arashafsharpour.framework.BaseView;
import com.arashafsharpour.framework.activity.ViewManager;
import com.arashafsharpour.framework.oracle.ResultInterface;
import com.arashafsharpour.framework.params.FrameParams;
import com.arashafsharpour.framework.params.RelativeParams;
import com.arashafsharpour.framework.ui.dialog.DateCallBack;
import com.arashafsharpour.framework.util.PersianCalendar;
import com.arashafsharpour.framework.model.Date;
import com.arashafsharpour.model.MinimumCost;

public class AppCalendar extends BaseView {

    public HashMap<CalendarSelectType, Date> selectedBefore = new HashMap<>();
    public HashMap<Date, Double> costMap = new HashMap<>();
    private String latestUpdate;

    public DateCallBack callBack;
    public Date selected;

    private final CalenderViewType type;
    private boolean show;

    public int current;
    public int size;

    public BaseApplication.Language language = BaseApplication.getLanguage();
    public boolean isProcessing;

    public int width;

    public CalendarSelectType item;
    private String destination;
    private String origin;

    public boolean isInitiated;
    public boolean withRequest;

    public AppCalendarWeek weekBox;
    public AppCalendarPager pager;
    private AppCalendarList list;

    public enum CalenderViewType {
        FULL, DIALOG
    }

    public enum CalendarSelectType {

        BEGIN("رفت"), END("برگشت");
        private String name;

        CalendarSelectType(String nam) {
            this.name = nam;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public boolean isShowing() {
        return show;
    }

    public void dismiss() {
        if (!isShowing() || type != CalenderViewType.DIALOG) {
            return;
        }
        this.show = false;
        YoYo.with(Techniques.FadeOut).duration(DateCallBack.ANIM_TIME).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                context.removeViewOverSlider(AppCalendar.this);
            }
        }).playOn(this);
    }

    public void show(DateCallBack callBack) {
        show(callBack, null, null);
    }

    public void show(DateCallBack callBack, CalendarSelectType item
            , HashMap<CalendarSelectType, Date> selected) {
        show(callBack, item, selected, false, null, null);
    }

    public void show(DateCallBack callBack, CalendarSelectType item
            , HashMap<CalendarSelectType, Date> selected, boolean withRequest
            , String destination, String origin) {
        if (isShowing() || type != CalenderViewType.DIALOG) {
            return;
        }
        this.show = true;
        this.item = item;
        this.origin = origin;
        this.callBack = callBack;
        this.destination = destination;
        this.withRequest = withRequest;
        this.selectedBefore = new HashMap<>();
        for (CalendarSelectType type : selected.keySet()) {
            if (type != null && selected.get(type) != null) {
                this.selectedBefore.put(type, selected.get(type));
            }
        }
        this.context.addViewOverSlider(this, new ViewGroup.LayoutParams(-1, -1));
        if (this.isInitiated) {
            setList();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        YoYo.with(Techniques.FadeIn).duration(DateCallBack.ANIM_TIME).playOn(this);
    }

    public AppCalendar(ViewManager context, CalenderViewType type) {
        super(context);
        this.type = type;
        if (context.isMaterial) {
            setElevation(100);
        }
        setClickable(true);
        setBackgroundResource(R.color.fade);
        addView(template());
    }

    private View template() {
        CardView cardView = new CardView(context);
        if (type == CalenderViewType.DIALOG) {
            width = (int) (dimen[0] - toPx(60));
            if (width > toPx(400)) {
                width = toPx(400);
            }
            int h = (int) (dimen[1] - toPx(100));
            if (h > width * 2) {
                h = width * 2;
            }
            cardView.setLayoutParams(RelativeParams.get(width, h, CENTER_IN_PARENT));
        } else {
            cardView.setLayoutParams(RelativeParams.get(-1, -1, CENTER_IN_PARENT));
        }
        cardView.setRadius(0);
        cardView.setCardElevation(toPx(4));
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(FrameParams.get(-1, -1));
        layout.addView(marker());
        layout.addView(week());
        layout.addView(list());
        layout.addView(functions());

        cardView.addView(layout);
        return cardView;
    }

    private View marker() {
        pager = new AppCalendarPager(context, this);
        return pager;
    }

    private View week() {
        weekBox = new AppCalendarWeek(context, this);
        return weekBox;
    }

    private View functions() {
        return new AppCalendarFunctions(context, this);
    }

    private View list() {
        list = new AppCalendarList(context, this);
        return list;
    }

    public void setList() {
        isProcessing = true;
        list.reset();
        if (withRequest) {
            sendRequest();
            return;
        }
        list.setView();
    }

    private void sendRequest() {
        Date date = selectedBefore.get(item);
        final String dateStamp = PersianCalendar.parse(date);
        if (dateStamp != null && latestUpdate != null
                && latestUpdate.equals(dateStamp)) {
            list.setView();
            return;
        }
        costMap.clear();
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).oracle().minimumCost(new ResultInterface() {
                @Override
                public void onBefore() {
                    isProcessing = true;
                }

                @Override
                public void onResult(String response) {
                    latestUpdate = dateStamp;
                    MinimumCost cost = new Gson().fromJson(response, MinimumCost.class);
                    if (cost != null && cost.getFlights() != null) {
                        for (MinimumCost.SubMinimumCost sub : cost.getFlights()) {
                            costMap.put(PersianCalendar.parse(sub.getFlight_date()),
                                    sub.getFlight_min_cost());
                        }
                    }
                    list.setView();
                }

                @Override
                public void onError(String response) {
                    list.setView();
                }

                @Override
                public void onConnection() {
                    isProcessing = false;
                    context.showConnectionSnack(this);
                }

                @Override
                public void onRetry() {
                    sendRequest();
                }
            }, dateStamp, destination, origin);
        } else {
            list.setView();
        }
    }
}
