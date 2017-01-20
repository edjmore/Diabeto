package com.droid.mooresoft.diabeto.view.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.control.input.SettingsInputControl;

/**
 * Created by Edward Moore on 4/14/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class SettingsFragment extends Fragment {

   private SettingsInputControl mSettingsInputControl;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_settings, container, false);
      // Controller will handle all user interaction w/ the Fragment.
      mSettingsInputControl = new SettingsInputControl(root, getContext());
      return root;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onPause() {
      super.onPause();
      // Save any changes the user made to their settings.
      mSettingsInputControl.commitChanges();
   }
}
