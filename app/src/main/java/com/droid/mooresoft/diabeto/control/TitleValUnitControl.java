package com.droid.mooresoft.diabeto.control;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;

/**
 * Created by Edward Moore on 4/26/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * Helper class to be used with the R.layout.merge_title_val_unit.xml layout. Once instantiated
 * w/ a View group to control it provides methods for easily setting the title, value, and unit
 * for the group.
 * TODO: Might be able to just make this into an extension of {@link View}.
 */
public class TitleValUnitControl {

   private Context mContext;
   private TextView mTitleView, mValView, mUnitView;

   /**
    * Creates a new TitleValUnitControl.
    *
    * @param titleValUnitLayout The View group you would like to control.
    * @param context            The current Context.
    */
   public TitleValUnitControl(View titleValUnitLayout, Context context) {
      mContext = context;
      mTitleView = (TextView) titleValUnitLayout.findViewById(R.id.text_title);
      mValView = (TextView) titleValUnitLayout.findViewById(R.id.text_val);
      mUnitView = (TextView) titleValUnitLayout.findViewById(R.id.text_unit);

      // Sanity check.
      if (null == mTitleView || null == mValView || null == mUnitView) {
         throw new IllegalArgumentException("titleValUnitLayout doesn't contain the required views");
      }
   }

   public void setTitle(int resId) {
      String title = mContext.getString(resId);
      setTitle(title);
   }

   public void setTitle(String title) {
      mTitleView.setText(title);
   }

   public void setIntegerVal(int val) {
      setDecimalVal(val, 0);
   }

   public void setDecimalVal(double val, int precision) {
      String formatString = "%." + String.format("%d", precision) + "f";
      String valString = String.format(formatString, val);
      mValView.setText(valString);
   }

   public void setUnit(int resId) {
      String unit = mContext.getString(resId);
      setUnit(unit);
   }

   public void setUnit(String unit) {
      mUnitView.setText(unit);
   }
}
