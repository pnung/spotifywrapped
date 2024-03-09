package com.example.spotifywrapped;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.databinding.ActivityEmailPasswordFirebaseAuthBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordFirebaseAuth extends AppCompatActivity {

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

//        EdgeToEdge.enable(this); //makes the content draw behind the system bars
//        setContentView(R.layout.activity_email_password_firebase_auth);

        //idk what this code does
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

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


        //button setups
        createAccountPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStateTextView.setText(R.string.Creation_Page_State);
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        //check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { //user is signed in
            //go do some other activity
        }
    }
}



/*TODO: create a quick layout to get user sign in info. have separate activities for login, sign up, and logged in. follow video/guide for methods needed. have login screen be first, and have check for if user already signed in in there.
 */