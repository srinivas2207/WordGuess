package com.shree.wordguess.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.TypedValue;

import com.shree.wordguess.R;
import com.shree.wordguess.WordGuessApplication;
import com.shree.wordguess.activity.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Utility class to handle app level common operations
 */
public class Utils {

	public static int currentTheme = ApplicationConstants.GREEN_THEME;
	public static int primaryColorDark = 0;

	/**
	 * Changin app's theme
	 * @param activity
	 * @param theme
	 */
	public static void changeToTheme(Activity activity, int theme) {
		currentTheme = theme;
		DatabaseUtil.getInstance().setAppTheme(theme);
		activity.finish();

		Intent intent = new Intent(activity, activity.getClass());
		intent.putExtra(ApplicationConstants.IS_THEME_CHANGED, true);
		activity.startActivity(intent);

		//activity.startActivity(intent);
//		activity.overridePendingTransition(android.R.anim.fade_in,
//				android.R.anim.fade_out);

//		TaskStackBuilder.create(activity)
//				.addNextIntent(new Intent(activity, HomeActivity.class))
//				.addNextIntent(intent)
//				.startActivities();
	}

	@SuppressLint("NewApi")
	public static void onActivityCreateSetTheme(Activity activity) {
		int statusBarColor = 0;
		switch (currentTheme) {
			case ApplicationConstants.BLUE_THEME:
				activity.setTheme(R.style.BlueTheme);
				statusBarColor = R.color.ColorPrimaryDark_blue;
				break;
			case ApplicationConstants.PURPLE_THEME:
				activity.setTheme(R.style.purpleTheme);
				statusBarColor = R.color.ColorPrimaryDark_purple;
				break;
			case ApplicationConstants.RED_THEME:
				activity.setTheme(R.style.RedTheme);
				statusBarColor = R.color.ColorPrimaryDark_red;
				break;
			case ApplicationConstants.GREEN_THEME:
				activity.setTheme(R.style.GreenTheme);
				statusBarColor = R.color.ColorPrimaryDark_green;
				break;
		}

		TypedValue typedValue = new TypedValue();
		if (activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)) {
			primaryColorDark = typedValue.data;
		}
		if (Build.VERSION.SDK_INT >= 21) {
			activity.getWindow().setStatusBarColor(activity.getResources().getColor(statusBarColor));
		}
	}

	public static int getAppVersion() {
		PackageManager manager = WordGuessApplication.getInstance().getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(
                    WordGuessApplication.getInstance().getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Sorting JSON Array of score details
	 * @param scoreList
	 * @return
	 */
	public static JSONArray sortScores(JSONArray scoreList) {
		if (scoreList == null || scoreList.length() == 0) {
			return scoreList;
		}

		HashMap<Float, Integer> indexMap = new HashMap<>();
		Float[] arr = new Float[scoreList.length()];

		for(int i=0;i<scoreList.length();i++) {
			try {
				JSONObject obj = scoreList.getJSONObject(i);
				int score = obj.getInt(JsonConstants.SCORE);
				int games = obj.getInt(JsonConstants.GAMES);
				arr[i] = Float.valueOf(score + 1/games);
				indexMap.put(arr[i], i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Arrays.sort(arr, Collections.reverseOrder());


		JSONArray sortedList = new JSONArray();
		for(int i=0;i<arr.length;i++) {
			try {
				int index = indexMap.get(arr[i]);
				JSONObject obj = scoreList.getJSONObject(index);
				sortedList.put(i, obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return sortedList;
	}

	private static final boolean showLogs = true;
	private static String TAG = "WordGuessLogs";
	public static void log(String message) {
		if (showLogs) {
			Log.i(TAG, message);
		}
	}
}
