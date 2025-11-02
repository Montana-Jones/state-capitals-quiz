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

/**
 * A single quiz question page.
 * Loads the state + 3 capital options from DB using the given rowId.
 */
public class StateCapitalFragment extends Fragment {

    private static final String ARG_INDEX = "index";
    private static final String ARG_ROWID = "row_id";

    private int questionIndex;
    private int rowId;

    public StateCapitalFragment() { }

    public static StateCapitalFragment newInstance(int questionIndex, int rowId) {
        StateCapitalFragment f = new StateCapitalFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_INDEX, questionIndex);
        b.putInt(ARG_ROWID, rowId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle a = getArguments();
        questionIndex = a.getInt(ARG_INDEX);
        rowId = a.getInt(ARG_ROWID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_state_capital, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        TextView title = v.findViewById(R.id.titleView);
        TextView question = v.findViewById(R.id.questionView);
        RadioGroup group = v.findViewById(R.id.answersGroup);
        RadioButton a1 = v.findViewById(R.id.answer1);
        RadioButton a2 = v.findViewById(R.id.answer2);
        RadioButton a3 = v.findViewById(R.id.answer3);
        TextView hint = v.findViewById(R.id.hintText);

        title.setText("Question " + (questionIndex + 1) + " of 6");

        // Load from DB
        String state, capital, o1, o2;
        StateCapitalsDBHelper h = StateCapitalsDBHelper.getInstance(requireContext());
        SQLiteDatabase db = h.getReadableDatabase();
        Cursor c = db.query(StateCapitalsDBHelper.TABLE_STATECAPITALS,
                null,
                StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID + "=?",
                new String[]{ String.valueOf(rowId) },
                null, null, null);
        if (c.moveToFirst()) {
            state   = c.getString(c.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE));
            capital = c.getString(c.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL));
            o1      = c.getString(c.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1));
            o2      = c.getString(c.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2));
        } else {
            state = "Unknown"; capital = "N/A"; o1 = "N/A"; o2 = "N/A";
        }
        c.close(); db.close();

        question.setText("What is the capital of " + state + "?");

        // Shuffle 3 options (capital, option1, option2)
        java.util.List<String> opts = new java.util.ArrayList<>();
        opts.add(capital); opts.add(o1); opts.add(o2);
        java.util.Collections.shuffle(opts);
        a1.setText(opts.get(0));
        a2.setText(opts.get(1));
        a3.setText(opts.get(2));

        hint.setText("Select one answer; you can swipe anytime.");

        // Save chosen answer back to activity
        group.setOnCheckedChangeListener((g, checkedId) -> {
            RadioButton chosen = v.findViewById(checkedId);
            if (chosen != null) {
                String selected = chosen.getText().toString();
                ((MainActivity) requireActivity()).saveAnswer(questionIndex, selected, capital);
            }
        });
    }
}
