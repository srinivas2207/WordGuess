package com.shree.wordguess.util;

public class ApplicationConstants {
	public static final String APP_META_DATA_URL = "https://raw.githubusercontent.com/srinivas2207/WordGuess/master/meta_data.json";
	public static final String APP_UPDATE_DATA_URL = "https://raw.githubusercontent.com/srinivas2207/WordGuess/master/app_data.json";
	public static int APP_DATA_VERSION = 1;
	public static int AVAILABLE_APP_VERSION = 1;
	public static int AD_INTERVAL = 2;

	public static String TRANSLATE_URL_TEMPLATE = "https://translate.google.com/#en/{code}/{word}";
	public static String TRANSLATE_RESULT_FETCHER_JS = "javascript: " +
			"var translateLoader = window.TRANSLATE_LOADER; " +
			"var source = null; " +
			"var result = null; " +
			"try { " +
			"	source = document.getElementById(\"source\").value; " +
			"} catch(e) " +
			"{} ; " +
			"try { " +
			"	result = document.querySelector(\"span.translation\").innerText; " +
			"} catch(e) " +
			"{} ; " +
			"translateLoader.setResult(source,result);";

	public static final String TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2?key=";
	public static final String API_KEY = "";

	public static final int GUESS_CHANCES = 6;
	public static final int MISTAKE_CHARGES = 10;
	public static final int REVEAL_CHARGES = 30;

	//Themes settings
	public static final int RED_THEME = 2;
	public static final int BLUE_THEME = 1;
	public static final int GREEN_THEME = 3;
	public static final int PURPLE_THEME = 4;
	public static final String IS_THEME_CHANGED = "isThemeChanged";


	public static final String IS_LOCALE_CHANGED = "IS_LOCALE_CHANGED";

	public static final long DATA_UPDATE_INTERVAL = 60 * 60 * 1000;


	public static final int PERMISSIONS_MSG = 1;

	public static final String LANG_CODE_ENG = "en";
	public static final String LANG_CODE_TEL = "te";

	public static final int APP_DATA_UPDATE_NOTIFICATION = 1;
	public static final int REST_NOTIFICATION = 2;
	public static final int FILE_OPERATION_NOTIFICATION = 3;
	public static final int APP_PERMISSION_NOTIFICATION = 4;

	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String NOTIFICATION_DATA = "data";

	public static final String PERMISSION_CODE = "permCode";
	public static final String REQUESTED_PERMISSIONS = "reqPerms";
	public static final String GRANTED_PERMISSIONS = "grantedPerms";

	public static final float KEYBOARD_ALPHA = (float) 0.3;


	public static final float PITCH_RATE = 1f;
	public static final float SPEACH_RATE= 0.6f;
	public static final String GOOLE_SEARCH_PREFIX = "https://www.google.com/search?q=";

	public static final String UI_NOTIFIER = "com.wordguess.uinotifier";


	// ANALYTIC USER PROPERTIES
	public static final String LANGUAGE_UP = "LANGUAGE";
	public static final String SPELL_BEE_UP = "SPELL_BEE";
	public static final String VOCAB_BEE_UP = "VOCAB_BEE";


}