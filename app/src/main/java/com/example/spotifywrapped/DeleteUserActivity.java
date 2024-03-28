package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotifywrapped.databinding.ActivityDeleteUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

public class DeleteUserActivity extends AppCompatActivity {
    private static final String TAG = "DeleteUserActivityClassTag";
    private ActivityDeleteUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeleteUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //delete button - calls the delete user function when pressed, using the info in the text fields
        //todo: change to confirm button sequence (ie submit -> are you sure y/n? -> function

        binding.deleteUserLayoutSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(binding.deleteUserLayoutEmailAddress.getText().toString(), binding.deleteUserLayoutPassword.getText().toString());
            }
        });

        //back button - returns to the main activity when pressed
        binding.deleteUserLayoutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return to the main activity when hitting back (aka cancelling the delete)
                startActivity(new Intent(DeleteUserActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * Function to delete a user from FireBase. Takes in an email and a password, checks them
     * against the user email/password
     * @param email     the email from the text field
     * @param password  the password from the text field
     */
    private void deleteUser(String email, String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  //reference to the current user

        AuthCredential credential = EmailAuthProvider.getCredential(email, password); //get email/password from text fields and create credential object

        if (user != null) { //if there is a user
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {  //reauthenticate takes the gathered email/password and compares them against the users to reauth user
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //if the user is successfully reauth'ed, delete them.
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {    //delete the user account
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {  //successful reauth. delete user
                                    Log.d(TAG, "Deleted user account.");    //update log

                                    //Force user to sign out and go to the login activity
                                    sign_out_helper();

                                    Toast.makeText(DeleteUserActivity.this, "Deleted account!", Toast.LENGTH_SHORT).show();    //toast to tell user that user was deleted
                                } else {    //task not successfully completed
                                    Log.w(TAG, "User account was not deleted.", task.getException()); //log the tag, short message, generated exception
                                    Toast.makeText(DeleteUserActivity.this, "User Not Deleted. Please Try Again.", Toast.LENGTH_SHORT).show();    //toast to tell user that user was deleted


                                    Exception error = task.getException(); //get the exception

                                    if (error == null) {
                                        Log.w(TAG, "task.getException() was null");   //log class tag, a short message, and what the exception was
                                        Toast.makeText(DeleteUserActivity.this, "Failed to delete account. Please try again", Toast.LENGTH_SHORT).show();
                                    } else if (error instanceof FirebaseAuthInvalidUserException) { //user account is disabled, deleted, or credentials changed on another device
                                        Log.w(TAG, ((FirebaseAuthInvalidUserException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                                        Toast.makeText(DeleteUserActivity.this, "Your account status appears to have changed.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(DeleteUserActivity.this, "Please log in again", Toast.LENGTH_SHORT).show();

                                        //force user sign out and start login activity
                                        sign_out_helper();
                                    } else if (error instanceof FirebaseAuthRecentLoginRequiredException) {
                                        Log.w(TAG, ((FirebaseAuthRecentLoginRequiredException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                                        Toast.makeText(DeleteUserActivity.this, "Your account has not logged in recently.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(DeleteUserActivity.this, "Please log in again", Toast.LENGTH_SHORT).show();

                                        //force user sign out and start login activity
                                        sign_out_helper();
                                    } else {
                                        Toast.makeText(DeleteUserActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(DeleteUserActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    } else { //failed to reauth user
                        Exception error = task.getException();  //put the exception into a variable

                        //determine what the error is and respond accordingly
                        if (error == null) {
                            Log.w(TAG, "task.getException() was null");   //log class tag, a short message, and what the exception was
                            Toast.makeText(DeleteUserActivity.this, "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();

                        } else if (error instanceof FirebaseAuthInvalidCredentialsException) { //something about the credential is wrong
                            Log.w(TAG, ((FirebaseAuthInvalidCredentialsException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(DeleteUserActivity.this, "Failed to verify your account information.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(DeleteUserActivity.this, "Are you sure your login information is correct?", Toast.LENGTH_SHORT).show();

                        } else if (error instanceof FirebaseAuthInvalidUserException) { //user account is disabled, deleted, or credentials changed on another device
                            Log.w(TAG, ((FirebaseAuthInvalidUserException) error).getErrorCode(), error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(DeleteUserActivity.this, "Your account status appears to have changed.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(DeleteUserActivity.this, "Please log in again", Toast.LENGTH_SHORT).show();

                            //force user sign out and start log in activity
                            sign_out_helper();
                        } else {
                            Log.w(TAG, error);   //log class tag, the error code, and what the exception was
                            Toast.makeText(DeleteUserActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(DeleteUserActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        } else { //user is null
            Log.w(TAG, "Attempted to delete user. CurrentUser was null");
            Toast.makeText(DeleteUserActivity.this, "Your account appears to be signed out.", Toast.LENGTH_SHORT).show();
            Toast.makeText(DeleteUserActivity.this, "Please sign in again.", Toast.LENGTH_SHORT).show();

            //sign out and go to login
            sign_out_helper();
        }
    }

    /**\
     * Helper method to sign out user and force them to go the login page
     */
    private void sign_out_helper() {
        //force the user to sign out
        FirebaseAuth.getInstance().signOut();

        //go to the login activity
        startActivity(new Intent(DeleteUserActivity.this, LoginCreateAccountActivity.class));
    }
}