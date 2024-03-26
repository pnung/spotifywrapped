package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {
    private static final String TAG = "ChangeEmailActivityClassTag";    //identifier for logging/debugging
    private com.example.spotifywrapped.databinding.ActivityChangeEmailBinding binding;  //reference for view hierarchy in the layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign binding and tell screen to render the layout
        binding = com.example.spotifywrapped.databinding.ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //submit button - submits the details to change the user email
        //todo: change to confirm button sequence (ie submit -> are you sure y/n? -> function
        binding.changeEmailLayoutSubmitButton.setOnClickListener(new View.OnClickListener() {   //set click listener
            @Override
            public void onClick(View v) {   //on click, call the change email function
                changeEmail(binding.changeEmailLayoutOldEmailAddress.getText().toString(),
                        binding.changeEmailLayoutNewEmailAddress.getText().toString(),
                        binding.changeEmailLayoutPassword.getText().toString()
                );
            }
        });

        //back button - returns to the main activity when pressed
        binding.changeEmailLayoutBackButton.setOnClickListener(new View.OnClickListener() { //set click listener
            @Override
            public void onClick(View v) {
                //return to the main activity when hitting back (aka cancelling the change)
                startActivity(new Intent(ChangeEmailActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * Function to change the email account of a user.
     * @param oldEmail  the old email of the user. retrieved from old email text field. used for credentials
     * @param newEmail  the new email of the user. retrieved from the new email text field.
     * @param password  the password of the user. retrieved from the password text field. used for credentials
     */
    private void changeEmail(String oldEmail, String newEmail, String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  //reference to current user

        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);    //get user's credential for verifying the input data

        if (user != null) {
            //calls reauthenticate function on user, using the input data from the text fields. this
            //is required since this is a security-sensitive operation, so the user must be recently
            //"logged in". it will essentially confirm the user is who they say they are, or fail the
            //change operation if the info is a mismatch
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, ((Boolean) task.isSuccessful()).toString());

                    //if the user is successfully verified
                    if (task.isSuccessful()) {
                        //the method that actually changes the email. sends a verification email to the old email.
                        //this verification must be done before the change takes effect, meaning the old email
                        //can be used as long as the verification link is not used
                        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {  //verification email sent / email changed successfully
                                    Log.d(TAG, "User email successfully changed.");

                                    //create a toast telling the user about the email verification
                                    Toast.makeText(ChangeEmailActivity.this, "Email Changed Successfully.", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(ChangeEmailActivity.this, "Before you can sign in with it, you must verify your email.", Toast.LENGTH_LONG).show();
                                    Toast.makeText(ChangeEmailActivity.this, "Until then, be aware that your old email will still work.", Toast.LENGTH_LONG).show();

                                    //force the user to sign out
                                    FirebaseAuth.getInstance().signOut();

                                    //go to the login activity
                                    startActivity(new Intent(ChangeEmailActivity.this, LoginCreateAccountActivity.class));
                                } else {    //something about the process failed.
                                    Log.w(TAG, task.getException());   //log class tag, the error code, and what the exception was
                                    Toast.makeText(ChangeEmailActivity.this, "Email Change Failed.", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(ChangeEmailActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Exception error = task.getException();  //put the exception into a variable

                        //determine what the error is and respond accordingly
                        if (error == null) {
                            Log.w(TAG, "task.getException() was null");   //log class tag, a short message, and what the exception was
                            Toast.makeText(ChangeEmailActivity.this, "Email Change Failed.", Toast.LENGTH_SHORT).show();

                        } else if (error instanceof FirebaseAuthInvalidCredentialsException) { //something about the credential is wrong
                            Log.w(TAG, ((FirebaseAuthInvalidCredentialsException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(ChangeEmailActivity.this, "Failed to verify user.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ChangeEmailActivity.this, "Are you sure your login information is correct?", Toast.LENGTH_SHORT).show();

                        } else if (error instanceof FirebaseAuthInvalidUserException) { //user account is disabled, deleted, or credentials changed on another device
                            Log.w(TAG, ((FirebaseAuthInvalidUserException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(ChangeEmailActivity.this, "Failed to verify user.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ChangeEmailActivity.this, "Are you sure your login information is correct?", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(ChangeEmailActivity.this, "Email Change Failed.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ChangeEmailActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {    //user was null. return to main activity, which at time of writing will force user to sign in activity
            Log.w(TAG, "Attempted to change user email. CurrentUser was null"); //log class tag, short message
            startActivity(new Intent(ChangeEmailActivity.this, MainActivity.class)); //return to main activity
        }

    }
}