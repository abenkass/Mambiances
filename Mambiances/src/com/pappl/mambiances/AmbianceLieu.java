package com.pappl.mambiances;

import java.util.Random;

import com.pappl.mambiances.db.Curseur;
import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.Marqueur;
import com.pappl.mambiances.db.Mot;
import com.pappl.mambiances.db.Places;
import com.pappl.mambiances.db.Utilisateur;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.TextView;

public class AmbianceLieu extends Activity {

	private LocalDataSource datasource;
	
	private TextView nomLieu;
	
	private TextView monMot1;
	private TextView monMot2;
	private TextView monMot3;
	
	private String utilisateur;
	
	private String latStr;
	private String lngStr;
	private double lat;
	private double lng;
	
	private SeekBar seekBar;
	private Curseur curseurAffiche;
	private TextView nomCurseurAffiche;
	private int valSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ambiance_lieu);
		
		datasource = Connexion.datasource;
		datasource.open();
		
		//Chargement de la référence et de l'utilisateur
		latStr = getIntent().getExtras().getString("LATITUDE");
		lngStr = getIntent().getExtras().getString("LONGITUDE");
		utilisateur = getIntent().getExtras().getString("LOGIN");
		
		lat = Double.parseDouble(latStr);
		lng = Double.parseDouble(lngStr);
		
		//Chargement des éléments à afficher
		Places lieu = datasource.getPlaceWithLatLng(lat, lng);
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
		
		Utilisateur util = datasource.getUtilisateurWithLogin(utilisateur);
		long utilisateurId = util.getUtilisateur_id();
		Places place = datasource.getPlaceWithLatLng(lat, lng);
		long placesId = place.getPlaces_id();
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ambiance_lieu, menu);
		return true;
	}

}
