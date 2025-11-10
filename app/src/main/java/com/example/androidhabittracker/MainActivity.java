package com.example.androidhabittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView dateTV;
    private GridLayout cal1;
    private DBHelper helper;
    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

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

        // BottomNavBar overlap with system bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        helper = new DBHelper(MainActivity.this);
        try {
            database = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dateTV = findViewById(R.id.dateTV);
        LocalDateTime now = LocalDateTime.now();
        dateTV.setText(now.format(DateTimeFormatter.ofPattern("E, d LLL yyyy", new Locale("ru", "RU"))));

        DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] days = new String[7];
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        cursor = database.rawQuery(
                "SELECT check_date FROM habits\n" +
                        "JOIN history ON habits.id = history.habit_id\n" +
                        "WHERE habits.id = 3\n" +
                        "AND strftime('%W', history.check_date, 'localtime', 'weekday 0', '-6 days')\n" +
                        "IS strftime('%W', 'now', 'localtime', 'weekday 0', '-6 days')\n" +
                        "ORDER BY check_date",
                null);
        cursor.moveToFirst();

        cal1 = findViewById(R.id.cal1);
        GridLayout gl = (GridLayout) ((LinearLayout) cal1.getChildAt(4)).getChildAt(0);
        int checkboxCounter = 0;
        for (int i = 0; i < gl.getChildCount(); i++) {
            if (gl.getChildAt(i) instanceof CheckBox) {
                if (days[checkboxCounter].equals(cursor.getString(0))) {
                    ((CheckBox) gl.getChildAt(i)).setChecked(true);
                    cursor.moveToNext();
                }
                checkboxCounter++;
            }
        }
        cursor.close();

        CheckBox cb = (CheckBox) cal1.getChildAt(3);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ContentValues cv = new ContentValues();
                    cv.put("habit_id", 3);
                    database.insert("history", null, cv);

                }
            }
        });


        calendar = Calendar.getInstance();

//        if (format.format(calendar.getTime()) == && cb.isChecked()) {
//
//        }

    }
}