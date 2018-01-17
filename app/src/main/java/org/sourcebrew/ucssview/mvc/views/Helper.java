package org.sourcebrew.ucssview.mvc.views;

/**
 * Created by John on 1/16/2018.
 */

public class Helper {

    public static int between(int v0, int va, int vb) {
        if (va < vb) {
            int t = vb;
            vb = va;
            va = t;
        }

        if (v0 > va) v0 = va;
        if (v0 < vb) v0 = vb;

        return v0;
    }
}
