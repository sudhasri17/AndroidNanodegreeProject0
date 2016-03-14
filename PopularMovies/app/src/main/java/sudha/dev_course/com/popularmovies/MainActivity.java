package sudha.dev_course.com.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MovieGridAdapter mMovieGridAdapter;
    private JSONArray mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView movieGrid = (GridView) findViewById(R.id.gridView);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                    detailsIntent.putExtra("data", mMovies.getJSONObject(position).toString());
                    startActivity(detailsIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        if (mMovies == null)
        {
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String sortOrder = preference.getString(getString(R.string.pref_sort_key), "1");
            String endUrl = getString(R.string.popular_movie_url);
            if (sortOrder.equals("1"))
            {
                endUrl = getString(R.string.popular_movie_url);
            }
            else
            {
                endUrl = getString(R.string.high_rated_movie_url);
            }
            APIRequest.fetchMovieList(MainActivity.this, endUrl, new APIRequest.ResponseHandler() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null)
                    {
                        try
                        {
                            mMovies = response.getJSONArray("results");
                            JSONObject movie;
                            ArrayList<String> posters = new ArrayList<String>();
                            for (int i = 0; i < mMovies.length(); i++)
                            {
                                movie = mMovies.getJSONObject(i);
                                posters.add(APIRequest.getPosterBaseUrl() + "w342" +movie.getString("poster_path"));
                            }

                            mMovieGridAdapter = new MovieGridAdapter(posters);
                            GridView movieGrid = (GridView) findViewById(R.id.gridView);
                            movieGrid.setAdapter(mMovieGridAdapter);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public class MovieGridAdapter extends BaseAdapter
    {
        private ArrayList<String> mPosters;

        public MovieGridAdapter(ArrayList posters)
        {
            mPosters = posters;
        }

        public void setPosters(ArrayList<String> mPosters)
        {
            this.mPosters = mPosters;
        }

        @Override
        public int getCount() {
            if (mPosters != null)
            {
                return mPosters.size();
            }
            return 0;
        }

        @Override
        public String getItem(int position) {
            return mPosters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = new ImageView(MainActivity.this);
            }
            Picasso.with(MainActivity.this).load(getItem(position)).into((ImageView) convertView);
            ((ImageView) convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            return convertView;
        }
    }
}
