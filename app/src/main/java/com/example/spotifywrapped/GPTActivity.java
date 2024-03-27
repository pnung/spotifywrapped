package com.example.spotifywrapped;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.databinding.ActivityGptactivityBinding;
//import com.example.spotifywrapped.databinding.FragmentChatGPTBinding;
import com.example.spotifywrapped.Chat.ChatGPTActivity;
import com.example.spotifywrapped.Chat.Message;
import com.example.spotifywrapped.Chat.MessageAdapter;

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

public class GPTActivity extends AppCompatActivity {

    private ActivityGptactivityBinding binding;
    private static final String TAG = "GPTActivityClassTag";
    RecyclerView recyclerView;
    TextView welcomeText;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGptactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeText.setVisibility(View.GONE);
        });

    }

   private void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    private void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callAPI(String q) {
        JSONObject jsonB = new JSONObject();
        try {
            Log.d(TAG, "JSON B PUT");

            jsonB.put("model","gpt-3.5-turbo");

            JSONObject systemSettings = new JSONObject();
            systemSettings.put("role", "system")
                    .put("content", "You are a helpful assistant");

            JSONObject userSettings = new JSONObject();
            userSettings.put("role", "user")
                    .put("content", q);

            JSONArray array = new JSONArray();
            array.put(systemSettings)
                    .put(userSettings);

            jsonB.put("messages", array);
            jsonB.put("max_tokens", 100);
            jsonB.put("temperature", 1);
            System.out.println(jsonB); // working
        } catch (JSONException e) {
            Log.w(TAG, "JSON B PUT ERROR", e);
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(jsonB.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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") //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
                .post(body)
                .build();
        Log.d(TAG, "REQUEST BODY");
        Log.d(TAG, request.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "failed to load response 1");
                addResponse("Failed to load the response");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Log.d(TAG, "failed to load response 2");
                    System.out.println(response);
                    System.out.println(response.isSuccessful());
                    addResponse("Failed to load the response");
                }
            }
        });

    }
}