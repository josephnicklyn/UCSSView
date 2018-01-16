package org.sourcebrew.ucssview.mvc.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.models.CourseModel;
import org.sourcebrew.ucssview.mvc.models.SectionModel;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.mvc.views.RangeSelect;
import org.sourcebrew.ucssview.timegraph.EventGraph;

import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CampusViewFragment extends UCSSFragment
        implements View.OnClickListener {

    EventGraph eventGraph;

    private TextView graphs_info_text;
    private ImageView graphs_view_toggle;
    private ViewGroup graphs_list_layout;
    private RangeSelect graphs_range_select;


    private String viewMessage = "";

    public CampusViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_campus_view, container, false);
        eventGraph = v.findViewById(R.id.eventGraph);

        graphs_info_text = v.findViewById(R.id.graphs_info_text);
        graphs_view_toggle = v.findViewById(R.id.graphs_view_toggle);
        graphs_list_layout = v.findViewById(R.id.graphs_list_layout);
        graphs_range_select = v.findViewById(R.id.graphs_range_select);

        graphs_range_select.hideLabels();
        graphs_range_select.addOptions("COURSES", "INSTRUCTORS", "ROOMS");


        graphs_view_toggle.setOnClickListener(this);

        return v;
    }

    @Override
    public void termChanged(TermModel term) {
        updateUI();
        /*
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
        */
    }

    private void updateUI() {

        String termLabel = (UCSSController.getAdapter().getTermModel()!=null)?
                UCSSController.getAdapter().getTermModel().getCode():
                "NOTHING SELECTED";
        graphs_info_text.setText(termLabel + ": " + viewMessage);

    }

    private void toggleView() {

        eventGraph.setVisibility(graphs_list_layout.getVisibility());

        int v = (graphs_list_layout.getVisibility()==View.GONE)?
                View.VISIBLE:View.GONE;

        if (v == View.VISIBLE) {
            graphs_view_toggle.setImageResource(R.drawable.ic_arrow_drop_down);
        } else {
            graphs_view_toggle.setImageResource(R.drawable.ic_arrow_drop);
        }
        graphs_list_layout.setVisibility(v);

    }

    @Override
    public void onClick(View view) {
        if (view == graphs_view_toggle) {
            toggleView();
        }
    }


}
