package org.sourcebrew.ucssview.timegraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.models.SectionModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 1/14/2018.
 */

public class EventGraph extends FrameLayout implements View.OnClickListener {

    public final static int MONDAY = 0;
    public final static int TUESDAY = 1;
    public final static int WENDESDAY  = 2;
    public final static int THURSDAY = 3;
    public final static int FRIDAY = 4;
    public final static int SATURDAY = 5;
    public final static int SUNDAY = 6;

    private List<TextView> hourLabels = new ArrayList<>();
    private final DayEvent[] dayEvents = {null, null, null, null, null, null, null};
    private int mDaysVisibleCount = 7;
    private float _1pix = 1, _2pix = 2, _4pix = 4, _6pix = 6, _8pix = 8;

    private float _default_hour_width = 60;

    private float mCalculatedHourWidth;
    private int mTargetCount = 0;
    private int mMaxHour = 0;
    private int mFirstVisibleHour = 8;

    private ImageView btnNext, btnPrev;

    private int mButtonBarWidth;

    private LinearLayout buttonBar;
    private LinearLayout scaleBar;

    private LinearLayout mScrollContainer;
    private ScrollView mScrollView;

    public EventGraph(Context context) {
        super(context);
        init();

    }

    public EventGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        _1pix*=getContext().getResources().getDisplayMetrics().density;
        _2pix*=_1pix;
        _4pix*=_1pix;
        _8pix*=_1pix;
        _6pix*=_1pix;
        _default_hour_width *= _1pix;

        btnNext = new ImageButton(getContext());
            btnNext.setImageResource(R.drawable.ic_chevron_right);
            btnNext.setColorFilter(getResources().getColor(R.color.colorHourChangeButtonFill, null));

        btnPrev = new ImageButton(getContext());
            btnPrev.setImageResource(R.drawable.ic_chevron_left);
            btnPrev.setColorFilter(getResources().getColor(R.color.colorHourChangeButtonFill, null));

        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        buttonBar = new LinearLayout(getContext());
            buttonBar.setOrientation(LinearLayout.HORIZONTAL);
            buttonBar.addView(btnPrev);
            buttonBar.addView(btnNext);
            buttonBar.setBackgroundColor(getResources().getColor(R.color.colorHourChangeButtonBar, null));
            buttonBar.setPadding(0, 0, (int)_2pix, 0);

        setWeight(btnPrev, 0, -2, 1, _4pix, _2pix, _4pix, _2pix);
        setWeight(btnNext, 0, -2, 1, _4pix, _2pix, _4pix, _2pix);

        scaleBar = new LinearLayout(getContext());
            scaleBar.setOrientation(LinearLayout.HORIZONTAL);
            scaleBar.addView(buttonBar);


        for(int i = 0; i < 24; i++) {
            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setText(String.format("%02d:00", i));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            hourLabels.add(tv);
            scaleBar.addView(tv);
            setLayoutParams(tv, (int)_default_hour_width , -1, 0, 0, 0, 0);//_6pix, _2pix, _6pix, _2pix);
            tv.setTextColor(getResources().getColor(R.color.colorHourLabelTextColor, null));
            int c = (i < 8 || i > 21)?
                getResources().getColor(R.color.colorAfterHoursLabel, null):
                getResources().getColor(R.color.colorHourLabel, null);

            stylize(tv, 0, c,getResources().getColor(R.color.colorHourLabelBorderColor, null) );
            tv.setOnClickListener(this);
        }

        addView(scaleBar);
        scaleBar.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

        mScrollContainer = new LinearLayout(getContext());
            mScrollContainer.setOrientation(LinearLayout.VERTICAL);
            mScrollContainer.setBackgroundColor(0xFF8090A0);

        mScrollView = new ScrollView(getContext());
            //mScrollView.setFillViewport(true);
            mScrollView.addView(mScrollContainer);

        addView(mScrollView);

        mScrollView.getLayoutParams().width = -1;

        for(int i = 0; i < 7; i++) {
            dayEvents[i] = new DayEvent(getContext(), i);
            mScrollContainer.addView(dayEvents[i]);
            dayEvents[i].getLayoutParams().width = -1;
            dayEvents[i].getLayoutParams().height = -2;
            ((LinearLayout.LayoutParams)dayEvents[i].getLayoutParams()).weight=0;
        }

