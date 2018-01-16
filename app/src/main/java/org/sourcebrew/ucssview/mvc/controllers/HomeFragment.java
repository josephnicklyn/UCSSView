package org.sourcebrew.ucssview.mvc.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sourcebrew.ucssview.R;
import org.sourcebrew.ucssview.mvc.models.TermModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends UCSSFragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void termChanged(TermModel term) {
        Log.e("SVSU", "termChanged isResumed = " + (UCSSController.getAdapter().getCurrentFragment() == this));
    }

    @Override public void onResume() {
        super.onResume();
        Log.e("SVSU", "onResume isResumed = " + (UCSSController.getAdapter().getCurrentFragment() == this));

    }
}
