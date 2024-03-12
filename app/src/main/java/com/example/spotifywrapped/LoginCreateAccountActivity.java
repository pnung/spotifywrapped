package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.databinding.ActivityEmailPasswordFirebaseAuthBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCreateAccountActivity extends AppCompatActivity {

    //tag for logging information
    private static final String TAG = "EmailPasswordFirebaseAuthActivityClassTag";

    private FirebaseAuth mAuth;

//    private EditText emailEntry;
//    private EditText passwordEntry;
    private TextView pageStateTextView;

    //binding to reference views/things in layout
    private ActivityEmailPasswordFirebaseAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign the binding reference and set the view of the screen
        binding = ActivityEmailPasswordFirebaseAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //assign references to views in the xml
        EditText emailEntry = binding.emailPassLayoutEmailAddress;       //reference to the email entry field
        EditText passwordEntry = binding.emailPassLayoutPassword;        //reference to the password entry field

        Button createAccountPageButton = binding.emailPassLayoutCreateAccountPageButton;   //reference to the button that sets the page to create account mode
        Button loginPageButton = binding.emailPassLayoutLoginPageButton;                   //reference to the button that sets the page to login to account mode
        Button submitAccountDetailsButton = binding.emailPassLayoutSubmitButton;           //reference to the button that submits details for account creation/login

        pageStateTextView = binding.emailPassLayoutPageState;                       //reference to the textview that indicates the state of the page

        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();



        //setup click listener to tell users that passwords must be at least 6 characters when they are creating an account
        binding.emailPassLayoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageStateTextView.getText().toString().equals(getResources().getString(R.string.Creation_Page_State))) {
                    if (binding.emailPassLayoutPassword.getText().length() < 6) {
                        Toast toast = Toast.makeText(LoginCreateAccountActivity.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });


        //button setups

        //clicking this button changes the page to the create account state
        createAccountPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStateTextView.setText(R.string.Creation_Page_State);
            }
        });

        //clicking this button changes the page to the login state
        loginPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pageStateTextView.setText(R.string.LogIn_Page_State);
            }
        });

        //clicking this button submits the text in the text fields to the appropriate function
        submitAccountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = pageStateTextView.getText().toString();
                if (state.equals(getResources().getString(R.string.Creation_Page_State))) {
                    createNewAccount(emailEntry.getText().toString(), passwordEntry.getText().toString());
                } else if (state.equals(getResources().getString(R.string.LogIn_Page_State))) {
                    signIn(emailEntry.getText().toString(), passwordEntry.getText().toString());
                } else {
                    //tell the user that an illegal state occurred and to try again.
                    Toast.makeText(LoginCreateAccountActivity.this, "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Function to sign a user in
     * @param email the user email
     * @param password  the user password
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //sign in success
                    Log.d(TAG, "signInWithEmail:success");

                    //go back to main activity
                    returnToMainActivity();
                } else { //task failed
                    //log the tag, short message, generated exception
                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                    //tell the user that authentication failed
                    authFailedToast();
                }
            }
        });
    }

    /**
     * Function to create a new account
     * @param email the email to be used
     * @param password  the password to be used
     */
    private void createNewAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //sign in success
                    Log.d(TAG, "createUserWithEmail:success");  //send the info to logcat, basically

                    //go back to main activity since creating account also signs in
                    returnToMainActivity();

                } else {
                    //log the tag, short message, exception
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

                    //tell the user that account authentication
                    authFailedToast(); //show a failure toast
                }
            }
        });
    }

    /**
     * Function to make a toast that tells the user that authentication failed.
     */
    private void authFailedToast() {
        Toast.makeText(LoginCreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        //check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { //user is signed in
            //go back to main activity
            returnToMainActivity();
        }
    }

    /**
     * Helper method to return to the main activity
     */
    private void returnToMainActivity() {
        startActivity(new Intent(LoginCreateAccountActivity.this, MainActivity.class));
    }
}