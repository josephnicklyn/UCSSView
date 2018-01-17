package org.sourcebrew.ucssview.mvc.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by John on 1/6/2018.
 */

public class BuildingModel extends Model {

    private static ModelMap<BuildingModel> buildings;
    private ModelMap<RoomModel> rooms;

    public BuildingModel(String building,
                         String location) throws NoSuchFieldException {
        super(building, location);
        getBuildings().put(BuildingModel.this);
    }

    public String getBuilding() {
        return super.getKey();
    }

    public String getLocation() {
        return super.getValue();
    }

    public static ModelMap<BuildingModel> getBuildings() {

        if (buildings == null) {
            buildings = new ModelMap<>();
        }
        return buildings;
    }

    public ModelMap<RoomModel> getRooms() {

        if (rooms == null) {
            rooms = new ModelMap<>();
        }
        return rooms;
    }

    public static void clearBuildings() {
        //getBuildings().clear();
    }

    public static int addBuilding(JSONObject obj) {
        int c = 0;

        if (obj == null)
            return c;
        JSONArray arr = JSONAssistant.getArray(obj, "meetingTimes");
        String location = JSONAssistant.getString(obj, "location");

        if (arr != null) {
            for(int i = 0; i < arr.length(); i++) {

                JSONObject meeting = JSONAssistant.getArrayObject(arr, i);
                String building = JSONAssistant.getString(meeting, "building");
                String room = JSONAssistant.getString(meeting, "room");

                BuildingModel buildingModel = getBuildings().get(building);



                if (buildingModel == null) {
                    try {
                        buildingModel = new BuildingModel(building, location);
                        c++;
                    } catch (NoSuchFieldException e) {

                    }
                }

                if (buildingModel != null) {
                    buildingModel.addRoom(room);
                }


            }
        }
        return c;
    }

    public RoomModel addRoom(String room) {
        if (getRooms().get(room) == null) {
            new RoomModel(room);
        }
        return getRooms().get(room);
    }

    public static RoomModel getRoom(String building, String room) {
        BuildingModel buildingModel = getBuildings().get(building);
        if (buildingModel!=null) {
            return buildingModel.getRooms().get(room);
        }
        return null;
    }

    public class RoomModel extends Model {
        private BuildingModel buildingModel;
        public RoomModel(String room) {
            super(room, BuildingModel.this.getKey());
            getRooms().put(RoomModel.this);
        }

        public BuildingModel getBuilding() {
            return BuildingModel.this;
        }

        public String getRoom() {
            return getValue();
        }

        @Override
        public String toString() {
            return getKey();
        }
    }

    @Override
    public String toString() {
        return getBuilding();
        /*
        StringBuilder b = new StringBuilder();

        b.append("Building: ").append(getBuilding()).append(" @").append(getLocation()).append(" {\n");

        Iterator it = getRooms().entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            String s = me.getKey().toString();
            if (!s.isEmpty()) {
                if (b.length() != 0) b.append("\n");
                b.append("\t").append(s);
            }
        }

        b.append("\n}");


        return b.toString();
        */
    }

}
