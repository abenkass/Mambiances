package com.pappl.mambiances;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SaisieMarqueur extends Activity {	
	/*
	 * the button for validate the data capture
	 */
	Button envoyer = null;
	/*
	 * written words by the user
	 */
	EditText mots = null ;
	/*
	 * List of the written words
	 */
	LinkedList<String> liste; 
	
	TextView result = null;
	TextView nomSeekBar = null;
	
	SeekBar seekBar = null;
	
	private int valSeekBar;
	
	private Boolean changed = false;
	private Boolean nouveauCurseur = false;
	
	private LocalDataSource datasource;
	
	private ArrayList<String> aCreer;
	private ArrayList<String> aSupprimer;
	
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
	
	private Curseur curseurAffiche;
	
	private ProgressDialog pDialog;
	  
	private JSONParser jsonParser = new JSONParser();
	
	private static String url_create_adresse = "http://mambiances.ser-info-02.ec-nantes.fr/create_adresse.php";

	private static String url_create_places = "http://mambiances.ser-info-02.ec-nantes.fr/create_places.php";
	
	private static String url_create_mots = "http://mambiances.ser-info-02.ec-nantes.fr/create_mots.php";

	private static String url_create_curseur = "http://mambiances.ser-info-02.ec-nantes.fr/create_curseur.php";

	private static String url_update_curseur = "http://mambiances.ser-info-02.ec-nantes.fr/update_curseur.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saisie_marqueur);
		
		datasource = Connexion.datasource;
		datasource.open();
		
		//Chargement des coordonnées et de l'utilisateur
		nom = getIntent().getExtras().getString("NOM");
		adr = getIntent().getExtras().getString("ADRESSE");
		latStr = getIntent().getExtras().getString("LATITUDE");
		lngStr = getIntent().getExtras().getString("LONGITUDE");
		utilisateur = getIntent().getExtras().getString("LOGIN");
		Utilisateur util = datasource.getUtilisateurWithLogin(utilisateur);
		utilisateurId = util.getUtilisateur_id();
		
		lat = Double.parseDouble(latStr);
		lng = Double.parseDouble(lngStr);
		
		mots = (EditText)findViewById(R.id.mots);
		envoyer = (Button)findViewById(R.id.bouton_envoyer);
		result = (TextView)findViewById(R.id.result);
		nomSeekBar = (TextView) findViewById(R.id.nomCurseurSaisi);
		seekBar = (SeekBar) findViewById(R.id.curseurSaisi);
		seekBar.setOnSeekBarChangeListener(curseurOnChangeListener);
		//on touche le bouton envoyer
		envoyer.setOnClickListener(envoyerListener);
		
		mots.addTextChangedListener(textWatcher);
		
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
    	
    	Places place = datasource.getPlaceWithLatLng(lat, lng);
		placesId = place.getPlaces_id();
		
		//Afficher mots déjà notés
		Mot[] mesMots = datasource.getMesMots(utilisateurId, placesId);
		for (Mot m : mesMots){
			mots.setText(mots.getText() + m.getMot() + ",");
		}
		setChips();
		
		//Choisir le curseur à afficher
		String[] aRemplir = datasource.getCurseursARemplir(utilisateurId, placesId);
		int n = aRemplir.length;
		if(n!=0){
			int p = (int)(Math.random() * n);
			nomSeekBar.setText(aRemplir[p]);
			nouveauCurseur = true;
		}else{
			String[] aRemplir1 = {"Cozy", "Palpitant", "Formel", "Accueillant", "Sécurisant", "Inspirant", "Intime", "Animé", "Luxueux", "Chill", "Personnel", "Romantique", "Ennuyeux", "Chaleureux", "Business", "Reposant"};
			int n1 = aRemplir1.length;
			int p = (int)(Math.random() * n1);
			nomSeekBar.setText(aRemplir[p]);
			curseurAffiche = datasource.getCurseurWithNom(utilisateurId, placesId, aRemplir[p]);
			valSeekBar = curseurAffiche.getCurseur_valeur();
			seekBar.setProgress(valSeekBar);
		}

		datasource.close();
		
	}


	
	//Methodes
	  
	  private OnSeekBarChangeListener curseurOnChangeListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {			
			valSeekBar = progress;
			changed = true;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-gemnerated method stub
			
		}
		  
	  };
		
	  private TextWatcher textWatcher = new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	if(count >=1){
				if(s.charAt(start) == ',')
					setChips(); // generate chips
	    	}
	    }
	    @Override	
	    public void beforeTextChanged(CharSequence s, int start, int count,
	      int after) {
	  
	    }
	    @Override
	    public void afterTextChanged(Editable s) {
	    	
	    }
	  };
	  
	//Method pour créer l'image du texte
	public void setChips() {
		if(mots.getText().toString().contains(",")){
			SpannableStringBuilder ssb = new SpannableStringBuilder(mots.getText());
			// split string wich comma
			String chips[] = mots.getText().toString().trim().split(",");
			int x =0;
			// loop will generate ImageSpan for every country name separated by comma
			for(String c : chips){
				// get textView 
				LayoutInflater lf = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				TextView textView = (TextView) lf.inflate(R.layout.chips_edittext, null);
				textView.setText(c); // set text
				
				// capture bitmapt of genreated textview
				int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				textView.measure(spec, spec);
				textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
				Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(),Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				canvas.translate(-textView.getScrollX(), -textView.getScrollY());
				textView.draw(canvas);
				textView.setDrawingCacheEnabled(true);
				Bitmap cacheBmp = textView.getDrawingCache();
				Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
				textView.destroyDrawingCache();  // destory drawable
				// create bitmap drawable for imagespan
				BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
				bmpDrawable.setBounds(0, 0,bmpDrawable.getIntrinsicWidth(),bmpDrawable.getIntrinsicHeight());
				// create and set imagespan 
				ssb.setSpan(new ImageSpan(bmpDrawable),x ,x + c.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				x = x+ c.length() +1;
			}
			// set chips span 
			mots.setText(ssb);
			
			mots.setSelection(mots.getText().length());
		}
	}
	
	// Cliquer sur le bouton envoyer
	private OnClickListener envoyerListener = new OnClickListener() {
	@Override
	public void onClick(View v){
		
		//Créer mots
		String m= mots.getText().toString();
		liste = new LinkedList();
		StringTokenizer st = new StringTokenizer(m,",");
		while (st.hasMoreTokens()) {
			liste.add(st.nextToken());
		}
		if (liste.size() == 0){
			Toast.makeText(SaisieMarqueur.this, "T'as pas rentré de mots galopin!", Toast.LENGTH_LONG).show();
		}else{
			datasource.open();
			Mot[] mesMots = datasource.getMesMots(utilisateurId, placesId);
			datasource.close();
			//entrer (ou réentrer) les nouveaux mots
			Boolean exist = false;
			for (int i=0; i < liste.size(); i++) {
				String mot = liste.get(i);
				for(int j = 0; j < mesMots.length; j++){
					if(mot == mesMots[j].getMot()){
						exist = true;
					}
				}
				if(!exist){
					aCreer.add(mot);
				}
			}
			exist = false;
			//supprimer les mots présents dans la base de données
			for (int i=0; i < mesMots.length; i++) {
				String mot = mesMots[i].getMot();
				for(int j = 0; j < liste.size(); j++){
					if(mot == liste.get(j)){
						exist = true;
					}
				}
				if(!exist){
					aSupprimer.add(mot);
					
				}
			}
			
			new CreateNewMots().execute();
			//TODO implémenter la suppression de mots
			//new SupprimerMots().execute();
		}
		
		//Créer curseur
		if(changed){
			if(nouveauCurseur){
				new CreateNewCurseur().execute();
			}else{
				new UpdateCurseur().execute();
			}
		}else{
			Toast.makeText(SaisieMarqueur.this, "T'as oublié de donner une valeur à ton curseur", Toast.LENGTH_LONG).show();
		}
		//test
		result.setText("Vous avez saisi le(s) mot(s) :");
		
		if (changed && liste.size() != 0){
			finish();
		}
		
		new Sync.LoadAllMotsCurseurs();
	}
	};
	
	class CreateNewAdresse extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SaisieMarqueur.this);
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
            pDialog = new ProgressDialog(SaisieMarqueur.this);
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
	
	class CreateNewMots extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SaisieMarqueur.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating mots
         * */
        
        //TODO
        protected String doInBackground(String... args) {
        	String places_id = String.valueOf(placesId);
        	String utilisateur_id = String.valueOf(utilisateurId);
        	
        	String JSonComplete = "";
        	int size = aCreer.size();
        	for(int i=0; i< size - 1; i++){
        		JSonComplete += "{\"places_id\" : " + places_id + ", " +
        						"\"utilisateur_id\" : " + utilisateur_id + ", " +
        						"\"mot\" : " + aCreer.get(i) + "},";
        	}
        	JSonComplete += "{\"places_id\" : " + places_id + ", " +
					"\"utilisateur_id\" : " + utilisateur_id + ", " +
					"\"mot\" : " + aCreer.get(size-1) + "}";
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mots", JSonComplete));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_mots,
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
	
//TODO implémenter la suppression de mots
//	class SupprimerMots extends AsyncTask<String, String, String> {
//		  
//        /**
//         * Before starting background thread Show Progress Dialog
//         **/
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(SaisieMarqueur.this);
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
// 
//        /**
//         * Creating mots
//         * */
//        
//       
//        protected String doInBackground(String... args) {
//        	String places_id = String.valueOf(placesId);
//        	String utilisateur_id = String.valueOf(utilisateurId);
//        	
//        	String JSonComplete = "";
//        	int size = aSupprimer.size();
//        	for(int i=0; i< size - 1; i++){
//        		JSonComplete += "{\"places_id\" : " + places_id + ", " +
//        						"\"utilisateur_id\" : " + utilisateur_id + ", " +
//        						"\"mot\" : " + aCreer.get(i) + "},";
//        	}
//        	JSonComplete += "{\"places_id\" : " + places_id + ", " +
//					"\"utilisateur_id\" : " + utilisateur_id + ", " +
//					"\"mot\" : " + aCreer.get(size-1) + "}";
//        	
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("mots", JSonComplete));
// 
//            // getting JSON Object
//            // Note that create product url accepts POST method
//            JSONObject json = jsonParser.makeHttpRequest(url_create_mots,
//                    "POST", params);
// 
//            // check log cat fro response
//            Log.d("Create Response", json.toString());
// 
//            return null;
//        }
// 
//        /**
//         * After completing background task Dismiss the progress dialog
//         * **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once done
//            pDialog.dismiss();
//        }
// 
//    }
	
	class CreateNewCurseur extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SaisieMarqueur.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating curseur
         * */
        
        protected String doInBackground(String... args) {
        	String places_id = String.valueOf(placesId);
        	String utilisateur_id = String.valueOf(utilisateurId);
        	String valCurseur = String.valueOf(valSeekBar);

        	String JSonComplete = "{\"places_id\" : " + places_id + ", " +
        						"\"utilisateur_id\" : " + utilisateur_id + ", " +
        						"\"curseur_nom\" : " + nomSeekBar.getText() + ", " +
        						"\"curseur_valeur\" : " + valCurseur + "}";
        	
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("curseur", JSonComplete));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_curseur,
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
	
	class UpdateCurseur extends AsyncTask<String, String, String> {
		  
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SaisieMarqueur.this);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating curseur
         * */
        
        protected String doInBackground(String... args) {
        	String valCurseur = String.valueOf(valSeekBar);
        	String curseur_nom = curseurAffiche.getCurseur_nom();

        	String JSonComplete = "{\"curseur_nom\" : " + curseur_nom + ", " +
        						"\"curseur_nom\" : " + valCurseur + "}";
        	
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("curseur", JSonComplete));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_curseur,
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
}
