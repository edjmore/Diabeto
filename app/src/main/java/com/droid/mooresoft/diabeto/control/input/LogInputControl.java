package com.droid.mooresoft.diabeto.control.input;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Edward Moore on 4/8/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class LogInputControl
      implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

   private Context mContext;

   private TextView mTextDate, mTextClock;
   private EditText mEditBloodGlucose;
   private EditText mEditBolus;
   private EditText mEditCarbohydrate;

   /**
    * Creates a new LogInputControl object.
    *
    * @param root    A View which has all LogEntry input forms as children.
    * @param context The current Context.
    */
   public LogInputControl(View root, Context context) {
      mContext = context;
      mTextDate = (TextView) root.findViewById(R.id.text_date);
      mTextClock = (TextView) root.findViewById(R.id.text_clock);
      mEditBloodGlucose = (EditText) root.findViewById(R.id.edit_blood_glucose);
      mEditBolus = (EditText) root.findViewById(R.id.edit_bolus);
      mEditCarbohydrate = (EditText) root.findViewById(R.id.edit_carbohydrate);
      init();
   }

   private void init() {
      // Set text date/clock to current date/time.
      long epochTime = System.currentTimeMillis();
      String
            dateString = DatetimeUtils.getDateString(epochTime),
            timeString = DatetimeUtils.getTimeString(epochTime);
      mTextDate.setText(dateString);
      mTextClock.setText(timeString);

      // Setup click handlers for editing date/time.
      mTextDate.setOnClickListener(this);
      mTextClock.setOnClickListener(this);
   }


   /**
    * Convenience method for retrieving the current date/time the user has set for this LogEntry.
    *
    * @return Epoch time in millis.
    */
   private long getCurrentTimeSetting() {
      String
            dateString = mTextDate.getText().toString(),
            timeString = mTextClock.getText().toString();
      return DatetimeUtils.parseEpochTime(dateString, timeString);
   }

   /**
    * Handles clicks on the date/time views. Will open a dialog so the user can pick a date/time.
    */
   @Override
   public void onClick(View v) {
      long epochTime = getCurrentTimeSetting();
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(epochTime);

      int id = v.getId();
      switch (id) {
         case R.id.text_date:
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                  mContext, this,
                  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
            break;
         case R.id.text_clock:
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                  mContext, this,
                  cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
            );
            timePickerDialog.show();
            break;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      GregorianCalendar gcal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
      String dateString = DatetimeUtils.getDateString(gcal.getTimeInMillis());
      mTextDate.setText(dateString);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      String timeString = DatetimeUtils.getTimeString(hourOfDay, minute);
      mTextClock.setText(timeString);
   }

   /**
    * Fills in the editable fields with values from the given LogEntry.
    *
    * @param logEntry The LogEntry currently being edited by the user.
    */
   public void setDefaults(LogEntry logEntry) {
      long time = logEntry.getTime();
      String
            dateString = DatetimeUtils.getDateString(time),
            timeString = DatetimeUtils.getTimeString(time);
      mTextDate.setText(dateString);
      mTextClock.setText(timeString);
      mEditBloodGlucose.setText(logEntry.hasBloodGlucose() ?
            String.valueOf(logEntry.getBloodGlucose()) : // getBloodGlucose() will return -1 if the user never
            null                                         // entered a blood glucose for this LogEntry.
      );
      mEditBolus.setText(String.valueOf(logEntry.getBolus()));
      mEditCarbohydrate.setText(String.valueOf(logEntry.getCarbohydrate()));
   }

   /**
    * Creates a new LogEntry from user input.
    *
    * @return A new (or updated) LogEntry.
    */
   public LogEntry generateLogEntry() {
      long time = getTime();
      int bloodGlucose = getBloodGlucose();
      double bolus = getBolus();
      int carbohydrate = getCarbohydrate();
      return new LogEntry(time, bloodGlucose, bolus, carbohydrate);
   }

   private long getTime() {
      String
            dateString = mTextDate.getText().toString(),
            timeString = mTextClock.getText().toString();
      return DatetimeUtils.parseEpochTime(dateString, timeString);
   }

   private int getBloodGlucose() {
      String str = mEditBloodGlucose.getText().toString();
      return str.isEmpty() ? -1 : Integer.parseInt(str);
   }

   private double getBolus() {
      String str = mEditBolus.getText().toString();
      return str.isEmpty() ? 0 : Double.parseDouble(str);
   }

   private int getCarbohydrate() {
      String str = mEditCarbohydrate.getText().toString();
      return str.isEmpty() ? 0 : Integer.parseInt(str);
   }
}
