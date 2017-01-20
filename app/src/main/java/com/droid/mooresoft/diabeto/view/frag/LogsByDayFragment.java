package com.droid.mooresoft.diabeto.view.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.adapter.ExpandableLogListAdapter;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.view.activity.LogInputActivity;
import com.droid.mooresoft.diabeto.view.activity.ViewLogEntryActivity;

/**
 * Created by Edward Moore on 4/25/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class LogsByDayFragment extends Fragment
      implements View.OnClickListener, ExpandableListView.OnChildClickListener {

   public static final int
         REQUEST_NEW_LOG = 0x01,
         REQUEST_VIEW_LOG = 0x10;

   private ExpandableLogListAdapter mAdapter;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_logs_by_day, container, false);
      // Create the ListAdapter and attach to the ExpandableListView.
      mAdapter = new ExpandableLogListAdapter(getContext());
      ExpandableListView listView = (ExpandableListView) root.findViewById(R.id.list_logs);
      listView.setAdapter(mAdapter);

      // We will handle clicks on LogEntry items.
      listView.setOnChildClickListener(this);
      return root;
   }

   /**
    * FAB will bring user to the LogInputActivity so they can add a new LogEntry.
    *
    * @param v The FAB.
    */
   @Override
   public void onClick(View v) {
      Intent logInputActivity = new Intent(getContext(), LogInputActivity.class);
      startActivityForResult(logInputActivity, REQUEST_NEW_LOG);
   }

   /*
    * Handles clicks on LogEntry items within the ExpandableListView. Brings user
    * to an activity where they can view/edit a LogEntry.
    */
   @Override
   public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
      LogEntry logEntry = (LogEntry) mAdapter.getChild(groupPosition, childPosition);
      Intent viewLogEntryActivity =
            new Intent(getContext(), ViewLogEntryActivity.class)
                  .putExtra(LogEntry.EXTRA_LOG_ENTRY, logEntry);
      startActivityForResult(viewLogEntryActivity, REQUEST_VIEW_LOG);
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      int messageId = 0;

      switch (requestCode) {
         case REQUEST_NEW_LOG:
            // If a new LogEntry was created we unpackage it and add it to the ListView.
            boolean wasNewLogCreated = LogInputActivity.RESULT_NEW_LOG_CREATED == resultCode;
            messageId = wasNewLogCreated ? handleNewLog(data) : R.string.log_entry_cancelled;
            break;
         case REQUEST_VIEW_LOG:
            if (ViewLogEntryActivity.RESULT_LOG_DELETED == resultCode) {
               // Remove deleted LogEntry from the ListView.
               messageId = handleLogDeleted(data);
            } else if (ViewLogEntryActivity.RESULT_LOG_EDITED == resultCode) {
               // Remove the out-of-date LogEntry, then add the updated one.
               messageId = handleLogEdited(data);
            }
            break;
      }

      // Give the user a visual indicator of what happened.
      if (messageId != 0) {
         Snackbar.make(getView(), messageId, Snackbar.LENGTH_SHORT).show();
      }
   }

   /*
    * Add the new LogEntry to our collection.
    */
   private int handleNewLog(Intent data) {
      // If a new LogEntry was created we unpackage it and add it to the LogEntryCollection.
      LogEntry newLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      LogEntryCollection.getInstance().insertInChronoOrder(newLogEntry);
      return R.string.log_entry_confirmed;
   }

   /*
    * Remove the deleted LogEntry from our collection.
    */
   private int handleLogDeleted(Intent data) {
      LogEntry deletedLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      LogEntryCollection.getInstance().remove(deletedLogEntry);
      return R.string.log_entry_deleted;
   }

   /*
    * Update the edited LogEntry in our collection.
    */
   private int handleLogEdited(Intent data) {
      LogEntry updatedLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      // Remove the out-of-date LogEntry, then add the updated one.
      LogEntryCollection.getInstance().updateLogEntry(updatedLogEntry);
      return R.string.log_entry_updated;
   }
}
