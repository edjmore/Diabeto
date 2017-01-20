package com.droid.mooresoft.diabeto.view.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

/**
 * Created by Edward Moore on 4/10/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class ViewLogEntryFragment extends Fragment {

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_view_log_entry, container, false);

      // Fill in the view with data from the provided LogEntry.
      LogEntry logEntry = getArguments().getParcelable(LogEntry.EXTRA_LOG_ENTRY);
      TextView
            textDate = (TextView) root.findViewById(R.id.text_date),
            textClock = (TextView) root.findViewById(R.id.text_clock),
            textBloodGlucose = (TextView) root.findViewById(R.id.text_blood_glucose),
            textBolus = (TextView) root.findViewById(R.id.text_bolus),
            textCarbohydrate = (TextView) root.findViewById(R.id.text_carbohydrate);
      long time = logEntry.getTime();
      String
            dateString = DatetimeUtils.getDateString(time),
            timeString = DatetimeUtils.getTimeString(time);

      // Hide all View groups by default since the LogEntry could be completely empty.
      View
            layoutBloodGlucose = root.findViewById(R.id.layout_blood_glucose),
            layoutBolus = root.findViewById(R.id.layout_bolus),
            layoutCarbohydrate = root.findViewById(R.id.layout_carbohydrate);
      layoutBloodGlucose.setVisibility(View.INVISIBLE);
      layoutBolus.setVisibility(View.INVISIBLE);
      layoutCarbohydrate.setVisibility(View.INVISIBLE);

      // If the LogEntry has a particular value we show its View group and fill in the value.
      textDate.setText(dateString);
      textClock.setText(timeString);
      if (logEntry.hasBloodGlucose()) {
         layoutBloodGlucose.setVisibility(View.VISIBLE);
         textBloodGlucose.setText(String.valueOf(logEntry.getBloodGlucose()));
      }
      if (logEntry.hasBolus()) {
         textBolus.setText(String.valueOf(logEntry.getBolus()));
         layoutBolus.setVisibility(View.VISIBLE);
      }
      if (logEntry.hasCarbohydrate()) {
         textCarbohydrate.setText(String.valueOf(logEntry.getCarbohydrate()));
         layoutCarbohydrate.setVisibility(View.VISIBLE);
      }

      return root;
   }
}
