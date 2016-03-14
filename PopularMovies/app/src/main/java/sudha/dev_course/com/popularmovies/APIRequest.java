package sudha.dev_course.com.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class APIRequest
{
    public final static String BASE_URL = "http://api.themoviedb.org/3";
    private static Context mContext;
    private final static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    public static String appendAPIkey() {
        String api_key = "?api_key=";
        return api_key +  mContext.getResources().getString(R.string.api_key);
    }

    public static String getPosterBaseUrl() {
        return POSTER_BASE_URL;
    }

    public interface ResponseHandler
    {
        public void onResponse(JSONObject response);
    }

    public static void fetchMovieList(Context context, String endUrl, ResponseHandler handler)
    {
        mContext = context;
        Uri uri = Uri.parse(BASE_URL + endUrl).buildUpon()
                .appendQueryParameter("api_key", mContext.getResources().getString(R.string.api_key))
                .build();

        APIFetchTask fetchTask = new APIFetchTask(handler);
        fetchTask.execute(uri.toString());
    }

    private static class APIFetchTask extends AsyncTask<String, Void, String>
    {
        private ResponseHandler mResponseHandler;

        public APIFetchTask(ResponseHandler handler) {
            mResponseHandler = handler;
        }

        @Override
        protected String doInBackground(String... params)
        {
            BufferedReader bufferedReader = null;
            HttpURLConnection urlConnection = null;
            StringBuffer buffer = new StringBuffer();
            String line = null;

            try
            {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null)
                {
                    buffer = null;
                }
                InputStreamReader reader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(reader);

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }

            } catch (IOException e) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result != null)
            {
                try
                {
                    JSONObject response = new JSONObject(result);
                    if (mResponseHandler != null)
                    {
                        mResponseHandler.onResponse(response);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }
}
