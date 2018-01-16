package org.sourcebrew.ucssview.mvc.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.models.CourseModel;
import org.sourcebrew.ucssview.mvc.models.SectionModel;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.timegraph.EventGraph;

import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CampusViewFragment extends UCSSFragment {

    EventGraph eventGraph;
    public CampusViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_campus_view, container, false);
        eventGraph = v.findViewById(R.id.eventGraph);
        return v;
    }

    @Override
    public void termChanged(TermModel term) {
        Log.e("TERM_CHANGE", term.toString() + ", " + SectionModel.getSections().size() + ", " + CourseModel.getCourses().size());
        Iterator it = SectionModel.getSections().entrySet().iterator();
        eventGraph.clear();
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            SectionModel sm = (SectionModel) me.getValue();
            if (sm.getTermModel() == term) {
                if (sm.getCourseModel().getPrefix().equalsIgnoreCase("cs")) {
                    eventGraph.addSectionModel(sm);
                }
            }
        }
    }
}
