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

    private EditText emailEntry;
    private EditText passwordEntry;
    private Button createAccountPageButton;
    private Button loginPageButton;
    private Button submitAccountDetailsButton;
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
        emailEntry = binding.emailPassLayoutEmailAddress;       //reference to the email entry field
        passwordEntry = binding.emailPassLayoutPassword;        //reference to the password entry field

        createAccountPageButton = binding.emailPassLayoutCreateAccountPageButton;   //reference to the button that sets the page to create account mode
        loginPageButton = binding.emailPassLayoutLoginPageButton;                   //reference to the button that sets the page to login to account mode
        submitAccountDetailsButton = binding.emailPassLayoutSubmitButton;           //reference to the button that submits details for account creation/login

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

        submitAccountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = pageStateTextView.getText().toString();
                if (state.equals(getResources().getString(R.string.Creation_Page_State))) {
                    createNewAccount(emailEntry.getText().toString(), passwordEntry.getText().toString());
                } else if (state.equals(getResources().getString(R.string.LogIn_Page_State))) {
                    signIn(emailEntry.getText().toString(), passwordEntry.getText().toString());
                } else {
                    CharSequence errorMessage = "An error occurred. Please try again.";
                    Toast errorPopUp = Toast.makeText(LoginCreateAccountActivity.this, errorMessage, Toast.LENGTH_LONG);
                    errorPopUp.show();
                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //sign in success
                    Log.d(TAG, "signInWithEmail:success");

                    //go back to main activity
                    returnToMainActivity();
                } else {
                    //if sign in fails, display error message
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginCreateAccountActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
                    //if sign in fails, display error message
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(LoginCreateAccountActivity.this, "Authenticationfailed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        //check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { //user is signed in
            //go back to main
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