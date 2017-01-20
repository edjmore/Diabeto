package com.droid.mooresoft.diabeto.ai.abstract_decision_tree;

import com.droid.mooresoft.diabeto.ai.Bag;
import com.droid.mooresoft.diabeto.util.Statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * Disclaimer: This is similar to an implementation I did for CS 440, but that was only
 * a binary classifier, this is more flexible.
 */
public class DecisionTree {

   private final boolean mIsLeaf;
   private final String mLabel;
   private final Attribute mRootAttribute;
   private final List<DecisionTree> mChildren;
   private final String[] mPossibleLabels;

   public DecisionTree(List<Attribute> attributeList, List<Instance> instanceList, String[] possibleLabels) {
      mPossibleLabels = possibleLabels;

      if (mIsLeaf = shouldBeLeaf(attributeList, instanceList)) {
         // If this is a leaf node, we just need to determine its label.
         mLabel = computeLeafLabel(instanceList);

         // Leaves have no Attribute or children.
         mRootAttribute = null;
         mChildren = null;
      } else {
         // This is a regular node, so it has no label.
         mLabel = null;

         // Use the Attribute which will split the training instances best.
         mRootAttribute = computeBestAttribute(attributeList, instanceList);

         // We will have a subtree for each remaining Attribute.
         List<Attribute> remainingAttrList = getRemainingAttributes(attributeList, mRootAttribute);
         mChildren = new ArrayList<>(remainingAttrList.size());

         // For each subtree we need an Instance List where each Instance has the given Attribute value.
         for (String possibleVal : mRootAttribute.getPossibleValues()) {
            List<Instance> instanceSplit =
                  generateInstanceSplitForAttrVal(instanceList, mRootAttribute, possibleVal);
            DecisionTree child = new DecisionTree(remainingAttrList, instanceSplit, possibleLabels);
            mChildren.add(child);
         }
      }
   }

   /**
    * This method must return an array of all possible labels the DecisionTree may use to classify an Instance.
    *
    * @return An array of all possible Instance labels.
    */
   public String[] getPossibleLabels() {
      return mPossibleLabels;
   }

   /**
    * Returns the a labeled classification of the given Instance.
    *
    * @param instance The Instance to classify.
    * @return A label for the given Instance.
    */
   public String classify(final Instance instance) {
      // If we found a leaf we're done!
      if (mIsLeaf) return mLabel;

      // Otherwise, we choose a subtree based on the splitting Attribute.
      String attrVal = instance.getValueForAttribute(mRootAttribute);
      int childIdx = mRootAttribute.lookupAttrValIdx(attrVal);

      // Recursion magic.
      DecisionTree subtree = mChildren.get(childIdx);
      return subtree.classify(instance);
   }

   /**
    * Computes the accuracy of the DecisionTree on the set of training Instances.
    *
    * @param testingData A List of training Instances.
    * @return The percentage of training Instances correctly classified.
    */
   public double computeAccuracy(List<Instance> testingData) {
      int numRight = 0;
      for (Instance instance : testingData) {
         String actualLabel = instance.getLabel();
         String computedLabel = classify(instance);
         if (actualLabel.equals(computedLabel)) numRight++;
      }
      return (double) numRight / testingData.size();
   }

   /**
    * Determine if this DecisionTree should be a leaf instead of a subtree.
    *
    * @param attributeList List of Attributes.
    * @param instanceList  List of Instances.
    * @return True iff this should be a leaf.
    */
   private boolean shouldBeLeaf(List<Attribute> attributeList, List<Instance> instanceList) {
      // If the Attribute List is empty, this must be a leaf.
      if (attributeList.isEmpty()) return true;

      // If there are no more Instances, this is a leaf.
      if (instanceList.isEmpty()) return true;

      // If all Instances have the same label this should be a leaf, otherwise no.
      String label = instanceList.get(0).getLabel();
      for (Instance instance : instanceList) {
         if (!instance.getLabel().equals(label)) return false;
      }
      return true;
   }

   /**
    * Computes a label for this leaf. The label is simply the label which shows up the most in
    * the given Instance List.
    *
    * @param instanceList A List of Instances.
    * @return The label for this leaf.
    */
   private String computeLeafLabel(List<Instance> instanceList) {
      Bag<String> labelBag = new Bag<>();
      for (Instance instance : instanceList) {
         labelBag.add(instance.getLabel());
      }
      String bestLabel = null;
      int bestLabelCount = 0;
      for (String label : labelBag) {
         int count = labelBag.getCount(label);
         if (null == bestLabel || bestLabelCount < count) {
            bestLabel = label;
            bestLabelCount = count;
         }
      }
      return null == bestLabel ? "Unknown" : bestLabel;
   }

