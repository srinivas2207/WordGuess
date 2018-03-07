package com.shree.wordguess;


import android.app.Application;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.shree.wordguess.network.NetworkOperations;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.FileOperations;
import com.shree.wordguess.util.Utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Application class to initialize app's initialization processes
 */
public class WordGuessApplication extends Application {
	private static WordGuessApplication applicationContext = null;
	private static ThreadPoolExecutor threadPoolExecutor;

	public WordGuessApplication() {
		applicationContext = this;
	}
	
	public static ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}
	
	public static WordGuessApplication getInstance() {
		if(applicationContext == null) {
			new WordGuessApplication();
		}			
		return applicationContext;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_initializeTheme();
		_initializeTP();
		_initializeDB();
	}

	/**
	 * Creating a threadpool to be used for network operations
	 */
	private void _initializeTP() {
		int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
		threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(100),
				new WGRejectExecutionHandler());
	}

	class WGRejectExecutionHandler implements RejectedExecutionHandler {

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		}
	}

	/**
	 * Initializing database
	 */
	public void _initializeDB() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				DatabaseUtil.getInstance().initAppData();
				// If the app data from assets is not parsed yet, initializing the parsing
				if (!DatabaseUtil.getInstance().isAppDataParsed()) {
					FileOperations.getInstance().parseResourceFiles();
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				// Checking if there's any update on app's data
				if (NetworkOperations.getInstance().checkNetworkConnection()) {
					NetworkOperations.getInstance().checkAppDataUpdate();
				}
			}

		}.execute(null, null, null);

	}

	/**
	 * Initializing app's theme
	 */
	private void _initializeTheme() {
		Utils.currentTheme = (Integer) DatabaseUtil.getInstance().getAppTheme();
	}


}
