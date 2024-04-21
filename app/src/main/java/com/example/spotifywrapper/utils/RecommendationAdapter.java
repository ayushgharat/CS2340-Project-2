package com.example.spotifywrapper.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapper.R;
import com.example.spotifywrapper.model.Wrapped;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private JsonArray localDataSet;
    private OnClickListener onClickListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_track, tv_artist;
        private ImageView iv_photo;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tv_track = (TextView) view.findViewById(R.id.tv_recommendation_track);
            tv_artist = view.findViewById(R.id.tv_recommendation_artist);
            iv_photo = view.findViewById(R.id.iv_recommendation_photo);
        }

        public ImageView getIv_photo() {
            return iv_photo;
        }

        public TextView getTv_artist() {
            return tv_artist;
        }

        public TextView getTv_track() {
            return tv_track;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public RecommendationAdapter(JsonArray dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recommendation_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTv_artist().setText(localDataSet.get(position).getAsJsonObject().get("artist").getAsString());
        viewHolder.getTv_track().setText(localDataSet.get(position).getAsJsonObject().get("name").getAsString());
        Picasso.get().load(localDataSet.get(position).getAsJsonObject().get("url").getAsString()).into(viewHolder.getIv_photo());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, localDataSet.get(position));
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, JsonElement element);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}