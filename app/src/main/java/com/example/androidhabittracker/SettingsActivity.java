package com.example.androidhabittracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox notificationCB;
    private EditText notificationET;
    private Button settingsB;
    String PREFS_FILE = "Settings";
    String PREFS_1 = "ENABLE_NOTIFICATIONS";
    String PREFS_2 = "NOTIFICATIONS_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        notificationCB = findViewById(R.id.notificationCB);
        notificationET = findViewById(R.id.notificationET);
        settingsB = findViewById(R.id.settingsB);

        notificationCB.setChecked(settings.getBoolean(PREFS_1, false));
        notificationET.setText(Integer.toString(settings.getInt(PREFS_2, 8)));

        settingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREFS_1, notificationCB.isChecked());
                if (Integer.parseInt(notificationET.getText().toString()) < 0) {
                    editor.putInt(PREFS_2, 0);
                }
                if (Integer.parseInt(notificationET.getText().toString()) > 23) {
                    editor.putInt(PREFS_2, 23);
                }
                editor.apply();
                finish();
            }
        });
    }
}