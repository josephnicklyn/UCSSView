package org.sourcebrew.ucssview.mvc.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by John on 1/6/2018.
 */

public class ModelMap<E extends Model> extends TreeMap<String, E> {

    private ArrayList<ModelMapChangeInterface<E>> listeners;

    @Override
    public E put(String key, E value) {

        value.setModelIndex(super.size());
        E model = super.put(key, value);

        if (listeners != null) {
            ModelMapChangeInterface<E>[] list = copyListeners();

            if (list != null) {
                for (ModelMapChangeInterface<E> l : list) {
                     try {
                        l.added(value);
                    } catch (Exception e) {
                        removeModelMapChangeListener(l);
                    }
                }
            }
        }
        return model;
    }

    public void completedOperation() {
        if (listeners != null) {
            ModelMapChangeInterface<E>[] list = copyListeners();

            if (list != null) {
                for (ModelMapChangeInterface<E> l : list) {
                    try {
                        l.completed();
                    } catch (Exception e) {
                        removeModelMapChangeListener(l);
                    }
                }
            }
        }
    }

    public E put(E value) {
        if (value != null) {
          return put(value.getKey(), value);
        } else {
            return null;
        }
    }


    @Override
    public E remove(Object key) {

        E model = super.remove(key);

        if (listeners != null) {
            ModelMapChangeInterface<E>[] list = copyListeners();

            if (list != null) {
                for (ModelMapChangeInterface<E> l : list) {
                    try {
                        l.removed(model);
                    } catch (Exception e) {
                        removeModelMapChangeListener(l);
                    }
                }
            }
        }

        return model;
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends E> m) {
        // not used
    }

    @Override
    public void clear() {
        if (listeners != null) {
            ModelMapChangeInterface<E>[] list = copyListeners();
            if (list != null) {
                for (ModelMapChangeInterface<E> l : list) {
                    try {
                        l.cleared();
                    } catch (Exception e) {
                        removeModelMapChangeListener(l);
                    }
                }
            }
        }

        super.clear();
    }

    public void addModelMapChangeListener(ModelMapChangeInterface listener) {
        if (getListeners().contains(listener))
            return;
        getListeners().add(listener);
    }

    public void removeModelMapChangeListener(ModelMapChangeInterface listener) {
        if (listeners != null) {
            getListeners().remove(listener);
            if (listeners.isEmpty()) {
                listeners = null;
            }
        }
    }

    public void removeAllListeners() {
        if (listeners != null) {
            listeners.clear();
            listeners = null;
        }
    }

    private ArrayList<ModelMapChangeInterface<E>> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        return listeners;
    }

    public ModelMapChangeInterface<E>[] copyListeners()  {
        if (listeners == null)
            return null;

        return getListeners().toArray(new ModelMapChangeInterface[listeners.size()]);
    }
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        Iterator it = entrySet().iterator();

        while(it.hasNext()) {
            Entry me = (Entry)it.next();
            String s = me.getValue().toString();
            if (!s.isEmpty()) {
                if (b.length() != 0) b.append("\n");
                b.append(s);
            }
        }

        return b.toString();
    }
}
