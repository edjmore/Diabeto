package com.droid.mooresoft.diabeto.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;
import com.droid.mooresoft.diabeto.view.ColorPalette;

import java.util.List;

/**
 * Created by Edward Moore on 4/18/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * Custom ArrayAdapter for adapting a List of LogEntry objects to a ListView.
 * TODO: I would like to seperate the list into chunks where each chunk is a different day.
 * TODO: I don't know how well this will perform with a large number of LogEntry objects.
 *
 * @see <a href="http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/">ExpandableListView tutorial</a>
 */
@Deprecated
public class LogListAdapter extends ArrayAdapter<LogEntry> {

   /**
    * Creates a new LogListAdapter object.
    *
    * @param context      The current Context.
    * @param logEntryList The List of LogEntry objects to display.
    */
   public LogListAdapter(Context context, List<LogEntry> logEntryList) {
      super(context, 0, 0, logEntryList);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      if (null == convertView) {
         LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = layoutInflater.inflate(R.layout.list_item_log, parent, false);
      }
      LogEntry logEntry = getItem(position);
      return populateView(convertView, logEntry, getContext());
   }

   /**
    * Fills in a View with data from a LogEntry object.
    *
    * @param view     A View inflated using the R.layout.log_list_item XML file.
    * @param logEntry The LogEntry whose data should be reflected in the view.
    * @return The populated parameter View.
    */
   public static View populateView(View view, LogEntry logEntry, Context context) {
      TextView
            textTime = (TextView) view.findViewById(R.id.text_time),
            textBloodGlucose = (TextView) view.findViewById(R.id.text_blood_glucose),
            textBolus = (TextView) view.findViewById(R.id.text_bolus),
            textCarbohydrate = (TextView) view.findViewById(R.id.text_carbohydrate);
      String
            time = DatetimeUtils.getTimeString(logEntry.getTime()),
            bloodGlucose = String.valueOf(logEntry.getBloodGlucose()),
            bolus = String.valueOf(logEntry.getBolus()),
            carbohydrate = String.valueOf(logEntry.getCarbohydrate());

      // Hide all View groups by default since the LogEntry could be completely empty.
      View
            layoutBloodGlucose = view.findViewById(R.id.layout_blood_glucose),
            layoutBolus = view.findViewById(R.id.layout_bolus),
            layoutCarbohydrate = view.findViewById(R.id.layout_carbohydrate);
      layoutBloodGlucose.setVisibility(View.INVISIBLE);
      layoutBolus.setVisibility(View.INVISIBLE);
      layoutCarbohydrate.setVisibility(View.INVISIBLE);

      // If the LogEntry has a particular value we show its View group and fill in the value.
      textTime.setText(time);
      if (logEntry.hasBloodGlucose()) {
         layoutBloodGlucose.setVisibility(View.VISIBLE);
         textBloodGlucose.setText(bloodGlucose);

         // Indicate meaning of value w/ coloured ring.
         ColorPalette.styleBloodGlucose(Double.parseDouble(bloodGlucose), textBloodGlucose, context);
      }
      if (logEntry.hasBolus()) {
         textBolus.setText(bolus);
         layoutBolus.setVisibility(View.VISIBLE);
      }
      if (logEntry.hasCarbohydrate()) {
         textCarbohydrate.setText(carbohydrate);
         layoutCarbohydrate.setVisibility(View.VISIBLE);
      }

      return view;
   }
}
