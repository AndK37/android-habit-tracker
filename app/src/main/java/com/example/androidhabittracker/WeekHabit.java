package com.example.androidhabittracker;

import static androidx.core.app.ActivityCompat.recreate;
import static androidx.core.content.ContextCompat.getColor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class WeekHabit extends Habit{
    private String[] days;
    public WeekHabit(Context context, SQLiteDatabase db, int id) {
        super(context, db, id);

        DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        days = new String[7];
        for (int i = 6; i >= 0; i--)
        {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        initHabit();
    }
    @Override
    public void initHabit() {
        LinearLayout container = ((Activity)context).findViewById(R.id.container);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View hContainer = layoutInflater.inflate(R.layout.calendar_habit, container, false);
        GridLayout habitContainer = (GridLayout) hContainer;
        habitContainer.setId(View.generateViewId());

        TextView name = habitContainer.findViewById(R.id.CHName);
        name.setText(this.name);

        TextView desc = habitContainer.findViewById(R.id.CHDesc);
        desc.setText(this.desc);

        CheckBox check = habitContainer.findViewById(R.id.CHCheck);
        check.setChecked(dbUtils.isHabitChecked(id));

        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {getColor(context, color), getColor(context, color)};
        CompoundButtonCompat.setButtonTintList(check, new ColorStateList(states, colors));

        ImageView habitIV = habitContainer.findViewById(R.id.habitIV);
        habitIV.setColorFilter(getColor(context, color));

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbUtils.checkHabit(id);

                } else {
                    dbUtils.uncheckHabit(id);
                }
                reloadHabit(context, habitContainer.getId(), isChecked);
            }
        });

        habitContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DefaultDialog);
                builder
                        .setTitle("Удаление")
                        .setMessage("Удалить привычку \'" + name.getText().toString() + "\'?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbUtils.deleteHabit(id);
                                recreate((Activity) context);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
                return true;
            }
        });
        container.addView(habitContainer);

        GridLayout weekContainer = (GridLayout) ((LinearLayout) habitContainer.getChildAt(4)).getChildAt(0);

        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.parse(days[i]);
            String weekDayText = date.format(DateTimeFormatter.ofPattern("E", new Locale("ru", "RU"))).toUpperCase();
            ((TextView) weekContainer.getChildAt(i)).setText(weekDayText);
        }

        int checkCounter = 0;
        for (int i = 7; i < weekContainer.getChildCount(); i++) {
            CheckBox weekCheck = ((CheckBox) weekContainer.getChildAt(i));
            CompoundButtonCompat.setButtonTintList(weekCheck, new ColorStateList(states, colors));

            if (checkDates.length == 0) {
                continue;
            }

            if (days[i - 7].equals(checkDates[checkCounter])) {
                weekCheck.setChecked(true);
                if (checkDates[checkCounter].equals(checkDates[checkDates.length - 1])) {
                    continue;
                }
                checkCounter++;
            }
        }

    }

    @Override
    public void reloadHabit(Context context, int id, boolean check) {
        LinearLayout container = ((Activity)context).findViewById(R.id.container);
        GridLayout habitContainer = container.findViewById(id);
        GridLayout weekContainer = (GridLayout) ((LinearLayout) habitContainer.getChildAt(4)).getChildAt(0);

        ((CheckBox) weekContainer.getChildAt(weekContainer.getChildCount() - 1)).setChecked(check);
    }
}
