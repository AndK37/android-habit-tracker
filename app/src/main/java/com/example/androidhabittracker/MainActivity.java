package com.example.androidhabittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Console;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView dateTV;
    private DBHelper helper;
    private SQLiteDatabase database;
    private DBUtils dbUtils;
    private Cursor cursor;
    private String[] days;
    private HashMap<Integer, Integer> habitsId = new HashMap<>();

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

        dbUtils = new DBUtils(database);

        dateTV = findViewById(R.id.dateTV);
        LocalDateTime now = LocalDateTime.now();
        dateTV.setText(now.format(DateTimeFormatter.ofPattern("E, d LLL yyyy", new Locale("ru", "RU"))));

        DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        days = new String[7];
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        initHabits();
    }
    private void initHabits() {
        LinearLayout container = findViewById(R.id.container);
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        cursor = dbUtils.getAllHabits();

        for (int i = 0; i < cursor.getCount(); i++) {
            View habitContainer = layoutInflater.inflate(R.layout.calendar_habit, container, false);

            habitContainer.setId(View.generateViewId());
            habitsId.put(cursor.getInt(0), habitContainer.getId());

            TextView name = habitContainer.findViewById(R.id.CHName);
            name.setText(cursor.getString(1));

            TextView desc = habitContainer.findViewById(R.id.CHDesc);
            desc.setText(cursor.getString(2));

            CheckBox check = habitContainer.findViewById(R.id.CHCheck);
            check.setChecked(dbUtils.isHabitChecked(cursor.getInt(0)));

            int id = cursor.getInt(0);
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dbUtils.checkHabit(id);
                    } else {
                        dbUtils.uncheckHabit(id);
                    }
                    reloadHabit(id);
                }
            });

            container.addView(habitContainer);
            cursor.moveToNext();
        }
        cursor.close();

        for (int key: habitsId.keySet()) {
            cursor = dbUtils.getHabitChecks(key);
            if (cursor.getCount() == 0) {
                break;
            }

            GridLayout habitContainer = container.findViewById(habitsId.get(key));
            GridLayout weekContainer = (GridLayout) ((LinearLayout) habitContainer.getChildAt(4)).getChildAt(0);

            int checkboxCounter = 0;
            for (int i = 0; i < weekContainer.getChildCount(); i++) {
                if (weekContainer.getChildAt(i) instanceof CheckBox) {
                    if (days[checkboxCounter].equals(cursor.getString(0))) {
                        ((CheckBox) weekContainer.getChildAt(i)).setChecked(true);
                        cursor.moveToNext();
                        if (cursor.isAfterLast()) {
                            break;
                        }
                    }
                    checkboxCounter++;
                }
            }

        }
        cursor.close();
    }
    private void reloadHabit(int id) {
        LinearLayout container = findViewById(R.id.container);
        GridLayout habitContainer = container.findViewById(habitsId.get(id));
        GridLayout weekContainer = (GridLayout) ((LinearLayout) habitContainer.getChildAt(4)).getChildAt(0);

        for (int i = 0; i < weekContainer.getChildCount(); i++) {
            if (weekContainer.getChildAt(i) instanceof CheckBox) {
                ((CheckBox) weekContainer.getChildAt(i)).setChecked(false);
            }
        }

        cursor = dbUtils.getHabitChecks(id);
        if (cursor.getCount() == 0) {
            return;
        }

        int checkboxCounter = 0;
        for (int i = 0; i < weekContainer.getChildCount(); i++) {
            if (weekContainer.getChildAt(i) instanceof CheckBox) {
                if (days[checkboxCounter].equals(cursor.getString(0))) {
                    ((CheckBox) weekContainer.getChildAt(i)).setChecked(true);
                    cursor.moveToNext();
                    if (cursor.isAfterLast()) {
                        break;
                    }
                }
                checkboxCounter++;
            }
        }
        cursor.close();
    }
}