package com.droid.mooresoft.diabeto.control;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Moore on 4/18/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class XYPlotControl implements LogEntryCollection.OnChangeListener {
   private static final String TAG = XYPlotControl.class.getSimpleName();

   private XYPlot mPlot;
   private Context mContext;

   /**
    * Creates a new XYPlotControl object.
    *
    * @param plot    The XYPlot to control.
    * @param context The current Context.
    */
   public XYPlotControl(XYPlot plot, Context context) {
      mPlot = plot;
      mContext = context;
      init();
   }

   /**
    * Handles styling and other initialization for the XYPlot. It would be nice if this could be
    * put in XML, but the androidplot XML stuff doesn't seem to work very well.
    */
   private void init() {
      // Use a minimalist plot style: mostly transparent/white with bright colors for points.
      mPlot.getBackgroundPaint().setColor(Color.TRANSPARENT);
      mPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
      mPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
      mPlot.getBorderPaint().setColor(Color.TRANSPARENT);
      // Hide the vertical and horizontal grid lines.
      mPlot.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
      mPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
      // Remove the legend indicators.
      mPlot.getLegendWidget().setVisible(false);

      // Style the y-axis.
      mPlot.setTicksPerRangeLabel(1);
      mPlot.setRangeBoundaries(-50, 350, BoundaryMode.FIXED);
      mPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 50);
      // Make the y-axis labels appear as Ints, not Doubles.
      mPlot.setRangeValueFormat(new Format() {
         @Override
         public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
            int bloodGlucose = ((Number) object).intValue();
            // Hide the negative values.
            String bgString = bloodGlucose >= 0 ?
                  String.format("%d", bloodGlucose) :
                  "";
            return buffer.append(bgString);
         }

         @Override
         public Object parseObject(String string, ParsePosition position) {
            return null;
         }
      });

      // Style the x-axis.
      mPlot.setTicksPerDomainLabel(1);
      mPlot.getGraphWidget().setDomainLabelOrientation(0);
      // Want to use whole hours as labels, starting today, incrementing 4 hours at a time.
      long startOfDay = DatetimeUtils.startOfDay(System.currentTimeMillis());
      mPlot.setDomainBoundaries(startOfDay, startOfDay + DatetimeUtils.ONE_DAY, BoundaryMode.FIXED);
      mPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4 * DatetimeUtils.ONE_HOUR);
      // Format the x-axis labels so they show a human readable time instead of epoch time.
      mPlot.setDomainValueFormat(new Format() {
         @Override
         public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
            long time = ((Number) object).longValue();
            String timeString = DatetimeUtils.getTimeString(time); // e.g. 12:00 AM
            timeString = timeString.replace(":00", "");            // e.g. 12 AM
            return buffer.append(timeString);
         }

         @Override
         public Object parseObject(String string, ParsePosition position) {
            return null;
         }
      });

      // Hide the origin lines.
      mPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.TRANSPARENT);
      mPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.TRANSPARENT);

      // Set origin label text color...
      int color = mContext.getResources().getColor(android.R.color.primary_text_light);
      mPlot.getGraphWidget().getRangeLabelPaint().setColor(color);
      mPlot.getGraphWidget().getDomainLabelPaint().setColor(color);
      // ...and size.
      float size = 32;
      mPlot.getGraphWidget().getRangeLabelPaint().setTextSize(size);
      mPlot.getGraphWidget().getDomainLabelPaint().setTextSize(size);

      // Adding margins...
      float margin = 48;
      mPlot.getGraphWidget().setMargins(margin, margin, margin, margin);
      // ...and padding.
      mPlot.setPlotPaddingRight(margin);
   }

   @Override
   public void onChange(@Nullable LogEntry logEntry, Type type) {
      Log.d(TAG, String.format("LogEntryCollection changed: %s", type));
      // No matter what, we just reload everything.
      mPlot.clear();
      showBloodGlucoses();
      showBoluses();
      showCarbohydrates();
      mPlot.redraw();
   }

   // METHODS FOR ADDING SERIES //
   // Why is there no Java 8 ?? //

   public void showBloodGlucoses() {
      List<Number>
            bloodGlucoseList = new ArrayList<>(),
            timestampList = new ArrayList<>();
      for (LogEntry logEntry : LogEntryCollection.getInstance().getCollection()) {
         if (wasToday(logEntry) && logEntry.hasBloodGlucose()) {
            bloodGlucoseList.add(logEntry.getBloodGlucose());
            timestampList.add(logEntry.getTime());
         }
      } // No Java 8 :( :(

      showSeries(R.string.title_blood_glucose, timestampList, bloodGlucoseList, R.xml.line_point_format_blood_glucose);
   }

   public void showCarbohydrates() {
      List<Number>
            carbsList = new ArrayList<>(),
            timestampList = new ArrayList<>();
      for (LogEntry logEntry : LogEntryCollection.getInstance().getCollection()) {
         if (wasToday(logEntry) && logEntry.hasCarbohydrate()) {
            carbsList.add(logEntry.getCarbohydrate());
            timestampList.add(logEntry.getTime());
         }
      } // Yuck...

      showSeries(R.string.title_carbohydrate, timestampList, carbsList, R.xml.line_point_format_carbohydrate);
   }

   public void showBoluses() {
      List<Number>
            bolusesList = new ArrayList<>(),
            timestampList = new ArrayList<>();
      for (LogEntry logEntry : LogEntryCollection.getInstance().getCollection()) {
         if (wasToday(logEntry) && logEntry.hasBolus()) {
            bolusesList.add(logEntry.getBolus());
            timestampList.add(logEntry.getTime());
         }
      } // Still yuck...

      showSeries(R.string.title_bolus, timestampList, bolusesList, R.xml.line_point_format_bolus);
   }

   /**
    * Helper method for asserting whether or not the given LogEntry was entered today.
    */
   private boolean wasToday(LogEntry logEntry) {
      long
            logTime = logEntry.getTime(),
            startOfDay = DatetimeUtils.startOfDay(System.currentTimeMillis());
      return logTime >= startOfDay &&
            logTime <= startOfDay + DatetimeUtils.ONE_DAY;
   }

   /*
    * Helper for series showing methods above.
    */
   private void showSeries(int titleId, List<Number> xVals, List<Number> yVals, int formatId) {
      String title = mContext.getString(titleId);
      XYSeries series = new SimpleXYSeries(xVals, yVals, title);

      LineAndPointFormatter formatter = new LineAndPointFormatter();
      formatter.setPointLabelFormatter(new PointLabelFormatter());
      formatter.configure(mContext, formatId);

      mPlot.addSeries(series, formatter);
   }
}
