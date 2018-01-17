package org.sourcebrew.ucssview.mvc.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.controllers.UCSSController;
import org.sourcebrew.ucssview.mvc.models.CourseModel;
import org.sourcebrew.ucssview.mvc.models.PrefixModel;
import org.sourcebrew.ucssview.mvc.models.SectionModel;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.network.SVSUAPIGetter;
import org.sourcebrew.ucssview.network.UIThreadSyncCallback;
import org.sourcebrew.ucssview.timegraph.EventGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by John on 1/17/2018.
 */

public class PrefixSelector extends FlowLayout implements View.OnClickListener {

    private HashMap<PrefixModel, CompoundButton> options = new HashMap<>();
    float _1pix = 0;
    int p = 0;
    int maxSelectCount = 3;

    public PrefixSelector(Context context) {
        super(context);
        setBackgroundResource(R.drawable.border_for_view);
        _1pix = getResources().getDisplayMetrics().density;
        p = (int)(_1pix*8);

        setPadding(p, p, p, p);

    }

    public void updateIfEmpty() {
        if (getChildCount() == 0)
            update();
    }

    public void update() {
        removeAllViews();

        Iterator it = PrefixModel.getPrefixes().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            PrefixModel pm = (PrefixModel) me.getValue();
            addPrefix(pm);
        }
    }

    private void addPrefix(PrefixModel pm) {
        if (options.get(pm) == null) {
            CompoundButton btn = new CheckBox(getContext());
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btn.setPadding(p, 0, 0, 0);
            btn.setText(pm.getPrefix() + "\n" + pm.getDescription());
            options.put(pm, btn);
            btn.setOnClickListener(this);
            addView(btn);
        }
    }
    @Override
    public void onClick(View v) {
        hasChangedSelections = true;
        toggleOthers(v);
    }

    private int getSelectedCount() {
        int i = 0;
        for(PrefixModel p: options.keySet()) {
            CompoundButton btn = options.get(p);
            if (btn.isChecked()) i++;
        }
        return i;
    }

    private void toggleOthers(View v) {

        if (maxSelectCount != 0) {
            if (getSelectedCount() > maxSelectCount) {
                ((CompoundButton) v).setChecked(false);
                return;
            }
        } else {
            for (PrefixModel p : options.keySet()) {
                CompoundButton btn = options.get(p);

                if (v == btn) {
                    continue;
                } else {
                    btn.setChecked(false);
                }
            }
        }
    }

    public String updateGraph(final EventGraph eventGraph, boolean force) {
        StringBuilder b = new StringBuilder();
        if (hasChangedSelections || force) {
            eventGraph.clear();

            TermModel tm = UCSSController.getAdapter().getTermModel();
            if (tm != null) {
                for (PrefixModel p : options.keySet()) {
                    CompoundButton btn = options.get(p);
                    if (btn.isChecked()) {

                        if (b.length() != 0)
                            b.append(", ");
                        b.append(p.getPrefix());

                        if (!tm.containsPrefixFor(p)) {
                            final PrefixModel pm = p;
                            SVSUAPIGetter.getInstance().getCourseData(
                                    new UIThreadSyncCallback() {
                                        @Override
                                        protected void onFinished() {
                                            updatePrefix(eventGraph, pm);
                                        }
                                    },
                                    tm.getCode(),
                                    p.getPrefix()
                            );
                        } else {
                            updatePrefix(eventGraph, p);
                        }

                    }
                }
            }
        }
        hasChangedSelections = false;
        b.insert(0, "Prefixes  [ ");
        b.append(" ]");
        return b.toString();
    }

    private void updatePrefix(EventGraph eventGraph, PrefixModel p) {

        TermModel tm = UCSSController.getAdapter().getTermModel();
        if (tm != null) {

            Iterator it = SectionModel.getSections().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                SectionModel sm = (SectionModel) me.getValue();

                if (sm.getCourseModel().getPrefixModel() == p) {
                    if (sm.getTermModel() == tm)
                        eventGraph.addSectionModel(sm);
                }
            }
        }
    }
    private boolean hasChangedSelections = false;
    /*
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (visibility != 0) {
            hasChangedSelections = false;
        }
    }
    */
}
