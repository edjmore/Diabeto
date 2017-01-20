package com.droid.mooresoft.diabeto.external;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;

import com.droid.mooresoft.diabeto.ai.BgDecisionTree;
import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.Instance;
import com.droid.mooresoft.diabeto.data.DbManager;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import java.util.List;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class AiService extends IntentService {
   private static final String TAG = AiService.class.getSimpleName();

   public AiService() {
      super(TAG);
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      // Get all LogEntry objects from the database.
      DbManager dbManager = DbManager.getInstance();
      List<LogEntry> logEntryList = dbManager.fetchLogEntries(null, null, this);

      // Predict a blood glucose value 30 minutes out.
      List<Instance> trainingData =
            BgDecisionTree.generateInstanceList(logEntryList, 0, System.currentTimeMillis(), this);
      BgDecisionTree bgDecisionTree = new BgDecisionTree(trainingData);
      Instance curInstance =
            BgDecisionTree.generateInstance(logEntryList, System.currentTimeMillis() + 30 * DatetimeUtils.ONE_MINUTE, null);
      String warning = bgDecisionTree.classify(curInstance);

      // Show the prediction in a Notification.
      Notification note = Notify.buildBgWarningNotification(
            String.format("Lookout for a %s blood glucose soon.", warning),
            this
      );
      Notify.postNotification(1, note, this);
   }
}