        setDaysVisible(FRIDAY, false);
        setDaysVisible(SATURDAY, false);
        setDaysVisible(SUNDAY, false);

    }

    private void setWeight(View v, int w, int h, int weight, float pl, float pt, float pr, float pb) {
        if (v.getLayoutParams() instanceof  LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams)(v.getLayoutParams())).weight = weight;
        }
        setLayoutParams(v, w, h, pl, pt, pr, pb);
    }

    private void setLayoutParams(View v, int w, int h, float pl, float pt, float pr, float pb) {
        v.getLayoutParams().width = w;
        v.getLayoutParams().height = h;
        v.setPadding((int)pl, (int)pt, (int)pr, (int)pb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hdWidth = (w - (int)(_2pix));
        int hdHeight = ((int)(_2pix));

        scaleBar.getLayoutParams().width = hdWidth;
        measureChild(scaleBar, widthMeasureSpec, heightMeasureSpec);
        hdHeight+=scaleBar.getMeasuredHeight();
        mButtonBarWidth = buttonBar.getMeasuredWidth();

        scaleBar.setX(_1pix);
        scaleBar.setY(_1pix);

        mCalculatedHourWidth(hdWidth - mButtonBarWidth);
        mScrollView.setX(_1pix);
        mScrollView.setY(hdHeight);
        h = (h - hdHeight - (int)_1pix);
        mScrollView.getLayoutParams().height = h;

        for(DayEvent e: dayEvents) {
            e.update(mFirstVisibleHour, mButtonBarWidth, (int)(h/mDaysVisibleCount), mCalculatedHourWidth);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float mCalculatedHourWidth(float measuredWidth) {
        mTargetCount = (int)(measuredWidth/_default_hour_width);
        mCalculatedHourWidth = measuredWidth / mTargetCount;
        mMaxHour = hourLabels.size()-mTargetCount;
        for(TextView tv: hourLabels) {
            tv.getLayoutParams().width = (int)mCalculatedHourWidth;
        }
        setHoursGone();
        return mCalculatedHourWidth;
    }

    private void setHoursGone() {

        int f = mFirstVisibleHour;

        if (f + mTargetCount >= hourLabels.size())
            f = hourLabels.size() - mTargetCount;

        int l = f + mTargetCount;
        for(int i = 0; i < hourLabels.size(); i++) {
            TextView t = hourLabels.get(i);
            if (i >= f && i <= l)
                t.setVisibility(View.VISIBLE);
            else
                t.setVisibility(View.GONE);

        }
    }

    private void stylize(View v, int radius, int c, int bc) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(c);
        if (radius != 0)
            border.setCornerRadius((_1pix * radius));

        border.setStroke(1, bc);
        v.setBackground(border);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext){
            goNextHour();
        } else if (v == btnPrev){
            goPrevHour();
        } else {
            int i = hourLabels.indexOf(v);
            if (i != -1) setHour(i);
        }
    }

    public void goNextHour() {
        setHour(mFirstVisibleHour+1);
    }

    public void goPrevHour() {
        setHour(mFirstVisibleHour-1);
    }

    public void setHour(int hour) {
        int v = hour;
        if (v < 0) v = 0;
        if (v > mMaxHour) v = mMaxHour;
        if (v != mFirstVisibleHour) {
            mFirstVisibleHour = v;
            setHoursGone();
        }
    }

    void setDaysVisible(int d, boolean visible) {
        d = d % 7;
        dayEvents[d].setVisibility((visible?View.VISIBLE:View.GONE));
        mDaysVisibleCount = 0;
        for(DayEvent e: dayEvents) {
            if (e.getVisibility() != View.GONE)
                mDaysVisibleCount++;
        }
    }

    public void clear() {
        for(DayEvent de: dayEvents) {
            de.clearEventItems();
        }
    }

    public void addSectionModel(SectionModel sm) {
        Iterator it = sm.getMeetings().entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            SectionModel.SectionMeetingsModel meet = (SectionModel.SectionMeetingsModel)me.getValue();
            String days = meet.getDays();
            for(int i = 0; i < days.length(); i++) {
                char c = days.charAt(i);
                int dayIndex = 0;
                switch (c) {
                    case 'M': dayIndex = 0; break;
                    case 'T': dayIndex = 1; break;
                    case 'W': dayIndex = 2; break;
                    case 'R': dayIndex = 3; break;
                    case 'F': dayIndex = 4; break;
                    default: dayIndex = 5; break;
                };

                EventItem eventItem = new EventItem(
                    meet.toString(),
                    meet.toMinutesStartTime(),
                    meet.toMinutesDuration(),
                    sm.getCourseModel().getModelIndex()
                );
                eventItem.setSubTitle(meet.getInstructor());
                dayEvents[dayIndex].addEventItem(eventItem);
            }
        }
       // mScrollContainer.setVisibility(GONE);
       // mScrollContainer.setVisibility(VISIBLE);
        invalidate();
    }

}
