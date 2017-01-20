package com.droid.mooresoft.diabeto.data;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.droid.mooresoft.diabeto.util.CsvUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Moore on 4/28/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class FileIO {
   private static final String TAG = FileIO.class.getSimpleName();

   public static Uri exportLogEntries(List<LogEntry> logEntryList, Context context) {
      // First we generate a CSV representation of the LogEntry List.
      List<List<String>> csvRecordList = new ArrayList<>(logEntryList.size());
      for (LogEntry log : logEntryList) {
         List<String> record = log.toCsvRecord();
         csvRecordList.add(record);
      }

      // Now we create a temp File to write to and open an OutputStream.
      try {
         File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
         File tempFile =
               File.createTempFile("diabeto_logbook", ".csv", dir);

         FileOutputStream fos = new FileOutputStream(tempFile);
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

         // Write the CSV records to the File.
         CsvUtils.writeCsv(writer, csvRecordList);

         if (writer != null) writer.close();
         if (fos != null) fos.close();

         Log.d(TAG, String.format("getPath():\t\t%s\ngetAbsolutePath():\t\t%s\ngetCanonicalPath():\t\t%s\ntoString():\t\t%s",
               tempFile.getPath(), tempFile.getAbsolutePath(), tempFile.getCanonicalPath(), tempFile.toString()));

         return Uri.parse("file://" + tempFile.toString());
      } catch (IOException e) {
         // TODO: Handle failure more gracefully.
         throw new RuntimeException(e);
      }
   }
}
