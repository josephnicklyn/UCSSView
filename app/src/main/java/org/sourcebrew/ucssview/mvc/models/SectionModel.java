package org.sourcebrew.ucssview.mvc.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by John on 1/6/2018.
 */

public class SectionModel extends Model {

    private static ModelMap<SectionModel> sections;
    private ModelMap<SectionMeetingsModel> sectionMeetings;

    private final CourseModel courseModel;
    private final TermModel termModel;

    public SectionModel(String lineNumber,
                        CourseModel forCourse,
                        TermModel forTerm) throws NoSuchFieldException {
        super(lineNumber, forCourse.getCourseNumber());

        if (forTerm == null)
            throw new NoSuchFieldException("Terms need to be created before sections and section meetings");

        if (forCourse == null)
            throw new NoSuchFieldException("Courses need to be created before sections and section meetings");

        this.termModel = forTerm;
        this.courseModel = forCourse;
        forTerm.addSection(SectionModel.this);
        getSections().put(SectionModel.this);
    }

    public final String getLineNumber() {
        return super.getKey();
    }

    public final String getCourseNumber() {
        return super.getValue();
    }

    public final CourseModel getCourseModel() {
        return courseModel;
    }

    public final TermModel getTermModel() {
        return termModel;
    }

    public static ModelMap<SectionModel> getSections() {

        if (sections == null) {
            sections = new ModelMap<>();
        }
        return sections;
    }

    public ModelMap<SectionMeetingsModel> getMeetings() {

        if (sectionMeetings == null) {
            sectionMeetings = new ModelMap<>();
        }
        return sectionMeetings;
    }

    public static void clearSections() {
        //getSections().clear();
    }

    public static int addMeetings(JSONObject obj) throws NoSuchFieldException {
        int c = 0;

        if (obj == null)
            return c;
        JSONArray arr = JSONAssistant.getArray(obj, "meetingTimes");

        String location = JSONAssistant.getString(obj, "location");
        String courseNumber = JSONAssistant.getString(obj, "courseNumber");

        String term = JSONAssistant.getString(obj, "term");
        String lineNumber = JSONAssistant.getString(obj, "lineNumber");

        String prefix = JSONAssistant.getString(obj, "prefix");

        String courseKey = prefix + "-" + courseNumber;

        CourseModel courseModel = CourseModel.getCourses().get(courseKey);
        TermModel termModel = TermModel.getTerms().get(term);


        if (courseModel == null || termModel == null)
            return c;

        SectionModel sectionModel = getSections().get(lineNumber+term);

        if (sectionModel == null) {
            sectionModel = new SectionModel(
                    lineNumber+term,
                    courseModel,
                    termModel
                    );
        }

        if (arr != null) {
            for(int i = 0; i < arr.length(); i++) {


                JSONObject meeting = JSONAssistant.getArrayObject(arr, i);

                String eventKey = String.format("%s-%d", lineNumber, (i+1));

                sectionModel.addSectionMeeting(meeting, eventKey, courseKey);
            }
        }

        return c;
    }

    public void addSectionMeeting(JSONObject json, String eventKey, String courseKey) {
        if (json == null)
            return;

        new SectionMeetingsModel(json, eventKey, courseKey);

    }

    public class SectionMeetingsModel extends Model {
        private String
                method,
                startTime,
                endTime,
                days;

        int iStartTime, iEndTime;

        BuildingModel.RoomModel roomModel;
        InstructorsModel instructorsModel;

        public SectionMeetingsModel(JSONObject json,
                              String eventKey,
                              String courseKey) {
            super(eventKey, courseKey);
            method = JSONAssistant.getString(json, "method");
            startTime = JSONAssistant.getString(json, "startTime");
            endTime = JSONAssistant.getString(json, "endTime");
            days = JSONAssistant.getString(json, "days");

            iStartTime = timeStringToInt(startTime);
            iEndTime = timeStringToInt(endTime);
            roomModel = BuildingModel.getRoom(
                JSONAssistant.getString(json, "building"),
                JSONAssistant.getString(json, "room")
            );

            instructorsModel = InstructorsModel.getInstructors().get(
                JSONAssistant.getString(json, "instructor")
            );
            getMeetings().put(SectionMeetingsModel.this);

        }

        public String getDays() {
            return days;
        }

        private int timeStringToInt(String v) {

            if (v.isEmpty()) {
                return -1;
            }
            int plus = v.endsWith("PM")?720:0;
            int h = toInt(v.substring(0, 2));
            if (h == 12) h = 0;
            h*=60;
            int m = toInt(v.substring(2, 5));

            return (h + m + plus);
        }

        public String getInstructor() {
            if (instructorsModel != null)
                return instructorsModel.toString();
            else
                return "????";
        }

        private int toInt(String s) {
            int r = 0;

            for(char c: s.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    r*=10;
                    r+=((int)(c-'0'));
                }
            }

            return r;
        }

        public final String getMethod() {
            return method;
        }

        public final String getStartTime() {
            return startTime;
        }

        public int toMinutesStartTime() {
            return iStartTime;
        }

        public final String getEndTime() {
            return endTime;
        }

        public final int toMinutesDuration() {
            return iEndTime-iStartTime;
        }

        public final int toMinutesEndTime() {
            return iEndTime;
        }

        public BuildingModel.RoomModel getRoomModel() {
            return roomModel;
        }

        public InstructorsModel getInstructorsModel() {
            return instructorsModel;
        }

        @Override
        public String toString() {
            return getCourseModel().getPrefix() + ":" + getCourseModel().getCourseNumber();
            //+ "[" + getCourseModel().getPrefixModelIndex() + "]";
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("Section: ").append(getLineNumber()).append(": ").append(getCourseModel().toString()).append(" {\n");

        Iterator it = getMeetings().entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            String s = me.toString();
            if (!s.isEmpty()) {
                if (b.length() != 0) b.append("\n");
                b.append("\t").append(s);
            }
        }

        b.append("\n}");


        return b.toString();
    }

}

