package com.droid.mooresoft.diabeto.view.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.TitleValUnitControl;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import java.util.List;

import static com.droid.mooresoft.diabeto.util.DiabetesUtils.calcAverageDailyBoluses;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.calcAverageDailyCarbs;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.calculateAverageBloodGlucose;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.calculateHba1c;

/**
 * Created by Edward Moore on 4/19/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class AnalyticsFragment extends Fragment {

   private View mRoot;
   private TitleValUnitControl mHba1cAdapter, mAvgCarbAdapter, mAvgBolusAdapter;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      mRoot = inflater.inflate(R.layout.frag_analytics, container, false);

      // Get handles for all the title-val-unit layouts.
      View
            hba1cLayout = mRoot.findViewById(R.id.layout_hba1c),
            avgCarbLayout = mRoot.findViewById(R.id.layout_avg_daily_carb),
            avgBolusLayout = mRoot.findViewById(R.id.layout_avg_daily_bolus);
      // Setup controllers for each one.
      mHba1cAdapter = new TitleValUnitControl(hba1cLayout, getContext());
      mAvgCarbAdapter = new TitleValUnitControl(avgCarbLayout, getContext());
      mAvgBolusAdapter = new TitleValUnitControl(avgBolusLayout, getContext());
      // Initialize title and units for each layout.
      mHba1cAdapter.setTitle(R.string.title_hba1c);
      mHba1cAdapter.setUnit(R.string.units_hba1c);
      mAvgCarbAdapter.setTitle(R.string.title_avg_daily_carb);
      mAvgCarbAdapter.setUnit(R.string.units_carbohydrate);
      mAvgBolusAdapter.setTitle(R.string.title_avg_daily_bolus);
      mAvgBolusAdapter.setUnit(R.string.units_insulin);

      return mRoot;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onResume() {
      super.onResume();
      List<LogEntry> logList = LogEntryCollection.getInstance().getCollection();

      // Calculate average blood glucose, average daily carb intake, and average
      // insulin bolused each day for the past week of LogEntry objects.
      // TODO: Not sure if I want to actually show the average BG over the past week (may overlap too much w/ {@link StatusFragment}).
      long
            endOfWeek = DatetimeUtils.startOfDay(System.currentTimeMillis()),
            startOfWeek = endOfWeek - DatetimeUtils.ONE_WEEK;
      double
            averageBloodGlucose = calculateAverageBloodGlucose(logList, startOfWeek, endOfWeek),
            averageDailyCarbs = calcAverageDailyCarbs(logList, startOfWeek, endOfWeek),
            averageDailyBoluses = calcAverageDailyBoluses(logList, startOfWeek, endOfWeek);

      // Calculate an estimated Hba1c value for the past week.
      // TODO: Should require some min number of blood glucose readings to estimate Hba1c.
      double hba1c = calculateHba1c(averageBloodGlucose);

      // Have the layout controllers fill in our view w/ the calculated values.
      mHba1cAdapter.setDecimalVal(hba1c, 2);
      mAvgCarbAdapter.setDecimalVal(averageDailyCarbs, 1);
      mAvgBolusAdapter.setDecimalVal(averageDailyBoluses, 1);
   }
}
