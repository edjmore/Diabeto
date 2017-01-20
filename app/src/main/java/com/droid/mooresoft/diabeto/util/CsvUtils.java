package com.droid.mooresoft.diabeto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for reading and writing CSV records. This class is poorly written, and
 * most likely has several bugs.
 * TODO: Make this nice, write some unit tests.
 *
 * @author Edward Moore
 */
@Deprecated
public class CsvUtils {

   /**
    * Reads a CSV file through the provided buffered stream and returns a list
    * of records encoded in the file.
    *
    * @param br A buffered stream of the CSV file to be read.
    * @return A list of records (each record is a list of strings).
    * @throws IOException An exception will be thrown if there is a problem with the file stream.
    */
   public static List<List<String>> readCsv(BufferedReader br) throws IOException {
      List<List<String>> records = new ArrayList<>();

      List<String> curRecord = new ArrayList<>();
      String curVal = "";

      final int
            STATE_LEADING_WHITESPACE = 0,       // In the whitespace before a value.
            STATE_QUOTE = 1,                    // Inside a quoted value.
            STATE_NO_QUOTE = 2,                 // Inside a non-quoted value.
            STATE_EMBEDDED_QUOTE = 3,           // Inside a quoted value and the previous character was a quote.
            STATE_TRAILING_WHITESPACE = 4;      // In the whitespace after a value.
      int state = STATE_LEADING_WHITESPACE;

      int c;
      while ((c = br.read()) != -1) {

         switch (state) {
            case STATE_LEADING_WHITESPACE:
               if (' ' == c || '\t' == c) {
                  curVal += (char) c;
               } else if ('"' == c) {
                  state = STATE_QUOTE;
               } else if (',' == c) {
                  curRecord.add(curVal);
                  curVal = "";
                  state = STATE_LEADING_WHITESPACE;
               } else {
                  curVal += (char) c;
                  state = STATE_NO_QUOTE;
               }
               break;

            case STATE_QUOTE:
               if ('"' == c) {
                  state = STATE_EMBEDDED_QUOTE;
               } else {
                  curVal += (char) c;
               }
               break;

            case STATE_NO_QUOTE:
               if (',' == c) {
                  curRecord.add(curVal);
                  curVal = "";
                  state = STATE_LEADING_WHITESPACE;
               } else if ('\n' == c) {
                  curRecord.add(curVal);
                  curVal = "";
                  records.add(curRecord);
                  curRecord = new ArrayList<>();
                  state = STATE_LEADING_WHITESPACE;
               } else {
                  curVal += (char) c;
               }
               break;

            case STATE_EMBEDDED_QUOTE:
               if ('"' == c) {
                  curVal += (char) c;
                  state = STATE_QUOTE;
               } else {
                  if (',' == c) {
                     curRecord.add(curVal);
                     curVal = "";
                     state = STATE_LEADING_WHITESPACE;
                  } else if ('\n' == c) {
                     curRecord.add(curVal);
                     curVal = "";
                     records.add(curRecord);
                     curRecord = new ArrayList<>();
                     state = STATE_LEADING_WHITESPACE;
                  } else {
                     curVal += (char) c;
                     state = STATE_TRAILING_WHITESPACE;
                  }
               }
               break;

            case STATE_TRAILING_WHITESPACE:
               if (' ' == c || '\t' == c) {
                  curVal += (char) c;
               } else if (',' == c) {
                  curRecord.add(curVal);
                  curVal = "";
                  state = STATE_LEADING_WHITESPACE;
               } else if ('\n' == c) {
                  curRecord.add(curVal);
                  curVal = "";
                  records.add(curRecord);
                  curRecord = new ArrayList<>();
                  state = STATE_LEADING_WHITESPACE;
               }
         }
      }

      if (!curVal.isEmpty()) {
         curRecord.add(curVal);
         records.add(curRecord);
      }

      return records;
   }

   /**
    * Writes the data in the provided record list to a CSV file represented by
    * the buffered writable file out-stream.
    *
    * @param bw      A writable CSV file out-stream.
    * @param records A list of records to be written to the CSV file.
    * @throws IOException An exception will be thrown if there is a problem with the
    *                     file out-stream.
    */
   public static void writeCsv(BufferedWriter bw, List<List<String>> records) throws IOException {
      for (List<String> record : records) {
         for (int i = 0; i < record.size(); i++) {
            String val = record.get(i);
            // Wrap all values in quotes.
            bw.append('"');

            for (int j = 0; j < val.length(); j++) {
               char c = val.charAt(j);
               bw.append(c);

               if ('"' == c) {
                  bw.append('"'); // Embedded quotes are doubled.
               }
            }
            bw.append("\"" + (record.size() - 1 == i ? "\n" : ","));
         }
      }
      bw.flush();
   }
}
