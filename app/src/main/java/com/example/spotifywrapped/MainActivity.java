package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.example.spotifywrapped.spotifyAPI.APIHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityClassTag";
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        database = FirebaseDatabase.getInstance("https://spotifywrapped-ef844-default-rtdb.firebaseio.com/");
        myRef = database.getReference();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Firebase user is not null. UID: " + user.getUid());
            checkSpotifyAuthentication();
            binding.textView.setText(user.getEmail());

            binding.MainActivityLayoutDeleteTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser();
                }
            });

            binding.MainActivityLayoutSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });

            binding.MainActivityLayoutChangeEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeEmail();
                }
            });

            binding.MainActivityLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                }
            });
        } else {
            startActivity(new Intent(this, LoginCreateAccountActivity.class));
        }
    }

    private void checkSpotifyAuthentication() {
        if (!APIHandler.isSpotifyAuthenticated()) {
            APIHandler.getToken(this);
        }
    }

    private void fetchAndStoreSpotifyID() {
        Log.d(TAG, "Fetching and storing Spotify ID");
        APIHandler.fetchSpotifyUserProfile(this, jsonResponse -> {
            try {
                String spotifyID = jsonResponse.getString("id");
                Log.d(TAG, "Fetched Spotify ID: " + spotifyID);
                storeSpotifyIDInFirebase(spotifyID);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to fetch Spotify ID", e);
            }
        });
    }

    // Pass in JSON key that you would like to store
    private void fetchAndStoreData(String data) {
        Log.d(TAG, "Fetching and storing data");
        APIHandler.fetchSpotifyUserProfile(this, jsonResponse -> {
            try {
                String spotifyID = jsonResponse.getString(data);
                Log.d(TAG, "Fetched data: " + data);
                storeSpotifyIDInFirebase(data);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to fetch data", e);
            }
        });
    }

    private void storeDataInFireBase(String data) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "Storing data: " + data + " for user: " + firebaseUser.getUid());
            DatabaseReference usersRef = myRef.child("Users");
            usersRef.child(firebaseUser.getUid()).child(data).setValue(data)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully stored data"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store data", e));
        } else {
            Log.e(TAG, "FirebaseUser is null. Cannot store data.");
        }
    }

    private void storeSpotifyIDInFirebase(String spotifyID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "Storing Spotify ID: " + spotifyID + " for user: " + firebaseUser.getUid());
            DatabaseReference usersRef = myRef.child("Users");
            usersRef.child(firebaseUser.getUid()).child("SpotifyID").setValue(spotifyID)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully stored Spotify ID"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store Spotify ID", e));
        } else {
            Log.e(TAG, "FirebaseUser is null. Cannot store Spotify ID.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        APIHandler.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APIHandler.AUTH_TOKEN_REQUEST_CODE) {
            if (APIHandler.isSpotifyAuthenticated()) {
                fetchAndStoreSpotifyID();
            } else {
                Log.d(TAG, "Spotify authentication failed.");
            }
        }
    }


        /**
         * Function to sign out the current user
         */
        public void signOut () {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginCreateAccountActivity.class));
            }
        }

        /**
         * Function to delete a user. starts a new activity and asks
         * user to input info to confirm.
         */
        public void deleteUser () {
            startActivity(new Intent(MainActivity.this, DeleteUserActivity.class));
        }


        /**
         * Function to change a user's email. starts a new activity
         * and asks user to input info to confirm.
         */
        public void changeEmail () {
            startActivity(new Intent(MainActivity.this, ChangeEmailActivity.class));
        }


    //    /**
    //     * TODO Function to change a user's name (not high priority + probably need to ask for name during account creation, which i am not doing yet)
    //     */
    //    public void changeName() {
    //
    //    }

        /**
         * Function to change a user's password. starts a new activity
         * and asks user to input relevant info
         */
        public void changePassword () {
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }
    }
