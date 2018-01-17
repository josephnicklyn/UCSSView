package org.sourcebrew.ucssview.mvc.views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.controllers.UCSSController;
import org.sourcebrew.ucssview.mvc.models.CourseModel;
import org.sourcebrew.ucssview.mvc.models.PrefixModel;
import org.sourcebrew.ucssview.mvc.models.SectionModel;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.network.SVSUAPIGetter;
import org.sourcebrew.ucssview.network.UIThreadSyncCallback;
import org.sourcebrew.ucssview.timegraph.EventGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by John on 1/16/2018.
 */

public class CourseSelectView extends LinearLayout implements FlowLayoutList.FlowLayoutListVisibilityChanged {

    public HashMap<PrefixModel, FlowLayoutListCheckBox<CourseModel>> lists = new HashMap<>();
    ScrollView graphs_scroll_view;
    public CourseSelectView(Context context, ScrollView graphs_scroll_view) {
        super(context);
        float _1pix = getResources().getDisplayMetrics().density;
        int p = (int)(_1pix*4);
        setPadding(p, p, p, p);
        setOrientation(LinearLayout.VERTICAL);
        this.graphs_scroll_view = graphs_scroll_view;
    }

    public boolean isEmpty() {
        return getChildCount() == 0;
    }

    public FlowLayoutListCheckBox<CourseModel> addOption(PrefixModel prefixModel) {
        if (prefixModel != null) {
            FlowLayoutListCheckBox<CourseModel> item = lists.get(prefixModel);

            if (item == null) {
                item = new FlowLayoutListCheckBox<CourseModel>(getContext());

                super.addView(item);
                float _1pix = getResources().getDisplayMetrics().density;
                int p = (int)(_1pix*4);
                ((LinearLayout.LayoutParams)item.getLayoutParams()).setMargins(4, 4, 4, 4);
                lists.put(prefixModel, item);
                item.setText(prefixModel.toString());

                item.setOnFlowLayoutListVisibilityChangedLister(CourseSelectView.this);
            }
            return item;
        }
        return null;
    }

    public void addOption(CourseModel courseModel) {
        FlowLayoutListCheckBox<CourseModel> item = addOption(courseModel.getPrefixModel());
        if (item != null && !containsCourseModel(courseModel)) {
            item.addOption(courseModel.toString(), courseModel);

        }
    }

    private boolean containsCourseModel(CourseModel cm) {
        for(PrefixModel p: lists.keySet()) {
            FlowLayoutListCheckBox<CourseModel> fc = lists.get(p);

            if (fc.containsOptionForObject(cm)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        lists.clear();
        super.removeAllViews();;
    }

    @Override
    public void visiblityChanged(FlowLayoutList source, boolean isExpanded) {

        PrefixModel target = null;
        for(PrefixModel p: lists.keySet()) {
            FlowLayoutListCheckBox<CourseModel> fc = lists.get(p);

            if (fc == source) {
                target = p;
                break;
            }
        }
        Log.e("ADD_OPTION", "PrefixModel <-> " + target + ", " + isExpanded);

        if (target != null && isExpanded) {
            updateCourseList(target);
            graphs_scroll_view.setScrollY((int)source.getY());
        }
    }

    private void updateCourseList(PrefixModel pm) {

        if (pm == null)
            return;

        TermModel tm = UCSSController.getAdapter().getTermModel();
        if (tm != null) {

            if (!tm.containsPrefixFor(pm)) {
                SVSUAPIGetter.getInstance().getCourseData(
                        new UIThreadSyncCallback() {
                            @Override
                            protected void onFinished() {
                                Iterator it = CourseModel.getCourses().entrySet().iterator();

                                while (it.hasNext()) {
                                    Map.Entry me = (Map.Entry) it.next();
                                    CourseModel cm = (CourseModel) me.getValue();
                                    addOption(cm);
                                }
                            }
                        },
                        tm.getCode(),
                        pm.getPrefix()
                );
            }
        }
    }

    public void updateGraph(EventGraph eventGraph) {
        eventGraph.clear();
        TermModel tm = UCSSController.getAdapter().getTermModel();
        if (tm != null) {
            for (PrefixModel p : lists.keySet()) {
                FlowLayoutListCheckBox<CourseModel> fc = lists.get(p);
                for (CourseModel cm : fc.getSelectedItems()) {
                    Iterator it = SectionModel.getSections().entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        SectionModel sm = (SectionModel) me.getValue();
                        if (sm.getCourseModel() == cm) {
                            if (sm.getTermModel() == tm)
                                eventGraph.addSectionModel(sm);
                        }
                    }
                }
            }
        }
    }
}
