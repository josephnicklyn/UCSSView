package org.sourcebrew.ucssview.mvc.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.sourcebrew.ucssview.mvc.models.CourseModel;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by John on 1/16/2018.
 */

public class FlowLayoutListCheckBox<E> extends FlowLayoutList implements View.OnClickListener {

    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    public FlowLayoutListCheckBox(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        tvSelectAll.setOnClickListener(this);
        tvSelectNone.setOnClickListener(this);

    }

    public boolean addOption(String title, E referenceObject) {
        if (containsOptionForObject(referenceObject)) {
            return false;
        }
        CheckBox item = new CheckBox(getContext());

        item.setText(title);
        item.setTag(referenceObject);
        getFlowLayout().addView(item);
        checkBoxes.add(item);


        //    flowLayoutListSelectChangeListener.stateChanged((E)target.getTag(), checked);
        item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (flowLayoutListSelectChangeListener != null)
                flowLayoutListSelectChangeListener.stateChanged((E)buttonView.getTag(), isChecked);

            }
        });
        return true;
    }

    public void clear() {
        checkBoxes.clear();
        getFlowLayout().removeAllViews();

    }

    public boolean containsOptionForObject(E referenceObject) {
        for(CompoundButton cpb: checkBoxes) {
            if (cpb.getTag() == referenceObject)
                return true;
        }
        return false;
    }

    public void selectOption(boolean checked, E referenceObject) {
        for(CompoundButton cpb: checkBoxes) {
            if (cpb.getTag() == referenceObject) {
                cpb.setChecked(checked);
                break;
            }
        }
    }

    public void selectAll() {
        for (CompoundButton cpb : checkBoxes) {
            cpb.setChecked(true);
        }
    }

    public void selectNone() {
        for (CompoundButton cpb : checkBoxes) {
            cpb.setChecked(false);
        }
    }

    private FlowLayoutListSelectChangeListener flowLayoutListSelectChangeListener;

    public void setOnListItemSelectChanged(FlowLayoutListSelectChangeListener listener) {
        flowLayoutListSelectChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == tvSelectAll) {
            selectAll();
        } else if (v == tvSelectNone) {
            selectNone();
        }
    }


    public interface FlowLayoutListSelectChangeListener<E> {
        public void stateChanged(E e, boolean isChecked);
    }

    public ArrayList<E> getSelectedItems() {
        ArrayList<E> r = new ArrayList<>();
        for(CompoundButton cpb: checkBoxes) {
            if (cpb.isChecked()) {
                r.add((E)cpb.getTag());
            }
        }
        return r;
    }

}
