package com.droid.mooresoft.diabeto.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Edward Moore on 4/18/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class LogEntryCollection {
   private static final String TAG = LogEntryCollection.class.getSimpleName();

   /**
    * Implementers can listen for any change in the collection's data.
    */
   public interface OnChangeListener {
      enum Type {
         NEW_COLLECTION, LOG_ADDED, LOG_DELETED
      }

      /**
       * Called any time the collection changes.
       *
       * @param logEntry The particular LogEntry which was changed (may be null).
       * @param type     The Type of change which occured.
       */
      void onChange(@Nullable LogEntry logEntry, Type type);
   }

   private static final LogEntryCollection sInstance = new LogEntryCollection();

   private final List<LogEntry> mLogEntryList;
   private final Set<OnChangeListener> mListeners;

   /**
    * Creates a new, empty LogEntryCollection object.
    */
   private LogEntryCollection() {
      mLogEntryList = new ArrayList<>();
      mListeners = new HashSet<>();
   }

   public static LogEntryCollection getInstance() {
      return sInstance;
   }

   /**
    * @return The current LogEntry collection.
    */
   public List<LogEntry> getCollection() {
      return mLogEntryList;
   }

   /**
    * Clears the current collection, then asynchronously fetches all LogEntry objects from the
    * database and adds them to this collection.
    *
    * @param context The current Context.
    */
   public void initAsync(final Context context) {
      new AsyncTask<Void, Void, List<LogEntry>>() {
         @Override
         protected List<LogEntry> doInBackground(Void... params) {
            DbManager dbManager = DbManager.getInstance();
            return dbManager.fetchLogEntries(null, null, context);
         }

         @Override
         protected void onPostExecute(List<LogEntry> logEntryList) {
            Log.d(TAG, String.format("Fetched %d LogEntry objects", logEntryList.size()));
            mLogEntryList.clear();
            mLogEntryList.addAll(logEntryList);

            onChange(null, OnChangeListener.Type.NEW_COLLECTION);
         }
      }.execute();
   }

   /**
    * Removes the given LogEntry from the collection. Uses LogEntry.equals(1) to find the right
    * LogEntry to remove.
    *
    * @param logEntry The LogEntry to remove.
    * @return True iff a LogEntry is removed from the collection.
    */
   public boolean remove(LogEntry logEntry) {
      boolean wasRemoved = mLogEntryList.remove(logEntry);

      if (wasRemoved) {
         onChange(logEntry, OnChangeListener.Type.LOG_DELETED);
      }

      return wasRemoved;
   }

   /**
    * Updates a LogEntry in the collection. Searches the collection for a LogEntry whose ID matches
    * the parameter's ID. Then removes this LogEntry from the collection and replaces it with the
    * updated version.
    *
    * @param updatedLogEntry An updated version of a LogEntry in the collection.
    */
   public void updateLogEntry(LogEntry updatedLogEntry) {
      if (!removeById(updatedLogEntry)) {
         // This would be a programming error on my part.
         throw new IllegalStateException("Tried to update a LogEntry which is not in the collection");
      }
      // Updated version might have a new timestamp, so we use the chronological insert method.
      insertInChronoOrder(updatedLogEntry);
   }

   /**
    * Removes the LogEntry with the same ID as the given one from the collection.
    *
    * @param entryWithId Entry with the ID you want to remove.
    * @return True iff a LogEntry was removed from the collection.
    */
   private boolean removeById(LogEntry entryWithId) {
      for (int i = 0; i < mLogEntryList.size(); i++) {
         LogEntry logEntry = mLogEntryList.get(i);

         if (LogEntry.haveSameId(logEntry, entryWithId)) {
            mLogEntryList.remove(i);

            onChange(logEntry, OnChangeListener.Type.LOG_DELETED);

            return true;
         }
      }
      return false;
   }

   /**
    * Inserts the given LogEntry into the collection in chronological order (by timestamp).
    *
    * @param newLogEntry The new LogEntry to insert.
    */
   public void insertInChronoOrder(LogEntry newLogEntry) {
      int insertIdx = 0;
      for (LogEntry logEntry : mLogEntryList) {
         if (logEntry.getTime() <= newLogEntry.getTime()) break;
         insertIdx++;
      }

      if (insertIdx >= mLogEntryList.size()) mLogEntryList.add(newLogEntry);
      else mLogEntryList.add(insertIdx, newLogEntry);

      onChange(newLogEntry, OnChangeListener.Type.LOG_ADDED);
   }

   // CHANGE LISTENER METHODS //

   public boolean addListener(OnChangeListener listener) {
      return mListeners.add(listener);
   }

   public boolean removeListener(OnChangeListener listener) {
      return mListeners.remove(listener);
   }

   private void onChange(LogEntry logEntry, OnChangeListener.Type type) {
      for (OnChangeListener listener : mListeners) {
         if (listener != null) listener.onChange(logEntry, type);
      }
   }
}
