package com.pappl.mambiances;

import com.pappl.mambiances.db.LocalDataSource;
import com.pappl.mambiances.db.Places;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class AmbianceLieu extends Activity {

	private String reference;

	private LocalDataSource datasource;
	
	private TextView nomLieu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ambiance_lieu);
		
		datasource = MapActivity.datasource;
		datasource.open();
		
		//Chargement de la référence
		reference = getIntent().getExtras().getString("REFERENCE_LIEU");
		
		//Chargement des éléments à afficher
		Places lieu = datasource.getPlaceWithId(reference);
		String nom = lieu.getPlaces_nom();
		
		//Remplir les champs du layout avec les éléments de la bdd
		
		nomLieu = (TextView) findViewById(R.id.nomLieu);
		
		nomLieu.setText(nom);
	
		
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ambiance_lieu, menu);
		return true;
	}

}
