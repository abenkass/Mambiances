package com.pappl.mambiances;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.*;
import android.view.View.OnClickListener;

public class ListeLieux extends Activity {
	
	//début de getLocation
	private static String[][] lieuxAdresses = new String[20][3];
	
	private LocationManager locMan;
	
	private List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
	
	
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
		@Override
		protected void onPostExecute(String result) {
		    //parse place data returned from Google Places
			if(lieuxAdresses!=null){
			    for(int pm=0; pm<lieuxAdresses.length; pm++){
			        if(lieuxAdresses[pm][0]!=null || lieuxAdresses[pm][1]!=null){
			            lieuxAdresses[pm][0]=null;
			            lieuxAdresses[pm][1]=null;
			            lieuxAdresses[pm][2]=null;
			        }
			    }
			}
			try {
			    //parse JSON
				JSONObject resultObject = new JSONObject(result);
				JSONArray placesArray = resultObject.getJSONArray("results");
				//loop through places
				for (int p=0; p<placesArray.length(); p++) {
				    //parse each place
					boolean missingValue=false;
					String placeName="";
					String vicinity="";
					String reference="";
					
					try{
					    //attempt to retrieve place data values
						missingValue=false;
						JSONObject placeObject = placesArray.getJSONObject(p);
						vicinity = placeObject.getString("vicinity");
						placeName = placeObject.getString("name");
						reference = placeObject.getString("reference");
					}
					catch(JSONException jse){
					    missingValue=true;
					    jse.printStackTrace();
					}
					if(missingValue) {
						lieuxAdresses[p][0]=null;
						lieuxAdresses[p][1]=null;
						lieuxAdresses[p][2]=null;
					}
					else
					    lieuxAdresses[p][0] = placeName;
						lieuxAdresses[p][1] = vicinity;
						lieuxAdresses[p][2] = reference;
				}
			}
			catch (Exception e) {
			    e.printStackTrace();
			}
		
		}
		
	}
	//fin de getLocation

	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_liste_lieux);
	    
	    locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		
		String placesNearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location="+lat+","+lng+
			    "&radius=100&sensor=true" +
			    "&key=AIzaSyDDRWm2cBS4tRli0Oo0DHnIaeqPsFYCgEY";
	    
	    
	    
	    new GetPlaces().execute(placesNearby);
	    
	    final ListView listView = (ListView) findViewById(R.id.listeLieux);
	    listView.setClickable(true);
	    
	    HashMap<String, String> element1;
	    element1 = new HashMap<String, String>();
		element1.put("Nom", "Maison");
		element1.put("Adresse", "Chez moi");
	    liste.add(element1);
	    
	    HashMap<String, String> element;
	    for(int i = 0 ; i < lieuxAdresses.length ; i++) {
	      element = new HashMap<String, String>();
	   
	      element.put("Nom", lieuxAdresses[i][0]);
	      
	      element.put("Adresse", lieuxAdresses[i][1]);
	      liste.add(element);
	    }
	    
	    ListAdapter adapter = new SimpleAdapter(this, 
	      liste, 
	      R.layout.simple_list_item_2_button,
	      new String[] {"Nom", "Adresse"}, 
	      new int[] {android.R.id.text1, android.R.id.text2 });
	    listView.setAdapter(adapter);
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {

	    	   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	      String ref = lieuxAdresses[position][2];
	    	      Intent AmbianceLieu = new Intent(getApplicationContext(), AmbianceLieu.class);
	    	      startActivity(AmbianceLieu);
	    	      
	    	   }
	    	 });
	    
	    
	    ((SimpleAdapter) adapter).setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation){
                if (view.getId() == R.id.boutonDetailsLieu) {
                    Button b=(Button) view;
                    b.setOnClickListener(ambianceLieuOnClickListener);
                    return true; 
               }
                return false;
            } 
	    });
	}
	    
		private OnClickListener ambianceLieuOnClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent AmbianceLieu = new Intent(getApplicationContext(), AmbianceLieu.class);
				startActivity(AmbianceLieu);
			}
		};

}
