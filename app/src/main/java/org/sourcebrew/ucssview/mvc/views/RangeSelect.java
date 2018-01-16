package org.sourcebrew.ucssview.mvc.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;

/**
 * Created by jfnickly on 1/16/2018.
 */

public class RangeSelect  extends LinearLayout {

    TextView range_select_left, range_select_right;
    LinearLayout range_select_container;

    String selectedValue = null;

    public RangeSelect(Context context) {
        super(context);
        init();
    }

    public RangeSelect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RangeSelect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.template_range_select, this, true);
        range_select_left = findViewById(R.id.range_select_left);
        range_select_right = findViewById(R.id.range_select_right);
        range_select_container = findViewById(R.id.range_select_container);

        range_select_right.setText("");
        range_select_left.setText("");


    }

    public void hideLabels() {
        range_select_right.setVisibility(View.GONE);
        range_select_left.setVisibility(View.GONE);
    }

    public void showLabels() {
        range_select_right.setVisibility(View.VISIBLE);
        range_select_left.setVisibility(View.VISIBLE);
    }

    public interface RangeSelectInterface {
        public void itemSelected(View oldView, View newView, String oldValue, String newValue);
    }

    private RangeSelectInterface rangeSelectInterface;

    public void select(String value) {
        for(int i = 0; i < range_select_container.getChildCount(); i++) {
            View v = range_select_container.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView)v;
                if (value.equalsIgnoreCase(tv.getText().toString())) {
                    tv.performClick();
                    return;
                }
            }
        }
    }

    View previousSelect = null;
    OnClickListener onClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (previousSelect != null) {
                previousSelect.setBackgroundResource(R.drawable.border_for_view);
            }
            if (v != previousSelect) {
                v.setBackgroundResource(R.drawable.border_for_view_selected);
                String value = selectedValue;
                selectedValue = ((TextView) v).getText().toString();
                if (rangeSelectInterface != null) {
                    rangeSelectInterface.itemSelected(
                            previousSelect,
                            v,
                            value,
                            selectedValue
                    );
                }
            }
            previousSelect = v;
        }
    };

    public void setLeftText(String value) {
        range_select_left.setText(value);
    }

    public void setRightText(String value) {
        range_select_right.setText(value);
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setOnRangeSelectChange(RangeSelectInterface listener) {
        rangeSelectInterface = listener;
    }

    public void addOptions(String... s) {
        if (s != null) {
            for(String i: s)
                addOption(i);
        }
    }

    public void addOption(String s) {

        if (s == null || s.isEmpty())
            return;

        TextView t = new TextView(getContext());
        t.setText(s);
        t.setGravity(Gravity.CENTER);

        LayoutParams ll = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        ll.weight = 1;
        int p = (int)(getResources().getDisplayMetrics().density * 4);
        t.setLayoutParams(ll);
        t.setBackgroundResource(R.drawable.border_for_view);
        t.setPadding(p, p+p, p, p+p);
        t.setTextColor(0xFF404050);
        range_select_container.addView(t);
        t.setClickable(true);
        t.setOnClickListener(onClicked);

    }

}