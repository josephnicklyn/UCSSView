package org.sourcebrew.ucssview.network;

import android.os.Handler;
import android.os.Looper;

/**
 * This class allows a connection/response to a server be reported on the UI thread.
 *
 * CAUTION: this class must be called from a Networking thread which was intialized from the
 * UI thread.
 *
 * Created by John on 1/3/2018.
 */

public abstract class UIThreadSyncCallback {

    protected void onException(final String source, final String message) {};

    protected void onResponce(final String source, final String result) {};

    protected void onFinished() {}

    public void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    public final void onUIResponce(final String source, final String result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onResponce(source, result);
            }
        });

    }

    public final void onUIException(final String source, final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onException(source, message);
            }
        });

    }

    public void onUIFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onFinished();
            }
        });
    }

}
