package com.droid.mooresoft.diabeto.control.input;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.TreatmentSettings;

/**
 * Created by Edward Moore on 4/14/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class SettingsInputControl {

   private View mRoot;
   private Context mContext;

   private TextView mActiveInsulinPeriod, mSensitivity, mCarbohydrateRatio, mBasalRate;

   /**
    * Creates a new SettingsInputControl object.
    *
    * @param root    A View which has all Settings input forms as children.
    * @param context The current Context.
    */
   public SettingsInputControl(View root, Context context) {
      mRoot = root;
      mContext = context;
      mActiveInsulinPeriod = (TextView) root.findViewById(R.id.edit_active_insulin_period);
      mSensitivity = (TextView) root.findViewById(R.id.edit_sensitivity);
      mCarbohydrateRatio = (TextView) root.findViewById(R.id.edit_carbohydrate_ratio);
      mBasalRate = (TextView) root.findViewById(R.id.edit_basal_rate);
      init();
   }

   /**
    * Fills in the current values for preferences in their respective input forms.
    */
   private void init() {
      float
            activeInsulinPeriod = TreatmentSettings.getActiveInsulinPeriod(mContext),
            sensitivity = TreatmentSettings.getSensitivity(mContext),
            carbohydrateRatio = TreatmentSettings.getCurrentCarbohydrateRatio(mContext),
            basalRate = TreatmentSettings.getCurrentBasalRate(mContext);
      mActiveInsulinPeriod.setText(String.format("%.2f", activeInsulinPeriod));
      mSensitivity.setText(String.format("%.2f", sensitivity));
      mCarbohydrateRatio.setText(String.format("%.2f", carbohydrateRatio));
      mBasalRate.setText(String.format("%.2f", basalRate));
   }

   /**
    * Saves any changes the user may have made to their settings.
    */
   public void commitChanges() {
      float activeInsulinPeriod = Float.parseFloat(mActiveInsulinPeriod.getText().toString()),
            sensitivity = Float.parseFloat(mSensitivity.getText().toString()),
            carbohydrateRatio = Float.parseFloat(mCarbohydrateRatio.getText().toString()),
            basalRate = Float.parseFloat(mBasalRate.getText().toString());
      boolean wasSuccessful =
            TreatmentSettings.setActiveInsulinPeriod(activeInsulinPeriod, mContext) &&
                  TreatmentSettings.setSensitivity(sensitivity, mContext) &&
                  TreatmentSettings.setCarbohydrateRatio(carbohydrateRatio, mContext) &&
                  TreatmentSettings.setBasalRate(basalRate, mContext);

      if (wasSuccessful) {
         // Assure the user their settings have been saved.
         Snackbar.make(mRoot, R.string.settings_saved, Snackbar.LENGTH_SHORT).show();
      } else {
         // TODO: This should never happen.
         throw new RuntimeException("Failed to commit pref changes");
      }
   }
}
