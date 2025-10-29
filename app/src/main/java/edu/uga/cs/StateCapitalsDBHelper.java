package edu.uga.cs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StateCapitalsDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "statecapitals.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_STATECAPITALS = "statecapitals";
    public static final String STATECAPITALS_COLUMN_ID = "_id";
    public static final String STATECAPITALS_COLUMN_STATE = "state";
    public static final String STATECAPITALS_COLUMN_CAPITAL = "capital";
    public static final String STATECAPITALS_COLUMN_OPTION1 = "option1";
    public static final String STATECAPITALS_COLUMN_OPTION2 = "option2";

    private static final String CREATE_STATECAPITALS =
            "CREATE TABLE " + TABLE_STATECAPITALS + " (" +
                    STATECAPITALS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STATECAPITALS_COLUMN_STATE + " TEXT, " +
                    STATECAPITALS_COLUMN_CAPITAL + " TEXT, " +
                    STATECAPITALS_COLUMN_OPTION1 + " TEXT, " +
                    STATECAPITALS_COLUMN_OPTION2 + " TEXT" +
                    ")";

    private static StateCapitalsDBHelper helperInstance;

    private StateCapitalsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized StateCapitalsDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new StateCapitalsDBHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATECAPITALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Corrected table name and structure
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATECAPITALS);
        onCreate(db);
    }
}
