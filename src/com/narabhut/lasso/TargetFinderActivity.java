package com.narabhut.lasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class TargetFinderActivity extends Activity implements OnItemClickListener{

	private TargetFinderTextView searchBox;
	private PlacesAutoCompleteAdapter searchSuggestions;
	private TargetPlace target;
	private Spinner searchRadius;
	private ArrayList<String> arrayRadius;
	private double[] coordinates;
	private String defaultRadiusString = "1 mile";
	private int defaultRadius = 1609;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google);

		Bundle extras = getIntent().getExtras();
		coordinates = extras.getDoubleArray("coordinates");
		
		arrayRadius = new ArrayList<String>();
		arrayRadius.add("1 mile");
		arrayRadius.add("2 miles");
		arrayRadius.add("5 miles");
		arrayRadius.add("10 miles");
		arrayRadius.add("25 miles");
		searchRadius = (Spinner)findViewById(R.id.searchRadiusSpinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayRadius);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		searchRadius.setAdapter(adapter);
		
		searchSuggestions = new PlacesAutoCompleteAdapter(this,R.layout.item_list,coordinates,defaultRadius);
		searchBox = (TargetFinderTextView) findViewById(R.id.searchBox);
		searchSuggestions.setNotifyOnChange(true);
		searchBox.setOnItemClickListener(this);
		searchBox.setAdapter(searchSuggestions);


		//enable back button like in Gmail app
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		//finish activity when back button is called
		finish();
		return super.onOptionsItemSelected(item);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(menu);
		return true;
	}

	private void createMenu(Menu menu)
	{
		MenuItem mnu1 = menu.add(0, 0, 0, "Accept target");
		{
			mnu1.setIcon(R.drawable.check);
			mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long ID) {
		String str = (String) adapterView.getItemAtPosition(position);
		TextView name = (TextView)findViewById(R.id.name);
		TextView formattedAddress = (TextView)findViewById(R.id.formattedAddress);
		TextView formattedPhoneNumber = (TextView)findViewById(R.id.formattedPhoneNumber);
		TextView averageRating = (TextView)findViewById(R.id.averageRating);
		ImageView image = (ImageView)findViewById(R.id.iconView);
		
		String reference = searchSuggestions.getReference(str);
		if (reference==null){
			name.setText("None found");
			formattedAddress.setText("None found");
			formattedPhoneNumber.setText("None found");
			averageRating.setText("None found");
		}
		else {
			PlaceDetailFinder pdf = new PlaceDetailFinder();
			try {
				target = pdf.execute(reference).get();
				
				String nameRes = target.getName();
				String addressRes = target.getFormattedAddress();
				String phoneNumberRes = target.getFormattedPhoneNumber();
				double averageRatingRes = target.getAverageRating();
				String imageURL = target.getImageURL();
				
				if (nameRes.equals("")){
					name.setVisibility(View.GONE);
				}
				else {
					name.setVisibility(View.VISIBLE);
					name.setText(nameRes);
				}
				
				if (addressRes.equals("")){
					formattedAddress.setVisibility(View.GONE);
				}
				else {
					formattedAddress.setVisibility(View.VISIBLE);
					formattedAddress.setText(addressRes);
				}
				
				if (phoneNumberRes.equals("")){
					formattedPhoneNumber.setVisibility(View.GONE);
				}
				else {
					formattedPhoneNumber.setVisibility(View.VISIBLE);
					formattedPhoneNumber.setText(phoneNumberRes);
				}
				
				//if (averageRatingRes==-999){
				//	averageRating.setVisibility(View.GONE);
				//}
				//else {
				//	averageRating.setVisibility(View.VISIBLE);
				//	averageRating.setText(""+averageRatingRes);
				//}
				
				if (imageURL.equals("")){
					image.setVisibility(View.GONE);
				}
				else {
					image.setVisibility(View.VISIBLE);
					new DownloadImageTask(image).execute(imageURL);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
		}

	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}


}
