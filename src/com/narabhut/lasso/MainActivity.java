package com.narabhut.lasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MapActivity {

	MapController mapController;
	Location latestLocation, targetLocation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    
		// Get a reference to the MapView
		MapView myMapView = (MapView)findViewById(R.id.myMapView);
		// Get the Map View’s controller
		mapController = myMapView.getController();
		// Configure the map display options
		myMapView.setSatellite(true);
		myMapView.displayZoomControls(false);
		// Zoom in
		mapController.setZoom(19);

		// Add the MyPositionOverlay
		List<Overlay> overlays = myMapView.getOverlays();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, myMapView);
		overlays.add(myLocationOverlay);
		//myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		latestLocation = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(latestLocation);

		locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);

	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}
		public void onProviderDisabled(String provider){
			updateWithNewLocation(null);
		}
		public void onProviderEnabled(String provider){ 

		}
		public void onStatusChanged(String provider, int status,Bundle extras){

		}
	};


	private void updateWithNewLocation(Location location) {
		TextView latitudeText = (TextView)findViewById(R.id.latitude);
		TextView longitudeText = (TextView)findViewById(R.id.longitude);
		TextView addressText = (TextView)findViewById(R.id.address);
		String addressString = "Address = No address found";

		if (location != null) {

			// Update the map location.
			Double geoLat = location.getLatitude()*1E6;
			Double geoLng = location.getLongitude()*1E6;
			GeoPoint point = new GeoPoint(geoLat.intValue(),
					geoLng.intValue());
			mapController.animateTo(point);


			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			latitudeText.setText("Latitude = "+latitude);
			longitudeText.setText("Longitude = "+longitude);

			Geocoder gc = new Geocoder(this, Locale.getDefault());
			try {
				List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
						sb.append(address.getAddressLine(i)).append("\n");
					sb.append(address.getLocality()).append("\n");
					sb.append(address.getPostalCode()).append("\n");

					sb.append("Address = "+address.getCountryName());
				}
				addressString = sb.toString();
			} catch (IOException e) {}

		}
		addressText.setText(addressString);
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(menu);
		return true;
	}

	private void createMenu(Menu menu)
	{
		MenuItem mnu1 = menu.add(0, 0, 0, "Change Target");
		{
			mnu1.setIcon(R.drawable.location_pin_icon);
			mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		displayMenu();
		return true;
	}

	private void displayMenu() {
		final String [] items=new String []{"Use Current Location","Use Another Location","Cancel"};
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("Change target location");

		builder.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				if (which==0){
					targetLocation = latestLocation;
					Toast.makeText(getBaseContext(), "Target set to "+targetLocation.getLatitude()+","+targetLocation.getLongitude(), Toast.LENGTH_LONG).show();
				}
				else if (which==1){
					Intent intent = new Intent(MainActivity.this, TargetFinderActivity.class);
					double[] coordinates = new double[2];
					coordinates[0] = latestLocation.getLatitude();
					coordinates[1] = latestLocation.getLongitude();
					intent.putExtra("coordinates",coordinates);
					startActivity(intent);
				}
			}
		});

		builder.show();

	}
}
