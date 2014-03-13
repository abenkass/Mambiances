package com.pappl.mambiances.db;

import android.content.ContentValues;

public class Image extends DAO {

	private long image_id;
	
	private String image_url;
	
	private long marqueur_id;
	
	//Constructeurs
	
	public Image(){}
	
	public Image(long image_id, String image_url, long marqueur_id) {
		super();
		this.image_id = image_id;
		this.image_url = image_url;
		this.marqueur_id = marqueur_id;
	}

	//Getters and setters
	public long getImage_id() {
		return image_id;
	}

	public void setImage_id(long image_id) {
		this.image_id = image_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
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

		values.put(MySQLiteHelper.COLUMN_IMAGEURL, this.image_url);	
		values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);
		//pour l'instant on en prend pas en compte le cas de la synchronisation avec l'ext.}
		 
		//!!!if(this.registredInLocal){
			String str = "image_id "+"="+this.image_id;
			datasource.getDatabase().update(MySQLiteHelper.TABLE_IMAGE, values, str, null);
		
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
