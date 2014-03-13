package com.pappl.mambiances.db;

import android.content.ContentValues;

public class Curseur extends DAO{

	private long curseur_id;
	
	private double curseur_valeur;
	
	private String curseur_nom;
	
	private long marqueur_id;
	
	//Constructeurs
	
	public Curseur(){}
	
	public Curseur(long curseur_id, double curseur_valeur, String curseur_nom, long marqueur_id) {
		super();
		this.curseur_id = curseur_id;
		this.curseur_valeur = curseur_valeur;
		this.curseur_nom = curseur_nom;
		this.marqueur_id = marqueur_id;
	}

	// Getters and Setters
	
	public long getCurseur_id() {
		return curseur_id;
	}

	public void setCurseur_id(long curseur_id) {
		this.curseur_id = curseur_id;
	}

	public double getCurseur_valeur() {
		return curseur_valeur;
	}

	public void setCurseur_valeur(double curseur_valeur) {
		this.curseur_valeur = curseur_valeur;
	}

	public String getCurseur_nom() {
		return curseur_nom;
	}

	public void setCurseur_nom(String curseur_nom) {
		this.curseur_nom = curseur_nom;
	}
	
	public long getMarqueur_id() {
		return marqueur_id;
	}

	public void setMarqueur_id(long marqueur_id) {
		this.marqueur_id = marqueur_id;
	}

	
	@Override
	public void saveToLocal(LocalDataSource datasource) {
		ContentValues values = new ContentValues(); 

		values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, this.curseur_valeur);
		values.put(MySQLiteHelper.COLUMN_CURSEURNOM, this.curseur_nom);
		values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);
		//pour l'instant on en prend pas en compte le cas de la synchronisation avec l'ext.}
		 
		//!!!if(this.registredInLocal){
			String str = "curseur_id "+"="+this.curseur_id;
			datasource.getDatabase().update(MySQLiteHelper.TABLE_CURSEUR, values, str, null);
		
		/*!!!else{
			Cursor cursor = datasource.getDatabase().rawQuery(GETMAXUTILISATEURID, null);
			cursor.moveToFirst();
			if(!cursor.isAfterLast()){
				long old_id = this.getUtilisateur_id();
				long new_id = 1+this.utilisateur_id+Sync.getMaxId().get("Utilisateur");
				this.setUtilisateur_id(new_id);
				this.trigger(old_id, new_id, MainActivity.composed);
			}
			values.put(MySQLiteHelper.COLUMN_UTILISATEURID, this.utilisateur_id);
			datasource.getDatabase().insert(MySQLiteHelper.TABLE_UTILISATEUR, null, values);
			this.setRegistredInLocal(true);
		}*/
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}
