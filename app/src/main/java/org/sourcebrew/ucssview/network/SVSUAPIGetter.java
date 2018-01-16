package org.sourcebrew.ucssview.network;

import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;
import org.sourcebrew.ucssview.mvc.models.BuildingModel;
import org.sourcebrew.ucssview.mvc.models.CourseModel;
import org.sourcebrew.ucssview.mvc.models.InstructorsModel;
import org.sourcebrew.ucssview.mvc.models.PrefixModel;
import org.sourcebrew.ucssview.mvc.models.SectionModel;
import org.sourcebrew.ucssview.mvc.models.TermModel;
import org.sourcebrew.ucssview.mvc.views.TermGetterView;

import java.util.Iterator;
import java.util.Map;

/**
 * This class grabs course data from a school's api
 *
 * Created by John on 1/3/2018.
 */

public final class SVSUAPIGetter extends ApiGetter {

    private static SVSUAPIGetter instance;


    public static SVSUAPIGetter getInstance() {
        if (instance == null) {
            instance = new SVSUAPIGetter();
        }
        return instance;
    }

    private SVSUAPIGetter() {

    }

    public void clearData(boolean clearAll) {

        if (clearAll) {
            TermModel.getTerms().clear();
            PrefixModel.getPrefixes().clear();
        }

        BuildingModel.getBuildings().clear();
        CourseModel.getCourses().clear();
        InstructorsModel.getInstructors().clear();
        SectionModel.getSections().clear();
    }

    public void getCourseData(
            final ProgressBar progressBar,
            final TermModel term,
            final UIThreadSyncCallback callback
    ) {
        if (term == null) {
            callback.onUIException("getCourseData()", "TermModel:term is null");
            return;
        }

        if (!term.isEmpty()) {
            callback.onUIFinished();

        } else {

            if (progressBar != null) {
                progressBar.setMax(PrefixModel.getPrefixes().size() - 1);
                progressBar.setProgress(0);
            }

            new Thread(new Runnable() {
                public void run() {

                    Iterator it = PrefixModel.getPrefixes().entrySet().iterator();
                    int z = 0;
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();

                        PrefixModel tm = (PrefixModel) me.getValue();

                        String p = String.format("courses?term=%s&prefix=%s", term.getKey(), tm.getKey());

                        if (progressBar != null) {
                            progressBar.setProgress(++z);
                        }
                        //if (z < 8) {
                            try {

                                String result = getSynchronous(p);
                                JSONObject json = JSONAssistant.create(result);
                                extractCourses(json);
                                //callback.onUIResponce(p, result);
                            } catch (Exception e) {
                                callback.onUIException(p, e.getMessage());
                            }
                       // }  else {
                       //     break;
                       // }
                    }
                    callback.onUIFinished();

                }
            }).start();
        }
    }

    public void getCourseData(
            final UIThreadSyncCallback callback,
            final String term,
            final String ... prefix) {

        if (prefix == null)
            return;

        final String[]  api_string = new String[prefix.length];

        for(int i = 0; i<prefix.length;i++) {
            api_string[i] = String.format("courses?term=%s&prefix=%s", term, prefix[i]);
        }

        getItems(
                new UIThreadSyncCallback() {
                    @Override
                    protected void onException(String source, String message) {

                    }

                    @Override
                    protected void onResponce(String source, String result) {
                        JSONObject json = JSONAssistant.create(result);
                        extractCourses(json);
                    }

                    @Override
                    protected void onFinished() {
                        if (callback!=null) callback.onUIFinished();
                    }

                    @Override  public void onUIFinished() {
                        //getAllCourses(progressBar, callback);
                        super.onUIFinished();
                    }
                },
                null,
                api_string
        );

    }

    public void getBasics(
            final ProgressBar progressBar,
            final UIThreadSyncCallback callback) {

        getItems(
            new UIThreadSyncCallback() {
                @Override
                protected void onException(String source, String message) {

                }

                @Override
                protected void onResponce(String source, String result) {
                    if (source.equalsIgnoreCase("prefixes")) {
                        PrefixModel.populatePrefixes(JSONAssistant.create(result));
                    } else if (source.equalsIgnoreCase("terms")) {
                        TermModel.populateTerms(JSONAssistant.create(result));
                    }
                }

                @Override
                protected void onFinished() {
                    if (callback!=null) callback.onUIFinished();
                }

                @Override  public void onUIFinished() {
                    //getAllCourses(progressBar, callback);
                    super.onUIFinished();
                }
            },
            progressBar,
            "prefixes",
            "terms"
        );
    }

    public SVSUAPIGetter setHost(String value) {
        HOST = value;
        return SVSUAPIGetter.this;
    }

    private void extractCourses(JSONObject json) {
        if (json == null)
            return;
        JSONArray courses = JSONAssistant.getArray(json, "courses");

        if (courses != null) {
            for(int i = 0; i < courses.length(); i++) {
                JSONObject course = JSONAssistant.getArrayObject(courses, i);

                JSONArray instructors = JSONAssistant.getArray(course, "instructors");
                JSONArray meetingTimes = JSONAssistant.getArray(course, "meetingTimes");

                InstructorsModel.populateInstructors(instructors, false);
                CourseModel.addCourse(course, false);
                BuildingModel.addBuilding(course);
            }



            for(int i = 0; i < courses.length(); i++) {
                JSONObject course = JSONAssistant.getArrayObject(courses, i);

                try {
                    SectionModel.addMeetings(course);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            //Log.e("SVSU", CrossReference.getCourseAndInstructors().toString());
        }

    }

}
