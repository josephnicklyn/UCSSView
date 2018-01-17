package org.sourcebrew.ucssview.mvc.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

import java.util.List;

/**
 * Created by John on 1/6/2018.
 */

public class CourseModel extends Model {

    private static ModelMap<CourseModel> courses;
    private final String
            courseNumber,
            prefix,
            description,
            lineNumber,
            prerequisites;
    private final String[] comments;

    private final int
            credits;

    private PrefixModel prefixModel;
    public PrefixModel getPrefixModel() {
        if (prefixModel == null) {
            prefixModel = PrefixModel.getPrefixes().get(prefix);
        }
        return prefixModel;
    }

    @Override public String toString() {
        return getCourseNumber() + " - " + getTitle();
    }

    public int getPrefixModelIndex() {
        getPrefixModel();
        if (prefixModel != null)
            return prefixModel.getModelIndex();
        return 0;
    }

    public CourseModel(JSONObject json, String key, String value) throws NoSuchFieldException {
        super(key, value);

        courseNumber = JSONAssistant.getString(json, "courseNumber");
        prefix = JSONAssistant.getString(json, "prefix");
        credits = JSONAssistant.getInt(json, "credits");
        description = JSONAssistant.getString(json, "description");
        lineNumber = JSONAssistant.getString(json, "lineNumber");
        prerequisites = JSONAssistant.getString(json, "prerequisites");

        JSONArray arr = JSONAssistant.getArray(json, "comments");

        if (arr != null) {
            comments = new String[arr.length()];
            for(int i = 0; i < arr.length(); i++) {
                try {
                    String v = arr.getString(i);
                    comments[i] = v;
                } catch (JSONException e) {
                    comments[i] = "error";
                }
            }
        } else {
            comments = new String[] {"no comments"};
        }
    }

    public final String getCourseNumber() {
        return courseNumber;
    }

    public final String getTitle() {
        return getValue();
    }

    public final String getDescription() {
        return description;
    }

    public final int getCredits() {
        return credits;
    }

    public final String getLineNumber() {
        return lineNumber;
    }

    public final String getPrerequisites() {
        return prerequisites;
    }

    public final String[] getComments() {
        return comments;
    }

    public String getPrefix() {
        return prefix;
    }

    public static ModelMap<CourseModel> getCourses() {

        if (courses == null) {
            courses = new ModelMap<>();
        }
        return courses;
    }

    public static void clearCourses() {
        //getCourses().clear();
    }



    public static int addCourse(JSONObject course, boolean doClear) {
        int c = 0;

        if (course != null) {
            //if (doClear) getCourses().clear();

            String keyA = JSONAssistant.getString(course, "prefix");
            String keyB = JSONAssistant.getString(course, "courseNumber");

            String courseKey = keyA + "-" + keyB;

            if (getCourses().containsKey(courseKey))
                return 0;

            if (course != null) {
                try {
                    String title = JSONAssistant.getString(course, "title");
                    CourseModel model = new CourseModel(course, courseKey, title);
                    PrefixModel.addCourse(model, course);
                    getCourses().put(model);
                    c++;
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        getCourses().completedOperation();

        return c;
    }

}