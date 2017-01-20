package com.droid.mooresoft.diabeto.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.DbManager;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.view.frag.EditLogEntryFragment;

public class LogInputActivity extends AppCompatActivity {
   private static final String TAG = LogInputActivity.class.getSimpleName();

   public static final int RESULT_NEW_LOG_CREATED = 0x1;

   /**
    * {@inheritDoc}
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_log_input);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.log_input, menu);
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (R.id.action_confirm == id) {
         handleNewLogEntry();
      }

      finish();
      return true;
   }

   private void handleNewLogEntry() {
      // Create a new LogEntry from user input and insert it into the database.
      EditLogEntryFragment editLogEntryFragment =
            (EditLogEntryFragment) getSupportFragmentManager().findFragmentById(R.id.frag_edit_log_entry);
      LogEntry logEntry = editLogEntryFragment.generateLogEntry();

      DbManager dbManager = DbManager.getInstance();
      boolean wasSuccessful = dbManager.insertLogEntry(logEntry, this);
      Log.d(TAG, wasSuccessful ? "New LogEntry inserted" : "Failed to insert new LogEntry");

      // Set activity result code to indicate success and send the new
      // LogEntry back to parent process in a Parcel.
      int resultCode = RESULT_NEW_LOG_CREATED;
      Intent data = new Intent().putExtra(LogEntry.EXTRA_LOG_ENTRY, logEntry);
      setResult(resultCode, data);
   }
}
