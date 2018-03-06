package com.shree.varikolepahani.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shree.varikolepahani.PahaniApplication;
import com.shree.varikolepahani.util.ApplicationConstants;
import com.shree.varikolepahani.util.BroadcastUtil;
import com.shree.varikolepahani.util.DatabaseUtil;
import com.shree.varikolepahani.util.FileOperations;
import com.shree.varikolepahani.util.JsonConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NetworkOperations {
	private static com.shree.varikolepahani.network.NetworkOperations networkOperations;
	private Map<String, Object> netWorkCalls = new Hashtable<>();
	public static final String HTTP_GET 	= "GET";
	public static final String HTTP_POST 	= "POST";
	public static final String HTTP_PUT 	= "PUT";
	public static final String HTTP_DELETE 	= "DELETE";

	public static final String NO_DATA_HTML_ALERT = "alert('Data Not Available')";

	public static com.shree.varikolepahani.network.NetworkOperations getInstance() {
		if (networkOperations == null) {
			networkOperations = new com.shree.varikolepahani.network.NetworkOperations();
		}
		return networkOperations;
	}

	public void clear() {
		netWorkCalls.clear();
	}

	public boolean checkNetworkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) PahaniApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public String getPahaniDetailsURL(final boolean isOneB, final ApplicationConstants.TYPE_MENU type, final String id) {
		String URL = null;
		switch (type) {
			case SURVEY:
				URL = ApplicationConstants.PAHANI_DETAILS_SURVEY_URL;
				break;
			case PATTADAR:
				URL = ApplicationConstants.PAHANI_DETAILS_KHATHA_URL;
				break;
			case KHATA:
				URL = ApplicationConstants.PAHANI_DETAILS_KHATHA_URL;
				break;
			default:
				break;
		}

		if (isOneB) {
			URL = ApplicationConstants.ONE_B_DETAILS_KHATHA_URL;
		}

		URL = URL.replace("{id}", id);
		URL = URL.replaceAll(" ", "%20");
		return URL;
	}

	public String getPahaniDetailsFileName(final boolean isOneB, final ApplicationConstants.TYPE_MENU type, final String id) {
		String fileName = null;
		switch (type) {
			case SURVEY:
				fileName = ApplicationConstants.SURVEY_FILE_NAME_PREFIX + id;
				break;
			case PATTADAR:
				fileName = ApplicationConstants.PATTA_FILE_NAME_PREFIX + id;
				break;
			case KHATA:
				fileName = ApplicationConstants.KHATHA_FILE_NAME_PREFIX + id;
				break;

			default:
				break;
		}

		if (isOneB) {
			fileName = ApplicationConstants.ONE_B_FILE_NAME_PREFIX + fileName;
		}
		fileName = encode(fileName);
		fileName += ".txt";
		return fileName;
	}

	public  Runnable getPahaniDetails(final boolean isOneB, final ApplicationConstants.TYPE_MENU type, final String id) {
		final String URL = getPahaniDetailsURL(isOneB, type, id);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String fileName = getPahaniDetailsFileName(isOneB, type, id);
				com.shree.varikolepahani.network.RestCallResponse resp = null;
				try {
					resp = sendHttpRequest(HTTP_GET, URL, null, null);
					if (resp != null && resp.getStatus() == 200) {
						if (resp.getResponse().contains(NO_DATA_HTML_ALERT)) {
							// delete file
							FileOperations.getInstance().deleteFile(fileName);
						} else {
							String responseHtml = resp.getResponse();
							if (responseHtml != null && responseHtml.trim().length() >0) {
								FileOperations.getInstance().updateFile(fileName, responseHtml);
								Map<String, Long> fileData = DatabaseUtil.getInstance().getFileData();
								if (fileData == null) {
									fileData = new HashMap<>();
								}
								fileData.put(fileName, new Date().getTime());
								DatabaseUtil.getInstance().updateFileData(fileData);
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				netWorkCalls.remove(HTTP_GET + URL);
				BroadcastUtil.getInstance().broadcast(ApplicationConstants.REST_NOTIFICATION , resp != null ? resp.toString() : null );
			}
		};

		if (checkNetworkConnection() && !netWorkCalls.containsKey(HTTP_GET + URL)) {
			netWorkCalls.put(HTTP_GET + URL, URL);
			PahaniApplication.getThreadPoolExecutor().execute(runnable);
		}
		return runnable;
	}

	public Runnable syncData() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				long appDataUpdatedTime = DatabaseUtil.getInstance().getAppDataUpdateTime();
				long currentTime = new Date().getTime();
				if ( (currentTime - appDataUpdatedTime) > ApplicationConstants.DATA_UPDATE_INTERVAL ) {
					updateAppData();
				}

				long fileDataUpdatedTime = DatabaseUtil.getInstance().getFileDataUpdateTime();
				if ( (currentTime - fileDataUpdatedTime) > ApplicationConstants.DATA_UPDATE_INTERVAL ) {
					updateFileData();
				}
			}
		};
		PahaniApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	public void updateAppData() {
		if (checkNetworkConnection()) {
			JSONArray surveyNoList = getDataList(ApplicationConstants.LIST_TYPE.SURVEY);
			JSONArray pattadarList = getDataList(ApplicationConstants.LIST_TYPE.PATTADAR);
			JSONArray khathaList = getDataList(ApplicationConstants.LIST_TYPE.KHATA);

			if(surveyNoList != null && pattadarList!=null && khathaList != null) {
				DatabaseUtil.getInstance().updateAppData(surveyNoList, pattadarList, khathaList);
			}
			DatabaseUtil.getInstance().updateAppDataTime();
			BroadcastUtil.getInstance().broadcast( ApplicationConstants.APP_DATA_UPDATE_NOTIFICATION, null);
		}
	}

	public void updateFileData() {
		Map<String , Long> fileData = DatabaseUtil.getInstance().getFileData();
		if (fileData == null ) {
			fileData = new HashMap<>();
		}

		JSONArray surveyNoList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.SURVEY);
		JSONArray pattadarList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.PATTADAR);
		JSONArray khathaList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.KHATA);

		DatabaseUtil.getInstance().updateFileData(fileData);
	}

	private String getFileName(ApplicationConstants.LIST_TYPE type, String id) {
		String fileName = null;
		switch (type) {
			case SURVEY:
				fileName = ApplicationConstants.SURVEY_FILE_NAME_PREFIX + id;
				break;
			case PATTADAR:
				fileName = ApplicationConstants.PATTA_FILE_NAME_PREFIX + id;
				break;
			case KHATA:
				fileName = ApplicationConstants.KHATHA_FILE_NAME_PREFIX + id;
				break;
			default:
				break;
		}
		return fileName;
	}

