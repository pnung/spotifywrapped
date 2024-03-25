package com.example.spotifywrapped;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spotifywrapped.spotifyAPI.data.WrappedInfo;
import java.util.List;
import java.util.StringJoiner;

public class WrappedAdapter extends RecyclerView.Adapter<WrappedAdapter.WrappedViewHolder> {
    private final List<WrappedInfo> wrappedInfoList;

    public WrappedAdapter(List<WrappedInfo> wrappedInfoList) {
        this.wrappedInfoList = wrappedInfoList;
    }

    @NonNull
    @Override
    public WrappedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapped_card_layout, parent, false);
        return new WrappedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WrappedViewHolder holder, int position) {
        WrappedInfo wrappedInfo = wrappedInfoList.get(position);
        // Bind data to views
        holder.textViewTopArtist.setText("Top Artist: " + wrappedInfo.topArtists.get(0));
        holder.textViewTopSong.setText("Top Song: " + wrappedInfo.topSongs.get(0));

        // Join genres list into a single string
        StringJoiner joiner = new StringJoiner(", ");
        for (String genre : wrappedInfo.topGenres) {
            joiner.add(genre);
        }
        holder.textViewTopGenres.setText("Top Genres: " + joiner.toString());
    }

    @Override
    public int getItemCount() {
        return wrappedInfoList.size();
    }

    static class WrappedViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTopArtist, textViewTopSong, textViewTopGenres;

        WrappedViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTopArtist = itemView.findViewById(R.id.textViewTopArtist);
            textViewTopSong = itemView.findViewById(R.id.textViewTopSong);
            textViewTopGenres = itemView.findViewById(R.id.textViewTopGenres);
        }
    }
}
