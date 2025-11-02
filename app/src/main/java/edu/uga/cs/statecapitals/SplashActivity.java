package edu.uga.cs.statecapitals;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity shows a loading screen while the database is prepared.
 * Once ready, the user can:
 * - Start a new quiz (clears any saved progress)
 * - View previous quiz results
 */

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progress;
    private Button startQuizBtn, previousBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = findViewById(R.id.progress);
        startQuizBtn = findViewById(R.id.startQuizBtn);
        previousBtn = findViewById(R.id.previousBtn);

        // Load and prepare the database in the background.
        new PreloadTask(getApplicationContext(),
                () -> {
                    progress.setVisibility(ProgressBar.GONE);
                    startQuizBtn.setEnabled(true);
                    previousBtn.setEnabled(true);
                },
                e -> {
                    progress.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Initialization failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    startQuizBtn.setEnabled(true);
                }
        ).execute();

        // Start a brand-new quiz: clear any previous SharedPreferences state.
        startQuizBtn.setOnClickListener(v -> {
            getSharedPreferences("quiz_prefs", MODE_PRIVATE)
                    .edit().clear().apply();

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("NEW_QUIZ", true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        // Open the list of previous quizzes.
        previousBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, PreviousQuizzesActivity.class);
            startActivity(i);
        });
    }

    /**
     * Loads the database in the background to prepare it before the quiz starts.
     * Uses {@link AsyncTask} as required for this assignment.
     */
    static class PreloadTask extends AsyncTask<Void, Void, Boolean> {

        /** Callback interface for success. */
        interface OK { void run(); }
        /** Callback interface for error. */
        interface ERR { void run(Exception e); }

        private final Context appCtx;
        private final OK ok;
        private final ERR err;
        private Exception error;

        PreloadTask(Context appCtx, OK ok, ERR err) {
            this.appCtx = appCtx.getApplicationContext();
            this.ok = ok;
            this.err = err;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                StateCapitalsDBHelper
                        .getInstance(appCtx)
                        .getReadableDatabase()
                        .close();
                return true;
            } catch (Exception e) {
                error = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                ok.run();
            } else {
                err.run(error != null ? error : new RuntimeException("Unknown error"));
            }
        }
    }
}
