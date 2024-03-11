package com.example.spotifywrapped.ui.Chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.FragmentChatGPTBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatGPT extends AppCompatActivity {
   /*MY BINDING IS MESSING UP AND INFLATE IS FUCKED UP HELP */
   private FragmentChatGPTBinding binding;
    RecyclerView recyclerView;
    TextView welcomeText;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;

    protected void onCreate(Bundle savedInstanceState) {
        binding = FragmentChatGPTBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);

        binding = ChatGPT.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.fragment_chat_g_p_t);
        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        welcomeText = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.sendBtn);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            Toast.makeText(this, question, Toast.LENGTH_LONG).show();
        });

    }

    private static FragmentChatGPTBinding inflate(LayoutInflater layoutInflater) {
        return null;
    }

}