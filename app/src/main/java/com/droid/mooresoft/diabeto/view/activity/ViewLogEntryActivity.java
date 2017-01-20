package com.droid.mooresoft.diabeto.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.DbManager;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.view.frag.EditLogEntryFragment;
import com.droid.mooresoft.diabeto.view.frag.ViewLogEntryFragment;

public class ViewLogEntryActivity extends AppCompatActivity {
   private static final String TAG = ViewLogEntryActivity.class.getSimpleName();

   public static final int
         RESULT_LOG_EDITED = 0x01,
         RESULT_LOG_DELETED = 0x10;

   private LogEntry mCurLogEntry, mOrigLogEntry;
   private Menu mMenu;

   /**
    * {@inheritDoc}
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_view_log_entry);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      // Unpackage the provided LogEntry and display it in a ViewLogEntryFragment.
      mCurLogEntry = getIntent().getParcelableExtra(LogEntry.EXTRA_LOG_ENTRY);
      setFragment(new ViewLogEntryFragment());

      // Save a version of the original LogEntry so we can remove it from the ListView
      // in our parent activity if the user deletes it.
      mOrigLogEntry = new LogEntry();
      LogEntry.copy(mCurLogEntry, mOrigLogEntry);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.view_log_entry, menu);
      mMenu = menu;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      switch (id) {
         case R.id.action_edit:
            // Switch to editing mode.
            handleEdit();
            break;
         case R.id.action_confirm:
            // Update LogEntry with values inputted by the user.
            handleConfirm();
            // Fall thru...
         case R.id.action_cancel:
            // Switch back to viewing mode.
            handleCancel();
            break;
         case R.id.action_delete:
            // Delete the LogEntry.
            handleDelete();
         default:
            return false; // Use default 'home' arrow functionality.
      }
      return true;
   }

   // MENU ITEM SELECTION HANDLERS //

   private void handleEdit() {
      setFragment(new EditLogEntryFragment());
      mMenu.setGroupVisible(R.id.group_edit_mode, true);
      mMenu.setGroupVisible(R.id.group_view_mode, false);
   }

   private void handleConfirm() {
      EditLogEntryFragment editLogFrag =
            (EditLogEntryFragment) getSupportFragmentManager().findFragmentById(R.id.frag_container);
      LogEntry newLogEntry = editLogFrag.generateLogEntry();
      LogEntry.copy(newLogEntry, mCurLogEntry); // Copy updated values to LogEntry (keep ID).

      boolean wasUpdated = DbManager.getInstance().updateLogEntry(mCurLogEntry, this);
      Log.d(TAG, wasUpdated ? "LogEntry updated" : "Failed updating LogEntry");

      // Return the updated LogEntry to the caller.
      Intent updatedData = new Intent().putExtra(LogEntry.EXTRA_LOG_ENTRY, mCurLogEntry);
      setResult(RESULT_LOG_EDITED, updatedData);
   }

   private void handleCancel() {
      setFragment(new ViewLogEntryFragment());
      mMenu.setGroupVisible(R.id.group_edit_mode, false);
      mMenu.setGroupVisible(R.id.group_view_mode, true);
   }

   private void handleDelete() {
      boolean wasDeleted = DbManager.getInstance().deleteLogEntry(mCurLogEntry, this);

      // TODO: Deletion should never fail.
      if (wasDeleted) Log.d(TAG, "LogEntry deleted");
      else throw new RuntimeException("Failed to delete LogEntry");

      // Let the caller know that this LogEntry was deleted.
      Intent deletedData = new Intent().putExtra(LogEntry.EXTRA_LOG_ENTRY, mOrigLogEntry);
      setResult(RESULT_LOG_DELETED, deletedData);
      finish();
   }

   /**
    * Bundles up the LogEntry member variable and passes it to the given fragment. Then replaces
    * the current fragment with the given fragment.
    *
    * @param fragment The fragment to display now.
    */
   private void setFragment(Fragment fragment) {
      Bundle args = new Bundle();
      args.putParcelable(LogEntry.EXTRA_LOG_ENTRY, mCurLogEntry);
      fragment.setArguments(args);
      FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().replace(R.id.frag_container, fragment).commit();
   }
}
