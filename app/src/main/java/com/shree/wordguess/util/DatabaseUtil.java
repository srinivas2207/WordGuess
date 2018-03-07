package com.shree.wordguess.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shree.wordguess.WordGuessApplication;
import com.shree.wordguess.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Util class for database operations
 */
public class DatabaseUtil {

	private static DatabaseUtil _dbUtil = null;
	private DatabaseHelper _dataDatabaseHelper = null;
	
	private DatabaseUtil() {
		_dataDatabaseHelper = new DatabaseHelper(WordGuessApplication.getInstance());
	}
	
	public static DatabaseUtil getInstance() {
		if(_dbUtil == null ) {
			_dbUtil = new DatabaseUtil();
		}
		return _dbUtil;
	}

	/**
	 * Getting demo viewed status
	 * @return
	 */
	public boolean getDemoStatus() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		return prefs.getBoolean(DBConstants.DEMO_STATUS, false);
	}

	/**
	 * Setting demo viewed status
	 * @param status
	 */
	public void setDemoStatus(boolean status) {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(DBConstants.DEMO_STATUS, status);
		editor.commit();
	}

	/**
	 * Getting app's last update check time
	 * @return
	 */
	public long getLastUpdatedTime(){
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		return prefs.getLong(DBConstants.LAST_UPDATED_TIME, 0);
	}

	/**
	 * Setting app's last update check time
	 */
	public void setLastUpdatedTime() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(DBConstants.LAST_UPDATED_TIME, new Date().getTime());
		editor.commit();
	}

	/**
	 * Status of app data parsing
	 * @return
	 */
	public boolean isAppDataParsed() {
		int appVersion = Utils.getAppVersion();
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		return prefs.getBoolean(DBConstants.APP_DATA_PARSE_STATE + appVersion, false);
	}

	/**
	 * Updated app's data parsing status
	 * @param isParsed
	 */
	public void setAppDataParsed(boolean isParsed) {
		int appVersion = Utils.getAppVersion();
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(DBConstants.APP_DATA_PARSE_STATE + appVersion, isParsed);
		editor.commit();
	}

	/**
	 * Updating app data
	 * @param appData
	 */
	public void updateAppData(JSONObject appData) {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(DBConstants.APP_DATA, appData.toString());
		editor.commit();
	}

	/**
	 * Getting app data
	 * @return
	 */
	public AppData getAppData() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		String data =  prefs.getString(DBConstants.APP_DATA, null);

		AppData appData = new Gson().fromJson(
				data, new TypeToken<AppData>() {}.getType()
		);
		return  appData;
	}

	/**
	 * Getting app's user chosen theme
	 * @return
	 */
	public int getAppTheme() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		return  prefs.getInt(DBConstants.APP_THEME, ApplicationConstants.GREEN_THEME);
	}

	/**
	 * Updating user selected theme
	 * @param theme
	 */
	public void setAppTheme(int theme) {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(DBConstants.APP_THEME, theme);
		editor.commit();
	}

	/**
	 * Getting home page info, which is updated by user
	 * @return
	 */
	public JSONObject getHomePageInfo() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		String homeData =  prefs.getString(DBConstants.HOME_PAGE_DATA, null);
		if (homeData != null) {
			try {
				return  new JSONObject(homeData);
			} catch (JSONException jse) {
				jse.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Updating home page info, chosen by user
	 * @param gameType
	 * @param langCode
	 * @param category
	 */
	public void updateHomePageInfo(int gameType, String langCode, int category) {
		JSONObject homeInfo = getHomePageInfo();
		if (homeInfo == null) {
			homeInfo = new JSONObject();
		}

		String homePageData = null;
		try {
			homeInfo.put(JsonConstants.TYPE, gameType);
			homeInfo.put(JsonConstants.CODE, langCode);
			homeInfo.put(JsonConstants.ID, category);
			homePageData = homeInfo.toString();
		} catch (JSONException jse) {
			jse.printStackTrace();
		}

		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(DBConstants.HOME_PAGE_DATA, homePageData);
		editor.commit();
	}

	/**
	 * Getting user secured scores
	 * @return
	 */
	public JSONObject getScores() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		String scoresDataStr =  prefs.getString(DBConstants.SCORES_DATA, null);
		if (scoresDataStr != null) {
			try {
				return  new JSONObject(scoresDataStr);
			} catch (JSONException jse) {
				jse.printStackTrace();
			}
		}
		JSONObject scoreData = new JSONObject();
		try {
			JSONArray vList = new JSONArray();
			JSONArray sList = new JSONArray();
			scoreData.put(JsonConstants.VOCAB_SCORES, vList);
			scoreData.put(JsonConstants.SPELL_SCORES, sList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return scoreData;
	}

	/**
	 * Updating current game's scores
	 * @param gameId
	 * @param isVocabBee
	 * @param games
	 * @param score
	 * @param category
	 */
	public void updateScores(long gameId, boolean isVocabBee, int games, int score, int category) {
		JSONObject scoresData = getScores();
		JSONArray list = null;

		try {
			if (isVocabBee) {
				list = scoresData.getJSONArray(JsonConstants.VOCAB_SCORES);
			} else {
				list = scoresData.getJSONArray(JsonConstants.SPELL_SCORES);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (list == null) {
			list = new JSONArray();
		}

		JSONObject scoreObj = null;
		for(int i=0; i< list.length(); i++) {
			try {
				JSONObject obj = list.getJSONObject(i);
				if (obj.getLong(JsonConstants.ID) == gameId) {
					scoreObj = obj;
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (scoreObj == null) {
			scoreObj = new JSONObject();
			list.put(scoreObj);
		}

		try {
			scoreObj.put(JsonConstants.ID, gameId);
			scoreObj.put(JsonConstants.GAMES, games);
			scoreObj.put(JsonConstants.SCORE, score);
			scoreObj.put(JsonConstants.CATEGORY, category);
		} catch (JSONException jse) {
			jse.printStackTrace();
		}

		if (list.length() >= 15) {
			try {
				list = Utils.sortScores(list);
				for(int i=0; i< 5; i++) {
					list.remove(list.length()-1);
				}

				if (isVocabBee) {
					scoresData.put(JsonConstants.VOCAB_SCORES, list);
				} else {
					scoresData.put(JsonConstants.SPELL_SCORES, list);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(DBConstants.SCORES_DATA, scoresData.toString());
		editor.commit();
	}

	/**
	 * Getting app's meta data
	 * @return
	 */
	public JSONObject getMetaData() {
		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE, Context.MODE_PRIVATE);
		String metaData =  prefs.getString(DBConstants.APP_META_DATA, null);
		if (metaData != null) {
			try {
				return  new JSONObject(metaData);
			} catch (JSONException jse) {
				jse.printStackTrace();
			}
		}
		return new JSONObject();
	}

	/**
	 * Updating app's meta data
	 * @param appDataVersion
	 * @param appVersion
	 */
	public void setMetaData(int appDataVersion, int appVersion) {
		JSONObject metaData = getMetaData();
		try {
			metaData.put(JsonConstants.APP_DATA_VERSION, appDataVersion);
			metaData.put(JsonConstants.APP_VERSION, appVersion);
		} catch (JSONException jse) {
			jse.printStackTrace();
		}

		SharedPreferences prefs = WordGuessApplication.getInstance().getSharedPreferences(DBConstants.WORD_GUESS_APP_PREFERENCE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(DBConstants.APP_META_DATA, metaData.toString());
		editor.commit();
	}

	/**
	 * Initializing static variables which uses app's data
	 */
	public void initAppData() {
		JSONObject metaData = getMetaData();

		try {
			if (metaData.has(JsonConstants.APP_DATA_VERSION)) {
				ApplicationConstants.APP_DATA_VERSION = metaData.getInt(JsonConstants.APP_DATA_VERSION);
			}

			if (metaData.has(JsonConstants.APP_VERSION)) {
				ApplicationConstants.AVAILABLE_APP_VERSION = metaData.getInt(JsonConstants.APP_VERSION);
			}

		} catch (JSONException jse) {
			jse.printStackTrace();
		}

		AppData appData = getAppData();
		if (appData != null && appData.getTranslateUrl() != null && appData.getTranslateUrl().trim().length() > 0) {
			ApplicationConstants.TRANSLATE_URL_TEMPLATE = appData.getTranslateUrl();
		}

		if (appData != null && appData.getTranslateJs() != null && appData.getTranslateJs().trim().length() > 0) {
			ApplicationConstants.TRANSLATE_RESULT_FETCHER_JS = appData.getTranslateJs();
		}

		if (appData != null && appData.getAdInterval() > 0) {
			ApplicationConstants.AD_INTERVAL = appData.getAdInterval();
		}

	}





	///////////////////////////////////SQLITE QUERIES AND TRANSACTIONS ////////////////////////////////

	/**
	 * Updating/Inserting words
	 * @param wordsList
	 */
	public void insertWordsFromAssets(List<WordData> wordsList) {
		// FETCHING ALL THE DATA WHICH IS FAVOURITE, TO BE UPDATED IN NEWLY ADDED ROWS
		List<WordData.Word> favList = getFavouriteList();

		// DELETING ALL ROWS
		_clearTable();

		insertWords(wordsList);

		// UPDATING DATA
		if (favList != null && favList.size() > 0) {
			for(WordData.Word word : favList) {
				String word_id_query = "Select " +
						DBConstants._ID +
						" from "+ DBConstants.WORD_TABLE+
						" where " + DBConstants.NAME +" = \"" + word.getName() + "\" LIMIT 1" ;
				Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery(word_id_query, null);
				try {
					long wordId = -1;
					if (c != null && c.getCount() > 0) {
						c.moveToFirst();
						wordId = c.getLong(c.getColumnIndex(DBConstants._ID));
						c.close();
					}
					if (wordId != -1) {
						ContentValues values = new ContentValues();
						values.put(DBConstants.FAVOURITE, true);
						values.put(DBConstants.TRANS_VALUE,word.getTranslatedValue());
						values.put(DBConstants.SOURCE_LANG,word.getSouceLang());
						_updateRow(DBConstants.WORD_TABLE, values, DBConstants._ID +" = \"" + wordId + "\" ");
					}
				} catch (Exception e){
					e.printStackTrace();
				}

			}
		}

	}

	public void insertWords(List<WordData> wordsList) {
		StringBuilder sb = new StringBuilder();
		boolean firstRow = true;
		for (WordData wordData : wordsList) {
			int category = wordData.getCategory();
			List<WordData.Word> words = wordData.getWords();
			for(WordData.Word word : words) {
				String name = null;
				String type = null;
				String desc = null;

				if (word.getName() != null) {
					name = "\"" + word.getName() + "\"";
				}

				if (word.getType() != null) {
					type ="\"" + word.getType() + "\"";
				}

				if (word.getDesc() != null) {
					desc = "\"" + word.getDesc() + "\"";
				}

				String rowBlock = "(" + name + "," + category + "," + type + ","  + desc +  ")";
				if (!firstRow) {
					rowBlock = "," + rowBlock;
				} else {
					firstRow =false;
				}
				sb.append(rowBlock);
			}
		}

		// INSERTING DATA
		String insertTemplate = "INSERT INTO " + DBConstants.WORD_TABLE + " (" + DBConstants.NAME + "," + DBConstants.CATEGORY + "," + DBConstants.TYPE + "," + DBConstants.DESC + ") VALUES ";

		String bukInsertQuery = insertTemplate + sb.toString();
		_dataDatabaseHelper.getWritableDatabase().beginTransaction();
		_dataDatabaseHelper.getWritableDatabase().execSQL(bukInsertQuery);
		_dataDatabaseHelper.getWritableDatabase().setTransactionSuccessful();
		_dataDatabaseHelper.getWritableDatabase().endTransaction();

		long rows =  DatabaseUtils.queryNumEntries(_dataDatabaseHelper.getReadableDatabase(), DBConstants.WORD_TABLE);
		Utils.log("Words ===== " + rows);
	}

	/**
	 * Updating all the translated words, with translation value
	 * @param wordList
	 */
	public void updateTranslations(List<WordData.Word> wordList) {
		for (WordData.Word wordInfo : wordList) {
			ContentValues values = new ContentValues();
			values.put(DBConstants.SOURCE_LANG,wordInfo.getTranslatedValue());
			values.put(DBConstants.TRANS_VALUE,wordInfo.getSouceLang());
			_updateRow(DBConstants.WORD_TABLE, values, DBConstants.NAME +" = \"" + wordInfo.getName() + "\" ");
		}
	}

	/**
	 * Words not in session words query
	 * @param wordsInSession
	 * @return
	 */
	private String _convertWordsCheckQuery(List<String> wordsInSession) {
		if (wordsInSession == null || wordsInSession.size() == 0) {
			return  "";
		}

		String query = "";
		boolean isFirst = true;
		for (String word : wordsInSession) {
			String queryStr = "\"" + word + "\"";
			if (isFirst) {
				isFirst = false;
			} else {
				queryStr = "," + queryStr;
			}
			query += queryStr;
		}

		if (query.trim().length() > 0) {
			query = DBConstants.NAME + " NOT IN ("+ query + ")";
		}
		return query;
	}

	/**
	 * Getting random words, which is not in current game session
	 * @param category
	 * @param wordsInSession
	 * @return
	 */
	public List<WordData.Word> getRandomWords(int category, List<String> wordsInSession) {
		Utils.log("WORDGUESS_LOG :Before randome select  ===== " + new Date().getTime());
		List<WordData.Word> wordList = new ArrayList<>();

		String wordsCheckQuery = _convertWordsCheckQuery(wordsInSession);
		String sql_query;
		sql_query = "SELECT "
				+ DBConstants._ID + ","
				+ DBConstants.NAME + ","
				+ DBConstants.CATEGORY + ","
				+ DBConstants.TYPE + ","
				+ DBConstants.DESC + ","
				+ DBConstants.TRANS_VALUE + ","
				+ DBConstants.SOURCE_LANG + ","
				+ DBConstants.FAVOURITE
				+ " FROM "
				+ DBConstants.WORD_TABLE ;

		if (category != 0 || wordsCheckQuery.trim().length() > 0) {
			sql_query += " WHERE ";
		}

		if (category != 0) {
			sql_query += DBConstants.CATEGORY + " = " + category;
			if (wordsCheckQuery.trim().length() > 0) {
				sql_query += " AND ";
			}
		}

		sql_query += wordsCheckQuery;

		sql_query +=  " ORDER BY RANDOM() LIMIT 5";

		Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery(sql_query, null);
		try{
			if ( c != null && c.getCount() > 0 ) {
				int rowsCount = c.getCount();
				c.moveToFirst();
				for(int i=0;i<rowsCount;i++) {
					long id = c.getLong(c.getColumnIndex(DBConstants._ID));
					String name = c.getString(c.getColumnIndex(DBConstants.NAME));
					int cat = c.getInt(c.getColumnIndex(DBConstants.CATEGORY));
					String type = c.getString(c.getColumnIndex(DBConstants.TYPE));
					String desc = c.getString(c.getColumnIndex(DBConstants.DESC));
					String translatedValue = c.getString(c.getColumnIndex(DBConstants.TRANS_VALUE));
					String sourceLang = c.getString(c.getColumnIndex(DBConstants.SOURCE_LANG));
					boolean isFavourite = c.getInt(c.getColumnIndex(DBConstants.FAVOURITE)) == 1;

					WordData.Word wordObj = new WordData.Word();
					wordObj.setId(id);
					wordObj.setName(name);
					wordObj.setType(type);
					wordObj.setCategory(cat);
					wordObj.setDesc(desc);
					wordObj.setTranslatedValue(translatedValue);
					wordObj.setSouceLang(sourceLang);
					wordObj.setFavourite(isFavourite);

					wordList.add(wordObj);

					c.moveToNext();
				}

				c.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}


		Utils.log("after randome select  ===== " + new Date().getTime());
		return wordList;
	}

	/**
	 * Updating word
	 * @param word
	 */
	public void updateWord(WordData.Word word) {
		if (word == null) {
			return;
		}

		Utils.log("Before word update  ===== " + new Date().getTime());
		ContentValues values = new ContentValues();

		values.put(DBConstants.DESC,word.getDesc());
		values.put(DBConstants.FAVOURITE,word.isFavourite());
		values.put(DBConstants.TRANS_VALUE,word.getTranslatedValue());
		values.put(DBConstants.SOURCE_LANG,word.getSouceLang());

		_updateRow(DBConstants.WORD_TABLE, values, DBConstants._ID +"=" + word.getId());
		Utils.log("After word update  ===== " + new Date().getTime());
	}

	public List<WordData.Word> getFavouriteList() {
		List<WordData.Word> wordList = new ArrayList<>();

		String sql_query
				= "SELECT "
				+ DBConstants._ID + ","
				+ DBConstants.NAME + ","
				+ DBConstants.CATEGORY + ","
				+ DBConstants.TYPE + ","
				+ DBConstants.DESC + ","
				+ DBConstants.TRANS_VALUE + ","
				+ DBConstants.SOURCE_LANG + ","
				+ DBConstants.FAVOURITE
				+ " FROM "
				+ DBConstants.WORD_TABLE
				+ " WHERE " + DBConstants.FAVOURITE + "=1";

		Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery(sql_query, null);
		try{
			if ( c != null && c.getCount() > 0 ) {
				int rowsCount = c.getCount();
				c.moveToFirst();
				for(int i=0;i<rowsCount;i++) {
					long id = c.getLong(c.getColumnIndex(DBConstants._ID));
					String name = c.getString(c.getColumnIndex(DBConstants.NAME));
					int cat = c.getInt(c.getColumnIndex(DBConstants.CATEGORY));
					String type = c.getString(c.getColumnIndex(DBConstants.TYPE));
					String desc = c.getString(c.getColumnIndex(DBConstants.DESC));
					String translatedValue = c.getString(c.getColumnIndex(DBConstants.TRANS_VALUE));
					String sourceLang = c.getString(c.getColumnIndex(DBConstants.SOURCE_LANG));
					boolean isFavourite = c.getInt(c.getColumnIndex(DBConstants.FAVOURITE)) == 1;

					WordData.Word wordObj = new WordData.Word();
					wordObj.setId(id);
					wordObj.setName(name);
					wordObj.setType(type);
					wordObj.setCategory(cat);
					wordObj.setDesc(desc);
					wordObj.setTranslatedValue(translatedValue);
					wordObj.setSouceLang(sourceLang);
					wordObj.setFavourite(isFavourite);

					wordList.add(wordObj);

					c.moveToNext();
				}

				c.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return wordList;
	}

	private void _clearTable() {
		_dataDatabaseHelper.getWritableDatabase().execSQL("delete from "+ DBConstants.WORD_TABLE);
	}

	private int _getCount(String querry) {
		try {
			Cursor c = _dataDatabaseHelper.getReadableDatabase().rawQuery( querry, null);
			return c.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void _updateRow(String tableName, ContentValues values, String whereClause) {
		try {
			_dataDatabaseHelper.getWritableDatabase().beginTransaction();
			_dataDatabaseHelper.getWritableDatabase().update( tableName, values, whereClause, null );
			_dataDatabaseHelper.getWritableDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_dataDatabaseHelper.getWritableDatabase().endTransaction();
		}
	}

	private void _insertRow(String tableName, ContentValues values) {
		try {
			_dataDatabaseHelper.getWritableDatabase().beginTransaction();
			_dataDatabaseHelper.getWritableDatabase().insertOrThrow( tableName, null, values);
			_dataDatabaseHelper.getWritableDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_dataDatabaseHelper.getWritableDatabase().endTransaction();
		}
	}

	private void _deleteRow(String tableName, String whereClause)  {
		try {
			_dataDatabaseHelper.getWritableDatabase().beginTransaction();
			_dataDatabaseHelper.getWritableDatabase().delete(tableName, whereClause, null);
			_dataDatabaseHelper.getWritableDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			_dataDatabaseHelper.getWritableDatabase().endTransaction();
		}
	}


}
