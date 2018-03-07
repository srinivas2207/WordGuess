package com.shree.wordguess.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shree.wordguess.WordGuessApplication;
import com.shree.wordguess.util.AppData;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.BroadcastUtil;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.JsonConstants;
import com.shree.wordguess.util.WordData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

/**
 * Util class for handling network operations
 */
public class NetworkOperations {
	private static NetworkOperations networkOperations;

	// Map to store active network calls in running thread
	private Map<String, Object> netWorkCalls = new Hashtable<>();

	public static final String HTTP_GET 	= "GET";
	public static final String HTTP_POST 	= "POST";
	public static final String HTTP_PUT 	= "PUT";
	public static final String HTTP_DELETE 	= "DELETE";

	public static NetworkOperations getInstance() {
		if (networkOperations == null) {
			networkOperations = new NetworkOperations();
		}
		return networkOperations;
	}

	public void clear() {
		netWorkCalls.clear();
	}

	/**
	 * Checking connection status
	 * @return
	 */
	public boolean checkNetworkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) WordGuessApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

//	public boolean translate(List<WordData.Word> wordList, String langCode) {
//		if (!checkNetworkConnection()) {
//			return false;
//		}
//
//		String url = ApplicationConstants.TRANSLATE_URL + ApplicationConstants.API_KEY;
//		JSONObject formObj = getFormBody(wordList, langCode);
//		JSONArray wordReqList = null;
//		try {
//			wordReqList = formObj.getJSONArray(JsonConstants.QUERY_Q);
//		} catch (JSONException jse) {
//			jse.printStackTrace();
//		}
//
//		if (wordReqList == null || wordReqList.length() == 0) {
//			return true;
//		}
//
//		RestCallResponse restCallResponse = doPost(url, formObj.toString() , null);
//		if (restCallResponse != null && restCallResponse.getStatus() == 200) {
//			Map<String, String> resultMap = new HashMap<>();
//			JSONArray transArr = new JSONArray();
//			try {
//				JSONObject translationResp = new JSONObject(restCallResponse.getResponse());
//				transArr = translationResp.getJSONObject(JsonConstants.DATA).getJSONArray(JsonConstants.TRANSLATIONS);
//			} catch (JSONException jse) {
//				jse.printStackTrace();
//			}
//			if (transArr != null && transArr.length() == wordReqList.length()) {
//				List<WordData.Word> noTranslationWords = new ArrayList<>();
//				for(int i =0; i< wordReqList.length(); i++) {
//					String word = null;
//					String translateRes = null;
//					try {
//						word = wordReqList.getString(i);
//						translateRes = transArr.getJSONObject(i).getString(JsonConstants.TRANSLATED_TEXT);
//					} catch (JSONException jse) {
//						jse.printStackTrace();
//					}
//
//					for(WordData.Word wordInfo : wordList) {
//						if (word != null && wordInfo.getName().equalsIgnoreCase(word)){
//							if (word.toLowerCase().equalsIgnoreCase(translateRes.toLowerCase())) {
//								noTranslationWords.add(wordInfo);
//							} else {
//								wordInfo.setSouceLang(langCode);
//								wordInfo.setTranslatedValue(translateRes);
//							}
//						}
//					}
//				}
//
//				for(WordData.Word wordInfo : noTranslationWords) {
//					wordList.remove(wordInfo);
//				}
//				DatabaseUtil.getInstance().updateTranslations(wordList);
//				return true;
//			}
//		}
//		return false;
//	}

