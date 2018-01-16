package org.sourcebrew.ucssview.mvc.controllers;

import android.support.v4.app.Fragment;

import org.sourcebrew.ucssview.mvc.models.TermModel;

/**
 * Created by John on 1/13/2018.
 */

public abstract class UCSSFragment extends Fragment {

    public void onSelect() {};

    public void termChanged(TermModel term) {};


}
