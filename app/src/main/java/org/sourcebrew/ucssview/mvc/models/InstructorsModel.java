package org.sourcebrew.ucssview.mvc.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

import java.util.HashMap;

/**
 * Created by John on 1/6/2018.
 */

public class InstructorsModel extends Model {

    private static ModelMap<InstructorsModel> instructors;

   // private HashMap<CourseModel, Integer> hasTaught

    public InstructorsModel(JSONObject json) throws NoSuchFieldException {
        super(json, "username", "name");
    }

    public final String getUserName() {
        return getKey();
    }

    public final String getName() {
        return getValue();
    }

    public static ModelMap<InstructorsModel> getInstructors() {

        if (instructors == null) {
            instructors = new ModelMap<>();
        }
        return instructors;
    }

    public static void clearInstructors() {
        //getInstructors().clear();
    }

    public static int populateInstructors(JSONObject json) {
        int c = 0;

        if (json != null) {

            JSONArray arr = JSONAssistant.getArray(json, "instructors");
            c = populateInstructors(arr, true);
        }

        return c;
    }

    public static int populateInstructors(JSONArray arr, boolean doClear) {
        int c = 0;

        if (arr != null) {

           // if (doClear) getInstructors().clear();

            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject instructor = JSONAssistant.getArrayObject(arr, i);
                    if (instructor != null) {
                        try {
                            InstructorsModel model = new InstructorsModel(instructor);
                            getInstructors().put(model);
                            c++;
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            getInstructors().completedOperation();

        }

        return c;
    }

    @Override
    public String toString() {
        return getUserName();
    }
}
