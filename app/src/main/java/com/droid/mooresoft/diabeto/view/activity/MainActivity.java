package com.droid.mooresoft.diabeto.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androidplot.xy.XYPlot;
import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.XYPlotControl;
import com.droid.mooresoft.diabeto.data.FileIO;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.external.AlarmHelper;
import com.droid.mooresoft.diabeto.external.SharingIsCaring;
import com.droid.mooresoft.diabeto.view.frag.AnalyticsFragment;
import com.droid.mooresoft.diabeto.view.frag.LogsByDayFragment;
import com.droid.mooresoft.diabeto.view.frag.PredictiveFragment;
import com.droid.mooresoft.diabeto.view.frag.SettingsFragment;
import com.droid.mooresoft.diabeto.view.frag.StatusFragment;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
   private static final String TAG = MainActivity.class.getSimpleName();

   private View mNavBarView, mGraphView;
   private XYPlotControl mXyPlotControl;

   /**
    * {@inheritDoc}
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Setup alarms.
      // TODO: I'm not sure what happens when you set the same alarm multiple times?
      AlarmHelper.setTestingAlarm(this);

      // Asynchronously fetch LogEntry objects from the database.
      LogEntryCollection.getInstance().initAsync(this);

      // We use two views for this activity. A nav bar view for portrait mode, and a graph view
      // for landscape mode. We inflate both here so we can dynamically switch between the two.
      mNavBarView = getLayoutInflater().inflate(R.layout.activity_main, null);
      mGraphView = getLayoutInflater().inflate(R.layout.activity_main_landscape, null);

      // Initialize both views.
      initNavBarView();
      initGraphView();

      // We let the configuration handler choose which view to show.
      onConfigurationChanged(getResources().getConfiguration());
   }

   /**
    * Initializes the navigation bar view (used in portrait mode).
    */
   private void initNavBarView() {
      Toolbar toolbar = (Toolbar) mNavBarView.findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      DrawerLayout drawer = (DrawerLayout) mNavBarView.findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      drawer.setDrawerListener(toggle);
      toggle.syncState();

      NavigationView navigationView = (NavigationView) mNavBarView.findViewById(R.id.nav_view);
      navigationView.setNavigationItemSelectedListener(this);
   }

   /**
    * Initializes the graph view (used in landscape mode).
    */
   private void initGraphView() {
      XYPlot plot = (XYPlot) mGraphView.findViewById(R.id.plot);
      mXyPlotControl = new XYPlotControl(plot, this);
      LogEntryCollection.getInstance().addListener(mXyPlotControl);
   }

   /**
    * Handles selection of navigation drawer menu items.
    *
    * @param item The selected MenuItem.
    * @return True iff we have completely handled the selection.
    */
   @Override
   public boolean onNavigationItemSelected(MenuItem item) {
      // Selection of most of the menu items will require us
      // to replace the current fragment w/ a new one.
      Fragment newFrag = null;
      int newTitle = 0;

      // Hide the FAB by default, only the ViewAllLogsFragment will use it.
      FloatingActionButton fab = (FloatingActionButton) mNavBarView.findViewById(R.id.fab_new_log);
      fab.setVisibility(View.INVISIBLE);

      int id = item.getItemId();
      switch (id) {
         case R.id.nav_logs:
            newFrag = new LogsByDayFragment();
            newTitle = R.string.title_logs;
            // Show the FAB and set the fragment to handle any clicks on it.
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener((LogsByDayFragment) newFrag);
            break;

         case R.id.nav_status:
            newFrag = new StatusFragment();
            newTitle = R.string.title_status;
            break;

         case R.id.nav_analytics:
            newFrag = new AnalyticsFragment();
            newTitle = R.string.title_analytics;
            break;

         case R.id.nav_predictive:
            newFrag = new PredictiveFragment();
            newTitle = R.string.title_predictive;
            break;

         case R.id.nav_settings:
            newFrag = new SettingsFragment();
            newTitle = R.string.title_settings;
            break;

         case R.id.nav_export:
            new AsyncTask<Void, Void, Uri>() {
               @Override
               protected Uri doInBackground(Void... params) {
                  List<LogEntry> logEntryList = LogEntryCollection.getInstance().getCollection();
                  return FileIO.exportLogEntries(logEntryList, MainActivity.this);
               }

               @Override
               protected void onPostExecute(Uri file) {
                  Intent sharingIntent = SharingIsCaring.shareLogEntries(file, MainActivity.this);
                  startActivity(sharingIntent);
               }
            }.execute();
            break;
      }

      if (newFrag != null && findViewById(R.id.frag_container) != null) {
         // Replace the current fragment w/ the new one.
         FragmentManager fm = getSupportFragmentManager();
         fm.beginTransaction().replace(R.id.frag_container, newFrag).commit();
         // New fragment needs new title.
         getSupportActionBar().setTitle(newTitle);
      }

      DrawerLayout drawer = (DrawerLayout) mNavBarView.findViewById(R.id.drawer_layout);
      drawer.closeDrawer(GravityCompat.START);

      return true;
   }

   /**
    * Overrides standard behaviour of the system navigation bar's 'back' button. If it is pressed
    * while the navigation drawer is open we close the drawer. Otherwise, we default back to
    * the standard behaviour.
    */
   @Override
   public void onBackPressed() {
      DrawerLayout drawer = (DrawerLayout) mNavBarView.findViewById(R.id.drawer_layout);
      if (drawer.isDrawerOpen(GravityCompat.START)) {
         drawer.closeDrawer(GravityCompat.START);
      } else {
         super.onBackPressed();
      }
   }

   /**
    * When the user switches to landscape mode we show a graph plotting LogEntry fields over time.
    *
    * @param newConfig The new Configuration.
    */
   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
         setContentView(mGraphView);
      } else {
         setContentView(mNavBarView);

         // See if any of the menu items have been selected.
         Menu menu = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
         boolean hasCheckedItem = false;
         for (int i = 0; i < menu.size() && !hasCheckedItem; i++) {
            if (menu.getItem(i).isChecked()) hasCheckedItem = true;
         }
         // If no menu item selected, choose the default.
         if (!hasCheckedItem) {
            MenuItem defState = menu.getItem(0);
            onNavigationItemSelected(defState);
            defState.setChecked(true);
         }
      }
   }
}
