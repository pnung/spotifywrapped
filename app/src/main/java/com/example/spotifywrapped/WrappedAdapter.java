package com.example.spotifywrapped;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped.spotifyAPI.data.Artist;
import com.example.spotifywrapped.spotifyAPI.data.Song;
import com.example.spotifywrapped.spotifyAPI.data.Wrap;

import java.util.List;

public class WrappedAdapter extends RecyclerView.Adapter<WrappedAdapter.WrappedViewHolder> {
    private final List<Wrap> wrapsList;
    private final Context context;

    public WrappedAdapter(Context context, List<Wrap> wrapsList) {
        this.context = context;
        this.wrapsList = wrapsList;
    }

    @NonNull
    @Override
    public WrappedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapped_card_layout, parent, false);
        return new WrappedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WrappedViewHolder holder, int position) {
        Wrap wrap = wrapsList.get(position);
        holder.bind(wrap);
    }

    @Override
    public int getItemCount() {
        return wrapsList.size();
    }

    class WrappedViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTopSongs, textViewTopGenres;
        RecyclerView recyclerViewTopArtists;

        WrappedViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewTopArtists = itemView.findViewById(R.id.recyclerViewTopArtists);
            textViewTopSongs = itemView.findViewById(R.id.textViewTopSongs);
            textViewTopGenres = itemView.findViewById(R.id.textViewTopGenres);
        }

        void bind(Wrap wrap) {
            if (wrap.getTopArtists() != null && !wrap.getTopArtists().isEmpty()) {
                recyclerViewTopArtists.setVisibility(View.VISIBLE);
                recyclerViewTopArtists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                ArtistAdapter artistAdapter = new ArtistAdapter(wrap.getTopArtists());
                recyclerViewTopArtists.setAdapter(artistAdapter);
            } else {
                recyclerViewTopArtists.setVisibility(View.GONE);
            }

            if (wrap.getTopSongs() != null && !wrap.getTopSongs().isEmpty()) {
                StringBuilder songsBuilder = new StringBuilder("Top Songs: ");
                for (int i = 0; i < Math.min(wrap.getTopSongs().size(), 5); i++) {
                    if (i > 0) songsBuilder.append(", ");
                    songsBuilder.append(wrap.getTopSongs().get(i).getName());
                }
                textViewTopSongs.setText(songsBuilder.toString());
            } else {
                textViewTopSongs.setText("No top songs");
            }

            if (wrap.getTopGenres() != null && !wrap.getTopGenres().isEmpty()) {
                StringBuilder genresBuilder = new StringBuilder("Top Genres: ");
                for (int i = 0; i < Math.min(wrap.getTopGenres().size(), 5); i++) {
                    if (i > 0) genresBuilder.append(", ");
                    genresBuilder.append(wrap.getTopGenres().get(i));
                }
                textViewTopGenres.setText(genresBuilder.toString());
            } else {
                textViewTopGenres.setText("No top genres");
            }
        }
    }
}
