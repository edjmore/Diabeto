package com.droid.mooresoft.diabeto.ai.abstract_decision_tree;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class Instance {

   private final String mLabel;
   private final String[] mAttrValues;

   public Instance(String label, String[] attrValues) {
      mLabel = label;
      mAttrValues = attrValues;
   }

   public String getLabel() {
      return mLabel;
   }

   /**
    * Returns the value in this Instance of the given Attribute.
    *
    * @param attribute An Attribute of the Instance.
    * @return The value of the given Attribute.
    */
   public String getValueForAttribute(Attribute attribute) {
      int idx = attribute.getIndex();
      return mAttrValues[idx];
   }
}