//	private boolean isFileUptoDate(Map<String , Long> fileData, ) {
//		// to-do
//		return false;
//	}

	private Runnable updatePahaniFiles() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (checkNetworkConnection()) {
					JSONArray surveyNoList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.SURVEY);
					JSONArray khathaList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.KHATA);
					JSONArray pattadarList = DatabaseUtil.getInstance().getAppData(ApplicationConstants.LIST_TYPE.PATTADAR);

					if(surveyNoList != null && pattadarList!=null && khathaList != null)
						DatabaseUtil.getInstance().updateAppData(surveyNoList, pattadarList, khathaList);
					DatabaseUtil.getInstance().updateAppDataTime();
				}
			}
		};
		PahaniApplication.getThreadPoolExecutor().execute(runnable);
		return runnable;
	}

	private JSONArray getDataList(ApplicationConstants.LIST_TYPE type){
		String URL = null;
		String body = null;

		switch (type) {
			case SURVEY:
				URL = ApplicationConstants.SURVEY_LIST_URL;
				body = ApplicationConstants.LIST_REQUEST_BODY;
				break;
			case PATTADAR:
				URL = ApplicationConstants.PATTADAR_LIST_URL;
				body = ApplicationConstants.PATTADAR_LIST_REQUEST_BODY;
				break;
			case KHATA:
				URL = ApplicationConstants.KHATHA_LIST_URL;
				body = ApplicationConstants.LIST_REQUEST_BODY;
				break;

			default:
				break;
		}

		JSONArray dataList = null;
		try {
			com.shree.varikolepahani.network.RestCallResponse resp = doPost(URL, body, null);
			if (resp != null && resp.getStatus() == 200) {
				String data = resp.getResponse();
				JSONObject dataObj = new JSONObject(data);
				JSONArray list = dataObj.getJSONArray(JsonConstants.DATA);

				dataList = new JSONArray();

				for (int i=0; i<list.length(); i++) {
					JSONObject element = list.getJSONObject(i);
					String text = element.getString(JsonConstants.TEXT);
					String value = element.getString(JsonConstants.VALUE);
					JSONObject elementObj = new JSONObject();
					elementObj.put(JsonConstants.TEXT, text);
					elementObj.put(JsonConstants.VALUE, value);
					dataList.put(elementObj);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
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
	public com.shree.varikolepahani.network.RestCallResponse doPost(String reqUrl, String postBody, Map<String, String> headers) {
		return sendHttpRequest(HTTP_POST, reqUrl, postBody, headers);
	}

	/**
	 * Sending HTTP GET request
	 * @param reqUrl
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	private com.shree.varikolepahani.network.RestCallResponse doGet(String reqUrl, Map<String, String> headers) {
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
	private com.shree.varikolepahani.network.RestCallResponse doPut(String reqUrl, String postBody, Map<String, String> headers) {
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
	private com.shree.varikolepahani.network.RestCallResponse doDelete(String reqUrl, String postBody, Map<String, String> headers) {
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
	private com.shree.varikolepahani.network.RestCallResponse sendHttpRequest(String httpMethod, String reqUrl, String body, Map<String, String> headers)  {
		reqUrl = reqUrl.replaceAll(" ", "%20");
		com.shree.varikolepahani.network.RestCallResponse restCallResponse = null;
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

				restCallResponse = new com.shree.varikolepahani.network.RestCallResponse();
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