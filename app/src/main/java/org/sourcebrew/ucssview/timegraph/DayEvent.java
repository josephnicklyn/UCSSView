package org.sourcebrew.ucssview.timegraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by John on 1/15/2018.
 */

public class DayEvent extends FrameLayout {

    private static boolean _isSet = false;
    private static float _1pix = 1, _2pix = 2, _3pix = 3, _4pix = 4, _6pix = 6, _8pix = 8;
    private static float _default_hour_width = 60;

    private int mLargestY = 0;

    private void setHelpers() {
        if (!_isSet) {
            _isSet = true;
            _1pix = getContext().getResources().getDisplayMetrics().density;
            _2pix = 2 * _1pix;
            _3pix = 3 * _1pix;
            _4pix = 4 * _1pix;
            _6pix = 6 * _1pix;
            _8pix = 8 * _1pix;
            _default_hour_width = 60 * _1pix;
        }
    }

    private TextView dayLabel;
    private final int day;
    private static final String dayLabels[] = {"MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN"};

    private final ArrayList<EventItem> eventItems = new ArrayList<>();

    public DayEvent(@NonNull Context context, int forDay) {
        super(context);
        day = (forDay % 7);
        init();
    }

    private void init() {

        setHelpers();

        if (day % 2 == 0) {
            setBackgroundColor(getResources().getColor(R.color.colorDayEvent, null));
        } else {
            setBackgroundColor(getResources().getColor(R.color.colorDayEventDark, null));
        }

        dayLabel = new TextView(getContext());
            dayLabel.setText(dayLabels[day]);
            dayLabel.setGravity(Gravity.CENTER);
            dayLabel.setMinHeight((int)(_1pix * 32));
            dayLabel.setPadding((int)_4pix, (int)_1pix, (int)_1pix, (int)_4pix);
            dayLabel.setTextColor(getResources().getColor(R.color.colorDayabelTextColor, null));

        int c = (day < 5)?
                (day%2==0)?getResources().getColor(R.color.colorDayLabel, null):getResources().getColor(R.color.colorDayLabelDark, null):
                getResources().getColor(R.color.colorDayWeekendLabel, null);

        stylize(dayLabel, 0, c,getResources().getColor(R.color.colorDayLabelBorderColor, null) );

        addView(dayLabel);
        dayLabel.getLayoutParams().height = -1;

        mLargestY = getTallest();
    }

    private void stylize(View v, int radius, int c, int bc) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(c);
        if (radius != 0)
            border.setCornerRadius((_1pix * radius));

