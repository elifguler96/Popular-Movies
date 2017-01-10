package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_display);
        mMovieAdapter = new MovieAdapter(this);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieData("popular");
    }

    private void loadMovieData(String sortOrder){
        showMovieDataView();

        new FetchMovieTask().execute(sortOrder);
    }

    private void showMovieDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(JSONObject movie) {
        Intent intent = new Intent(this, DetailActivity.class);

        try{
            intent.putExtra("original_title", movie.getString("original_title"));
            intent.putExtra("poster_path", movie.getString("poster_path"));
            intent.putExtra("overview", movie.getString("overview"));
            intent.putExtra("vote_average", movie.getString("vote_average"));
            intent.putExtra("release_date", movie.getString("release_date"));
        } catch (JSONException e){
            e.printStackTrace();
        }

        startActivity(intent);

    }

    public class FetchMovieTask extends AsyncTask<String, Void, JSONObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject[] doInBackground(String... strings) {
            if(strings.length == 0)
                return null;

            JSONObject[] result = null;
            String sortOrder = strings[0];

            URL movieRequestUrl = null;

            try{
                movieRequestUrl = new URL(Uri.parse("https://api.themoviedb.org/3/movie/" + sortOrder + "?api_key=" + BuildConfig.API_KEY).buildUpon().build().toString());
                Log.d(TAG, movieRequestUrl.toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
            }

            try{
                String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);

                JSONObject jsonMovieObject = new JSONObject(jsonMovieResponse);

                JSONArray movieArray = jsonMovieObject.getJSONArray("results");

                result = new JSONObject[movieArray.length()];

                for(int i=0; i<movieArray.length(); i++){
                    result[i] = movieArray.getJSONObject(i);
                }

                return result;

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject[] jsonObjects) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(jsonObjects != null){
                showMovieDataView();
                mMovieAdapter.setMovieJSONData(jsonObjects);
            } else{
                showErrorMessage();
            }
        }

        public String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_sort_popularity:
                mMovieAdapter.setMovieJSONData(null);
                loadMovieData("popular");
                return true;

            case R.id.action_sort_rate:
                mMovieAdapter.setMovieJSONData(null);
                loadMovieData("top_rated");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
