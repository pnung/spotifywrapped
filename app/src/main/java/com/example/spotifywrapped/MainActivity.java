package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.example.spotifywrapped.spotifyAPI.APIHandler;
import com.example.spotifywrapped.spotifyAPI.RequestParser;
import com.example.spotifywrapped.spotifyAPI.data.Wrap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityClassTag";
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance("https://spotifywrapped-ef844-default-rtdb.firebaseio.com/");
        myRef = database.getReference();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigationView();
        handleUserAuthentication();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void handleUserAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Firebase user is not null. UID: " + user.getUid());
            binding.textView.setText(user.getEmail());
            // Set up button listeners
            setupButtonListeners();
            // Check Spotify Authentication
            checkSpotifyAuthentication();
        } else {
            startActivity(new Intent(this, LoginCreateAccountActivity.class));
            finish();
        }
    }

    private void setupButtonListeners() {
        binding.MainActivityLayoutDeleteTest.setOnClickListener(v -> deleteUser());
        binding.MainActivityLayoutSignOut.setOnClickListener(v -> signOut());
        binding.MainActivityLayoutChangeEmail.setOnClickListener(v -> changeEmail());
        binding.MainActivityLayoutChangePassword.setOnClickListener(v -> changePassword());
        binding.btnCreateWrapped.setOnClickListener(v -> createNewWrapped());
    }

    private void checkSpotifyAuthentication() {
        if (!APIHandler.isSpotifyAuthenticated()) {
            APIHandler.getToken(this);
        }
    }

    private void createNewWrapped() {
        if (!APIHandler.isSpotifyAuthenticated()) {
            Log.w(TAG, "Spotify is not authenticated. Attempting to authenticate.");
            APIHandler.getToken(this);
            return;
        }

        RequestParser.songsRequest(this, topSongsList -> {
            RequestParser.topArtistsRequest(this, topArtistsList -> {
                RequestParser.fetchTopGenresBasedOnTopArtists(this, topGenresList -> {
                    Wrap newWrap = new Wrap(topSongsList, topArtistsList, topGenresList); // Adjust as per the constructor
                    storeWrappedInfoInFirebase(newWrap);
                });
            });
        });
    }


    private void storeWrappedInfoInFirebase(Wrap wrap) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference wrapsRef = myRef.child("Users").child(firebaseUser.getUid()).child("Wraps");
            String wrapId = wrapsRef.push().getKey(); // Generate a unique ID for the wrap
            wrap.setId(wrapId); // Now set the id
            wrapsRef.child(wrapId).setValue(wrap)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully stored wrap with ID: " + wrapId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store wrap", e));
        }
    }

    private void fetchWrapsFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid()).child("Wraps");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Wrap> wraps = new ArrayList<>();
                    for (DataSnapshot wrapSnapshot : dataSnapshot.getChildren()) {
                        wraps.add(wrapSnapshot.getValue(Wrap.class));
                    }
                    updateUIWithWraps(wraps);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void updateUIWithWraps(List<Wrap> wraps) {
        // Update your RecyclerView adapter with the list of wraps
    }


    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginCreateAccountActivity.class));
        finish();
    }

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");
                    startActivity(new Intent(MainActivity.this, LoginCreateAccountActivity.class));
                    finish();
                }
            });
        }
    }

    private void changeEmail() {
        // This would lead to an activity or a dialog where the user can change their email.
        Log.d(TAG, "Change Email Clicked");
        // Implementation details will vary based on how you have set up the email change functionality.
    }

    private void changePassword() {
        // This would lead to an activity or a dialog where the user can change their password.
        Log.d(TAG, "Change Password Clicked");
        // Implementation details will vary based on how you have set up the password change functionality.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        APIHandler.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APIHandler.AUTH_TOKEN_REQUEST_CODE) {
            if (APIHandler.isSpotifyAuthenticated()) {
                Log.d(TAG, "Spotify authentication successful.");
                // Here you might want to initiate fetching the user's Spotify data
            } else {
                Log.e(TAG, "Spotify authentication failed.");
            }
        }
    }
}
