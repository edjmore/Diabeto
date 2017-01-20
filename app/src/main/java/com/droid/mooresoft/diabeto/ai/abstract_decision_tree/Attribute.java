package com.droid.mooresoft.diabeto.ai.abstract_decision_tree;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class Attribute {

   private final String mName;
   private final int mIndex;
   private final String[] mPossibleValues;

   public Attribute(String name, int index, String[] possibleValues) {
      mName = name;
      mIndex = index;
      mPossibleValues = possibleValues;
   }

   public String getName() {
      return mName;
   }

   public int getIndex() {
      return mIndex;
   }

   public String[] getPossibleValues() {
      return mPossibleValues;
   }

   /**
    * Looks up the index of the given Attribute value in the array of possible Attribute values.
    *
    * @param attrVal A possible value of the Attribute.
    * @return The index of the given Attribute value.
    */
   public int lookupAttrValIdx(String attrVal) {
      for (int i = 0; i < mPossibleValues.length; i++) {
         String possibleAttrVal = mPossibleValues[i];
         if (possibleAttrVal.equals(attrVal)) {
            return i;
         }
      }
      throw new RuntimeException(String.format("Index of attrVal '%s' not found"));
   }
}
