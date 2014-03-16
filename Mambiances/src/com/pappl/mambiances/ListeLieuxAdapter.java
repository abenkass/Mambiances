package com.pappl.mambiances;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListeLieuxAdapter<T> extends ArrayAdapter<T> {
	
	private final Context context;
	private int layoutResourceId;
	private final List<T> lieux;
	
	private String utilisateur;

	public ListeLieuxAdapter(Context context, int layoutResourceId, ArrayList<T> lieux) { 
	super(context, layoutResourceId, lieux); 
	this.context = context;
	this.layoutResourceId = layoutResourceId;
	this.lieux = lieux;
	}   

	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {
	
	// We need the layoutinflater to pick up the view from xml
	
	if(convertView==null){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutResourceId, parent, false);
    }
	
	final Lieu lieu = ((Lieu) lieux.get(position));
	
	TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
	TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);	
	
	text1.setText(lieu.getNom());
	text2.setText(lieu.getAdresse());
	
	View boutonDetailsLieu = convertView.findViewById(R.id.boutonDetailsLieu);
	
	boutonDetailsLieu.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			double lat = lieu.getLatitude();
			double lng = lieu.getLongitude();
			String latStr = String.valueOf(lat);
			String lngStr = String.valueOf(lng);
			String utilisateur = lieu.getUtilisateur();
			
			Intent ambianceLieu = new Intent(context, AmbianceLieu.class);
  	      	ambianceLieu.putExtra("LATITUDE", latStr);
  	        ambianceLieu.putExtra("LONGITUDE", lngStr);
  	      	ambianceLieu.putExtra("LOGIN", utilisateur);
  	      	context.startActivity(ambianceLieu);
		}
	});
	
	return convertView; } 

}
