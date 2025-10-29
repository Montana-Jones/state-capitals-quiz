package edu.uga.cs.statecapitals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuizResultTask extends AsyncTask<Void, Void, Integer> {

    private Context context;

    public QuizResultTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        MainActivity activity = (MainActivity) context;
        List<String> selected = activity.getSelectedAnswers();
        List<String> correct = activity.getCorrectAnswers();

        int score = 0;
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).equals(correct.get(i))) score++;
        }

        //  to database
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put(StateCapitalsDBHelper.QUIZ_RESULTS_COLUMN_DATETIME, datetime);
        values.put(StateCapitalsDBHelper.QUIZ_RESULTS_COLUMN_SCORE, score);

        db.insert(StateCapitalsDBHelper.TABLE_QUIZ_RESULTS, null, values);
        db.close();

        return score;
    }

    @Override
    protected void onPostExecute(Integer score) {
        Toast.makeText(context, "Your score: " + score + "/6", Toast.LENGTH_LONG).show();
        // You could also navigate to a result fragment or reset the quiz
    }
}
