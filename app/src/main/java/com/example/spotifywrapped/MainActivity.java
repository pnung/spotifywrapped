package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;

import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityClassTag";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            //user is authorized logic

            //debugging: displays email of signed in user
            binding.textView.setText(user.getEmail());
        } else {
            startActivity(new Intent(this, EmailPasswordFirebaseAuthActivity.class));
        }
    }


    /**
     * Function to sign out the current user
     */
    public void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    /**
     * Function to delete a user. starts a new activity and asks user to input info to confirm. //TODO actually test if delete works properly lol
     */
    public void deleteUser() {
        startActivity(new Intent(MainActivity.this, DeleteUserActivity.class));
    }


    /**
     * //TODO Function to change a user's email
     */
    public void changeEmail() {

    }


    /**
     * //TODO Function to change a user's name (i think this is a thing)
     */
    public void changeName() {

    }

    /**
     * //TODO Function to change a user's password
     */
    public void changePassword() {

    }


}