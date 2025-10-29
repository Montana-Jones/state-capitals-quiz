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

    private int position;  // which state we’re showing in the pager
    private int questionNum = 1;

    public StateCapitalFragment() {
        // Required empty public constructor
    }

    // Use this factory method to pass which state to show
    public static StateCapitalFragment newInstance(int position) {
        StateCapitalFragment fragment = new StateCapitalFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
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

        return view;
    }

    private void loadStateData() {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                StateCapitalsDBHelper.TABLE_STATECAPITALS,
                null, null, null, null, null, null
        );

        if (cursor.moveToPosition(position)) {
            String state = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE));
            String capital = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL));
            String option1 = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1));
            String option2 = cursor.getString(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2));

            titleView.setText("Question " + questionNum);
            questionView.setText("What is the capital of " + state + "?");

            // Randomize answer order
            List<String> options = new ArrayList<>();
            options.add(capital);
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
}
