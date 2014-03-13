package com.pappl.mambiances.db;

import android.content.ContentValues;

public class Mot extends DAO{
	
	private long mot_id;
	
	private String mot;
	
	private long marqueur_id;

	// Constructeurs
	
	public Mot(){}
	
	public Mot(long mot_id, String mot, long marqueur_id) {
		super();
		this.mot_id = mot_id;
		this.mot = mot;
		this.marqueur_id = marqueur_id;
	}
	
	// Getters and Setters
	
	public long getMot_id() {
		return mot_id;
	}

	public void setMot_id(long mot_id) {
		this.mot_id = mot_id;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}
	
	public long getMarqueur_id() {
		return marqueur_id;
	}

	public void setMarqueur_id(long marqueur_id) {
		this.marqueur_id = marqueur_id;
	}
	
	//Save to local
	
	@Override
	public void saveToLocal(LocalDataSource datasource) {
		ContentValues values = new ContentValues(); 

		values.put(MySQLiteHelper.COLUMN_MOT, this.mot);
		values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);
		//pour l'instant on en prend pas en compte le cas de la synchronisation avec l'ext.}
		 
		//!!!if(this.registredInLocal){
			String str = "mot_id "+"="+this.mot_id;
			datasource.getDatabase().update(MySQLiteHelper.TABLE_MOT, values, str, null);
		
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
