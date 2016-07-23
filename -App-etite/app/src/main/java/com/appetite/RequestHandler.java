package com.appetite;

/**
 * Created by Kavi on 3/8/16.
 */import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sneha on 3/4/2016.
 */
public class RequestHandler  extends AsyncTask<String,Void,String> {
    private ChefUsersMapActivity mMapsActivity;

    public RequestHandler(ChefUsersMapActivity maps) {
        super();
        mMapsActivity = maps;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return requestUrl((String) params[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String result) {

        mMapsActivity.requestCompletedCallBack(result);
    }

    private String requestUrl(String url) throws IOException {
        InputStream in = null;

        try {
            URL newUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("DEBUG_TAG", "The response is: " + response);
            in = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(in, conn.getContentLength());
            Log.d("response", contentAsString);
            return contentAsString;

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}