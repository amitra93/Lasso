package com.narabhut.lasso;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	private PairOfResultsAndReferences pair;
	private ArrayList<String> resultList;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyBKiSGeyC2Dm3fe-YdBdOkVA3mqgqsbihw";
	private double[] coordinates;
	private int searchRadius;
	
	private PairOfResultsAndReferences autocomplete(String input) {
		PairOfResultsAndReferences internalPair = new PairOfResultsAndReferences();
		
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + API_KEY);
			sb.append("&location="+coordinates[0] + "," + coordinates[1]);
			sb.append("&radius="+searchRadius);
			sb.append("&components=country:us");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

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
			return internalPair;
		} catch (IOException e) {
			return internalPair;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			internalPair.createNewLists(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				String res = predsJsonArray.getJSONObject(i).getString("description");
				String ref = predsJsonArray.getJSONObject(i).getString("reference");
				internalPair.setLists(res,ref);
			}
		} catch (JSONException e) {
		}

		return internalPair;
	}

	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, double[] coordinates, int searchRadius) {
		super(context, textViewResourceId);
		this.coordinates = coordinates;
		this.searchRadius = searchRadius;
		
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					pair = autocomplete(constraint.toString());
					resultList = pair.getResultList();
					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}};
			return filter;
	}
	
	public String getReference(String str){
		return pair.getReference(str);
	}

}