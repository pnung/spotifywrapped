package com.example.spotifywrapped.Chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.Chat.Message;
import com.example.spotifywrapped.Chat.MessageAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.databinding.ActivityChatgptBinding;
import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatGPTActivity extends AppCompatActivity {
    private ActivityChatgptBinding binding;
    RecyclerView recyclerView;
    TextView welcomeText;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChatgptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgpt);
        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        welcomeText = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.sendBtn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            Toast.makeText(this,question,Toast.LENGTH_LONG).show();
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeText.setVisibility(View.GONE);
        });

        /* HELP HERE
        BottomNavigationView navView = findViewById(R.id.navigation_chatGPT);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        */

    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callAPI(String q) {
        JSONObject jsonB = new JSONObject();
        try {
            jsonB.put("model","gpt-3.5-turbo");
            jsonB.put("prompt", q);
            jsonB.put("max_tokens", 4000);
            jsonB.put("temperature", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(jsonB.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load the response");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    addResponse("Failed to load the response");
                }
            }
        });

    }


}