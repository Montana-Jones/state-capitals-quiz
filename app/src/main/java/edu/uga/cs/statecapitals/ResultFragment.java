package edu.uga.cs.statecapitals;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Shows the final score and navigation actions.
 * If the fragment was created before the score was known, it updates
 * as soon as the score becomes available.
 */
public class ResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";
    private int score = -1;

    private TextView scoreText;

    public ResultFragment() { }

    /** Create a new instance with the (possibly provisional) score. */
    public static ResultFragment newInstance(int score) {
        ResultFragment f = new ResultFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SCORE, score);
        f.setArguments(b);
        return f;
    }

    /** Allow the adapter/activity to inject the score after creation. */
    public void setScore(int score) {
        this.score = score;
        updateScoreText();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE, -1);
        }
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        scoreText = v.findViewById(R.id.scoreText);
        Button retry = v.findViewById(R.id.retryButton);
        Button seePrev = v.findViewById(R.id.seePreviousButton);

        updateScoreText();

        retry.setOnClickListener(view -> {
            Intent i = new Intent(requireContext(), SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        seePrev.setOnClickListener(view ->
                startActivity(new Intent(requireContext(), PreviousQuizzesActivity.class)));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Safety net: if score was still unknown, ask the activity for it.
        if (score < 0 && getActivity() instanceof MainActivity) {
            int s = ((MainActivity) getActivity()).getCurrentScore();
            if (s >= 0) {
                setScore(s);
            }
        }
    }

    /** Updates the score label; falls back to a generic message if unknown. */
    private void updateScoreText() {
        if (scoreText == null) return;
        String msg = (score >= 0) ? ("You scored " + score + " out of 6!")
                : "Quiz finished!";
        scoreText.setText(msg);
    }
}
