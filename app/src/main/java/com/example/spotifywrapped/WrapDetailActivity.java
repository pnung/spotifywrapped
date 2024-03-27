package com.example.spotifywrapped;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped.spotifyAPI.data.Artist;
import com.example.spotifywrapped.spotifyAPI.data.Song;
import com.example.spotifywrapped.spotifyAPI.data.Wrap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.util.List;
import java.util.StringJoiner;

public class WrapDetailActivity extends AppCompatActivity {

    private static final String TAG = "WrapDetailActivity";

    // UI components
    private TextView tvWrapTitle, tvTopSongsLabel, tvTopArtistsLabel, tvTopGenresLabel, artistNameTextView;
    private ImageView artistImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_detail);

        // Initialize UI components
        tvWrapTitle = findViewById(R.id.tvWrapTitle);
        tvTopSongsLabel = findViewById(R.id.tvTopSongsLabel);
        tvTopArtistsLabel = findViewById(R.id.tvTopArtistsLabel);
        tvTopGenresLabel = findViewById(R.id.tvTopGenresLabel);
        artistImageView = findViewById(R.id.artistImageView);
        artistNameTextView = findViewById(R.id.artistNameTextView);

        // Get the wrap ID from the intent
        String wrapId = getIntent().getStringExtra("wrap_id");
        if (wrapId != null) {
            getWrapById(wrapId, this::displayWrapDetails);
        }
    }

    private void getWrapById(String wrapId, final WrapCallback callback) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference wrapsRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseUser.getUid()).child("Wraps").child(wrapId);

            wrapsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Wrap wrap = dataSnapshot.getValue(Wrap.class);
                    if (wrap != null) {
                        callback.onCallback(wrap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read wrap", databaseError.toException());
                }
            });
        }
    }

    private void displayWrapDetails(Wrap wrap) {
        // Setting the title for the wrap
        tvWrapTitle.setText(String.format("Wrap Details for %s", wrap.getId()));

        // Concatenate and set top artists
        StringJoiner artistsJoiner = new StringJoiner(", ");
        for (Artist artist : wrap.getTopArtists()) {
            artistsJoiner.add(artist.getName());
        }
        tvTopArtistsLabel.setText("Top Artists: " + artistsJoiner.toString());

        // Concatenate and set top songs
        StringJoiner songsJoiner = new StringJoiner(", ");
        for (Song song : wrap.getTopSongs()) {
            songsJoiner.add(song.getName());
        }
        tvTopSongsLabel.setText("Top Songs: " + songsJoiner.toString());

        // Concatenate and set top genres
        StringJoiner genresJoiner = new StringJoiner(", ");
        for (String genre : wrap.getTopGenres()) {
            genresJoiner.add(genre);
        }
        tvTopGenresLabel.setText("Top Genres: " + genresJoiner.toString());
    }

    public interface WrapCallback {
        void onCallback(Wrap wrap);
    }
}
