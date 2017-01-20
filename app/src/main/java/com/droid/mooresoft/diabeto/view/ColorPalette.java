package com.droid.mooresoft.diabeto.view;

import android.content.Context;
import android.view.View;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.data.TreatmentSettings;

/**
 * Created by Edward Moore on 4/20/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class ColorPalette {

   /**
    * Styles the given view displaying a blood glucose value.
    *
    * @param bloodGlucose     The blood glucose value.
    * @param bloodGlucoseView The View displaying the blood glucose value.
    * @param context          The current Context.
    */
   public static void styleBloodGlucose(double bloodGlucose, View bloodGlucoseView, Context context) {
      double
            hypo = TreatmentSettings.getHypoCutoff(context),
            hyper = TreatmentSettings.getHyperCutoff(context);

      int resId;
      if (bloodGlucose < hypo) resId = R.drawable.circle_red;
      else if (bloodGlucose >= hypo && bloodGlucose <= hyper) resId = R.drawable.circle_green;
      else resId = R.drawable.circle_orange;

      bloodGlucoseView.setBackgroundResource(resId);
   }

   /**
    * Styles a group View.
    *
    * @param group      The View group.
    * @param isSelected If the group is selected or not.
    */
   public static void styleGroup(View group, boolean isSelected) {
      int resId = isSelected ? android.R.color.background_light : android.R.color.background_light;
      group.setBackgroundResource(resId);
   }
}
