package com.arashafsharpour.musicplatform.lib.ui.calendar;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.arashafsharpour.musicplatform.R;
import com.arashafsharpour.musicplatform.lib.BaseApplication;
import com.arashafsharpour.musicplatform.lib.BaseView;
import com.arashafsharpour.musicplatform.lib.activity.ViewManager;
import com.arashafsharpour.musicplatform.lib.model.Date;
import com.arashafsharpour.musicplatform.lib.params.FrameParams;
import com.arashafsharpour.musicplatform.lib.params.LinearParams;
import com.arashafsharpour.musicplatform.lib.params.RelativeParams;
import com.arashafsharpour.musicplatform.lib.ui.text.AppText;
import com.arashafsharpour.musicplatform.lib.util.PersianCalendar;

public class AppDatePicker extends BaseView {

    private static final int YEAR = +2376456;
    private static final int MONTH = +237646;
    private static final int DAY = +23766;

    private HashMap<Integer, AppText> headerMap = new HashMap<>();
    private HashMap<Integer, NumberPicker> pickerMap = new HashMap<>();

    private BaseApplication.Language language = BaseApplication.getLanguage();
    private Button languageFunction;
    private DateCallBack callBack;
    private Date selectedBefore;
    private Date selected;
    private boolean show;
    private int width;

    private AppText text;
    private int item;

    public AppDatePicker(ViewManager context) {
        super(context);
        width = (int) (dimen[0] - toPx(60));
        if (width > toPx(400)) {
            width = toPx(400);
        }
        item = width / 3;
        if (context.isMaterial) {
            setElevation(100);
        }
        setId(+76786000);
        setClickable(true);
        setBackgroundResource(R.color.fade);
        addView(template());
    }

    private View template() {
        CardView cardView = new CardView(context);
        cardView.setLayoutParams(RelativeParams.get(width, -2, CENTER_IN_PARENT));
        cardView.setRadius(0);
        cardView.setCardElevation(context.tiny);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(FrameParams.get(-1, -2));
        layout.addView(title());
        layout.addView(headers());
        layout.addView(list());
        layout.addView(functions());

        cardView.addView(layout);
        return cardView;
    }

