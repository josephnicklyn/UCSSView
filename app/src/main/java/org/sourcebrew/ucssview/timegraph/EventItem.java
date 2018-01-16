package org.sourcebrew.ucssview.timegraph;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import java.util.Random;

/**
 * Created by John on 1/15/2018.
 */

public class EventItem {

    private int startTime, eventDuration;
    private String title = "", subTitle = "";
    private static float _1pix = 1, _2pix = 2;
    private static boolean setPix = false;
    private static float _titleSize = 16, _subTitleSize = 12;
    private float _titleWidth, _subTitleWidth;
    private int yOffset = 1;
    private static boolean showSubTitle = true;
    public int colorHint = 0;
    public static float getPix(int p) {
        return get_1pix()*p;
    }

    public static float get_1pix() {
        if (!setPix) {
            _1pix=Resources.getSystem().getDisplayMetrics().density;
            _2pix = 2 * _1pix;
        }
        return _1pix;
    }

    public static float get_2pix() {
        if (!setPix) {
            _1pix=Resources.getSystem().getDisplayMetrics().density;
            _2pix = 2 * _1pix;
        }
        return _2pix;
    }
    public EventItem() {

    }

    public EventItem(String title) {
        setTitle(title);
        init();
    }

    public EventItem(String title, int startTime, int eventDuration) {
        setTitle(title);
        setStartTime(startTime);
        setEventDuration(eventDuration);
        init();
    }

    public EventItem(String title, int startTime, int eventDuration, int colorHint) {
        setTitle(title);
        setStartTime(startTime);
        setEventDuration(eventDuration);
        this.colorHint = colorHint;
        init();
    }

    private void init() {

    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int value) {
        value %= 1440;
        startTime = value;
    }

    public int getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(int value) {
        value %= 1440;
        eventDuration = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        if (value != null && !value.isEmpty()) {
            title = value;
        }
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String value) {
        if (value != null && !value.isEmpty()) {
            subTitle = value;
        }
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int v) {
        if (v < 0) v = 0;
        if (v > 20) v = 20;
        yOffset =v;
    }

    private static int defaultColor = 0xFFA0B0C0;
    public final static int generatePastel(int n, int i) {

        if (i == -1)
            return defaultColor;

        if (n <= 0)
            n = 10;
        if (n >= 64)
            n = 64;
        i = i % n;

        float r = (float)(0.2 + (0.4/(double) n*i));
        float h = (float)(120+200/(double)((i+1)*2));
        return Color.HSVToColor(new float[] {h, r, 1.0f});
    }

    public static int pastel60(int value) {
        return pastels(
                ((value%60)*6),
                true
        );
    }

    public static int pastel20(int value) {
        return pastels(
                ((value%20)*18),
                true
        );
    }

    public static int pastels(int hue, boolean lo) {
        return Color.HSVToColor(
            new float[] {
                    (float)(hue % 360),
                    (float)(lo?0.24:0.48),
                    0.875f
            }
        );
    }
}