//	private static JSONObject getFormBody(List<WordData.Word> wordList, String langCode) {
//		JSONObject formBody = new JSONObject();
//		JSONArray qArray = new JSONArray();
//		for(WordData.Word word : wordList) {
//			if (word.getTranslatedValue() != null
//					&& word.getTranslatedValue().trim().length() > 0
//					&& word.getSouceLang() != null
//					&& word.getSouceLang().equalsIgnoreCase(langCode)) {
//				// skip
//			} else {
//				qArray.put(word.getName().trim());
//			}
//		}
//		try {
//			formBody.put(JsonConstants.SOURCE, JsonConstants.ENGLISH_CODE);
//			formBody.put(JsonConstants.TARGET, langCode);
//			formBody.put(JsonConstants.QUERY_Q, qArray);
//		} catch (JSONException jse) {
//			jse.printStackTrace();
//		}
//		return formBody;
//	}


	/**
	 * Checking available updates from app server
	 * @return
	 */
	public  Runnable checkAppDataUpdate() {
		final String URL = ApplicationConstants.APP_META_DATA_URL;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				RestCallResponse resp = null;
				try {
					resp = sendHttpRequest(HTTP_GET, URL, null, null);
					if (resp != null && resp.getStatus() == 200) {
						String responseHtml = resp.getResponse();
						JSONObject metaData = new JSONObject(responseHtml);
						if (metaData != null ) {
							int availableVersion = metaData.getInt(JsonConstants.APP_VERSION);
							int appDataVersion = metaData.getInt(JsonConstants.APP_DATA_VERSION);

							if (ApplicationConstants.APP_DATA_VERSION < appDataVersion) {
								updateAppData();
							}

							DatabaseUtil.getInstance().setLastUpdatedTime();
							DatabaseUtil.getInstance().setMetaData(appDataVersion, availableVersion);
							DatabaseUtil.getInstance().initAppData();
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				netWorkCalls.remove(URL);
				BroadcastUtil.getInstance().broadcast(ApplicationConstants.APP_DATA_UPDATE_NOTIFICATION , resp != null ? resp.toString() : null );
			}
		};

		if (checkNetworkConnection() && !netWorkCalls.containsKey(URL)) {
			netWorkCalls.put(URL, URL);
			WordGuessApplication.getThreadPoolExecutor().execute(runnable);
		}
		return runnable;
	}

	/**
	 * Updating all the changes
	 * @throws Exception
	 */
	public void updateAppData() throws Exception{
		String URL = ApplicationConstants.APP_UPDATE_DATA_URL;
		RestCallResponse response = sendHttpRequest(HTTP_GET, URL, null, null);
		if (response == null || response.getStatus() != 200) {
			throw new Exception("Unable to update app data");
		}

		String newAppData = response.getResponse();
		JSONObject appDataObj = new JSONObject(newAppData);

		JSONArray newWords = new JSONArray();
		List<String> newWordFileUrlList = _getUrlsToDownload(appDataObj);
		if (newWordFileUrlList != null && newWordFileUrlList.size() > 0) {
			for (String wordFileUrl : newWordFileUrlList) {
				RestCallResponse resp = sendHttpRequest(HTTP_GET, wordFileUrl, null, null);
				if (resp.getStatus() == 200) {
					JSONArray wordArray = new JSONArray(resp.getResponse());
					newWords = concatJsonArrays(newWords, wordArray);
				} else {
					throw new Exception("Error while fetching file data !");
				}
			}
		}


		// Adding new words to the table
		if (newWords != null & newWords.length() >0) {
			List<WordData> wordList = new Gson().fromJson(
					newWords.toString(), new TypeToken<List<WordData>>() {}.getType()
			);

			System.out.println("Inserting new words =======> " + wordList.size());
			DatabaseUtil.getInstance().insertWords(wordList);
		}

		DatabaseUtil.getInstance().updateAppData(appDataObj);
	}

	private List<String> _getUrlsToDownload(JSONObject appDataObj) throws Exception{
		List<String> urlList = new ArrayList<>();
		AppData currentData = DatabaseUtil.getInstance().getAppData();
		List<AppData.WordFile> downloadedFiles = currentData.getFiles();

		if (appDataObj != null && appDataObj.has(JsonConstants.FILES)) {
			JSONArray filesArr = appDataObj.getJSONArray(JsonConstants.FILES);
			if (filesArr != null && filesArr.length() > 0) {
				for (int i=0;i < filesArr.length(); i++) {
					String fileName = filesArr.getJSONObject(i).getString(JsonConstants.NAME);
					String url = filesArr.getJSONObject(i).getString(JsonConstants.URL);
					if (downloadedFiles == null || downloadedFiles.size() == 0) {
						urlList.add(url);
					} else {
						boolean isFileDownloaded = false;
						for (AppData.WordFile wordFile : downloadedFiles) {
							if (wordFile.getName().equalsIgnoreCase(fileName)) {
								isFileDownloaded = true;
								break;
							}
						}
						if (!isFileDownloaded) {
							urlList.add(url);
						}
					}
				}
			}
		}
		return urlList;
	}

	private JSONArray concatJsonArrays(JSONArray parentArr, JSONArray newArr) {
		if(newArr != null && newArr.length() >0) {
			for (int i=0;i<newArr.length();i++) {
				try {
					parentArr.put(newArr.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return parentArr;
	}


	public String encode(String fileName)
	{
		try {
			String encodeName = URLEncoder.encode(fileName, "UTF-8");
			return encodeName;
		}
		catch (UnsupportedEncodingException e) {
			return fileName;
		}
	}

	/**
	 * Sending HTTP POST request
	 * @param reqUrl
	 * @param postBody
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public RestCallResponse doPost(String reqUrl, String postBody, Map<String, String> headers) {
		return sendHttpRequest(HTTP_POST, reqUrl, postBody, headers);
	}

	/**
	 * Sending HTTP GET request
	 * @param reqUrl
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	private RestCallResponse doGet(String reqUrl, Map<String, String> headers) {
		return sendHttpRequest(HTTP_GET, reqUrl, null, headers);
	}

	/**
	 * Sending HTTP PUT request
	 * @param reqUrl
	 * @param postBody
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	private RestCallResponse doPut(String reqUrl, String postBody, Map<String, String> headers) {
		return sendHttpRequest(HTTP_PUT, reqUrl, postBody, headers);
	}

	/**
	 * Sending HTTP DELETE request
	 * @param reqUrl
	 * @param postBody
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	private RestCallResponse doDelete(String reqUrl, String postBody, Map<String, String> headers) {
		return sendHttpRequest(HTTP_DELETE, reqUrl, postBody, headers);
	}

	/**
	 * Sending HTTP request
	 * @param httpMethod
	 * @param reqUrl
	 * @param body
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	private RestCallResponse sendHttpRequest(String httpMethod, String reqUrl, String body, Map<String, String> headers)  {
		reqUrl = reqUrl.replaceAll(" ", "%20");
		RestCallResponse restCallResponse = null;
		InputStream is = null;
		String result = null;
		HttpURLConnection conn = null;
		try {
			URL url;
			try {
				url = new URL(reqUrl);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Invalid url: " + reqUrl);
			}

			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setUseCaches(false);
				conn.setRequestMethod(httpMethod);
				conn.setRequestProperty("Content-Type", "application/json; charset=utf8");

				// adding header params
				if (headers != null) {
					Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, String> header = iterator.next();
						conn.setRequestProperty(header.getKey(), header.getValue());
					}
				}

				byte[] bytes = null;
				if (body != null) {
					bytes = body.getBytes();
					conn.setFixedLengthStreamingMode(bytes.length);
				}

				if (httpMethod.equals(HTTP_POST)) {
					conn.setDoOutput(true);
				}
				else if (httpMethod.equals(HTTP_GET)) {

				}
				else if (httpMethod.equals(HTTP_PUT)) {
					conn.setDoOutput(true);
				}
				else if (httpMethod.equals(HTTP_DELETE)) {

				}

				if (body != null) {
					OutputStream out = conn.getOutputStream();
					out.write(bytes);
					out.close();
				}

				int responseCode = conn.getResponseCode();
				if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
					is = conn.getInputStream();
				} else {
					is = conn.getErrorStream();
				}

				// Convert the InputStream into a string
				if (is != null) {
					result = readStream(is);
				}

				restCallResponse = new RestCallResponse();
				restCallResponse.setUrl(reqUrl);
				restCallResponse.setMethod(httpMethod);
				restCallResponse.setStatus(responseCode);
				restCallResponse.setResponse(result);

			} catch(Exception e) {
				// handle exception
			} finally {
				if (is != null) {
					is.close();
				}
				if (conn != null) {
					conn.disconnect();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
		System.out.println("REST RESPONSE ==== > " + restCallResponse);
		return restCallResponse;
	}


	private void closeConnection(HttpURLConnection httpURLConnection) {
		try {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
				httpURLConnection = null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Reads an InputStream and converts it to a String.
	 */
	private String readStream(InputStream stream) {
		BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
		String line;
		StringBuffer response = new StringBuffer();
		try {
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String result = response.toString();
		result = result.replaceAll("[\\t\\n\\r]"," ");
		return result.trim();
	}


}