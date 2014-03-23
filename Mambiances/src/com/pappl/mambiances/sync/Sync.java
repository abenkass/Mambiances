package com.pappl.mambiances.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pappl.mambiances.Connexion;
import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.utils.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Sync {
	
	private static ProgressDialog pDialog;
	
	private static JSONParser jParser;
	
	public static HashMap<String, Integer> maxId = new HashMap<String, Integer>();

	//url to get maxId
    private static String url_max_id = "http://mambiances.ser-info-02.ec-nantes.fr/maxID.php";

    // url to get all users list
    private static String url_all_users = "http://mambiances.ser-info-02.ec-nantes.fr/get_all_users.php";
    // url to get all users list
    private static String url_all_adresse = "http://mambiances.ser-info-02.ec-nantes.fr/get_all_adresse.php";
    // url to get all users list
    private static String url_all_places = "http://mambiances.ser-info-02.ec-nantes.fr/get_all_places.php";
 // url to get all users list
    private static String url_all_mots_curseurs = "http://mambiances.ser-info-02.ec-nantes.fr/get_all_mots_curseurs.php";

    // users JSONArray
    private static JSONArray users = null;
 // users JSONArray
    private static JSONArray adresse = null;
 // users JSONArray
    private static JSONArray places = null;
    // users JSONArray
    private static JSONArray marqueur = null;
    // users JSONArray
    private static JSONArray mot = null;
    // users JSONArray
    private static JSONArray curseur = null;
    
    //JSON node names
    private static final String TAG_SUCCESS = "success";
    
    private static LocalDataSource datasource;
    
    /**
	 * Get the max id of each critical tables in external DB AND get current timestamp from database
	 * @return Hashmap of all max id
	 */
	public static HashMap<String, Integer> getMaxId() {
		if(maxId.isEmpty()) {
			try {
				maxId = new BackTastMaxId().execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return maxId;
	}
	
    /**
	 * The additional threat to get the Max id of each tables on server AND the current timestamp for synchronise purpose
	 * @author Sebastien
	 *
	 */
	public static class BackTastMaxId extends AsyncTask<Void, HashMap<String, Integer>, HashMap<String, Integer>> {
		
		private Context mContext;
		
		/**
		 * Constructor
		 * @param json
		 */
		public BackTastMaxId(){
			super();			
			this.mContext = Connexion.baseContext;
		}

		/**
		 * Ask the server and transform them to HashMap
		 */
		@SuppressWarnings("finally")
		protected HashMap<String, Integer> doInBackground(Void... params) { 
			
			String JSON = getData();
			try {
			    	JSONObject jObj = new JSONObject(JSON); 
			    	HashMap<String, Integer> maxID = new HashMap<String, Integer>();
			    	try {
				    	maxID.put("Adresse", jObj.getInt("adresse"));
				    	maxID.put("Curseur", jObj.getInt("curseur"));
				    	maxID.put("Marqueur", jObj.getInt("marqueur"));
				    	maxID.put("Mot", jObj.getInt("mot"));
				    	maxID.put("Places", jObj.getInt("places"));
				    	maxID.put("Utilisateur", jObj.getInt("utilisateur"));
				    	maxID.put("date", jObj.getInt("date"));
			    	} catch (Exception e) {
			    		maxID.put("Adresse", 0);
				    	maxID.put("Curseur", 0);
				    	maxID.put("Marqueur", 0);
				    	maxID.put("Mot", 0);
				    	maxID.put("Places", 0);
				    	maxID.put("Utilisateur", 0);
				    	maxID.put("date", jObj.getInt("date"));
			    	} finally {
			    		return maxID;
			    	}
			    	
			} catch (JSONException e) {
			           Log.e("JSON Parser", "Error parsing data " + e.toString());
			           return (HashMap<String, Integer>) null;
			}  
		}
		
		/**
		 * The request method to server
		 * @return the string of the server response
		 */
	    public String getData() {
		    HttpClient httpclient = new DefaultHttpClient();
		    // specify the URL you want to post to
		    HttpPost httppost = new HttpPost(url_max_id);
		    try {
			    // send the variable and value, in other words post, to the URL
			    HttpResponse response = httpclient.execute(httppost);
			    
			    StringBuilder sb = new StringBuilder();
			    try {
			    	BufferedReader reader = 
			    			new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
			    	String line = null;

			    	while ((line = reader.readLine()) != null) {
			    		sb.append(line);
			    	}
			    }
			    catch (IOException e) { e.printStackTrace(); }
			    catch (Exception e) { e.printStackTrace(); }
			    
			    return sb.toString();
				
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } ;
	        return "error";
	    }

		
		/**
		 * The things to execute after the backTask 
		 */
	    protected void onPostExecute(HashMap<String, Integer> result) {	
	    	
	    }
	}
	
	/**
     * Background Async Task to Load all users by making HTTP Request
     * */
    public static class LoadAllUsers extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Connexion.baseContext);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
        	datasource.open();
        	datasource.clearUtilisateur();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Users: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // utilisateurs found
                    // Getting Array of Utilisateur
                    users = json.getJSONArray("utilisateur");
 
                    // looping through All Products
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("utiliateur_id");
                        long id = Long.parseLong(idStr);
                        String login = c.getString("utilisateur_login");
                        String mdp = c.getString("utilisateur_mdp");
                        
                        datasource.createUtilisateur(id, login, mdp);
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datasource.close();
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }
    
    /**
     * Background Async Task to Load all adresses by making HTTP Request
     * */
    public static class LoadAllAdresse extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Connexion.baseContext);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
        	datasource.open();
        	datasource.clearAdresse();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_adresse, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Adresse: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
 
                if (success == 1) {
                    // adresse found
                    // Getting Array of adresse
                    adresse = json.getJSONArray("adresse");
 
                    // looping through All Products
                    for (int i = 0; i < adresse.length(); i++) {
                        JSONObject c = adresse.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("adresse_id");
                        long id = Long.parseLong(idStr);
                        String adresse_nom = c.getString("adresse_nom");
                        
                        datasource.createAdresse(id, adresse_nom);
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datasource.close();
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }
    
    /**
     * Background Async Task to Load all places by making HTTP Request
     * */
    public static class LoadAllPlaces extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Connexion.baseContext);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
        	datasource.open();
        	datasource.clearPlaces();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_places, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Places: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
 
                if (success == 1) {
                    // adresse found
                    // Getting Array of adresse
                    places = json.getJSONArray("places");
 
                    // looping through All Products
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject c = places.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("places_id");
                        long id = Long.parseLong(idStr);
                        String places_nom = c.getString("places_nom");
                        String places_lat_str = c.getString("places_latitude");
                        double places_lat = Double.parseDouble(places_lat_str);
                        String places_lng_str = c.getString("places_longitude");
                        double places_lng = Double.parseDouble(places_lng_str);
                        String adrIdStr = c.getString("adresse_id");
                        long adresse_id = Long.parseLong(adrIdStr);
                        
                        datasource.createPlace(id, places_nom, places_lat, places_lng, adresse_id);
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datasource.close();
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }
    
    /**
     * Background Async Task to Load all mots and curseurs with marqueur linked to this user_id by making HTTP Request
     * */
    public static class LoadAllMotsCurseurs extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Connexion.baseContext);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All marqueur from url
         * */
        protected String doInBackground(String... args) {
        	datasource.open();
        	datasource.clearMarqueur();
        	datasource.clearMots();
        	datasource.clearCurseur();
            // Building Parameters
    		List<NameValuePair> params = new ArrayList<NameValuePair>();
        	for (String utilisateur_id : args){
                params.add(new BasicNameValuePair("utilisateur_id", utilisateur_id));
        	}
            //TODO move it and add user id 
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_mots_curseurs, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Mots and Curseur: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
 
                if (success == 1) {
                    marqueur = json.getJSONArray("marqueur");
 
                    // looping through All Products
                    for (int i = 0; i < marqueur.length(); i++) {
                        JSONObject c = marqueur.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("marqueur_id");
                        long id = Long.parseLong(idStr);
                        String placesIdStr = c.getString("places_id");
                        long placesId = Long.parseLong(placesIdStr);
                        String utilisateurIdStr = c.getString("utilisateur_id");
                        long utilisateurId = Long.parseLong(utilisateurIdStr);
                        
                        datasource.createMarqueur(id, placesId, utilisateurId);
                    }
                    
                    mot = json.getJSONArray("mot");
                    
                    // looping through All Products
                    for (int i = 0; i < mot.length(); i++) {
                        JSONObject c = mot.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("mot_id");
                        long id = Long.parseLong(idStr);
                        String mot = c.getString("mot");
                        String marqueurIdStr = c.getString("marqueur_id");
                        long marqueurId = Long.parseLong(marqueurIdStr);
                        
                        datasource.createMot(id, mot, marqueurId);
                    }
                    
                    curseur = json.getJSONArray("curseur");
                    
                    // looping through All Products
                    for (int i = 0; i < curseur.length(); i++) {
                        JSONObject c = curseur.getJSONObject(i);
 
                        // Storing each json item in variable
                        String idStr = c.getString("curseur_id");
                        long id = Long.parseLong(idStr);
                        String nom = c.getString("curseur_nom");
                        String curseurValStr = c.getString("curseur_valeur");
                        int valeur = Integer.parseInt(curseurValStr);
                        String marqueurIdStr = c.getString("marqueur_id");
                        long marqueurId = Long.parseLong(marqueurIdStr);
                        
                        datasource.createCurseur(id, valeur, nom , marqueurId);
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datasource.close();
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }
    
    
}
