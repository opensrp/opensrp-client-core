/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.TreeNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Maimoona
 *         Class containing some static utility methods.
 */
public class Utils {
    private static final String TAG = "Utils";
    private static final SimpleDateFormat UI_DF = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat UI_DTF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DB_DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Utils() {
    }

    public static String convertDateFormat(String date, boolean suppressException) {
        try {
            return UI_DF.format(DB_DF.parse(date));
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static Date toDate(String date, boolean suppressException) {
        try {
            return DB_DF.parse(date);
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return null;
    }

    public static String convertDateFormat(String date, String defaultV, boolean suppressException) {
        try {
            return UI_DF.format(DB_DF.parse(date));
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return StringUtils.isNotBlank(defaultV) ? defaultV : "";
    }

    public static String convertDateFormat(DateTime date) {
        return UI_DF.format(date.toDate());
    }

    public static String convertDateTimeFormat(String date, boolean suppressException) {
        try {
            return UI_DTF.format(DB_DTF.parse(date.replace("T", " ")));
        } catch (ParseException e) {
            e.printStackTrace();
            if (!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, boolean humanize) {
        v.setText(getValue(cm, field, humanize));
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, String defaultV, boolean humanize) {
        String val = getValue(cm, field, humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        v.setText(val);
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, boolean humanize) {
        v.setText(getValue(pc, field, humanize));
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, String defaultV, boolean humanize) {
        String val = getValue(pc, field, humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        v.setText(val);
    }

    public static void addToList(Map<String, String> locations, Map<String, TreeNode<String, Location>> locationMap, String locationTag) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            boolean tagFound = false;
            if (entry.getValue() != null) {
                Location l = entry.getValue().getNode();

                if (l.getTags() != null) {
                    for (String s : l.getTags()) {
                        if (s.equalsIgnoreCase(locationTag)) {
                            locations.put(locationTag, l.getName());
                            tagFound = true;
                        }
                    }
                }
            }
            if (!tagFound) {
                if (entry.getValue().getChildren() != null) {
                    addToList(locations, entry.getValue().getChildren(), locationTag);
                }
            }
        }
    }

    public static void fillValue(TextView v, String value) {
        v.setText(value);
    }

    public static String formatValue(String value, boolean humanize) {
        if (value == null) {
            value = "";
        }
        return humanize ? WordUtils.capitalize(StringUtil.humanize(value)) : value;
    }

    public static String formatValue(Object value, boolean humanize) {
        if (value == null) {
            value = "";
        }
        return humanize ? WordUtils.capitalize(StringUtil.humanize(value.toString())) : value.toString();
    }

    public static String getValue(CommonPersonObjectClient pc, String field, boolean humanize) {
        return formatValue(pc.getDetails().get(field), humanize);
    }

    public static String getValue(CommonPersonObjectClient pc, String field, String defaultV, boolean humanize) {
        String val = formatValue(pc.getDetails().get(field), humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, String defaultV, boolean humanize) {
        String val = formatValue(cm.get(field), humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, boolean humanize) {
        return formatValue(cm.get(field), humanize);
    }

    public static String nonEmptyValue(Map<String, String> cm, boolean asc, boolean humanize, String... fields) {
        List<String> l = Arrays.asList(fields);
        if (!asc) {
            Collections.reverse(l);
        }
        for (String f : l) {
            String v = getValue(cm, f, humanize);
            if (v != "") {
                return v;
            }
        }
        return "";
    }

    public static boolean hasAnyEmptyValue(Map<String, String> cm, String postFix, String... fields) {
        List<String> l = Arrays.asList(fields);
        for (String f : l) {
            String v = getValue(cm, f, false);
            if (v == "" && (StringUtils.isBlank(postFix) || StringUtils.isBlank(getValue(cm, f + postFix, false)))) {
                return true;
            }
        }
        return false;
    }


    public static int addAsInts(boolean ignoreEmpty, String... vals) {
        int i = 0;
        for (String v : vals) {
            i += ignoreEmpty && StringUtils.isBlank(v) ? 0 : Integer.parseInt(v);
        }
        return i;
    }

    public static TableRow addToRow(Context context, String value, TableRow row) {
        return addToRow(context, value, row, false, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, int weight) {
        return addToRow(context, value, row, false, weight);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact) {
        return addToRow(context, value, row, compact, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact, int weight) {
        return addToRow(context, Html.fromHtml(value), row, compact, weight);
    }

    public static TableRow addToRow(Context context, Spanned value, TableRow row, boolean compact, int weight) {
        TextView v = new TextView(context);
        v.setText(value);
        if (compact) {
            v.setPadding(15, 4, 1, 1);
        } else {
            v.setPadding(2, 15, 2, 15);
        }
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT, weight
        );
        params.setMargins(0, 0, 1, 0);
        v.setLayoutParams(params);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        row.addView(v);

        return row;
    }

    public static String getPreference(Context context, String key, String defaultVal) {
        return context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getString(key, defaultVal);
    }

    public static Gson getLongDateAwareGson() {
        Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement e, Type t, JsonDeserializationContext jd) throws JsonParseException {
                if (e.isJsonNull()) {
                    return null;
                } else if (e.isJsonObject()) {
                    JsonObject je = e.getAsJsonObject();
                    return new DateTime(je.get("iMillis").getAsLong());
                } else if (e.isJsonPrimitive()) {
                    return new DateTime(e.getAsString());
                } else return null;
            }

        }).create();
        return g;
    }

    public static boolean writePreference(Context context, String name, String value) {
        SharedPreferences pref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(name, value);
        return ed.commit();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static String getName(String firstName, String lastName) {
        firstName = firstName.trim();
        lastName = lastName.trim();
        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            return firstName + " " + lastName;
        } else {
            if (StringUtils.isNotBlank(firstName)) {
                return firstName;
            } else if (StringUtils.isNotBlank(lastName)) {
                return lastName;
            }
        }

        return "";
    }

    public static String readAssetContents(Context context, String path) {
        String fileContents = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }

        return fileContents;
    }

    public static InputStream getAssetFileInputStream(Context context, String path) {
        InputStream is = null;
        try {
            is = context.getAssets().open(path);

        } catch (IOException ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }

        return is;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static <T> void startAsyncTask(AsyncTask<T, ?, ?> asyncTask, T[] params) {
        if (params == null) {
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) new Void[0];
            params = arr;
        }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }

    public static DateTime dobToDateTime(CommonPersonObjectClient childDetails) {
        DateTime birthDateTime = null;
        String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
        if (!TextUtils.isEmpty(dobString)) {
            birthDateTime = new DateTime(dobString);
        }
        return birthDateTime;
    }

    /**
     * This method is only intended to be used for processing Zambia-EIR-DataDictionaryReporting-HIA2.csv
     *
     * @param csvFileName
     * @param columns     this map has the db column name as value and the csv column no as the key
     * @return each map is db row with key as the column name and value as the value from the csv file
     */
    public static List<Map<String, String>> populateTableFromCSV(Context context, String csvFileName, Map<Integer, String> columns) {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            InputStream is = getAssetFileInputStream(context, csvFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            try {
                String line;
                String category = "";
                while ((line = reader.readLine()) != null) {
                    Map<String, String> csvValues = new HashMap<>();
                    String[] rowData = line.split(",");
                    if (!TextUtils.isDigitsOnly(rowData[0])) {
                        category = cleanUpHeader(line);
                        continue;
                    }
                    for (Integer key : columns.keySet()) {
                        if (key != 999) {
                            String value = rowData[key];
                            csvValues.put(columns.get(key), value);
                            csvValues.put(columns.get(999), category);
                        }
                    }
                    result.add(csvValues);
                }
            } catch (IOException e) {
                Log.e(TAG, "populateTableFromCSV: error reading csv file " + e.getMessage());

            } finally {
                try {
                    is.close();
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "populateTableFromCSV: unable to close inputstream/bufferedreader " + e.getMessage());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "populateTableFromCSV " + e.getMessage());
        }
        return result;
    }

    private static String cleanUpHeader(String header) {

        try {
            header = header.contains("\"") ? header.replaceAll("\"", "") : header;
            int length = header.length() - 1;
            for (int i = length; i > 0; i--) {
                if (header.charAt(i) != ',') {
                    header = header.substring(0, i + 1);
                    break;
                }
            }
            return header;
        } catch (Exception ex) {
            return header;
        }
    }

    private static String KG_FORMAT = "%s kg";

    public static String kgStringSuffix(Float weight) {
        return String.format(KG_FORMAT, weight);
    }

    public static String kgStringSuffix(String weight) {
        return String.format(KG_FORMAT, weight);
    }

    public static CommonPersonObjectClient convert(CommonPersonObject commonPersonObject) {
        CommonPersonObjectClient pc = new CommonPersonObjectClient(commonPersonObject.getCaseId(),
                commonPersonObject.getDetails(), commonPersonObject.getColumnmaps().get("first_name"));
        pc.setColumnmaps(commonPersonObject.getColumnmaps());
        return pc;
    }
}