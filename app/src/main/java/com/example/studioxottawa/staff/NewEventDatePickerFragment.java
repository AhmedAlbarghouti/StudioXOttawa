package com.example.studioxottawa.staff;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.Year;
import java.util.Calendar;

/**
 * @Author: Ahmed Albarghouti
 * @Date: Feb 2021
 * @Purpose: DialogFragment that displays Calender to enable user to select from.
 */
public class NewEventDatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    /**
     * On Create Dialog that returns new DatePickerDialog Object containing the selected date
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog obj = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        obj.getDatePicker().setMinDate(c.getTimeInMillis()); // disables past dates from being selected
        return obj;
    }


}
