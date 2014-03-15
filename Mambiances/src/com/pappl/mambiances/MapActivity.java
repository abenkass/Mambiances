package com.pappl.mambiances;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pappl.mambiances.db.LocalDataSource;
 
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
 
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class MapActivity extends Activity implements LocationListener, LoaderCallbacks<Cursor> {

	private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
	
	public 	static GoogleMap theMap;
	
	private LocationManager locMan;

	private Marker userMarker;
	
	private Marker[] placeMarkers;
	
	private final int MAX_PLACES = 20;
	
	private MarkerOptions[] places;
	
	public static LocalDataSource datasource;
	
	@Override
	public void onLocationChanged(Location location) {
		
		//Initialisation de la bdd locale
		datasource = new LocalDataSource(this);
		
	    Log.v("MyMapActivity", "location changed");
	    locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		
		String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location="+lat+","+lng+
			    "&radius=1000&sensor=true" +
			    //"&types=food%7Cbar%7Cstore%7Cmuseum%7Cart_gallery"+
			    "&key=AIzaSyDDRWm2cBS4tRli0Oo0DHnIaeqPsFYCgEY";
		
	    updatePlaces(placesSearchStr);
	}
	@Override
	public void onProviderDisabled(String provider){
	    Log.v("MyMapActivity", "provider disabled");
	}
	@Override
	public void onProviderEnabled(String provider) {
	    Log.v("MyMapActivity", "provider enabled");
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	    Log.v("MyMapActivity", "status changed");
	}
	
	private void handleIntent(Intent intent){
		if(intent.getAction().equals(Intent.ACTION_SEARCH)){
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		}else if(intent.getAction().equals(Intent.ACTION_VIEW)){
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {		
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void doSearch(String query){		
		Bundle data = new Bundle();
		data.putString("query", query);		
		getLoaderManager().restartLoader(0, data, this);
	}
	
	private void getPlace(String query){		
		Bundle data = new Bundle();
		data.putString("query", query);		
		getLoaderManager().restartLoader(1, data, this);
	}
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_search:	
				onSearchRequested();
				break;
		}	
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
		if(arg0==0)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
		else if(arg0==1)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
		return cLoader;
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {	
		showLocations(c);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub		
	}
	private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        theMap.clear();
        while(c.moveToNext()){
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            theMap.addMarker(markerOptions);
        }
        if(position!=null){
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            theMap.animateCamera(cameraPosition);
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
	
		userIcon = R.drawable.yellow_point;
		foodIcon = R.drawable.red_point;
		drinkIcon = R.drawable.blue_point;
		shopIcon = R.drawable.green_point;
		otherIcon = R.drawable.purple_point;
		
		handleIntent(getIntent());
		
		findViewById(R.id.locButton).setOnClickListener(locButton_OnClickListener);
		findViewById(R.id.addButton).setOnClickListener(addButton_OnClickListener);
	
		if (theMap==null){
			//map not instanciated yet
			
			theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.the_map)).getMap();
			
			if(theMap != null){
				//ok - proceed
				
				theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
				placeMarkers = new Marker[MAX_PLACES];
				
				locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				
				Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				double lat = lastLoc.getLatitude();
				double lng = lastLoc.getLongitude();
				
				String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
					    "json?location="+lat+","+lng+
					    "&radius=1000&sensor=true" +
					    //"&types=food%7Cbar%7Cstore%7Cmuseum%7Cart_gallery"+
					    "&key=AIzaSyDDRWm2cBS4tRli0Oo0DHnIaeqPsFYCgEY";
				
				updatePlaces(placesSearchStr);
				
				
			}
		}
		
	}
	
	private OnClickListener locButton_OnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			
			Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			double lat = lastLoc.getLatitude();
			double lng = lastLoc.getLongitude();
			
			LatLng lastLatLng = new LatLng(lat, lng);
			theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
		}
	};
	
	private OnClickListener addButton_OnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			Intent ListeLieux = new Intent(getApplicationContext(), ListeLieuxActivity.class);
			startActivity(ListeLieux);
		}
	};
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(theMap!=null){
	        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 100, this);
	    }
	}
	 
	@Override
	protected void onPause() {
	    super.onPause();
	    if(theMap!=null){
	        locMan.removeUpdates(this);
	    }
	}

	private void updatePlaces(String placesSearchStr){
		//update location
		locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		
		LatLng lastLatLng = new LatLng(lat, lng);
		
		if(userMarker != null)userMarker.remove();
		
		userMarker = theMap.addMarker( new MarkerOptions()
		.position(lastLatLng)
		.title("Vous êtes ici")
		.icon(BitmapDescriptorFactory.fromResource(userIcon))
		.snippet("Votre dernière localisation"));
		
		
		new GetPlaces().execute(placesSearchStr);
		
		locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 100, this);
		
	}
	
	private class GetPlaces extends AsyncTask<String, Void, String> {
		//fetch and parse place data
		@Override
		protected String doInBackground(String... placesURL) {
		    //fetch places
			StringBuilder placesBuilder = new StringBuilder();
			
			//process search parameter string(s)
			for (String placeSearchURL : placesURL) {
				//execute search
				
				HttpClient placesClient = new DefaultHttpClient();
				
				try {
				    //try to fetch the data
					HttpGet placesGet = new HttpGet(placeSearchURL);
					HttpResponse placesResponse = placesClient.execute(placesGet);
					StatusLine placeSearchStatus = placesResponse.getStatusLine();
					if (placeSearchStatus.getStatusCode() == 200) {
						//we have an OK response
						HttpEntity placesEntity = placesResponse.getEntity();
						InputStream placesContent = placesEntity.getContent();
						InputStreamReader placesInput = new InputStreamReader(placesContent);
						BufferedReader placesReader = new BufferedReader(placesInput);
						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
						    placesBuilder.append(lineIn);
						}
					}
				}
				catch(Exception e){
				    e.printStackTrace();
				}
			}
			return placesBuilder.toString();
		}
	
		protected void onPostExecute(String result) {
		    //parse place data returned from Google Places
			if(placeMarkers!=null){
			    for(int pm=0; pm<placeMarkers.length; pm++){
			        if(placeMarkers[pm]!=null)
			            placeMarkers[pm].remove();
			    }
			}
			try {
			    //parse JSON
				JSONObject resultObject = new JSONObject(result);
				JSONArray placesArray = resultObject.getJSONArray("results");
				places = new MarkerOptions[placesArray.length()];
				//loop through places
				for (int p=0; p<placesArray.length(); p++) {
				    //parse each place
					boolean missingValue=false;
					LatLng placeLL=null;
					String placeName="";
					String vicinity="";
					int currIcon = otherIcon;
					
					try{
					    //attempt to retrieve place data values
						missingValue=false;
						JSONObject placeObject = placesArray.getJSONObject(p);
						JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
						placeLL = new LatLng(
							    Double.valueOf(loc.getString("lat")),
							    Double.valueOf(loc.getString("lng")));
						JSONArray types = placeObject.getJSONArray("types");
						for(int t=0; t<types.length(); t++){
						    //what type is it
							String thisType=types.get(t).toString();
							if(thisType.contains("food")){
							    currIcon = foodIcon;
							    break;
							}
							else if(thisType.contains("bar")){
							    currIcon = drinkIcon;
							    break;
							}
							else if(thisType.contains("store")){
							    currIcon = shopIcon;
							    break;
							}
						}
						vicinity = placeObject.getString("vicinity");
						placeName = placeObject.getString("name");
						
					}
					catch(JSONException jse){
					    missingValue=true;
					    jse.printStackTrace();
					}
					if(missingValue)    places[p]=null;
					else
					    places[p]=new MarkerOptions()
					    .position(placeLL)
					    .title(placeName)
					    .icon(BitmapDescriptorFactory.fromResource(currIcon))
					    .snippet(vicinity);
				}
			}
			catch (Exception e) {
			    e.printStackTrace();
			}
			if(places!=null && placeMarkers!=null){
			    for(int p=0; p<places.length && p<placeMarkers.length; p++){
			        //will be null if a value was missing
			        if(places[p]!=null)
			            placeMarkers[p]=theMap.addMarker(places[p]);
			    }
			}
			
		}
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}
