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
import com.google.firebase.auth.FirebaseUser;

public class DeleteUserActivity extends AppCompatActivity {
    private static final String TAG = "DeleteUserActivityClassTag";
    private ActivityDeleteUserBinding binding;

    private EditText emailEntry;
    private EditText passwordEntry;
    private Button submitButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDeleteUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //delete button - calls the delete user function when pressed, using the info in the text fields
        //todo: change to confirm button sequence (ie submit -> are you sure y/n? -> function

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(emailEntry.getText().toString(), passwordEntry.getText().toString());
            }
        });

        //back button - returns to the main activity when pressed
        backButton.setOnClickListener(new View.OnClickListener() {
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
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {  //successful reauth. delete user
                                Log.d(TAG, "User account deleted.");    //update log
                                startActivity(new Intent(DeleteUserActivity.this, MainActivity.class));     //return to main activity
                                Toast.makeText(DeleteUserActivity.this, "Deleted User.", Toast.LENGTH_SHORT).show();    //toast to tell user that user was deleted
                            } else {    //task not successfully completed
                                Log.w(TAG, "User account was not deleted.");
                                Toast.makeText(DeleteUserActivity.this, "User Not Deleted. Please Try Again.", Toast.LENGTH_SHORT).show();    //toast to tell user that user was deleted
                            }
                        }
                    });
                }
            });
        } else { //user is null
            Log.w(TAG, "Attempted to delete user. CurrentUser was null");
            startActivity(new Intent(DeleteUserActivity.this, MainActivity.class));     //return to main activity
        }
    }
}