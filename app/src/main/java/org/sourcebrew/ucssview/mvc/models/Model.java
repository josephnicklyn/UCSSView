package org.sourcebrew.ucssview.mvc.models;

import org.json.JSONObject;
import org.sourcebrew.ucssview.mvc.JSONAssistant;

/**
 * Created by John on 1/6/2018.
 */

public abstract class Model {

    public final String key;
    public final String value;
    public int modelIndex = 0;

    public Model(JSONObject json, String key, String value) throws NoSuchFieldException {

        this.key = JSONAssistant.getString(json, key, "");
        this.value = JSONAssistant.getString(json, value, "");

        if (key.isEmpty())
            throw new NoSuchFieldException("new Model(): Missing key[" + key + "].");
        if (value.isEmpty())
            throw new NoSuchFieldException("new Model(): Missing value[" + value + "].");
    }

    public Model(JSONObject json, String keyA, String keyB, String value) throws NoSuchFieldException {
        this.key = JSONAssistant.getString(json, keyA, "") + "-" + JSONAssistant.getString(json, keyB, "");;
        this.value = JSONAssistant.getString(json, value, "");

        if (key.isEmpty())
            throw new NoSuchFieldException("new Model(): Missing key[" + key + "].");
        if (value.isEmpty())
            throw new NoSuchFieldException("new Model(): Missing value[" + value + "].");
    }

    public Model(String key, String value) {

        this.key = key;
        this.value = value;
    }

    public final String getKey() {
        return key;
    }

    public final String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " - " + value;
    }

    public void setModelIndex(int value) {
        modelIndex = value;
    }

    public int getModelIndex(){
        return modelIndex;
    }


}
