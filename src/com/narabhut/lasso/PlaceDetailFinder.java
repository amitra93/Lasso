package com.narabhut.lasso;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class PlaceDetailFinder extends AsyncTask<String,Void,TargetPlace>{

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/details";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyBKiSGeyC2Dm3fe-YdBdOkVA3mqgqsbihw";
	

	private TargetPlace findCoordinates(String reference){
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&reference=" + reference + "&sensor=false");

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONObject resultObj = jsonObj.getJSONObject("result");
			JSONObject geometryObj = resultObj.getJSONObject("geometry");
			JSONObject locationObj = geometryObj.getJSONObject("location");
			
			//extract values from json object
			double lat = -999, lng = -999, rating = -999;
			String name = "", address = "", phone = "", imageURL = "";
			if (!locationObj.isNull("lat")){
				lat = locationObj.getDouble("lat");
			}
			if (!locationObj.isNull("lng")){
				lng = locationObj.getDouble("lng");
			}
			if (!resultObj.isNull("name")){
				name = resultObj.getString("name");
			}
			if (!resultObj.isNull("formatted_address")){
				address = resultObj.getString("formatted_address");
			}
			if (!resultObj.isNull("international_phone_number")){
				phone = resultObj.getString("international_phone_number");
			}
			if (!resultObj.isNull("rating")){
				rating = resultObj.getDouble("rating");
			}
			if (!resultObj.isNull("icon")){
				imageURL = resultObj.getString("icon");
			}
			return new TargetPlace(lat,lng,name,address,phone,rating,imageURL);

		} catch (JSONException e) {
			Log.e("JSON parsing failed",e.getMessage());
		}

		return null;
	}

	@Override
	protected TargetPlace doInBackground(String... ref) {
		return findCoordinates(ref[0]);
	}

	@Override
	protected void onPostExecute(TargetPlace coordinates) {

	}


}
