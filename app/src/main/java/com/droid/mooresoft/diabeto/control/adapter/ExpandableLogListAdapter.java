package com.droid.mooresoft.diabeto.control.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;
import com.droid.mooresoft.diabeto.view.ColorPalette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edward Moore on 4/25/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class ExpandableLogListAdapter extends BaseExpandableListAdapter implements LogEntryCollection.OnChangeListener {

   private Context mContext;
   private String[] mGroupHeaders;
   private Map<String, List<LogEntry>> mHeaderToChildrenMap;

   public ExpandableLogListAdapter(Context context) {
      mContext = context;
      // Subsribe to data set updates.
      LogEntryCollection.getInstance().addListener(this);
      // Initialize headers and header-to-children map.
      refreshDataSet();
   }

   @Override
   public boolean hasStableIds() {
      return false;
   }

   // CHILD METHODS //

   @Override
   public int getChildrenCount(int groupPosition) {
      String header = mGroupHeaders[groupPosition];
      return mHeaderToChildrenMap.get(header).size();
   }

   @Override
   public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
      if (null == convertView) {
         LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = layoutInflater.inflate(R.layout.list_item_log, parent, false);
      }
      LogEntry logEntry = (LogEntry) getChild(groupPosition, childPosition);
      return LogListAdapter.populateView(convertView, logEntry, mContext);
   }

   @Override
   public Object getChild(int groupPosition, int childPosition) {
      String header = mGroupHeaders[groupPosition];
      return mHeaderToChildrenMap.get(header).get(childPosition);
   }

   @Override
   public long getChildId(int groupPosition, int childPosition) {
      return childPosition;
   }

   @Override
   public boolean isChildSelectable(int groupPosition, int childPosition) {
      return true;
   }

   // GROUP METHODS //

   @Override
   public int getGroupCount() {
      return mGroupHeaders.length;
   }

   @Override
   public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
      if (null == convertView) {
         LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         convertView = layoutInflater.inflate(R.layout.list_header_date, parent, false);
      }
      // Highlight the View when expanded.
      ColorPalette.styleGroup(convertView, isExpanded);

      // Fill in date indicator and number of LogEntry items on the given date.
      String
            header = (String) getGroup(groupPosition),
            numLogs = String.valueOf(getChildrenCount(groupPosition));
      // Convert the header to a more human readable format.
      header = DatetimeUtils.convertToRelativeDateString(header, System.currentTimeMillis());
      TextView
            headerView = (TextView) convertView.findViewById(R.id.text_header_date),
            numLogsView = (TextView) convertView.findViewById(R.id.text_num_logs);
      headerView.setText(header);
      numLogsView.setText(numLogs);
      return convertView;
   }

   @Override
   public Object getGroup(int groupPosition) {
      return mGroupHeaders[groupPosition];
   }

   @Override
   public long getGroupId(int groupPosition) {
      return groupPosition;
   }

   // LOG ENTRY COLLECTION MANAGEMENT METHODS //

   @Override
   public void onChange(@Nullable LogEntry logEntry, Type type) {
      // Just regenerate everything.
      // TODO: If the change is just a single LogEntry we can probably do better.
      refreshDataSet();
      notifyDataSetChanged();
   }

   private void refreshDataSet() {
      mHeaderToChildrenMap = generateHeaderToChildrenMap();
      mGroupHeaders = generateHeaders();
   }

   private Map<String, List<LogEntry>> generateHeaderToChildrenMap() {
      Map<String, List<LogEntry>> map = new HashMap<>();
      List<LogEntry> logList = LogEntryCollection.getInstance().getCollection();

      for (LogEntry log : logList) {
         // Generate the appropriate header for this LogEntry.
         long time = log.getTime();
         String header = DatetimeUtils.getDateString(time);

         // Get the List of children this LogEntry should belong to,
         // or create a new one if it doesn't exist yet.
         List<LogEntry> children = map.containsKey(header) ?
               map.get(header) : new ArrayList<LogEntry>();

         // Add the LogEntry and commit the child List back to the map.
         children.add(log);
         map.put(header, children);
      }

      return map;
   }

   private String[] generateHeaders() {
      // Just return the keys used in the header-to-children map, sorted in chronological order.
      String[] headers = mHeaderToChildrenMap.keySet().toArray(new String[]{});
      Arrays.sort(headers, new Comparator<String>() {
         @Override
         public int compare(String lhs, String rhs) {
            // Compare the epoch times the date strings represent.
            long l = DatetimeUtils.parseEpochTime(lhs), r = DatetimeUtils.parseEpochTime(rhs);
            long dif = r - l;

            if (0 == dif) return 0;
            return (int) (dif / Math.abs(dif)); // = either -1 or 1
         }
      });
      return headers;
   }
}
