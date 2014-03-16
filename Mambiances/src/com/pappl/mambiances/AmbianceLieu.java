package com.pappl.mambiances;

import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.Marqueur;
import com.pappl.mambiances.db.Mot;
import com.pappl.mambiances.db.Places;
import com.pappl.mambiances.db.Utilisateur;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
		
		int position = 0;
		Marqueur marqueur = datasource.getMonMarqueur(utilisateurId, placesId, position);
		long marqueurId = marqueur.getMarqueur_id();
		Mot mot1 = datasource.getMotWithMarqueurId(marqueurId);
		
		position = 1;
		marqueur = datasource.getMonMarqueur(utilisateurId, placesId, position);
		marqueurId = marqueur.getMarqueur_id();
		Mot mot2 = datasource.getMotWithMarqueurId(marqueurId);
		
		position = 2;
		marqueur = datasource.getMonMarqueur(utilisateurId, placesId, position);
		marqueurId = marqueur.getMarqueur_id();
		Mot mot3 = datasource.getMotWithMarqueurId(marqueurId);
	
		monMot1.setText(mot1.getMot());
		monMot2.setText(mot2.getMot());
		monMot3.setText(mot3.getMot());
		
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ambiance_lieu, menu);
		return true;
	}

}
