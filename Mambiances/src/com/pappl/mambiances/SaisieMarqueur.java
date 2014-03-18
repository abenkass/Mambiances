package com.pappl.mambiances;

import java.util.LinkedList;
import java.util.StringTokenizer;

import com.pappl.mambiances.db.Curseur;
import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.Marqueur;
import com.pappl.mambiances.db.Mot;
import com.pappl.mambiances.db.Places;
import com.pappl.mambiances.db.Utilisateur;



import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
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
	
	private String latStr;
	private String lngStr;
	private String utilisateur;
	private double lat;
	private double lng;
	
	private long utilisateurId;
	private long placesId;
	
	private Curseur curseurAffiche;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saisie_marqueur);
		
		datasource = Connexion.datasource;
		datasource.open();
		
		//Chargement des coordonnées et de l'utilisateur
		latStr = getIntent().getExtras().getString("LATITUDE");
		lngStr = getIntent().getExtras().getString("LONGITUDE");
		utilisateur = getIntent().getExtras().getString("LOGIN");
		Utilisateur util = datasource.getUtilisateurWithLogin(utilisateur);
		utilisateurId = util.getUtilisateur_id();
		
		lat = Double.parseDouble(latStr);
		lng = Double.parseDouble(lngStr);
		Places place = datasource.getPlaceWithLatLng(lat, lng);
		placesId = place.getPlaces_id();
		
		mots = (EditText)findViewById(R.id.mots);
		envoyer = (Button)findViewById(R.id.bouton_envoyer);
		result = (TextView)findViewById(R.id.result);
		nomSeekBar = (TextView) findViewById(R.id.nomCurseurSaisi);
		seekBar = (SeekBar) findViewById(R.id.curseurSaisi);
		seekBar.setOnSeekBarChangeListener(curseurOnChangeListener);
		//on touche le bouton envoyer
		envoyer.setOnClickListener(envoyerListener);
		
		mots.addTextChangedListener(textWatcher);
		
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
		datasource.open();
		
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
			//supprimer les mots présents dans la base de données
			Mot[] mesMots = datasource.getMesMots(utilisateurId, placesId);
			for (Mot mot : mesMots){
				long marqueur_id = mot.getMarqueur_id();
				datasource.deleteMot(mot);
				Marqueur m1 = datasource.getMarqueurWithId(marqueur_id);
				datasource.deleteMarqueur(m1);
			}
			//entrer (ou réentrer) les nouveaux mots
			for (int i=0; i < liste.size(); i++) {
				result.setText(result.getText() + "'" + liste.get(i).toString() + "' ");
				Marqueur marqueur = datasource.createMarqueur(placesId, utilisateurId);
				long marqueurId = marqueur.getMarqueur_id();
				datasource.createMot(liste.get(i).toString(), marqueurId);
			}
		}
		
		//Créer curseur
		if(changed){
			if(nouveauCurseur){
				Marqueur marqueur = datasource.createMarqueur(placesId, utilisateurId);
				long marqueurId = marqueur.getMarqueur_id();
				String nomStr = (String) nomSeekBar.getText();
				datasource.createCurseur(valSeekBar, nomStr, marqueurId);
			}else{
				datasource.updateCurseur(curseurAffiche, valSeekBar);
			}
		}else{
			Toast.makeText(SaisieMarqueur.this, "T'as oublié de donner une valeur à ton curseur", Toast.LENGTH_LONG).show();
		}
		//test
		result.setText("Vous avez saisi le(s) mot(s) :");
				
		datasource.close();
		
		if (changed && liste.size() != 0){
			finish();
		}
	}
	};
}
