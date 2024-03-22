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

import java.util.Arrays;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onTopSongsReceived(String[] parsedData) {
        loginButton.setText(Arrays.toString(parsedData));
    }

}