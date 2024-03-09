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


        //assign button stuff
        //delete button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(emailEntry.getText().toString(), passwordEntry.getText().toString());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return to the main activity when hitting back (aka cancelling the delete)
                startActivity(new Intent(DeleteUserActivity.this, MainActivity.class));
            }
        });
    }

    private void deleteUser(String email, String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        if (user != null) {
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                                startActivity(new Intent(DeleteUserActivity.this, MainActivity.class));
                                Toast.makeText(DeleteUserActivity.this, "Deleted User.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}