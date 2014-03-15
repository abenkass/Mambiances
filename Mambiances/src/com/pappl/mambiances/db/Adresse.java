package com.pappl.mambiances.db;

import android.content.ContentValues;

public class Adresse extends DAO {
	
	private long adresse_id;
	
	private String adresse_nom;
	
	//Constructeurs
	
	public Adresse() {}
	
	public Adresse(long adresse_id, String adresse_nom) {
		super();
		this.adresse_id = adresse_id;
		this.adresse_nom = adresse_nom;
	}

	//Getters et setters
	
	public long getAdresse_id() {
		return adresse_id;
	}

	public void setAdresse_id(long adresse_id) {
		this.adresse_id = adresse_id;
	}

	public String getAdresse_nom() {
		return adresse_nom;
	}

	public void setAdresse_nom(String adresse_nom) {
		this.adresse_nom = adresse_nom;
	}

	@Override
	public void saveToLocal(LocalDataSource datasource) {
		ContentValues values = new ContentValues(); 

		values.put(MySQLiteHelper.COLUMN_ADRESSENOM, this.adresse_nom);
		//pour l'instant on en prend pas en compte le cas de la synchronisation avec l'ext.}
		 
		//!!!if(this.registredInLocal){
			String str = "adresse_id "+"="+this.adresse_id;
			datasource.getDatabase().update(MySQLiteHelper.TABLE_ADRESSE, values, str, null);
		
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
