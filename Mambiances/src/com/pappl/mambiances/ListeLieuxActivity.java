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
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.pappl.mambiances.db.Adresse;
import com.pappl.mambiances.db.LocalDataSource;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TwoLineListItem;
import android.view.*;
import android.view.View.OnClickListener;

public class ListeLieuxActivity extends Activity {
	
	//début de getLocation
	private static String[][] lieuxAdresses = new String[20][3];
	
	private static double[][] lieuxAdressesD = new double[20][2];
	
	private LocationManager locMan;
	
	private ArrayList<Lieu> lieux = new ArrayList<Lieu>();
	
	private LocalDataSource datasource;
	
	private String utilisateur;
	
	private class GetPlaces extends AsyncTask<String, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(ListeLieuxActivity.this);
		
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
		protected void onPreExecute(){
			dialog.setMessage("Processing...");
			dialog.show();
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
			            lieuxAdressesD[pm][0]=0;
			            lieuxAdressesD[pm][1]=0;
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
					double lat = 0;
					double lng = 0;
					
					try{
					    //attempt to retrieve place data values
						missingValue=false;
						JSONObject placeObject = placesArray.getJSONObject(p);
						JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
						
						lat = Double.valueOf(loc.getString("lat"));
						lng = Double.valueOf(loc.getString("lng"));
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
						lieuxAdressesD[p][0]=0;
						lieuxAdressesD[p][1]=0;
					}
					else
					    lieuxAdresses[p][0] = placeName;
						lieuxAdresses[p][1] = vicinity;
						lieuxAdresses[p][2] = reference;
						lieuxAdressesD[p][0] = lat;
						lieuxAdressesD[p][1] = lng;
				}
			}
			catch (Exception e) {
			    e.printStackTrace();
			}
			
			datasource = Connexion.datasource;
			datasource.open();
			    
			final ListView listView = (ListView) findViewById(R.id.listeLieux);
		    
		    
		    for(int i = 0 ; i < lieuxAdresses.length ; i++) {
		    	Lieu lieu = new Lieu();
		    	String nom = lieuxAdresses[i][0];
		    	String adr = lieuxAdresses[i][1];
		    	String ref = lieuxAdresses[i][2];
		    	double lati = lieuxAdressesD[i][0];
		    	double lngi = lieuxAdressesD[i][1];
		    	
		    	lieu.setNom(nom);
		    	lieu.setAdresse(adr);
		    	lieu.setUtilisateur(utilisateur);
		    	lieu.setLatitude(lati);
		    	lieu.setLongitude(lngi);
		    	lieux.add(lieu);
		    	
		    	//TODO créer une Adresse, créer un Places et alimenter
		    	Boolean exist = datasource.existPlaceWithLatLng(lati, lngi);
		    	
		    	if (exist){
		    	}else{
		    		try {
		    			Adresse adresse = datasource.createAdresse(adr);
		    			long adrId = adresse.getAdresse_id();
		    			datasource.createPlace (nom, lati, lngi, adrId);
		    		}
		    		catch(Exception e){
					    e.printStackTrace();
					}
		    
		    	}
		    }
		    ListeLieuxAdapter<Lieu> lieuxAdapter = new ListeLieuxAdapter<Lieu>(getApplicationContext(), 
		      R.layout.simple_list_item_2_button, lieux);
		    
		    listView.setAdapter(lieuxAdapter);
		    
		    listView.setOnItemClickListener(new OnItemClickListener() {

		    	   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		  System.out.println("pushed");
		    	      double lat = lieuxAdressesD[position][0];
		    	      double lng = lieuxAdressesD[position][1];
		    	      String latStr = String.valueOf(lat);
		    	      String lngStr = String.valueOf(lng);
		    	      Intent saisieMarqueur = new Intent(getApplicationContext(), SaisieMarqueur.class);
		    	      saisieMarqueur.putExtra("LATITUDE", latStr);
		    	      saisieMarqueur.putExtra("LONGITUDE", lngStr);
		    	      saisieMarqueur.putExtra("LOGIN", utilisateur);
		    	      startActivity(saisieMarqueur);
		    	      
		    	   }
		    	 });
		    
		    datasource.close();
		    
		    dialog.dismiss();
		    
		}
		
	}
	//fin de getLocation

	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_liste_lieux);
	    
	    utilisateur = getIntent().getExtras().getString("LOGIN");
	    
	    locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		
		String placesNearby = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location="+lat+","+lng+
			    "&radius=100&sensor=true" +
			    "&key=AIzaSyDDRWm2cBS4tRli0Oo0DHnIaeqPsFYCgEY";
	    
	    new GetPlaces().execute(placesNearby);
	    
	    
	    
	}
	    


}
