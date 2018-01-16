package org.sourcebrew.ucssview.mvc.models;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by John on 1/6/2018.
 */

public class TermModel extends Model implements Iterable<SectionModel> {

    private static ModelMap<TermModel> terms;

    private final ArrayList<SectionModel> sectionsList = new ArrayList<>();

    public void addSection(SectionModel section) {
        if (sectionsList.contains(section))
            return;
        else {
            sectionsList.add(section);
        }
    }

    public boolean isEmpty() {
        return sectionsList.isEmpty();
    }

    public int sectionsCount() {
        return sectionsList.size();
    }

    public TermModel(JSONObject json) throws NoSuchFieldException {
        super(json, "code", "text");
        getTerms().put(TermModel.this);
    }



    public String getCode() {
        return super.getKey();
    }

    public String getText() {
        return super.getValue();
    }

    public static ModelMap<TermModel> getTerms() {

        if (terms == null) {
            terms = new ModelMap<>();
        }
        return terms;
    }

    public static void clearTerms() {
        getTerms().clear();
    }

    public static int populateTerms(JSONObject json) {
        int c = 0;

        if (json != null) {

            JSONArray arr = JSONAssistant.getArray(json, "terms");

            getTerms().clear();

            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject term = JSONAssistant.getArrayObject(arr, i);
                    if (term != null) {
                        try {
                            new TermModel(term);
                            c++;
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            getTerms().completedOperation();
        }

        return c;
    }

    @NonNull
    @Override
    public Iterator<SectionModel> iterator() {
        return sectionsList.iterator();
    }

    @Override
    public String toString() {
        return getCode();
    }


}
