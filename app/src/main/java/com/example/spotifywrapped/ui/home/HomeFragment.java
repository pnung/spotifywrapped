package com.example.spotifywrapped.ui.home;

import static com.example.spotifywrapped.spotifyAPI.APIHandler.makeRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.databinding.FragmentHomeBinding;
import com.example.spotifywrapped.spotifyAPI.APIHandler;
import com.example.spotifywrapped.spotifyAPI.RequestParser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Button loginButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        System.out.println("creating view");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        loginButton = binding.loginButton;
        loginButton.setOnClickListener((v) -> APIHandler.getToken(getActivity()));

        Button homeButton = binding.homeButton;
        homeButton.setOnClickListener((v) -> {

            // get top songs
            RequestParser.testRequest(getActivity(), (parsedData) -> {
                // this is what you do with the top songs data
                onTopSongsReceived(parsedData);
            });

        });

//        makeRequest("https://api.spotify.com/v1/me", getActivity(), (JSONResult) -> {
//            System.out.println("response propagated");
//            try {
//                Thread.sleep(2000);
//            } catch (Exception e) {
//                System.out.println("fail");
//            }
//            System.out.println(JSONResult.toString());
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onTopSongsReceived(String[] parsedData) {
        loginButton.setText(parsedData[0]);
    }

}