package org.sourcebrew.ucssview.network;

import android.widget.ProgressBar;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by John on 1/3/2018.
 */

public class ApiGetter {


    public String HOST = null;


    protected ApiGetter() {

    }

    /**
     * Synchronous allows multiple calls on the same worker thread, whence the call back can be
     * utilized from a single call.
     *
     * @param content: the path of the content to get
     * @return a string of the response
     * @throws IOException
     * @throws NullPointerException
     */
    protected String getSynchronous(String content) throws IOException, NullPointerException {
        OkHttpClient client = new OkHttpClient();

        if (HOST == null)
            throw new NullPointerException("Host has not be set.");

        String url = HOST + content;

        final Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;

        response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * A convenience method to grab multiple sources of data from a single call
     *
     * @param callback, the listener for the response
     * @param progressBar, (optional), a progress bar used to display the current progress
     * @param path, 1 or more paths to be appended to the HOST
     */
    public void getItems(
            final UIThreadSyncCallback callback,
            final ProgressBar progressBar,
            final String ... path) {

        new Thread(new Runnable() {
            public void run() {
                if (progressBar != null) {
                    progressBar.setMax(path.length - 1);
                    progressBar.setProgress(0);
                }
                int max = 0;
                for(String p: path) {
                    try {
                        if (progressBar != null) {
                            progressBar.setProgress(max++);
                        }

                        String source = p;
                        String result = getSynchronous(p);
                        callback.onUIResponce(source, result);
                    } catch (Exception e) {
                        callback.onUIException(p, e.getMessage());
                    }
                }
                callback.onUIFinished();
            }
        }).start();
    }

    protected ApiGetter setHost(String value) {
        HOST = value;
        return ApiGetter.this;
    }



}
