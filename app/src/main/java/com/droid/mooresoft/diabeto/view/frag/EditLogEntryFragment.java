package com.droid.mooresoft.diabeto.view.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.input.LogInputControl;
import com.droid.mooresoft.diabeto.data.LogEntry;

/**
 * Created by Edward Moore on 4/9/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class EditLogEntryFragment extends Fragment {
   private static final String TAG = EditLogEntryFragment.class.getSimpleName();

   private LogInputControl mLogInputControl;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_edit_log_entry, container, false);

      mLogInputControl = new LogInputControl(root, getContext());

      // Check the bundled args for a LogEntry.
      Bundle args = getArguments();
      if (args != null) {
         LogEntry logEntry = args.getParcelable(LogEntry.EXTRA_LOG_ENTRY);
         // Use the bundled LogEntry to set default values to editable fields.
         mLogInputControl.setDefaults(logEntry);
      }

      return root;
   }

   public LogEntry generateLogEntry() {
      return mLogInputControl.generateLogEntry();
   }
}
