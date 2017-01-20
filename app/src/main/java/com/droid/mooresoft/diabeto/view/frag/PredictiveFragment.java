package com.droid.mooresoft.diabeto.view.frag;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.ai.BgDecisionTree;
import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.DecisionTree;
import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.Instance;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.LogEntryCollection;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import java.util.List;

/**
 * Created by Edward Moore on 4/19/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class PredictiveFragment extends Fragment {
   private static final String TAG = PredictiveFragment.class.getSimpleName();

   private TextView mPredictiveText;
   private boolean mShouldGrowDecisionTree = true;
   private double mAccuracy;
   private BgDecisionTree mDecisionTree;

   /**
    * {@inheritDoc}
    */
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View root = inflater.inflate(R.layout.frag_predictive, container, false);
      mPredictiveText = (TextView) root.findViewById(R.id.text_predicted_blood_glucose);
      return root;
   }

   @Override
   public void onResume() {
      super.onResume();
      new PredictionTask().execute();
   }

   private class PredictionTask extends AsyncTask<Void, Void, String> {

      @Override
      protected String doInBackground(Void... params) {
         List<LogEntry> logEntryList = LogEntryCollection.getInstance().getCollection();

         if (mShouldGrowDecisionTree) {
            Log.d(TAG, "Beginning decision tree growth...");

            mShouldGrowDecisionTree = false;

            Log.d(TAG, "Generating instance lists...");
            List<Instance> allTrainingData =
                  BgDecisionTree.generateInstanceList(logEntryList, 0, System.currentTimeMillis(), getContext());
            List<Instance> trainingData =
                  BgDecisionTree.generateInstanceList(logEntryList, 0, System.currentTimeMillis() - 3 * DatetimeUtils.ONE_DAY, getContext());
            List<Instance> testingData =
                  BgDecisionTree.generateInstanceList(logEntryList, System.currentTimeMillis() - 3 * DatetimeUtils.ONE_DAY, System.currentTimeMillis(), getContext());
            Log.d(TAG, String.format("%s\n%d total instances, %d training instances and %d testing instances.",
                  "Instance lists generated:", allTrainingData.size(), trainingData.size(), testingData.size()));

            Log.d(TAG, "Building tree...");
            mDecisionTree = new BgDecisionTree(allTrainingData);
            DecisionTree testingTree = new BgDecisionTree(trainingData);
            Log.d(TAG, "Tree built.");
            Log.d(TAG, "Decision tree growth complete.");

            Log.d(TAG, "Evaluating decision tree performance...");
            mAccuracy = testingTree.computeAccuracy(testingData);
            Log.d(TAG, "Finished evaluating decision tree performace.");

            for (String s : mDecisionTree.print()) {
               Log.d(TAG, s);
            }
         }

         Log.d(TAG, "Generating query instance...");
         Instance now = BgDecisionTree.generateInstance(logEntryList, System.currentTimeMillis(), null);
         Log.d(TAG, "Query instance generated.");

         Log.d(TAG, "Classifying query...");
         return mDecisionTree.classify(now);
      }

      @Override
      protected void onPostExecute(String s) {
         Log.d(TAG, "Classification complete.");

         mPredictiveText.setText(String.format("Prediction: %s\nAI accuracy: %.2f", s, mAccuracy));
      }
   }
}
