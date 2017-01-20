package com.droid.mooresoft.diabeto.view.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.data.TreatmentSettings;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;
import com.droid.mooresoft.diabeto.util.DiabetesUtils;
import com.droid.mooresoft.diabeto.util.Statistics;
import com.droid.mooresoft.diabeto.view.ColorPalette;

import java.util.List;

/**
 * Created by Edward Moore on 4/19/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class StatusFragment extends Fragment {

   private View mRoot;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      mRoot = inflater.inflate(R.layout.frag_status, container, false);
      return mRoot;
   }

   @Override
   public void onResume() {
      super.onResume();
      List<LogEntry> logEntryList = LogEntryCollection.getInstance().getCollection();

      // Show average blood sugar so far today...
      long
            newest = System.currentTimeMillis(),
            oldest = DatetimeUtils.startOfDay(newest);
      double averageBloodGlucose = DiabetesUtils.calculateAverageBloodGlucose(logEntryList, oldest, newest);
      TextView averageBgView = (TextView) mRoot.findViewById(R.id.text_average_blood_glucose);
      averageBgView.setText(String.format("%.1f", averageBloodGlucose));
      ColorPalette.styleBloodGlucose(averageBloodGlucose, averageBgView, getContext());
      // ...plus std dev...
      List<Number> bloodGlucoseList = DiabetesUtils.extractBloodGlucoses(logEntryList, oldest, newest);
      double stdDev = Statistics.stdDev(bloodGlucoseList, averageBloodGlucose);
      TextView stdDevView = (TextView) mRoot.findViewById(R.id.text_std_dev_blood_glucose);
      stdDevView.setText(String.format("%.2f", stdDev));
      // ...and range.
      Pair<Double, Double> range = Statistics.minMax(bloodGlucoseList);
      TextView rangeView = (TextView) mRoot.findViewById(R.id.text_range_blood_glucose);
      rangeView.setText(String.format("%d - %d", range.first.intValue(), range.second.intValue()));

      // Show estimated active insulin level.
      double activeInsulin = DiabetesUtils.calculateActiveInsulin(
            logEntryList, System.currentTimeMillis(), TreatmentSettings.getActiveInsulinPeriod(getContext()));
      TextView activeInsulinView = (TextView) mRoot.findViewById(R.id.text_active_insulin);
      activeInsulinView.setText(String.format("%.2f", activeInsulin));

      // Show current basal rate and carbohydrate ratio.
      double
            curBasalRate = TreatmentSettings.getCurrentBasalRate(getContext()),
            curCarbohydrateRatio = TreatmentSettings.getCurrentCarbohydrateRatio(getContext());
      TextView
            basalRateView = (TextView) mRoot.findViewById(R.id.text_basal_rate),
            carbohydrateRatioView = (TextView) mRoot.findViewById(R.id.text_carbohydrate_ratio);
      basalRateView.setText(String.format("%.2f", curBasalRate));
      carbohydrateRatioView.setText(String.format("%.2f", curCarbohydrateRatio));
   }
}