        border.setStroke(1, bc);
        v.setBackground(border);
    }

    private int targetHeight, buttonBarWidth, firstHour, firstMinute;
    private float hourWidth;

    public float getScale() {
        return (float)(hourWidth/_default_hour_width);
    }

    public void update(int firstHour, int buttonBarWidth, int targetHeight, float hourWidth) {
        this.targetHeight = targetHeight;
        this.buttonBarWidth = buttonBarWidth;
        this.hourWidth = hourWidth;
        this.firstHour = firstHour;
        this.firstMinute=(firstHour*60);
        dayLabel.getLayoutParams().width = buttonBarWidth;
        invalidate();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight() - (int)_1pix;

        canvas.drawLine(0, h, w, h, getGridPaint());
        float xStep = hourWidth/2;
        float x = buttonBarWidth + xStep;
        int i = firstHour;

        Paint p1 = getGridPaint();
        Paint p2 = getGridPaintDark();

        while(x < w) {
            if ( (i++) % 2 == 0)
                canvas.drawLine((int)x, 0, (int)x, h, p1);
            else
                canvas.drawLine((int)x, 0, (int)x, h, p2);

            x+=xStep;
        }
        for(EventItem item: eventItems)
            drawEvent(item, canvas);

    }

    public int getTallest() {
        int largestY = 0;
        for(EventItem i: eventItems) {
            int y = i.getYOffset();
            if (y > largestY)
                largestY = y;
        }
        int result = (int)((largestY+1) * (getEventHeight()+_2pix) + _4pix);
        mLargestY = result;
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int y = mLargestY;
        int ny = MeasureSpec.makeMeasureSpec(y, MeasureSpec.getMode(h));
        dayLabel.setHeight(y);
        measureChild(dayLabel, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,mLargestY);
    }

    private static Paint gridPaint;

    private Paint getGridPaint() {
        if (gridPaint == null) {
            gridPaint = new Paint();
            gridPaint.setColor(0x20808090);
            gridPaint.setStrokeWidth(_1pix);
            gridPaint.setAntiAlias(true);
        }
        return gridPaint;
    }

    private static Paint gridPaintDark;

    private Paint getGridPaintDark() {
        if (gridPaintDark == null) {
            gridPaintDark = new Paint();
            gridPaintDark.setColor(0x20606090);
            gridPaintDark.setStrokeWidth(_1pix);
            gridPaintDark.setAntiAlias(true);
        }
        return gridPaintDark;
    }


    private static float _titleSize = 14, _subTitleSize = 12;
    private static float[] eventTextHeight = { 0, 0 };
    private static float _titleHeight = 0, _subTitleHeight = 0;
    private static boolean intializedHeight = false;
    private static Paint eventBorder;
    private static Paint eventDefaultColor;
    private static TextPaint eventTitlePaint;
    private static TextPaint eventSubTitlePaint;
    private static boolean showSubtitles = true;

    private void calcHeights() {
        if (!intializedHeight) {
            getEventTitlePaint();
            getEventSubTitlePaint();
            eventTextHeight[0] = (_6pix + _titleHeight);
            eventTextHeight[1] = (_8pix +(_subTitleHeight+_titleHeight));
            intializedHeight = true;
        }
    }

    public float getEventHeight() {
        calcHeights();
        return showSubtitles?eventTextHeight[1]:eventTextHeight[0];
    }

    public boolean isShowSubTitles() {
        return showSubtitles;
    }

    public void setShowSubTitles(boolean value) {
        showSubtitles = value;
    }

    private Paint getEventBorder() {
        if (eventBorder == null) {
            eventBorder = new Paint();
            eventBorder.setColor(0xFF4b7181);
            eventBorder.setStrokeWidth(_1pix);
            eventBorder.setStyle(Paint.Style.STROKE);
            eventBorder.setAntiAlias(true);
        }
        return eventBorder;
    }


    private Paint getEventDefaultColor() {
        if (eventDefaultColor == null) {
            eventDefaultColor = new Paint();
            eventDefaultColor.setColor(0xffbed1d9);
            eventDefaultColor.setAntiAlias(true);
        }
        return eventDefaultColor;
    }

    private Paint getEventDefaultColor(int colorIndex) {
        if (eventDefaultColor == null) {
            eventDefaultColor = new Paint();
            eventDefaultColor.setColor(0xffbed1d9);
            eventDefaultColor.setAntiAlias(true);
        }

        eventDefaultColor.setColor(EventItem.pastel20(colorIndex));
        return eventDefaultColor;
    }

    private TextPaint getEventTitlePaint() {
        if (eventTitlePaint == null) {
            eventTitlePaint = new TextPaint();
            eventTitlePaint.setTextAlign(Paint.Align.CENTER);
            eventTitlePaint.setColor(0xFF2f4751);
            eventTitlePaint.setAntiAlias(true);
            eventTitlePaint.setTextSize(_titleSize * _1pix);
            Paint.FontMetrics fm = eventTitlePaint.getFontMetrics();
            _titleHeight = fm.descent - fm.ascent - (fm.ascent-fm.top);
        }
        return eventTitlePaint;
    }

    private TextPaint getEventSubTitlePaint() {
        if (eventSubTitlePaint == null) {
            eventSubTitlePaint = new TextPaint();
            eventSubTitlePaint.setTextAlign(Paint.Align.CENTER);
            eventSubTitlePaint.setColor(0xd02f4751);
            eventSubTitlePaint.setAntiAlias(true);
            eventSubTitlePaint.setTextSize(_subTitleSize * _1pix);
            Paint.FontMetrics fm = eventSubTitlePaint.getFontMetrics();
            _subTitleHeight = fm.descent - fm.ascent - (fm.ascent-fm.top);
        }
        return eventSubTitlePaint;
    }


    private int drawEvent(EventItem e, Canvas c) {

        float scale = getScale() * _1pix;
        float eventHeight = getEventHeight();
        float eventWidth = e.getEventDuration() * scale - _2pix;

        float eventLeft = (e.getStartTime()-firstMinute) * scale + buttonBarWidth + _1pix;
        float eventTop = _2pix + (e.getYOffset()*(eventHeight+_2pix));
        float eventRight = eventLeft + eventWidth;
        float eventBottom = eventTop + eventHeight;

        float eventCenterX = eventLeft + eventWidth/2;

      //  if (eventRight < buttonBarWidth || eventLeft > getWidth())
      //      return (int)eventBottom;

        c.drawRoundRect(
                eventLeft,
                eventTop,
                eventRight,
                eventBottom,
                _2pix,
                _2pix,
                getEventDefaultColor(e.colorHint)
        );

        c.drawRoundRect(
                eventLeft,
                eventTop,
                eventRight,
                eventBottom,
                _2pix,
                _2pix,
                getEventBorder()
        );

        float eventY = eventTop + (_titleHeight);
        c.drawText(e.getTitle(), eventCenterX, eventY, getEventTitlePaint());
        if (showSubtitles) {
            eventY += _subTitleHeight+_2pix;
            c.drawText(e.getSubTitle(), eventCenterX, eventY, getEventSubTitlePaint());
        }
        return (int)eventBottom;
    }

    public void clearEventItems() {
        eventItems.clear();
    }

    public void addEventItem(EventItem item) {
        int forRow = 0;
        for(EventItem i: eventItems) {
            boolean conflicts = eventsConflict(item, i);
            if (conflicts) {
                int tRow = i.getYOffset()+1;
                if (tRow > forRow)
                    forRow = tRow;
            }
        }
        item.setYOffset(forRow);
        eventItems.add(item);
        mLargestY = getTallest();
        //invalidate();
        dayLabel.setVisibility(GONE);
        dayLabel.setVisibility(VISIBLE);
    }

    public static boolean eventsConflict(EventItem a, EventItem b) {

        int a1 = a.getStartTime(),
            a2 = a1 + a.getEventDuration();
        int b1 = b.getStartTime(),
            b2 = b1 + b.getEventDuration();

        return (
            (a1 >= b1 && a1 <= b2) ||
            (b1 >= a1 && b1 <= a2) ||
            (a2 >= b1 && a2 <= b2) ||
            (b2 >= a1 && b2 <= a2)
        );
    }

}
