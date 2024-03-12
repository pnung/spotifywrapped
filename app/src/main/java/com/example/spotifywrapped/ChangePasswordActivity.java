package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotifywrapped.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivityClassTag";
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //setup password length warning for new password. shows a toast if new password length < 6
        binding.changePasswordLayoutNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.changePasswordLayoutNewPassword.getText().length() < 6) {
                    Toast toast = Toast.makeText(ChangePasswordActivity.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //submit button
        //TODO: setup confirmation chain (ie submit -> are you sure y/n? -> change password)
        binding.changePasswordLayoutSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call function to change password
                changePassword(binding.changePasswordLayoutEmailAddress.getText().toString(),
                        binding.changePasswordLayoutOldPassword.getText().toString(),
                        binding.changePasswordLayoutNewPassword.getText().toString());
            }
        });

        binding.changePasswordLayoutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return to main activity when hitting back button
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
            }
        });
    }

    /**
     * Function to change the password of a user. takes in email and old password for authentication, and a new password.
     * @param email the user's email
     * @param oldPassword   the users old password
     * @param newPassword   the users new password
     */
    private void changePassword(String email, String oldPassword, String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  //reference to current user

        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);    //get account credential for verification

        if (user != null) { //user signed in
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() { //validate text field input against user credential to reauth and allow password change
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {  //password changed
                                Log.d(TAG, "User password successfully changed.");  //log tag, short message on success

                                //return to main activity
                                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));

                                //make toast to tell user password changed successfully
                                Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                            } else {    //password failed to change
                                Log.w(TAG, "User password change failed.", task.getException());    //log tag, short message, generated exception

                                //tell user password change failed
                                Toast.makeText(ChangePasswordActivity.this, "Password Change Failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else { //user was null. return to main activity, which will force user to sign in activity
            Log.w(TAG, "Attempted to change user password. CurrentUser was null.");

            //return to main activity, which will force the user to the sign in (user was null)
            startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
        }
    }
}