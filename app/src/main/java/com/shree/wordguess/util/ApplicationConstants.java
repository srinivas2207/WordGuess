package com.shree.wordguess.util;

public class ApplicationConstants {
	// App's meta data url to check app and data version
	public static final String APP_META_DATA_URL = "https://raw.githubusercontent.com/srinivas2207/WordGuess/master/server/meta_data.json";

	// App's data url
	public static final String APP_UPDATE_DATA_URL = "https://raw.githubusercontent.com/srinivas2207/WordGuess/master/server/app_data.json";

	// App's default settings
	public static int APP_DATA_VERSION = 1;
	public static int AVAILABLE_APP_VERSION = 1;
	public static int AD_INTERVAL = 2;

	public static final int GUESS_CHANCES = 6;
	public static final int MISTAKE_CHARGES = 10;

	// Translation URL template used by background webview to translate words
	public static String TRANSLATE_URL_TEMPLATE = "https://translate.google.com/#en/{code}/{word}";

	// Google search URL
	public static final String GOOGLE_SEARCH_PREFIX = "https://www.google.com/search?q=";

	// TextToSpeech settings
	public static final float PITCH_RATE = 1f;
	public static final float SPEACH_RATE= 0.6f;

	// JS to be loaded into background webview to fetched translation results
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

	//Themes settings
	public static final int RED_THEME = 2;
	public static final int BLUE_THEME = 1;
	public static final int GREEN_THEME = 3;
	public static final int PURPLE_THEME = 4;
	public static final String IS_THEME_CHANGED = "isThemeChanged";


	// Broadcast notifications
	public static final String UI_NOTIFIER = "com.wordguess.uinotifier";
	public static final int APP_DATA_UPDATE_NOTIFICATION = 1;
	public static final int APP_PERMISSION_NOTIFICATION = 4;

	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String NOTIFICATION_DATA = "data";

	// Permission request constants
	public static final int PERMISSIONS_MSG = 1;
	public static final String PERMISSION_CODE = "permCode";
	public static final String REQUESTED_PERMISSIONS = "reqPerms";
	public static final String GRANTED_PERMISSIONS = "grantedPerms";

	// Keyboard disabled alpha
	public static final float KEYBOARD_ALPHA = (float) 0.3;

	// ANALYTIC USER PROPERTIES
	public static final String LANGUAGE_UP = "LANGUAGE";
	public static final String SPELL_BEE_UP = "SPELL_BEE";
	public static final String VOCAB_BEE_UP = "VOCAB_BEE";


}