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

public class PrefixModel  extends Model implements Iterable<CourseModel> {

    private static ModelMap<PrefixModel> prefixes;
    private final ArrayList<CourseModel> courses = new ArrayList<>();


    public PrefixModel(JSONObject json) throws NoSuchFieldException {
        super(json, "prefix", "description");
        getPrefixes().put(PrefixModel.this);
    }

    public String getPrefix() {
        return super.getKey();
    }

    public String getDescription() {
        return super.getValue();
    }

    public ArrayList<CourseModel> getCourses() {
        return courses;
    }

    @NonNull
    @Override
    public Iterator<CourseModel> iterator() {
        return courses.iterator();
    }

    public boolean hasCourses() {
        return !courses.isEmpty();
    }

    public CourseModel getCourseByID(String id) {
        for(CourseModel c: courses) {
            if (c.getKey().equalsIgnoreCase(id))
                return c;
        }
        return null;
    }

    public boolean addCourse(CourseModel course) {
        if (courses.contains(course))
            return false;
        else {
            return courses.add(course);
        }
    }

    @Override
    public String toString() {

        if (courses.isEmpty())
            return "";

        StringBuilder b = new StringBuilder();
        String header =  key + ":" + value;
        b.append(header).append(" {");
        for(CourseModel c: courses) {
            b.append("\n\t").append(c.toString());
        }
        b.append("\n}");
        return b.toString();
    }

    public static ModelMap<PrefixModel> getPrefixes() {

        if (prefixes == null) {
            prefixes = new ModelMap<>();
        }
        return prefixes;
    }

    public static void clearTerms() {
        getPrefixes().clear();
    }

    public static int populatePrefixes(JSONObject json) {
        int c = 0;

        if (json != null) {

            JSONArray arr = JSONAssistant.getArray(json, "prefixes");

            getPrefixes().clear();

            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject prefix = JSONAssistant.getArrayObject(arr, i);
                    if (prefix != null) {
                        try {
                            new PrefixModel(prefix);

                            c++;
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            getPrefixes().completedOperation();
        }

        return c;
    }

    public static boolean addCourse(CourseModel model, JSONObject course) {
        String prefix = JSONAssistant.getString(course, "prefix");
        String courseNumber = JSONAssistant.getString(course, "courseNumber");
        PrefixModel prefixModel = PrefixModel.getPrefixes().get(prefix);

        if (prefix != null) {
            return prefixModel.addCourse(model);
        } else {
            return false;
        }

    }


}
