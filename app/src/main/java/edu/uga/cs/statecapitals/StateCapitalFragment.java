package edu.uga.cs.statecapitals;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateCapitalFragment extends Fragment {

    private TextView titleView;
    private TextView questionView;
    private RadioButton radioButton1, radioButton2, radioButton3;
    private RadioGroup radioGroup;

    private int questionId;
    private int questionNumber;
    private String correctAnswer;

    public StateCapitalFragment() {}

    public static StateCapitalFragment newInstance(int questionId, int questionNumber) {
        StateCapitalFragment fragment = new StateCapitalFragment();
        Bundle args = new Bundle();
        args.putInt("questionId", questionId);
        args.putInt("questionNumber", questionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionId = getArguments().getInt("questionId");
            questionNumber = getArguments().getInt("questionNumber");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_state_capital, container, false);

        titleView = view.findViewById(R.id.titleView);
        questionView = view.findViewById(R.id.questionView);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.radioButton1);
        radioButton2 = view.findViewById(R.id.radioButton2);
        radioButton3 = view.findViewById(R.id.radioButton3);

        loadStateData();

        // Restore state if available


        if (savedInstanceState != null) {
            titleView.setText(savedInstanceState.getString("titleText"));
            questionView.setText(savedInstanceState.getString("questionText"));
            radioButton1.setText(savedInstanceState.getString("radio1Text"));
            radioButton2.setText(savedInstanceState.getString("radio2Text"));
            radioButton3.setText(savedInstanceState.getString("radio3Text"));
            int checkedId = savedInstanceState.getInt("checkedId", -1);
            if (checkedId != -1) radioGroup.check(checkedId);
        }

        // Listen for selection changes
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selected = "";
            if (checkedId == radioButton1.getId()) selected = radioButton1.getText().toString();
            else if (checkedId == radioButton2.getId()) selected = radioButton2.getText().toString();
            else if (checkedId == radioButton3.getId()) selected = radioButton3.getText().toString();

            // Save the answer to MainActivity
            ((MainActivity) requireActivity()).saveAnswer(questionNumber - 1, selected, correctAnswer);
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("checkedId", radioGroup.getCheckedRadioButtonId());
        outState.putString("titleText", titleView.getText().toString());
        outState.putString("questionText", questionView.getText().toString());
        outState.putString("radio1Text", radioButton1.getText().toString());
        outState.putString("radio2Text", radioButton2.getText().toString());
        outState.putString("radio3Text", radioButton3.getText().toString());
    }

    private void loadStateData() {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                StateCapitalsDBHelper.TABLE_STATECAPITALS,
                null,
                StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID + "=?",
                new String[]{String.valueOf(questionId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String state = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE));
            correctAnswer = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL));
            String option1 = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2));

            titleView.setText("Question " + questionNumber);
            questionView.setText("What is the capital of " + state + "?");

            // Randomize answer order
            List<String> options = new ArrayList<>();
            options.add(correctAnswer);
            options.add(option1);
            options.add(option2);
            Collections.shuffle(options);

            radioButton1.setText(options.get(0));
            radioButton2.setText(options.get(1));
            radioButton3.setText(options.get(2));
        }

        cursor.close();
        db.close();
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
