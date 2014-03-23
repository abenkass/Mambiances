package com.pappl.mambiances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pappl.mambiances.SaisieMarqueur.CreateNewAdresse;
import com.pappl.mambiances.SaisieMarqueur.CreateNewPlaces;
import com.pappl.mambiances.db.Adresse;
import com.pappl.mambiances.db.Curseur;
import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.Marqueur;
import com.pappl.mambiances.db.Mot;
import com.pappl.mambiances.db.Places;
import com.pappl.mambiances.db.Utilisateur;
import com.pappl.mambiances.sync.Sync;
import com.pappl.utils.JSONParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class AmbianceLieu extends Activity {

	private LocalDataSource datasource;
	
	private TextView nomLieu;
	
	private TextView monMot1;
	private TextView monMot2;
	private TextView monMot3;
	
	private TextView motPop1;
	private TextView motPop2;
	private TextView motPop3;
	private TextView motAlea1;
	private TextView motAlea2;
	private TextView motAlea3;
	private TextView motAlea4;
	private TextView motAlea5;
	private TextView motAlea6;
	private TextView motAlea7;
	
	private ImageButton moyCozy;
	private ImageButton moyPalpitant;
	private ImageButton moyFormel;
	private ImageButton moyAccueillant;
	private ImageButton moySecurisant;
	private ImageButton moyInspirant;
	private ImageButton moyIntime;
	private ImageButton moyAnime;
	private ImageButton moyLuxueux;
	private ImageButton moyChill;
	private ImageButton moyPersonnel;
	private ImageButton moyRomantique;
	private ImageButton moyEnnuyeux;
	private ImageButton moyChaleureux;
	private ImageButton moyBusiness;
	private ImageButton moyReposant;
	
	
	private String nom;
	private String adr;
	private String latStr;
	private String lngStr;
	private String utilisateur;
	private String adrIdStr;
	private double lat;
	private double lng;
	
	private long utilisateurId;
	private long placesId;
	
	private SeekBar seekBar;
	private Curseur curseurAffiche;
	private TextView nomCurseurAffiche;
	private int valSeekBar;
	
	private static ProgressDialog pDialog;
	private static JSONParser jsonParser = new JSONParser();
	private static String url_create_adresse = "http://mambiances.ser-info-02.ec-nantes.fr/create_adresse.php";
	private static String url_create_places = "http://mambiances.ser-info-02.ec-nantes.fr/create_places.php";
	private static String url_ambiance_mots = "http://mambiances.ser-info-02.ec-nantes.fr/importMots.php";
	private static String url_ambiance_curseurs = "http://mambiances.ser-info-02.ec-nantes.fr/importCurseurs.php";
	private JSONArray mots = null;
	private JSONArray curseurs = null;
	
	private String[] motsAmb;
	private ArrayList<HashMap<String, Integer>> curseursAmb = new ArrayList<HashMap<String, Integer>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ambiance_lieu);
		
		datasource = Connexion.datasource;
		datasource.open();
		
		//Chargement de la référence et de l'utilisateur
		nom = getIntent().getExtras().getString("NOM");
		adr = getIntent().getExtras().getString("ADRESSE");
		latStr = getIntent().getExtras().getString("LATITUDE");
		lngStr = getIntent().getExtras().getString("LONGITUDE");
		utilisateur = getIntent().getExtras().getString("LOGIN");
		Utilisateur util = datasource.getUtilisateurWithLogin(utilisateur);
		utilisateurId = util.getUtilisateur_id();
		
		lat = Double.parseDouble(latStr);
		lng = Double.parseDouble(lngStr);
		
		//Load all places, marqueurs, mots et curseurs
				new Sync.LoadAllPlaces().execute();
				new Sync.LoadAllMotsCurseurs().execute();
				
				//TODO créer une Adresse, créer un Places et alimenter
		    	Boolean exist = datasource.existPlaceWithLatLng(lat, lng);
		    	
		    	if (exist){
		    	}else{
		    		try {
		    			new CreateNewAdresse().execute();
		    			new Sync.LoadAllAdresse().execute();
		    			Adresse adresse = datasource.getAdresseWithNom(adr);
		    			long adrId = adresse.getAdresse_id();
		    			adrIdStr = String.valueOf(adrId);
		    			new CreateNewPlaces().execute();
		    			new Sync.LoadAllPlaces().execute();
		    		}
		    		catch(Exception e){
					    e.printStackTrace();
					}
		    
		    	}
		
		//Chargement des éléments à afficher
		Places lieu = datasource.getPlaceWithLatLng(lat, lng);
		placesId = lieu.getPlaces_id();
		String nom = lieu.getPlaces_nom();
		
		/*Remplir les champs du layout avec les éléments de la bdd*/
		
		//Remplir les éléments du lieu
		nomLieu = (TextView) findViewById(R.id.nomLieu);
		nomLieu.setText(nom);
		
		/*Remplir "mes marqueurs"*/

		//Remplir "mes mots"
		monMot1 = (TextView) findViewById(R.id.monMot1);
		monMot2 = (TextView) findViewById(R.id.monMot2);
		monMot3 = (TextView) findViewById(R.id.monMot3);
		
		Mot[] mesMots = datasource.getMesMots(utilisateurId, placesId);
		int l = mesMots.length;
		switch (l){
		case 0 :	monMot1.setText("");
					monMot3.setText("");
					monMot2.setText("Pas de mot saisi");
					break;
		case 1 : 	monMot2.setText(mesMots[0].getMot());
					monMot1.setText("");
					monMot3.setText("");
					break;
		case 2 :	monMot1.setText(mesMots[0].getMot());
					monMot3.setText(mesMots[1].getMot());
					monMot2.setText("");
					break;
		default : 	monMot1.setText(mesMots[0].getMot());
					monMot2.setText(mesMots[1].getMot());
					monMot3.setText(mesMots[2].getMot());
		}
		
		//Remplir "Mon curseur"
		nomCurseurAffiche = (TextView) findViewById(R.id.nomCurseurAffiche);
		seekBar = (SeekBar) findViewById(R.id.curseurAffiche);
		
		Curseur[] mesCurseurs = datasource.getMesCurseurs(utilisateurId, placesId);
		int n = mesCurseurs.length;
		if(n!=0){
			int p = new Random().nextInt(n);
			curseurAffiche = mesCurseurs[p];
			nomCurseurAffiche.setText(curseurAffiche.getCurseur_nom());
			seekBar.setProgress(curseurAffiche.getCurseur_valeur());
		}else{
			nomCurseurAffiche.setText("Aucun marqueur saisi");
		}
		
		datasource.close();
		
		//Remplir "mots ambiance"
		motPop1 = (TextView) findViewById(R.id.motPop1);
		motPop2 = (TextView) findViewById(R.id.motPop2);
		motPop3 = (TextView) findViewById(R.id.motPop3);
		motAlea1 = (TextView) findViewById(R.id.motAlea1);
		motAlea2 = (TextView) findViewById(R.id.motAlea2);
		motAlea3 = (TextView) findViewById(R.id.motAlea3);
		motAlea4 = (TextView) findViewById(R.id.motAlea4);
		motAlea5 = (TextView) findViewById(R.id.motAlea5);
		motAlea6 = (TextView) findViewById(R.id.motAlea6);
		motAlea7 = (TextView) findViewById(R.id.motAlea7);
		
		//Remplir "curseurs ambiance"
		moyCozy = (ImageButton) findViewById(R.id.moyCozy);
		moyPalpitant = (ImageButton) findViewById(R.id.moyPalpitant);
		moyFormel = (ImageButton) findViewById(R.id.moyFormel);
		moyAccueillant = (ImageButton) findViewById(R.id.moyAccueillant);
		moySecurisant = (ImageButton) findViewById(R.id.moySecurisant);
		moyInspirant = (ImageButton) findViewById(R.id.moyInspirant);
		moyIntime = (ImageButton) findViewById(R.id.moyIntime);
		moyAnime = (ImageButton) findViewById(R.id.moyAnime);
		moyLuxueux = (ImageButton) findViewById(R.id.moyLuxueux);
		moyChill = (ImageButton) findViewById(R.id.moyChill);
		moyPersonnel = (ImageButton) findViewById(R.id.moyPersonnel);
		moyRomantique = (ImageButton) findViewById(R.id.moyRomantique);
		moyEnnuyeux = (ImageButton) findViewById(R.id.moyEnnuyeux);
		moyChaleureux = (ImageButton) findViewById(R.id.moyChaleureux);
		moyBusiness = (ImageButton) findViewById(R.id.moyBusiness);
		moyReposant = (ImageButton) findViewById(R.id.moyReposant);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ambiance_lieu, menu);
		return true;
	}
	
	class CreateNewAdresse extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AmbianceLieu.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating adresse
         * */
        protected String doInBackground(String... args) {
            String adresse_nom = adr;
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("adresse_nom", adresse_nom));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_adresse,
                    "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
	
	class CreateNewPlaces extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AmbianceLieu.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating places
         * */
        protected String doInBackground(String... args) {
            String places_nom = nom;
            String places_lat = latStr;
            String places_lng = lngStr;
            String adresse_id = adrIdStr;
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("places_nom", places_nom));
            params.add(new BasicNameValuePair("places_latitude", places_lat));
            params.add(new BasicNameValuePair("places_longitude", places_lng));
            params.add(new BasicNameValuePair("adresse_id", adresse_id));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_places,
                    "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }

	/**
     * Background Async Task to Load mots for the ambiance by making HTTP Request
     * */
    class LoadAmbianceMots extends AsyncTask<String, String, String> {
 
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
        	String places_id = String.valueOf(placesId);
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("places_id", places_id));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_ambiance_mots, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("Mots ambiance: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
 
                if (success == 1) {
                    mots = json.getJSONArray("Mots");
 
                    // looping through All Products
                    for (int i = 0; i < mots.length(); i++) {
                        JSONObject c = mots.getJSONObject(i);
 
                        // Storing each json item in variable
                        String mot = c.getString("mot");
                        motsAmb[i] = mot;
                    }
                    
                    motPop1.setText(motsAmb[0]);
                    motPop2.setText(motsAmb[1]);
                    motPop3.setText(motsAmb[2]);
                    motAlea1.setText(motsAmb[3]);
                    motAlea2.setText(motsAmb[4]);
                    motAlea3.setText(motsAmb[5]);
                    motAlea4.setText(motsAmb[6]);
                    motAlea5.setText(motsAmb[7]);
                    motAlea6.setText(motsAmb[8]);
                    motAlea7.setText(motsAmb[9]);
                    
                    
               } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
     * Background Async Task to Load mots for the ambiance by making HTTP Request
     * */
    class LoadAmbianceCurseurs extends AsyncTask<String, String, String> {
 
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
        	String places_id = String.valueOf(placesId);
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("places_id", places_id));
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_ambiance_curseurs, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("Curseurs ambiance: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
 
                if (success == 1) {
                    curseurs = json.getJSONArray("Curseurs");
                    HashMap<String, Integer> curseur = new HashMap<String, Integer>();
                    
                    // looping through All Products
                    for (int i = 0; i < curseurs.length(); i++) {
                        JSONObject c = curseurs.getJSONObject(i);
 
                        // Storing each json item in variable
                        String curseur_nom = c.getString("curseur_nom");
                        int curseur_valeur = c.getInt("curseur_valeur");
                        curseur.put(curseur_nom, curseur_valeur);                        
                    }
                    
                    //TODO mettre dans les bonnes valeurs
                    int alpha;
                    try{
                    alpha = (int) (curseur.get("Cozy")*2.55);
                    moyCozy.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Palpitant")*2.55);
                    moyPalpitant.getBackground().setAlpha(alpha);
                	}catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Formel")*2.55);
                    moyFormel.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Accueillant")*2.55);
                    moyAccueillant.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Securisant")*2.55);
                    moySecurisant.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Inspirant")*2.55);
                    moyInspirant.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Intime")*2.55);
                    moyIntime.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Anime")*2.55);
                    moyAnime.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Luxueux")*2.55);
                    moyLuxueux.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Chill")*2.55);
                    moyChill.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Personnel")*2.55);
                    moyPersonnel.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Romantique")*2.55);
                    moyRomantique.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Ennuyeux")*2.55);
                    moyEnnuyeux.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Chaleureux")*2.55);
                    moyChaleureux.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Business")*2.55);
                    moyBusiness.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    try{
                    alpha = (int) (curseur.get("Reposant")*2.55);
                    moyReposant.getBackground().setAlpha(alpha);
                    }catch(Exception e){}
                    
               } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
