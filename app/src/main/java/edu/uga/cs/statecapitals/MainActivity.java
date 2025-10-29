package edu.uga.cs.statecapitals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> selectedAnswers = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();
    private List<Integer> questionIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate DB from CSV if needed
        readCSV(this);

        // Generate 6 random questions
        questionIds = getRandomQuestionIds(this, 6);

        // Restore state if applicable
        if (savedInstanceState != null) {
            selectedAnswers = savedInstanceState.getStringArrayList("selectedAnswers");
            correctAnswers = savedInstanceState.getStringArrayList("correctAnswers");
        }

        ViewPager2 pager = findViewById(R.id.viewpager);
        StateCapitalsPagerAdapter adapter = new StateCapitalsPagerAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                questionIds
        );
        pager.setAdapter(adapter);

        // Restore position
        if (savedInstanceState != null) {
            int restoredPosition = savedInstanceState.getInt("currentPosition", 0);
            pager.setCurrentItem(restoredPosition, false);
        }

        // Detect swipe past last question to submit automatically
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == questionIds.size()) {
                    new QuizResultTask(MainActivity.this).execute();
                }
            }
        });
    }

    // Save state for rotation/process death
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current ViewPager position
        ViewPager2 pager = findViewById(R.id.viewpager);
        outState.putInt("currentPosition", pager.getCurrentItem());

        // Save selected answers and correct answers
        outState.putStringArrayList("selectedAnswers", new ArrayList<>(selectedAnswers));
        outState.putStringArrayList("correctAnswers", new ArrayList<>(correctAnswers));
    }

    public void saveAnswer(int questionNumber, String selectedAnswer, String correctAnswer) {
        if (selectedAnswers.size() < 6) selectedAnswers.add(selectedAnswer);
        if (correctAnswers.size() < 6) correctAnswers.add(correctAnswer);
    }

    public List<String> getSelectedAnswers() { return selectedAnswers; }
    public List<String> getCorrectAnswers() { return correctAnswers; }

    // Read CSV and populate DB
    public static void readCSV(Context context) {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, null); // clear old data

        try {
            InputStream is = context.getAssets().open("state_capitals.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String state = parts[0].trim();
                String capital = parts[1].trim();
                String option1 = parts[2].trim();
                String option2 = parts[3].trim();

                android.content.ContentValues values = new android.content.ContentValues();
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE, state);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL, capital);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1, option1);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2, option2);

                db.insert(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, values);
            }

            reader.close();
            db.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Pick N random question IDs
    public static List<Integer> getRandomQuestionIds(Context context, int count) {
        List<Integer> ids = new ArrayList<>();
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(StateCapitalsDBHelper.TABLE_STATECAPITALS,
                new String[]{StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID)));
        }

        cursor.close();
        db.close();

        Collections.shuffle(ids);
        if (ids.size() > count) ids = ids.subList(0, count);

        return ids;
    }
}
