package com.droid.mooresoft.diabeto.ai;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class BagTest extends TestCase {

   private static final int
         NUM_DISTINCT_ITEMS = 10,
         MAX_INSTANCES_PER_ITEM = 50;

   private final Random mRandom = new Random();

   private Bag<String> mBag;
   private String[] mDistinctItems;
   private int[] mNumItemInstances;

   /**
    * Initialize a new Bag with some random stuff in it.
    *
    * @throws Exception No. Just don't.
    */
   public void setUp() throws Exception {
      mBag = new Bag<>();
      mDistinctItems = new String[NUM_DISTINCT_ITEMS];
      mNumItemInstances = new int[NUM_DISTINCT_ITEMS];

      // Generate some random items to put in the Bag.
      for (int i = 0; i < mDistinctItems.length; i++) {
         mDistinctItems[i] = getRandomString();
      }

      for (int i = 0; i < mDistinctItems.length; i++) {
         String item = mDistinctItems[i];

         // Add random amounts of each item to the bag.
         int numInstances = mRandom.nextInt(MAX_INSTANCES_PER_ITEM);
         for (int j = 0; j < numInstances; j++) {
            mBag.add(item);
         }

         // Remove a random amount from what we just added.
         int instancesRemoved = mRandom.nextInt(MAX_INSTANCES_PER_ITEM);
         for (int j = 0; j < instancesRemoved; j++) {
            mBag.remove(item);
         }

         mNumItemInstances[i] = Math.max(0, numInstances - instancesRemoved);
      }
   }

   private String getRandomString() {
      return new BigInteger(128, mRandom).toString();
   }

   public void testGetCount() throws Exception {
      for (int i = 0; i < mDistinctItems.length; i++) {
         final int expectedCount = mNumItemInstances[i];

         String item = mDistinctItems[i];
         int count = mBag.getCount(item);

         assertEquals(expectedCount, count);
      }
   }
}