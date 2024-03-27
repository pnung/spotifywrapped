package com.example.spotifywrapped.ui.home;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.spotifywrapped.WrappedAdapter;
import com.example.spotifywrapped.databinding.FragmentHomeBinding;
import com.example.spotifywrapped.spotifyAPI.data.Artist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.StringJoiner;
import com.example.spotifywrapped.spotifyAPI.data.Wrap;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Binding object for accessing the fragment's views.
    private FragmentHomeBinding binding;
    // List to store wrap objects fetched from Firebase.
    private List<Wrap> wrapsList = new ArrayList<>();
    // Adapter for the RecyclerView to display the list of wraps.
    private WrappedAdapter wrapAdapter;

    // onCreateView is called to draw the UI for the fragment when it's created.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ViewModel is fetched here if needed for data handling. Not used directly in this snippet.
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate the layout for this fragment using data binding.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the RecyclerView with a grid layout manager and the wrapAdapter.
        wrapAdapter = new WrappedAdapter(getContext(), wrapsList);
        binding.recyclerViewWraps.setAdapter(wrapAdapter);
        // GridLayoutManager is used to layout items in a grid. Here, we specify 2 columns.
        binding.recyclerViewWraps.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Fetch wrap data from Firebase and populate the RecyclerView.
        fetchWraps();

        return root;
    }

    // fetchWraps connects to Firebase to retrieve wraps data specific to the logged-in user.
    private void fetchWraps() {
        // Get the current user from FirebaseAuth.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Reference to the user's wraps in Firebase Database.
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid()).child("Wraps");

            // Add a listener to fetch or update data anytime there's a change in the database.
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear existing data to avoid duplicates.
                    wrapsList.clear();
                    // Loop through the snapshots of each wrap and add them to the list.
                    for (DataSnapshot wrapSnapshot : dataSnapshot.getChildren()) {
                        Wrap wrap = wrapSnapshot.getValue(Wrap.class);
                        if (wrap != null) {
                            wrapsList.add(wrap);
                        }
                    }
                    // Notify the adapter that data has changed so it can update the UI.
                    wrapAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Log a warning if the database operation was cancelled.
                    Log.w(TAG, "loadWrap:onCancelled", databaseError.toException());
                }
            });
        }
    }

    // onDestroyView is called when the view hierarchy associated with the fragment is being removed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding object to avoid memory leaks.
        binding = null;
    }
}