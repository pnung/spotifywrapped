package com.example.spotifywrapped.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.spotifywrapped.spotifyAPI.data.WrappedInfo;

import java.util.StringJoiner;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        fetchWrappedInfo();

        return root;
    }

    private void fetchWrappedInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid()).child("WrappedInfo");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    WrappedInfo wrappedInfo = dataSnapshot.getValue(WrappedInfo.class);
                    if (wrappedInfo != null) {
                        updateUIWithWrappedInfo(wrappedInfo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log error or show error message
                }
            });
        }
    }

    private void updateUIWithWrappedInfo(WrappedInfo wrappedInfo) {
        if (getView() == null) return;

        TextView textView = binding.textHome;

        StringJoiner topArtistsJoiner = new StringJoiner(", ");
        for (String artist : wrappedInfo.topArtists) {
            topArtistsJoiner.add(artist);
        }

        StringJoiner topSongsJoiner = new StringJoiner(", ");
        for (String song : wrappedInfo.topSongs) {
            topSongsJoiner.add(song);
        }

        StringJoiner topGenresJoiner = new StringJoiner(", ");
        for (String genre : wrappedInfo.topGenres) {
            topGenresJoiner.add(genre);
        }

        String text = "Top Artists: " + topArtistsJoiner.toString() + "\n" +
                "Top Songs: " + topSongsJoiner.toString() + "\n" +
                "Top Genres: " + topGenresJoiner.toString();

        textView.setText(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
