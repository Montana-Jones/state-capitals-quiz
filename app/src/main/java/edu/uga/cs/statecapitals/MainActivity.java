package edu.uga.cs.statecapitals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readCSV(this);
        ViewPager2 pager = findViewById( R.id.viewpager );
        StateCapitalsPagerAdapter adapter = new
                StateCapitalsPagerAdapter(
                getSupportFragmentManager(), getLifecycle() );
        pager.setOrientation(
                ViewPager2.ORIENTATION_HORIZONTAL );
        pager.setAdapter( adapter );
    }

    public static void readCSV(Context context) {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Optional: clear old data first
        db.delete(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, null);

        try {
            InputStream is = context.getAssets().open( "state_capitals.csv" );
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                // Skip the header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Split the CSV line into columns
                String[] parts = line.split(",");

                // Your CSV has 7 columns, but we only need the first 4
                if (parts.length < 4) continue;

                String state = parts[0].trim();       // State
                String capital = parts[1].trim();     // Capital city
                String option1 = parts[2].trim();     // Second city
                String option2 = parts[3].trim();     // Third city

                ContentValues values = new ContentValues();
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE, state);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL, capital);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1, option1);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2, option2);

                db.insert(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, values);
            }

            reader.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}