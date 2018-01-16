package org.sourcebrew.ucssview.mvc.models;

/**
 * Created by John on 1/6/2018.
 */

public interface ModelMapChangeInterface<E> {

    public void added(E model);

    public void removed(E model);

    public void cleared();

    public void completed();

}
