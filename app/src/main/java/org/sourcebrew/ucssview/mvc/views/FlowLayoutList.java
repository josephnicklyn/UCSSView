package org.sourcebrew.ucssview.mvc.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;

/**
 * Created by John on 1/16/2018.
 */

public class FlowLayoutList extends LinearLayout {

    public interface FlowLayoutListVisibilityChanged {
        public void visiblityChanged(FlowLayoutList source, boolean isExpanded);
    }

    private FlowLayoutListVisibilityChanged flowLayoutListVisibilityChanged;

    public void setOnFlowLayoutListVisibilityChangedLister(FlowLayoutListVisibilityChanged listener) {
        flowLayoutListVisibilityChanged = listener;
    }

    private LinearLayout header;
    private TextView title;
    private FlowLayout flowLayout;
    private ImageView imageView;

    protected TextView tvSelectAll, tvSelectNone;


    public FlowLayoutList(Context context) {
        super(context);
        init();
    }

    private void init() {

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.drawable.border_for_view);

        imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_arrow_drop_down);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == imageView) {
                    toggleExpand();
                }
            }
        });

        header = new LinearLayout(getContext());
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setGravity(Gravity.CENTER_VERTICAL);
            header.setBackgroundResource(R.drawable.border_bottom_line);
        title = new TextView(getContext());
            setBoldText(true);
            setTextSize(18);

        tvSelectAll = new TextView(getContext());
        tvSelectAll.setText("ALL");
        tvSelectAll.setBackgroundResource(R.drawable.border_for_view);
        tvSelectNone = new TextView(getContext());
        tvSelectNone.setText("NONE");
        tvSelectNone.setBackgroundResource(R.drawable.border_for_view);

        flowLayout = new FlowLayout(getContext());
            flowLayout.setVisibility(View.GONE);
            flowLayout.setBackgroundColor(0xFFfafaff);

        header.addView(title);
        header.addView(tvSelectAll);
        header.addView(tvSelectNone);
        header.addView(imageView);

        addView(header);
        addView(flowLayout);
        float _1pix = getResources().getDisplayMetrics().density;
        int p = (int)(_1pix*4);

        title.setPadding(p, p, p, p);
        setPadding(p, p, p, p);
        header.setPadding(p, p, p, p);
        flowLayout.setPadding(p+p, 0, 0, 0);
        LinearLayout.LayoutParams lt = (LayoutParams) title.getLayoutParams();

        lt.weight = 1;
        lt.width = -1;
        lt.height = -2;

        lt = (LayoutParams) imageView.getLayoutParams();

        lt.weight = 0;
        lt.width = -2;
        lt.height = -2;

        lt = (LayoutParams) flowLayout.getLayoutParams();

        lt.weight = 1;
        lt.width = -1;
        lt.height = -1;

        lt = (LayoutParams) header.getLayoutParams();

        lt.weight = 0;
        lt.width = -1;
        lt.height = -2;

        lt = (LayoutParams) tvSelectNone.getLayoutParams();

        lt.weight = 0;
        lt.width = -2;
        lt.height = -2;

        lt = (LayoutParams) tvSelectAll.getLayoutParams();

        lt.weight = 0;
        lt.width = -2;
        lt.height = -2;

        p/=2;
        lt.setMargins(0, 0, p, 0);

        tvSelectAll.setPadding(p, p, p, p);

        tvSelectNone.setPadding(p, p, p, p);


    }

    public void setExpanded(boolean value) {
        boolean oldValue = getExpanded();
        flowLayout.setVisibility((value?View.VISIBLE:View.GONE));
        if (value) {
            imageView.setImageResource(R.drawable.ic_arrow_drop);
        } else {
            imageView.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        if (oldValue != value && flowLayoutListVisibilityChanged != null) {
            flowLayoutListVisibilityChanged.visiblityChanged(FlowLayoutList.this, value);
        }
    }

    public boolean getExpanded() {
        return (flowLayout.getVisibility() == View.VISIBLE);
    }

    private void toggleExpand() {
        setExpanded(!getExpanded());
    }

    public void setText(String value) {
        title.setText(value);
    }

    public String getText() {
        return title.getText().toString();
    }

    public void setBoldText(boolean value) {
        title.setTypeface(null, (value?Typeface.BOLD: Typeface.NORMAL));
    }

    public void setTextSize(int value) {
        value = Helper.between(value, 8, 24);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
    }

    public FlowLayout getFlowLayout() {
        return flowLayout;
    }
}
