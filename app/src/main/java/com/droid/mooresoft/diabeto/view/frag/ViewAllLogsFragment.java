package com.droid.mooresoft.diabeto.view.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.adapter.LogListAdapter;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.view.activity.LogInputActivity;
import com.droid.mooresoft.diabeto.view.activity.MainActivity;
import com.droid.mooresoft.diabeto.view.activity.ViewLogEntryActivity;

/**
 * Created by Edward Moore on 4/18/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * This Fragment shows a ListView with all LogEntry objects in the current LogEntryCollection. At
 * this point it should only be used in the MainActvity b/c it handles clicks on the FAB in the
 * app_bar_main.xml file.
 *
 * @see MainActivity
 * @see LogEntryCollection
 * @see LogListAdapter
 */
@Deprecated
public class ViewAllLogsFragment extends Fragment implements ListView.OnItemClickListener, View.OnClickListener {
   private static final String TAG = ViewAllLogsFragment.class.getSimpleName();

   public static final int
         REQUEST_NEW_LOG = 0x01,
         REQUEST_VIEW_LOG = 0x10;

   private LogListAdapter mLogListAdapter;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_view_all_logs, container, false);

      // Setup the LogListAdapter and attach it to the ListView.
      mLogListAdapter = new LogListAdapter(getContext(), LogEntryCollection.getInstance().getCollection());
      mLogListAdapter.setNotifyOnChange(true);
      ListView listView = (ListView) root.findViewById(R.id.list_logs);
      listView.setAdapter(mLogListAdapter);
      listView.setOnItemClickListener(this);

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

   //            CASE HANDLERS FOR ACTIVITY RESULTS               //
   // All return an ID for a string resource which should be used //
   // in a Snackbar to alert the user to whatever action occured. //

   /*
    * Add the new LogEntry to our collection.
    */
   private int handleNewLog(Intent data) {
      // If a new LogEntry was created we unpackage it and add it to the ListView.
      LogEntry newLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      LogEntryCollection.getInstance().insertInChronoOrder(newLogEntry);
      mLogListAdapter.notifyDataSetChanged();
      return R.string.log_entry_confirmed;
   }

   /*
    * Remove the deleted LogEntry from our collection.
    */
   private int handleLogDeleted(Intent data) {
      LogEntry deletedLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      LogEntryCollection.getInstance().remove(deletedLogEntry);
      mLogListAdapter.notifyDataSetChanged();
      return R.string.log_entry_deleted;
   }

   /*
    * Update the edited LogEntry in our collection.
    */
   private int handleLogEdited(Intent data) {
      LogEntry updatedLogEntry = data.getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      // Remove the out-of-date LogEntry, then add the updated one.
      LogEntryCollection.getInstance().updateLogEntry(updatedLogEntry);
      mLogListAdapter.notifyDataSetChanged();
      return R.string.log_entry_updated;
   }

   /**
    * Handles clicks on specific LogEntry items in the ListView.
    *
    * @param parent   The LogEntry ListView.
    * @param view     The View which was clicked.
    * @param position The index of the LogEntry item backing the clicked View.
    * @param id       The ID of the clicked View.
    */
   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      LogEntry logEntry = mLogListAdapter.getItem(position);
      Intent viewLogEntryActivity =
            new Intent(getContext(), ViewLogEntryActivity.class)
                  .putExtra(LogEntry.EXTRA_LOG_ENTRY, logEntry);
      startActivityForResult(viewLogEntryActivity, REQUEST_VIEW_LOG);
   }
}
