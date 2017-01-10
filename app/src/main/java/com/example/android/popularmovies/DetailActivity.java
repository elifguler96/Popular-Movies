package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView mMovieThumbnail;
    private TextView mMovieTitle;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mOverview = (TextView) findViewById(R.id.tv_overview);

        Intent intent = getIntent();

        if(intent != null){
            if(intent.hasExtra("original_title")){
                mMovieTitle.setText(intent.getStringExtra("original_title"));
            }

            if(intent.hasExtra("overview")){
                mOverview.setText(intent.getStringExtra("overview"));
            }

            if(intent.hasExtra("vote_average")){
                mUserRating.setText("Rating: " + intent.getStringExtra("vote_average"));
            }

            if(intent.hasExtra("release_date")){
                mReleaseDate.setText("Release Date: " + intent.getStringExtra("release_date"));
            }

            if(intent.hasExtra("poster_path")){
                Uri posterUri = Uri.parse("http://image.tmdb.org/t/p/w185/" + intent.getStringExtra("poster_path"));

                Picasso.with(this).load(posterUri).into(mMovieThumbnail);
            }
        }
    }
}
