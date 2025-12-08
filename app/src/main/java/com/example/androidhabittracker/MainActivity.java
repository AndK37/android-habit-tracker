package com.example.androidhabittracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageButton addIB, settingsIB;
    private TextView dateTV;
    private DBHelper helper;
    private SQLiteDatabase database;
    private DBUtils dbUtils;
    private Cursor cursor;

    PendingIntent pendingIntent;
    BroadcastReceiver appReceiver;
    AlarmManager alarmManager;

    String PREFS_FILE = "Settings";
    String PREFS_1 = "ENABLE_NOTIFICATIONS";
    String PREFS_2 = "NOTIFICATIONS_TIME";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
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

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        if (!settings.contains(PREFS_1)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_1, false);
            editor.apply();
        }

        if (!settings.contains(PREFS_2)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(PREFS_2, 8);
            editor.apply();
        }

        if (settings.getBoolean(PREFS_1, false)) {
            AppNotification.getPermissions(MainActivity.this);
            AppNotification appNotification = new AppNotification(MainActivity.this);

            appReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (cursor.getCount() == 0) {
                        return;
                    }
                    String notificationDesc = "";
                    cursor = dbUtils.getAllUncheckedHabits();
                    while (!cursor.isAfterLast()) {
                        notificationDesc += cursor.getString(0) + "\n";
                        cursor.moveToNext();
                    }
                    appNotification.send(R.drawable.outline_check_24, "Неотмеченные привычки", notificationDesc);
                }
            };

            registerReceiver(appReceiver, new IntentFilter("com.example.androidhabittracker.UNCHECKED_HABITS"), RECEIVER_EXPORTED);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                    new Intent("com.example.androidhabittracker.UNCHECKED_HABITS"),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, settings.getInt(PREFS_2, 8));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            alarmManager = (AlarmManager)(this.getSystemService(ALARM_SERVICE));
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }

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

        settingsIB = findViewById(R.id.settingsIB);
        settingsIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddForResult.launch(new Intent(MainActivity.this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
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
        dateTV.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, d LLL yyyy", new Locale("ru", "RU"))));
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        cursor = dbUtils.getAllHabits();
        while (!cursor.isAfterLast()) {
            switch (cursor.getString(3)) {
                case "DAY":
                    new DayHabit(MainActivity.this, database, cursor.getInt(0));
                    break;
                case "WEEK":
                    new WeekHabit(MainActivity.this, database, cursor.getInt(0));
                    break;
                case "MONTH":
                    new MonthHabit(MainActivity.this, database, cursor.getInt(0));
                    break;
                case "PROGRESSBAR":
                    new ProgressBarHabit(MainActivity.this, database, cursor.getInt(0));
                    break;
            }
            cursor.moveToNext();
        }
    }
}