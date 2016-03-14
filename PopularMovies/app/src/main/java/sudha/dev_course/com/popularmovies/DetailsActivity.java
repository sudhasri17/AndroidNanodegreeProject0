package sudha.dev_course.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sudharshi on 28/12/15.
 */
public class DetailsActivity extends AppCompatActivity
{
    private JSONObject mJsonData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            mJsonData = new JSONObject(getIntent().getStringExtra("data"));


        //bind view and data
        if (mJsonData != null)
        {
            String posterUrl = APIRequest.getPosterBaseUrl() + "w500" + mJsonData.getString("poster_path");
            Picasso.with(DetailsActivity.this).load(posterUrl).into((ImageView)findViewById(R.id.movie_poster));
            ((TextView)findViewById(R.id.title)).setText(mJsonData.getString("original_title"));
            ((TextView)findViewById(R.id.rating)).setText("User Rating : " + mJsonData.getString("vote_average"));
            ((TextView)findViewById(R.id.release)).setText("Releasde Date : " + mJsonData.getString("release_date"));
            ((TextView)findViewById(R.id.synopsis)).setText(mJsonData.getString("overview"));

            setTitle(mJsonData.getString("original_title"));

        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
