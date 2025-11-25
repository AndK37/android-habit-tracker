package com.example.androidhabittracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageButton addIB;
    private TextView dateTV;
    private DBHelper helper;
    private SQLiteDatabase database;
    private DBUtils dbUtils;
    private Cursor cursor;
    private LocalDateTime now = LocalDateTime.now();
    private ArrayList<Habit> habits;

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

        ActivityResultLauncher<Intent> startAddForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 100) {
                recreate();
            }
        });
        addIB = findViewById(R.id.addIB);
        addIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddForResult.launch(new Intent(MainActivity.this, AddActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        helper = new DBHelper(MainActivity.this);
        try {
            database = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbUtils = new DBUtils(database);

        dateTV = findViewById(R.id.dateTV);
        dateTV.setText(now.format(DateTimeFormatter.ofPattern("E, d LLL yyyy", new Locale("ru", "RU"))));


        cursor = dbUtils.getAllHabits();
        while (!cursor.isAfterLast()) {
            switch (cursor.getString(3)) {
                case "WEEK":
                    WeekHabit wh = new WeekHabit(MainActivity.this, database, cursor.getInt(0));
                    break;
            }
            cursor.moveToNext();
        }
    }
}