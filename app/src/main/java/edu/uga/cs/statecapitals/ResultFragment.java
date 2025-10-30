package edu.uga.cs.statecapitals;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";
    private int score;

    public static ResultFragment newInstance(int score) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
        }

        TextView scoreText = view.findViewById(R.id.scoreText);
        Button retryButton = view.findViewById(R.id.retryButton);

        scoreText.setText("You scored " + score + " out of 6!");

        retryButton.setOnClickListener(v -> {
            requireActivity().recreate(); // restart quiz
        });

        return view;
    }
}
