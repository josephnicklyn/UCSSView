package org.sourcebrew.ucssview.mvc.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.mvc.views.TermGetterView;
import org.sourcebrew.ucssview.network.SVSUAPIGetter;
import org.sourcebrew.ucssview.network.UIThreadSyncCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class InitializeFragment extends UCSSFragment {

    private TermGetterView termGetterView;
    private ViewGroup initializeFragmentMainView;

    public InitializeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_initialize, container, false);

        initializeFragmentMainView = (ViewGroup)v.findViewById(R.id.initializeFragmentMainView);
        termGetterView = new TermGetterView(getContext());

        initializeFragmentMainView.addView(termGetterView);
        return v;
    }

    @Override public void onResume() {
        super.onResume();
        if (TermModel.getTerms().isEmpty()) {
            initialize();
        }
    }

    private void initialize() {
        SVSUAPIGetter.getInstance().setHost("https://api.svsu.edu/").getBasics(
                null,
                new UIThreadSyncCallback() {
                    @Override
                    protected void onFinished() {
                    termGetterView.updateTerms();
                    }
                }
        );
    }

}