    private View list() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(LinearParams.get(-1, -2));
        layout.addView(pickerTv(YEAR));
        layout.addView(pickerTv(MONTH));
        layout.addView(pickerTv(DAY));
        return layout;
    }

    private View pickerTv(final int id) {
        final NumberPicker picker = new NumberPicker(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(item, -2);
        params.gravity = Gravity.CENTER_VERTICAL;
        picker.setLayoutParams(params);
        picker.setOrientation(NumberPicker.VERTICAL);
        picker.setTextColor(Color.DKGRAY);
        picker.setDividerColorResource(R.color.lite);
        picker.setSelectedTextColorResource(R.color.black);
        picker.setDividerThickness(line);
        picker.setGravity(Gravity.CENTER);
        picker.setTextSize((float) toPx(17));
        picker.setSelectedTextSize((float) toPx(20));
        if (language == BaseApplication.Language.FA) {
            picker.setTypeface(context.getTypeface());
        } else {
            picker.setTypeface(Typeface.DEFAULT);
        }
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (language == BaseApplication.Language.FA) {
                    switch (id) {
                        case YEAR:
                            selected.getPersian()[0] = newVal;
                            break;
                        case MONTH:
                            selected.getPersian()[1] = newVal;
                            break;
                        case DAY:
                            selected.getPersian()[2] = newVal;
                            break;
                    }
                } else {
                    switch (id) {
                        case YEAR:
                            selected.getGeorgian()[0] = newVal;
                            break;
                        case MONTH:
                            selected.getGeorgian()[1] = newVal;
                            break;
                        case DAY:
                            selected.getGeorgian()[2] = newVal;
                            break;
                    }
                }
                if (id == MONTH) {
                    setDefaults();
                }
            }
        });
        pickerMap.put(id, picker);
        return picker;
    }

    private String[] createList(NumberPicker picker) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = picker.getMinValue(); i < picker.getMaxValue() + 1; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(i + "");
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private View headers() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(LinearParams.get(-1, -2, new int[]{0, medium, 0, margin}));
        layout.addView(headerTv(YEAR));
        layout.addView(headerTv(MONTH));
        layout.addView(headerTv(DAY));
        return layout;
    }

    private View headerTv(int id) {
        AppText text = new AppText(context);
        text.setId(id);
        text.setLayoutParams(LinearParams.get(item, -2));
        text.setGravity(Gravity.CENTER);
        text.setTextSize(1, 13);
        text.setTextColor(Color.GRAY);
        text.setSingleLine();
        headerMap.put(id, text);
        return text;
    }

    private View title() {
        text = new AppText(context);
        text.setTextColor(Color.WHITE);
        text.setTextSize(1, 14);
        text.setLayoutParams(LinearParams.get(-1, toolbar_size));
        text.setBackgroundResource(R.color.colorPrimary);
        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        if (isMaterial) {
            text.setElevation(6);
        }
        return text;
    }

    private View functions() {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(LinearParams.get(-1, toolbar_size, new int[]{0, margin, 0, 0}));
        int size = width - medium * 2;
        size = (int) (1f * size / 3f) - line;
        layout.addView(function(0, size));
        layout.addView(line());
        layout.addView(function(1, size));
        layout.addView(line());
        layout.addView(function(2, size));
        return layout;
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
        button.setSupportBackgroundTintList(context.getColorState(color(R.color.colorAccent)
                , color(R.color.colorPrimary)));
        button.setLayoutParams(LinearParams.get(size, -1, Gravity.CENTER_VERTICAL));
        if (rule == 2) {
            button.setText("انتخاب");
        } else if (rule == 0) {
            button.setText("بازگشت");
        } else {
            languageFunction = button;
            button.setLineSpacing(0, 0.8f);
            if (language == BaseApplication.Language.EN) {
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
                    dismiss();
                } else {
                    changeLanguage();
                }
            }
        });
        return button;
    }

    public boolean isShowing() {
        return show;
    }

    public void dismiss() {
        if (!isShowing()) {
            return;
        }
        this.show = false;
        YoYo.with(Techniques.FadeOut).duration(DateCallBack.ANIM_TIME).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                context.removeViewOverSlider(AppDatePicker.this);
            }
        }).playOn(this);
    }

    public void show(DateCallBack callBack, String title) {
        show(callBack, null, title);
    }

    public void show(DateCallBack callBack, Date selected, String title) {
        if (isShowing()) {
            return;
        }
        this.show = true;
        this.callBack = callBack;
        this.selectedBefore = selected;
        if (selectedBefore != null) {
            if (selectedBefore.getLanguageOrdinal() == BaseApplication.Language.FA.ordinal()) {
                language = BaseApplication.Language.FA;
            } else {
                language = BaseApplication.Language.EN;
            }
        }
        this.context.addViewOverSlider(this, new ViewGroup.LayoutParams(-1, -1));
        this.text.setText(title);
        this.text.setPadding(big, 0, big, 0);
        setPickers();
    }

    private void changeLanguage() {
        completeSelected();
        if (language == BaseApplication.Language.EN) {
            language = BaseApplication.Language.FA;
            languageFunction.setText("تقویم\nمیلادی");
        } else {
            language = BaseApplication.Language.EN;
            languageFunction.setText("تقویم\nشمسی");
        }
        setPickers();
    }

    private void submit() {
        if (callBack != null) {
            if (selected != null) {
                completeSelected();
                callBack.onResult(selected.getPersian(), selected.getGeorgian(), language);
                dismiss();
            }
        }
    }

    private void completeSelected() {
        if (language == BaseApplication.Language.FA) {
            selected.setGeorgian(PersianCalendar.convertToEn(selected.getPersian()));
        } else {
            selected.setPersian(PersianCalendar.convertToFa(selected.getGeorgian()));
        }
    }

    private void setPickers() {
        setHeaders();
        setDefaults();
        initSelected();
    }

    private void setDefaults() {
        try {
            int[] calendar = getDefault();
            int[] min = getMinimum();
            int[] max = getMaximum(calendar);

            for (int id : pickerMap.keySet()) {
                NumberPicker picker = pickerMap.get(id);
                if (language == BaseApplication.Language.FA) {
                    picker.setTypeface(context.getTypeface());
                } else {
                    picker.setTypeface(Typeface.DEFAULT);
                }
                picker.setDisplayedValues(null);
                switch (id) {
                    case YEAR:
                        picker.setMaxValue(max[0]);
                        picker.setMinValue(min[0]);
                        picker.setValue(calendar[0]);
                        break;
                    case MONTH:
                        picker.setMaxValue(max[1]);
                        picker.setMinValue(min[1]);
                        picker.setValue(calendar[1]);
                        break;
                    case DAY:
                        picker.setMaxValue(max[2]);
                        picker.setMinValue(min[2]);
                        picker.setValue(calendar[2]);
                        break;
                }
                picker.setDisplayedValues(createList(picker));
            }
            selectedBefore = null;
        } catch (Throwable t) {
            t.printStackTrace();
            selectedBefore = null;
            selected = null;
            setDefaults();
        }
    }

    private int[] getMaximum(int[] calendar) {
        int max[] = new int[3];
        max[1] = 12;
        if (language == BaseApplication.Language.FA) {
            PersianCalendar persianCalendar = new PersianCalendar();
            max[0] = persianCalendar.getYear();
            max[2] = PersianCalendar.getMonthDays(calendar, language);
        } else {
            Calendar georgianCalendar = Calendar.getInstance();
            max[0] = georgianCalendar.get(Calendar.YEAR);
            max[2] = PersianCalendar.getMonthDays(calendar, language);
        }
        return max;
    }

    private int[] getMinimum() {
        int min[] = new int[3];
        min[1] = 1;
        min[2] = 1;
        if (language == BaseApplication.Language.FA) {
            min[0] = 1300;
        } else {
            min[0] = 1900;
        }
        return min;
    }

    private int[] getDefault() {
        int calendar[];
        if (language == BaseApplication.Language.FA) {
            if (selectedBefore != null) {
                calendar = selectedBefore.getPersian();
            } else if (selected != null) {
                calendar = selected.getPersian();
                if (calendar[2] > 26) {
                    calendar[2] = 1;
                }
            } else {
                PersianCalendar persianCalendar = new PersianCalendar();
                calendar = new int[]{persianCalendar.getYear(), 1, 1};
            }
            if (selected == null) {
                initSelected();
            }
            selected.setPersian(calendar);
        } else {
            if (selectedBefore != null) {
                calendar = selectedBefore.getGeorgian();
            } else if (selected != null) {
                calendar = selected.getGeorgian();
                if (calendar[2] > 26) {
                    calendar[2] = 1;
                }
            } else {
                Calendar georgianCalendar = Calendar.getInstance();
                calendar = new int[]{georgianCalendar.get(Calendar.YEAR), 1, 1};
            }
            if (selected == null) {
                initSelected();
            }
            selected.setGeorgian(calendar);
        }
        return calendar;
    }

    private void initSelected() {
        if (selected == null) {
            this.selected = new Date();
            this.selected.setGeorgian(new int[3]);
            this.selected.setPersian(new int[3]);
        }
    }

    private void setHeaders() {
        for (int id : headerMap.keySet()) {
            AppText text = headerMap.get(id);
            if (language == BaseApplication.Language.FA) {
                switch (id) {
                    case YEAR:
                        text.setText("سال");
                        break;
                    case MONTH:
                        text.setText("ماه");
                        break;
                    case DAY:
                        text.setText("روز");
                        break;
                }
            } else {
                switch (id) {
                    case YEAR:
                        text.setText("Year");
                        break;
                    case MONTH:
                        text.setText("Month");
                        break;
                    case DAY:
                        text.setText("Day");
                        break;
                }
            }
        }
    }
}
