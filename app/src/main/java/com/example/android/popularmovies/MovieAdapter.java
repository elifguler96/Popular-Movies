package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieAdapter  extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private final MovieAdapterOnClickHandler mClickHandler;
    private JSONObject[] mMovieData;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setMovieJSONData(JSONObject[] movieJSONData){
        mMovieData = movieJSONData;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        JSONObject jsonMovieObject = mMovieData[position];

        String posterPath;

        try{
            posterPath = jsonMovieObject.getString("poster_path");

            Picasso.with(holder.mMoviePoster.getContext()).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(holder.mMoviePoster);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mMovieData == null)
            return 0;

        return mMovieData.length;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mMoviePoster;

        public MovieAdapterViewHolder(View view){
            super(view);

            mMoviePoster = (ImageView) view.findViewById(R.id.iv_movie_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick(mMovieData[adapterPosition]);
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(JSONObject movie);
    }
}
