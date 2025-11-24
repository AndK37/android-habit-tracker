package com.example.androidhabittracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {
    private EditText nameET, descET, targetET;
    private Button addB;
    private Spinner typeS, colorS;
    private DBHelper helper;
    private SQLiteDatabase database;
    private DBUtils dbUtils;
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
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new DBHelper(AddActivity.this);
        try {
            database = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbUtils = new DBUtils(database);

        typeS = findViewById(R.id.typeS);
        typeS.setAdapter(new ArrayAdapter<String>(AddActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dbUtils.getHabitTypes()));

        colorS = findViewById(R.id.colorS);
        colorS.setAdapter(new ArrayAdapter<String>(AddActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dbUtils.getHabitColors()));

        nameET = findViewById(R.id.nameET);
        descET = findViewById(R.id.descET);
        targetET = findViewById(R.id.targetET);

        addB = findViewById(R.id.addB);
        addB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameET.getText().toString().equals("")) {
                    Toast.makeText(AddActivity.this, "Название обязательно!", Toast.LENGTH_LONG).show();
                    return;
                }

                Integer target = null;
                if (!targetET.getText().toString().equals("")) {
                    target = Integer.parseInt(targetET.getText().toString());
                }
                dbUtils.addHabit(nameET.getText().toString(), descET.getText().toString(),
                        typeS.getSelectedItem().toString(), colorS.getSelectedItem().toString(),
                        target);
                setResult(100);
                finish();
            }
        });
    }
}