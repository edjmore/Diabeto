package com.droid.mooresoft.diabeto.util;

import android.util.Pair;

import java.util.List;

/**
 * Created by Edward Moore on 4/20/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class Statistics {

   /**
    * Calculates the mean value for the given List.
    *
    * @param numList A List of Numbers.
    * @return The average value.
    */
   public static double average(List<Number> numList) {
      double sum = 0, count = numList.size();
      for (Number n : numList) {
         sum += n.doubleValue();
      }
      return sum / count;
   }

   /**
    * Calculates the standard deviation for the given List.
    *
    * @param numList A List of Numbers.
    * @param mean    The average value for the List.
    * @return The standard deviation from the mean.
    */
   public static double stdDev(List<Number> numList, double mean) {
      return Math.sqrt(variance(numList, mean));
   }

   /**
    * Calculates the variance of the given List.
    *
    * @param numList A List of Numbers.
    * @param mean    The average value for the List.
    * @return The variance from the mean.
    */
   public static double variance(List<Number> numList, double mean) {
      double sum = 0, count = numList.size();
      for (Number n : numList) {
         double aDouble = n.doubleValue();
         double
               diff = mean - aDouble,
               diff2 = diff * diff;
         sum += diff2;
      }
      return sum / count;
   }

   /**
    * Calculates the min and max values in the List.
    *
    * @param numList A List of Numbers.
    * @return A Pair of Doubles, the first being the min value, and the second the max.
    */
   public static Pair<Double, Double> minMax(List<Number> numList) {
      double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
      for (Number n : numList) {
         double aDouble = n.doubleValue();
         min = Math.min(aDouble, min);
         max = Math.max(aDouble, max);
      }
      return new Pair<>(min, max);
   }

   /**
    * Returns the entropy of the given array of probabilities.
    *
    * @param probabilites An array of probabilities.
    * @return The entropy of the probabilities.
    */
   public static double entropy(double[] probabilites) {
      double entropy = 0;
      for (double p : probabilites) {
         entropy -= p * log2(p);
      }
      return entropy;
   }

   private static double log2(double n) {
      if (0 == n) return 0;
      return Math.log(n) / Math.log(2);
   }
}