   /**
    * Computes the best Attribute for partitioning the Instance List, a.k.a. the one which
    * yields the highest information gain (least entropy).
    *
    * @param attributeList A List of Attributes.
    * @param instanceList  A List of Instances.
    * @return The most meaningful Attribute.
    */
   private Attribute computeBestAttribute(List<Attribute> attributeList, List<Instance> instanceList) {
      double minEntropy = Double.MAX_VALUE;
      Attribute bestAttr = null;

      // Compute entropy for each Attribute, keeping track of the best one (w/ smallest entropy).
      for (Attribute attr : attributeList) {

         // Attribute entropy is the sum of all the scaled entropies of each Attribute value.
         double entropy = 0;
         for (String attrVal : attr.getPossibleValues()) {
            entropy += computeScaledEntropy(attr, attrVal, instanceList);
         }

         if (null == bestAttr || entropy < minEntropy) {
            // This Attribute has the smallest entropy so far, so it is the new best.
            bestAttr = attr;
            minEntropy = entropy;
         }
      }

      return bestAttr;
   }

   private double computeScaledEntropy(Attribute attribute, String attrVal, List<Instance> instanceList) {
      // Determine the frequency of each label within the Instances which have the given Attribute value.
      Bag<String> labelBag = new Bag<>();
      for (Instance instance : instanceList) {
         String instanceAttrVal = instance.getValueForAttribute(attribute);
         // If this instance has the Attribute value we are interested in, we add its label to the Bag.
         if (instanceAttrVal.equals(attrVal)) {
            String label = instance.getLabel();
            labelBag.add(label);
         }
      }
      // Use the Bag to match up labels with frequencies, and record the number of Instances we
      // found which have our chosen Attribute value.
      String[] allLabels = getPossibleLabels();
      int[] labelFrequencies = new int[allLabels.length];
      int numInstancesWithAttrVal = 0;
      for (int i = 0; i < labelFrequencies.length; i++) {
         String label = allLabels[i];
         int frequency = labelBag.getCount(label);
         labelFrequencies[i] = frequency;
         numInstancesWithAttrVal += frequency;
      }

      if (0 == numInstancesWithAttrVal) return 1; // Done!

      // Now we can calculate the probability of each label within the Instances which have our Attribute value.
      double[] labelProbabilities = new double[allLabels.length];
      for (int i = 0; i < labelProbabilities.length; i++) {
         double probability = labelFrequencies[i] / numInstancesWithAttrVal;
         labelProbabilities[i] = probability;
      }

      // Return entropy scaled by Attribute value probability wihin the Instance List.
      double scale = numInstancesWithAttrVal / instanceList.size();
      return scale * Statistics.entropy(labelProbabilities);
   }

   /**
    * Returns a new Attribute List which is exactly the given List, minus the root Attribute.
    *
    * @param attributeList List of candidate Attributes at this subtree.
    * @param rootAttribute The root Attribute of the subtree.
    * @return A List of remaining Attributes for the subtree.
    */
   private List<Attribute> getRemainingAttributes(List<Attribute> attributeList, Attribute rootAttribute) {
      List<Attribute> result = new ArrayList<>(attributeList.size() - 1);
      for (Attribute attribute : attributeList) {
         if (attribute != rootAttribute) result.add(attribute);
      }
      return result;
   }

   /**
    * Splits the data on an Attribute-value pair (i.e. returns a List of Instances which have
    * the given value for the given Attribute).
    *
    * @param instanceList   A List of Instances.
    * @param splitAttribute The Attribute to split on.
    * @param splitVal       The Attribute value to split on.
    * @return A List of Instances that constitute the said split.
    */
   private List<Instance> generateInstanceSplitForAttrVal(List<Instance> instanceList, Attribute splitAttribute, String splitVal) {
      List<Instance> result = new ArrayList<>();
      for (Instance instance : instanceList) {
         // If the Instance has the given Attribute, we add it to the result List.
         String instanceVal = instance.getValueForAttribute(splitAttribute);
         if (instanceVal.equals(splitVal)) {
            result.add(instance);
         }
      }
      return result;
   }

   // TEMPORARY PRINTING METHODS //

   public List<String> print() {
      String s = print(0);
      List<String> ss = new ArrayList<>();
      while (s.length() > 1024) {
         ss.add(s.substring(0, 1023));
         s = s.substring(1023);
      }
      ss.add(s);
      return ss;
   }

   private String print(final int rootDepth) {
      String s = "";
      if (!mIsLeaf) {
         final Iterator<DecisionTree> itr = mChildren.iterator();
         for (final String possibleAttrVal : mRootAttribute.getPossibleValues()) {
            s += printIndent(rootDepth);
            s += mRootAttribute.getName() + " = " + possibleAttrVal + " :\n";
            s += itr.next().print(rootDepth + 1);
         }
      } else {
         s += printIndent(rootDepth);
         s += mLabel + "\n";
      }
      return s;
   }

   private String printIndent(final int n) {
      String s = "";
      for (int i = 0; i < n; i++) {
         s += "\t";
      }
      return s;
   }
}
