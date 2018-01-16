package org.sourcebrew.ucssview.mvc.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.controllers.UCSSController;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.network.SVSUAPIGetter;
import org.sourcebrew.ucssview.network.UIThreadSyncCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by John on 1/13/2018.
 */

public class TermGetterView extends FrameLayout {

    LinearLayout template_get_data_list;
    ProgressBar template_get_data_progressBar;
    public TermGetterView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.template_get_data, this, true);

        template_get_data_list = findViewById(R.id.template_get_data_list);
        template_get_data_progressBar = findViewById(R.id.template_get_data_progressBar);
        float _1pix = context.getResources().getDisplayMetrics().density;
    }

    public void updateTerms() {

        template_get_data_list.removeAllViews();
        Iterator it = TermModel.getTerms().entrySet().iterator();
        template_get_data_progressBar.setVisibility(View.GONE);
        TermButton termButton = null;

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();

            TermModel tm = (TermModel)me.getValue();

            termButton = new TermButton(getContext(), tm);
        }

        if (termButton != null) {
            termButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);

        }
    }

    protected void disable() {
        for(int i = 0; i < template_get_data_list.getChildCount(); i++) {
            View v = template_get_data_list.getChildAt(i);
            v.setEnabled(false);
        }

    }

    protected void enabled() {
        for(int i = 0; i < template_get_data_list.getChildCount(); i++) {
            View v = template_get_data_list.getChildAt(i);
            v.setEnabled(true);
        }
    }


    private class TermButton extends android.support.v7.widget.AppCompatButton
        implements OnClickListener {

        private final TermModel term;
        private boolean wasSelected = false;
        boolean wasSelected() {
            return wasSelected;
        }
        public TermButton(Context context, TermModel forTerm) {
            super(context);
            term = forTerm;
            setText(forTerm.getText());
            template_get_data_list.addView(TermButton.this, 0);

            this.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            this.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
            setOnClickListener(TermButton.this);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }

        public TermModel getTerm() {
            return term;
        }

        @Override
        public void onClick(View v) {
            if (v == TermButton.this) {
                highlightButton(TermButton.this);

                if (term.isEmpty()) {
                    wasSelected = true;
                    disable();

                    template_get_data_progressBar.setVisibility(View.VISIBLE);
                    SVSUAPIGetter.getInstance().getCourseData(
                            template_get_data_progressBar,
                            term,
                            new UIThreadSyncCallback() {
                                @Override
                                protected void onFinished() {
                                    template_get_data_progressBar.setVisibility(View.GONE);
                                    enabled();
                                    UCSSController.getAdapter().selectTerm(term);
                                }

                                @Override
                                protected void onException(final String source, final String message) {
                                    Log.e("SVSU", source + " <=> " + message);
                                    enabled();
                                }
                            }
                    );
                } else {
                    UCSSController.getAdapter().selectTerm(term);
                    template_get_data_progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    private void highlightButton(TermButton termButton) {
        for(int i = 0; i < template_get_data_list.getChildCount(); i++) {
            View v = template_get_data_list.getChildAt(i);
            if (v instanceof TermButton) {
                TermButton tv = (TermButton)v;
                if (tv == termButton)
                    tv.setTextColor(0xFF0080FF);
                else {
                    if (tv.wasSelected)
                        tv.setTextColor(0xFF0060A0);
                    else
                        tv.setTextColor(0xFF000000);
                }
            }

        }
    }
}
