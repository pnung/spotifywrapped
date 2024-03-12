package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.spotifywrapped.spotifyAPI.APIHandler;
import com.example.spotifywrapped.databinding.ActivityMainBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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

        if(FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        database = FirebaseDatabase.getInstance("https://spotifywrapped-ef844-default-rtdb.firebaseio.com/");
        myRef = database.getReference();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        //TODO decide if we want to force users to the sign in screen before doing anything or if we want to default to not that but have logic that still forces a sign in before doing anything
        //rn the code below forces the user to the login screen if not signed in

        //check if user is authorized
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(TAG, "Firebase user is not null. UID: " + user.getUid());

            //user is authorized logic

            // Check user has logged in with Spotify before, if not send to Spotify login page
            checkSpotifyAuthentication();



            //debugging: displays email of signed in user
            binding.textView.setText(user.getEmail());

            //testing purposes: delete/signout/change email/change password
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
        }

     else { //user is not signed in so go to the login/create account page
        startActivity(new Intent(this, LoginCreateAccountActivity.class));
    }
}

    private void checkSpotifyAuthentication() {
        if (!APIHandler.isSpotifyAuthenticated()) {
            // No valid Spotify access token exists, so start the Spotify authentication process.
            APIHandler.getToken(this); // Directly initiate Spotify login.
        }
    }

//    private void storeTestValueInFirebase() {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (firebaseUser != null) {
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//            DatabaseReference usersRef = databaseReference.child("Users");
//
//            // Use the Firebase user's UID as the key for the user-specific data
//            String testValue = "Test Spotify Login Success";
//            usersRef.child(firebaseUser.getUid()).child("TestValue").setValue(testValue)
//                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully wrote test value to database"))
//                    .addOnFailureListener(e -> Log.e(TAG, "Failed to write test value to database", e));
//        } else {
//            Log.e(TAG, "FirebaseUser is null. Cannot write test value to database.");
//        }
//    }

    private void fetchAndStoreSpotifyID() {
        System.out.println("Fetching and storing");
        Log.d(TAG, "Fetching and storing Spotify ID"); // Log at start of method
        APIHandler.fetchSpotifyUserProfile(this, jsonResponse -> {
            try {
                Log.d(TAG, "Complete JSON Response: " + jsonResponse.toString());
                String spotifyID = jsonResponse.getString("id");
                Log.d(TAG, "Fetched Spotify ID: " + spotifyID); // Log the fetched Spotify ID
                // Now store this ID in Firebase
                storeSpotifyIDInFirebase(spotifyID);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to fetch Spotify ID", e); // Log if there's an issue fetching the ID
            }
        });
    }


    private void storeSpotifyIDInFirebase(String spotifyID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "Storing Spotify ID: " + spotifyID + " for user: " + firebaseUser.getUid());
            DatabaseReference usersRef = myRef.child("Users");
            System.out.println(usersRef);
            usersRef.child(firebaseUser.getUid()).child("SpotifyID").setValue(spotifyID)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully stored Spotify ID"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store Spotify ID", e));
        } else {
            Log.e(TAG, "FirebaseUser is null. Cannot store Spotify ID.");
        }
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        APIHandler.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APIHandler.AUTH_TOKEN_REQUEST_CODE) {
            if (APIHandler.isSpotifyAuthenticated()) {
                fetchAndStoreSpotifyID();
            } else {
                System.out.println("NOT AUTHENTICATED");
            }
        }
    }


    /**
     * Function to sign out the current user
     */
    public void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginCreateAccountActivity.class));
        }
    }

    /**
     * Function to delete a user. starts a new activity and asks user to input info to confirm. //TODO actually test if delete works properly lol
     */
    public void deleteUser() {
        startActivity(new Intent(MainActivity.this, DeleteUserActivity.class));
    }


    /**
     * //Function to change a user's email. starts a new activity and asks user to input info to confirm.
     */
    public void changeEmail() {
        startActivity(new Intent(MainActivity.this, ChangeEmailActivity.class));
    }


//    /**
//     * //TODO Function to change a user's name (not high priority + probably need to ask for name during account creation, which i am not doing yet)
//     */
//    public void changeName() {
//
//    }

    /**
     * //TODO Function to change a user's password
     */
    public void changePassword() {
        startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
    }


}