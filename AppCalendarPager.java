package com.arashafsharpour.framework.ui.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;

import com.arashafsharpour.R;
import com.arashafsharpour.framework.BaseApplication;
import com.arashafsharpour.framework.BaseView;
import com.arashafsharpour.framework.activity.ViewManager;
import com.arashafsharpour.framework.params.RelativeParams;
import com.arashafsharpour.framework.ui.AppButton;
import com.arashafsharpour.framework.ui.text.AppText;
import com.arashafsharpour.framework.util.PersianCalendar;

class AppCalendarPager extends BaseView implements ViewPager.OnPageChangeListener {

    private static final int MAX = 6;
    private AppButton right, left;
    private AppCalendar calendar;
    private ViewPager pager;

    public AppCalendarPager(ViewManager context, AppCalendar calendar) {
        super(context);
        this.calendar = calendar;
        setLayoutParams(new LinearLayout.LayoutParams(-1, toolbar_size));
        addView(pager());
        addView(btn(ALIGN_PARENT_RIGHT));
        addView(btn(ALIGN_PARENT_LEFT));
        setBackgroundResource(R.color.colorPrimary);
        if (isMaterial) {
            setElevation(6);
        }
    }

    private View pager() {
        pager = new ViewPager(context);
        pager.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        pager.setPageTransformer(true, new ZoomOutTranformer());
        pager.addOnPageChangeListener(this);
        pager.setAdapter(adaptor);
        return pager;
    }

    private View btn(final int rule) {
        AppButton button = new AppButton(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(5);
        }
        if (rule == ALIGN_PARENT_RIGHT) {
            right = button;
            button.setBackgroundResource(R.drawable.ic_chevron_right);
        } else {
            left = button;
            button.setBackgroundResource(R.drawable.ic_chevron_left);
            left.setVisibility(GONE);
        }
        button.setLayoutParams(RelativeParams.get(toolbar_size, toolbar_size, rule, CENTER_VERTICAL));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rule == ALIGN_PARENT_RIGHT) {
                    pager.setCurrentItem(calendar.current + 1);
                } else {
                    pager.setCurrentItem(calendar.current - 1);
                }
            }
        });
        return button;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if (calendar.isProcessing) {
            pager.setCurrentItem(calendar.current);
            return;
        }
        if (calendar.current == position) {
            return;
        }
        calendar.current = position;
        if (position == 0) {
            right.setVisibility(VISIBLE);
            left.setVisibility(GONE);
        } else if (position == MAX - 1) {
            right.setVisibility(GONE);
            left.setVisibility(VISIBLE);
        } else {
            right.setVisibility(VISIBLE);
            left.setVisibility(VISIBLE);
        }
        calendar.setList();
    }

    private PagerAdapter adaptor = new PagerAdapter() {
        @Override
        public int getCount() {
            return MAX;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View item = createItem(position);
            container.addView(item, 0);
            return item;
        }
    };

    private View createItem(int position) {
        AppText text = new AppText(context);
        text.setMaxLines(2);
        text.setGravity(Gravity.CENTER);
        text.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        text.setTextColor(Color.WHITE);
        text.setText(getValue(position));
        if (calendar.language == BaseApplication.Language.FA) {
            text.setTypeface(context.getTypeface());
        } else {
            text.setTypeface(Typeface.DEFAULT);
        }
        return text;
    }

    private SpannableString getValue(int position) {
        int[] dates = PersianCalendar.getDates(position, calendar.language);
        String var;
        if (calendar.language == BaseApplication.Language.EN) {
            var = PersianCalendar.getMonthStringEn(dates[1]) + "\n" + dates[0];
        } else {
            var = PersianCalendar.getMonthString(dates[1]) + "\n" + dates[0];
        }
        SpannableString span = new SpannableString(var);
        span.setSpan(new AbsoluteSizeSpan(20, true), 0, var.indexOf("\n"), 0);
        span.setSpan(new AbsoluteSizeSpan(14, true), var.indexOf("\n"), var.length(), 0);
        return span;
    }

    public void regenerate(){
        left.setVisibility(GONE);
        right.setVisibility(VISIBLE);
        pager.setAdapter(adaptor);
    }
}
